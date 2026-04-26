package com.pillchill.migration.network.communication;

import java.io.Serializable;

public class KhachHangPayload implements Serializable {
    private static final long serialVersionUID = 1L;

    private String maKH;
    private String tenKH;
    private String soDienThoai;
    private Integer diemTichLuy;
    private boolean isActive;

    public KhachHangPayload() {
    }

    public KhachHangPayload(String maKH, String tenKH, String soDienThoai, Integer diemTichLuy, boolean isActive) {
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

    public Integer getDiemTichLuy() {
        return diemTichLuy;
    }

    public void setDiemTichLuy(Integer diemTichLuy) {
        this.diemTichLuy = diemTichLuy;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
