package com.pillchill.migration.repository.impl;

import com.pillchill.migration.entity.KhuyenMai;
import com.pillchill.migration.repository.IKhuyenMaiRepository;

import java.util.List;

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
    public boolean deleteKhuyenMai(String maKhuyenMai) {
        return delete(maKhuyenMai);
    }

    @Override
    public KhuyenMai findById(String maKhuyenMai) {
        return findByID(maKhuyenMai);
    }

    @Override
    public List<KhuyenMai> loadAllKhuyenMai() {
        return loadAll();
    }
}
