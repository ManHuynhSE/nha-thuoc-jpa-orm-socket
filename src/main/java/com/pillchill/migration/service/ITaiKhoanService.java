package com.pillchill.migration.service;

import java.util.List;
import java.util.Optional;

import com.pillchill.migration.entity.TaiKhoan;

public interface ITaiKhoanService {
    Optional<TaiKhoan> getTaiKhoanById(String maNV);

    List<TaiKhoan> getAllTaiKhoan();
}
