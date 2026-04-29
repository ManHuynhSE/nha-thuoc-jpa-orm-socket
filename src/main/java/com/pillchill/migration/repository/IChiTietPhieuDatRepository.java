package com.pillchill.migration.repository;

import com.pillchill.migration.entity.ChiTietPhieuDat;

import java.util.List;

public interface IChiTietPhieuDatRepository {
    List<ChiTietPhieuDat> findByMaPhieuDatWithThuoc(String maPhieuDat);
}
