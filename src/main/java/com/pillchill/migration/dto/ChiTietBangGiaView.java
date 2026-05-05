package com.pillchill.migration.dto;

import java.io.Serializable;

public class ChiTietBangGiaView implements Serializable {
    private String maBangGia;
    private String maThuoc;
    private String tenThuoc;
    private double donGia;
    private String maDonVi;

    public ChiTietBangGiaView() {
    }

    public ChiTietBangGiaView(String maBangGia, String maThuoc, String tenThuoc, double donGia, String maDonVi) {
        this.maBangGia = maBangGia;
        this.maThuoc = maThuoc;
        this.tenThuoc = tenThuoc;
        this.donGia = donGia;
        this.maDonVi = maDonVi;
    }

    public String getMaBangGia() {
        return maBangGia;
    }

    public void setMaBangGia(String maBangGia) {
        this.maBangGia = maBangGia;
    }

    public String getMaThuoc() {
        return maThuoc;
    }

    public void setMaThuoc(String maThuoc) {
        this.maThuoc = maThuoc;
    }

    public String getTenThuoc() {
        return tenThuoc;
    }

    public void setTenThuoc(String tenThuoc) {
        this.tenThuoc = tenThuoc;
    }

    public double getDonGia() {
        return donGia;
    }

    public void setDonGia(double donGia) {
        this.donGia = donGia;
    }

    public String getMaDonVi() {
        return maDonVi;
    }

    public void setMaDonVi(String maDonVi) {
        this.maDonVi = maDonVi;
    }
}
