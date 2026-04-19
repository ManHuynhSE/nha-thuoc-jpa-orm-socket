package com.pillchill.migration.service.impl;

import java.util.List;
import java.util.Optional;

import com.pillchill.migration.dto.ThuocKemGiaView;
import com.pillchill.migration.entity.Thuoc;
import com.pillchill.migration.repository.IGiaThuocRepository;
import com.pillchill.migration.repository.IThuocRepository;
import com.pillchill.migration.repository.impl.GiaThuocRepository;
import com.pillchill.migration.repository.impl.ThuocRepository;
import com.pillchill.migration.service.IThuocService;

public class ThuocService implements IThuocService {
    private final IThuocRepository thuocRepository;
    private final IGiaThuocRepository giaThuocRepository;

    public ThuocService(IThuocRepository thuocRepository, IGiaThuocRepository giaThuocRepository) {
        this.thuocRepository = thuocRepository;
        this.giaThuocRepository = giaThuocRepository;
    }

    public ThuocService() {
        this.thuocRepository = new ThuocRepository();
        this.giaThuocRepository = new GiaThuocRepository();
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
        return thuocRepository.findAllActive()
                .stream()
                .map(this::toThuocKemGiaView)
                .toList();
    }

    private ThuocKemGiaView toThuocKemGiaView(Thuoc thuoc) {
        double gia = giaThuocRepository.getGiaHienTaiByMaThuoc(thuoc.getMaThuoc()).orElse(0.0);
        String maDonVi = thuoc.getDonVi() == null ? null : thuoc.getDonVi().getMaDonVi();
        String maNSX = thuoc.getNhaSanXuat() == null ? null : thuoc.getNhaSanXuat().getMaNSX();
        return new ThuocKemGiaView(
                thuoc.getMaThuoc(),
                thuoc.getTenThuoc(),
                thuoc.getSoLuongTon(),
                gia,
                maDonVi,
                thuoc.getSoLuongToiThieu() == null ? 0 : thuoc.getSoLuongToiThieu(),
                maNSX,
                thuoc.isActive()
        );
    }
}
