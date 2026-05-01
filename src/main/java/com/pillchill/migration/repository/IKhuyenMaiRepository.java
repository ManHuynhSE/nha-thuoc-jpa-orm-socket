package com.pillchill.migration.repository;

import com.pillchill.migration.entity.KhuyenMai;

import java.util.List;
import java.util.Optional;

public interface IKhuyenMaiRepository extends GenericRepository<KhuyenMai, String> {
    List<KhuyenMai> findAllActive();

    Optional<KhuyenMai> findById(String maKM);

    long countActive();

    boolean deactivateKhuyenMai(String maKM);

    KhuyenMai findByMa(String maKM);

    boolean isValid(String maKM);
}
