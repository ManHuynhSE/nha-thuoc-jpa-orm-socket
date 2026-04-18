package com.pillchill.migration.dto;

public record ThuocKemGiaView(
        String maThuoc,
        String tenThuoc,
        int soLuongTon,
        double giaBan,
        String donVi,
        int soLuongToiThieu,
        String maNSX,
        boolean isActive
) {
}
