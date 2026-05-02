package com.pillchill.migration.repository.impl;

import com.pillchill.migration.dto.HoaDonKemGiaDTO;
import com.pillchill.migration.dto.ThongKeKhachHangDTO;
import com.pillchill.migration.repository.IThongKeKhachHangRepository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ThongKeKhachHangRepository extends RepositoryTemplate implements IThongKeKhachHangRepository {

    @Override
    public List<ThongKeKhachHangDTO> getDoanhThuTatCaKhachHang() {
        return execute(em -> {
            String jpql =
                    "select kh.maKH, kh.tenKH, kh.soDienThoai, " +
                            "coalesce(sum(cthd.soLuong * cthd.donGia), 0) " +
                            "from KhachHang kh " +
                            "left join HoaDon hd on hd.khachHang = kh and hd.isActive = true " +
                            "left join ChiTietHoaDon cthd on cthd.hoaDon = hd and cthd.isActive = true " +
                            "where kh.isActive = true " +
                            "group by kh.maKH, kh.tenKH, kh.soDienThoai " +
                            "order by kh.tenKH";

            List<Object[]> rows = em.createQuery(jpql, Object[].class).getResultList();
            return mapToThongKeList(rows);
        });
    }

    @Override
    public List<ThongKeKhachHangDTO> getDoanhThuKhachHangTheoThangNam(int thang, int nam) {
        return execute(em -> {
            String jpql =
                    "select kh.maKH, kh.tenKH, kh.soDienThoai, " +
                            "coalesce(sum(cthd.soLuong * cthd.donGia), 0) " +
                            "from KhachHang kh " +
                            "join HoaDon hd on hd.khachHang = kh " +
                            "join ChiTietHoaDon cthd on cthd.hoaDon = hd " +
                            "where kh.isActive = true " +
                            "and hd.isActive = true " +
                            "and cthd.isActive = true " +
                            "and function('month', hd.ngayBan) = :thang " +
                            "and function('year', hd.ngayBan) = :nam " +
                            "group by kh.maKH, kh.tenKH, kh.soDienThoai " +
                            "having sum(cthd.soLuong * cthd.donGia) > 0 " +
                            "order by kh.tenKH";

            List<Object[]> rows = em.createQuery(jpql, Object[].class)
                    .setParameter("thang", thang)
                    .setParameter("nam", nam)
                    .getResultList();
            return mapToThongKeList(rows);
        });
    }

    @Override
    public List<ThongKeKhachHangDTO> timKiemTheoMaKH(String maKH) {
        return execute(em -> {
            String jpql =
                    "select kh.maKH, kh.tenKH, kh.soDienThoai, " +
                            "coalesce(sum(cthd.soLuong * cthd.donGia), 0) " +
                            "from KhachHang kh " +
                            "left join HoaDon hd on hd.khachHang = kh and hd.isActive = true " +
                            "left join ChiTietHoaDon cthd on cthd.hoaDon = hd and cthd.isActive = true " +
                            "where kh.maKH = :maKH " +
                            "group by kh.maKH, kh.tenKH, kh.soDienThoai";

            List<Object[]> rows = em.createQuery(jpql, Object[].class)
                    .setParameter("maKH", maKH)
                    .getResultList();
            return mapToThongKeList(rows);
        });
    }

    @Override
    public List<ThongKeKhachHangDTO> timKiemTheoTenKH(String tenKH) {
        return execute(em -> {
            String jpql =
                    "select kh.maKH, kh.tenKH, kh.soDienThoai, " +
                            "coalesce(sum(cthd.soLuong * cthd.donGia), 0) " +
                            "from KhachHang kh " +
                            "left join HoaDon hd on hd.khachHang = kh and hd.isActive = true " +
                            "left join ChiTietHoaDon cthd on cthd.hoaDon = hd and cthd.isActive = true " +
                            "where kh.isActive = true " +
                            "and (kh.tenKH like :keyword or kh.soDienThoai like :keyword) " +
                            "group by kh.maKH, kh.tenKH, kh.soDienThoai " +
                            "order by kh.tenKH";

            List<Object[]> rows = em.createQuery(jpql, Object[].class)
                    .setParameter("keyword", "%" + tenKH + "%")
                    .getResultList();
            return mapToThongKeList(rows);
        });
    }

    @Override
    public List<ThongKeKhachHangDTO> timKiemTheoSoDienThoai(String soDienThoai) {
        return execute(em -> {
            String jpql =
                    "select kh.maKH, kh.tenKH, kh.soDienThoai, " +
                            "coalesce(sum(cthd.soLuong * cthd.donGia), 0) " +
                            "from KhachHang kh " +
                            "left join HoaDon hd on hd.khachHang = kh and hd.isActive = true " +
                            "left join ChiTietHoaDon cthd on cthd.hoaDon = hd and cthd.isActive = true " +
                            "where kh.soDienThoai = :soDienThoai " +
                            "group by kh.maKH, kh.tenKH, kh.soDienThoai";

            List<Object[]> rows = em.createQuery(jpql, Object[].class)
                    .setParameter("soDienThoai", soDienThoai.trim())
                    .getResultList();
            return mapToThongKeList(rows);
        });
    }

    @Override
    public List<HoaDonKemGiaDTO> getHoaDonTheoKhachHang(String maKH) {
        return execute(em -> {
            String jpql =
                    "select hd.maHoaDon, nv.tenNV, kh.tenKH, hd.ngayBan, hd.ghiChu, " +
                            "sum(cthd.soLuong * cthd.donGia) " +
                            "from HoaDon hd " +
                            "join hd.nhanVien nv " +
                            "join hd.khachHang kh " +
                            "join ChiTietHoaDon cthd on cthd.hoaDon = hd " +
                            "where hd.isActive = true " +
                            "and cthd.isActive = true " +
                            "and kh.maKH = :maKH " +
                            "group by hd.maHoaDon, nv.tenNV, kh.tenKH, hd.ngayBan, hd.ghiChu " +
                            "order by hd.ngayBan desc, hd.maHoaDon desc";

            List<Object[]> rows = em.createQuery(jpql, Object[].class)
                    .setParameter("maKH", maKH)
                    .getResultList();

            List<HoaDonKemGiaDTO> result = new ArrayList<>();
            for (Object[] row : rows) {
                String maHoaDon = row[0] == null ? null : row[0].toString();
                String tenNV = row[1] == null ? null : row[1].toString();
                String tenKH = row[2] == null ? null : row[2].toString();
                Date ngayBan = toSqlDate(row[3]);
                String ghiChu = row[4] == null ? null : row[4].toString();
                double tongTien = (row[5] instanceof Number number) ? number.doubleValue() : 0d;
                result.add(new HoaDonKemGiaDTO(maHoaDon, tenNV, tenKH, ngayBan, ghiChu, tongTien));
            }
            return result;
        });
    }

    private List<ThongKeKhachHangDTO> mapToThongKeList(List<Object[]> rows) {
        List<ThongKeKhachHangDTO> result = new ArrayList<>();
        for (Object[] row : rows) {
            String maKH = row[0] == null ? null : row[0].toString();
            String tenKH = row[1] == null ? null : row[1].toString();
            String soDienThoai = row[2] == null ? null : row[2].toString();
            double tongTien = (row[3] instanceof Number number) ? number.doubleValue() : 0d;
            result.add(new ThongKeKhachHangDTO(maKH, tenKH, soDienThoai, tongTien));
        }
        return result;
    }

    private Date toSqlDate(Object value) {
        if (value == null) return null;
        if (value instanceof Date sqlDate) return sqlDate;
        if (value instanceof java.util.Date utilDate) return new Date(utilDate.getTime());
        if (value instanceof LocalDate localDate) return Date.valueOf(localDate);
        return null;
    }
}
