package com.pillchill.migration.service.impl;

import com.pillchill.migration.entity.BangGia;
import com.pillchill.migration.repository.IBangGiaRepository;
import com.pillchill.migration.repository.impl.BangGiaRepository;
import com.pillchill.migration.service.IBangGiaService;

import java.util.List;
import java.util.Optional;

public class BangGiaService implements IBangGiaService {
    private final IBangGiaRepository bangGiaRepository;

    public BangGiaService(IBangGiaRepository bangGiaRepository) {
        this.bangGiaRepository = bangGiaRepository;
    }

    @Override
    public Optional<BangGia> getBangGiaById(String maBangGia) {
        return bangGiaRepository.findById(maBangGia);
    }

    @Override
    public List<BangGia> getAllBangGiaActive() {
        return bangGiaRepository.findAllActive();
    }

    @Override
    public BangGia createBangGia(BangGia bangGia) {
        if (bangGia != null && !bangGia.isActive()) {
            bangGia.setActive(true);
        }
        return bangGiaRepository.create(bangGia);
    }

    @Override
    public BangGia updateBangGia(BangGia bangGia) {
        return bangGiaRepository.update(bangGia);
    }

    @Override
    public boolean deactivateBangGia(String maBangGia) {
        return bangGiaRepository.deactivateBangGia(maBangGia);
    }
}
