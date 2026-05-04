package com.pillchill.migration.network.communication;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class HoaDonCreatePayload implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String maHoaDon;
    private String ghiChu;
    private String maKhachHang;
    private String maKhuyenMai;
    private String maPhieuDat;
    private List<HoaDonCreateItemPayload> items = new ArrayList<>();

    public HoaDonCreatePayload() {
    }

    public HoaDonCreatePayload(String maHoaDon, String ghiChu, String maKhachHang, String maKhuyenMai, String maPhieuDat, List<HoaDonCreateItemPayload> items) {
        this.maHoaDon = maHoaDon;
        this.ghiChu = ghiChu;
        this.maKhachHang = maKhachHang;
        this.maKhuyenMai = maKhuyenMai;
        this.maPhieuDat = maPhieuDat;
        this.items = items;
    }

    public String getMaHoaDon() {
        return maHoaDon;
    }

    public void setMaHoaDon(String maHoaDon) {
        this.maHoaDon = maHoaDon;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public String getMaKhachHang() {
        return maKhachHang;
    }

    public void setMaKhachHang(String maKhachHang) {
        this.maKhachHang = maKhachHang;
    }

    public String getMaKhuyenMai() {
        return maKhuyenMai;
    }

    public void setMaKhuyenMai(String maKhuyenMai) {
        this.maKhuyenMai = maKhuyenMai;
    }

    public String getMaPhieuDat() {
        return maPhieuDat;
    }

    public void setMaPhieuDat(String maPhieuDat) {
        this.maPhieuDat = maPhieuDat;
    }

    public List<HoaDonCreateItemPayload> getItems() {
        return items;
    }

    public void setItems(List<HoaDonCreateItemPayload> items) {
        this.items = items;
    }
}
