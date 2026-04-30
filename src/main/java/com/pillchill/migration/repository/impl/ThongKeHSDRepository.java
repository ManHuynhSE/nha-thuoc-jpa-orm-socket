package com.pillchill.migration.repository.impl;

import com.pillchill.migration.dto.LoThuocHetHan;
import com.pillchill.migration.repository.IThongKeHSDRepository;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.StoredProcedureQuery;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ThongKeHSDRepository extends RepositoryTemplate implements IThongKeHSDRepository {

    @Override
    public List<LoThuocHetHan> getCacLoThuocHetHan() {
        return execute(em -> {
            StoredProcedureQuery query = em.createStoredProcedureQuery("sp_GetLoThuocDaHetHan");
            List<Object[]> rows = query.getResultList();

            List<LoThuocHetHan> result = new ArrayList<>();
            for (Object[] row : rows) {
                result.add(mapRow(row));
            }
            return result;
        });
    }

    @Override
    public List<LoThuocHetHan> getCacLoThuocSapHetHan(int soNgay) {
        return execute(em -> {
            StoredProcedureQuery query = em.createStoredProcedureQuery("sp_GetLoThuocSapHetHan");
            query.registerStoredProcedureParameter("pSoNgay", Integer.class, ParameterMode.IN);
            query.setParameter("pSoNgay", soNgay);

            List<Object[]> rows = query.getResultList();

            List<LoThuocHetHan> result = new ArrayList<>();
            for (Object[] row : rows) {
                result.add(mapRow(row));
            }
            return result;
        });
    }

    @Override
    public boolean xoaChiTietLoThuoc(String maLo, String maThuoc) {
        return execute(em -> {
            int deleted = em.createQuery(
                            "delete from ChiTietLoThuoc ct where ct.id.maLo = :maLo and ct.id.maThuoc = :maThuoc")
                    .setParameter("maLo", maLo)
                    .setParameter("maThuoc", maThuoc)
                    .executeUpdate();
            return deleted > 0;
        });
    }

    /**
     * Maps a stored procedure result row to LoThuocHetHan.
     * SP columns (both sp_GetLoThuocDaHetHan and sp_GetLoThuocSapHetHan):
     *   [0] maLo, [1] maThuoc, [2] tenThuoc, [3] ngaySanXuat, [4] hanSuDung,
     *   [5] soLuongTon, [6] soNgayDaHetHan / soNgayConLai
     */
    private LoThuocHetHan mapRow(Object[] row) {
        String maLo = row[0] == null ? null : row[0].toString();
        String maThuoc = row[1] == null ? null : row[1].toString();
        String tenThuoc = row[2] == null ? null : row[2].toString();
        java.util.Date ngaySanXuat = toUtilDate(row[3]);
        java.util.Date hanSuDung = toUtilDate(row[4]);
        int soLuongTon = (row[5] instanceof Number number) ? number.intValue() : 0;
        int soNgay = (row[6] instanceof Number number) ? number.intValue() : 0;
        return new LoThuocHetHan(maLo, maThuoc, tenThuoc, ngaySanXuat, hanSuDung, soLuongTon, soNgay);
    }

    private java.util.Date toUtilDate(Object value) {
        if (value == null) return null;
        if (value instanceof java.util.Date date) return date;
        if (value instanceof LocalDate localDate) return Date.valueOf(localDate);
        return null;
    }
}
