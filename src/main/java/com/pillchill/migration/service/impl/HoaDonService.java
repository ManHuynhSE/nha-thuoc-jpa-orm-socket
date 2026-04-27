package com.pillchill.migration.service.impl;

import java.util.List;
import java.util.Optional;

import com.pillchill.migration.dto.ChiTietHoaDonView;
import com.pillchill.migration.entity.ChiTietHoaDon;
import com.pillchill.migration.entity.ChiTietLoThuoc;
import com.pillchill.migration.entity.HoaDon;
import com.pillchill.migration.entity.KhachHang;
import com.pillchill.migration.entity.KhuyenMai;
import com.pillchill.migration.entity.LoThuoc;
import com.pillchill.migration.entity.NhanVien;
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
            NhanVien nhanVien = em.getReference(NhanVien.class, command.maNV());
            KhachHang khachHang = command.maKH() == null || command.maKH().isBlank()
                    ? null
                    : em.getReference(KhachHang.class, command.maKH());
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

            for (HoaDonItemCommand item : command.items()) {
                Thuoc thuoc = em.getReference(Thuoc.class, item.maThuoc());
                List<FifoAllocation> allocations = truTonKhoTheoFIFO(em, item.maThuoc(), item.soLuong());
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
