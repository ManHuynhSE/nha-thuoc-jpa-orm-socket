/* ============================================================
   MariaDB version - TRIGGERS & PROCEDURES (converted from MSSQL)
   ============================================================ */

-- Khuyến nghị dùng đúng charset để thay thế NVARCHAR/N'...'
-- (phần này tùy bạn đặt khi tạo database)
-- SET NAMES utf8mb4;

-- ============================================================
-- CẬP NHẬT SỐ LƯỢNG TỒN (THỦ CÔNG) - MariaDB
-- MSSQL: UPDATE T ... FROM Thuoc T LEFT JOIN (subquery) Sub ...
-- MariaDB: dùng UPDATE ... LEFT JOIN ...
-- ============================================================
UPDATE Thuoc T
    LEFT JOIN (
    SELECT maThuoc, SUM(soLuong) AS TotalQuantity
    FROM ChiTietLoThuoc
    WHERE isActive = 1
    GROUP BY maThuoc
    ) Sub ON T.maThuoc = Sub.maThuoc
    SET T.soLuongTon = IFNULL(Sub.TotalQuantity, 0);

-- ============================================================
-- TRIGGERS
-- ============================================================
DELIMITER //

/* ------------------------------------------------------------
   tr_CapNhatSoLuongTon (MSSQL: AFTER INSERT, UPDATE, DELETE)
   MariaDB phải tách 3 triggers: AFTER INSERT/UPDATE/DELETE.
   Logic: cập nhật Thuoc.soLuongTon = SUM(ChiTietLoThuoc.soLuong)
          theo từng maThuoc bị ảnh hưởng.
------------------------------------------------------------ */

CREATE TRIGGER tr_CapNhatSoLuongTon_AI
    AFTER INSERT ON ChiTietLoThuoc
    FOR EACH ROW
BEGIN
    UPDATE Thuoc
    SET soLuongTon = IFNULL((
                                SELECT SUM(soLuong)
                                FROM ChiTietLoThuoc
                                WHERE maThuoc = NEW.maThuoc AND isActive = 1
                            ), 0)
    WHERE maThuoc = NEW.maThuoc;
END//

CREATE TRIGGER tr_CapNhatSoLuongTon_AU
    AFTER UPDATE ON ChiTietLoThuoc
    FOR EACH ROW
BEGIN
    -- Nếu đổi maThuoc thì phải cập nhật cả maThuoc cũ và mới
    IF (OLD.maThuoc <> NEW.maThuoc) THEN
    UPDATE Thuoc
    SET soLuongTon = IFNULL((
                                SELECT SUM(soLuong)
                                FROM ChiTietLoThuoc
                                WHERE maThuoc = OLD.maThuoc AND isActive = 1
                            ), 0)
    WHERE maThuoc = OLD.maThuoc;
END IF;

UPDATE Thuoc
SET soLuongTon = IFNULL((
                            SELECT SUM(soLuong)
                            FROM ChiTietLoThuoc
                            WHERE maThuoc = NEW.maThuoc AND isActive = 1
                        ), 0)
WHERE maThuoc = NEW.maThuoc;
END//

CREATE TRIGGER tr_CapNhatSoLuongTon_AD
    AFTER DELETE ON ChiTietLoThuoc
    FOR EACH ROW
BEGIN
    UPDATE Thuoc
    SET soLuongTon = IFNULL((
                                SELECT SUM(soLuong)
                                FROM ChiTietLoThuoc
                                WHERE maThuoc = OLD.maThuoc AND isActive = 1
                            ), 0)
    WHERE maThuoc = OLD.maThuoc;
END//

/* ------------------------------------------------------------
   tr_CapNhatDiemTichLuy (MSSQL: AFTER INSERT ON HoaDon)
   MSSQL dùng INSERTED table + join ChiTietHoaDon
   MariaDB: FOR EACH ROW, dùng NEW.maHoaDon, NEW.maKH
   Lưu ý: nếu insert HoaDon trước rồi mới insert ChiTietHoaDon
         thì trigger này chạy khi chưa có chi tiết => điểm = 0.
         (MSSQL cũng gặp tình huống tương tự nếu dữ liệu chi tiết
          chưa tồn tại lúc trigger chạy.)
------------------------------------------------------------ */
CREATE TRIGGER tr_CapNhatDiemTichLuy_AI
    AFTER INSERT ON HoaDon
    FOR EACH ROW
BEGIN
    IF NEW.maKH IS NOT NULL THEN
    UPDATE KhachHang
    SET diemTichLuy = IFNULL(diemTichLuy, 0) + IFNULL((
                                                          SELECT FLOOR(SUM(ct.soLuong * ct.donGia) / 1000)
                                                          FROM ChiTietHoaDon ct
                                                          WHERE ct.maHoaDon = NEW.maHoaDon
                                                            AND ct.isActive = 1
                                                      ), 0)
    WHERE maKH = NEW.maKH;
END IF;
END//

/* ------------------------------------------------------------
   tr_DongBoAnNhanVien (MSSQL: AFTER UPDATE ON NhanVien)
   MSSQL cập nhật TaiKhoan.isActive theo NhanVien.isActive.
   MariaDB: FOR EACH ROW. Chỉ cần set theo NEW.isActive.
------------------------------------------------------------ */
CREATE TRIGGER tr_DongBoAnNhanVien_AU
    AFTER UPDATE ON NhanVien
    FOR EACH ROW
BEGIN
    -- Đồng bộ trạng thái y hệt nhân viên
    UPDATE TaiKhoan
    SET isActive = NEW.isActive
    WHERE maNV = NEW.maNV;
END//

DELIMITER ;

-- ============================================================
-- PROCEDURES
-- ============================================================
DELIMITER //

/* ------------------------------------------------------------
   sp_CapNhatThuocHetHan
   MSSQL: TRY/CATCH + TRANSACTION, UPDATE theo GETDATE()
   MariaDB: dùng START TRANSACTION/COMMIT + EXIT HANDLER rollback
------------------------------------------------------------ */
CREATE PROCEDURE sp_CapNhatThuocHetHan()
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
BEGIN
ROLLBACK;
END;

START TRANSACTION;

UPDATE ChiTietLoThuoc
SET isActive = 0
WHERE hanSuDung < NOW()
  AND isActive = 1;

COMMIT;
END//

/* ------------------------------------------------------------
   sp_CapNhatTrangThaiBangGia
------------------------------------------------------------ */
CREATE PROCEDURE sp_CapNhatTrangThaiBangGia()
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
BEGIN
ROLLBACK;
END;

START TRANSACTION;

UPDATE BangGia
SET trangThai = 'Đã kết thúc'
WHERE ngayKetThuc < NOW()
  AND isActive = 1;

UPDATE BangGia
SET trangThai = 'Đang áp dụng'
WHERE ngayApDung <= NOW()
  AND ngayKetThuc >= NOW()
  AND isActive = 1;

COMMIT;
END//

/* ------------------------------------------------------------
   sp_ThongKeDoanhThuTheoThang(@Nam)
------------------------------------------------------------ */
CREATE PROCEDURE sp_ThongKeDoanhThuTheoThang(IN pNam INT)
BEGIN
SELECT
    MONTH(ngayBan) AS Thang,
    SUM(ct.soLuong * ct.donGia) AS DoanhThu
FROM HoaDon hd
    INNER JOIN ChiTietHoaDon ct ON hd.maHoaDon = ct.maHoaDon
WHERE YEAR(ngayBan) = pNam
  AND hd.isActive = 1
  AND ct.isActive = 1
GROUP BY MONTH(ngayBan)
ORDER BY Thang;
END//

/* ------------------------------------------------------------
   sp_ThongKeDoanhThuNhanVien(@MaNV, @Nam)
   MSSQL dùng table variable @Thang.
   MariaDB: tạo bảng tạm TEMPORARY TABLE, hoặc UNION ALL.
   Ở đây dùng TEMPORARY TABLE cho dễ đọc.
------------------------------------------------------------ */
CREATE PROCEDURE sp_ThongKeDoanhThuNhanVien(IN pMaNV VARCHAR(50), IN pNam INT)
BEGIN
    DROP TEMPORARY TABLE IF EXISTS tmp_Thang;
    CREATE TEMPORARY TABLE tmp_Thang (Thang INT NOT NULL PRIMARY KEY);

INSERT INTO tmp_Thang (Thang) VALUES
                                  (1),(2),(3),(4),(5),(6),(7),(8),(9),(10),(11),(12);

SELECT
    nv.maNV,
    nv.tenNV,
    t.Thang AS `Tháng`,
    IFNULL(COUNT(DISTINCT hd.maHoaDon), 0) AS `Số hóa đơn`,
    IFNULL(SUM(cthd.soLuong * cthd.donGia), 0) AS `Doanh thu`
FROM tmp_Thang t
         CROSS JOIN (SELECT maNV, tenNV FROM NhanVien WHERE maNV = pMaNV) nv
         LEFT JOIN HoaDon hd
                   ON MONTH(hd.ngayBan) = t.Thang
    AND YEAR(hd.ngayBan) = pNam
    AND hd.maNV = pMaNV
    AND hd.isActive = 1
    LEFT JOIN ChiTietHoaDon cthd
ON hd.maHoaDon = cthd.maHoaDon
    AND cthd.isActive = 1
GROUP BY nv.maNV, nv.tenNV, t.Thang
ORDER BY t.Thang;
END//

/* ------------------------------------------------------------
   sp_ThongKeThueTrongNam(@Nam)
------------------------------------------------------------ */
CREATE PROCEDURE sp_ThongKeThueTrongNam(IN pNam INT)
BEGIN
SELECT
    MONTH(hd.ngayBan) AS Thang,
    SUM(hd.giaTriThue * (cthd.soLuong * cthd.donGia)) AS giaTriThue
FROM HoaDon hd
    INNER JOIN ChiTietHoaDon cthd ON hd.maHoaDon = cthd.maHoaDon
WHERE YEAR(hd.ngayBan) = pNam
  AND hd.isActive = 1
  AND cthd.isActive = 1
GROUP BY MONTH(hd.ngayBan)
ORDER BY Thang;
END//

/* ------------------------------------------------------------
   sp_GetSoHoaDonTheoNam(@Nam)
------------------------------------------------------------ */
CREATE PROCEDURE sp_GetSoHoaDonTheoNam(IN pNam INT)
BEGIN
SELECT YEAR(ngayBan) AS Nam, COUNT(maHoaDon) AS SoHoaDon
FROM HoaDon
WHERE isActive = 1
  AND YEAR(ngayBan) = pNam
GROUP BY YEAR(ngayBan);
END//

/* ------------------------------------------------------------
   sp_GetSoHoaDonTheoThang(@Thang, @Nam)
------------------------------------------------------------ */
DROP PROCEDURE IF EXISTS sp_GetSoHoaDonTheoThang//
CREATE PROCEDURE sp_GetSoHoaDonTheoThang(IN pThang INT, IN pNam INT)
BEGIN
    SELECT COUNT(*) AS SoHoaDon
    FROM HoaDon
    WHERE YEAR(ngayBan) = pNam
      AND MONTH(ngayBan) = pThang
      AND isActive = 1;
END//

/* ------------------------------------------------------------
   sp_ThongKeDoanhThuTheoNgay(@Thang, @Nam)
------------------------------------------------------------ */
CREATE PROCEDURE sp_ThongKeDoanhThuTheoNgay(IN pThang INT, IN pNam INT)
BEGIN
SELECT
    DAY(HD.ngayBan) AS Ngay,
    MONTH(HD.ngayBan) AS Thang,
    YEAR(HD.ngayBan) AS Nam,
    SUM(CTHD.soLuong * CTHD.donGia) AS doanhThu
FROM HoaDon HD
    JOIN ChiTietHoaDon CTHD ON HD.maHoaDon = CTHD.maHoaDon
WHERE YEAR(HD.ngayBan) = pNam
  AND MONTH(HD.ngayBan) = pThang
  AND HD.isActive = 1
  AND CTHD.isActive = 1
GROUP BY DAY(HD.ngayBan), MONTH(HD.ngayBan), YEAR(HD.ngayBan)
ORDER BY Ngay;
END//

/* ------------------------------------------------------------
   sp_GetDoanhThuCuaNam(@Nam)
------------------------------------------------------------ */
CREATE PROCEDURE sp_GetDoanhThuCuaNam(IN pNam INT)
BEGIN
SELECT
    pNam AS Nam,
    IFNULL(SUM(cthd.soLuong * cthd.donGia), 0) AS TongDoanhThu
FROM HoaDon hd
         INNER JOIN ChiTietHoaDon cthd ON hd.maHoaDon = cthd.maHoaDon
WHERE YEAR(hd.ngayBan) = pNam
  AND hd.isActive = 1
  AND cthd.isActive = 1;
END//

/* ------------------------------------------------------------
   sp_GetDoanhThuCuaThang(@Thang, @Nam)
------------------------------------------------------------ */

DROP PROCEDURE IF EXISTS sp_GetDoanhThuCuaThang//
CREATE PROCEDURE sp_GetDoanhThuCuaThang(IN pThang INT, IN pNam INT)
BEGIN
    SELECT IFNULL(SUM(cthd.soLuong * cthd.donGia), 0) AS TongDoanhThu
    FROM HoaDon hd
             JOIN ChiTietHoaDon cthd ON hd.maHoaDon = cthd.maHoaDon
    WHERE YEAR(hd.ngayBan) = pNam
      AND MONTH(hd.ngayBan) = pThang
      AND hd.isActive = 1
      AND cthd.isActive = 1;
END//

/* ------------------------------------------------------------
   sp_LayDanhSachThuocKemGia
   MSSQL dùng CTE + ROW_NUMBER OVER(PARTITION BY...)
   MariaDB 10.2+ hỗ trợ window function => giữ nguyên được.
   GETDATE() => NOW()
------------------------------------------------------------ */
CREATE PROCEDURE sp_LayDanhSachThuocKemGia()
BEGIN
WITH RankedPrice AS (
    SELECT
        T.maThuoc,
        T.tenThuoc,
        T.soLuongTon,
        CTBG.donGia,
        DV.tenDonVi,
        T.soLuongToiThieu,
        T.maNSX,
        T.isActive,
        ROW_NUMBER() OVER (
                PARTITION BY CTBG.maThuoc
                ORDER BY
                    BG.doUuTien DESC,
                    BG.ngayApDung DESC,
                    BG.maBangGia DESC
            ) AS rn
    FROM ChiTietBangGia CTBG
             JOIN BangGia BG ON BG.maBangGia = CTBG.maBangGia
             JOIN Thuoc T ON T.maThuoc = CTBG.maThuoc
             JOIN DonVi DV ON DV.maDonVi = T.maDonVi
    WHERE BG.trangThai = 'Đang áp dụng'
      AND NOW() >= BG.ngayApDung
      AND NOW() <= BG.ngayKetThuc
)
SELECT
    maThuoc,
    tenThuoc,
    soLuongTon,
    donGia,
    tenDonVi,
    soLuongToiThieu,
    maNSX,
    isActive
FROM RankedPrice
WHERE rn = 1 AND isActive = 1;
END//

/* ------------------------------------------------------------
   sp_LayDanhSachThuocKemGiaChuanVaGiaHienTai
------------------------------------------------------------ */
CREATE PROCEDURE sp_LayDanhSachThuocKemGiaChuanVaGiaHienTai()
BEGIN
WITH RankedPrice AS (
    SELECT
        T.maThuoc,
        T.tenThuoc,
        T.soLuongTon,
        CTBG.donGia,
        DV.tenDonVi,
        T.soLuongToiThieu,
        T.maNSX,
        T.isActive,
        ROW_NUMBER() OVER (
                PARTITION BY CTBG.maThuoc
                ORDER BY
                    BG.doUuTien DESC,
                    BG.ngayApDung DESC,
                    BG.maBangGia DESC
            ) AS rn
    FROM ChiTietBangGia CTBG
             JOIN BangGia BG ON BG.maBangGia = CTBG.maBangGia
             JOIN Thuoc T ON T.maThuoc = CTBG.maThuoc
             JOIN DonVi DV ON DV.maDonVi = T.maDonVi
    WHERE BG.trangThai = 'Đang áp dụng'
      AND NOW() >= BG.ngayApDung
      AND NOW() <= BG.ngayKetThuc
)
SELECT
    RP.maThuoc,
    RP.tenThuoc,
    RP.tenDonVi,
    CTBG.donGia AS giaChuan,
    RP.donGia AS giaHienTai
FROM RankedPrice RP
         JOIN ChiTietBangGia CTBG ON RP.maThuoc = CTBG.maThuoc
WHERE RP.rn = 1
  AND CTBG.maBangGia = 'BG001';
END//

/* ------------------------------------------------------------
   sp_LayDanhSachThuocKemGiaKemTenNhaSanXuat
------------------------------------------------------------ */
CREATE PROCEDURE sp_LayDanhSachThuocKemGiaKemTenNhaSanXuat()
BEGIN
WITH RankedPrice AS (
    SELECT
        CTBG.maThuoc,
        CTBG.donGia,
        ROW_NUMBER() OVER (
                PARTITION BY CTBG.maThuoc
                ORDER BY
                    BG.doUuTien DESC,
                    BG.ngayApDung DESC,
                    BG.maBangGia DESC
            ) AS rn
    FROM ChiTietBangGia CTBG
             JOIN BangGia BG ON BG.maBangGia = CTBG.maBangGia
    WHERE BG.trangThai = 'Đang áp dụng'
      AND NOW() >= BG.ngayApDung
      AND NOW() <= BG.ngayKetThuc
      AND CTBG.isActive = 1
)
SELECT
    T.maThuoc,
    T.tenThuoc,
    T.soLuongTon,
    IFNULL(RP.donGia, 0) AS donGia,
    DV.tenDonVi,
    T.soLuongToiThieu,
    NSX.tenNSX,
    T.isActive
FROM Thuoc T
         LEFT JOIN DonVi DV ON DV.maDonVi = T.maDonVi
         LEFT JOIN NhaSanXuat NSX ON NSX.maNSX = T.maNSX
         LEFT JOIN RankedPrice RP ON RP.maThuoc = T.maThuoc AND RP.rn = 1
WHERE T.isActive = 1
ORDER BY T.maThuoc;
END//

/* ------------------------------------------------------------
   sp_CapNhatGiaThuocBase(@maThuoc, @giaBase)
   MSSQL dùng RAISERROR/PRINT/TRY-CATCH.
   MariaDB: dùng SIGNAL để báo lỗi. "PRINT" không có, nên trả message
   bằng SELECT (hoặc bạn bỏ).
------------------------------------------------------------ */
CREATE PROCEDURE sp_CapNhatGiaThuocBase(IN pMaThuoc VARCHAR(50), IN pGiaBase DOUBLE)
BEGIN
    DECLARE vMaDonVi VARCHAR(50);

    -- kiểm tra tồn tại thuốc
    IF NOT EXISTS (SELECT 1 FROM Thuoc WHERE maThuoc = pMaThuoc) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Mã thuốc không tồn tại trong hệ thống!';
END IF;

    -- kiểm tra giá hợp lệ
    IF pGiaBase < 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Giá không được âm!';
END IF;

    -- lấy mã đơn vị
SELECT maDonVi INTO vMaDonVi
FROM Thuoc
WHERE maThuoc = pMaThuoc
    LIMIT 1;

-- upsert chi tiết bảng giá BG001
IF EXISTS (
        SELECT 1
        FROM ChiTietBangGia
        WHERE maBangGia = 'BG001' AND maThuoc = pMaThuoc
    ) THEN
UPDATE ChiTietBangGia
SET donGia = pGiaBase,
    maDonVi = vMaDonVi,
    isActive = 1
WHERE maBangGia = 'BG001'
  AND maThuoc = pMaThuoc;

SELECT CONCAT('Đã cập nhật giá thuốc ', pMaThuoc, ' thành ', pGiaBase) AS message;
ELSE
        INSERT INTO ChiTietBangGia (maBangGia, maThuoc, donGia, maDonVi, isActive)
        VALUES ('BG001', pMaThuoc, pGiaBase, vMaDonVi, 1);

SELECT CONCAT('Đã thêm mới giá thuốc ', pMaThuoc, ' với giá ', pGiaBase) AS message;
END IF;
END//

/* ------------------------------------------------------------
   sp_LayThuocKemGiaById(@maThuoc)
   MSSQL: SELECT TOP 1 ... ORDER BY ...
   MariaDB: LIMIT 1
------------------------------------------------------------ */
CREATE PROCEDURE sp_LayThuocKemGiaById(IN pMaThuoc VARCHAR(20))
BEGIN
SELECT
    T.maThuoc,
    T.tenThuoc,
    T.soLuongTon,
    CTBG.donGia,
    DV.tenDonVi,
    T.soLuongToiThieu,
    T.maNSX,
    T.isActive
FROM ChiTietBangGia CTBG
         JOIN BangGia BG ON BG.maBangGia = CTBG.maBangGia
         JOIN Thuoc T ON T.maThuoc = CTBG.maThuoc
         JOIN DonVi DV ON DV.maDonVi = T.maDonVi
WHERE CTBG.maThuoc = pMaThuoc
  AND BG.trangThai = 'Đang áp dụng'
  AND NOW() BETWEEN BG.ngayApDung AND BG.ngayKetThuc
ORDER BY
    BG.doUuTien DESC,
    BG.ngayApDung DESC,
    BG.maBangGia DESC
    LIMIT 1;
END//

/* ------------------------------------------------------------
   sp_GetSoKhachHangCuaNam(@Nam)
------------------------------------------------------------ */
CREATE PROCEDURE sp_GetSoKhachHangCuaNam(IN pNam INT)
BEGIN
SELECT pNam AS Nam, COUNT(DISTINCT hd.maKH) AS SoKhachHang
FROM HoaDon hd
WHERE YEAR(hd.ngayBan) = pNam
  AND hd.isActive = 1
  AND hd.maKH IS NOT NULL;
END//

/* ------------------------------------------------------------
   sp_GetSoKhachHangCuaThang(@Thang, @Nam)
------------------------------------------------------------ */
DROP PROCEDURE IF EXISTS sp_GetSoKhachHangCuaThang//
CREATE PROCEDURE sp_GetSoKhachHangCuaThang(IN pThang INT, IN pNam INT)
BEGIN
    SELECT COUNT(DISTINCT hd.maKH) AS SoKhachHang
    FROM HoaDon hd
    WHERE YEAR(hd.ngayBan) = pNam
      AND MONTH(hd.ngayBan) = pThang
      AND hd.isActive = 1
      AND hd.maKH IS NOT NULL;
END//

/* ------------------------------------------------------------
   sp_GetDoanhThuTrungBinhTheoNgay(@Thang, @Nam)
   MSSQL: EOMONTH(DATEFROMPARTS(@Nam, @Thang, 1))
   MariaDB: LAST_DAY(STR_TO_DATE(CONCAT(pNam,'-',pThang,'-01'),'%Y-%m-%d'))
------------------------------------------------------------ */
DROP PROCEDURE IF EXISTS sp_GetDoanhThuTrungBinhTheoNgay//
CREATE PROCEDURE sp_GetDoanhThuTrungBinhTheoNgay(IN pThang INT, IN pNam INT)
BEGIN
    DECLARE vTongDoanhThu DOUBLE DEFAULT 0;
    DECLARE vSoNgayTrongThang INT DEFAULT 0;
    DECLARE vNgayDauThang DATE;
    DECLARE vNgayCuoiThang DATE;

    SET vNgayDauThang = STR_TO_DATE(CONCAT(pNam, '-', LPAD(pThang,2,'0'), '-01'), '%Y-%m-%d');
    SET vNgayCuoiThang = LAST_DAY(vNgayDauThang);
    SET vSoNgayTrongThang = DAY(vNgayCuoiThang);

    SELECT IFNULL(SUM(cthd.soLuong * cthd.donGia), 0)
    INTO vTongDoanhThu
    FROM HoaDon hd
             JOIN ChiTietHoaDon cthd ON hd.maHoaDon = cthd.maHoaDon
    WHERE YEAR(hd.ngayBan) = pNam
      AND MONTH(hd.ngayBan) = pThang
      AND hd.isActive = 1
      AND cthd.isActive = 1;

    SELECT CASE
               WHEN vSoNgayTrongThang > 0 THEN vTongDoanhThu / vSoNgayTrongThang
               ELSE 0
               END AS DoanhThuTrungBinhMoiNgay;
END//

/* ------------------------------------------------------------
   sp_GetDoanhThuTrungBinhTheoThang(@Nam)
------------------------------------------------------------ */
CREATE PROCEDURE sp_GetDoanhThuTrungBinhTheoThang(IN pNam INT)
BEGIN
    DECLARE vTongDoanhThu DOUBLE DEFAULT 0;
    DECLARE vSoThangDaQua INT DEFAULT 0;
    DECLARE vThangHienTai INT DEFAULT MONTH(NOW());
    DECLARE vNamHienTai INT DEFAULT YEAR(NOW());

    IF pNam < vNamHienTai THEN
        SET vSoThangDaQua = 12;
    ELSEIF pNam = vNamHienTai THEN
        SET vSoThangDaQua = vThangHienTai;
ELSE
        SET vSoThangDaQua = 0;
END IF;

SELECT IFNULL(SUM(cthd.soLuong * cthd.donGia), 0)
INTO vTongDoanhThu
FROM HoaDon hd
         INNER JOIN ChiTietHoaDon cthd ON hd.maHoaDon = cthd.maHoaDon
WHERE YEAR(hd.ngayBan) = pNam
  AND hd.isActive = 1
  AND cthd.isActive = 1;

SELECT
    pNam AS Nam,
    vSoThangDaQua AS SoThangDaQua,
    vTongDoanhThu AS TongDoanhThu,
    CASE
        WHEN vSoThangDaQua > 0 THEN vTongDoanhThu / vSoThangDaQua
        ELSE 0
        END AS DoanhThuTrungBinhMoiThang;
END//

/* ------------------------------------------------------------
   sp_GetLoThuocDaHetHan()
------------------------------------------------------------ */
CREATE PROCEDURE sp_GetLoThuocDaHetHan()
BEGIN
SELECT
    ct.maLo,
    t.maThuoc,
    t.tenThuoc,
    ct.ngaySanXuat,
    ct.hanSuDung,
    ct.soLuong AS soLuongTon,
    DATEDIFF(CURDATE(), ct.hanSuDung) AS soNgayDaHetHan
FROM ChiTietLoThuoc ct
         INNER JOIN Thuoc t ON ct.maThuoc = t.maThuoc
WHERE ct.hanSuDung < CURDATE()
  AND ct.isActive = 0
ORDER BY ct.hanSuDung ASC;
END//

/* ------------------------------------------------------------
   sp_GetLoThuocSapHetHan(@SoNgay default 30)
------------------------------------------------------------ */
CREATE PROCEDURE sp_GetLoThuocSapHetHan(IN pSoNgay INT)
BEGIN
    DECLARE vNgayHienTai DATE DEFAULT CURDATE();
    DECLARE vNgayKiemTra DATE DEFAULT DATE_ADD(CURDATE(), INTERVAL pSoNgay DAY);

SELECT
    ct.maLo,
    t.maThuoc,
    t.tenThuoc,
    ct.ngaySanXuat,
    ct.hanSuDung,
    ct.soLuong AS soLuongTon,
    DATEDIFF(ct.hanSuDung, vNgayHienTai) AS soNgayConLai
FROM ChiTietLoThuoc ct
         INNER JOIN Thuoc t ON ct.maThuoc = t.maThuoc
WHERE ct.hanSuDung >= vNgayHienTai
  AND ct.hanSuDung <= vNgayKiemTra
  AND ct.isActive = 1
  AND ct.soLuong > 0
  AND t.isActive = 1
ORDER BY ct.hanSuDung ASC, ct.maThuoc;
END//


/* ------------------------------------------------------------
   sp_GetHoaDonTrongThang()
------------------------------------------------------------ */
CREATE PROCEDURE sp_GetHoaDonTrongThang(IN pThang INT, IN pNam INT)
BEGIN
    SELECT
        hd.maHoaDon,
        nv.tenNV,
        kh.tenKH,
        hd.ngayBan,
        hd.ghiChu,
        SUM(cthd.donGia * cthd.soLuong) AS tongTien
    FROM HoaDon hd
             LEFT JOIN KhachHang kh
                       ON hd.maKH = kh.maKH
             JOIN NhanVien nv
                  ON nv.maNV = hd.maNV
             JOIN ChiTietHoaDon cthd
                  ON cthd.maHoaDon = hd.maHoaDon
    WHERE hd.isActive = 1
      AND MONTH(hd.ngayBan) = pThang
      AND YEAR(hd.ngayBan) = pNam
      AND cthd.isActive = 1
    GROUP BY
        hd.maHoaDon, nv.tenNV, kh.tenKH, hd.ngayBan, hd.ghiChu
    ORDER BY
        hd.ngayBan DESC, hd.maHoaDon DESC;
END//

/* ------------------------------------------------------------
   sp_GetHoaDonTrongNam()
------------------------------------------------------------ */
CREATE PROCEDURE sp_GetHoaDonTrongNam( IN pNam INT)
BEGIN
    SELECT
        hd.maHoaDon,
        nv.tenNV,
        kh.tenKH,
        hd.ngayBan,
        hd.ghiChu,
        SUM(cthd.donGia * cthd.soLuong) AS tongTien
    FROM HoaDon hd
             LEFT JOIN KhachHang kh
                       ON hd.maKH = kh.maKH
             JOIN NhanVien nv
                  ON nv.maNV = hd.maNV
             JOIN ChiTietHoaDon cthd
                  ON cthd.maHoaDon = hd.maHoaDon
    WHERE hd.isActive = 1
      AND YEAR(hd.ngayBan) = pNam
      AND cthd.isActive = 1
    GROUP BY
        hd.maHoaDon, nv.tenNV, kh.tenKH, hd.ngayBan, hd.ghiChu
    ORDER BY
        hd.ngayBan DESC, hd.maHoaDon DESC;
END//


DELIMITER ;