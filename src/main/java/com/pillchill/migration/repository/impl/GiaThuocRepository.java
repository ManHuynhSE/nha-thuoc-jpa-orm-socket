package com.pillchill.migration.repository.impl;

import java.util.Optional;

import com.pillchill.migration.repository.IGiaThuocRepository;

public class GiaThuocRepository extends RepositoryTemplate implements IGiaThuocRepository {

    @Override
    public Optional<Double> getGiaHienTaiByMaThuoc(String maThuoc) {
        return execute(em -> em.createQuery(
                        """
                        select cbg.donGia
                        from ChiTietBangGia cbg
                        where cbg.thuoc.maThuoc = :maThuoc
                          and cbg.isActive = true
                          and cbg.bangGia.isActive = true
                        order by cbg.bangGia.doUuTien desc
                        """,
                        Double.class)
                .setParameter("maThuoc", maThuoc)
                .setMaxResults(1)
                .getResultStream()
                .findFirst());
    }
}
