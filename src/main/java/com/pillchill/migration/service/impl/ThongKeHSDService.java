package com.pillchill.migration.service.impl;

import com.pillchill.migration.dto.LoThuocHetHan;
import com.pillchill.migration.repository.IThongKeHSDRepository;
import com.pillchill.migration.repository.impl.ThongKeHSDRepository;
import com.pillchill.migration.service.IThongKeHSDService;

import java.util.List;

public class ThongKeHSDService implements IThongKeHSDService {
    private final IThongKeHSDRepository thongKeHSDRepository;

    public ThongKeHSDService() {
        this.thongKeHSDRepository = new ThongKeHSDRepository();
    }

    @Override
    public List<LoThuocHetHan> getCacLoThuocHetHan() {
        return thongKeHSDRepository.getCacLoThuocHetHan();
    }

    @Override
    public List<LoThuocHetHan> getCacLoThuocSapHetHan(int soNgay) {
        return thongKeHSDRepository.getCacLoThuocSapHetHan(soNgay);
    }

    @Override
    public boolean xoaChiTietLoThuoc(String maLo, String maThuoc) {
        return thongKeHSDRepository.xoaChiTietLoThuoc(maLo, maThuoc);
    }
}
