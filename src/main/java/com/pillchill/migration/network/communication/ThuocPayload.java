package com.pillchill.migration.network.communication;

import java.io.Serializable;

public class ThuocPayload implements Serializable {
    private final String maThuoc;
    private final String tenThuoc;
    private final int soLuongTon;
    private final String donVi;
    private final int soLuongToiThieu;
    private final String nhaSanXuat;
    private final Double giaBanDau;

    public ThuocPayload(String maThuoc, String tenThuoc, int soLuongTon, String donVi, int soLuongToiThieu, String nhaSanXuat, Double giaBanDau) {
        this.maThuoc = maThuoc;
        this.tenThuoc = tenThuoc;
        this.soLuongTon = soLuongTon;
        this.donVi = donVi;
        this.soLuongToiThieu = soLuongToiThieu;
        this.nhaSanXuat = nhaSanXuat;
        this.giaBanDau = giaBanDau;
    }

    public String getMaThuoc() {
        return maThuoc;
    }

    public String getTenThuoc() {
        return tenThuoc;
    }

    public int getSoLuongTon() {
        return soLuongTon;
    }

    public String getDonVi() {
        return donVi;
    }

    public int getSoLuongToiThieu() {
        return soLuongToiThieu;
    }

    public String getNhaSanXuat() {
        return nhaSanXuat;
    }

    public Double getGiaBanDau() {
        return giaBanDau;
    }
}
