package com.pillchill.migration.migration;

import java.util.ArrayList;

import com.pillchill.migration.dto.ThuocKemGiaView;
import com.pillchill.migration.dto.ThuocTheoLoView;
import com.pillchill.migration.entity.Thuoc;
import com.pillchill.migration.service.IThuocService;
import com.pillchill.migration.service.impl.ThuocService;

public class ThuocJpaDAO {
    private final IThuocService thuocService;

    public ThuocJpaDAO(IThuocService thuocService) {
        this.thuocService = thuocService;
    }

    public ThuocJpaDAO() {
        this.thuocService = new ThuocService();
    }

    public ArrayList<Thuoc> getAllThuoc() {
        return new ArrayList<>(thuocService.getAllThuoc());
    }

    public Thuoc getThuocById(String id) {
        return thuocService.getThuocById(id).orElse(null);
    }

    public ArrayList<ThuocKemGiaView> getAllThuocKemGia() {
        return new ArrayList<>(thuocService.getAllThuocKemGia());
    }

    public ArrayList<ThuocTheoLoView> getAllThuocTheoLo() {
        return new ArrayList<>(thuocService.getAllThuocTheoLo());
    }

    public Thuoc createThuoc(Thuoc thuoc) {
        return thuocService.createThuoc(thuoc);
    }

    public Thuoc createThuoc(Thuoc thuoc, double giaBanCoSo) {
        return thuocService.createThuoc(thuoc, giaBanCoSo);
    }

    public Thuoc updateThuoc(Thuoc thuoc) {
        return thuocService.updateThuoc(thuoc);
    }

    public boolean deactivateThuoc(String maThuoc) {
        return thuocService.deactivateThuoc(maThuoc);
    }

    public static void main(String[] args) {
        ThuocJpaDAO thuocJpaDAO = new ThuocJpaDAO();
    }
}
