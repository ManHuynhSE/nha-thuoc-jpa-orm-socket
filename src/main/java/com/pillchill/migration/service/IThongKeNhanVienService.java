package com.pillchill.migration.service;

import com.pillchill.migration.dto.DoanhThuTheoThangDTO;
import com.pillchill.migration.dto.HoaDonKemGiaDTO;
import com.pillchill.migration.dto.ThongKeNhanVienDTO;

import java.util.List;

public interface IThongKeNhanVienService {
    List<Integer> getNamCoHoaDon();

    List<ThongKeNhanVienDTO> getThongKeDoanhThuNhanVien(int nam);

    List<DoanhThuTheoThangDTO> getThongKeDoanhThuNhanVienTheoThang(String maNV, int nam);

    List<HoaDonKemGiaDTO> getHoaDonTrongNamCuaNhanVien(int nam, String maNV);
}
