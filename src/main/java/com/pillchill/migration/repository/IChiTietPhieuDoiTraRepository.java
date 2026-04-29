package com.pillchill.migration.repository;

import com.pillchill.migration.entity.ChiTietPhieuDoiTra;

import java.util.List;

public interface IChiTietPhieuDoiTraRepository {
    List<ChiTietPhieuDoiTra> findByMaPhieuDoiTraWithThuoc(String maPhieuDoiTra);
}
