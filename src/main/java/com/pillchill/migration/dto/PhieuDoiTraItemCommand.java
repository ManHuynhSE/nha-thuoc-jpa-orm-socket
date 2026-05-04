package com.pillchill.migration.dto;

public record PhieuDoiTraItemCommand(
        String maThuoc,
        String maLo,
        int soLuong,
        double donGia,
        String lyDo
) {
}
