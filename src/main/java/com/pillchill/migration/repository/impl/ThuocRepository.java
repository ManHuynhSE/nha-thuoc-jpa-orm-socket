package com.pillchill.migration.repository.impl;

import java.util.List;
import java.util.Optional;

import com.pillchill.migration.entity.Thuoc;
import com.pillchill.migration.repository.IThuocRepository;

public class ThuocRepository extends AbstracGenericRepository<Thuoc,String> implements IThuocRepository {
    private final Template template;
     public ThuocRepository() {
        super(Thuoc.class);
         this.template = new Template();
    }

    @Override
    public List<Thuoc> findAllActive() {
        return template.execute(em -> em.createQuery(
                        "select t from Thuoc t " +
                                "left join fetch t.donVi dv " +
                                "left join fetch t.nhaSanXuat nsx " +
                                "where t.isActive = true " +
                                "order by t.maThuoc",
                        Thuoc.class)
                .getResultList());
    }

    @Override
    public Optional<Thuoc> findById(String maThuoc) {
        return template.execute(em -> Optional.ofNullable(em.find(Thuoc.class, maThuoc)));
    }

    @Override
    public long countActive() {
        return template.execute(em -> em.createQuery(
                        "select count(t) from Thuoc t where t.isActive = true",
                        Long.class)
                .getSingleResult());
    }

    @Override
    public void updateSoLuongTon(String maThuoc, int soLuongTon) {
        template.execute(em -> {
            em.createQuery("update Thuoc t set t.soLuongTon = :soLuongTon where t.maThuoc = :maThuoc")
                    .setParameter("soLuongTon", soLuongTon)
                    .setParameter("maThuoc", maThuoc)
                    .executeUpdate();
            return null;
        });
    }


    @Override
    public boolean deactivateThuoc(String maThuoc) {
        return template.execute(em -> {
            int updated = em.createQuery(
                            "update Thuoc t set t.isActive = false where t.maThuoc = :maThuoc and t.isActive = true")
                    .setParameter("maThuoc", maThuoc)
                    .executeUpdate();
            return updated > 0;
        });
    }

    public static void main(String[] args) {
        ThuocRepository thuocRepository = new ThuocRepository();
        System.out.println(thuocRepository.findAllActive());
    }
}
