package com.pillchill.migration.repository;

import com.pillchill.migration.dto.ChiTietPhieuNhapView;

import java.util.List;

public interface IChiTietPhieuNhapRepository {
    List<ChiTietPhieuNhapView> findViewByMaPhieuNhap(String maPhieuNhapThuoc);
}
