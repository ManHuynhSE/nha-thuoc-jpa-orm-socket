package com.pillchill.migration.migration;


import java.util.ArrayList;

import com.pillchill.migration.dto.NhanVienDTO;
import com.pillchill.migration.entity.NhanVien;
import com.pillchill.migration.entity.TaiKhoan;
import com.pillchill.migration.repository.INhanVienRepository;
import com.pillchill.migration.repository.impl.NhanVienRepository;
import com.pillchill.migration.service.IAuthService;
import com.pillchill.migration.service.INhanVienService;
import com.pillchill.migration.service.ITaiKhoanService;
import com.pillchill.migration.service.impl.AuthService;
import com.pillchill.migration.service.impl.NhanVienService;
import com.pillchill.migration.service.impl.TaiKhoanService;

public class NhanVienJpaDAO {
    private final INhanVienService nhanVienService;

    public NhanVienJpaDAO() {
        this.nhanVienService = new NhanVienService();
    }

//    public NhanVienJpaDAO() {
//        this.authService = new AuthService();
//        this.nhanVienRepository = new NhanVienRepository();
//    }

//    public TaiKhoan kiemTraDangNhap(String maNV, String matKhau) {
//        return authService.login(maNV, matKhau).orElse(null);
//    }
//
//    public TaiKhoan getTaiKhoanById(String maNV) {
//        return taiKhoanService.getTaiKhoanById(maNV).orElse(null);
//    }

    public ArrayList<NhanVienDTO> getAllNhanVien() {
        return new ArrayList<>(nhanVienService.loadALlNhanVien());
    }

    public NhanVienDTO addNhanVien(NhanVienDTO nhanVienDTO) {
        return nhanVienService.addNhanVien(nhanVienDTO);
    }

    public NhanVienDTO updateNhanVien(NhanVienDTO nhanVienDTO) {
        return nhanVienService.updateNhanVien(nhanVienDTO);
    }

    public boolean deleteNhanVien(String maNhanVien) {
        return nhanVienService.deleteNhanVien(maNhanVien);
    }
}
