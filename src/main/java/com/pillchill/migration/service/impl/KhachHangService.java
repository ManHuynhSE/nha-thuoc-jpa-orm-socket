package com.pillchill.migration.service.impl;

import com.pillchill.migration.entity.KhachHang;
import com.pillchill.migration.repository.IKhachHangRepository;
import com.pillchill.migration.repository.impl.KhachHangRepository;
import com.pillchill.migration.service.IKhachHangService;

import java.util.List;
import java.util.Optional;

public class KhachHangService implements IKhachHangService {
    private final IKhachHangRepository khachHangRepository;

    public KhachHangService(IKhachHangRepository khachHangRepository) {
        this.khachHangRepository = khachHangRepository;
    }

    public KhachHangService() {
        this(new KhachHangRepository());
    }

    @Override
    public List<KhachHang> getAllKhachHang() {
        return khachHangRepository.findAllActive();
    }

    @Override
    public Optional<KhachHang> getKhachHangById(String maKH) {
        return khachHangRepository.findById(maKH);
    }

    @Override
    public KhachHang createKhachHang(KhachHang khachHang) {
        if (khachHang != null && !khachHang.isActive()) {
            khachHang.setActive(true);
        }
        return khachHangRepository.create(khachHang);
    }

    @Override
    public KhachHang updateKhachHang(KhachHang khachHang) {
        return khachHangRepository.update(khachHang);
    }

    @Override
    public boolean deactivateKhachHang(String maKH) {
        return khachHangRepository.deactivateKhachHang(maKH);
    }

    @Override
    public long countActive() {
        return khachHangRepository.countActive();
    }
}
