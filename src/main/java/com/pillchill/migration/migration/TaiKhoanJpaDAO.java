package com.pillchill.migration.migration;

import java.util.ArrayList;

import com.pillchill.migration.entity.TaiKhoan;
import com.pillchill.migration.repository.ITaiKhoanRepository;
import com.pillchill.migration.repository.impl.TaiKhoanRepository;

public class TaiKhoanJpaDAO {
    private final ITaiKhoanRepository taiKhoanRepository;

    public TaiKhoanJpaDAO(ITaiKhoanRepository taiKhoanRepository) {
        this.taiKhoanRepository = taiKhoanRepository;
    }

    public TaiKhoanJpaDAO() {
        this.taiKhoanRepository = new TaiKhoanRepository();
    }

    public TaiKhoan kiemTraDangNhap(String maNV, String matKhau) {
        return taiKhoanRepository.login(maNV, matKhau).orElse(null);
    }

    public TaiKhoan getTaiKhoanById(String maNV) {
        return taiKhoanRepository.findById(maNV).orElse(null);
    }

    public ArrayList<TaiKhoan> getAllTaiKhoan() {
        return new ArrayList<>(taiKhoanRepository.findAllActive());
    }
}
