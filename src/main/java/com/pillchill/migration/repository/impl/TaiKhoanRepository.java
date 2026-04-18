package com.pillchill.migration.repository.impl;

import java.util.List;
import java.util.Optional;

import com.pillchill.migration.entity.TaiKhoan;
import com.pillchill.migration.repository.ITaiKhoanRepository;

public class TaiKhoanRepository extends RepositoryTemplate implements ITaiKhoanRepository {

    @Override
    public Optional<TaiKhoan> login(String maNV, String matKhau) {
        return execute(em -> em.createQuery(
                        "select tk from TaiKhoan tk where tk.maNV = :maNV and tk.matKhau = :matKhau and tk.isActive = true",
                        TaiKhoan.class)
                .setParameter("maNV", maNV)
                .setParameter("matKhau", matKhau)
                .getResultStream()
                .findFirst());
    }

    @Override
    public List<TaiKhoan> findAllActive() {
        return execute(em -> em.createQuery(
                        "select tk from TaiKhoan tk where tk.isActive = true",
                        TaiKhoan.class)
                .getResultList());
    }

    @Override
    public Optional<TaiKhoan> findById(String maNV) {
        return execute(em -> Optional.ofNullable(em.find(TaiKhoan.class, maNV)));
    }
}
