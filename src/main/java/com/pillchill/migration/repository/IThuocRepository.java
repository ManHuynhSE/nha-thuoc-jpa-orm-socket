package com.pillchill.migration.repository;

import com.pillchill.migration.entity.Thuoc;

import java.util.List;
import java.util.Optional;

public interface IThuocRepository extends GenericRepository<Thuoc,String> {
    List<Thuoc> findAllActive();

    Optional<Thuoc> findById(String maThuoc);

    long countActive();

    void updateSoLuongTon(String maThuoc, int soLuongTon);
}
