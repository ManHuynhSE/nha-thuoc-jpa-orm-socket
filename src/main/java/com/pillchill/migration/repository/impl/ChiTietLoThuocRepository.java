package com.pillchill.migration.repository.impl;

import java.util.List;

import com.pillchill.migration.entity.ChiTietLoThuoc;
import com.pillchill.migration.entity.id.ChiTietLoThuocId;
import com.pillchill.migration.repository.IChiTietLoThuocRepository;

public class ChiTietLoThuocRepository extends RepositoryTemplate implements IChiTietLoThuocRepository {

    @Override
    public List<ChiTietLoThuoc> findActiveByMaThuocOrderByNgaySanXuat(String maThuoc) {
        return execute(em -> em.createQuery(
                        "select c from ChiTietLoThuoc c where c.id.maThuoc = :maThuoc and c.isActive = true and c.soLuong > 0 order by c.ngaySanXuat asc",
                        ChiTietLoThuoc.class)
                .setParameter("maThuoc", maThuoc)
                .getResultList());
    }

    @Override
    public ChiTietLoThuoc getReference(ChiTietLoThuocId id) {
        return execute(em -> em.find(ChiTietLoThuoc.class, id));
    }

    @Override
    public void save(ChiTietLoThuoc chiTietLoThuoc) {
        execute(em -> {
            em.merge(chiTietLoThuoc);
            return null;
        });
    }

    @Override
    public int sumSoLuongByMaThuoc(String maThuoc) {
        return execute(em -> em.createQuery(
                        "select coalesce(sum(c.soLuong), 0) from ChiTietLoThuoc c where c.id.maThuoc = :maThuoc and c.isActive = true",
                        Number.class)
                .setParameter("maThuoc", maThuoc)
                .getSingleResult()
                .intValue());
    }
}
