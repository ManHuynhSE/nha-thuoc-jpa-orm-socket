package com.pillchill.migration.service;

import java.util.List;
import java.util.Optional;

import com.pillchill.migration.dto.ThuocKemGiaView;
import com.pillchill.migration.entity.Thuoc;

public interface IThuocService {
    List<Thuoc> getAllThuoc();

    Optional<Thuoc> getThuocById(String maThuoc);

    List<ThuocKemGiaView> getAllThuocKemGia();
}
