package com.pillchill.migration.service;

import com.pillchill.migration.entity.KhachHang;

import java.util.List;
import java.util.Optional;

public interface IKhachHangService {
    List<KhachHang> getAllKhachHang();

    Optional<KhachHang> getKhachHangById(String maKH);

    KhachHang createKhachHang(KhachHang khachHang);

    KhachHang updateKhachHang(KhachHang khachHang);

    boolean deactivateKhachHang(String maKH);

    long countActive();
}
