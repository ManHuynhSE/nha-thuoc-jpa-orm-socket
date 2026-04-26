package com.pillchill.migration.migration;

import java.util.ArrayList;

import com.pillchill.migration.entity.NhanVien;
import com.pillchill.migration.service.INhanVienService;
import com.pillchill.migration.service.impl.NhanVienService;

public class NhanVienJpaDAO {
    private final INhanVienService nhanVienService;

    public NhanVienJpaDAO(INhanVienService nhanVienService) {
        this.nhanVienService = nhanVienService;
    }

    public NhanVienJpaDAO() {
        this.nhanVienService = new NhanVienService();
    }

    public ArrayList<NhanVien> getAllNhanVien() {
        return new ArrayList<>(nhanVienService.getAllNhanVien());
    }

    public NhanVien getNhanVienById(String maNV) {
        return nhanVienService.getNhanVienById(maNV).orElse(null);
    }

//    public NhanVien createNhanVien(NhanVien nhanVien) {
//        return nhanVienService.createNhanVien(nhanVien);
//    }

    public NhanVien updateNhanVien(NhanVien nhanVien) {
        return nhanVienService.updateNhanVien(nhanVien);
    }

    public boolean deactivateNhanVien(String maNV) {
        return nhanVienService.deactivateNhanVien(maNV);
    }

    public long countActive() {
        return nhanVienService.countActive();
    }
}

