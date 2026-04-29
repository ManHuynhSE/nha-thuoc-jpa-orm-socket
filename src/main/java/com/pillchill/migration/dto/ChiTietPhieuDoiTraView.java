package com.pillchill.migration.dto;

import java.io.Serializable;

public class ChiTietPhieuDoiTraView implements Serializable {
    private String maPhieuDoiTra;
    private String maThuoc;
    private String tenThuoc;
    private String maLo;
    private int soLuong;
    private double donGia;
    private String lyDo;

    public ChiTietPhieuDoiTraView(
            String maPhieuDoiTra,
            String maThuoc,
            String tenThuoc,
            String maLo,
            int soLuong,
            double donGia,
            String lyDo
    ) {
        this.maPhieuDoiTra = maPhieuDoiTra;
        this.maThuoc = maThuoc;
        this.tenThuoc = tenThuoc;
        this.maLo = maLo;
        this.soLuong = soLuong;
        this.donGia = donGia;
        this.lyDo = lyDo;
    }

    public String getMaPhieuDoiTra() {
        return maPhieuDoiTra;
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

    public double getDonGia() {
        return donGia;
    }

    public String getLyDo() {
        return lyDo;
    }
}
