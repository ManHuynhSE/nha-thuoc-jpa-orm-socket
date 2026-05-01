package com.pillchill.migration.service.impl;

import com.pillchill.migration.entity.NhaSanXuat;
import com.pillchill.migration.repository.INhaSanXuatRepository;
import com.pillchill.migration.repository.impl.NhaSanXuatRepository;
import com.pillchill.migration.service.INhaSanXuatService;

import java.util.List;
import java.util.Optional;

public class NhaSanXuatService implements INhaSanXuatService {
    private final INhaSanXuatRepository nhaSanXuatRepository;

    public NhaSanXuatService() {
        this.nhaSanXuatRepository = new NhaSanXuatRepository();
    }

    @Override
    public List<NhaSanXuat> getAllNhaSanXuat() {
        return nhaSanXuatRepository.findAllActive();
    }

    @Override
    public boolean addNhaSanXuat(NhaSanXuat nhaSanXuat) {
        return nhaSanXuatRepository.create(nhaSanXuat) != null;
    }

    @Override
    public boolean updateNhaSanXuat(NhaSanXuat nhaSanXuat) {
        return nhaSanXuatRepository.update(nhaSanXuat) != null;
    }

    @Override
    public boolean deleteNhaSanXuat(String maNSX) {
        NhaSanXuat nsx = nhaSanXuatRepository.findByID(maNSX);
        if (nsx != null) {
            nsx.setActive(false);
            return nhaSanXuatRepository.update(nsx) != null;
        }
        return false;
    }
}
