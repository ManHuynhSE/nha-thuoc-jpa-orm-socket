package com.pillchill.migration.service.impl;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import com.pillchill.migration.dto.ThongKeThuoc;
import com.pillchill.migration.dto.ThuocKemGiaView;
import com.pillchill.migration.entity.Thuoc;
import com.pillchill.migration.repository.IThuocRepository;
import com.pillchill.migration.repository.impl.ThuocRepository;
import com.pillchill.migration.service.IThuocService;

public class ThuocService implements IThuocService {
    private final IThuocRepository thuocRepository;

    public ThuocService(IThuocRepository thuocRepository) {
        this.thuocRepository = thuocRepository;
    }

    public ThuocService() {
        this.thuocRepository = new ThuocRepository();
    }

    @Override
    public List<Thuoc> getAllThuoc() {
        return thuocRepository.findAllActive();
    }

    @Override
    public Optional<Thuoc> getThuocById(String maThuoc) {
        return thuocRepository.findById(maThuoc);
    }

    @Override
    public List<ThuocKemGiaView> getAllThuocKemGia() {
        return thuocRepository.getAllThuocKemGia();
    }

    @Override
    public List<ThongKeThuoc> thongKeThuocTheoNgay(Date ngay, int topN) {
        return thuocRepository.thongKeThuocTheoNgay(ngay, topN);
    }

    @Override
    public List<ThongKeThuoc> thongKeThuocTheoThang(int thang, int nam, int topN) {
        return thuocRepository.thongKeThuocTheoThang(thang, nam, topN);
    }

    @Override
    public List<ThongKeThuoc> thongKeThuocTheoNam(int nam, int topN) {
        return thuocRepository.thongKeThuocTheoNam(nam, topN);
    }

    @Override
    public double getTongDoanhThuThuocTheoNgay(Date ngay) {
        return thuocRepository.getTongDoanhThuThuocTheoNgay(ngay);
    }

    @Override
    public double getTongDoanhThuThuocTheoThang(int thang, int nam) {
        return thuocRepository.getTongDoanhThuThuocTheoThang(thang, nam);
    }

    @Override
    public double getTongDoanhThuThuocTheoNam(int nam) {
        return thuocRepository.getTongDoanhThuThuocTheoNam(nam);
    }

    @Override
    public boolean addThuoc(Thuoc thuoc) {
        return thuocRepository.create(thuoc) != null;
    }

    @Override
    public boolean updateThuoc(Thuoc thuoc) {
        return thuocRepository.update(thuoc) != null;
    }

    @Override
    public boolean deleteThuoc(String maThuoc) {
        Optional<Thuoc> thuocOpt = thuocRepository.findById(maThuoc);
        if (thuocOpt.isPresent()) {
            Thuoc thuoc = thuocOpt.get();
            thuoc.setActive(false);
            return thuocRepository.update(thuoc) != null;
        }
        return false;
    }
}
