package com.pillchill.migration.service;

import com.pillchill.migration.dto.CreateHoaDonCommand;
import com.pillchill.migration.dto.NhanVienDTO;
import com.pillchill.migration.entity.ChiTietLoThuoc;
import com.pillchill.migration.entity.HoaDon;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface INhanVienService {
    NhanVienDTO addNhanVien(NhanVienDTO nhanVienDTO);
    NhanVienDTO updateNhanVien(NhanVienDTO nhanVienDTO);
    boolean deleteNhanVien(String maNhanVien);
    List<NhanVienDTO> loadALlNhanVien();
}
