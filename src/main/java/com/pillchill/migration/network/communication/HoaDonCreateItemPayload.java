package com.pillchill.migration.network.communication;

import java.io.Serial;
import java.io.Serializable;

public class HoaDonCreateItemPayload implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String maThuoc;
    private String tenThuoc;
    private String maLo;
    private int soLuong;
    private float donGia;

    public HoaDonCreateItemPayload() {
    }

    public HoaDonCreateItemPayload(String maThuoc,String tenThuoc, String maLo, int soLuong, float donGia) {
        this.tenThuoc = tenThuoc;
        this.maThuoc = maThuoc;
        this.maLo = maLo;
        this.soLuong = soLuong;
        this.donGia = donGia;
    }

    public String getMaThuoc() {
        return maThuoc;
    }

    public void setMaThuoc(String maThuoc) {
        this.maThuoc = maThuoc;
    }

    public String getMaLo() {
        return maLo;
    }

    public void setMaLo(String maLo) {
        this.maLo = maLo;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public float getDonGia() {
        return donGia;
    }

    public void setDonGia(float donGia) {
        this.donGia = donGia;
    }

    public String getTenThuoc() {
        return tenThuoc;
    }
}
