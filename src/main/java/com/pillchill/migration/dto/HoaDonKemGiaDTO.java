package com.pillchill.migration.dto;

import java.io.Serializable;
import java.sql.Date;


public class HoaDonKemGiaDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private String maHoaDon;
	private String tenNV;
	private String tenKH;
	private Date ngayBan;
	private String ghiChu;
	private double tongTien;
	
    public HoaDonKemGiaDTO(String maHD, String tenNV, String tenKH, Date ngayBan, String ghiChu, double tongTien) {
        this.maHoaDon = maHD;
        this.tenNV = tenNV;
        this.tenKH = tenKH;
        this.ngayBan = ngayBan;
        this.ghiChu = ghiChu;
        this.tongTien = tongTien;
	}

	public String getMaHoaDon() {
		return maHoaDon;
	}

	public void setMaHoaDon(String maHoaDon) {
		this.maHoaDon = maHoaDon;
	}

	public String getTenNV() {
		return tenNV;
	}

	public void setTenNV(String tenNV) {
		this.tenNV = tenNV;
	}

	public String getTenKH() {
		return tenKH;
	}

	public void setTenKH(String tenKH) {
		this.tenKH = tenKH;
	}

	public Date getNgayBan() {
		return ngayBan;
	}

	public void setNgayBan(Date ngayBan) {
		this.ngayBan = ngayBan;
	}

	public String getGhiChu() {
		return ghiChu;
	}

	public void setGhiChu(String ghiChu) {
		this.ghiChu = ghiChu;
	}

	public double getTongTien() {
		return tongTien;
	}

	public void setTongTien(double tongTien) {
		this.tongTien = tongTien;
	}

	@Override
	public String toString() {
		return "HoaDonKemGiaDTO [maHoaDon=" + maHoaDon + ", tenNV=" + tenNV + ", tenKH=" + tenKH + ", ngayBan="
				+ ngayBan + ", ghiChu=" + ghiChu + ", tongTien=" + tongTien + "]";
	}
	
	
    
    
}