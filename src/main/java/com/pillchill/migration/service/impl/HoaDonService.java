package com.pillchill.migration.service.impl;

import java.util.List;

import com.pillchill.migration.entity.ChiTietHoaDon;
import com.pillchill.migration.entity.ChiTietLoThuoc;
import com.pillchill.migration.entity.HoaDon;
import com.pillchill.migration.entity.KhachHang;
import com.pillchill.migration.entity.KhuyenMai;
import com.pillchill.migration.entity.LoThuoc;
import com.pillchill.migration.entity.NhanVien;
import com.pillchill.migration.entity.Thuoc;
import com.pillchill.migration.entity.id.ChiTietHoaDonId;
import com.pillchill.migration.entity.id.ChiTietLoThuocId;
import com.pillchill.migration.dto.CreateHoaDonCommand;
import com.pillchill.migration.dto.HoaDonItemCommand;
import com.pillchill.migration.db.JPAUtil;
import com.pillchill.migration.service.IHoaDonService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

public class HoaDonService implements IHoaDonService {
    @Override
    public HoaDon createHoaDon(CreateHoaDonCommand command) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            NhanVien nhanVien = em.getReference(NhanVien.class, command.maNV());
            KhachHang khachHang = command.maKH() == null || command.maKH().isBlank()
                    ? null
                    : em.getReference(KhachHang.class, command.maKH());
            KhuyenMai khuyenMai = command.maKM() == null || command.maKM().isBlank()
                    ? null
                    : em.getReference(KhuyenMai.class, command.maKM());

            HoaDon hoaDon = HoaDon.builder()
                    .maHoaDon(command.maHoaDon())
                    .ngayBan(command.ngayBan())
                    .ghiChu(command.ghiChu())
                    .nhanVien(nhanVien)
                    .khachHang(khachHang)
                    .khuyenMai(khuyenMai)
                    .giaTriThue(command.giaTriThue())
                    .tenLoaiThue(command.tenLoaiThue())
                    .isActive(true)
                    .build();
            em.persist(hoaDon);

            for (HoaDonItemCommand item : command.items()) {
                String maLo = truTonKhoTheoFIFO(em, item.maThuoc(), item.soLuong());
                Thuoc thuoc = em.getReference(Thuoc.class, item.maThuoc());
                LoThuoc loThuoc = em.getReference(LoThuoc.class, maLo);
                ChiTietHoaDon chiTietHoaDon = ChiTietHoaDon.builder()
                        .id(new ChiTietHoaDonId(command.maHoaDon(), item.maThuoc(), maLo))
                        .hoaDon(hoaDon)
                        .thuoc(thuoc)
                        .loThuoc(loThuoc)
                        .soLuong(item.soLuong())
                        .donGia(item.donGia())
                        .isActive(true)
                        .build();
                em.persist(chiTietHoaDon);
                dongBoSoLuongTon(em, item.maThuoc());
            }

            tx.commit();
            return hoaDon;
        } catch (RuntimeException ex) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw ex;
        } finally {
            em.close();
        }
    }

    private String truTonKhoTheoFIFO(EntityManager em, String maThuoc, int soLuongCanMua) {
        List<ChiTietLoThuoc> loThuocs = em.createQuery(
                        "select c from ChiTietLoThuoc c where c.id.maThuoc = :maThuoc and c.isActive = true and c.soLuong > 0 order by c.ngaySanXuat asc",
                        ChiTietLoThuoc.class)
                .setParameter("maThuoc", maThuoc)
                .getResultList();

        int canTru = soLuongCanMua;
        String maLoKetQua = null;
        for (ChiTietLoThuoc loThuoc : loThuocs) {
            if (canTru <= 0) {
                break;
            }
            int soLuongLo = loThuoc.getSoLuong();
            int soLuongTru = Math.min(soLuongLo, canTru);
            loThuoc.setSoLuong(soLuongLo - soLuongTru);
            em.merge(loThuoc);
            canTru -= soLuongTru;
            maLoKetQua = loThuoc.getId().getMaLo();
        }

        if (canTru > 0) {
            throw new IllegalStateException("Khong du ton kho cho ma thuoc " + maThuoc + ", con thieu " + canTru);
        }
        return maLoKetQua;
    }

    private void dongBoSoLuongTon(EntityManager em, String maThuoc) {
        int tongSoLuong = em.createQuery(
                        "select coalesce(sum(c.soLuong), 0) from ChiTietLoThuoc c where c.id.maThuoc = :maThuoc and c.isActive = true",
                        Number.class)
                .setParameter("maThuoc", maThuoc)
                .getSingleResult()
                .intValue();
        em.createQuery("update Thuoc t set t.soLuongTon = :soLuong where t.maThuoc = :maThuoc")
                .setParameter("soLuong", tongSoLuong)
                .setParameter("maThuoc", maThuoc)
                .executeUpdate();
    }

    @Override
    public ChiTietLoThuoc getLot(String maLo, String maThuoc) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(ChiTietLoThuoc.class, new ChiTietLoThuocId(maLo, maThuoc));
        } finally {
            em.close();
        }
    }
}
