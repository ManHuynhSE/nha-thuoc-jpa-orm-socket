package com.pillchill.migration.service.impl;

import com.pillchill.migration.dto.ChiTietPhieuDatView;
import com.pillchill.migration.dto.PhieuDatView;
import com.pillchill.migration.entity.ChiTietPhieuDat;
import com.pillchill.migration.entity.PhieuDat;
import com.pillchill.migration.repository.IChiTietPhieuDatRepository;
import com.pillchill.migration.repository.IPhieuDatRepository;
import com.pillchill.migration.repository.impl.ChiTietPhieuDatRepository;
import com.pillchill.migration.repository.impl.PhieuDatRepository;
import com.pillchill.migration.service.IPhieuDatService;

import java.time.LocalDate;
import java.util.List;

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
}
