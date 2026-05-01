package com.pillchill.migration.service;

import com.pillchill.migration.entity.NhaSanXuat;

import java.util.List;

public interface INhaSanXuatService {
    List<NhaSanXuat> getAllNhaSanXuat();
    boolean addNhaSanXuat(NhaSanXuat nhaSanXuat);
    boolean updateNhaSanXuat(NhaSanXuat nhaSanXuat);
    boolean deleteNhaSanXuat(String maNSX);
}
