package com.pillchill.migration.repository;

import java.util.Optional;

public interface IGiaThuocRepository {
    Optional<Double> getGiaHienTaiByMaThuoc(String maThuoc);

    void upsertGiaCoSo(String maThuoc, String maDonVi, double donGia);
}
