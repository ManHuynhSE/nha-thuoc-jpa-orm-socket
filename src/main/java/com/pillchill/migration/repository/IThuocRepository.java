package com.pillchill.migration.repository;

import com.pillchill.migration.entity.Thuoc;

import java.util.List;
import java.util.Optional;

public interface IThuocRepository {
    List<Thuoc> findAllActive();

    Optional<Thuoc> findById(String maThuoc);

    long countActive();

    void updateSoLuongTon(String maThuoc, int soLuongTon);
}
