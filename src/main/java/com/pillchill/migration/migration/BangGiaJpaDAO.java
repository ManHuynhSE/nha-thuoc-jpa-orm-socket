package com.pillchill.migration.migration;

import com.pillchill.migration.entity.BangGia;
import com.pillchill.migration.repository.IBangGiaRepository;
import com.pillchill.migration.repository.impl.BangGiaRepository;

import java.util.ArrayList;
import java.util.List;

public class BangGiaJpaDAO {
    private final IBangGiaRepository bangGiaRepository;

    public BangGiaJpaDAO() {
        this.bangGiaRepository = new BangGiaRepository();
    }

    public ArrayList<BangGia> getAllBangGia() {
        List<BangGia> allBangGia= bangGiaRepository.loadAll();
        ArrayList<BangGia> activeBangGia = new ArrayList<>();
        for (BangGia bangGia : allBangGia) {
            if (bangGia.isActive()) {
                activeBangGia.add(bangGia);
            }
        }
        return activeBangGia;
    }

    public ArrayList<BangGia> getAllBangGiaInactive() {
        List<BangGia> allBangGia = bangGiaRepository.loadAll();
        ArrayList<BangGia> inactiveBangGia = new ArrayList<>();
        for (BangGia bangGia : allBangGia) {
            if (!bangGia.isActive()) {
                inactiveBangGia.add(bangGia);
            }
        }
        return inactiveBangGia;
    }

    public void addBangGia(BangGia bangGia) {
        bangGia.setActive(true);
        bangGiaRepository.create(bangGia);
    }

    public void updateBangGia(BangGia bangGia) {
        BangGia existing = bangGiaRepository.findByID(bangGia.getMaBangGia());
        if (existing == null) {
            throw new IllegalArgumentException("Không tìm thấy bảng giá");
        }

        existing.setTrangThai(bangGia.getTrangThai());
        existing.setGhiChu(bangGia.getGhiChu());
        existing.setNgayApDung(bangGia.getNgayApDung());
        existing.setNgayKetThuc(bangGia.getNgayKetThuc());
        bangGiaRepository.update(existing);
    }

    public boolean deleteBangGia(String maBangGia) {
        BangGia existing = bangGiaRepository.findByID(maBangGia);
        if (existing == null) {
            throw new IllegalArgumentException("Không tìm thấy khuyến mãi");
        }

        existing.setActive(false);
        bangGiaRepository.update(existing);
        return true;
    }

    public boolean reactiveBangGia(String maBangGia) {
        BangGia existing = bangGiaRepository.findByID(maBangGia);
        if (existing == null) {
            throw new IllegalArgumentException("Không tìm thấy bảng giá");
        }

        existing.setActive(true);
        bangGiaRepository.update(existing);
        return true;
    }

    public BangGia findBangGiaById(String maBangGia) {
        return bangGiaRepository.findByID(maBangGia);
    }



}