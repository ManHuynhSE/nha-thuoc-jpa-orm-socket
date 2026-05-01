package com.pillchill.migration.migration;

import java.util.ArrayList;

import com.pillchill.migration.entity.KhuyenMai;
import com.pillchill.migration.service.IKhuyenMaiService;
import com.pillchill.migration.service.impl.KhuyenMaiService;

public class KhuyenMaiJpaDAO {
    private final IKhuyenMaiService khuyenMaiService;

    public KhuyenMaiJpaDAO(IKhuyenMaiService khuyenMaiService) {
        this.khuyenMaiService = khuyenMaiService;
    }

    public KhuyenMaiJpaDAO() {
        this.khuyenMaiService = new KhuyenMaiService();
    }

    public ArrayList<KhuyenMai> getAllKhuyenMai() {
        return new ArrayList<>(khuyenMaiService.getAllKhuyenMai());
    }

    public KhuyenMai getKhuyenMaiById(String maKM) {
        return khuyenMaiService.getKhuyenMaiById(maKM).orElse(null);
    }

    public KhuyenMai createKhuyenMai(KhuyenMai khuyenMai) {
        return khuyenMaiService.createKhuyenMai(khuyenMai);
    }

    public KhuyenMai updateKhuyenMai(KhuyenMai khuyenMai) {
        return khuyenMaiService.updateKhuyenMai(khuyenMai);
    }

    public boolean deactivateKhuyenMai(String maKM) {
        return khuyenMaiService.deactivateKhuyenMai(maKM);
    }

    public long countActive() {
        return khuyenMaiService.countActive();
    }

    public KhuyenMai findByMa(String maKM) {
        return khuyenMaiService.findByMa(maKM);
    }

    public boolean isValid(String maKM) {
        return khuyenMaiService.isValid(maKM);
    }
}
