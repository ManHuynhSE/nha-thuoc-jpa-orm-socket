package com.pillchill.migration.service.impl;

import com.pillchill.migration.dto.NhanVienDTO;
import com.pillchill.migration.entity.ChucVu;
import com.pillchill.migration.entity.NhanVien;
import com.pillchill.migration.repository.impl.ChucVuRepository;
import com.pillchill.migration.repository.INhanVienRepository;
import com.pillchill.migration.repository.impl.NhanVienRepository;
import com.pillchill.migration.service.INhanVienService;

import java.util.ArrayList;
import java.util.List;

public class NhanVienService implements INhanVienService {
    private final INhanVienRepository nhanVienRepository;
    private final ChucVuRepository chucVuRepository;

    public NhanVienService() {
        nhanVienRepository = new NhanVienRepository();
        chucVuRepository = new ChucVuRepository();
    }

    @Override
    public NhanVienDTO addNhanVien(NhanVienDTO nhanVienDTO) {
        if (nhanVienDTO == null) {
            throw new RuntimeException("Dữ liệu nhân viên không hợp lệ");
        }
        ChucVu chucVu = chucVuRepository.findByID(nhanVienDTO.getChucVu());
        if (chucVu == null) {
            throw new RuntimeException("Chức vụ không tồn tại");
        }

        NhanVien created = nhanVienRepository.createNhanVien(NhanVien.builder()
                .maNV(nhanVienDTO.getMaNV())
                .tenNV(nhanVienDTO.getTenNV())
                .soDienThoai(nhanVienDTO.getSoDienThoai())
                .isActive(true)
                .chucVu(chucVu)
                .build());
        return toDTO(created);
    }

    @Override
    public NhanVienDTO updateNhanVien(NhanVienDTO nhanVienDTO) {
        if (nhanVienDTO == null) {
            throw new RuntimeException("Dữ liệu nhân viên không hợp lệ");
        }
        NhanVien existing = nhanVienRepository.findById(nhanVienDTO.getMaNV());
        if (existing == null) {
            throw new RuntimeException("Không tìm thấy nhân viên để cập nhật");
        }
        ChucVu chucVu = chucVuRepository.findByID(nhanVienDTO.getChucVu());
        if (chucVu == null) {
            throw new RuntimeException("Chức vụ không tồn tại");
        }

        existing.setTenNV(nhanVienDTO.getTenNV());
        existing.setSoDienThoai(nhanVienDTO.getSoDienThoai());
        existing.setChucVu(chucVu);
        NhanVien updated = nhanVienRepository.updateNhanVien(existing);
        return toDTO(updated);
    }

    @Override
    public boolean deleteNhanVien(String maNhanVien) {
        if (maNhanVien == null || maNhanVien.isBlank()) {
            throw new RuntimeException("Mã nhân viên không hợp lệ");
        }

        NhanVien existing = nhanVienRepository.findById(maNhanVien);
        if (existing == null) {
            throw new RuntimeException("Không tìm thấy nhân viên để xóa");
        }
        if (!existing.isActive()) {
            return true;
        }

        existing.setActive(false);
        nhanVienRepository.updateNhanVien(existing);
        return true;
    }

    @Override
    public List<NhanVienDTO> loadALlNhanVien() {
        List<NhanVien> list = nhanVienRepository.loadAllNhanVien();

        List<NhanVienDTO> result = new ArrayList<>();
        for (NhanVien item : list) {
            if (item.isActive()) {
                result.add(toDTO(item));
            }
        }
        return result;
    }

    private NhanVienDTO toDTO(NhanVien item) {
        String tenChucVu = item.getChucVu() != null ? item.getChucVu().getTenChucVu() : "";
        return NhanVienDTO.builder()
                .maNV(item.getMaNV())
                .tenNV(item.getTenNV())
                .chucVu(tenChucVu)
                .soDienThoai(item.getSoDienThoai())
                .isActive(item.isActive())
                .build();
    }

    public static void main(String[] args) {
        INhanVienRepository nhanVienRepository = new NhanVienRepository();
        List<NhanVien> list = nhanVienRepository.loadAllNhanVien();
//        System.out.println(DataMapper.mapList(list, NhanVienDTO.class));
    }
}
