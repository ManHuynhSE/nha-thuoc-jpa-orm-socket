package com.pillchill.migration.dto;

import java.io.Serializable;

public class ThongKeKhachHangDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String maKH;
    private String tenKH;
    private String soDienThoai;
    private double tongTien;

    public ThongKeKhachHangDTO() {
    }

    public ThongKeKhachHangDTO(String maKH, String tenKH, String soDienThoai, double tongTien) {
        this.maKH = maKH;
        this.tenKH = tenKH;
        this.soDienThoai = soDienThoai;
        this.tongTien = tongTien;
    }

    public String getMaKH() { return maKH; }
    public void setMaKH(String maKH) { this.maKH = maKH; }

    public String getTenKH() { return tenKH; }
    public void setTenKH(String tenKH) { this.tenKH = tenKH; }

    public String getSoDienThoai() { return soDienThoai; }
    public void setSoDienThoai(String soDienThoai) { this.soDienThoai = soDienThoai; }

    public double getTongTien() { return tongTien; }
    public void setTongTien(double tongTien) { this.tongTien = tongTien; }
}
