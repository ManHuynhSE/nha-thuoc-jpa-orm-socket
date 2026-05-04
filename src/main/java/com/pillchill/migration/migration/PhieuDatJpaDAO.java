package com.pillchill.migration.migration;

import com.pillchill.migration.dto.ChiTietPhieuDatView;
import com.pillchill.migration.dto.CreatePhieuDatCommand;
import com.pillchill.migration.dto.PhieuDatView;
import com.pillchill.migration.service.IPhieuDatService;
import com.pillchill.migration.service.impl.PhieuDatService;

import java.util.ArrayList;

public class PhieuDatJpaDAO {
    private final IPhieuDatService phieuDatService;

    public PhieuDatJpaDAO(IPhieuDatService phieuDatService) {
        this.phieuDatService = phieuDatService;
    }

    public PhieuDatJpaDAO() {
        this.phieuDatService = new PhieuDatService();
    }

    public ArrayList<PhieuDatView> getAllPhieuDatViews() {
        return new ArrayList<>(phieuDatService.getAllPhieuDatViews());
    }

    public ArrayList<PhieuDatView> getPhieuDatViewsByMonthYear(int month, int year) {
        return new ArrayList<>(phieuDatService.getPhieuDatViewsByMonthYear(month, year));
    }

    public ArrayList<ChiTietPhieuDatView> getChiTietPhieuDatByMaPhieuDat(String maPhieuDat) {
        return new ArrayList<>(phieuDatService.getChiTietPhieuDatByMaPhieuDat(maPhieuDat));
    }

    public String createPhieuDat(CreatePhieuDatCommand command, String maNhanVien) {
        return phieuDatService.createPhieuDat(command, maNhanVien);
    }
}
