package com.pillchill.migration.repository.impl;

import com.pillchill.migration.entity.DonVi;
import com.pillchill.migration.repository.IDonViRepository;

import java.util.List;

public class DonViRepository extends AbstracGenericRepository<DonVi, String> implements IDonViRepository {
    public DonViRepository() {
        super(DonVi.class);
    }

    @Override
    public DonVi createDonVi(DonVi donVi) {
        return create(donVi);
    }

    @Override
    public DonVi updateDonVi(DonVi donVi) {
        return update(donVi);
    }

    @Override
    public boolean deleteDonVi(String maDonVi) {
        return delete(maDonVi);
    }

    @Override
    public DonVi findById(String maDonVi) {
        return findByID(maDonVi);
    }

    @Override
    public List<DonVi> loadAllDonVi() {
        return loadAll();
    }
}
