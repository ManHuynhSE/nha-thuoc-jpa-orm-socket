package com.pillchill.migration.migration;

import com.pillchill.migration.dto.ChiTietPhieuDoiTraView;
import com.pillchill.migration.dto.PhieuDoiTraView;
import com.pillchill.migration.service.IPhieuDoiTraService;
import com.pillchill.migration.service.impl.PhieuDoiTraService;

import java.util.ArrayList;

public class PhieuDoiTraJpaDAO {
    private final IPhieuDoiTraService phieuDoiTraService;

    public PhieuDoiTraJpaDAO(IPhieuDoiTraService phieuDoiTraService) {
        this.phieuDoiTraService = phieuDoiTraService;
    }

    public PhieuDoiTraJpaDAO() {
        this.phieuDoiTraService = new PhieuDoiTraService();
    }

    public ArrayList<PhieuDoiTraView> getAllPhieuDoiTraViews() {
        return new ArrayList<>(phieuDoiTraService.getAllPhieuDoiTraViews());
    }

    public ArrayList<PhieuDoiTraView> getPhieuDoiTraViewsByMonthYear(int month, int year) {
        return new ArrayList<>(phieuDoiTraService.getPhieuDoiTraViewsByMonthYear(month, year));
    }

    public ArrayList<ChiTietPhieuDoiTraView> getChiTietPhieuDoiTraByMaPhieuDoiTra(String maPhieuDoiTra) {
        return new ArrayList<>(phieuDoiTraService.getChiTietPhieuDoiTraByMaPhieuDoiTra(maPhieuDoiTra));
    }
}
