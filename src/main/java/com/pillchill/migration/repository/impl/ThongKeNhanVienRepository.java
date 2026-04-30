package com.pillchill.migration.repository.impl;

import com.pillchill.migration.dto.DoanhThuTheoThangDTO;
import com.pillchill.migration.dto.HoaDonKemGiaDTO;
import com.pillchill.migration.dto.ThongKeNhanVienDTO;
import com.pillchill.migration.repository.IThongKeNhanVienRepository;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.StoredProcedureQuery;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ThongKeNhanVienRepository extends RepositoryTemplate implements IThongKeNhanVienRepository {

    @Override
    public List<Integer> getNamCoHoaDon() {
        return execute(em -> {
            List<?> rawYears = em.createQuery(
                            "select distinct function('year', hd.ngayBan) " +
                                    "from HoaDon hd " +
                                    "where hd.isActive = true " +
                                    "order by function('year', hd.ngayBan)")
                    .getResultList();

            List<Integer> years = new ArrayList<>();
            for (Object item : rawYears) {
                if (item instanceof Number number) {
                    years.add(number.intValue());
                }
            }
            return years;
        });
    }

    @Override
    public List<ThongKeNhanVienDTO> getThongKeDoanhThuNhanVien(int nam) {
        return execute(em -> {
            // JPQL query dùng entity relationship
            String jpql =
                    "select nv.maNV, nv.tenNV, count(distinct hd.maHoaDon), count(distinct kh.maKH), " +
                            "coalesce(sum(cthd.soLuong * cthd.donGia), 0) " +
                            "from HoaDon hd " +
                            "join hd.nhanVien nv " +
                            "left join hd.khachHang kh " +
                            "join ChiTietHoaDon cthd on cthd.hoaDon = hd " +   // hoặc on cthd.maHoaDon = hd.maHoaDon tùy mapping
                            "where hd.isActive = true " +
                            "and cthd.isActive = true " +
                            "and function('year', hd.ngayBan) = :nam " +
                            "group by nv.maNV, nv.tenNV " +
                            "order by coalesce(sum(cthd.soLuong * cthd.donGia), 0) desc";

            List<Object[]> rows = em.createQuery(jpql, Object[].class)
                    .setParameter("nam", nam)
                    .getResultList();

            System.out.println("[DEBUG] ThongKeNhanVienRepository.getThongKeDoanhThuNhanVien - nam=" + nam + ", rows=" + rows.size());

            List<ThongKeNhanVienDTO> result = new ArrayList<>();
            for (Object[] row : rows) {
                String maNV = row[0] == null ? null : row[0].toString();
                String tenNV = row[1] == null ? null : row[1].toString();
                int soLuongDonHang = (row[2] instanceof Number number) ? number.intValue() : 0;
                int soLuongKhachHang = (row[3] instanceof Number number) ? number.intValue() : 0;
                double doanhThu = (row[4] instanceof Number number) ? number.doubleValue() : 0d;
                double giaTriTrungBinhDonHang = soLuongDonHang > 0 ? doanhThu / soLuongDonHang : 0d;
                
                ThongKeNhanVienDTO dto = new ThongKeNhanVienDTO(maNV, tenNV, soLuongDonHang, soLuongKhachHang, doanhThu, giaTriTrungBinhDonHang, 0d);
                System.out.println("[DEBUG] Row: maNV=" + maNV + ", tenNV=" + tenNV + ", doanhThu=" + doanhThu);
                result.add(dto);
            }
            return result;
        });
    }

    @Override
    public List<DoanhThuTheoThangDTO> getThongKeDoanhThuNhanVienTheoThang(String maNV, int nam) {
        return execute(em -> {
            StoredProcedureQuery query = em.createStoredProcedureQuery("sp_ThongKeDoanhThuNhanVien");
            query.registerStoredProcedureParameter("pMaNV", String.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("pNam", Integer.class, ParameterMode.IN);
            query.setParameter("pMaNV", maNV);
            query.setParameter("pNam", nam);

            List<Object[]> rows = query.getResultList();
            List<DoanhThuTheoThangDTO> result = new ArrayList<>();
            for (Object[] row : rows) {
                int thang = (row[2] instanceof Number number) ? number.intValue() : 0;
                int soLuongHoaDon = (row[3] instanceof Number number) ? number.intValue() : 0;
                double doanhThu = (row[4] instanceof Number number) ? number.doubleValue() : 0d;
                result.add(new DoanhThuTheoThangDTO(thang, nam, doanhThu, soLuongHoaDon));
            }
            return result;
        });
    }

    @Override
    public List<HoaDonKemGiaDTO> getHoaDonTrongNamCuaNhanVien(int nam, String maNV) {
        return execute(em -> {
            StringBuilder jpql = new StringBuilder();
            jpql.append("select hd.maHoaDon, nv.tenNV, kh.tenKH, hd.ngayBan, hd.ghiChu, sum(cthd.soLuong * cthd.donGia) ");
            jpql.append("from HoaDon hd ");
            jpql.append("left join hd.khachHang kh ");
            jpql.append("join hd.nhanVien nv ");
            jpql.append("join ChiTietHoaDon cthd on cthd.hoaDon = hd ");
            jpql.append("where hd.isActive = true ");
            jpql.append("and cthd.isActive = true ");
            jpql.append("and function('year', hd.ngayBan) = :nam ");
            if (maNV != null && !maNV.isBlank() && !"ALL".equalsIgnoreCase(maNV)) {
                jpql.append("and nv.maNV = :maNV ");
            }
            jpql.append("group by hd.maHoaDon, nv.tenNV, kh.tenKH, hd.ngayBan, hd.ghiChu ");
            jpql.append("order by hd.ngayBan desc, hd.maHoaDon desc");

            var query = em.createQuery(jpql.toString(), Object[].class)
                    .setParameter("nam", nam);
            if (maNV != null && !maNV.isBlank() && !"ALL".equalsIgnoreCase(maNV)) {
                query = query.setParameter("maNV", maNV);
            }

            List<Object[]> rows = query.getResultList();
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

    private Date toSqlDate(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Date sqlDate) {
            return sqlDate;
        }
        if (value instanceof java.util.Date utilDate) {
            return new Date(utilDate.getTime());
        }
        if (value instanceof LocalDate localDate) {
            return Date.valueOf(localDate);
        }
        return null;
    }
}
