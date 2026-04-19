package com.pillchill.migration.migration;

import java.util.ArrayList;

import com.pillchill.migration.entity.TaiKhoan;
import com.pillchill.migration.service.IAuthService;
import com.pillchill.migration.service.ITaiKhoanService;
import com.pillchill.migration.service.impl.AuthService;
import com.pillchill.migration.service.impl.TaiKhoanService;

public class TaiKhoanJpaDAO {
    private final IAuthService authService;
    private final ITaiKhoanService taiKhoanService;

    public TaiKhoanJpaDAO(IAuthService authService, ITaiKhoanService taiKhoanService) {
        this.authService = authService;
        this.taiKhoanService = taiKhoanService;
    }

    public TaiKhoanJpaDAO() {
        this.authService = new AuthService();
        this.taiKhoanService = new TaiKhoanService();
    }

    public TaiKhoan kiemTraDangNhap(String maNV, String matKhau) {
        return authService.login(maNV, matKhau).orElse(null);
    }

    public TaiKhoan getTaiKhoanById(String maNV) {
        return taiKhoanService.getTaiKhoanById(maNV).orElse(null);
    }

    public ArrayList<TaiKhoan> getAllTaiKhoan() {
        return new ArrayList<>(taiKhoanService.getAllTaiKhoan());
    }
}
