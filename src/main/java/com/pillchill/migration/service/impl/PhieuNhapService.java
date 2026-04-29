package com.pillchill.migration.service.impl;

import com.pillchill.migration.dto.ChiTietPhieuNhapView;
import com.pillchill.migration.dto.PhieuNhapImportItem;
import com.pillchill.migration.dto.PhieuNhapView;
import com.pillchill.migration.db.JPAUtil;
import com.pillchill.migration.entity.ChiTietLoThuoc;
import com.pillchill.migration.entity.ChiTietPhieuNhap;
import com.pillchill.migration.entity.DonVi;
import com.pillchill.migration.entity.LoThuoc;
import com.pillchill.migration.entity.NhaSanXuat;
import com.pillchill.migration.entity.NhanVien;
import com.pillchill.migration.entity.PhieuNhapThuoc;
import com.pillchill.migration.entity.Thuoc;
import com.pillchill.migration.entity.id.ChiTietLoThuocId;
import com.pillchill.migration.entity.id.ChiTietPhieuNhapId;
import com.pillchill.migration.repository.IChiTietPhieuNhapRepository;
import com.pillchill.migration.repository.IPhieuNhapRepository;
import com.pillchill.migration.repository.impl.ChiTietPhieuNhapRepository;
import com.pillchill.migration.repository.impl.PhieuNhapRepository;
import com.pillchill.migration.service.IPhieuNhapService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class PhieuNhapService implements IPhieuNhapService {
    private final IPhieuNhapRepository phieuNhapRepository;
    private final IChiTietPhieuNhapRepository chiTietPhieuNhapRepository;

    public PhieuNhapService(IPhieuNhapRepository phieuNhapRepository, IChiTietPhieuNhapRepository chiTietPhieuNhapRepository) {
        this.phieuNhapRepository = phieuNhapRepository;
        this.chiTietPhieuNhapRepository = chiTietPhieuNhapRepository;
    }

    public PhieuNhapService(IPhieuNhapRepository phieuNhapRepository) {
        this(phieuNhapRepository, new ChiTietPhieuNhapRepository());
    }

    public PhieuNhapService() {
        this(new PhieuNhapRepository(), new ChiTietPhieuNhapRepository());
    }

    @Override
    public List<PhieuNhapView> getAllPhieuNhapViews() {
        return phieuNhapRepository.findAllActiveWithNhanVien()
                .stream()
                .map(this::toPhieuNhapView)
                .toList();
    }

    @Override
    public List<ChiTietPhieuNhapView> getChiTietPhieuNhapByMaPhieuNhap(String maPhieuNhapThuoc) {
        if (maPhieuNhapThuoc == null || maPhieuNhapThuoc.isBlank()) {
            throw new IllegalArgumentException("Mã phiếu nhập không hợp lệ");
        }
        return chiTietPhieuNhapRepository.findViewByMaPhieuNhap(maPhieuNhapThuoc);
    }

    @Override
    public String importFromExcel(List<PhieuNhapImportItem> items, String maNhanVien) {
        List<PhieuNhapImportItem> normalizedItems = normalizeAndValidateItems(items);
        if (maNhanVien == null || maNhanVien.isBlank()) {
            throw new IllegalArgumentException("Mã nhân viên không hợp lệ");
        }

        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            NhanVien nhanVien = em.find(NhanVien.class, maNhanVien);
            if (nhanVien == null || !nhanVien.isActive()) {
                throw new IllegalArgumentException("Nhân viên không tồn tại hoặc đã ngưng hoạt động: " + maNhanVien);
            }

            String maPhieuNhap = taoMaPhieuNhapTuDong(em);
            PhieuNhapThuoc phieuNhapThuoc = PhieuNhapThuoc.builder()
                    .maPhieuNhapThuoc(maPhieuNhap)
                    .nhanVien(nhanVien)
                    .ngayNhap(LocalDate.now())
                    .isActive(true)
                    .build();
            em.persist(phieuNhapThuoc);

            Set<String> maThuocCanDongBoTonKho = new HashSet<>();
            for (PhieuNhapImportItem item : normalizedItems) {
                DonVi donVi = em.find(DonVi.class, item.getMaDonVi());
                if (donVi == null || !donVi.isActive()) {
                    throw new IllegalArgumentException("Đơn vị không tồn tại hoặc đã ngưng hoạt động: " + item.getMaDonVi());
                }

                NhaSanXuat nhaSanXuat = em.find(NhaSanXuat.class, item.getMaNSX());
                if (nhaSanXuat == null || !nhaSanXuat.isActive()) {
                    throw new IllegalArgumentException("Nhà sản xuất không tồn tại hoặc đã ngưng hoạt động: " + item.getMaNSX());
                }

                Thuoc thuoc = em.find(Thuoc.class, item.getMaThuoc());
                if (thuoc == null) {
                    thuoc = Thuoc.builder()
                            .maThuoc(item.getMaThuoc())
                            .tenThuoc(item.getTenThuoc())
                            .soLuongTon(0)
                            .donVi(donVi)
                            .soLuongToiThieu(item.getSoLuongToiThieu())
                            .nhaSanXuat(nhaSanXuat)
                            .isActive(true)
                            .build();
                    em.persist(thuoc);
                } else {
                    thuoc.setTenThuoc(item.getTenThuoc());
                    thuoc.setDonVi(donVi);
                    thuoc.setNhaSanXuat(nhaSanXuat);
                    thuoc.setSoLuongToiThieu(item.getSoLuongToiThieu());
                    thuoc.setActive(true);
                    em.merge(thuoc);
                }

                LoThuoc loThuoc = em.find(LoThuoc.class, item.getMaLo());
                if (loThuoc == null) {
                    loThuoc = LoThuoc.builder()
                            .maLo(item.getMaLo())
                            .nhaSanXuat(nhaSanXuat)
                            .isActive(true)
                            .build();
                    em.persist(loThuoc);
                } else {
                    loThuoc.setNhaSanXuat(nhaSanXuat);
                    loThuoc.setActive(true);
                    em.merge(loThuoc);
                }

                ChiTietLoThuocId chiTietLoThuocId = new ChiTietLoThuocId(item.getMaLo(), item.getMaThuoc());
                ChiTietLoThuoc chiTietLoThuoc = em.find(ChiTietLoThuoc.class, chiTietLoThuocId);
                if (chiTietLoThuoc == null) {
                    chiTietLoThuoc = ChiTietLoThuoc.builder()
                            .id(chiTietLoThuocId)
                            .loThuoc(loThuoc)
                            .thuoc(thuoc)
                            .ngaySanXuat(item.getNgaySanXuat())
                            .hanSuDung(item.getHanSuDung())
                            .soLuong(item.getSoLuong())
                            .giaNhap(item.getGiaNhap())
                            .isActive(true)
                            .build();
                    em.persist(chiTietLoThuoc);
                } else {
                    chiTietLoThuoc.setNgaySanXuat(item.getNgaySanXuat());
                    chiTietLoThuoc.setHanSuDung(item.getHanSuDung());
                    chiTietLoThuoc.setGiaNhap(item.getGiaNhap());
                    chiTietLoThuoc.setSoLuong(chiTietLoThuoc.getSoLuong() + item.getSoLuong());
                    chiTietLoThuoc.setActive(true);
                    em.merge(chiTietLoThuoc);
                }

                ChiTietPhieuNhap chiTietPhieuNhap = ChiTietPhieuNhap.builder()
                        .id(new ChiTietPhieuNhapId(maPhieuNhap, item.getMaLo(), item.getMaThuoc()))
                        .phieuNhapThuoc(phieuNhapThuoc)
                        .loThuoc(loThuoc)
                        .thuoc(thuoc)
                        .soLuong(item.getSoLuong())
                        .donGia(item.getGiaNhap())
                        .isActive(true)
                        .build();
                em.persist(chiTietPhieuNhap);
                maThuocCanDongBoTonKho.add(item.getMaThuoc());
            }

            for (String maThuoc : maThuocCanDongBoTonKho) {
                int tongSoLuong = em.createQuery(
                                "select coalesce(sum(c.soLuong), 0) from ChiTietLoThuoc c " +
                                        "where c.id.maThuoc = :maThuoc and c.isActive = true",
                                Number.class)
                        .setParameter("maThuoc", maThuoc)
                        .getSingleResult()
                        .intValue();

                em.createQuery("update Thuoc t set t.soLuongTon = :soLuong where t.maThuoc = :maThuoc")
                        .setParameter("soLuong", tongSoLuong)
                        .setParameter("maThuoc", maThuoc)
                        .executeUpdate();
            }

            tx.commit();
            return maPhieuNhap;
        } catch (RuntimeException ex) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw ex;
        } finally {
            em.close();
        }
    }

    private PhieuNhapView toPhieuNhapView(PhieuNhapThuoc phieuNhapThuoc) {
        String maNhanVien = phieuNhapThuoc.getNhanVien() == null ? null : phieuNhapThuoc.getNhanVien().getMaNV();
        String tenNhanVien = phieuNhapThuoc.getNhanVien() == null ? null : phieuNhapThuoc.getNhanVien().getTenNV();
        return new PhieuNhapView(
                phieuNhapThuoc.getMaPhieuNhapThuoc(),
                maNhanVien,
                tenNhanVien,
                phieuNhapThuoc.getNgayNhap()
        );
    }

    private List<PhieuNhapImportItem> normalizeAndValidateItems(List<PhieuNhapImportItem> items) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Danh sách nhập thuốc trống");
        }

        Map<String, PhieuNhapImportItem> mergedMap = new LinkedHashMap<>();
        LocalDate now = LocalDate.now();

        for (PhieuNhapImportItem rawItem : items) {
            if (rawItem == null) {
                throw new IllegalArgumentException("Danh sách nhập chứa dòng rỗng");
            }

            PhieuNhapImportItem normalized = normalizeItem(rawItem);
            validateItem(normalized, now);

            String key = normalized.getMaThuoc() + "|" + normalized.getMaLo();
            if (!mergedMap.containsKey(key)) {
                mergedMap.put(key, normalized);
                continue;
            }

            PhieuNhapImportItem existing = mergedMap.get(key);
            if (!isSameIdentity(existing, normalized)) {
                throw new IllegalArgumentException("Dữ liệu trùng mã thuốc/mã lô nhưng thông tin khác nhau: " + key);
            }
            existing.setSoLuong(existing.getSoLuong() + normalized.getSoLuong());
        }

        return new ArrayList<>(mergedMap.values());
    }

    private PhieuNhapImportItem normalizeItem(PhieuNhapImportItem item) {
        return new PhieuNhapImportItem(
                normalizeUpper(item.getMaThuoc()),
                normalizeUpper(item.getMaLo()),
                normalizeText(item.getTenThuoc()),
                item.getSoLuong(),
                item.getGiaNhap(),
                normalizeLower(item.getMaDonVi()),
                item.getSoLuongToiThieu(),
                normalizeUpper(item.getMaNSX()),
                item.getNgaySanXuat(),
                item.getHanSuDung()
        );
    }

    private void validateItem(PhieuNhapImportItem item, LocalDate now) {
        if (item.getMaThuoc() == null || !item.getMaThuoc().matches("^T\\d{3}$")) {
            throw new IllegalArgumentException("Mã thuốc không hợp lệ: " + item.getMaThuoc());
        }
        if (item.getMaLo() == null || !item.getMaLo().matches("^LO\\d{3}$")) {
            throw new IllegalArgumentException("Mã lô không hợp lệ: " + item.getMaLo());
        }
        if (item.getTenThuoc() == null || item.getTenThuoc().isBlank()) {
            throw new IllegalArgumentException("Tên thuốc không được để trống");
        }
        if (item.getSoLuong() == null || item.getSoLuong() <= 0) {
            throw new IllegalArgumentException("Số lượng phải lớn hơn 0: " + item.getMaThuoc());
        }
        if (item.getGiaNhap() == null || item.getGiaNhap() <= 0) {
            throw new IllegalArgumentException("Giá nhập phải lớn hơn 0: " + item.getMaThuoc());
        }
        if (item.getSoLuongToiThieu() == null || item.getSoLuongToiThieu() < 0) {
            throw new IllegalArgumentException("Số lượng tối thiểu không hợp lệ: " + item.getMaThuoc());
        }
        if (item.getMaDonVi() == null || item.getMaDonVi().isBlank()) {
            throw new IllegalArgumentException("Mã đơn vị không hợp lệ: " + item.getMaThuoc());
        }
        if (item.getMaNSX() == null || !item.getMaNSX().matches("^NSX\\d{3}$")) {
            throw new IllegalArgumentException("Mã NSX không hợp lệ: " + item.getMaNSX());
        }
        if (item.getNgaySanXuat() == null || item.getHanSuDung() == null) {
            throw new IllegalArgumentException("Ngày sản xuất/Hạn sử dụng không hợp lệ: " + item.getMaThuoc());
        }
        if (item.getHanSuDung().isBefore(item.getNgaySanXuat())) {
            throw new IllegalArgumentException("Hạn sử dụng phải sau ngày sản xuất: " + item.getMaThuoc());
        }
        if (item.getHanSuDung().isBefore(now)) {
            throw new IllegalArgumentException("Thuốc đã hết hạn sử dụng: " + item.getMaThuoc());
        }
    }

    private boolean isSameIdentity(PhieuNhapImportItem left, PhieuNhapImportItem right) {
        return equalsString(left.getTenThuoc(), right.getTenThuoc())
                && equalsString(left.getMaDonVi(), right.getMaDonVi())
                && equalsString(left.getMaNSX(), right.getMaNSX())
                && equalsObject(left.getSoLuongToiThieu(), right.getSoLuongToiThieu())
                && equalsObject(left.getGiaNhap(), right.getGiaNhap())
                && equalsObject(left.getNgaySanXuat(), right.getNgaySanXuat())
                && equalsObject(left.getHanSuDung(), right.getHanSuDung());
    }

    private String taoMaPhieuNhapTuDong(EntityManager em) {
        String prefix = "PNT";
        String maxMa = em.createQuery(
                        "select max(p.maPhieuNhapThuoc) from PhieuNhapThuoc p where p.maPhieuNhapThuoc like :prefix",
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
            throw new IllegalStateException("Mã phiếu nhập hiện tại không đúng định dạng: " + maxMa);
        }
        return String.format(Locale.ROOT, "%s%03d", prefix, nextNumber);
    }

    private String normalizeUpper(String value) {
        if (value == null) {
            return null;
        }
        return value.trim().toUpperCase(Locale.ROOT);
    }

    private String normalizeLower(String value) {
        if (value == null) {
            return null;
        }
        return value.trim().toLowerCase(Locale.ROOT);
    }

    private String normalizeText(String value) {
        if (value == null) {
            return null;
        }
        return value.trim();
    }

    private boolean equalsString(String left, String right) {
        if (left == null) {
            return right == null;
        }
        return left.equals(right);
    }

    private boolean equalsObject(Object left, Object right) {
        if (left == null) {
            return right == null;
        }
        return left.equals(right);
    }
}
