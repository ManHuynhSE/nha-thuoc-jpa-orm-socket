package com.pillchill.migration.service;

import com.pillchill.migration.dto.CreateHoaDonCommand;
import com.pillchill.migration.entity.ChiTietLoThuoc;
import com.pillchill.migration.entity.HoaDon;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IHoaDonService {
    HoaDon createHoaDon(CreateHoaDonCommand command);

    ChiTietLoThuoc getLot(String maLo, String maThuoc);

    Optional<HoaDon> getHoaDonById(String maHoaDon);

    List<HoaDon> findHoaDonByDateRange(LocalDate fromDate, LocalDate toDate);
}
