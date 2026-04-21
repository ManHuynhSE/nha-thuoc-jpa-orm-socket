package com.pillchill.migration.repository.impl;

import com.pillchill.migration.entity.BangGia;
import com.pillchill.migration.entity.ChiTietBangGia;
import com.pillchill.migration.entity.DonVi;
import com.pillchill.migration.entity.Thuoc;
import com.pillchill.migration.entity.id.ChiTietBangGiaId;
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

    @Override
    public void upsertGiaCoSo(String maThuoc, String maDonVi, double donGia) {
        execute(em -> {
            BangGia bangGia = em.find(BangGia.class, "BG001");
            if (bangGia == null || !bangGia.isActive()) {
                throw new IllegalStateException("Không tìm thấy bảng giá BG001 đang hoạt động");
            }

            ChiTietBangGiaId id = new ChiTietBangGiaId("BG001", maThuoc);
            ChiTietBangGia existing = em.find(ChiTietBangGia.class, id);
            DonVi donViRef = em.getReference(DonVi.class, maDonVi);
            Thuoc thuocRef = em.getReference(Thuoc.class, maThuoc);

            if (existing == null) {
                ChiTietBangGia created = ChiTietBangGia.builder()
                        .id(id)
                        .bangGia(bangGia)
                        .thuoc(thuocRef)
                        .donVi(donViRef)
                        .donGia(donGia)
                        .isActive(true)
                        .build();
                em.persist(created);
            } else {
                existing.setDonGia(donGia);
                existing.setDonVi(donViRef);
                existing.setActive(true);
            }
            return null;
        });
    }
}
