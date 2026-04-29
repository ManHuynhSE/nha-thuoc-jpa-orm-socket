package com.pillchill.migration.dto;

import java.io.Serializable;
import java.time.LocalDate;

public class PhieuDatView implements Serializable {
    private String maPhieuDat;
    private String maNhanVien;
    private String tenNhanVien;
    private String maKhachHang;
    private String tenKhachHang;
    private LocalDate ngayDat;
    private boolean received;
    private String ghiChu;

    public PhieuDatView(
            String maPhieuDat,
            String maNhanVien,
            String tenNhanVien,
            String maKhachHang,
            String tenKhachHang,
            LocalDate ngayDat,
            boolean received,
            String ghiChu
    ) {
        this.maPhieuDat = maPhieuDat;
        this.maNhanVien = maNhanVien;
        this.tenNhanVien = tenNhanVien;
        this.maKhachHang = maKhachHang;
        this.tenKhachHang = tenKhachHang;
        this.ngayDat = ngayDat;
        this.received = received;
        this.ghiChu = ghiChu;
    }

    public String getMaPhieuDat() {
        return maPhieuDat;
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

    public LocalDate getNgayDat() {
        return ngayDat;
    }

    public boolean isReceived() {
        return received;
    }

    public String getGhiChu() {
        return ghiChu;
    }
}
