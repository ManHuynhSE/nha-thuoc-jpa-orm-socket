package com.pillchill.migration.service;

import com.pillchill.migration.dto.CreatePhieuDatCommand;
import com.pillchill.migration.dto.ChiTietPhieuDatView;
import com.pillchill.migration.dto.PhieuDatView;

import java.util.List;

public interface IPhieuDatService {
    List<PhieuDatView> getAllPhieuDatViews();

    List<PhieuDatView> getPhieuDatViewsByMonthYear(int month, int year);

    List<ChiTietPhieuDatView> getChiTietPhieuDatByMaPhieuDat(String maPhieuDat);

    String createPhieuDat(CreatePhieuDatCommand command, String maNhanVien);
}
