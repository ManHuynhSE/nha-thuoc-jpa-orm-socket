package com.pillchill.migration.repository;

import com.pillchill.migration.entity.HoaDon;
import com.pillchill.migration.entity.NhanVien;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface INhanVienRepository {
    NhanVien createNhanVien(NhanVien nhanVien);
    NhanVien updateNhanVien(NhanVien nhanVien);
    boolean deleteNhanVien(String maNhanVien);
    NhanVien findById(String maNhanVien);
    List<NhanVien> loadAllNhanVien();
}

