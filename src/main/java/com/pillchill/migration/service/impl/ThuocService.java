package com.pillchill.migration.service.impl;

import java.util.List;
import java.util.Optional;

import com.pillchill.migration.db.JPAUtil;
import com.pillchill.migration.dto.ThuocTheoLoView;
import com.pillchill.migration.entity.ChiTietLoThuoc;
import com.pillchill.migration.entity.DonVi;
import com.pillchill.migration.entity.NhaSanXuat;
import com.pillchill.migration.dto.ThuocKemGiaView;
import com.pillchill.migration.entity.Thuoc;
import com.pillchill.migration.repository.IChiTietLoThuocRepository;
import com.pillchill.migration.repository.IGiaThuocRepository;
import com.pillchill.migration.repository.IThuocRepository;
import com.pillchill.migration.repository.impl.ChiTietLoThuocRepository;
import com.pillchill.migration.repository.impl.GiaThuocRepository;
import com.pillchill.migration.repository.impl.ThuocRepository;
import com.pillchill.migration.service.IThuocService;
import jakarta.persistence.EntityManager;

public class ThuocService implements IThuocService {
    private final IThuocRepository thuocRepository;
    private final IGiaThuocRepository giaThuocRepository;
    private final IChiTietLoThuocRepository chiTietLoThuocRepository;

    public ThuocService(IThuocRepository thuocRepository, IGiaThuocRepository giaThuocRepository, IChiTietLoThuocRepository chiTietLoThuocRepository) {
        this.thuocRepository = thuocRepository;
        this.giaThuocRepository = giaThuocRepository;
        this.chiTietLoThuocRepository = chiTietLoThuocRepository;
    }

    public ThuocService(IThuocRepository thuocRepository, IGiaThuocRepository giaThuocRepository) {
        this(thuocRepository, giaThuocRepository, new ChiTietLoThuocRepository());
    }

    public ThuocService() {
        this(new ThuocRepository(), new GiaThuocRepository(), new ChiTietLoThuocRepository());
    }

    @Override
    public List<Thuoc> getAllThuoc() {
        return thuocRepository.findAllActive();
    }

    @Override
    public Optional<Thuoc> getThuocById(String maThuoc) {
        return thuocRepository.findById(maThuoc);
    }

    @Override
    public List<ThuocKemGiaView> getAllThuocKemGia() {
        List<Thuoc> thuocList = thuocRepository.findAllActive();
        return  thuocList
                .stream()
                .map(thuoc -> {
                    return toThuocKemGiaView(thuoc);
                })
                .toList();
    }

    @Override
    public List<ThuocTheoLoView> getAllThuocTheoLo() {
        return chiTietLoThuocRepository.findAllActiveWithThuocAndLo()
                .stream()
                .map(this::toThuocTheoLoView)
                .toList();
    }

    @Override
    public Thuoc createThuoc(Thuoc thuoc) {
        DonVi donVi = resolveDonVi(thuoc.getDonVi())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn vị hợp lệ"));
        NhaSanXuat nhaSanXuat = resolveNhaSanXuat(thuoc.getNhaSanXuat())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy nhà sản xuất hợp lệ"));

        Thuoc created = Thuoc.builder()
                .maThuoc(thuoc.getMaThuoc())
                .tenThuoc(thuoc.getTenThuoc())
                .soLuongTon(thuoc.getSoLuongTon())
                .donVi(donVi)
                .soLuongToiThieu(thuoc.getSoLuongToiThieu() == null ? 0 : thuoc.getSoLuongToiThieu())
                .nhaSanXuat(nhaSanXuat)
                .isActive(true)
                .build();
        return thuocRepository.create(created);
    }

    @Override
    public Thuoc createThuoc(Thuoc thuoc, double giaBanCoSo) {
        Thuoc created = createThuoc(thuoc);
        giaThuocRepository.upsertGiaCoSo(created.getMaThuoc(), created.getDonVi().getMaDonVi(), giaBanCoSo);
        return created;
    }

    @Override
    public Thuoc updateThuoc(Thuoc thuoc) {
        Thuoc existing = thuocRepository.findById(thuoc.getMaThuoc())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thuốc: " + thuoc.getMaThuoc()));
        DonVi donVi = resolveDonVi(thuoc.getDonVi())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn vị hợp lệ"));
        NhaSanXuat nhaSanXuat = resolveNhaSanXuat(thuoc.getNhaSanXuat())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy nhà sản xuất hợp lệ"));

        existing.setTenThuoc(thuoc.getTenThuoc());
        existing.setSoLuongTon(thuoc.getSoLuongTon());
        existing.setDonVi(donVi);
        existing.setSoLuongToiThieu(thuoc.getSoLuongToiThieu() == null ? 0 : thuoc.getSoLuongToiThieu());
        existing.setNhaSanXuat(nhaSanXuat);
        existing.setActive(true);
        return thuocRepository.update(existing);
    }


    @Override
    public boolean deactivateThuoc(String maThuoc) {
        return thuocRepository.deactivateThuoc(maThuoc);
    }

    private ThuocKemGiaView toThuocKemGiaView(Thuoc thuoc) {
        double gia = giaThuocRepository.getGiaHienTaiByMaThuoc(thuoc.getMaThuoc()).orElse(0.0);
        String maDonVi = thuoc.getDonVi() == null ? null : thuoc.getDonVi().getTenDonVi();
        String maNSX = thuoc.getNhaSanXuat() == null ? null : thuoc.getNhaSanXuat().getTenNSX();
        return new ThuocKemGiaView(
                thuoc.getMaThuoc(),
                thuoc.getTenThuoc(),
                thuoc.getSoLuongTon(),
                gia,
                maDonVi,
                thuoc.getSoLuongToiThieu() == null ? 0 : thuoc.getSoLuongToiThieu(),
                maNSX,
                thuoc.isActive()
        );
    }

    private ThuocTheoLoView toThuocTheoLoView(ChiTietLoThuoc chiTietLoThuoc) {
        Thuoc thuoc = chiTietLoThuoc.getThuoc();
        String maDonVi = thuoc.getDonVi() == null ? null : thuoc.getDonVi().getTenDonVi();
        String maNSX = thuoc.getNhaSanXuat() == null ? null : thuoc.getNhaSanXuat().getTenNSX();
        return new ThuocTheoLoView(
                thuoc.getMaThuoc(),
                thuoc.getTenThuoc(),
                chiTietLoThuoc.getLoThuoc().getMaLo(),
                chiTietLoThuoc.getHanSuDung(),
                chiTietLoThuoc.getSoLuong(),
                maDonVi,
                maNSX
        );
    }

    private Optional<DonVi> findDonViByTen(String tenDonVi) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            return em.createQuery(
                            "select dv from DonVi dv where dv.tenDonVi = :tenDonVi",
                            DonVi.class)
                    .setParameter("tenDonVi", tenDonVi)
                    .setMaxResults(1)
                    .getResultStream()
                    .findFirst();
        }
    }

    private Optional<NhaSanXuat> findNhaSanXuatByTen(String tenNhaSanXuat) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            return em.createQuery(
                            "select nsx from NhaSanXuat nsx where nsx.tenNSX = :tenNhaSanXuat",
                            NhaSanXuat.class)
                    .setParameter("tenNhaSanXuat", tenNhaSanXuat)
                    .setMaxResults(1)
                    .getResultStream()
                    .findFirst();
        }
    }

    private Optional<DonVi> resolveDonVi(DonVi donViInput) {
        if (donViInput == null) {
            return Optional.empty();
        }
        if (donViInput.getMaDonVi() != null && !donViInput.getMaDonVi().isBlank()) {
            try (EntityManager em = JPAUtil.getEntityManager()) {
                return Optional.ofNullable(em.find(DonVi.class, donViInput.getMaDonVi()));
            }
        }
        if (donViInput.getTenDonVi() != null && !donViInput.getTenDonVi().isBlank()) {
            return findDonViByTen(donViInput.getTenDonVi());
        }
        return Optional.empty();
    }

    private Optional<NhaSanXuat> resolveNhaSanXuat(NhaSanXuat nsxInput) {
        if (nsxInput == null) {
            return Optional.empty();
        }
        if (nsxInput.getMaNSX() != null && !nsxInput.getMaNSX().isBlank()) {
            try (EntityManager em = JPAUtil.getEntityManager()) {
                return Optional.ofNullable(em.find(NhaSanXuat.class, nsxInput.getMaNSX()));
            }
        }
        if (nsxInput.getTenNSX() != null && !nsxInput.getTenNSX().isBlank()) {
            return findNhaSanXuatByTen(nsxInput.getTenNSX());
        }
        return Optional.empty();
    }

    public static void main(String[] args) {
        ThuocService thuocService = new ThuocService();
        System.out.println(thuocService.getAllThuocTheoLo());
    }
}
