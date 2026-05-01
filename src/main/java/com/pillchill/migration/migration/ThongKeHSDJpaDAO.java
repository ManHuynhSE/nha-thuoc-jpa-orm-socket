package com.pillchill.migration.migration;

import com.pillchill.migration.dto.LoThuocHetHan;
import com.pillchill.migration.service.IThongKeHSDService;
import com.pillchill.migration.service.impl.ThongKeHSDService;

import java.util.List;

public class ThongKeHSDJpaDAO {
    private final IThongKeHSDService thongKeHSDService;

    public ThongKeHSDJpaDAO() {
        this.thongKeHSDService = new ThongKeHSDService();
    }

    public List<LoThuocHetHan> getCacLoThuocHetHan() {
        return thongKeHSDService.getCacLoThuocHetHan();
    }

    public List<LoThuocHetHan> getCacLoThuocSapHetHan(int soNgay) {
        return thongKeHSDService.getCacLoThuocSapHetHan(soNgay);
    }

    public boolean xoaChiTietLoThuoc(String maLo, String maThuoc) {
        return thongKeHSDService.xoaChiTietLoThuoc(maLo, maThuoc);
    }
}