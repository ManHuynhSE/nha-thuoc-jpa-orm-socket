package com.pillchill.migration.dto;

import java.io.Serializable;

public class ChiTietPhieuDatView implements Serializable {
    private String maPhieuDat;
    private String maThuoc;
    private String tenThuoc;
    private String maLo;
    private int soLuong;

    public ChiTietPhieuDatView(String maPhieuDat, String maThuoc, String tenThuoc, String maLo, int soLuong) {
        this.maPhieuDat = maPhieuDat;
        this.maThuoc = maThuoc;
        this.tenThuoc = tenThuoc;
        this.maLo = maLo;
        this.soLuong = soLuong;
    }

    public String getMaPhieuDat() {
        return maPhieuDat;
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
}
