package com.pillchill.migration.dto;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

public class PhieuNhapImportItem implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String maThuoc;
    private String maLo;
    private String tenThuoc;
    private Integer soLuong;
    private Double giaNhap;
    private String maDonVi;
    private Integer soLuongToiThieu;
    private String maNSX;
    private LocalDate ngaySanXuat;
    private LocalDate hanSuDung;

    public PhieuNhapImportItem() {
    }

    public PhieuNhapImportItem(
            String maThuoc,
            String maLo,
            String tenThuoc,
            Integer soLuong,
            Double giaNhap,
            String maDonVi,
            Integer soLuongToiThieu,
            String maNSX,
            LocalDate ngaySanXuat,
            LocalDate hanSuDung
    ) {
        this.maThuoc = maThuoc;
        this.maLo = maLo;
        this.tenThuoc = tenThuoc;
        this.soLuong = soLuong;
        this.giaNhap = giaNhap;
        this.maDonVi = maDonVi;
        this.soLuongToiThieu = soLuongToiThieu;
        this.maNSX = maNSX;
        this.ngaySanXuat = ngaySanXuat;
        this.hanSuDung = hanSuDung;
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

    public String getTenThuoc() {
        return tenThuoc;
    }

    public void setTenThuoc(String tenThuoc) {
        this.tenThuoc = tenThuoc;
    }

    public Integer getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(Integer soLuong) {
        this.soLuong = soLuong;
    }

    public Double getGiaNhap() {
        return giaNhap;
    }

    public void setGiaNhap(Double giaNhap) {
        this.giaNhap = giaNhap;
    }

    public String getMaDonVi() {
        return maDonVi;
    }

    public void setMaDonVi(String maDonVi) {
        this.maDonVi = maDonVi;
    }

    public Integer getSoLuongToiThieu() {
        return soLuongToiThieu;
    }

    public void setSoLuongToiThieu(Integer soLuongToiThieu) {
        this.soLuongToiThieu = soLuongToiThieu;
    }

    public String getMaNSX() {
        return maNSX;
    }

    public void setMaNSX(String maNSX) {
        this.maNSX = maNSX;
    }

    public LocalDate getNgaySanXuat() {
        return ngaySanXuat;
    }

    public void setNgaySanXuat(LocalDate ngaySanXuat) {
        this.ngaySanXuat = ngaySanXuat;
    }

    public LocalDate getHanSuDung() {
        return hanSuDung;
    }

    public void setHanSuDung(LocalDate hanSuDung) {
        this.hanSuDung = hanSuDung;
    }
}
