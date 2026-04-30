package com.pillchill.migration.service.impl;

import com.pillchill.migration.dto.DoanhThuHoaDonDTO;
import com.pillchill.migration.dto.HoaDonKemGiaDTO;
import com.pillchill.migration.repository.IDoanhThuRepository;
import com.pillchill.migration.repository.impl.DoanhThuRepository;
import com.pillchill.migration.service.IDoanhThuService;

import java.util.List;

public class DoanhThuService implements IDoanhThuService {
    private final IDoanhThuRepository doanhThuRepository;

    public DoanhThuService(IDoanhThuRepository doanhThuRepository) {
        this.doanhThuRepository = doanhThuRepository;
    }

    public DoanhThuService() {
        this.doanhThuRepository = new DoanhThuRepository();
    }

    @Override
    public double getDoanhThuCuaThang(int thangDuocChon, int namDuocChon) {
        return doanhThuRepository.getDoanhThuCuaThang(thangDuocChon, namDuocChon);
    }

    @Override
    public int getSoHoaDonTheoThang(int thangDuocChon, int namDuocChon) {
        return doanhThuRepository.getSoHoaDonTheoThang(thangDuocChon, namDuocChon);
    }

    @Override
    public int getSoKhachHangCuaThang(int thangDuocChon, int namDuocChon) {
        return doanhThuRepository.getSoKhachHangCuaThang(thangDuocChon, namDuocChon);
    }

    @Override
    public double getDoanhThuTrungBinhTheoNgay(int thangDuocChon, int namDuocChon) {
        return doanhThuRepository.getDoanhThuTrungBinhTheoNgay(thangDuocChon, namDuocChon);
    }

    @Override
    public List<HoaDonKemGiaDTO> getHoaDonTrongThang(int thang, int nam) {
        return doanhThuRepository.getHoaDonTrongThang(thang, nam);
    }

    @Override
    public List<HoaDonKemGiaDTO> getHoaDonTrongNam(int nam) {
        return doanhThuRepository.getHoaDonTrongNam(nam);
    }

    @Override
    public List<Integer> getNamCoHoaDon() {
        return doanhThuRepository.getNamCoHoaDon();
    }

    @Override
    public List<Integer> getThangCoHoaDonTrongNam(int nam) {
        return doanhThuRepository.getThangCoHoaDonTrongNam(nam);
    }

    @Override
    public List<DoanhThuHoaDonDTO> getDoanhThuTheoNgay(int thang, int nam) {
        return doanhThuRepository.getDoanhThuTheoNgay(thang, nam);
    }
}
