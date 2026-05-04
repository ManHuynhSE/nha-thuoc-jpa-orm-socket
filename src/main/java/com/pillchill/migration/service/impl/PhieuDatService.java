package com.pillchill.migration.service.impl;

import com.pillchill.migration.dto.ChiTietPhieuDatView;
import com.pillchill.migration.dto.CreatePhieuDatCommand;
import com.pillchill.migration.dto.PhieuDatItemCommand;
import com.pillchill.migration.dto.PhieuDatView;
import com.pillchill.migration.db.JPAUtil;
import com.pillchill.migration.entity.ChiTietLoThuoc;
import com.pillchill.migration.entity.ChiTietPhieuDat;
import com.pillchill.migration.entity.KhachHang;
import com.pillchill.migration.entity.LoThuoc;
import com.pillchill.migration.entity.NhanVien;
import com.pillchill.migration.entity.PhieuDat;
import com.pillchill.migration.entity.Thuoc;
import com.pillchill.migration.entity.id.ChiTietLoThuocId;
import com.pillchill.migration.entity.id.ChiTietPhieuDatId;
import com.pillchill.migration.repository.IChiTietPhieuDatRepository;
import com.pillchill.migration.repository.IPhieuDatRepository;
import com.pillchill.migration.repository.impl.ChiTietPhieuDatRepository;
import com.pillchill.migration.repository.impl.PhieuDatRepository;
import com.pillchill.migration.service.IPhieuDatService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PhieuDatService implements IPhieuDatService {
    private final IPhieuDatRepository phieuDatRepository;
    private final IChiTietPhieuDatRepository chiTietPhieuDatRepository;

    public PhieuDatService(IPhieuDatRepository phieuDatRepository, IChiTietPhieuDatRepository chiTietPhieuDatRepository) {
        this.phieuDatRepository = phieuDatRepository;
        this.chiTietPhieuDatRepository = chiTietPhieuDatRepository;
    }

    public PhieuDatService(IPhieuDatRepository phieuDatRepository) {
        this(phieuDatRepository, new ChiTietPhieuDatRepository());
    }

    public PhieuDatService() {
        this(new PhieuDatRepository(), new ChiTietPhieuDatRepository());
    }

    @Override
    public List<PhieuDatView> getAllPhieuDatViews() {
        return phieuDatRepository.findAllActiveWithNhanVienKhachHang()
                .stream()
                .map(this::toPhieuDatView)
                .toList();
    }

    @Override
    public List<PhieuDatView> getPhieuDatViewsByMonthYear(int month, int year) {
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Tháng không hợp lệ");
        }
        if (year < 1) {
            throw new IllegalArgumentException("Năm không hợp lệ");
        }
        LocalDate fromDate = LocalDate.of(year, month, 1);
        LocalDate toDate = fromDate.withDayOfMonth(fromDate.lengthOfMonth());
        return phieuDatRepository.findByDateRange(fromDate, toDate)
                .stream()
                .map(this::toPhieuDatView)
                .toList();
    }

    @Override
    public List<ChiTietPhieuDatView> getChiTietPhieuDatByMaPhieuDat(String maPhieuDat) {
        if (maPhieuDat == null || maPhieuDat.isBlank()) {
            throw new IllegalArgumentException("Mã phiếu đặt không hợp lệ");
        }
        return chiTietPhieuDatRepository.findByMaPhieuDatWithThuoc(maPhieuDat)
                .stream()
                .map(this::toChiTietPhieuDatView)
                .toList();
    }

    @Override
    public String createPhieuDat(CreatePhieuDatCommand command, String maNhanVien) {
        if (command == null) {
            throw new IllegalArgumentException("Dữ liệu tạo phiếu đặt không hợp lệ");
        }
        if (maNhanVien == null || maNhanVien.isBlank()) {
            throw new IllegalArgumentException("Mã nhân viên không hợp lệ");
        }

        List<PhieuDatItemCommand> normalizedItems = normalizeAndMergeItems(command.items());
        String normalizedMaKH = normalizeUpper(command.maKhachHang());
        String normalizedGhiChu = normalizeText(command.ghiChu());

        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            NhanVien nhanVien = em.find(NhanVien.class, normalizeUpper(maNhanVien));
            if (nhanVien == null || !nhanVien.isActive()) {
                throw new IllegalArgumentException("Nhân viên không tồn tại hoặc đã ngưng hoạt động: " + maNhanVien);
            }

            KhachHang khachHang = null;
            if (normalizedMaKH != null && !normalizedMaKH.isBlank()) {
                khachHang = em.find(KhachHang.class, normalizedMaKH);
                if (khachHang == null || !khachHang.isActive()) {
                    throw new IllegalArgumentException("Khách hàng không tồn tại hoặc đã ngưng hoạt động: " + normalizedMaKH);
                }
            }

            String maPhieuDat = taoMaPhieuDatTuDong(em);
            PhieuDat phieuDat = new PhieuDat();
            phieuDat.setMaPhieuDat(maPhieuDat);
            phieuDat.setNhanVien(nhanVien);
            phieuDat.setNgayDat(LocalDate.now());
            phieuDat.setKhachHang(khachHang);
            phieuDat.setGhiChu(normalizedGhiChu);
            phieuDat.setReceived(false);
            phieuDat.setActive(true);
            em.persist(phieuDat);

            for (PhieuDatItemCommand item : normalizedItems) {
                Thuoc thuoc = em.find(Thuoc.class, item.maThuoc());
                if (thuoc == null || !thuoc.isActive()) {
                    throw new IllegalArgumentException("Thuốc không tồn tại hoặc đã ngưng hoạt động: " + item.maThuoc());
                }

                LoThuoc loThuoc = em.find(LoThuoc.class, item.maLo());
                if (loThuoc == null || !loThuoc.isActive()) {
                    throw new IllegalArgumentException("Lô thuốc không tồn tại hoặc đã ngưng hoạt động: " + item.maLo());
                }

                ChiTietLoThuoc chiTietLoThuoc = em.find(ChiTietLoThuoc.class, new ChiTietLoThuocId(item.maLo(), item.maThuoc()));
                if (chiTietLoThuoc == null || !chiTietLoThuoc.isActive()) {
                    throw new IllegalArgumentException("Cặp thuốc/lô không tồn tại hoặc đã ngưng hoạt động: " + item.maThuoc() + " - " + item.maLo());
                }

                ChiTietPhieuDat chiTietPhieuDat = ChiTietPhieuDat.builder()
                        .id(new ChiTietPhieuDatId(maPhieuDat, item.maThuoc(), item.maLo()))
                        .phieuDat(phieuDat)
                        .thuoc(thuoc)
                        .loThuoc(loThuoc)
                        .tenThuoc(thuoc.getTenThuoc())
                        .soLuong(item.soLuong())
                        .isActive(true)
                        .build();
                em.persist(chiTietPhieuDat);
            }

            tx.commit();
            return maPhieuDat;
        } catch (RuntimeException ex) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw ex;
        } finally {
            em.close();
        }
    }

    private PhieuDatView toPhieuDatView(PhieuDat phieuDat) {
        String maNhanVien = phieuDat.getNhanVien() == null ? null : phieuDat.getNhanVien().getMaNV();
        String tenNhanVien = phieuDat.getNhanVien() == null ? null : phieuDat.getNhanVien().getTenNV();
        String maKhachHang = phieuDat.getKhachHang() == null ? null : phieuDat.getKhachHang().getMaKH();
        String tenKhachHang = phieuDat.getKhachHang() == null ? null : phieuDat.getKhachHang().getTenKH();
        return new PhieuDatView(
                phieuDat.getMaPhieuDat(),
                maNhanVien,
                tenNhanVien,
                maKhachHang,
                tenKhachHang,
                phieuDat.getNgayDat(),
                phieuDat.isReceived(),
                phieuDat.getGhiChu()
        );
    }

    private ChiTietPhieuDatView toChiTietPhieuDatView(ChiTietPhieuDat chiTietPhieuDat) {
        return new ChiTietPhieuDatView(
                chiTietPhieuDat.getId().getMaPhieuDat(),
                chiTietPhieuDat.getId().getMaThuoc(),
                chiTietPhieuDat.getThuoc() == null ? chiTietPhieuDat.getTenThuoc() : chiTietPhieuDat.getThuoc().getTenThuoc(),
                chiTietPhieuDat.getId().getMaLo(),
                chiTietPhieuDat.getSoLuong() == null ? 0 : chiTietPhieuDat.getSoLuong()
        );
    }

    private List<PhieuDatItemCommand> normalizeAndMergeItems(List<PhieuDatItemCommand> items) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Danh sách chi tiết phiếu đặt trống");
        }

        Map<String, Integer> merged = new LinkedHashMap<>();
        for (PhieuDatItemCommand rawItem : items) {
            if (rawItem == null) {
                throw new IllegalArgumentException("Chi tiết phiếu đặt chứa dòng rỗng");
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
                throw new IllegalArgumentException("Số lượng phải lớn hơn 0 cho thuốc " + maThuoc);
            }
            String key = maThuoc + "|" + maLo;
            merged.put(key, merged.getOrDefault(key, 0) + rawItem.soLuong());
        }

        List<PhieuDatItemCommand> result = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : merged.entrySet()) {
            String[] parts = entry.getKey().split("\\|", 2);
            result.add(new PhieuDatItemCommand(parts[0], parts[1], entry.getValue()));
        }
        return result;
    }

    private String taoMaPhieuDatTuDong(EntityManager em) {
        String prefix = "PD";
        String maxMa = em.createQuery(
                        "select max(p.maPhieuDat) from PhieuDat p where p.maPhieuDat like :prefix",
                        String.class)
                .setParameter("prefix", prefix + "%")
                .getSingleResult();

        if (maxMa == null || maxMa.length() <= prefix.length()) {
            return prefix + "001";
        }

        int nextNumber;
        try {
            nextNumber = Integer.parseInt(maxMa.substring(prefix.length())) + 1;
        } catch (NumberFormatException ex) {
            throw new IllegalStateException("Mã phiếu đặt hiện tại không đúng định dạng: " + maxMa);
        }
        return String.format(Locale.ROOT, "%s%03d", prefix, nextNumber);
    }

    private String normalizeUpper(String value) {
        if (value == null) {
            return null;
        }
        return value.trim().toUpperCase(Locale.ROOT);
    }

    private String normalizeText(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
