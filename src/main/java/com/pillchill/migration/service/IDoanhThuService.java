package com.pillchill.migration.service;

import com.pillchill.migration.dto.DoanhThuHoaDonDTO;
import com.pillchill.migration.dto.HoaDonKemGiaDTO;

import java.util.List;

public interface IDoanhThuService {
    double getDoanhThuCuaThang(int thangDuocChon, int namDuocChon);

    int getSoHoaDonTheoThang(int thangDuocChon, int namDuocChon);

    int getSoKhachHangCuaThang(int thangDuocChon, int namDuocChon);

    double getDoanhThuTrungBinhTheoNgay(int thangDuocChon, int namDuocChon);

    List<HoaDonKemGiaDTO> getHoaDonTrongThang(int thang, int nam);

    List<HoaDonKemGiaDTO> getHoaDonTrongNam(int nam);

    List<Integer> getNamCoHoaDon();

    List<Integer> getThangCoHoaDonTrongNam(int nam);

    List<DoanhThuHoaDonDTO> getDoanhThuTheoNgay(int thang, int nam);
}
