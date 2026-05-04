package com.pillchill.migration.dto;

import java.util.List;

public record CreatePhieuDatCommand(
        String maKhachHang,
        String ghiChu,
        List<PhieuDatItemCommand> items
) {
}
