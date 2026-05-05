package com.pillchill.migration.dto;

import java.io.Serializable;

public class KhachHangDTO implements Serializable {
    private String maKH;
    private String tenKH;
    private String soDienThoai;
    private int diemTichLuy;
    private boolean isActive;

    public KhachHangDTO() {}

    public KhachHangDTO(String maKH, String tenKH, String soDienThoai, int diemTichLuy, boolean isActive) {
        this.maKH = maKH;
        this.tenKH = tenKH;
        this.soDienThoai = soDienThoai;
        this.diemTichLuy = diemTichLuy;
        this.isActive = isActive;
    }

    public String getMaKH() {
        return maKH;
    }

    public void setMaKH(String maKH) {
        this.maKH = maKH;
    }

    public String getTenKH() {
        return tenKH;
    }

    public void setTenKH(String tenKH) {
        this.tenKH = tenKH;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public int getDiemTichLuy() {
        return diemTichLuy;
    }

    public void setDiemTichLuy(int diemTichLuy) {
        this.diemTichLuy = diemTichLuy;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
