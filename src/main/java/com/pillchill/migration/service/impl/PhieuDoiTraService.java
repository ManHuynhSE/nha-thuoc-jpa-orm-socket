package com.pillchill.migration.service.impl;

import com.pillchill.migration.dto.ChiTietPhieuDoiTraView;
import com.pillchill.migration.dto.CreatePhieuDoiTraCommand;
import com.pillchill.migration.dto.PhieuDoiTraItemCommand;
import com.pillchill.migration.dto.PhieuDoiTraView;
import com.pillchill.migration.db.JPAUtil;
import com.pillchill.migration.entity.ChiTietLoThuoc;
import com.pillchill.migration.entity.ChiTietPhieuDoiTra;
import com.pillchill.migration.entity.HoaDon;
import com.pillchill.migration.entity.KhachHang;
import com.pillchill.migration.entity.LoThuoc;
import com.pillchill.migration.entity.NhanVien;
import com.pillchill.migration.entity.PhieuDoiTra;
import com.pillchill.migration.entity.Thuoc;
import com.pillchill.migration.entity.id.ChiTietLoThuocId;
import com.pillchill.migration.entity.id.ChiTietPhieuDoiTraId;
import com.pillchill.migration.repository.IChiTietPhieuDoiTraRepository;
import com.pillchill.migration.repository.IPhieuDoiTraRepository;
import com.pillchill.migration.repository.impl.ChiTietPhieuDoiTraRepository;
import com.pillchill.migration.repository.impl.PhieuDoiTraRepository;
import com.pillchill.migration.service.IPhieuDoiTraService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class PhieuDoiTraService implements IPhieuDoiTraService {
    private final IPhieuDoiTraRepository phieuDoiTraRepository;
    private final IChiTietPhieuDoiTraRepository chiTietPhieuDoiTraRepository;

    public PhieuDoiTraService(IPhieuDoiTraRepository phieuDoiTraRepository, IChiTietPhieuDoiTraRepository chiTietPhieuDoiTraRepository) {
        this.phieuDoiTraRepository = phieuDoiTraRepository;
        this.chiTietPhieuDoiTraRepository = chiTietPhieuDoiTraRepository;
    }

    public PhieuDoiTraService(IPhieuDoiTraRepository phieuDoiTraRepository) {
        this(phieuDoiTraRepository, new ChiTietPhieuDoiTraRepository());
    }

    public PhieuDoiTraService() {
        this(new PhieuDoiTraRepository(), new ChiTietPhieuDoiTraRepository());
    }

    @Override
    public List<PhieuDoiTraView> getAllPhieuDoiTraViews() {
        return phieuDoiTraRepository.findAllActiveWithNhanVienKhachHang()
                .stream()
                .map(this::toPhieuDoiTraView)
                .toList();
    }

    @Override
    public List<PhieuDoiTraView> getPhieuDoiTraViewsByMonthYear(int month, int year) {
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Tháng không hợp lệ");
        }
        if (year < 1) {
            throw new IllegalArgumentException("Năm không hợp lệ");
        }
        LocalDate fromDate = LocalDate.of(year, month, 1);
        LocalDate toDate = fromDate.withDayOfMonth(fromDate.lengthOfMonth());
        return phieuDoiTraRepository.findByDateRange(fromDate, toDate)
                .stream()
                .map(this::toPhieuDoiTraView)
                .toList();
    }

    @Override
    public List<ChiTietPhieuDoiTraView> getChiTietPhieuDoiTraByMaPhieuDoiTra(String maPhieuDoiTra) {
        if (maPhieuDoiTra == null || maPhieuDoiTra.isBlank()) {
            throw new IllegalArgumentException("Mã phiếu đổi trả không hợp lệ");
        }
        return chiTietPhieuDoiTraRepository.findByMaPhieuDoiTraWithThuoc(maPhieuDoiTra)
                .stream()
                .map(this::toChiTietPhieuDoiTraView)
                .toList();
    }

    @Override
    public String createPhieuDoiTra(CreatePhieuDoiTraCommand command, String maNhanVien) {
        if (command == null) {
            throw new IllegalArgumentException("Dữ liệu tạo phiếu đổi trả không hợp lệ");
        }
        if (maNhanVien == null || maNhanVien.isBlank()) {
            throw new IllegalArgumentException("Mã nhân viên không hợp lệ");
        }
        String maHoaDon = normalizeUpper(command.maHoaDon());
        if (maHoaDon == null || maHoaDon.isBlank()) {
            throw new IllegalArgumentException("Mã hóa đơn không hợp lệ");
        }
        List<PhieuDoiTraItemCommand> normalizedItems = normalizeAndMergeItems(command.items());

        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            NhanVien nhanVien = em.find(NhanVien.class, normalizeUpper(maNhanVien));
            if (nhanVien == null || !nhanVien.isActive()) {
                throw new IllegalArgumentException("Nhân viên không tồn tại hoặc đã ngưng hoạt động: " + maNhanVien);
            }

            HoaDon hoaDon = em.find(HoaDon.class, maHoaDon);
            if (hoaDon == null || !hoaDon.isActive()) {
                throw new IllegalArgumentException("Hóa đơn không tồn tại hoặc đã ngưng hoạt động: " + maHoaDon);
            }
            KhachHang khachHang = hoaDon.getKhachHang();
            if (khachHang != null && !khachHang.isActive()) {
                throw new IllegalArgumentException("Khách hàng của hóa đơn đã ngưng hoạt động: " + khachHang.getMaKH());
            }

            String maPhieuDoiTra = taoMaPhieuDoiTraTuDong(em);
            PhieuDoiTra phieuDoiTra = new PhieuDoiTra();
            phieuDoiTra.setMaPhieuDoiTra(maPhieuDoiTra);
            phieuDoiTra.setNgayDoiTra(LocalDate.now());
            phieuDoiTra.setNhanVien(nhanVien);
            phieuDoiTra.setKhachHang(khachHang);
            phieuDoiTra.setActive(true);
            em.persist(phieuDoiTra);

            Set<String> maThuocCanDongBoTonKho = new HashSet<>();
            for (PhieuDoiTraItemCommand item : normalizedItems) {
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

                int soLuongDaMua = em.createQuery(
                                "select coalesce(sum(c.soLuong), 0) from ChiTietHoaDon c " +
                                        "where c.id.maHoaDon = :maHoaDon and c.id.maThuoc = :maThuoc and c.id.maLo = :maLo and c.isActive = true",
                                Number.class)
                        .setParameter("maHoaDon", maHoaDon)
                        .setParameter("maThuoc", item.maThuoc())
                        .setParameter("maLo", item.maLo())
                        .getSingleResult()
                        .intValue();
                if (soLuongDaMua <= 0) {
                    throw new IllegalArgumentException("Thuốc/lô không thuộc hóa đơn đã chọn: " + item.maThuoc() + " - " + item.maLo());
                }
                if (item.soLuong() > soLuongDaMua) {
                    throw new IllegalArgumentException(
                            "Số lượng trả vượt quá số lượng đã mua cho " + item.maThuoc() + " - " + item.maLo() +
                                    " (đã mua: " + soLuongDaMua + ", yêu cầu trả: " + item.soLuong() + ")"
                    );
                }

                ChiTietPhieuDoiTra chiTietPhieuDoiTra = ChiTietPhieuDoiTra.builder()
                        .id(new ChiTietPhieuDoiTraId(maPhieuDoiTra, item.maThuoc(), item.maLo()))
                        .phieuDoiTra(phieuDoiTra)
                        .thuoc(thuoc)
                        .loThuoc(loThuoc)
                        .soLuong(item.soLuong())
                        .donGia(item.donGia())
                        .lyDo(normalizeText(item.lyDo()))
                        .isActive(true)
                        .build();
                em.persist(chiTietPhieuDoiTra);

                chiTietLoThuoc.setSoLuong(chiTietLoThuoc.getSoLuong() + item.soLuong());
                em.merge(chiTietLoThuoc);
                maThuocCanDongBoTonKho.add(item.maThuoc());
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
            return maPhieuDoiTra;
        } catch (RuntimeException ex) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw ex;
        } finally {
            em.close();
        }
    }

    private PhieuDoiTraView toPhieuDoiTraView(PhieuDoiTra phieuDoiTra) {
        String maNhanVien = phieuDoiTra.getNhanVien() == null ? null : phieuDoiTra.getNhanVien().getMaNV();
        String tenNhanVien = phieuDoiTra.getNhanVien() == null ? null : phieuDoiTra.getNhanVien().getTenNV();
        String maKhachHang = phieuDoiTra.getKhachHang() == null ? null : phieuDoiTra.getKhachHang().getMaKH();
        String tenKhachHang = phieuDoiTra.getKhachHang() == null ? null : phieuDoiTra.getKhachHang().getTenKH();
        return new PhieuDoiTraView(
                phieuDoiTra.getMaPhieuDoiTra(),
                maNhanVien,
                tenNhanVien,
                maKhachHang,
                tenKhachHang,
                phieuDoiTra.getNgayDoiTra()
        );
    }

    private ChiTietPhieuDoiTraView toChiTietPhieuDoiTraView(ChiTietPhieuDoiTra chiTietPhieuDoiTra) {
        return new ChiTietPhieuDoiTraView(
                chiTietPhieuDoiTra.getId().getMaPhieuDoiTra(),
                chiTietPhieuDoiTra.getId().getMaThuoc(),
                chiTietPhieuDoiTra.getThuoc() == null ? null : chiTietPhieuDoiTra.getThuoc().getTenThuoc(),
                chiTietPhieuDoiTra.getId().getMaLo(),
                chiTietPhieuDoiTra.getSoLuong() == null ? 0 : chiTietPhieuDoiTra.getSoLuong(),
                chiTietPhieuDoiTra.getDonGia() == null ? 0 : chiTietPhieuDoiTra.getDonGia(),
                chiTietPhieuDoiTra.getLyDo()
        );
    }

    private List<PhieuDoiTraItemCommand> normalizeAndMergeItems(List<PhieuDoiTraItemCommand> items) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Danh sách chi tiết phiếu đổi trả trống");
        }

        Map<String, PhieuDoiTraItemCommand> merged = new LinkedHashMap<>();
        for (PhieuDoiTraItemCommand rawItem : items) {
            if (rawItem == null) {
                throw new IllegalArgumentException("Chi tiết phiếu đổi trả chứa dòng rỗng");
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
                throw new IllegalArgumentException("Số lượng trả phải lớn hơn 0 cho thuốc " + maThuoc);
            }
            if (rawItem.donGia() < 0) {
                throw new IllegalArgumentException("Đơn giá trả không hợp lệ cho thuốc " + maThuoc);
            }

            String key = maThuoc + "|" + maLo;
            PhieuDoiTraItemCommand existing = merged.get(key);
            if (existing == null) {
                merged.put(
                        key,
                        new PhieuDoiTraItemCommand(
                                maThuoc,
                                maLo,
                                rawItem.soLuong(),
                                rawItem.donGia(),
                                normalizeText(rawItem.lyDo())
                        )
                );
                continue;
            }

            if (Math.abs(existing.donGia() - rawItem.donGia()) > 0.0001d) {
                throw new IllegalArgumentException("Đơn giá không nhất quán cho cùng thuốc/lô: " + maThuoc + " - " + maLo);
            }
            String lyDo = existing.lyDo();
            if ((lyDo == null || lyDo.isBlank()) && rawItem.lyDo() != null && !rawItem.lyDo().isBlank()) {
                lyDo = normalizeText(rawItem.lyDo());
            }
            merged.put(
                    key,
                    new PhieuDoiTraItemCommand(
                            maThuoc,
                            maLo,
                            existing.soLuong() + rawItem.soLuong(),
                            existing.donGia(),
                            lyDo
                    )
            );
        }

        return new ArrayList<>(merged.values());
    }

    private String taoMaPhieuDoiTraTuDong(EntityManager em) {
        String prefix = "PDT";
        String maxMa = em.createQuery(
                        "select max(p.maPhieuDoiTra) from PhieuDoiTra p where p.maPhieuDoiTra like :prefix",
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
            throw new IllegalStateException("Mã phiếu đổi trả hiện tại không đúng định dạng: " + maxMa);
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
