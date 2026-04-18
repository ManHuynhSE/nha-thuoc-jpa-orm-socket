package com.pillchill.migration.service;

import com.pillchill.migration.dto.CreateHoaDonCommand;
import com.pillchill.migration.entity.ChiTietLoThuoc;
import com.pillchill.migration.entity.HoaDon;

public interface IHoaDonService {
    HoaDon createHoaDon(CreateHoaDonCommand command);

    ChiTietLoThuoc getLot(String maLo, String maThuoc);
}
