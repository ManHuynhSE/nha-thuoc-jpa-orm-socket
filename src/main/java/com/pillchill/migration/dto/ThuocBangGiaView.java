package com.pillchill.migration.dto;

import java.io.Serializable;

public class ThuocBangGiaView implements Serializable {
    private String maThuoc;
    private String tenThuoc;
    private String maDonVi;
    private double giaChuan;
    private double giaHienTai;

    public ThuocBangGiaView() {
    }

    public ThuocBangGiaView(String maThuoc, String tenThuoc, String maDonVi, double giaChuan, double giaHienTai) {
        this.maThuoc = maThuoc;
        this.tenThuoc = tenThuoc;
        this.maDonVi = maDonVi;
        this.giaChuan = giaChuan;
        this.giaHienTai = giaHienTai;
    }

    public String getMaThuoc() {
        return maThuoc;
    }

    public void setMaThuoc(String maThuoc) {
        this.maThuoc = maThuoc;
    }

    public String getTenThuoc() {
        return tenThuoc;
    }

    public void setTenThuoc(String tenThuoc) {
        this.tenThuoc = tenThuoc;
    }

    public String getMaDonVi() {
        return maDonVi;
    }

    public void setMaDonVi(String maDonVi) {
        this.maDonVi = maDonVi;
    }

    public double getGiaChuan() {
        return giaChuan;
    }

    public void setGiaChuan(double giaChuan) {
        this.giaChuan = giaChuan;
    }

    public double getGiaHienTai() {
        return giaHienTai;
    }

    public void setGiaHienTai(double giaHienTai) {
        this.giaHienTai = giaHienTai;
    }
}
