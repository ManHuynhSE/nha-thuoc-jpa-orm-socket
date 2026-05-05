package com.pillchill.migration.repository.impl;

import com.pillchill.migration.entity.BangGia;
import com.pillchill.migration.repository.IBangGiaRepository;

import java.util.List;
import java.util.Optional;

public class BangGiaRepository extends AbstracGenericRepository<BangGia, String> implements IBangGiaRepository{
    private final Template template;

    public BangGiaRepository() {
        super(BangGia.class);
        this.template = new Template();
    }


    @Override
    public Optional<BangGia> findById(String maBangGia) {
        return template.execute(em -> Optional.ofNullable(em.find(BangGia.class, maBangGia)));
    }

    @Override
    public List<BangGia> findAllActive() {
        return template.execute(em -> em.createQuery(
                        "select bg from BangGia bg " +
                                "where bg.isActive = true " +
                                "order by bg.maBangGia",
                        BangGia.class)
                .getResultList());
    }

    @Override
    public boolean deactivateBangGia(String maBG) {
        return template.execute(em -> {
            int updated = em.createQuery(
                            "update BangGia bg set bg.isActive = false where bg.maBangGia = :maBG and bg.isActive = true")
                    .setParameter("maBG", maBG)
                    .executeUpdate();
            return updated > 0;
        });
    }

    public static void main(String[] args) {
        BangGiaRepository bangGiaRepository = new BangGiaRepository();
        System.out.println(bangGiaRepository.findAllActive());
    }
}
