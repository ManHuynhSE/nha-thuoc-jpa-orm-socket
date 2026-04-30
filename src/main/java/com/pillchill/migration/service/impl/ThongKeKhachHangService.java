package com.pillchill.migration.service.impl;

import com.pillchill.migration.dto.HoaDonKemGiaDTO;
import com.pillchill.migration.dto.ThongKeKhachHangDTO;
import com.pillchill.migration.repository.IThongKeKhachHangRepository;
import com.pillchill.migration.repository.impl.ThongKeKhachHangRepository;
import com.pillchill.migration.service.IThongKeKhachHangService;

import java.util.List;

public class ThongKeKhachHangService implements IThongKeKhachHangService {
    private final IThongKeKhachHangRepository thongKeKhachHangRepository;

    public ThongKeKhachHangService() {
        this.thongKeKhachHangRepository = new ThongKeKhachHangRepository();
    }

    @Override
    public List<ThongKeKhachHangDTO> getDoanhThuTatCaKhachHang() {
        return thongKeKhachHangRepository.getDoanhThuTatCaKhachHang();
    }

    @Override
    public List<ThongKeKhachHangDTO> getDoanhThuKhachHangTheoThangNam(int thang, int nam) {
        return thongKeKhachHangRepository.getDoanhThuKhachHangTheoThangNam(thang, nam);
    }

    @Override
    public List<ThongKeKhachHangDTO> timKiemTheoMaKH(String maKH) {
        return thongKeKhachHangRepository.timKiemTheoMaKH(maKH);
    }

    @Override
    public List<ThongKeKhachHangDTO> timKiemTheoTenKH(String tenKH) {
        return thongKeKhachHangRepository.timKiemTheoTenKH(tenKH);
    }

    @Override
    public List<ThongKeKhachHangDTO> timKiemTheoSoDienThoai(String soDienThoai) {
        return thongKeKhachHangRepository.timKiemTheoSoDienThoai(soDienThoai);
    }

    @Override
    public List<HoaDonKemGiaDTO> getHoaDonTheoKhachHang(String maKH) {
        return thongKeKhachHangRepository.getHoaDonTheoKhachHang(maKH);
    }
}
