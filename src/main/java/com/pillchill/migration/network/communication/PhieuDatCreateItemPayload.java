package com.pillchill.migration.network.communication;

import java.io.Serial;
import java.io.Serializable;

public class PhieuDatCreateItemPayload implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String maThuoc;
    private String maLo;
    private int soLuong;

    public PhieuDatCreateItemPayload() {
    }

    public PhieuDatCreateItemPayload(String maThuoc, String maLo, int soLuong) {
        this.maThuoc = maThuoc;
        this.maLo = maLo;
        this.soLuong = soLuong;
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
}
