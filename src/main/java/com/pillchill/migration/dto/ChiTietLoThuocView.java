package com.pillchill.migration.dto;

import java.io.Serializable;
import java.time.LocalDate;

public class ChiTietLoThuocView implements Serializable {
    private String maLo;
    private String maThuoc;
    private String tenThuoc;
    private int soLuong;
    private double giaNhap;
    private LocalDate ngaySanXuat;
    private LocalDate hanSuDung;

    public ChiTietLoThuocView(String maLo, String maThuoc, String tenThuoc, int soLuong, double giaNhap, LocalDate ngaySanXuat, LocalDate hanSuDung) {
        this.maLo = maLo;
        this.maThuoc = maThuoc;
        this.tenThuoc = tenThuoc;
        this.soLuong = soLuong;
        this.giaNhap = giaNhap;
        this.ngaySanXuat = ngaySanXuat;
        this.hanSuDung = hanSuDung;
    }

    public String getMaLo() {
        return maLo;
    }

    public String getMaThuoc() {
        return maThuoc;
    }

    public String getTenThuoc() {
        return tenThuoc;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public double getGiaNhap() {
        return giaNhap;
    }

    public LocalDate getNgaySanXuat() {
        return ngaySanXuat;
    }

    public LocalDate getHanSuDung() {
        return hanSuDung;
    }
}
