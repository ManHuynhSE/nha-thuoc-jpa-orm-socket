package com.pillchill.migration.repository.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.pillchill.migration.entity.HoaDon;
import com.pillchill.migration.repository.IHoaDonRepository;

public class HoaDonRepository extends RepositoryTemplate implements IHoaDonRepository {

    @Override
    public Optional<HoaDon> findById(String maHoaDon) {
        return execute(em -> Optional.ofNullable(em.find(HoaDon.class, maHoaDon)));
    }

    @Override
    public List<HoaDon> findByDateRange(LocalDate fromDate, LocalDate toDate) {
        return execute(em -> em.createQuery(
                        "select h from HoaDon h " +
                                "join fetch h.nhanVien nv " +
                                "left join fetch h.khachHang kh " +
                                "where h.isActive = true and nv.isActive = true and h.ngayBan between :fromDate and :toDate " +
                                "order by h.ngayBan desc, h.maHoaDon desc",
                        HoaDon.class)
                .setParameter("fromDate", fromDate)
                .setParameter("toDate", toDate)
                .getResultList());
    }

    @Override
    public List<HoaDon> findAllActiveWithNhanVienKhachHang() {
        return execute(em -> em.createQuery(
                        "select h from HoaDon h " +
                                "join fetch h.nhanVien nv " +
                                "left join fetch h.khachHang kh " +
                                "where h.isActive = true and nv.isActive = true " +
                                "order by h.ngayBan desc, h.maHoaDon desc",
                        HoaDon.class)
                .getResultList());
    }

    @Override
    public void save(HoaDon hoaDon) {
        execute(em -> {
            em.merge(hoaDon);
            return null;
        });
    }
}
