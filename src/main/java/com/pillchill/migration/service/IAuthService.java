package com.pillchill.migration.service;

import com.pillchill.migration.entity.TaiKhoan;

import java.util.Optional;

public interface IAuthService {
    Optional<TaiKhoan> login(String maNV, String matKhau);
}
