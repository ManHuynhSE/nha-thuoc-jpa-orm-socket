package com.pillchill.migration.repository;

import com.pillchill.migration.dto.ThongKeThuoc;
import com.pillchill.migration.entity.Thuoc;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

public interface IThuocRepository extends GenericRepository<Thuoc,String> {
    List<Thuoc> findAllActive();

    Optional<Thuoc> findById(String maThuoc);

    long countActive();

    void updateSoLuongTon(String maThuoc, int soLuongTon);

    List<ThongKeThuoc> thongKeThuocTheoNgay(Date ngay, int topN);

    List<ThongKeThuoc> thongKeThuocTheoThang(int thang, int nam, int topN);

    List<ThongKeThuoc> thongKeThuocTheoNam(int nam, int topN);

    double getTongDoanhThuThuocTheoNgay(Date ngay);

    double getTongDoanhThuThuocTheoThang(int thang, int nam);

    double getTongDoanhThuThuocTheoNam(int nam);
}
