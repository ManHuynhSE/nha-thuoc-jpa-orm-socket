package com.pillchill.migration.dto;

import java.io.Serializable;

public class ThongKeNhanVienDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String maNV;
    private String tenNV;
    private int soLuongDonHang;
    private int soLuongKhachHang;
    private double doanhThu;
    private double giaTriTrungBinhDonHang;
    private double tyLeDongGop; // %

    public ThongKeNhanVienDTO(String maNV, String tenNV, int soLuongDonHang,
                              int soLuongKhachHang, double doanhThu,
                              double giaTriTrungBinhDonHang, double tyLeDongGop) {
        this.maNV = maNV;
        this.tenNV = tenNV;
        this.soLuongDonHang = soLuongDonHang;
        this.soLuongKhachHang = soLuongKhachHang;
        this.doanhThu = doanhThu;
        this.giaTriTrungBinhDonHang = giaTriTrungBinhDonHang;
        this.tyLeDongGop = tyLeDongGop;
    }

    public String getMaNV() { return maNV; }
    public void setMaNV(String maNV) { this.maNV = maNV; }

    public String getTenNV() { return tenNV; }
    public void setTenNV(String tenNV) { this.tenNV = tenNV; }

    public int getSoLuongDonHang() { return soLuongDonHang; }
    public void setSoLuongDonHang(int soLuongDonHang) { this.soLuongDonHang = soLuongDonHang; }

    public int getSoLuongKhachHang() { return soLuongKhachHang; }
    public void setSoLuongKhachHang(int soLuongKhachHang) { this.soLuongKhachHang = soLuongKhachHang; }

    public double getDoanhThu() { return doanhThu; }
    public void setDoanhThu(double doanhThu) { this.doanhThu = doanhThu; }

    public double getGiaTriTrungBinhDonHang() { return giaTriTrungBinhDonHang; }
    public void setGiaTriTrungBinhDonHang(double giaTriTrungBinhDonHang) {
        this.giaTriTrungBinhDonHang = giaTriTrungBinhDonHang;
    }

    public double getTyLeDongGop() { return tyLeDongGop; }
    public void setTyLeDongGop(double tyLeDongGop) { this.tyLeDongGop = tyLeDongGop; }
}