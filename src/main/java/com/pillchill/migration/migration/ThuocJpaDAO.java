package com.pillchill.migration.migration;

import java.util.ArrayList;
import java.util.stream.Collectors;

import com.pillchill.migration.dto.ThuocKemGiaView;
import com.pillchill.migration.entity.Thuoc;
import com.pillchill.migration.repository.IGiaThuocRepository;
import com.pillchill.migration.repository.IThuocRepository;
import com.pillchill.migration.repository.impl.GiaThuocRepository;
import com.pillchill.migration.repository.impl.ThuocRepository;

public class ThuocJpaDAO {
    private final IThuocRepository thuocRepository;
    private final IGiaThuocRepository giaThuocRepository;

    public ThuocJpaDAO(IThuocRepository thuocRepository, IGiaThuocRepository giaThuocRepository) {
        this.thuocRepository = thuocRepository;
        this.giaThuocRepository = giaThuocRepository;
    }

    public ThuocJpaDAO() {
        this.thuocRepository = new ThuocRepository();
        this.giaThuocRepository = new GiaThuocRepository();
    }

    public ArrayList<Thuoc> getAllThuoc() {
        return new ArrayList<>(thuocRepository.findAllActive());
    }

    public Thuoc getThuocById(String id) {
        return thuocRepository.findById(id).orElse(null);
    }

    public ArrayList<ThuocKemGiaView> getAllThuocKemGia() {
        return thuocRepository.findAllActive()
                .stream()
                .map(this::toThuocKemGiaView)
                .collect(Collectors.toCollection(ArrayList::new));
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
