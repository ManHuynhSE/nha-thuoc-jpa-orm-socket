package com.pillchill.migration.dto;

import java.util.List;

public record CreatePhieuDoiTraCommand(
        String maHoaDon,
        List<PhieuDoiTraItemCommand> items
) {
}
