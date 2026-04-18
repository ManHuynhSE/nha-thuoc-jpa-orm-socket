package com.pillchill.migration.repository;

import com.pillchill.migration.entity.ChiTietLoThuoc;
import com.pillchill.migration.entity.id.ChiTietLoThuocId;

import java.util.List;

public interface IChiTietLoThuocRepository {
    List<ChiTietLoThuoc> findActiveByMaThuocOrderByNgaySanXuat(String maThuoc);

    ChiTietLoThuoc getReference(ChiTietLoThuocId id);

    void save(ChiTietLoThuoc chiTietLoThuoc);

    int sumSoLuongByMaThuoc(String maThuoc);
}
