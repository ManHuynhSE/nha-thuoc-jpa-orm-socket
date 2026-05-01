package com.pillchill.migration.repository.impl;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import com.pillchill.migration.dto.ThongKeThuoc;
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
                        "select t from Thuoc t where t.isActive = true order by t.maThuoc",
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

    public static void main(String[] args) {
        ThuocRepository thuocRepository = new ThuocRepository();
        System.out.println(thuocRepository.findByID("T001"));
    }

    @Override
    public List<ThongKeThuoc> thongKeThuocTheoNgay(Date ngay, int topN) {
        return List.of();
    }

    @Override
    public List<ThongKeThuoc> thongKeThuocTheoThang(int thang, int nam, int topN) {
        return List.of();
    }

    @Override
    public List<ThongKeThuoc> thongKeThuocTheoNam(int nam, int topN) {
        return List.of();
    }

    @Override
    public double getTongDoanhThuThuocTheoNgay(Date ngay) {
        return 0;
    }

    @Override
    public double getTongDoanhThuThuocTheoThang(int thang, int nam) {
        return 0;
    }

    @Override
    public double getTongDoanhThuThuocTheoNam(int nam) {
        return 0;
    }
}
