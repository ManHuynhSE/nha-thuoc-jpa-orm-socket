package com.pillchill.migration.network.communication;

import java.io.Serializable;

public class NhanVienPayload implements Serializable {
    private static final long serialVersionUID = 1L;

    private String maNV;
    private String tenNV;
    private String maChucVu;
    private String soDienThoai;
    private boolean isActive;

    public NhanVienPayload() {
    }

    public NhanVienPayload(String maNV, String tenNV, String maChucVu, String soDienThoai, boolean isActive) {
        this.maNV = maNV;
        this.tenNV = tenNV;
        this.maChucVu = maChucVu;
        this.soDienThoai = soDienThoai;
        this.isActive = isActive;
    }

    public String getMaNV() {
        return maNV;
    }

    public void setMaNV(String maNV) {
        this.maNV = maNV;
    }

    public String getTenNV() {
        return tenNV;
    }

    public void setTenNV(String tenNV) {
        this.tenNV = tenNV;
    }

    public String getMaChucVu() {
        return maChucVu;
    }

    public void setMaChucVu(String maChucVu) {
        this.maChucVu = maChucVu;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
