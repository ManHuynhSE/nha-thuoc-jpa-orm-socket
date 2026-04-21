package com.pillchill.migration.dto;

import java.io.Serializable;
import java.time.LocalDate;

public class ThuocTheoLoView implements Serializable {
    private String maThuoc;
    private String tenThuoc;
    private String maLo;
    private LocalDate hanSuDung;
    private int soLuongLo;
    private String donVi;
    private String maNSX;

    public ThuocTheoLoView(String maThuoc, String tenThuoc, String maLo, LocalDate hanSuDung, int soLuongLo, String donVi, String maNSX) {
        this.maThuoc = maThuoc;
        this.tenThuoc = tenThuoc;
        this.maLo = maLo;
        this.hanSuDung = hanSuDung;
        this.soLuongLo = soLuongLo;
        this.donVi = donVi;
        this.maNSX = maNSX;
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

    public LocalDate getHanSuDung() {
        return hanSuDung;
    }

    public int getSoLuongLo() {
        return soLuongLo;
    }

    public String getDonVi() {
        return donVi;
    }

    public String getMaNSX() {
        return maNSX;
    }
}
