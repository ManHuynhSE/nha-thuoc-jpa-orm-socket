package com.pillchill.migration.repository;

import com.pillchill.migration.entity.HoaDon;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IHoaDonRepository {
    Optional<HoaDon> findById(String maHoaDon);

    List<HoaDon> findByDateRange(LocalDate fromDate, LocalDate toDate);

    void save(HoaDon hoaDon);
}
