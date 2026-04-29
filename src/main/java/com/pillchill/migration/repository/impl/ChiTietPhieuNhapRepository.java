package com.pillchill.migration.repository.impl;

import com.pillchill.migration.dto.ChiTietPhieuNhapView;
import com.pillchill.migration.repository.IChiTietPhieuNhapRepository;

import java.util.List;

public class ChiTietPhieuNhapRepository extends RepositoryTemplate implements IChiTietPhieuNhapRepository {
    @Override
    public List<ChiTietPhieuNhapView> findViewByMaPhieuNhap(String maPhieuNhapThuoc) {
        return execute(em -> em.createQuery(
                        "select new com.pillchill.migration.dto.ChiTietPhieuNhapView(" +
                                "c.id.maPhieuNhapThuoc, c.id.maThuoc, c.id.maLo, t.tenThuoc, c.soLuong, c.donGia, dv.tenDonVi, nsx.maNSX, clt.ngaySanXuat, clt.hanSuDung) " +
                                "from ChiTietPhieuNhap c " +
                                "join c.thuoc t " +
                                "left join t.donVi dv " +
                                "left join t.nhaSanXuat nsx " +
                                "left join ChiTietLoThuoc clt on clt.id.maThuoc = c.id.maThuoc and clt.id.maLo = c.id.maLo " +
                                "where c.id.maPhieuNhapThuoc = :maPhieuNhapThuoc and c.isActive = true and t.isActive = true " +
                                "and (clt is null or clt.isActive = true) " +
                                "order by c.id.maThuoc, c.id.maLo",
                        ChiTietPhieuNhapView.class)
                .setParameter("maPhieuNhapThuoc", maPhieuNhapThuoc)
                .getResultList());
    }
}
