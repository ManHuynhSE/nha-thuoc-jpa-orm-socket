package com.pillchill.migration.dto;

import java.io.Serializable;
import java.time.LocalDate;

public class HoaDonView implements Serializable {
    private String maHoaDon;
    private String maNhanVien;
    private String tenNhanVien;
    private String maKhachHang;
    private String tenKhachHang;
    private LocalDate ngayBan;
    private String ghiChu;

    public HoaDonView(String maHoaDon, String maNhanVien, String tenNhanVien, String maKhachHang, String tenKhachHang, LocalDate ngayBan, String ghiChu) {
        this.maHoaDon = maHoaDon;
        this.maNhanVien = maNhanVien;
        this.tenNhanVien = tenNhanVien;
        this.maKhachHang = maKhachHang;
        this.tenKhachHang = tenKhachHang;
        this.ngayBan = ngayBan;
        this.ghiChu = ghiChu;
    }

    public String getMaHoaDon() {
        return maHoaDon;
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

    public LocalDate getNgayBan() {
        return ngayBan;
    }

    public String getGhiChu() {
        return ghiChu;
    }
}
