package com.pillchill.migration.service.impl;

import java.util.Optional;

import com.pillchill.migration.entity.TaiKhoan;
import com.pillchill.migration.repository.ITaiKhoanRepository;
import com.pillchill.migration.repository.impl.TaiKhoanRepository;
import com.pillchill.migration.service.IAuthService;

public class AuthService implements IAuthService {
    private final ITaiKhoanRepository taiKhoanRepository;

    public AuthService(ITaiKhoanRepository taiKhoanRepository) {
        this.taiKhoanRepository = taiKhoanRepository;
    }

    public AuthService() {
        this.taiKhoanRepository = new TaiKhoanRepository();
    }

    @Override
    public Optional<TaiKhoan> login(String maNV, String matKhau) {
        return taiKhoanRepository.login(maNV, matKhau);
    }
}
