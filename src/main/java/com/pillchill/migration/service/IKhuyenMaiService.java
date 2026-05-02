package com.pillchill.migration.service;

import com.pillchill.migration.entity.KhuyenMai;

import java.util.List;
import java.util.Optional;

public interface IKhuyenMaiService {
    List<KhuyenMai> getAllKhuyenMai();

    KhuyenMai getKhuyenMaiById(String maKM);

    KhuyenMai createKhuyenMai(KhuyenMai khuyenMai);

    KhuyenMai updateKhuyenMai(KhuyenMai khuyenMai);

    boolean deactivateKhuyenMai(String maKM);

    long countActive();

    KhuyenMai findByMa(String maKM);

    boolean isValid(String maKM);
}
