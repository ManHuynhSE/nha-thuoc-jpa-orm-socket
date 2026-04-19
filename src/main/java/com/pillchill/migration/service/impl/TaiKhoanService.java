package com.pillchill.migration.service.impl;

import java.util.List;
import java.util.Optional;

import com.pillchill.migration.entity.TaiKhoan;
import com.pillchill.migration.repository.ITaiKhoanRepository;
import com.pillchill.migration.repository.impl.TaiKhoanRepository;
import com.pillchill.migration.service.ITaiKhoanService;

public class TaiKhoanService implements ITaiKhoanService {
    private final ITaiKhoanRepository taiKhoanRepository;

    public TaiKhoanService(ITaiKhoanRepository taiKhoanRepository) {
        this.taiKhoanRepository = taiKhoanRepository;
    }

    public TaiKhoanService() {
        this.taiKhoanRepository = new TaiKhoanRepository();
    }

    @Override
    public Optional<TaiKhoan> getTaiKhoanById(String maNV) {
        return taiKhoanRepository.findById(maNV);
    }

    @Override
    public List<TaiKhoan> getAllTaiKhoan() {
        return taiKhoanRepository.findAllActive();
    }
}
