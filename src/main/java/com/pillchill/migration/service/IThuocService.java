package com.pillchill.migration.service;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import com.pillchill.migration.dto.ThongKeThuoc;
import com.pillchill.migration.dto.ThuocKemGiaView;
import com.pillchill.migration.entity.Thuoc;

public interface IThuocService {
    List<Thuoc> getAllThuoc();

    Optional<Thuoc> getThuocById(String maThuoc);

    List<ThuocKemGiaView> getAllThuocKemGia();

    List<ThongKeThuoc> thongKeThuocTheoNgay(Date ngay, int topN);

    List<ThongKeThuoc> thongKeThuocTheoThang(int thang, int nam, int topN);

    List<ThongKeThuoc> thongKeThuocTheoNam(int nam, int topN);

    double getTongDoanhThuThuocTheoNgay(Date ngay);

    double getTongDoanhThuThuocTheoThang(int thang, int nam);

    double getTongDoanhThuThuocTheoNam(int nam);
}
