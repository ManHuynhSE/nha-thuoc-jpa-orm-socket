package com.pillchill.migration.repository;

import com.pillchill.migration.dto.HoaDonKemGiaDTO;
import com.pillchill.migration.dto.ThongKeKhachHangDTO;

import java.util.List;

public interface IThongKeKhachHangRepository {
    List<ThongKeKhachHangDTO> getDoanhThuTatCaKhachHang();

    List<ThongKeKhachHangDTO> getDoanhThuKhachHangTheoThangNam(int thang, int nam);

    List<ThongKeKhachHangDTO> timKiemTheoMaKH(String maKH);

    List<ThongKeKhachHangDTO> timKiemTheoTenKH(String tenKH);

    List<ThongKeKhachHangDTO> timKiemTheoSoDienThoai(String soDienThoai);

    List<HoaDonKemGiaDTO> getHoaDonTheoKhachHang(String maKH);
}
