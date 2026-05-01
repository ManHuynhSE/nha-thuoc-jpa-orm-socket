package com.pillchill.migration.repository;

import com.pillchill.migration.entity.NhaSanXuat;

import java.util.List;

public interface INhaSanXuatRepository extends GenericRepository<NhaSanXuat, String> {
    List<NhaSanXuat> findAllActive();
}
