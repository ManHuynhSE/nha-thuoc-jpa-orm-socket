package com.pillchill.migration.repository.impl;

import com.pillchill.migration.entity.NhanVien;
import com.pillchill.migration.repository.INhanVienRepository;

import java.util.List;
import java.util.Optional;

public class NhanVienRepository extends AbstracGenericRepository<NhanVien, String> implements INhanVienRepository {
    private final Template template;

    public NhanVienRepository() {
        super(NhanVien.class);
        this.template = new Template();
    }

    @Override
    public List<NhanVien> findAllActive() {
        return template.execute(em -> em.createQuery(
                        "select n from NhanVien n " +
                                "left join fetch n.chucVu cv " +
                                "where n.isActive = true " +
                                "order by n.maNV",
                        NhanVien.class)
                .getResultList());
    }

    @Override
    public Optional<NhanVien> findById(String maNV) {
        return template.execute(em -> Optional.ofNullable(em.find(NhanVien.class, maNV)));
    }

    @Override
    public long countActive() {
        return template.execute(em -> em.createQuery(
                        "select count(n) from NhanVien n where n.isActive = true",
                        Long.class)
                .getSingleResult());
    }

    @Override
    public boolean deactivateNhanVien(String maNV) {
        return template.execute(em -> {
            int updated = em.createQuery(
                            "update NhanVien n set n.isActive = false where n.maNV = :maNV and n.isActive = true")
                    .setParameter("maNV", maNV)
                    .executeUpdate();
            return updated > 0;
        });
    }

    public static void main(String[] args) {
        NhanVienRepository nhanVienRepository = new NhanVienRepository();
        System.out.println(nhanVienRepository.findAllActive());
    }
}
