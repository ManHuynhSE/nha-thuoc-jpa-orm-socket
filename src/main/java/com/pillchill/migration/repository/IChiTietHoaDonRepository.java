package com.pillchill.migration.repository;

import com.pillchill.migration.entity.ChiTietHoaDon;
import com.pillchill.migration.entity.id.ChiTietHoaDonId;

import java.util.List;

public interface IChiTietHoaDonRepository {
    List<ChiTietHoaDon> findByMaHoaDon(String maHoaDon);

    void save(ChiTietHoaDon chiTietHoaDon);

    void softDelete(ChiTietHoaDonId id);
}
