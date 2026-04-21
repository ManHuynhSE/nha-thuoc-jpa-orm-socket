package com.pillchill.migration.repository.impl;

import com.pillchill.migration.entity.NhanVien;
import com.pillchill.migration.repository.INhanVienRepository;

import java.util.List;

public class NhanVienRepository extends AbstracGenericRepository<NhanVien, String> implements INhanVienRepository {
    private final Template template;
    public NhanVienRepository() {
        super(NhanVien.class);
        this.template = new Template();
    }

    public static void main(String[] args) {
        NhanVienRepository nhanVienRepository = new NhanVienRepository();
        System.out.println(nhanVienRepository.loadAll());
        
    }

    @Override
    public NhanVien createNhanVien(NhanVien nhanVien) {
        return create(nhanVien);
    }

    @Override
    public NhanVien updateNhanVien(NhanVien nhanVien) {
        return update(nhanVien);
    }

    @Override
    public boolean deleteNhanVien(String maNhanVien) {
        return delete(maNhanVien);
    }

    @Override
    public NhanVien findById(String maNhanVien) {
        return findByID(maNhanVien);
    }

    @Override
    public List<NhanVien> loadAllNhanVien() {
        return loadAll();
    }
}
