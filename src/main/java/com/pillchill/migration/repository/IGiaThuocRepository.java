package com.pillchill.migration.repository;

import java.util.Optional;

public interface IGiaThuocRepository {
    Optional<Double> getGiaHienTaiByMaThuoc(String maThuoc);
}
