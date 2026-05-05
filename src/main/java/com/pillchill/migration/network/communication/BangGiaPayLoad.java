package com.pillchill.migration.network.communication;

import java.io.Serializable;
import java.time.LocalDate;

public class BangGiaPayLoad implements Serializable {
    private String maBangGia;
    private String tenBangGia;
    private String loaiGia;
    private LocalDate ngayApDung;
    private LocalDate ngayKetThuc;
    private String trangThai;
    private String ghiChu;
    private int doUuTien;
    private boolean isActive;

    public BangGiaPayLoad() {
    }

    public BangGiaPayLoad(String maBangGia, String tenBangGia, String loaiGia, LocalDate ngayApDung, LocalDate ngayKetThuc, String trangThai, String ghiChu, int doUuTien, boolean isActive) {
        this.maBangGia = maBangGia;
        this.tenBangGia = tenBangGia;
        this.loaiGia = loaiGia;
        this.ngayApDung = ngayApDung;
        this.ngayKetThuc = ngayKetThuc;
        this.trangThai = trangThai;
        this.ghiChu = ghiChu;
        this.doUuTien = doUuTien;
        this.isActive = isActive;
    }

    public String getMaBangGia() {
        return maBangGia;
    }

    public void setMaBangGia(String maBangGia) {
        this.maBangGia = maBangGia;
    }

    public String getTenBangGia() {
        return tenBangGia;
    }

    public void setTenBangGia(String tenBangGia) {
        this.tenBangGia = tenBangGia;
    }

    public String getLoaiGia() {
        return loaiGia;
    }

    public void setLoaiGia(String loaiGia) {
        this.loaiGia = loaiGia;
    }

    public LocalDate getNgayApDung() {
        return ngayApDung;
    }

    public void setNgayApDung(LocalDate ngayApDung) {
        this.ngayApDung = ngayApDung;
    }

    public LocalDate getNgayKetThuc() {
        return ngayKetThuc;
    }

    public void setNgayKetThuc(LocalDate ngayKetThuc) {
        this.ngayKetThuc = ngayKetThuc;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public int getDoUuTien() {
        return doUuTien;
    }

    public void setDoUuTien(int doUuTien) {
        this.doUuTien = doUuTien;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isHieuLuc() {
        if (!isActive) {
            return false;
        }
        LocalDate today = LocalDate.now();
        return (ngayApDung == null || !today.isBefore(ngayApDung)) &&
                (ngayKetThuc == null || !today.isAfter(ngayKetThuc));
    }
}
