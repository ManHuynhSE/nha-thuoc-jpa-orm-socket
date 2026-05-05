package com.pillchill.migration.service;

import com.pillchill.migration.entity.BangGia;

import java.util.List;
import java.util.Optional;

public interface IBangGiaService {
    Optional<BangGia> getBangGiaById(String maBangGia);

    List<BangGia> getAllBangGiaActive();

    BangGia createBangGia(BangGia bangGia);

    BangGia updateBangGia(BangGia bangGia);

    boolean deactivateBangGia(String maBangGia);
}