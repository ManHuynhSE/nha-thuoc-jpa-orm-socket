package com.pillchill.migration.repository;

import com.pillchill.migration.entity.KhuyenMai;

import java.util.List;

public interface IKhuyenMaiRepository {
    KhuyenMai createKhuyenMai(KhuyenMai khuyenMai);

    KhuyenMai updateKhuyenMai(KhuyenMai khuyenMai);

    boolean deleteKhuyenMai(String maKhuyenMai);

    KhuyenMai findById(String maKhuyenMai);

    List<KhuyenMai> loadAllKhuyenMai();
}
