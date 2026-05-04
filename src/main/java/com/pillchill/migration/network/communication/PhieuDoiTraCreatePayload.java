package com.pillchill.migration.network.communication;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PhieuDoiTraCreatePayload implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String maHoaDon;
    private List<PhieuDoiTraCreateItemPayload> items = new ArrayList<>();

    public PhieuDoiTraCreatePayload() {
    }

    public PhieuDoiTraCreatePayload(String maHoaDon, List<PhieuDoiTraCreateItemPayload> items) {
        this.maHoaDon = maHoaDon;
        this.items = items;
    }

    public String getMaHoaDon() {
        return maHoaDon;
    }

    public void setMaHoaDon(String maHoaDon) {
        this.maHoaDon = maHoaDon;
    }

    public List<PhieuDoiTraCreateItemPayload> getItems() {
        return items;
    }

    public void setItems(List<PhieuDoiTraCreateItemPayload> items) {
        this.items = items;
    }
}
