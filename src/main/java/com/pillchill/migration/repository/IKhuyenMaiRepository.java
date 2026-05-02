package com.pillchill.migration.repository;

import com.pillchill.migration.entity.KhuyenMai;

import java.util.List;
import java.util.Optional;

public interface IKhuyenMaiRepository {
    KhuyenMai createKhuyenMai(KhuyenMai khuyenMai);

    KhuyenMai updateKhuyenMai(KhuyenMai khuyenMai);

    KhuyenMai findById(String maKhuyenMai);

    List<KhuyenMai> loadAllKhuyenMai();

    List<KhuyenMai> findAllActive();

    long countActive();

    boolean deactivateKhuyenMai(String maKM);


    boolean isValid(String maKM);
}
