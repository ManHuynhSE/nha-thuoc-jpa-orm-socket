package com.pillchill.migration.repository.impl;

import com.pillchill.migration.entity.PhieuDoiTra;
import com.pillchill.migration.repository.IPhieuDoiTraRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class PhieuDoiTraRepository extends RepositoryTemplate implements IPhieuDoiTraRepository {
    @Override
    public Optional<PhieuDoiTra> findById(String maPhieuDoiTra) {
        return execute(em -> Optional.ofNullable(em.find(PhieuDoiTra.class, maPhieuDoiTra)));
    }

    @Override
    public List<PhieuDoiTra> findByDateRange(LocalDate fromDate, LocalDate toDate) {
        return execute(em -> em.createQuery(
                        "select p from PhieuDoiTra p " +
                                "join fetch p.nhanVien nv " +
                                "left join fetch p.khachHang kh " +
                                "where p.isActive = true and nv.isActive = true and p.ngayDoiTra between :fromDate and :toDate " +
                                "order by p.ngayDoiTra desc, p.maPhieuDoiTra desc",
                        PhieuDoiTra.class)
                .setParameter("fromDate", fromDate)
                .setParameter("toDate", toDate)
                .getResultList());
    }

    @Override
    public List<PhieuDoiTra> findAllActiveWithNhanVienKhachHang() {
        return execute(em -> em.createQuery(
                        "select p from PhieuDoiTra p " +
                                "join fetch p.nhanVien nv " +
                                "left join fetch p.khachHang kh " +
                                "where p.isActive = true and nv.isActive = true " +
                                "order by p.ngayDoiTra desc, p.maPhieuDoiTra desc",
                        PhieuDoiTra.class)
                .getResultList());
    }
}
