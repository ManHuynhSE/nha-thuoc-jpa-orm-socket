package com.pillchill.migration.dto;

import java.io.Serializable;
import java.time.LocalDate;

public class ChiTietPhieuNhapView implements Serializable {
    private String maPhieuNhapThuoc;
    private String maThuoc;
    private String maLo;
    private String tenThuoc;
    private Integer soLuong;
    private Double donGia;
    private String donVi;
    private String maNSX;
    private LocalDate ngaySanXuat;
    private LocalDate hanSuDung;

    public ChiTietPhieuNhapView(
            String maPhieuNhapThuoc,
            String maThuoc,
            String maLo,
            String tenThuoc,
            Integer soLuong,
            Double donGia,
            String donVi,
            String maNSX,
            LocalDate ngaySanXuat,
            LocalDate hanSuDung
    ) {
        this.maPhieuNhapThuoc = maPhieuNhapThuoc;
        this.maThuoc = maThuoc;
        this.maLo = maLo;
        this.tenThuoc = tenThuoc;
        this.soLuong = soLuong;
        this.donGia = donGia;
        this.donVi = donVi;
        this.maNSX = maNSX;
        this.ngaySanXuat = ngaySanXuat;
        this.hanSuDung = hanSuDung;
    }

    public String getMaPhieuNhapThuoc() {
        return maPhieuNhapThuoc;
    }

    public String getMaThuoc() {
        return maThuoc;
    }

    public String getMaLo() {
        return maLo;
    }

    public String getTenThuoc() {
        return tenThuoc;
    }

    public Integer getSoLuong() {
        return soLuong;
    }

    public Double getDonGia() {
        return donGia;
    }

    public String getDonVi() {
        return donVi;
    }

    public String getMaNSX() {
        return maNSX;
    }

    public LocalDate getNgaySanXuat() {
        return ngaySanXuat;
    }

    public LocalDate getHanSuDung() {
        return hanSuDung;
    }
}
