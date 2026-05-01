package com.pillchill.migration.service.impl;

import com.pillchill.migration.entity.KhuyenMai;
import com.pillchill.migration.repository.IKhuyenMaiRepository;
import com.pillchill.migration.repository.impl.KhuyenMaiRepository;
import com.pillchill.migration.service.IKhuyenMaiService;

import java.util.List;
import java.util.Optional;

public class KhuyenMaiService implements IKhuyenMaiService {
    private final IKhuyenMaiRepository khuyenMaiRepository;

    public KhuyenMaiService(IKhuyenMaiRepository khuyenMaiRepository) {
        this.khuyenMaiRepository = khuyenMaiRepository;
    }

    public KhuyenMaiService() {
        this(new KhuyenMaiRepository());
    }

    @Override
    public List<KhuyenMai> getAllKhuyenMai() {
        return khuyenMaiRepository.findAllActive();
    }

    @Override
    public Optional<KhuyenMai> getKhuyenMaiById(String maKM) {
        return khuyenMaiRepository.findById(maKM);
    }

    @Override
    public KhuyenMai createKhuyenMai(KhuyenMai khuyenMai) {
        if (khuyenMai != null && !khuyenMai.isActive()) {
            khuyenMai.setActive(true);
        }
        return khuyenMaiRepository.create(khuyenMai);
    }

    @Override
    public KhuyenMai updateKhuyenMai(KhuyenMai khuyenMai) {
        return khuyenMaiRepository.update(khuyenMai);
    }

    @Override
    public boolean deactivateKhuyenMai(String maKM) {
        return khuyenMaiRepository.deactivateKhuyenMai(maKM);
    }

    @Override
    public long countActive() {
        return khuyenMaiRepository.countActive();
    }

    @Override
    public KhuyenMai findByMa(String maKM) {
        return khuyenMaiRepository.findByMa(maKM);
    }

    @Override
    public boolean isValid(String maKM) {
        return khuyenMaiRepository.isValid(maKM);
    }
}
