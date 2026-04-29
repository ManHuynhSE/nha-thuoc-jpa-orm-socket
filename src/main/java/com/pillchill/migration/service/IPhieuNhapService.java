package com.pillchill.migration.service;

import com.pillchill.migration.dto.ChiTietPhieuNhapView;
import com.pillchill.migration.dto.PhieuNhapImportItem;
import com.pillchill.migration.dto.PhieuNhapView;

import java.util.List;

public interface IPhieuNhapService {
    List<PhieuNhapView> getAllPhieuNhapViews();

    List<ChiTietPhieuNhapView> getChiTietPhieuNhapByMaPhieuNhap(String maPhieuNhapThuoc);

    String importFromExcel(List<PhieuNhapImportItem> items, String maNhanVien);
}
