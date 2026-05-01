package com.pillchill.migration.migration;

import com.pillchill.migration.entity.NhaSanXuat;
import com.pillchill.migration.service.INhaSanXuatService;
import com.pillchill.migration.service.impl.NhaSanXuatService;

import java.util.ArrayList;

public class NhaSanXuatJpaDAO {
    private final INhaSanXuatService nhaSanXuatService;

    public NhaSanXuatJpaDAO() {
        this.nhaSanXuatService = new NhaSanXuatService();
    }

    public ArrayList<NhaSanXuat> getAllNhaSanXuat() {
        return new ArrayList<>(nhaSanXuatService.getAllNhaSanXuat());
    }

    public boolean addNhaSanXuat(NhaSanXuat nhaSanXuat) {
        return nhaSanXuatService.addNhaSanXuat(nhaSanXuat);
    }

    public boolean updateNhaSanXuat(NhaSanXuat nhaSanXuat) {
        return nhaSanXuatService.updateNhaSanXuat(nhaSanXuat);
    }

    public boolean deleteNhaSanXuat(String maNSX) {
        return nhaSanXuatService.deleteNhaSanXuat(maNSX);
    }
}
