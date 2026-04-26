package com.pillchill.migration.service;

import com.pillchill.migration.entity.NhanVien;

import java.util.List;
import java.util.Optional;

public interface INhanVienService {
    List<NhanVien> getAllNhanVien();

    Optional<NhanVien> getNhanVienById(String maNV);

    NhanVien createNhanVien(NhanVien nhanVien);

    NhanVien updateNhanVien(NhanVien nhanVien);

    boolean deactivateNhanVien(String maNV);

    long countActive();
}
