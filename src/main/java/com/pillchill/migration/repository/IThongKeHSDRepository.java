package com.pillchill.migration.repository;

import com.pillchill.migration.dto.LoThuocHetHan;

import java.util.List;

public interface IThongKeHSDRepository {
    List<LoThuocHetHan> getCacLoThuocHetHan();

    List<LoThuocHetHan> getCacLoThuocSapHetHan(int soNgay);

    boolean xoaChiTietLoThuoc(String maLo, String maThuoc);
}
