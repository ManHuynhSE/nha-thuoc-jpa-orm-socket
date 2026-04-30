package com.pillchill.migration.repository.impl;

import com.pillchill.migration.dto.DoanhThuHoaDonDTO;
import com.pillchill.migration.dto.HoaDonKemGiaDTO;
import com.pillchill.migration.repository.IDoanhThuRepository;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.StoredProcedureQuery;

import java.sql.Date;
import java.util.List;

public class DoanhThuRepository extends RepositoryTemplate implements IDoanhThuRepository {


    @Override
    public double getDoanhThuTrungBinhTheoNgay(int thangDuocChon, int namDuocChon) {
        return execute(em -> {
            StoredProcedureQuery query = em.createStoredProcedureQuery("sp_GetDoanhThuTrungBinhTheoNgay");

            // Khuyến nghị: đăng ký theo vị trí để khỏi lệch tên param (pThang/pNam)
            query.registerStoredProcedureParameter(1, Integer.class, ParameterMode.IN);
            query.registerStoredProcedureParameter(2, Integer.class, ParameterMode.IN);

            query.setParameter(1, thangDuocChon);
            query.setParameter(2, namDuocChon);

            Object result = query.getSingleResult();
            if (result == null) return 0d;

            // Nếu driver trả scalar
            if (result instanceof Number n) return n.doubleValue();

            // Nếu driver trả 1 row dạng Object[]
            if (result instanceof Object[] row && row.length > 0 && row[0] instanceof Number n) {
                return n.doubleValue();
            }

            throw new RuntimeException("Kết quả sp_GetDoanhThuTrungBinhTheoNgay không hợp lệ: " + result.getClass());
        });
    }

    @Override
    public double getDoanhThuCuaThang(int thangDuocChon, int namDuocChon) {
        return execute(em -> {
            // Tạo đối tượng StoredProcedureQuery
            StoredProcedureQuery query = em.createStoredProcedureQuery("sp_GetDoanhThuCuaThang");

            // Khai báo các tham số (Parameter)
            query.registerStoredProcedureParameter("thang", Integer.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("nam", Integer.class, ParameterMode.IN);

            // Truyền giá trị vào
            query.setParameter("thang", thangDuocChon);
            query.setParameter("nam", namDuocChon);

            Object result = query.getSingleResult();
            return (result != null) ? ((Number) result).doubleValue() : 0;
        });
    }

    @Override
    public int getSoKhachHangCuaThang(int thangDuocChon, int namDuocChon) {
        return execute(em -> {
            // Tạo đối tượng StoredProcedureQuery
            StoredProcedureQuery query = em.createStoredProcedureQuery("sp_GetSoKhachHangCuaThang");

            // Khai báo các tham số (Parameter)
            query.registerStoredProcedureParameter("thang", Integer.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("nam", Integer.class, ParameterMode.IN);

            // Truyền giá trị vào
            query.setParameter("thang", thangDuocChon);
            query.setParameter("nam", namDuocChon);

            Object result = query.getSingleResult();
            return (result != null) ? ((Number) result).intValue() : 0;
        });
    }
    
    @Override
    public int getSoHoaDonTheoThang(int thang, int nam) {
        return execute(em -> {
            // Tạo đối tượng StoredProcedureQuery
            StoredProcedureQuery query = em.createStoredProcedureQuery("sp_GetSoHoaDonTheoThang");

            // Khai báo các tham số (Parameter)
            query.registerStoredProcedureParameter("thang", Integer.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("nam", Integer.class, ParameterMode.IN);

            // Truyền giá trị vào
            query.setParameter("thang", thang);
            query.setParameter("nam", nam);

            Object result = query.getSingleResult();
            return (result != null) ? ((Number) result).intValue() : 0;
        });
    }

    @Override
    public List<HoaDonKemGiaDTO> getHoaDonTrongThang(int thang, int nam) {
        return execute(em -> {
            // Tạo đối tượng StoredProcedureQuery
            StoredProcedureQuery query = em.createStoredProcedureQuery("sp_GetHoaDonTrongThang");

            // Khai báo các tham số (Parameter)
            query.registerStoredProcedureParameter("thang", Integer.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("nam", Integer.class, ParameterMode.IN);

            // Truyền giá trị vào
            query.setParameter("thang", thang);
            query.setParameter("nam", nam);


            // Ép kiểu sang List<Object[]> để Stream có thể hiểu được cấu trúc phần tử
            List<Object[]> rows = query.getResultList();

            return rows.stream()
                    .map(r -> {
                        String maHD = r[0].toString();
                        String tenNV = r[1].toString();
                        String tenKH = r[2].toString();
                        String ghiChu = r[4].toString();
                        Date ngayBan = new Date(((java.sql.Date) r[3]).getTime());
                        double doanhThu = (r[5] == null) ? 0.0 : ((Number) r[5]).doubleValue();
                        return new HoaDonKemGiaDTO(maHD,tenNV,tenKH, ngayBan,ghiChu,doanhThu);
                    })
                    .toList(); // Hoặc .collect(Collectors.toList()) nếu dùng Java cũ hơn 16
        });
    }

    @Override
    public List<HoaDonKemGiaDTO> getHoaDonTrongNam(int nam) {
        return execute(em -> {
            StoredProcedureQuery query = em.createStoredProcedureQuery("sp_GetHoaDonTrongNam");

            query.registerStoredProcedureParameter("nam", Integer.class, ParameterMode.IN);
            query.setParameter("nam", nam);

            List<Object[]> rows = query.getResultList();

            return rows.stream()
                    .map(r -> {
                        String maHD = r[0] == null ? null : r[0].toString();
                        String tenNV = r[1] == null ? null : r[1].toString();
                        String tenKH = r[2] == null ? null : r[2].toString();
                        Date ngayBan = (r[3] instanceof java.util.Date utilDate) ? new Date(utilDate.getTime()) : null;
                        String ghiChu = r[4] == null ? null : r[4].toString();
                        double doanhThu = (r[5] instanceof Number n) ? n.doubleValue() : 0d;
                        return new HoaDonKemGiaDTO(maHD, tenNV, tenKH, ngayBan, ghiChu, doanhThu);
                    })
                    .toList();
        });
    }

    @Override
    public List<Integer> getNamCoHoaDon() {
        return execute(entityManager -> 
            entityManager.createQuery("select distinct function('year', hd.ngayBan) " +
                    "from HoaDon hd " +
                    "where hd.isActive = true")
            .getResultList()
        );
    }

    @Override
    public List<Integer> getThangCoHoaDonTrongNam(int nam) {
        return execute(entityManager ->
                entityManager.createQuery("select distinct function('month', hd.ngayBan) as thang " +
                                "from HoaDon hd " +
                                "where hd.isActive = true and function('year', hd.ngayBan) = :nam ")
                        .setParameter("nam", nam)
                        .getResultList()
        );
    }

    @Override
    public List<DoanhThuHoaDonDTO> getDoanhThuTheoNgay(int thang, int nam) {
        return execute(em -> {
            StoredProcedureQuery query = em.createStoredProcedureQuery("sp_ThongKeDoanhThuTheoNgay");
            query.registerStoredProcedureParameter("pThang", Integer.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("pNam", Integer.class, ParameterMode.IN);
            query.setParameter("pThang", thang);
            query.setParameter("pNam", nam);

            // Ép kiểu sang List<Object[]> để Stream có thể hiểu được cấu trúc phần tử
            List<Object[]> rows = query.getResultList();

            return rows.stream()
                    .map(r -> {
                        int ngay = (r[0] == null) ? 0 : ((Number) r[0]).intValue();
                        double doanhThu = (r[3] == null) ? 0.0 : ((Number) r[3]).doubleValue();
                        return new DoanhThuHoaDonDTO(ngay, doanhThu);
                    })
                    .toList(); // Hoặc .collect(Collectors.toList()) nếu dùng Java cũ hơn 16
        });
    }
}
