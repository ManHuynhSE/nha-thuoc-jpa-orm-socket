package com.pillchill.migration.repository;

import com.pillchill.migration.entity.BangGia;

import java.util.List;
import java.util.Optional;

public interface IBangGiaRepository extends GenericRepository<BangGia, String>{
    Optional<BangGia> findById(String maBangGia);

    List<BangGia> findAllActive();

    boolean deactivateBangGia(String maBG);
}
