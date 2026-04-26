package com.pillchill.migration.repository;

import com.pillchill.migration.entity.NhanVien;

import java.util.List;
import java.util.Optional;

public interface INhanVienRepository extends GenericRepository<NhanVien, String> {
    List<NhanVien> findAllActive();

    Optional<NhanVien> findById(String maNV);

    long countActive();

    boolean deactivateNhanVien(String maNV);
}
