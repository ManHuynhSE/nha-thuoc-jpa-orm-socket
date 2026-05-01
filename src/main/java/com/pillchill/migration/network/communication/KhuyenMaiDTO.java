package com.pillchill.migration.network.communication;

import java.io.Serializable;
import java.time.LocalDate;

public class KhuyenMaiDTO implements Serializable {
    private String maKM;
    private double mucGiamGia;
    private LocalDate ngayApDung;
    private LocalDate ngayKetThuc;
    private boolean isActive;

    public KhuyenMaiDTO() {}

    public KhuyenMaiDTO(String maKM, double mucGiamGia, LocalDate ngayApDung, LocalDate ngayKetThuc, boolean isActive) {
        this.maKM = maKM;
        this.mucGiamGia = mucGiamGia;
        this.ngayApDung = ngayApDung;
        this.ngayKetThuc = ngayKetThuc;
        this.isActive = isActive;
    }

    public String getMaKM() {
        return maKM;
    }

    public void setMaKM(String maKM) {
        this.maKM = maKM;
    }

    public double getMucGiamGia() {
        return mucGiamGia;
    }

    public void setMucGiamGia(double mucGiamGia) {
        this.mucGiamGia = mucGiamGia;
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
