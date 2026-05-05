package com.pillchill.migration.network.communication;

import java.io.Serializable;

public class ChiTietBangGiaPayload implements Serializable {
    private String maBangGia;
    private String maThuoc;
    private double donGia;
    private String maDonVi;
    private boolean isActive;

    public ChiTietBangGiaPayload() {
    }

    public ChiTietBangGiaPayload(String maBangGia, String maThuoc, double donGia, String maDonVi, boolean isActive) {
        this.maBangGia = maBangGia;
        this.maThuoc = maThuoc;
        this.donGia = donGia;
        this.maDonVi = maDonVi;
        this.isActive = isActive;
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

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
