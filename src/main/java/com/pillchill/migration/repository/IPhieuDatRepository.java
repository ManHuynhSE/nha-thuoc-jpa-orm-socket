package com.pillchill.migration.repository;

import com.pillchill.migration.entity.PhieuDat;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IPhieuDatRepository {
    Optional<PhieuDat> findById(String maPhieuDat);

    List<PhieuDat> findByDateRange(LocalDate fromDate, LocalDate toDate);

    List<PhieuDat> findAllActiveWithNhanVienKhachHang();
}
