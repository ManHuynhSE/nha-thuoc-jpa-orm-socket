package com.pillchill.migration.dto;

import java.io.Serializable;

public class ChiTietHoaDonView implements Serializable {
    private String maHoaDon;
    private String maThuoc;
    private String tenThuoc;
    private String maLo;
    private int soLuong;
    private float donGia;

    public ChiTietHoaDonView(String maHoaDon, String maThuoc, String tenThuoc, String maLo, int soLuong, float donGia) {
        this.maHoaDon = maHoaDon;
        this.maThuoc = maThuoc;
        this.tenThuoc = tenThuoc;
        this.maLo = maLo;
        this.soLuong = soLuong;
        this.donGia = donGia;
    }

    public String getMaHoaDon() {
        return maHoaDon;
    }

    public String getMaThuoc() {
        return maThuoc;
    }

    public String getTenThuoc() {
        return tenThuoc;
    }

    public String getMaLo() {
        return maLo;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public float getDonGia() {
        return donGia;
    }
}
