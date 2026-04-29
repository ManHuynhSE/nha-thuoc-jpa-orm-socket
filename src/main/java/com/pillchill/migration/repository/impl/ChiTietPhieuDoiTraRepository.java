package com.pillchill.migration.repository.impl;

import com.pillchill.migration.entity.ChiTietPhieuDoiTra;
import com.pillchill.migration.repository.IChiTietPhieuDoiTraRepository;

import java.util.List;

public class ChiTietPhieuDoiTraRepository extends RepositoryTemplate implements IChiTietPhieuDoiTraRepository {
    @Override
    public List<ChiTietPhieuDoiTra> findByMaPhieuDoiTraWithThuoc(String maPhieuDoiTra) {
        return execute(em -> em.createQuery(
                        "select c from ChiTietPhieuDoiTra c " +
                                "join fetch c.thuoc t " +
                                "join fetch c.loThuoc l " +
                                "where c.id.maPhieuDoiTra = :maPhieuDoiTra and c.isActive = true and t.isActive = true and l.isActive = true " +
                                "order by c.id.maThuoc, c.id.maLo",
                        ChiTietPhieuDoiTra.class)
                .setParameter("maPhieuDoiTra", maPhieuDoiTra)
                .getResultList());
    }
}
