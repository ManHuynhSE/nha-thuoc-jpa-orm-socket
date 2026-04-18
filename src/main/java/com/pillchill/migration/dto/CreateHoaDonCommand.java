package com.pillchill.migration.dto;

import java.time.LocalDate;
import java.util.List;

public record CreateHoaDonCommand(
        String maHoaDon,
        LocalDate ngayBan,
        String ghiChu,
        String maNV,
        String maKH,
        String maKM,
        double giaTriThue,
        String tenLoaiThue,
        List<HoaDonItemCommand> items
) {
}
