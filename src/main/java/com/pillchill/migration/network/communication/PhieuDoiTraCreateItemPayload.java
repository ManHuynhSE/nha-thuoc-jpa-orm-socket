package com.pillchill.migration.network.communication;

import java.io.Serial;
import java.io.Serializable;

public class PhieuDoiTraCreateItemPayload implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String maThuoc;
    private String maLo;
    private int soLuong;
    private double donGia;
    private String lyDo;

    public PhieuDoiTraCreateItemPayload() {
    }

    public PhieuDoiTraCreateItemPayload(String maThuoc, String maLo, int soLuong, double donGia, String lyDo) {
        this.maThuoc = maThuoc;
        this.maLo = maLo;
        this.soLuong = soLuong;
        this.donGia = donGia;
        this.lyDo = lyDo;
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

    public double getDonGia() {
        return donGia;
    }

    public void setDonGia(double donGia) {
        this.donGia = donGia;
    }

    public String getLyDo() {
        return lyDo;
    }

    public void setLyDo(String lyDo) {
        this.lyDo = lyDo;
    }
}
