package com.pillchill.migration.repository.impl;

import com.pillchill.migration.entity.KhachHang;
import com.pillchill.migration.repository.IKhachHangRepository;

import java.util.List;
import java.util.Optional;

public class KhachHangRepository extends AbstracGenericRepository<KhachHang, String> implements IKhachHangRepository {
    private final Template template;

    public KhachHangRepository() {
        super(KhachHang.class);
        this.template = new Template();
    }

    @Override
    public List<KhachHang> findAllActive() {
        return template.execute(em -> em.createQuery(
                        "select k from KhachHang k " +
                                "where k.isActive = true " +
                                "order by k.maKH",
                        KhachHang.class)
                .getResultList());
    }

    @Override
    public Optional<KhachHang> findById(String maKH) {
        return template.execute(em -> Optional.ofNullable(em.find(KhachHang.class, maKH)));
    }

    @Override
    public long countActive() {
        return template.execute(em -> em.createQuery(
                        "select count(k) from KhachHang k where k.isActive = true",
                        Long.class)
                .getSingleResult());
    }

    @Override
    public boolean deactivateKhachHang(String maKH) {
        return template.execute(em -> {
            int updated = em.createQuery(
                            "update KhachHang k set k.isActive = false where k.maKH = :maKH and k.isActive = true")
                    .setParameter("maKH", maKH)
                    .executeUpdate();
            return updated > 0;
        });
    }

    public static void main(String[] args) {
        KhachHangRepository khachHangRepository = new KhachHangRepository();
        System.out.println(khachHangRepository.findAllActive());
    }
}
