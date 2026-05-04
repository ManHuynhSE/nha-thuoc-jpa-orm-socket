package com.pillchill.migration.service.impl;

import java.util.List;
import java.util.Optional;

import com.pillchill.migration.dto.ChiTietHoaDonView;
import com.pillchill.migration.entity.ChiTietHoaDon;
import com.pillchill.migration.entity.ChiTietPhieuDat;
import com.pillchill.migration.entity.ChiTietLoThuoc;
import com.pillchill.migration.entity.HoaDon;
import com.pillchill.migration.entity.KhachHang;
import com.pillchill.migration.entity.KhuyenMai;
import com.pillchill.migration.entity.LoThuoc;
import com.pillchill.migration.entity.NhanVien;
import com.pillchill.migration.entity.PhieuDat;
import com.pillchill.migration.entity.Thuoc;
import com.pillchill.migration.entity.id.ChiTietHoaDonId;
import com.pillchill.migration.entity.id.ChiTietLoThuocId;
import com.pillchill.migration.dto.CreateHoaDonCommand;
import com.pillchill.migration.dto.HoaDonView;
import com.pillchill.migration.dto.HoaDonItemCommand;
import com.pillchill.migration.db.JPAUtil;
import com.pillchill.migration.repository.IChiTietHoaDonRepository;
import com.pillchill.migration.repository.IHoaDonRepository;
import com.pillchill.migration.repository.impl.ChiTietHoaDonRepository;
import com.pillchill.migration.repository.impl.HoaDonRepository;
import com.pillchill.migration.service.IHoaDonService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.LinkedHashMap;
import java.util.Map;

public class HoaDonService implements IHoaDonService {
    private final IHoaDonRepository hoaDonRepository;
    private final IChiTietHoaDonRepository chiTietHoaDonRepository;

    public HoaDonService(IHoaDonRepository hoaDonRepository, IChiTietHoaDonRepository chiTietHoaDonRepository) {
        this.hoaDonRepository = hoaDonRepository;
        this.chiTietHoaDonRepository = chiTietHoaDonRepository;
    }

    public HoaDonService(IHoaDonRepository hoaDonRepository) {
        this(hoaDonRepository, new ChiTietHoaDonRepository());
    }

    public HoaDonService() {
        this(new HoaDonRepository(), new ChiTietHoaDonRepository());
    }

    @Override
    public HoaDon createHoaDon(CreateHoaDonCommand command) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            String maPhieuDat = normalizeUpper(command.maPhieuDat());
            PhieuDat phieuDatNguon = null;
            if (maPhieuDat != null) {
                phieuDatNguon = em.find(PhieuDat.class, maPhieuDat);
                if (phieuDatNguon == null || !phieuDatNguon.isActive()) {
                    throw new IllegalArgumentException("Không tìm thấy phiếu đặt hợp lệ: " + maPhieuDat);
                }
                if (phieuDatNguon.isReceived()) {
                    throw new IllegalStateException("Phiếu đặt " + maPhieuDat + " đã được xuất hóa đơn");
                }
            }

            Map<String, HoaDonItemCommand> normalizedItems = normalizeAndMergeItems(command.items());
            validateAgainstPhieuDat(em, maPhieuDat, normalizedItems);

            NhanVien nhanVien = em.getReference(NhanVien.class, command.maNV());
            String maKhachHang = resolveMaKhachHang(command.maKH(), phieuDatNguon);
            KhachHang khachHang = maKhachHang == null || maKhachHang.isBlank()
                    ? null
                    : em.getReference(KhachHang.class, maKhachHang);
            KhuyenMai khuyenMai = command.maKM() == null || command.maKM().isBlank()
                    ? null
                    : em.getReference(KhuyenMai.class, command.maKM());

            HoaDon hoaDon = HoaDon.builder()
                    .maHoaDon(command.maHoaDon())
                    .ngayBan(command.ngayBan())
                    .ghiChu(command.ghiChu())
                    .nhanVien(nhanVien)
                    .khachHang(khachHang)
                    .khuyenMai(khuyenMai)
                    .giaTriThue(command.giaTriThue())
                    .tenLoaiThue(command.tenLoaiThue())
                    .isActive(true)
                    .build();
            em.persist(hoaDon);

            for (HoaDonItemCommand item : normalizedItems.values()) {
                Thuoc thuoc = em.getReference(Thuoc.class, item.maThuoc());
                List<FifoAllocation> allocations = item.maLo() != null && !item.maLo().isBlank()
                        ? truTonKhoTheoLoDuocChon(em, item.maThuoc(), item.maLo(), item.soLuong())
                        : truTonKhoTheoFIFO(em, item.maThuoc(), item.soLuong());
                for (FifoAllocation allocation : allocations) {
                    LoThuoc loThuoc = em.getReference(LoThuoc.class, allocation.maLo());
                    ChiTietHoaDon chiTietHoaDon = ChiTietHoaDon.builder()
                            .id(new ChiTietHoaDonId(command.maHoaDon(), item.maThuoc(), allocation.maLo()))
                            .hoaDon(hoaDon)
                            .thuoc(thuoc)
                            .loThuoc(loThuoc)
                            .soLuong(allocation.soLuong())
                            .donGia(item.donGia())
                            .isActive(true)
                            .build();
                    em.persist(chiTietHoaDon);
                }
                dongBoSoLuongTon(em, item.maThuoc());
            }
            if (phieuDatNguon != null) {
                phieuDatNguon.setReceived(true);
                em.merge(phieuDatNguon);
            }

            tx.commit();
            return hoaDon;
        } catch (RuntimeException ex) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw ex;
        } finally {
            em.close();
        }
    }

    private List<FifoAllocation> truTonKhoTheoFIFO(EntityManager em, String maThuoc, int soLuongCanMua) {
        if (soLuongCanMua <= 0) {
            throw new IllegalArgumentException("So luong mua phai lon hon 0");
        }
        List<ChiTietLoThuoc> loThuocs = em.createQuery(
                        "select c from ChiTietLoThuoc c where c.id.maThuoc = :maThuoc and c.isActive = true and c.soLuong > 0 order by c.ngaySanXuat asc",
                        ChiTietLoThuoc.class)
                .setParameter("maThuoc", maThuoc)
                .getResultList();

        int canTru = soLuongCanMua;
        List<FifoAllocation> allocations = new java.util.ArrayList<>();
        for (ChiTietLoThuoc loThuoc : loThuocs) {
            if (canTru <= 0) {
                break;
            }
            int soLuongLo = loThuoc.getSoLuong();
            int soLuongTru = Math.min(soLuongLo, canTru);
            loThuoc.setSoLuong(soLuongLo - soLuongTru);
            em.merge(loThuoc);
            canTru -= soLuongTru;
            allocations.add(new FifoAllocation(loThuoc.getId().getMaLo(), soLuongTru));
        }

        if (canTru > 0) {
            throw new IllegalStateException("Khong du ton kho cho ma thuoc " + maThuoc + ", con thieu " + canTru);
        }
        return allocations;
    }

    private List<FifoAllocation> truTonKhoTheoLoDuocChon(EntityManager em, String maThuoc, String maLo, int soLuongCanMua) {
        if (soLuongCanMua <= 0) {
            throw new IllegalArgumentException("So luong mua phai lon hon 0");
        }
        ChiTietLoThuoc chiTietLoThuoc = em.find(ChiTietLoThuoc.class, new ChiTietLoThuocId(maLo, maThuoc));
        if (chiTietLoThuoc == null || !chiTietLoThuoc.isActive()) {
            throw new IllegalStateException("Lo thuoc khong hop le: " + maThuoc + " - " + maLo);
        }
        if (chiTietLoThuoc.getSoLuong() < soLuongCanMua) {
            throw new IllegalStateException(
                    "Khong du ton kho cho lo " + maLo + " cua ma thuoc " + maThuoc + ", con thieu " + (soLuongCanMua - chiTietLoThuoc.getSoLuong()));
        }

        chiTietLoThuoc.setSoLuong(chiTietLoThuoc.getSoLuong() - soLuongCanMua);
        em.merge(chiTietLoThuoc);
        return List.of(new FifoAllocation(maLo, soLuongCanMua));
    }

    private void dongBoSoLuongTon(EntityManager em, String maThuoc) {
        int tongSoLuong = em.createQuery(
                        "select coalesce(sum(c.soLuong), 0) from ChiTietLoThuoc c where c.id.maThuoc = :maThuoc and c.isActive = true",
                        Number.class)
                .setParameter("maThuoc", maThuoc)
                .getSingleResult()
                .intValue();
        em.createQuery("update Thuoc t set t.soLuongTon = :soLuong where t.maThuoc = :maThuoc")
                .setParameter("soLuong", tongSoLuong)
                .setParameter("maThuoc", maThuoc)
                .executeUpdate();
    }

    private Map<String, HoaDonItemCommand> normalizeAndMergeItems(List<HoaDonItemCommand> items) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Danh sách chi tiết hóa đơn trống");
        }

        Map<String, HoaDonItemCommand> merged = new LinkedHashMap<>();
        for (HoaDonItemCommand rawItem : items) {
            if (rawItem == null) {
                throw new IllegalArgumentException("Chi tiết hóa đơn chứa dòng rỗng");
            }
            String maThuoc = normalizeUpper(rawItem.maThuoc());
            String maLo = normalizeUpper(rawItem.maLo());
            if (maThuoc == null || maThuoc.isBlank()) {
                throw new IllegalArgumentException("Mã thuốc không hợp lệ");
            }
            if (maLo == null || maLo.isBlank()) {
                throw new IllegalArgumentException("Mã lô không hợp lệ");
            }
            if (rawItem.soLuong() <= 0) {
                throw new IllegalArgumentException("Số lượng phải lớn hơn 0");
            }

            String key = maThuoc + "|" + maLo;
            HoaDonItemCommand existing = merged.get(key);
            if (existing == null) {
                merged.put(key, new HoaDonItemCommand(maThuoc, maLo, rawItem.soLuong(), rawItem.donGia()));
            } else {
                merged.put(
                        key,
                        new HoaDonItemCommand(
                                maThuoc,
                                maLo,
                                existing.soLuong() + rawItem.soLuong(),
                                existing.donGia()
                        )
                );
            }
        }
        return merged;
    }

    private void validateAgainstPhieuDat(EntityManager em, String maPhieuDat, Map<String, HoaDonItemCommand> normalizedItems) {
        if (maPhieuDat == null || maPhieuDat.isBlank()) {
            return;
        }

        List<ChiTietPhieuDat> chiTietPhieuDatList = em.createQuery(
                        "select c from ChiTietPhieuDat c " +
                                "where c.id.maPhieuDat = :maPhieuDat and c.isActive = true",
                        ChiTietPhieuDat.class)
                .setParameter("maPhieuDat", maPhieuDat)
                .getResultList();
        if (chiTietPhieuDatList.isEmpty()) {
            throw new IllegalArgumentException("Phiếu đặt không có chi tiết hợp lệ: " + maPhieuDat);
        }

        Map<String, Integer> expected = new LinkedHashMap<>();
        for (ChiTietPhieuDat chiTiet : chiTietPhieuDatList) {
            String key = chiTiet.getId().getMaThuoc() + "|" + chiTiet.getId().getMaLo();
            expected.put(key, expected.getOrDefault(key, 0) + (chiTiet.getSoLuong() == null ? 0 : chiTiet.getSoLuong()));
        }

        if (expected.size() != normalizedItems.size()) {
            throw new IllegalArgumentException("Chi tiết hóa đơn phải khớp toàn bộ chi tiết phiếu đặt");
        }

        for (Map.Entry<String, Integer> entry : expected.entrySet()) {
            HoaDonItemCommand item = normalizedItems.get(entry.getKey());
            if (item == null) {
                throw new IllegalArgumentException("Thiếu mặt hàng từ phiếu đặt: " + entry.getKey());
            }
            if (item.soLuong() != entry.getValue()) {
                throw new IllegalArgumentException("Số lượng không khớp với phiếu đặt cho mặt hàng: " + entry.getKey());
            }
        }
    }

    private String resolveMaKhachHang(String maKhachHang, PhieuDat phieuDatNguon) {
        String normalizedInput = normalizeUpper(maKhachHang);
        if (phieuDatNguon == null) {
            return normalizedInput;
        }
        String maKhachHangPhieuDat = phieuDatNguon.getKhachHang() == null
                ? null
                : normalizeUpper(phieuDatNguon.getKhachHang().getMaKH());
        if (maKhachHangPhieuDat == null) {
            return normalizedInput;
        }
        if (normalizedInput != null && !maKhachHangPhieuDat.equals(normalizedInput)) {
            throw new IllegalArgumentException("Khách hàng hóa đơn phải trùng với khách hàng của phiếu đặt");
        }
        return maKhachHangPhieuDat;
    }

    private String normalizeUpper(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        return trimmed.toUpperCase();
    }

    @Override
    public ChiTietLoThuoc getLot(String maLo, String maThuoc) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(ChiTietLoThuoc.class, new ChiTietLoThuocId(maLo, maThuoc));
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<HoaDon> getHoaDonById(String maHoaDon) {
        return hoaDonRepository.findById(maHoaDon);
    }

    @Override
    public List<HoaDon> findHoaDonByDateRange(java.time.LocalDate fromDate, java.time.LocalDate toDate) {
        return hoaDonRepository.findByDateRange(fromDate, toDate);
    }

    @Override
    public List<HoaDonView> getAllHoaDonViews() {
        return hoaDonRepository.findAllActiveWithNhanVienKhachHang()
                .stream()
                .map(this::toHoaDonView)
                .toList();
    }

    @Override
    public List<HoaDonView> getHoaDonViewsByMonthYear(int month, int year) {
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Tháng không hợp lệ");
        }
        if (year < 1) {
            throw new IllegalArgumentException("Năm không hợp lệ");
        }
        java.time.LocalDate fromDate = java.time.LocalDate.of(year, month, 1);
        java.time.LocalDate toDate = fromDate.withDayOfMonth(fromDate.lengthOfMonth());
        return hoaDonRepository.findByDateRange(fromDate, toDate)
                .stream()
                .map(this::toHoaDonView)
                .toList();
    }

    @Override
    public List<ChiTietHoaDonView> getChiTietHoaDonByMaHoaDon(String maHoaDon) {
        if (maHoaDon == null || maHoaDon.isBlank()) {
            throw new IllegalArgumentException("Mã hóa đơn không hợp lệ");
        }
        return chiTietHoaDonRepository.findByMaHoaDonWithThuoc(maHoaDon)
                .stream()
                .map(this::toChiTietHoaDonView)
                .toList();
    }

    @Override
    public String getLatestHoaDon() {
        return hoaDonRepository.getLatestHoaDon();
    }

    @Override
    public List<HoaDon> findHoaDonByThuoc(String maThuoc) {
        if (maThuoc == null || maThuoc.isBlank()) {
            throw new IllegalArgumentException("Mã thuốc không hợp lệ");
        }
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                            "select distinct h from HoaDon h join ChiTietHoaDon c on c.id.maHoaDon = h.maHoaDon " +
                                    "where h.isActive = true and c.isActive = true and c.id.maThuoc = :maThuoc " +
                                    "order by h.ngayBan desc, h.maHoaDon desc",
                            HoaDon.class)
                    .setParameter("maThuoc", maThuoc)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Integer> findNamCoHoaDon() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                            "select distinct year(h.ngayBan) from HoaDon h where h.isActive = true order by year(h.ngayBan)",
                            Integer.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Integer> findThangCoHoaDonTrongNam(int nam) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                            "select distinct month(h.ngayBan) from HoaDon h " +
                                    "where h.isActive = true and year(h.ngayBan) = :nam order by month(h.ngayBan)",
                            Integer.class)
                    .setParameter("nam", nam)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    private HoaDonView toHoaDonView(HoaDon hoaDon) {
        String maNhanVien = hoaDon.getNhanVien() == null ? null : hoaDon.getNhanVien().getMaNV();
        String tenNhanVien = hoaDon.getNhanVien() == null ? null : hoaDon.getNhanVien().getTenNV();
        String maKhachHang = hoaDon.getKhachHang() == null ? null : hoaDon.getKhachHang().getMaKH();
        String tenKhachHang = hoaDon.getKhachHang() == null ? null : hoaDon.getKhachHang().getTenKH();
        return new HoaDonView(
                hoaDon.getMaHoaDon(),
                maNhanVien,
                tenNhanVien,
                maKhachHang,
                tenKhachHang,
                hoaDon.getNgayBan(),
                hoaDon.getGhiChu()
        );
    }

    private ChiTietHoaDonView toChiTietHoaDonView(ChiTietHoaDon chiTietHoaDon) {
        return new ChiTietHoaDonView(
                chiTietHoaDon.getId().getMaHoaDon(),
                chiTietHoaDon.getId().getMaThuoc(),
                chiTietHoaDon.getThuoc() == null ? null : chiTietHoaDon.getThuoc().getTenThuoc(),
                chiTietHoaDon.getId().getMaLo(),
                chiTietHoaDon.getSoLuong(),
                chiTietHoaDon.getDonGia()
        );
    }

    private record FifoAllocation(String maLo, int soLuong) {
    }
}
