package com.pillchill.migration.migration;

import com.pillchill.migration.dto.DoanhThuHoaDonDTO;
import com.pillchill.migration.dto.HoaDonKemGiaDTO;
import com.pillchill.migration.service.IDoanhThuService;
import com.pillchill.migration.service.impl.DoanhThuService;

import java.util.List;

public class DoanhThuJpaDAO {
    private final IDoanhThuService doanhThuService;

    public DoanhThuJpaDAO() {
        this.doanhThuService = new DoanhThuService();
    }

    public double getDoanhThuCuaThang(int thangDuocChon, int namDuocChon) {
        return doanhThuService.getDoanhThuCuaThang(thangDuocChon, namDuocChon);
    }

    public int getSoHoaDonTheoThang(int thangDuocChon, int namDuocChon) {
        return doanhThuService.getSoHoaDonTheoThang(thangDuocChon, namDuocChon);
    }

    public int getSoKhachHangCuaThang(int thangDuocChon, int namDuocChon) {
        return doanhThuService.getSoKhachHangCuaThang(thangDuocChon, namDuocChon);
    }

    public double getDoanhThuTrungBinhTheoNgay(int thangDuocChon, int namDuocChon) {
        return doanhThuService.getDoanhThuTrungBinhTheoNgay(thangDuocChon, namDuocChon);
    }

    public List<HoaDonKemGiaDTO> getHoaDonTrongThang(int thang, int nam) {
        return doanhThuService.getHoaDonTrongThang(thang, nam);
    }

    public List<HoaDonKemGiaDTO> getHoaDonTrongNam(int nam) {
        return doanhThuService.getHoaDonTrongNam(nam);
    }

    public List<Integer> getNamCoHoaDon() {
        return doanhThuService.getNamCoHoaDon();
    }

    public List<Integer> getThangCoHoaDonTrongNam(int nam) {
        return doanhThuService.getThangCoHoaDonTrongNam(nam);
    }

    public List<DoanhThuHoaDonDTO> getDoanhThuTheoNgay(int thang, int nam) {
        return doanhThuService.getDoanhThuTheoNgay(thang, nam);
    }
}