package com.pillchill.migration.migration;

import com.pillchill.migration.entity.KhuyenMai;
import com.pillchill.migration.repository.IKhuyenMaiRepository;
import com.pillchill.migration.repository.impl.KhuyenMaiRepository;

import java.util.ArrayList;
import java.util.List;

public class KhuyenMaiJpaDAO {
    private final IKhuyenMaiRepository khuyenMaiRepository;

    public KhuyenMaiJpaDAO() {
        this.khuyenMaiRepository = new KhuyenMaiRepository();
    }

    public ArrayList<KhuyenMai> getAllKhuyenMai() {
        List<KhuyenMai> allKhuyenMai = khuyenMaiRepository.loadAllKhuyenMai();
        ArrayList<KhuyenMai> activeKhuyenMai = new ArrayList<>();
        for (KhuyenMai khuyenMai : allKhuyenMai) {
            if (khuyenMai.isActive()) {
                activeKhuyenMai.add(khuyenMai);
            }
        }
        return activeKhuyenMai;
    }

    public void addKhuyenMai(KhuyenMai khuyenMai) {
        khuyenMai.setActive(true);
        khuyenMaiRepository.createKhuyenMai(khuyenMai);
    }

    public void updateKhuyenMai(KhuyenMai khuyenMai) {
        KhuyenMai existing = khuyenMaiRepository.findById(khuyenMai.getMaKM());
        if (existing == null) {
            throw new IllegalArgumentException("Không tìm thấy khuyến mãi");
        }

        existing.setMucGiamGia(khuyenMai.getMucGiamGia());
        existing.setNgayApDung(khuyenMai.getNgayApDung());
        existing.setNgayKetThuc(khuyenMai.getNgayKetThuc());
        khuyenMaiRepository.updateKhuyenMai(existing);
    }

    public void deleteKhuyenMai(String maKhuyenMai) {
        KhuyenMai existing = khuyenMaiRepository.findById(maKhuyenMai);
        if (existing == null) {
            throw new IllegalArgumentException("Không tìm thấy khuyến mãi");
        }

        existing.setActive(false);
        khuyenMaiRepository.updateKhuyenMai(existing);
    }
}
