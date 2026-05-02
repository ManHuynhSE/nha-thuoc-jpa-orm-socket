package com.pillchill.migration.service;

import com.pillchill.migration.dto.LoThuocHetHan;

import java.util.List;

public interface IThongKeHSDService {
    List<LoThuocHetHan> getCacLoThuocHetHan();

    List<LoThuocHetHan> getCacLoThuocSapHetHan(int soNgay);

    boolean xoaChiTietLoThuoc(String maLo, String maThuoc);
}
