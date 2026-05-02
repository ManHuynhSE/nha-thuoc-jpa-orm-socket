package com.pillchill.migration.migration;

import com.pillchill.migration.entity.DonVi;
import com.pillchill.migration.repository.IDonViRepository;
import com.pillchill.migration.repository.impl.DonViRepository;

import java.util.ArrayList;
import java.util.List;

public class DonViJpaDAO {
    private final IDonViRepository donViRepository;

    public DonViJpaDAO() {
        this.donViRepository = new DonViRepository();
    }

    public ArrayList<DonVi> getAllDonVi() {
        List<DonVi> allDonVi = donViRepository.loadAllDonVi();
        ArrayList<DonVi> activeDonVi = new ArrayList<>();
        for (DonVi donVi : allDonVi) {
            if (donVi.isActive()) {
                activeDonVi.add(donVi);
            }
        }
        return activeDonVi;
    }

    public void addDonVi(DonVi donVi) {
        donVi.setActive(true);
        donViRepository.createDonVi(donVi);
    }

    public void updateDonVi(DonVi donVi) {
        DonVi existing = donViRepository.findById(donVi.getMaDonVi());
        if (existing == null) {
            throw new IllegalArgumentException("Không tìm thấy đơn vị");
        }

        existing.setTenDonVi(donVi.getTenDonVi());
        donViRepository.updateDonVi(existing);
    }

    public void deleteDonVi(String maDonVi) {
        DonVi existing = donViRepository.findById(maDonVi);
        if (existing == null) {
            throw new IllegalArgumentException("Không tìm thấy đơn vị");
        }

        existing.setActive(false);
        donViRepository.updateDonVi(existing);
    }
}
