package com.pillchill.migration.migration;

import java.util.ArrayList;

import com.pillchill.migration.entity.ChiTietLoThuoc;
import com.pillchill.migration.entity.id.ChiTietLoThuocId;
import com.pillchill.migration.service.ITonKhoService;
import com.pillchill.migration.service.impl.TonKhoService;

public class TonKhoJpaDAO {
    private final ITonKhoService tonKhoService;

    public TonKhoJpaDAO(ITonKhoService tonKhoService) {
        this.tonKhoService = tonKhoService;
    }

    public TonKhoJpaDAO() {
        this.tonKhoService = new TonKhoService();
    }

    public ArrayList<ChiTietLoThuoc> getLoTonTheoFIFO(String maThuoc) {
        return new ArrayList<>(tonKhoService.getLoTonTheoFIFO(maThuoc));
    }

    public ChiTietLoThuoc getChiTietLo(String maLo, String maThuoc) {
        return tonKhoService.getChiTietLo(new ChiTietLoThuocId(maLo, maThuoc));
    }

    public boolean capNhatChiTietLo(ChiTietLoThuoc chiTietLoThuoc) {
        tonKhoService.capNhatChiTietLo(chiTietLoThuoc);
        return true;
    }

    public int getTongSoLuongTon(String maThuoc) {
        return tonKhoService.getTongSoLuongTon(maThuoc);
    }

    public void dongBoSoLuongTon(String maThuoc) {
        tonKhoService.dongBoSoLuongTon(maThuoc);
    }
}
