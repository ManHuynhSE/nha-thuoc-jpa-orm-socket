package com.pillchill.migration.repository.impl;

import java.util.List;
import java.util.Optional;

import com.pillchill.migration.entity.Thuoc;
import com.pillchill.migration.repository.IThuocRepository;

public class ThuocRepository extends RepositoryTemplate implements IThuocRepository {

    @Override
    public List<Thuoc> findAllActive() {
        return execute(em -> em.createQuery(
                        "select t from Thuoc t where t.isActive = true order by t.maThuoc",
                        Thuoc.class)
                .getResultList());
    }

    @Override
    public Optional<Thuoc> findById(String maThuoc) {
        return execute(em -> Optional.ofNullable(em.find(Thuoc.class, maThuoc)));
    }

    @Override
    public long countActive() {
        return execute(em -> em.createQuery(
                        "select count(t) from Thuoc t where t.isActive = true",
                        Long.class)
                .getSingleResult());
    }

    @Override
    public void updateSoLuongTon(String maThuoc, int soLuongTon) {
        execute(em -> {
            em.createQuery("update Thuoc t set t.soLuongTon = :soLuongTon where t.maThuoc = :maThuoc")
                    .setParameter("soLuongTon", soLuongTon)
                    .setParameter("maThuoc", maThuoc)
                    .executeUpdate();
            return null;
        });
    }
}
