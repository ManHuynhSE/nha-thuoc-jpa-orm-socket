package com.pillchill.migration.migration;

import com.pillchill.migration.db.JPAUtil;
import com.pillchill.migration.dto.ChiTietBangGiaView;
import com.pillchill.migration.dto.ThuocBangGiaView;
import com.pillchill.migration.entity.BangGia;
import com.pillchill.migration.entity.ChiTietBangGia;
import com.pillchill.migration.entity.DonVi;
import com.pillchill.migration.entity.Thuoc;
import com.pillchill.migration.entity.id.ChiTietBangGiaId;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.ArrayList;
import java.util.List;

public class ChiTietBangGiaJpaDAO {

    public List<ChiTietBangGiaView> getChiTietBangGiaTheoMa(String maBangGia) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            return em.createQuery(
                            "select new com.pillchill.migration.dto.ChiTietBangGiaView(" +
                                    "bg.maBangGia, t.maThuoc, t.tenThuoc, ct.donGia, dv.maDonVi) " +
                                    "from ChiTietBangGia ct " +
                                    "join ct.bangGia bg " +
                                    "join ct.thuoc t " +
                                    "left join ct.donVi dv " +
                                    "where bg.maBangGia = :maBangGia " +
                                    "order by t.maThuoc",
                            ChiTietBangGiaView.class)
                    .setParameter("maBangGia", maBangGia)
                    .getResultList();
        }
    }

    public List<ThuocBangGiaView> getAllThuocKemGia() {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            List<Thuoc> thuocList = em.createQuery(
                            "select t from Thuoc t where t.isActive = true order by t.maThuoc",
                            Thuoc.class)
                    .getResultList();

            List<ThuocBangGiaView> result = new ArrayList<>();
            for (Thuoc thuoc : thuocList) {
                String maThuoc = thuoc.getMaThuoc();
                String tenThuoc = thuoc.getTenThuoc();
                String maDonVi = thuoc.getDonVi() == null ? "" : thuoc.getDonVi().getMaDonVi();

                double giaChuan = em.createQuery(
                                "select ct.donGia from ChiTietBangGia ct " +
                                        "where ct.bangGia.maBangGia = :maBangGia " +
                                        "and ct.thuoc.maThuoc = :maThuoc " +
                                        "and ct.isActive = true",
                                Double.class)
                        .setParameter("maBangGia", "BG001")
                        .setParameter("maThuoc", maThuoc)
                        .setMaxResults(1)
                        .getResultStream()
                        .findFirst()
                        .orElse(0.0);

                double giaHienTai = em.createQuery(
                                "select ct.donGia from ChiTietBangGia ct " +
                                        "where ct.thuoc.maThuoc = :maThuoc " +
                                        "and ct.isActive = true " +
                                        "and ct.bangGia.isActive = true " +
                                        "order by ct.bangGia.doUuTien desc",
                                Double.class)
                        .setParameter("maThuoc", maThuoc)
                        .setMaxResults(1)
                        .getResultStream()
                        .findFirst()
                        .orElse(0.0);

                result.add(new ThuocBangGiaView(maThuoc, tenThuoc, maDonVi, giaChuan, giaHienTai));
            }
            return result;
        }
    }

    public boolean themChiTietBangGia(String maBangGia, String maThuoc, double donGia, String maDonVi, boolean isActive) {
        EntityTransaction tx = null;
        try (EntityManager em = JPAUtil.getEntityManager()) {
            tx = em.getTransaction();
            tx.begin();

            BangGia bangGia = em.find(BangGia.class, maBangGia);
            Thuoc thuoc = em.find(Thuoc.class, maThuoc);
            DonVi donVi = em.find(DonVi.class, maDonVi);

            if (bangGia == null || thuoc == null || donVi == null) {
                throw new IllegalArgumentException("Dữ liệu chi tiết bảng giá không hợp lệ");
            }

            ChiTietBangGiaId id = new ChiTietBangGiaId(maBangGia, maThuoc);
            ChiTietBangGia existing = em.find(ChiTietBangGia.class, id);
            if (existing != null) {
                tx.rollback();
                return false;
            }

            ChiTietBangGia created = ChiTietBangGia.builder()
                    .id(id)
                    .bangGia(bangGia)
                    .thuoc(thuoc)
                    .donGia(donGia)
                    .donVi(donVi)
                    .isActive(isActive)
                    .build();
            em.persist(created);

            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException(e);
        }
    }

    public boolean capNhatChiTietBangGia(String maBangGia, String maThuoc, double donGia, String maDonVi, boolean isActive) {
        EntityTransaction tx = null;
        try (EntityManager em = JPAUtil.getEntityManager()) {
            tx = em.getTransaction();
            tx.begin();

            ChiTietBangGiaId id = new ChiTietBangGiaId(maBangGia, maThuoc);
            ChiTietBangGia existing = em.find(ChiTietBangGia.class, id);
            DonVi donVi = em.find(DonVi.class, maDonVi);

            if (existing == null || donVi == null) {
                tx.rollback();
                return false;
            }

            existing.setDonGia(donGia);
            existing.setDonVi(donVi);
            existing.setActive(isActive);

            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException(e);
        }
    }

    public boolean xoaChiTietBangGia(String maBangGia, String maThuoc) {
        EntityTransaction tx = null;
        try (EntityManager em = JPAUtil.getEntityManager()) {
            tx = em.getTransaction();
            tx.begin();

            ChiTietBangGiaId id = new ChiTietBangGiaId(maBangGia, maThuoc);
            ChiTietBangGia existing = em.find(ChiTietBangGia.class, id);
            if (existing == null) {
                tx.rollback();
                return false;
            }

            em.remove(existing);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException(e);
        }
    }
}
