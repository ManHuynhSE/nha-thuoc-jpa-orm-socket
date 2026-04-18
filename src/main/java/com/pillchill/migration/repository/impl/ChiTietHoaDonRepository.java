package com.pillchill.migration.repository.impl;

import java.util.List;

import com.pillchill.migration.entity.ChiTietHoaDon;
import com.pillchill.migration.entity.id.ChiTietHoaDonId;
import com.pillchill.migration.repository.IChiTietHoaDonRepository;

public class ChiTietHoaDonRepository extends RepositoryTemplate implements IChiTietHoaDonRepository {

    @Override
    public List<ChiTietHoaDon> findByMaHoaDon(String maHoaDon) {
        return execute(em -> em.createQuery(
                        "select c from ChiTietHoaDon c where c.id.maHoaDon = :maHoaDon and c.isActive = true",
                        ChiTietHoaDon.class)
                .setParameter("maHoaDon", maHoaDon)
                .getResultList());
    }

    @Override
    public void save(ChiTietHoaDon chiTietHoaDon) {
        execute(em -> {
            em.merge(chiTietHoaDon);
            return null;
        });
    }

    @Override
    public void softDelete(ChiTietHoaDonId id) {
        execute(em -> {
            em.createQuery("update ChiTietHoaDon c set c.isActive = false where c.id = :id")
                    .setParameter("id", id)
                    .executeUpdate();
            return null;
        });
    }
}
