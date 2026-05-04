package com.pillchill.migration.network.communication;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PhieuDatCreatePayload implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String maKhachHang;
    private String ghiChu;
    private List<PhieuDatCreateItemPayload> items = new ArrayList<>();

    public PhieuDatCreatePayload() {
    }

    public PhieuDatCreatePayload(String maKhachHang, String ghiChu, List<PhieuDatCreateItemPayload> items) {
        this.maKhachHang = maKhachHang;
        this.ghiChu = ghiChu;
        this.items = items;
    }

    public String getMaKhachHang() {
        return maKhachHang;
    }

    public void setMaKhachHang(String maKhachHang) {
        this.maKhachHang = maKhachHang;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public List<PhieuDatCreateItemPayload> getItems() {
        return items;
    }

    public void setItems(List<PhieuDatCreateItemPayload> items) {
        this.items = items;
    }
}
