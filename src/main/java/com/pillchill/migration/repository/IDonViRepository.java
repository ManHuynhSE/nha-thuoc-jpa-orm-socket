package com.pillchill.migration.repository;

import com.pillchill.migration.entity.DonVi;

import java.util.List;

public interface IDonViRepository {
    DonVi createDonVi(DonVi donVi);

    DonVi updateDonVi(DonVi donVi);

    boolean deleteDonVi(String maDonVi);

    DonVi findById(String maDonVi);

    List<DonVi> loadAllDonVi();
}
