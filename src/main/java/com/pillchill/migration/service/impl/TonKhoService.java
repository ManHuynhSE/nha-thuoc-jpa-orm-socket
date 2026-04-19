package com.pillchill.migration.service.impl;

import java.util.List;

import com.pillchill.migration.entity.ChiTietLoThuoc;
import com.pillchill.migration.entity.id.ChiTietLoThuocId;
import com.pillchill.migration.repository.IChiTietLoThuocRepository;
import com.pillchill.migration.repository.IThuocRepository;
import com.pillchill.migration.repository.impl.ChiTietLoThuocRepository;
import com.pillchill.migration.repository.impl.ThuocRepository;
import com.pillchill.migration.service.ITonKhoService;

public class TonKhoService implements ITonKhoService {
    private final IChiTietLoThuocRepository chiTietLoThuocRepository;
    private final IThuocRepository thuocRepository;

    public TonKhoService(IChiTietLoThuocRepository chiTietLoThuocRepository, IThuocRepository thuocRepository) {
        this.chiTietLoThuocRepository = chiTietLoThuocRepository;
        this.thuocRepository = thuocRepository;
    }

    public TonKhoService() {
        this.chiTietLoThuocRepository = new ChiTietLoThuocRepository();
        this.thuocRepository = new ThuocRepository();
    }

    @Override
    public List<ChiTietLoThuoc> getLoTonTheoFIFO(String maThuoc) {
        return chiTietLoThuocRepository.findActiveByMaThuocOrderByNgaySanXuat(maThuoc);
    }

    @Override
    public ChiTietLoThuoc getChiTietLo(ChiTietLoThuocId id) {
        return chiTietLoThuocRepository.getReference(id);
    }

    @Override
    public void capNhatChiTietLo(ChiTietLoThuoc chiTietLoThuoc) {
        chiTietLoThuocRepository.save(chiTietLoThuoc);
        dongBoSoLuongTon(chiTietLoThuoc.getId().getMaThuoc());
    }

    @Override
    public int getTongSoLuongTon(String maThuoc) {
        return chiTietLoThuocRepository.sumSoLuongByMaThuoc(maThuoc);
    }

    @Override
    public void dongBoSoLuongTon(String maThuoc) {
        thuocRepository.updateSoLuongTon(maThuoc, getTongSoLuongTon(maThuoc));
    }
}
