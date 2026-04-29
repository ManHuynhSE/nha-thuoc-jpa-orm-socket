package com.pillchill.migration.service;

import com.pillchill.migration.dto.ChiTietPhieuDoiTraView;
import com.pillchill.migration.dto.PhieuDoiTraView;

import java.util.List;

public interface IPhieuDoiTraService {
    List<PhieuDoiTraView> getAllPhieuDoiTraViews();

    List<PhieuDoiTraView> getPhieuDoiTraViewsByMonthYear(int month, int year);

    List<ChiTietPhieuDoiTraView> getChiTietPhieuDoiTraByMaPhieuDoiTra(String maPhieuDoiTra);
}
