package com.pillchill.migration.repository;

import com.pillchill.migration.entity.KhachHang;

import java.util.List;
import java.util.Optional;

public interface IKhachHangRepository extends GenericRepository<KhachHang, String> {
    List<KhachHang> findAllActive();

    Optional<KhachHang> findById(String maKH);

    long countActive();

    boolean deactivateKhachHang(String maKH);
}
