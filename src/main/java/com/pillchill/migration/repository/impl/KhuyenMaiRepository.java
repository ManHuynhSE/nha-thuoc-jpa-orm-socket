package com.pillchill.migration.repository.impl;

import com.pillchill.migration.entity.KhuyenMai;
import com.pillchill.migration.repository.IKhuyenMaiRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class KhuyenMaiRepository extends AbstracGenericRepository<KhuyenMai, String> implements IKhuyenMaiRepository {
    private final Template template;

    public KhuyenMaiRepository() {
        super(KhuyenMai.class);
        this.template = new Template();
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
    public Optional<KhuyenMai> findById(String maKM) {
        return template.execute(em -> Optional.ofNullable(em.find(KhuyenMai.class, maKM)));
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
    public KhuyenMai findByMa(String maKM) {
        return findById(maKM).orElse(null);
    }

    @Override
    public boolean isValid(String maKM) {
        return template.execute(em -> {
            List<KhuyenMai> results = em.createQuery(
                            "select k from KhuyenMai k where k.maKM = :maKM and k.isActive = true " +
                                    "and (k.ngayApDung is null or k.ngayApDung <= :today) " +
                                    "and (k.ngayKetThuc is null or k.ngayKetThuc >= :today)",
                            KhuyenMai.class)
                    .setParameter("maKM", maKM)
                    .setParameter("today", LocalDate.now())
                    .getResultList();
            return !results.isEmpty();
        });
    }
}
