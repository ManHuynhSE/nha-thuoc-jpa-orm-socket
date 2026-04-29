package com.pillchill.migration.migration;

import com.pillchill.migration.dto.ChiTietPhieuNhapView;
import com.pillchill.migration.dto.PhieuNhapImportItem;
import com.pillchill.migration.dto.PhieuNhapView;
import com.pillchill.migration.service.IPhieuNhapService;
import com.pillchill.migration.service.impl.PhieuNhapService;

import java.util.ArrayList;
import java.util.List;

public class PhieuNhapJpaDAO {
    private final IPhieuNhapService phieuNhapService;

    public PhieuNhapJpaDAO(IPhieuNhapService phieuNhapService) {
        this.phieuNhapService = phieuNhapService;
    }

    public PhieuNhapJpaDAO() {
        this.phieuNhapService = new PhieuNhapService();
    }

    public ArrayList<PhieuNhapView> getAllPhieuNhapViews() {
        return new ArrayList<>(phieuNhapService.getAllPhieuNhapViews());
    }

    public ArrayList<ChiTietPhieuNhapView> getChiTietPhieuNhapByMaPhieuNhap(String maPhieuNhapThuoc) {
        return new ArrayList<>(phieuNhapService.getChiTietPhieuNhapByMaPhieuNhap(maPhieuNhapThuoc));
    }

    public String importFromExcel(List<PhieuNhapImportItem> items, String maNhanVien) {
        return phieuNhapService.importFromExcel(items, maNhanVien);
    }
}
