package com.pillchill.migration.repository.impl;

import com.pillchill.migration.entity.PhieuNhapThuoc;
import com.pillchill.migration.repository.IPhieuNhapRepository;

import java.util.List;

public class PhieuNhapRepository extends RepositoryTemplate implements IPhieuNhapRepository {
    @Override
    public List<PhieuNhapThuoc> findAllActiveWithNhanVien() {
        return execute(em -> em.createQuery(
                        "select p from PhieuNhapThuoc p " +
                                "join fetch p.nhanVien nv " +
                                "where p.isActive = true and nv.isActive = true " +
                                "order by p.ngayNhap desc, p.maPhieuNhapThuoc desc",
                        PhieuNhapThuoc.class)
                .getResultList());
    }
}
