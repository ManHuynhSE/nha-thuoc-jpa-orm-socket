package com.pillchill.migration.migration;

import com.pillchill.migration.dto.DoanhThuTheoThangDTO;
import com.pillchill.migration.dto.HoaDonKemGiaDTO;
import com.pillchill.migration.dto.ThongKeNhanVienDTO;
import com.pillchill.migration.service.IThongKeNhanVienService;
import com.pillchill.migration.service.impl.ThongKeNhanVienService;

import java.util.List;

public class ThongKeNhanVienJpaDAO {
    private final IThongKeNhanVienService thongKeNhanVienService;

    public ThongKeNhanVienJpaDAO() {
        this.thongKeNhanVienService = new ThongKeNhanVienService();
    }

    public List<Integer> getNamCoHoaDon() {
        return thongKeNhanVienService.getNamCoHoaDon();
    }

    public List<ThongKeNhanVienDTO> getThongKeDoanhThuNhanVien(int nam) {
        return thongKeNhanVienService.getThongKeDoanhThuNhanVien(nam);
    }

    public List<DoanhThuTheoThangDTO> getThongKeDoanhThuNhanVienTheoThang(String maNV, int nam) {
        return thongKeNhanVienService.getThongKeDoanhThuNhanVienTheoThang(maNV, nam);
    }

    public List<HoaDonKemGiaDTO> getHoaDonTrongNamCuaNhanVien(int nam, String maNV) {
        return thongKeNhanVienService.getHoaDonTrongNamCuaNhanVien(nam, maNV);
    }
}