package com.pillchill.migration.service.impl;

import com.pillchill.migration.dto.DoanhThuTheoThangDTO;
import com.pillchill.migration.dto.HoaDonKemGiaDTO;
import com.pillchill.migration.dto.ThongKeNhanVienDTO;
import com.pillchill.migration.repository.IThongKeNhanVienRepository;
import com.pillchill.migration.repository.impl.ThongKeNhanVienRepository;
import com.pillchill.migration.service.IThongKeNhanVienService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ThongKeNhanVienService implements IThongKeNhanVienService {
    private final IThongKeNhanVienRepository thongKeNhanVienRepository;

    public ThongKeNhanVienService() {
        this.thongKeNhanVienRepository = new ThongKeNhanVienRepository();
    }

    @Override
    public List<Integer> getNamCoHoaDon() {
        return thongKeNhanVienRepository.getNamCoHoaDon();
    }

    @Override
    public List<ThongKeNhanVienDTO> getThongKeDoanhThuNhanVien(int nam) {
        System.out.println("[DEBUG] ThongKeNhanVienService.getThongKeDoanhThuNhanVien - nam=" + nam);
        List<ThongKeNhanVienDTO> result = new ArrayList<>(thongKeNhanVienRepository.getThongKeDoanhThuNhanVien(nam));
        System.out.println("[DEBUG] After repository call, result size=" + result.size());
        
        double tongDoanhThu = result.stream().mapToDouble(ThongKeNhanVienDTO::getDoanhThu).sum();
        System.out.println("[DEBUG] tongDoanhThu=" + tongDoanhThu);
        
        for (ThongKeNhanVienDTO item : result) {
            double tyLeDongGop = tongDoanhThu > 0 ? (item.getDoanhThu() * 100d / tongDoanhThu) : 0d;
            item.setTyLeDongGop(tyLeDongGop);
        }
        result.sort(Comparator.comparingDouble(ThongKeNhanVienDTO::getDoanhThu).reversed());
        System.out.println("[DEBUG] Final result size=" + result.size());
        return result;
    }

    @Override
    public List<DoanhThuTheoThangDTO> getThongKeDoanhThuNhanVienTheoThang(String maNV, int nam) {
        return thongKeNhanVienRepository.getThongKeDoanhThuNhanVienTheoThang(maNV, nam);
    }

    @Override
    public List<HoaDonKemGiaDTO> getHoaDonTrongNamCuaNhanVien(int nam, String maNV) {
        return thongKeNhanVienRepository.getHoaDonTrongNamCuaNhanVien(nam, maNV);
    }
}
