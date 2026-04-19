package com.pillchill.migration.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.pillchill.migration.db.JPAUtil;
import com.pillchill.migration.dto.CreateHoaDonCommand;
import com.pillchill.migration.dto.HoaDonItemCommand;
import com.pillchill.migration.dto.ThuocKemGiaView;
import com.pillchill.migration.entity.BangGia;
import com.pillchill.migration.entity.ChiTietBangGia;
import com.pillchill.migration.entity.ChiTietHoaDon;
import com.pillchill.migration.entity.ChiTietLoThuoc;
import com.pillchill.migration.entity.ChucVu;
import com.pillchill.migration.entity.DonVi;
import com.pillchill.migration.entity.LoThuoc;
import com.pillchill.migration.entity.NhaSanXuat;
import com.pillchill.migration.entity.NhanVien;
import com.pillchill.migration.entity.TaiKhoan;
import com.pillchill.migration.entity.Thuoc;
import com.pillchill.migration.entity.id.ChiTietBangGiaId;
import com.pillchill.migration.entity.id.ChiTietLoThuocId;
import com.pillchill.migration.migration.HoaDonJpaDAO;
import com.pillchill.migration.migration.TaiKhoanJpaDAO;
import com.pillchill.migration.migration.ThuocJpaDAO;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

class JpaMigrationIntegrationTest {
    private String maChucVu;
    private String maNV;
    private String matKhau;
    private String maDonVi;
    private String maNSX;
    private String maThuoc;
    private String maBangGia;
    private String maLo1;
    private String maLo2;
    private String maHoaDon;

    @BeforeEach
    void setUp() {
        String tag = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        maChucVu = "CV" + tag;
        maNV = "NV" + tag;
        matKhau = "pw" + tag;
        maDonVi = "DV" + tag;
        maNSX = "NSX" + tag;
        maThuoc = "T" + tag;
        maBangGia = "BG" + tag;
        maLo1 = "LO" + tag + "A";
        maLo2 = "LO" + tag + "B";
        maHoaDon = "HD" + tag;

        try (EntityManager em = JPAUtil.getEntityManager()) {
            em.getTransaction().begin();

            ChucVu chucVu = ChucVu.builder().maChucVu(maChucVu).tenChucVu("Test role").isActive(true).build();
            em.persist(chucVu);

            NhanVien nhanVien = NhanVien.builder()
                    .maNV(maNV)
                    .tenNV("Test NV")
                    .chucVu(chucVu)
                    .soDienThoai("0900000000")
                    .isActive(true)
                    .build();
            em.persist(nhanVien);

            TaiKhoan taiKhoan = TaiKhoan.builder()
                    .maNV(maNV)
                    .nhanVien(nhanVien)
                    .matKhau(matKhau)
                    .isActive(true)
                    .build();
            em.persist(taiKhoan);

            DonVi donVi = DonVi.builder().maDonVi(maDonVi).tenDonVi("Hop").isActive(true).build();
            em.persist(donVi);

            NhaSanXuat nhaSanXuat = NhaSanXuat.builder()
                    .maNSX(maNSX)
                    .tenNSX("NSX Test")
                    .diaChiNSX("Dia chi test")
                    .soDienThoai("0281234567")
                    .isActive(true)
                    .build();
            em.persist(nhaSanXuat);

            Thuoc thuoc = Thuoc.builder()
                    .maThuoc(maThuoc)
                    .tenThuoc("Thuoc test")
                    .soLuongTon(15)
                    .donVi(donVi)
                    .soLuongToiThieu(1)
                    .nhaSanXuat(nhaSanXuat)
                    .isActive(true)
                    .build();
            em.persist(thuoc);

            LoThuoc lo1 = LoThuoc.builder().maLo(maLo1).nhaSanXuat(nhaSanXuat).isActive(true).build();
            LoThuoc lo2 = LoThuoc.builder().maLo(maLo2).nhaSanXuat(nhaSanXuat).isActive(true).build();
            em.persist(lo1);
            em.persist(lo2);

            ChiTietLoThuoc chiTietLo1 = ChiTietLoThuoc.builder()
                    .id(new ChiTietLoThuocId(maLo1, maThuoc))
                    .loThuoc(lo1)
                    .thuoc(thuoc)
                    .ngaySanXuat(LocalDate.now().minusDays(10))
                    .hanSuDung(LocalDate.now().plusDays(365))
                    .soLuong(5)
                    .giaNhap(10000d)
                    .isActive(true)
                    .build();
            ChiTietLoThuoc chiTietLo2 = ChiTietLoThuoc.builder()
                    .id(new ChiTietLoThuocId(maLo2, maThuoc))
                    .loThuoc(lo2)
                    .thuoc(thuoc)
                    .ngaySanXuat(LocalDate.now().minusDays(5))
                    .hanSuDung(LocalDate.now().plusDays(365))
                    .soLuong(10)
                    .giaNhap(10000d)
                    .isActive(true)
                    .build();
            em.persist(chiTietLo1);
            em.persist(chiTietLo2);

            BangGia bangGia = BangGia.builder()
                    .maBangGia(maBangGia)
                    .tenBangGia("Bang gia test")
                    .loaiGia("LE")
                    .ngayApDung(LocalDate.now().minusDays(1))
                    .ngayKetThuc(LocalDate.now().plusDays(30))
                    .trangThai("ACTIVE")
                    .ghiChu("IT")
                    .doUuTien(99)
                    .isActive(true)
                    .build();
            em.persist(bangGia);

            ChiTietBangGia chiTietBangGia = ChiTietBangGia.builder()
                    .id(new ChiTietBangGiaId(maBangGia, maThuoc))
                    .bangGia(bangGia)
                    .thuoc(thuoc)
                    .donGia(15000d)
                    .donVi(donVi)
                    .isActive(true)
                    .build();
            em.persist(chiTietBangGia);

            em.getTransaction().commit();
        }
    }

    @AfterEach
    void tearDown() {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            em.getTransaction().begin();

            em.createQuery("delete from ChiTietHoaDon c where c.id.maHoaDon = :maHoaDon")
                    .setParameter("maHoaDon", maHoaDon)
                    .executeUpdate();
            em.createQuery("delete from HoaDon h where h.maHoaDon = :maHoaDon")
                    .setParameter("maHoaDon", maHoaDon)
                    .executeUpdate();

            em.createQuery("delete from ChiTietLoThuoc c where c.id.maThuoc = :maThuoc")
                    .setParameter("maThuoc", maThuoc)
                    .executeUpdate();
            em.createQuery("delete from ChiTietBangGia c where c.id.maBangGia = :maBangGia and c.id.maThuoc = :maThuoc")
                    .setParameter("maBangGia", maBangGia)
                    .setParameter("maThuoc", maThuoc)
                    .executeUpdate();

            em.createQuery("delete from BangGia b where b.maBangGia = :maBangGia")
                    .setParameter("maBangGia", maBangGia)
                    .executeUpdate();
            em.createQuery("delete from LoThuoc l where l.maLo in :maLos")
                    .setParameter("maLos", List.of(maLo1, maLo2))
                    .executeUpdate();
            em.createQuery("delete from Thuoc t where t.maThuoc = :maThuoc")
                    .setParameter("maThuoc", maThuoc)
                    .executeUpdate();

            em.createQuery("delete from TaiKhoan t where t.maNV = :maNV")
                    .setParameter("maNV", maNV)
                    .executeUpdate();
            em.createQuery("delete from NhanVien n where n.maNV = :maNV")
                    .setParameter("maNV", maNV)
                    .executeUpdate();
            em.createQuery("delete from ChucVu c where c.maChucVu = :maChucVu")
                    .setParameter("maChucVu", maChucVu)
                    .executeUpdate();
            em.createQuery("delete from DonVi d where d.maDonVi = :maDonVi")
                    .setParameter("maDonVi", maDonVi)
                    .executeUpdate();
            em.createQuery("delete from NhaSanXuat n where n.maNSX = :maNSX")
                    .setParameter("maNSX", maNSX)
                    .executeUpdate();

            em.getTransaction().commit();
        }
    }

    @AfterAll
    static void shutdownJpa() {
        JPAUtil.shutdown();
    }

    @Test
    void entityMappingsShouldMatchMariaDbSchema() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(
                "mariadb-pu",
                Map.of("hibernate.hbm2ddl.auto", "validate"));
        try (EntityManager em = emf.createEntityManager()) {
            em.createNativeQuery("SELECT 1").getSingleResult();
        } finally {
            emf.close();
        }
    }

    @Test
    void loginFlowShouldWorkWithJpaAdapter() {
        TaiKhoanJpaDAO taiKhoanJpaDAO = new TaiKhoanJpaDAO();

        TaiKhoan taiKhoan = taiKhoanJpaDAO.kiemTraDangNhap(maNV, matKhau);
        assertNotNull(taiKhoan);
        assertEquals(maNV, taiKhoan.getMaNV());
    }

    @Test
    void listMedicineWithPriceShouldWorkWithJpaAdapter() {
        ThuocJpaDAO thuocJpaDAO = new ThuocJpaDAO();
        List<ThuocKemGiaView> thuocKemGiaViews = thuocJpaDAO.getAllThuocKemGia();

        ThuocKemGiaView thuocView = thuocKemGiaViews.stream()
                .filter(t -> t.maThuoc().equals(maThuoc))
                .findFirst()
                .orElseThrow();

        assertEquals(15000d, thuocView.giaBan(), 0.001d);
        assertEquals(15, thuocView.soLuongTon());
    }

    @Test
    void invoiceCreationShouldDeductStockByFifo() {
        HoaDonJpaDAO hoaDonJpaDAO = new HoaDonJpaDAO();
        CreateHoaDonCommand command = new CreateHoaDonCommand(
                maHoaDon,
                LocalDate.now(),
                "hoa don test",
                maNV,
                null,
                null,
                0.0d,
                "VAT",
                List.of(new HoaDonItemCommand(maThuoc, 7, 15000f)));

        assertTrue(hoaDonJpaDAO.addHoaDon(command));

        try (EntityManager em = JPAUtil.getEntityManager()) {
            ChiTietLoThuoc lo1 = em.find(ChiTietLoThuoc.class, new ChiTietLoThuocId(maLo1, maThuoc));
            ChiTietLoThuoc lo2 = em.find(ChiTietLoThuoc.class, new ChiTietLoThuocId(maLo2, maThuoc));
            Thuoc thuoc = em.find(Thuoc.class, maThuoc);

            List<ChiTietHoaDon> chiTietHoaDons = em.createQuery(
                            "select c from ChiTietHoaDon c where c.id.maHoaDon = :maHoaDon order by c.id.maLo",
                            ChiTietHoaDon.class)
                    .setParameter("maHoaDon", maHoaDon)
                    .getResultList();

            assertEquals(0, lo1.getSoLuong());
            assertEquals(8, lo2.getSoLuong());
            assertEquals(8, thuoc.getSoLuongTon());

            assertEquals(2, chiTietHoaDons.size());
            assertEquals(maLo1, chiTietHoaDons.get(0).getId().getMaLo());
            assertEquals(5, chiTietHoaDons.get(0).getSoLuong());
            assertEquals(maLo2, chiTietHoaDons.get(1).getId().getMaLo());
            assertEquals(2, chiTietHoaDons.get(1).getSoLuong());
        }
    }
}
