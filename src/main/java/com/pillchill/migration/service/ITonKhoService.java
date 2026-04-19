package com.pillchill.migration.service;

import java.util.List;

import com.pillchill.migration.entity.ChiTietLoThuoc;
import com.pillchill.migration.entity.id.ChiTietLoThuocId;

public interface ITonKhoService {
    List<ChiTietLoThuoc> getLoTonTheoFIFO(String maThuoc);

    ChiTietLoThuoc getChiTietLo(ChiTietLoThuocId id);

    void capNhatChiTietLo(ChiTietLoThuoc chiTietLoThuoc);

    int getTongSoLuongTon(String maThuoc);

    void dongBoSoLuongTon(String maThuoc);
}
