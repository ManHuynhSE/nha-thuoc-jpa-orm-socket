package com.pillchill.migration.service.impl;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import com.pillchill.migration.dto.ChiTietLoThuocView;
import com.pillchill.migration.dto.ThongKeThuoc;
import com.pillchill.migration.dto.ThuocKemGiaView;
import com.pillchill.migration.dto.ThuocTheoLoView;
import com.pillchill.migration.entity.ChiTietLoThuoc;
import com.pillchill.migration.entity.Thuoc;
import com.pillchill.migration.repository.IChiTietLoThuocRepository;
import com.pillchill.migration.repository.IGiaThuocRepository;
import com.pillchill.migration.repository.IThuocRepository;
import com.pillchill.migration.repository.impl.ChiTietLoThuocRepository;
import com.pillchill.migration.repository.impl.GiaThuocRepository;
import com.pillchill.migration.repository.impl.ThuocRepository;
import com.pillchill.migration.service.IThuocService;

public class ThuocService implements IThuocService {
    private final IThuocRepository thuocRepository;
    private final IGiaThuocRepository giaThuocRepository;
    private final IChiTietLoThuocRepository chiTietLoThuocRepository;



    public ThuocService() {
        this.chiTietLoThuocRepository = new ChiTietLoThuocRepository();
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

    @Override
    public List<ThongKeThuoc> thongKeThuocTheoNgay(Date ngay, int topN) {
        return List.of();
    }

    @Override
    public List<ThongKeThuoc> thongKeThuocTheoThang(int thang, int nam, int topN) {
        return List.of();
    }

    @Override
    public List<ThongKeThuoc> thongKeThuocTheoNam(int nam, int topN) {
        return List.of();
    }

    @Override
    public double getTongDoanhThuThuocTheoNgay(Date ngay) {
        return 0;
    }

    @Override
    public double getTongDoanhThuThuocTheoThang(int thang, int nam) {
        return 0;
    }

    @Override
    public double getTongDoanhThuThuocTheoNam(int nam) {
        return 0;
    }

    @Override
    public List<ThuocTheoLoView> getAllThuocTheoLo() {
            return chiTietLoThuocRepository.findAllActiveWithThuocAndLo()
                    .stream()
                    .map(this::toThuocTheoLoView)
                    .toList();
    }

    @Override
    public Thuoc createThuoc(Thuoc thuoc) {
        return null;
    }

    @Override
    public Thuoc createThuoc(Thuoc thuoc, double giaBanCoSo) {
        return null;
    }

    @Override
    public Thuoc updateThuoc(Thuoc thuoc) {
        return null;
    }

    @Override
    public boolean deactivateThuoc(String maThuoc) {
        return false;
    }

    @Override
    public List<ChiTietLoThuocView> getAllChiTietLoThuoc() {
        return chiTietLoThuocRepository.findAllActiveWithThuocAndLo()
                .stream()
                .map(this::toChiTietLoThuocView)
                .toList();
    }

    private ThuocTheoLoView toThuocTheoLoView(ChiTietLoThuoc chiTietLoThuoc) {
        Thuoc thuoc = chiTietLoThuoc.getThuoc();
        String maDonVi = thuoc.getDonVi() == null ? null : thuoc.getDonVi().getTenDonVi();
        String maNSX = thuoc.getNhaSanXuat() == null ? null : thuoc.getNhaSanXuat().getTenNSX();
        return new ThuocTheoLoView(
                thuoc.getMaThuoc(),
                thuoc.getTenThuoc(),
                chiTietLoThuoc.getLoThuoc().getMaLo(),
                chiTietLoThuoc.getHanSuDung(),
                chiTietLoThuoc.getSoLuong(),
                maDonVi,
                maNSX
        );
    }
    private ChiTietLoThuocView toChiTietLoThuocView(ChiTietLoThuoc chiTietLoThuoc) {
        Thuoc thuoc = chiTietLoThuoc.getThuoc();
        return new ChiTietLoThuocView(
                chiTietLoThuoc.getLoThuoc().getMaLo(),
                thuoc.getMaThuoc(),
                thuoc.getTenThuoc(),
                chiTietLoThuoc.getSoLuong(),
                chiTietLoThuoc.getGiaNhap(),
                chiTietLoThuoc.getNgaySanXuat(),
                chiTietLoThuoc.getHanSuDung()
        );
    }

}
