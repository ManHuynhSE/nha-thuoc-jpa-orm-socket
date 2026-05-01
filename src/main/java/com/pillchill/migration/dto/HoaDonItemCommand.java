package com.pillchill.migration.dto;

public record HoaDonItemCommand(
        String maThuoc,
        String maLo,
        int soLuong,
        float donGia
) {
    public HoaDonItemCommand(String maThuoc, int soLuong, float donGia) {
        this(maThuoc, null, soLuong, donGia);
    }
}
