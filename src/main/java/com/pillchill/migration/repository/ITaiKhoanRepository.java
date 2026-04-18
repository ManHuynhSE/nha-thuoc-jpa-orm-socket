package com.pillchill.migration.repository;

import com.pillchill.migration.entity.TaiKhoan;

import java.util.List;
import java.util.Optional;

public interface ITaiKhoanRepository {
    Optional<TaiKhoan> login(String maNV, String matKhau);

    List<TaiKhoan> findAllActive();

    Optional<TaiKhoan> findById(String maNV);
}
