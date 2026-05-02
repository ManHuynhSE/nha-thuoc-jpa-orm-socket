package com.pillchill.migration.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;


public class LoThuocHetHan implements Serializable {
    private static final long serialVersionUID = 1L;

    private String maLo;
    private String maThuoc;
    private String tenThuoc;
    private Date ngaySanXuat;
    private Date hanSuDung;
    private int soLuongTon;
    private int soNgayDaHetHan;
    /**
     * Constructor mặc định.
     */
    public LoThuocHetHan() {
    }
    

    public LoThuocHetHan(String maLo, String maThuoc, String tenThuoc, 
                         Date ngaySanXuat, Date hanSuDung, int soLuongTon, int soNgayDaHetHan) {
        this.maLo = maLo;
        this.maThuoc = maThuoc;
        this.tenThuoc = tenThuoc;
        this.ngaySanXuat = ngaySanXuat;
        this.hanSuDung = hanSuDung;
        this.soLuongTon = soLuongTon;
        this.soNgayDaHetHan = soNgayDaHetHan;
    }
    

    public String getMaLo() {
        return maLo;
    }
    

    public void setMaLo(String maLo) {
        this.maLo = maLo;
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
    

    public Date getNgaySanXuat() {
        return ngaySanXuat;
    }
    

    public void setNgaySanXuat(Date ngaySanXuat) {
        this.ngaySanXuat = ngaySanXuat;
    }
    

    public Date getHanSuDung() {
        return hanSuDung;
    }
    

    public void setHanSuDung(Date hanSuDung) {
        this.hanSuDung = hanSuDung;
    }
    

    public int getSoLuongTon() {
        return soLuongTon;
    }
    

    public void setSoLuongTon(int soLuongTon) {
        this.soLuongTon = soLuongTon;
    }
    
    
    public int getSoNgayDaHetHan() {
		return soNgayDaHetHan;
	}


	public void setSoNgayDaHetHan(int soNgayDaHetHan) {
		this.soNgayDaHetHan = soNgayDaHetHan;
	}


	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoThuocHetHan that = (LoThuocHetHan) o;
        return Objects.equals(maLo, that.maLo) && 
               Objects.equals(maThuoc, that.maThuoc);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(maLo, maThuoc);
    }
    
    @Override
    public String toString() {
        return "LoThuocHetHan{" +
                "maLo='" + maLo + '\'' +
                ", maThuoc='" + maThuoc + '\'' +
                ", tenThuoc='" + tenThuoc + '\'' +
                ", ngaySanXuat=" + ngaySanXuat +
                ", hanSuDung=" + hanSuDung +
                ", soLuongTon=" + soLuongTon +
                '}';
    }
}