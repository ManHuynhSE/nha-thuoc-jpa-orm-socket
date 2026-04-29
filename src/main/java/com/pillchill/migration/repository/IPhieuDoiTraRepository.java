package com.pillchill.migration.repository;

import com.pillchill.migration.entity.PhieuDoiTra;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IPhieuDoiTraRepository {
    Optional<PhieuDoiTra> findById(String maPhieuDoiTra);

    List<PhieuDoiTra> findByDateRange(LocalDate fromDate, LocalDate toDate);

    List<PhieuDoiTra> findAllActiveWithNhanVienKhachHang();
}
