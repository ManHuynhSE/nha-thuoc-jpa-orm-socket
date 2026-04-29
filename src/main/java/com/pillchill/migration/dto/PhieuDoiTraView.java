package com.pillchill.migration.dto;

import java.io.Serializable;
import java.time.LocalDate;

public class PhieuDoiTraView implements Serializable {
    private String maPhieuDoiTra;
    private String maNhanVien;
    private String tenNhanVien;
    private String maKhachHang;
    private String tenKhachHang;
    private LocalDate ngayDoiTra;

    public PhieuDoiTraView(
            String maPhieuDoiTra,
            String maNhanVien,
            String tenNhanVien,
            String maKhachHang,
            String tenKhachHang,
            LocalDate ngayDoiTra
    ) {
        this.maPhieuDoiTra = maPhieuDoiTra;
        this.maNhanVien = maNhanVien;
        this.tenNhanVien = tenNhanVien;
        this.maKhachHang = maKhachHang;
        this.tenKhachHang = tenKhachHang;
        this.ngayDoiTra = ngayDoiTra;
    }

    public String getMaPhieuDoiTra() {
        return maPhieuDoiTra;
    }

    public String getMaNhanVien() {
        return maNhanVien;
    }

    public String getTenNhanVien() {
        return tenNhanVien;
    }

    public String getMaKhachHang() {
        return maKhachHang;
    }

    public String getTenKhachHang() {
        return tenKhachHang;
    }

    public LocalDate getNgayDoiTra() {
        return ngayDoiTra;
    }
}
