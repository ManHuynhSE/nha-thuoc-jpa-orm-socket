package com.pillchill.migration.dto;

import java.io.Serializable;
import java.time.LocalDate;

public class PhieuNhapView implements Serializable {
    private String maPhieuNhapThuoc;
    private String maNhanVien;
    private String tenNhanVien;
    private LocalDate ngayNhap;

    public PhieuNhapView(String maPhieuNhapThuoc, String maNhanVien, String tenNhanVien, LocalDate ngayNhap) {
        this.maPhieuNhapThuoc = maPhieuNhapThuoc;
        this.maNhanVien = maNhanVien;
        this.tenNhanVien = tenNhanVien;
        this.ngayNhap = ngayNhap;
    }

    public String getMaPhieuNhapThuoc() {
        return maPhieuNhapThuoc;
    }

    public String getMaNhanVien() {
        return maNhanVien;
    }

    public String getTenNhanVien() {
        return tenNhanVien;
    }

    public LocalDate getNgayNhap() {
        return ngayNhap;
    }
}
