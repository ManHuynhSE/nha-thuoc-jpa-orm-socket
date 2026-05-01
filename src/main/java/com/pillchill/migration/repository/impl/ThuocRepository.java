package com.pillchill.migration.repository.impl;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import com.pillchill.migration.dto.ThongKeThuoc;
import com.pillchill.migration.dto.ThuocKemGiaView;
import com.pillchill.migration.entity.ChiTietBangGia;
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
        return template.execute(em -> new java.util.ArrayList<>(em.createQuery(
                        "select t from Thuoc t where t.isActive = true order by t.maThuoc",
                        Thuoc.class)
                .getResultList()));
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
    public List<ThuocKemGiaView> getAllThuocKemGia() {
        return template.execute(em -> new java.util.ArrayList<>(em.createQuery(
                        "select distinct new com.pillchill.migration.dto.ThuocKemGiaView(" +
                                "t.maThuoc, " +
                                "t.tenThuoc, " +
                                "t.soLuongTon, " +
                                "coalesce(cbg.donGia, 0), " +
                                "donVi.maDonVi, " +
                                "coalesce(t.soLuongToiThieu, 0), " +
                                "nhaSanXuat.maNSX, " +
                                "t.isActive) " +
                                "from Thuoc t " +
                                "left join t.donVi donVi " +
                                "left join t.nhaSanXuat nhaSanXuat " +
                                "left join ChiTietBangGia cbg on cbg.thuoc.maThuoc = t.maThuoc " +
                                "and cbg.isActive = true " +
                                "and cbg.bangGia.isActive = true " +
                                "and cbg.bangGia.doUuTien = (" +
                                "    select max(c2.bangGia.doUuTien) " +
                                "    from ChiTietBangGia c2 " +
                                "    where c2.thuoc.maThuoc = t.maThuoc " +
                                "      and c2.isActive = true " +
                                "      and c2.bangGia.isActive = true" +
                                ") " +
                                "where t.isActive = true " +
                                "order by t.maThuoc",
                        ThuocKemGiaView.class)
                .getResultList()));
    }

    public static void main(String[] args) {
        ThuocRepository thuocRepository = new ThuocRepository();
        System.out.println(thuocRepository.findByID("T001"));
    }

    @Override
    public List<ThongKeThuoc> thongKeThuocTheoNgay(Date ngay, int topN) {
        return template.execute(em -> {
            var query = em.createQuery(
                    "SELECT new com.pillchill.migration.dto.ThongKeThuoc(t.maThuoc, t.tenThuoc, SUM(CAST(ct.soLuong AS long)), SUM(CAST(ct.soLuong * ct.donGia AS double))) " +
                            "FROM ChiTietHoaDon ct " +
                            "JOIN ct.thuoc t " +
                            "JOIN ct.hoaDon hd " +
                            "WHERE hd.ngayBan = :ngay " +
                            "GROUP BY t.maThuoc, t.tenThuoc " +
                            "ORDER BY SUM(ct.soLuong) DESC", ThongKeThuoc.class)
                    .setParameter("ngay", ngay.toLocalDate());
            if (topN > 0) {
                query.setMaxResults(topN);
            }
            return new java.util.ArrayList<>(query.getResultList());
        });
    }

    @Override
    public List<ThongKeThuoc> thongKeThuocTheoThang(int thang, int nam, int topN) {
        return template.execute(em -> {
            var query = em.createQuery(
                    "SELECT new com.pillchill.migration.dto.ThongKeThuoc(t.maThuoc, t.tenThuoc, SUM(CAST(ct.soLuong AS long)), SUM(CAST(ct.soLuong * ct.donGia AS double))) " +
                            "FROM ChiTietHoaDon ct " +
                            "JOIN ct.thuoc t " +
                            "JOIN ct.hoaDon hd " +
                            "WHERE YEAR(hd.ngayBan) = :nam AND MONTH(hd.ngayBan) = :thang " +
                            "GROUP BY t.maThuoc, t.tenThuoc " +
                            "ORDER BY SUM(ct.soLuong) DESC", ThongKeThuoc.class)
                    .setParameter("nam", nam)
                    .setParameter("thang", thang);
            if (topN > 0) {
                query.setMaxResults(topN);
            }
            return new java.util.ArrayList<>(query.getResultList());
        });
    }

    @Override
    public List<ThongKeThuoc> thongKeThuocTheoNam(int nam, int topN) {
        return template.execute(em -> {
            var query = em.createQuery(
                    "SELECT new com.pillchill.migration.dto.ThongKeThuoc(t.maThuoc, t.tenThuoc, SUM(CAST(ct.soLuong AS long)), SUM(CAST(ct.soLuong * ct.donGia AS double))) " +
                            "FROM ChiTietHoaDon ct " +
                            "JOIN ct.thuoc t " +
                            "JOIN ct.hoaDon hd " +
                            "WHERE YEAR(hd.ngayBan) = :nam " +
                            "GROUP BY t.maThuoc, t.tenThuoc " +
                            "ORDER BY SUM(ct.soLuong) DESC", ThongKeThuoc.class)
                    .setParameter("nam", nam);
            if (topN > 0) {
                query.setMaxResults(topN);
            }
            return new java.util.ArrayList<>(query.getResultList());
        });
    }

    @Override
    public double getTongDoanhThuThuocTheoNgay(Date ngay) {
        return template.execute(em -> {
            Double result = em.createQuery(
                    "SELECT SUM(CAST(ct.soLuong * ct.donGia AS double)) " +
                            "FROM ChiTietHoaDon ct " +
                            "JOIN ct.hoaDon hd " +
                            "WHERE hd.ngayBan = :ngay", Double.class)
                    .setParameter("ngay", ngay.toLocalDate())
                    .getSingleResult();
            return result != null ? result : 0.0;
        });
    }

    @Override
    public double getTongDoanhThuThuocTheoThang(int thang, int nam) {
        return template.execute(em -> {
            Double result = em.createQuery(
                    "SELECT SUM(CAST(ct.soLuong * ct.donGia AS double)) " +
                            "FROM ChiTietHoaDon ct " +
                            "JOIN ct.hoaDon hd " +
                            "WHERE YEAR(hd.ngayBan) = :nam AND MONTH(hd.ngayBan) = :thang", Double.class)
                    .setParameter("nam", nam)
                    .setParameter("thang", thang)
                    .getSingleResult();
            return result != null ? result : 0.0;
        });
    }

    @Override
    public double getTongDoanhThuThuocTheoNam(int nam) {
        return template.execute(em -> {
            Double result = em.createQuery(
                    "SELECT SUM(CAST(ct.soLuong * ct.donGia AS double)) " +
                            "FROM ChiTietHoaDon ct " +
                            "JOIN ct.hoaDon hd " +
                            "WHERE YEAR(hd.ngayBan) = :nam", Double.class)
                    .setParameter("nam", nam)
                    .getSingleResult();
            return result != null ? result : 0.0;
        });
    }
}
