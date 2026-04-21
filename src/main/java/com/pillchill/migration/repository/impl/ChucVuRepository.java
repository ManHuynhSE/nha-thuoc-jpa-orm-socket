package com.pillchill.migration.repository.impl;

import com.pillchill.migration.entity.ChucVu;
import com.pillchill.migration.repository.IChucVuRepository;

public class ChucVuRepository extends AbstracGenericRepository<ChucVu, String> implements IChucVuRepository {
    public ChucVuRepository() {
        super(ChucVu.class);
    }
    public static void main(String[] args) {
        ChucVuRepository nhanVienRepository = new ChucVuRepository();
        System.out.println(nhanVienRepository.loadAll());

    }
}
