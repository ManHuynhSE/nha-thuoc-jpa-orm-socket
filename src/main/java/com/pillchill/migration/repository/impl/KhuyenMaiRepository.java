package com.pillchill.migration.repository.impl;

import com.pillchill.migration.entity.KhuyenMai;
import com.pillchill.migration.repository.IKhuyenMaiRepository;

import java.util.List;

public class KhuyenMaiRepository extends AbstracGenericRepository<KhuyenMai, String> implements IKhuyenMaiRepository {
    private final Template template;

    public KhuyenMaiRepository() {
        super(KhuyenMai.class);
        this.template = new Template();
    }

    @Override
    public KhuyenMai createKhuyenMai(KhuyenMai khuyenMai) {
        return create(khuyenMai);
    }

    @Override
    public KhuyenMai updateKhuyenMai(KhuyenMai khuyenMai) {
        return update(khuyenMai);
    }

    @Override
    public KhuyenMai findById(String maKhuyenMai) {
        return findByID(maKhuyenMai);
    }


    @Override
    public List<KhuyenMai> loadAllKhuyenMai() {
        return loadAll();
    }

    @Override
    public List<KhuyenMai> findAllActive() {
        return template.execute(em -> em.createQuery(
                        "select k from KhuyenMai k " +
                                "where k.isActive = true " +
                                "order by k.maKM",
                        KhuyenMai.class)
                .getResultList());
    }

    @Override
    public List<KhuyenMai> findAllInactive() {
        return template.execute(em -> em.createQuery(
                        "select k from KhuyenMai k " +
                                "where k.isActive = false " +
                                "order by k.maKM",
                        KhuyenMai.class)
                .getResultList());
    }

    @Override
    public long countActive() {
        return template.execute(em -> em.createQuery(
                        "select count(k) from KhuyenMai k where k.isActive = true",
                        Long.class)
                .getSingleResult());
    }

    @Override
    public boolean deactivateKhuyenMai(String maKM) {
        return template.execute(em -> {
            int updated = em.createQuery(
                            "update KhuyenMai k set k.isActive = false where k.maKM = :maKM and k.isActive = true")
                    .setParameter("maKM", maKM)
                    .executeUpdate();
            return updated > 0;
        });
    }

    @Override
    public boolean reactivateKhuyenMai(String maKM) {
        return template.execute(em -> {
            int updated = em.createQuery(
                            "update KhuyenMai k set k.isActive = true where k.maKM = :maKM and k.isActive = false")
                    .setParameter("maKM", maKM)
                    .executeUpdate();
            return updated > 0;
        });
    }


    @Override
    public boolean isValid(String maKM) {
        return template.execute(em -> {
            Long count = em.createQuery(
                            "select count(k) from KhuyenMai k where k.maKM = :maKM and k.isActive = true",
                            Long.class)
                    .setParameter("maKM", maKM)
                    .getSingleResult();
            return count != null && count > 0;
        });
    }
}
