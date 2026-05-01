package com.pillchill.migration.migration;

import com.pillchill.migration.dto.HoaDonKemGiaDTO;
import com.pillchill.migration.dto.ThongKeKhachHangDTO;
import com.pillchill.migration.service.IThongKeKhachHangService;
import com.pillchill.migration.service.impl.ThongKeKhachHangService;

import java.util.List;

public class ThongKeKhachHangJpaDAO {
    private final IThongKeKhachHangService thongKeKhachHangService;

    public ThongKeKhachHangJpaDAO() {
        this.thongKeKhachHangService = new ThongKeKhachHangService();
    }

    public List<ThongKeKhachHangDTO> getDoanhThuTatCaKhachHang() {
        return thongKeKhachHangService.getDoanhThuTatCaKhachHang();
    }

    public List<ThongKeKhachHangDTO> getDoanhThuKhachHangTheoThangNam(int thang, int nam) {
        return thongKeKhachHangService.getDoanhThuKhachHangTheoThangNam(thang, nam);
    }

    public List<ThongKeKhachHangDTO> timKiemTheoMaKH(String maKH) {
        return thongKeKhachHangService.timKiemTheoMaKH(maKH);
    }

    public List<ThongKeKhachHangDTO> timKiemTheoTenKH(String tenKH) {
        return thongKeKhachHangService.timKiemTheoTenKH(tenKH);
    }

    public List<ThongKeKhachHangDTO> timKiemTheoSoDienThoai(String soDienThoai) {
        return thongKeKhachHangService.timKiemTheoSoDienThoai(soDienThoai);
    }

    public List<HoaDonKemGiaDTO> getHoaDonTheoKhachHang(String maKH) {
        return thongKeKhachHangService.getHoaDonTheoKhachHang(maKH);
    }
}