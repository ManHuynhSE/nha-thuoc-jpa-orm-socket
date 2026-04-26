package com.pillchill.migration.service.impl;

import com.pillchill.migration.entity.NhanVien;
import com.pillchill.migration.repository.INhanVienRepository;
import com.pillchill.migration.repository.impl.NhanVienRepository;
import com.pillchill.migration.service.INhanVienService;

import java.util.List;
import java.util.Optional;

public class NhanVienService implements INhanVienService {
    private final INhanVienRepository nhanVienRepository;

    public NhanVienService(INhanVienRepository nhanVienRepository) {
        this.nhanVienRepository = nhanVienRepository;
    }

    public NhanVienService() {
        this(new NhanVienRepository());
    }

    @Override
    public List<NhanVien> getAllNhanVien() {
        return nhanVienRepository.findAllActive();
    }

    @Override
    public Optional<NhanVien> getNhanVienById(String maNV) {
        return nhanVienRepository.findById(maNV);
    }

    @Override
    public NhanVien createNhanVien(NhanVien nhanVien) {
        if (nhanVien != null && !nhanVien.isActive()) {
            nhanVien.setActive(true);
        }
        return nhanVienRepository.create(nhanVien);
    }

    @Override
    public NhanVien updateNhanVien(NhanVien nhanVien) {
        return nhanVienRepository.update(nhanVien);
    }

    @Override
    public boolean deactivateNhanVien(String maNV) {
        return nhanVienRepository.deactivateNhanVien(maNV);
    }

    @Override
    public long countActive() {
        return nhanVienRepository.countActive();
    }
}
