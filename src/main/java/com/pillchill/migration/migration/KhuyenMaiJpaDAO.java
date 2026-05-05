package com.pillchill.migration.migration;

import com.pillchill.migration.entity.KhuyenMai;
import com.pillchill.migration.service.IKhuyenMaiService;
import com.pillchill.migration.service.impl.KhuyenMaiService;

import java.util.ArrayList;
import java.util.List;

public class KhuyenMaiJpaDAO {
    private final IKhuyenMaiService khuyenMaiService;

    public KhuyenMaiJpaDAO() {
        this.khuyenMaiService = new KhuyenMaiService();
    }

    public ArrayList<KhuyenMai> getAllKhuyenMai() {
        return new ArrayList<>(khuyenMaiService.getAllKhuyenMai());
    }

    public ArrayList<KhuyenMai> getAllKhuyenMaiInactive() {
        return new ArrayList<>(khuyenMaiService.getAllKhuyenMaiInactive());
    }

    public void addKhuyenMai(KhuyenMai khuyenMai) {
        khuyenMaiService.createKhuyenMai(khuyenMai);
    }

    public void updateKhuyenMai(KhuyenMai khuyenMai) {
        khuyenMaiService.updateKhuyenMai(khuyenMai);
    }

    public void deleteKhuyenMai(String maKhuyenMai) {
        boolean deactivated = khuyenMaiService.deactivateKhuyenMai(maKhuyenMai);
        if (!deactivated) {
            throw new IllegalArgumentException("Không tìm thấy khuyến mãi");
        }
    }

    public boolean reactivateKhuyenMai(String maKhuyenMai) {
        return khuyenMaiService.reactivateKhuyenMai(maKhuyenMai);
    }

    public KhuyenMai getKhuyenMaiById(String maKhuyenMai) {
        return khuyenMaiService.getKhuyenMaiById(maKhuyenMai);
    }


    public static void main(String[] args) {
        KhuyenMaiJpaDAO khuyenMaiJpaDAO = new KhuyenMaiJpaDAO();
        System.out.println(khuyenMaiJpaDAO.getAllKhuyenMai());
    }
}
