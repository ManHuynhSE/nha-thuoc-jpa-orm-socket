package com.pillchill.migration.service.impl;

import com.pillchill.migration.dto.ChiTietPhieuDoiTraView;
import com.pillchill.migration.dto.PhieuDoiTraView;
import com.pillchill.migration.entity.ChiTietPhieuDoiTra;
import com.pillchill.migration.entity.PhieuDoiTra;
import com.pillchill.migration.repository.IChiTietPhieuDoiTraRepository;
import com.pillchill.migration.repository.IPhieuDoiTraRepository;
import com.pillchill.migration.repository.impl.ChiTietPhieuDoiTraRepository;
import com.pillchill.migration.repository.impl.PhieuDoiTraRepository;
import com.pillchill.migration.service.IPhieuDoiTraService;

import java.time.LocalDate;
import java.util.List;

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
}
