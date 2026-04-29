package com.pillchill.migration.repository;

import com.pillchill.migration.entity.PhieuNhapThuoc;

import java.util.List;

public interface IPhieuNhapRepository {
    List<PhieuNhapThuoc> findAllActiveWithNhanVien();
}
