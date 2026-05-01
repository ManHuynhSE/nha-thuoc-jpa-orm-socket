package com.pillchill.migration.migration;

import com.pillchill.migration.dto.ThongKeThuoc;
import com.pillchill.migration.dto.ThuocKemGiaView;
import com.pillchill.migration.entity.Thuoc;
import com.pillchill.migration.service.IThuocService;
import com.pillchill.migration.service.impl.ThuocService;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class ThuocJpaDAO {
    private final IThuocService thuocService;

    public ThuocJpaDAO() {
        this.thuocService = new ThuocService();
    }

    public ArrayList<Thuoc> getAllThuoc() {
        return new ArrayList<>(thuocService.getAllThuoc());
    }

    public Thuoc getThuocById(String id) {
        return thuocService.getThuocById(id).orElse(null);
    }

    public ArrayList<ThuocKemGiaView> getAllThuocKemGia() {
        return new ArrayList<>(thuocService.getAllThuocKemGia());
    }

    public ArrayList<ThongKeThuoc> thongKeThuocTheoNgay(Date ngay, int topN) {
        return new ArrayList<>(thuocService.thongKeThuocTheoNgay(ngay, topN));
    }

    public ArrayList<ThongKeThuoc> thongKeThuocTheoThang(int thang, int nam, int topN) {
        return new ArrayList<>(thuocService.thongKeThuocTheoThang(thang, nam, topN));
    }

    public ArrayList<ThongKeThuoc> thongKeThuocTheoNam(int nam, int topN) {
        return new ArrayList<>(thuocService.thongKeThuocTheoNam(nam, topN));
    }

    public double getTongDoanhThuThuocTheoNgay(Date ngay) {
        return thuocService.getTongDoanhThuThuocTheoNgay(ngay);
    }

    public double getTongDoanhThuThuocTheoThang(int thang, int nam) {
        return thuocService.getTongDoanhThuThuocTheoThang(thang, nam);
    }

    public double getTongDoanhThuThuocTheoNam(int nam) {
        return thuocService.getTongDoanhThuThuocTheoNam(nam);
    }

    public boolean addThuoc(Thuoc thuoc) {
        return thuocService.addThuoc(thuoc);
    }

    public boolean updateThuoc(Thuoc thuoc) {
        return thuocService.updateThuoc(thuoc);
    }

    public boolean deleteThuoc(String maThuoc) {
        return thuocService.deleteThuoc(maThuoc);
    }
}
