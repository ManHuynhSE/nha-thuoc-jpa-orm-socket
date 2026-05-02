package com.pillchill.migration.repository.impl;

import com.pillchill.migration.entity.KhuyenMai;
import com.pillchill.migration.repository.IKhuyenMaiRepository;

import java.util.List;
import java.util.Optional;

public class KhuyenMaiRepository extends AbstracGenericRepository<KhuyenMai, String> implements IKhuyenMaiRepository {
    public KhuyenMaiRepository() {
        super(KhuyenMai.class);
    }

    @Override
    public KhuyenMai createKhuyenMai(KhuyenMai khuyenMai) {
        return create(khuyenMai);
    }

    @Override
    public KhuyenMai updateKhuyenMai(KhuyenMai khuyenMai) {
        return update(khuyenMai);
    }

    @Override
    public KhuyenMai findById(String maKhuyenMai) {
        return findByID(maKhuyenMai);
    }


    @Override
    public List<KhuyenMai> loadAllKhuyenMai() {
        return loadAll();
    }

    @Override
    public List<KhuyenMai> findAllActive() {
        return List.of();
    }

    @Override
    public long countActive() {
        return 0;
    }

    @Override
    public boolean deactivateKhuyenMai(String maKM) {
        return false;
    }


    @Override
    public boolean isValid(String maKM) {
        return false;
    }
}
