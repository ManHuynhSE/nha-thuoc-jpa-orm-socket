package com.pillchill.migration.migration;

import java.util.ArrayList;

import com.pillchill.migration.entity.KhachHang;
import com.pillchill.migration.service.IKhachHangService;
import com.pillchill.migration.service.impl.KhachHangService;

public class KhachHangJpaDAO {
    private final IKhachHangService khachHangService;

    public KhachHangJpaDAO(IKhachHangService khachHangService) {
        this.khachHangService = khachHangService;
    }

    public KhachHangJpaDAO() {
        this.khachHangService = new KhachHangService();
    }

    public ArrayList<KhachHang> getAllKhachHang() {
        return new ArrayList<>(khachHangService.getAllKhachHang());
    }

    public KhachHang getKhachHangById(String maKH) {
        return khachHangService.getKhachHangById(maKH).orElse(null);
    }

    public KhachHang createKhachHang(KhachHang khachHang) {
        return khachHangService.createKhachHang(khachHang);
    }

    public KhachHang updateKhachHang(KhachHang khachHang) {
        return khachHangService.updateKhachHang(khachHang);
    }

    public boolean deactivateKhachHang(String maKH) {
        return khachHangService.deactivateKhachHang(maKH);
    }

    public long countActive() {
        return khachHangService.countActive();
    }
}

