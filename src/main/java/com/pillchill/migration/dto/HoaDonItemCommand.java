package com.pillchill.migration.dto;

public record HoaDonItemCommand(
        String maThuoc,
        int soLuong,
        float donGia
) {
}
