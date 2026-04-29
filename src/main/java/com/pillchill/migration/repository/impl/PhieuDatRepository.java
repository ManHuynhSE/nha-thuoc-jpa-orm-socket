package com.pillchill.migration.repository.impl;

import com.pillchill.migration.entity.PhieuDat;
import com.pillchill.migration.repository.IPhieuDatRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class PhieuDatRepository extends RepositoryTemplate implements IPhieuDatRepository {
    @Override
    public Optional<PhieuDat> findById(String maPhieuDat) {
        return execute(em -> Optional.ofNullable(em.find(PhieuDat.class, maPhieuDat)));
    }

    @Override
    public List<PhieuDat> findByDateRange(LocalDate fromDate, LocalDate toDate) {
        return execute(em -> em.createQuery(
                        "select p from PhieuDat p " +
                                "join fetch p.nhanVien nv " +
                                "left join fetch p.khachHang kh " +
                                "where p.isActive = true and nv.isActive = true and p.ngayDat between :fromDate and :toDate " +
                                "order by p.ngayDat desc, p.maPhieuDat desc",
                        PhieuDat.class)
                .setParameter("fromDate", fromDate)
                .setParameter("toDate", toDate)
                .getResultList());
    }

    @Override
    public List<PhieuDat> findAllActiveWithNhanVienKhachHang() {
        return execute(em -> em.createQuery(
                        "select p from PhieuDat p " +
                                "join fetch p.nhanVien nv " +
                                "left join fetch p.khachHang kh " +
                                "where p.isActive = true and nv.isActive = true " +
                                "order by p.ngayDat desc, p.maPhieuDat desc",
                        PhieuDat.class)
                .getResultList());
    }
}
