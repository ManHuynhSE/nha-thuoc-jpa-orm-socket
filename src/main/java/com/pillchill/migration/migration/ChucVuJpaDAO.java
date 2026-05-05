package com.pillchill.migration.migration;

import java.util.ArrayList;
import java.util.List;

import com.pillchill.migration.entity.ChucVu;
import com.pillchill.migration.repository.impl.ChucVuRepository;

public class ChucVuJpaDAO {
    private final ChucVuRepository chucVuRepository;

    public ChucVuJpaDAO() {
        this.chucVuRepository = new ChucVuRepository();
    }

    public ArrayList<ChucVu> getAllChucVu() {
        List<ChucVu> allChucVu = chucVuRepository.loadAll();
        ArrayList<ChucVu> activeChucVu = new ArrayList<>();
        for (ChucVu chucVu : allChucVu) {
            if (chucVu.isActive()) {
                activeChucVu.add(chucVu);
            }
        }
        return activeChucVu;
    }

    public ArrayList<ChucVu> getAllInactiveChucVu() {
        List<ChucVu> allChucVu = chucVuRepository.loadAll();
        ArrayList<ChucVu> inactiveChucVu = new ArrayList<>();
        for (ChucVu chucVu : allChucVu) {
            if (!chucVu.isActive()) {
                inactiveChucVu.add(chucVu);
            }
        }
        return inactiveChucVu;
    }

    public void addChucVu(ChucVu chucVu) {
        chucVu.setActive(true);
        chucVuRepository.create(chucVu);
    }

    public void updateChucVu(ChucVu chucVu) {
        ChucVu existing = chucVuRepository.findByID(chucVu.getMaChucVu());
        if (existing == null) {
            throw new IllegalArgumentException("Không tìm thấy chức vụ");
        }

        existing.setTenChucVu(chucVu.getTenChucVu());
        chucVuRepository.update(existing);
    }

    public void deleteChucVu(String maChucVu) {
        ChucVu existing = chucVuRepository.findByID(maChucVu);
        if (existing == null) {
            throw new IllegalArgumentException("Không tìm thấy chức vụ");
        }

        existing.setActive(false);
        chucVuRepository.update(existing);
    }

    public boolean reactivateChucVu(String maChucVu) {
        ChucVu existing = chucVuRepository.findByID(maChucVu);
        if (existing == null) {
            throw new IllegalArgumentException("Không tìm thấy chức vụ");
        }
        if (existing.isActive()) {
            return true;
        }
        existing.setActive(true);
        chucVuRepository.update(existing);
        return true;
    }
}
