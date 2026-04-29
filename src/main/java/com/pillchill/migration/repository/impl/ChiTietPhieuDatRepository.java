package com.pillchill.migration.repository.impl;

import com.pillchill.migration.entity.ChiTietPhieuDat;
import com.pillchill.migration.repository.IChiTietPhieuDatRepository;

import java.util.List;

public class ChiTietPhieuDatRepository extends RepositoryTemplate implements IChiTietPhieuDatRepository {
    @Override
    public List<ChiTietPhieuDat> findByMaPhieuDatWithThuoc(String maPhieuDat) {
        return execute(em -> em.createQuery(
                        "select c from ChiTietPhieuDat c " +
                                "join fetch c.thuoc t " +
                                "join fetch c.loThuoc l " +
                                "where c.id.maPhieuDat = :maPhieuDat and c.isActive = true and t.isActive = true and l.isActive = true " +
                                "order by c.id.maThuoc, c.id.maLo",
                        ChiTietPhieuDat.class)
                .setParameter("maPhieuDat", maPhieuDat)
                .getResultList());
    }
}
