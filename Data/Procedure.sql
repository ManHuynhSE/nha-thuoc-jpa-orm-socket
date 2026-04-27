-- ===============================================
-- CẬP NHẬT BAN ĐẦU
-- ===============================================
UPDATE Thuoc T
LEFT JOIN (
    SELECT 
        maThuoc,
        SUM(soLuong) AS TotalQuantity
    FROM ChiTietLoThuoc
    WHERE isActive = 1
    GROUP BY maThuoc
) AS Sub ON T.maThuoc = Sub.maThuoc
SET T.soLuongTon = IFNULL(Sub.TotalQuantity, 0);

DELIMITER //

-- ===============================================
-- TRIGGER
-- (MariaDB yêu cầu tách riêng từng sự kiện cho FOR EACH ROW)
-- ===============================================

CREATE TRIGGER tr_CapNhatSoLuongTon_Insert
AFTER INSERT ON ChiTietLoThuoc
FOR EACH ROW
BEGIN
    UPDATE Thuoc
    SET soLuongTon = IFNULL((
        SELECT SUM(soLuong)
        FROM ChiTietLoThuoc
        WHERE ChiTietLoThuoc.maThuoc = NEW.maThuoc 
          AND ChiTietLoThuoc.isActive = 1
    ), 0)
    WHERE maThuoc = NEW.maThuoc;
END //

CREATE TRIGGER tr_CapNhatSoLuongTon_Update
AFTER UPDATE ON ChiTietLoThuoc
FOR EACH ROW
BEGIN
    UPDATE Thuoc
    SET soLuongTon = IFNULL((
        SELECT SUM(soLuong)
        FROM ChiTietLoThuoc
        WHERE ChiTietLoThuoc.maThuoc = NEW.maThuoc 
          AND ChiTietLoThuoc.isActive = 1
    ), 0)
    WHERE maThuoc = NEW.maThuoc;
    
    -- Nếu đổi mã thuốc
    IF OLD.maThuoc <> NEW.maThuoc THEN
        UPDATE Thuoc
        SET soLuongTon = IFNULL((
            SELECT SUM(soLuong)
            FROM ChiTietLoThuoc
            WHERE ChiTietLoThuoc.maThuoc = OLD.maThuoc 
              AND ChiTietLoThuoc.isActive = 1
        ), 0)
        WHERE maThuoc = OLD.maThuoc;
    END IF;
END //

CREATE TRIGGER tr_CapNhatSoLuongTon_Delete
AFTER DELETE ON ChiTietLoThuoc
FOR EACH ROW
BEGIN
    UPDATE Thuoc
    SET soLuongTon = IFNULL((
        SELECT SUM(soLuong)
        FROM ChiTietLoThuoc
        WHERE ChiTietLoThuoc.maThuoc = OLD.maThuoc 
          AND ChiTietLoThuoc.isActive = 1
    ), 0)
    WHERE maThuoc = OLD.maThuoc;
END //

CREATE TRIGGER tr_CapNhatDiemTichLuy
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
END //

CREATE TRIGGER tr_DongBoAnNhanVien
AFTER UPDATE ON NhanVien
FOR EACH ROW
BEGIN
    IF NEW.isActive = 0 THEN
        UPDATE TaiKhoan SET isActive = 0 WHERE maNV = NEW.maNV AND isActive = 1;
    ELSEIF NEW.isActive = 1 THEN
        UPDATE TaiKhoan SET isActive = 1 WHERE maNV = NEW.maNV AND isActive = 0;
    END IF;
END //

-- ===============================================
-- PROCEDURES CẬP NHẬT TRẠNG THÁI
-- ===============================================
DELIMITER //

CREATE PROCEDURE sp_CapNhatThuocHetHan()
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION 
    BEGIN
        ROLLBACK;
    END;

    START TRANSACTION;
    UPDATE ChiTietLoThuoc
    SET isActive = 0
    WHERE hanSuDung < NOW() AND isActive = 1;
    COMMIT;
END //

CREATE PROCEDURE sp_CapNhatTrangThaiBangGia()
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION 
    BEGIN
        ROLLBACK;
    END;

    START TRANSACTION;
    UPDATE BangGia
    SET trangThai = 'Đã kết thúc'
    WHERE ngayKetThuc < NOW() AND isActive = 1;

    UPDATE BangGia
    SET trangThai = 'Đang áp dụng'
    WHERE ngayApDung <= NOW()
      AND ngayKetThuc >= NOW() AND isActive = 1;
    COMMIT;
END //

-- ===============================================
-- PROCEDURES THỐNG KÊ
-- ===============================================

CREATE PROCEDURE sp_ThongKeDoanhThuTheoThang(p_Nam INT)
BEGIN
    SELECT 
        MONTH(ngayBan) AS Thang,
        SUM(ct.soLuong * ct.donGia) AS DoanhThu
    FROM HoaDon hd
    INNER JOIN ChiTietHoaDon ct ON hd.maHoaDon = ct.maHoaDon
    WHERE YEAR(ngayBan) = p_Nam AND hd.isActive = 1 AND ct.isActive = 1
    GROUP BY MONTH(ngayBan)
    ORDER BY Thang;
END //

CREATE PROCEDURE sp_ThongKeDoanhThuNhanVien(p_MaNV VARCHAR(50), p_Nam INT)
BEGIN
    WITH Thang AS (
        SELECT 1 AS Thang UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6
        UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10 UNION SELECT 11 UNION SELECT 12
    )
    SELECT 
        nv.maNV,
        nv.tenNV,
        t.Thang AS 'Tháng',
        IFNULL(COUNT(DISTINCT hd.maHoaDon), 0) AS 'Số hóa đơn',
        IFNULL(SUM(cthd.soLuong * cthd.donGia), 0) AS 'Doanh thu'
    FROM Thang t
    CROSS JOIN NhanVien nv 
    LEFT JOIN HoaDon hd ON MONTH(hd.ngayBan) = t.Thang 
                        AND YEAR(hd.ngayBan) = p_Nam 
                        AND hd.maNV = p_MaNV
                        AND hd.isActive = 1
    LEFT JOIN ChiTietHoaDon cthd ON hd.maHoaDon = cthd.maHoaDon AND cthd.isActive = 1
    WHERE nv.maNV = p_MaNV
    GROUP BY nv.maNV, nv.tenNV, t.Thang
    ORDER BY t.Thang;
END //

CREATE PROCEDURE sp_ThongKeThueTrongNam(p_Nam INT)
BEGIN
    SELECT
        MONTH(hd.ngayBan) AS `Thang`,
        SUM(hd.giaTriThue * (cthd.soLuong * cthd.donGia)) AS `giaTriThue`
    FROM HoaDon hd
    INNER JOIN ChiTietHoaDon cthd ON hd.maHoaDon = cthd.maHoaDon
    WHERE YEAR(hd.ngayBan) = p_Nam AND hd.isActive = 1 AND cthd.isActive = 1
    GROUP BY MONTH(hd.ngayBan)
    ORDER BY `Thang`;
END //

CREATE PROCEDURE sp_GetSoHoaDonTheoNam(p_Nam INT)
BEGIN
    SELECT YEAR(ngayBan) AS `Nam`, COUNT(maHoaDon) AS `SoHoaDon`
    FROM HoaDon WHERE isActive = 1 AND YEAR(ngayBan) = p_Nam
    GROUP BY YEAR(ngayBan);
END //

CREATE PROCEDURE sp_GetSoHoaDonTheoThang(p_Thang INT, p_Nam INT)
BEGIN
    SELECT MONTH(ngayBan) AS `Thang`, YEAR(ngayBan) AS `Nam`, COUNT(maHoaDon) AS `SoHoaDon`
    FROM HoaDon
    WHERE YEAR(ngayBan) = p_Nam AND isActive = 1 AND MONTH(ngayBan) = p_Thang
    GROUP BY MONTH(ngayBan), YEAR(ngayBan)
    ORDER BY `Thang`;
END //

CREATE PROCEDURE sp_ThongKeDoanhThuTheoNgay(p_Thang INT, p_Nam INT)
BEGIN
    SELECT
        DAY(ngayBan) AS `Ngay`,
        MONTH(ngayBan) AS `Thang`,
        YEAR(ngayBan) AS `Nam`,
        SUM(soLuong * donGia) AS `doanhThu`
    FROM HoaDon HD JOIN ChiTietHoaDon CTHD ON HD.maHoaDon = CTHD.maHoaDon	
    WHERE YEAR(ngayBan) = p_Nam AND MONTH(ngayBan) = p_Thang AND HD.isActive = 1
    GROUP BY DAY(ngayBan), MONTH(ngayBan), YEAR(ngayBan)
    ORDER BY `Ngay`;
END //

CREATE PROCEDURE sp_GetDoanhThuCuaNam(p_Nam INT)
BEGIN
    SELECT
        p_Nam AS `Nam`,
        IFNULL(SUM(cthd.soLuong * cthd.donGia), 0) AS `TongDoanhThu`
    FROM HoaDon hd
    INNER JOIN ChiTietHoaDon cthd ON hd.maHoaDon = cthd.maHoaDon
    WHERE YEAR(hd.ngayBan) = p_Nam AND hd.isActive = 1 AND cthd.isActive = 1;
END //

CREATE PROCEDURE sp_GetDoanhThuCuaThang(p_Thang INT, p_Nam INT)
BEGIN
    SELECT
        p_Thang AS `Thang`, p_Nam AS `Nam`,
        IFNULL(SUM(cthd.soLuong * cthd.donGia), 0) AS `TongDoanhThu`
    FROM HoaDon hd
    INNER JOIN ChiTietHoaDon cthd ON hd.maHoaDon = cthd.maHoaDon
    WHERE YEAR(hd.ngayBan) = p_Nam AND MONTH(hd.ngayBan) = p_Thang AND hd.isActive = 1 AND cthd.isActive = 1;
END //

-- ===============================================
-- PROCEDURES VỀ THUỐC & BẢNG GIÁ
-- ===============================================

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
        FROM chiTietBangGia CTBG
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
END //

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
        FROM chiTietBangGia CTBG
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
        CTBG.donGia as giaChuan,
        RP.donGia as giaHienTai
    FROM RankedPrice RP 
    JOIN ChiTietBangGia CTBG ON RP.maThuoc = CTBG.maThuoc
    WHERE rn = 1 AND CTBG.maBangGia = 'BG001';
END //

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
END //

CREATE PROCEDURE sp_CapNhatGiaThuocBase(p_maThuoc VARCHAR(50), p_giaBase FLOAT)
BEGIN
    DECLARE v_maDonVi VARCHAR(50);
    
    -- Xử lý lỗi
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Có lỗi xảy ra trong quá trình cập nhật!';
    END;

    IF NOT EXISTS (SELECT 1 FROM Thuoc WHERE maThuoc = p_maThuoc) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Mã thuốc không tồn tại trong hệ thống!';
    END IF;
    
    IF p_giaBase < 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Giá không được âm!';
    END IF;
    
    SELECT maDonVi INTO v_maDonVi FROM Thuoc WHERE maThuoc = p_maThuoc;
    
    IF EXISTS (SELECT 1 FROM ChiTietBangGia WHERE maBangGia = 'BG001' AND maThuoc = p_maThuoc) THEN
        UPDATE ChiTietBangGia
        SET donGia = p_giaBase,
            maDonVi = v_maDonVi,
            isActive = 1
        WHERE maBangGia = 'BG001' AND maThuoc = p_maThuoc;
    ELSE
        INSERT INTO ChiTietBangGia (maBangGia, maThuoc, donGia, maDonVi, isActive)
        VALUES ('BG001', p_maThuoc, p_giaBase, v_maDonVi, 1);
    END IF;
END //

CREATE PROCEDURE sp_LayThuocKemGiaById(p_maThuoc VARCHAR(20))
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
    FROM chiTietBangGia CTBG
    JOIN BangGia BG ON BG.maBangGia = CTBG.maBangGia
    JOIN Thuoc T ON T.maThuoc = CTBG.maThuoc
    JOIN DonVi DV ON DV.maDonVi = T.maDonVi
    WHERE CTBG.maThuoc = p_maThuoc
      AND BG.trangThai = 'Đang áp dụng'
      AND NOW() BETWEEN BG.ngayApDung AND BG.ngayKetThuc
    ORDER BY 
        BG.doUuTien DESC,
        BG.ngayApDung DESC,
        BG.maBangGia DESC
    LIMIT 1;
END //

-- ===============================================
-- PROCEDURES KHÁCH HÀNG & HẠN SỬ DỤNG
-- ===============================================

CREATE PROCEDURE sp_GetSoKhachHangCuaNam(p_Nam INT)
BEGIN
    SELECT p_Nam AS `Nam`, COUNT(DISTINCT hd.maKH) AS `SoKhachHang`
    FROM HoaDon hd
    WHERE YEAR(hd.ngayBan) = p_Nam AND hd.isActive = 1 AND hd.maKH IS NOT NULL;
END //

CREATE PROCEDURE sp_GetSoKhachHangCuaThang(p_Thang INT, p_Nam INT)
BEGIN
    SELECT p_Thang AS `Thang`, p_Nam AS `Nam`, COUNT(DISTINCT hd.maKH) AS `SoKhachHang`
    FROM HoaDon hd
    WHERE YEAR(hd.ngayBan) = p_Nam AND MONTH(hd.ngayBan) = p_Thang AND hd.isActive = 1 AND hd.maKH IS NOT NULL;
END //

CREATE PROCEDURE sp_GetDoanhThuTrungBinhTheoNgay(p_Thang INT, p_Nam INT)
BEGIN
    DECLARE v_TongDoanhThu FLOAT DEFAULT 0;
    DECLARE v_SoNgayTrongThang INT;
    
    SELECT IFNULL(SUM(cthd.soLuong * cthd.donGia), 0) INTO v_TongDoanhThu
    FROM HoaDon hd
    INNER JOIN ChiTietHoaDon cthd ON hd.maHoaDon = cthd.maHoaDon
    WHERE YEAR(hd.ngayBan) = p_Nam AND MONTH(hd.ngayBan) = p_Thang AND hd.isActive = 1 AND cthd.isActive = 1;
    
    SET v_SoNgayTrongThang = DAY(LAST_DAY(STR_TO_DATE(CONCAT(p_Nam, '-', p_Thang, '-01'), '%Y-%m-%d')));
    
    SELECT p_Thang AS `Thang`, p_Nam AS `Nam`, v_SoNgayTrongThang AS `SoNgayTrongThang`, v_TongDoanhThu AS `TongDoanhThu`,
        CASE WHEN v_SoNgayTrongThang > 0 THEN v_TongDoanhThu / v_SoNgayTrongThang ELSE 0 END AS `DoanhThuTrungBinhMoiNgay`;
END //

CREATE PROCEDURE sp_GetDoanhThuTrungBinhTheoThang(p_Nam INT)
BEGIN
    DECLARE v_TongDoanhThu FLOAT DEFAULT 0;
    DECLARE v_SoThangDaQua INT;
    DECLARE v_ThangHienTai INT DEFAULT MONTH(NOW());
    DECLARE v_NamHienTai INT DEFAULT YEAR(NOW());
    
    IF p_Nam < v_NamHienTai THEN 
        SET v_SoThangDaQua = 12;
    ELSEIF p_Nam = v_NamHienTai THEN 
        SET v_SoThangDaQua = v_ThangHienTai;
    ELSE 
        SET v_SoThangDaQua = 0;
    END IF;
    
    SELECT IFNULL(SUM(cthd.soLuong * cthd.donGia), 0) INTO v_TongDoanhThu
    FROM HoaDon hd
    INNER JOIN ChiTietHoaDon cthd ON hd.maHoaDon = cthd.maHoaDon
    WHERE YEAR(hd.ngayBan) = p_Nam AND hd.isActive = 1 AND cthd.isActive = 1;
    
    SELECT p_Nam AS `Nam`, v_SoThangDaQua AS `SoThangDaQua`, v_TongDoanhThu AS `TongDoanhThu`,
        CASE WHEN v_SoThangDaQua > 0 THEN v_TongDoanhThu / v_SoThangDaQua ELSE 0 END AS `DoanhThuTrungBinhMoiThang`;
END //

CREATE PROCEDURE sp_GetLoThuocDaHetHan()
BEGIN
    SELECT 
        ct.maLo, t.maThuoc, t.tenThuoc, ct.ngaySanXuat, ct.hanSuDung, 
        ct.soLuong AS 'soLuongTon', 
        DATEDIFF(NOW(), ct.hanSuDung) AS 'soNgayDaHetHan'
    FROM ChiTietLoThuoc ct
    INNER JOIN Thuoc t ON ct.maThuoc = t.maThuoc
    WHERE ct.hanSuDung < NOW() AND ct.isActive = 0
    ORDER BY ct.hanSuDung ASC;
END //

CREATE PROCEDURE sp_GetLoThuocSapHetHan(p_SoNgay INT)
BEGIN
    DECLARE v_NgayHienTai DATE DEFAULT CAST(NOW() AS DATE);
    DECLARE v_NgayKiemTra DATE;
    
    -- Nếu không truyền số ngày vào, gán mặc định là 30
    IF p_SoNgay IS NULL THEN SET p_SoNgay = 30; END IF;
    
    SET v_NgayKiemTra = DATE_ADD(v_NgayHienTai, INTERVAL p_SoNgay DAY);
    
    SELECT 
        ct.maLo, t.maThuoc, t.tenThuoc, ct.ngaySanXuat, ct.hanSuDung, 
        ct.soLuong AS 'soLuongTon', 
        DATEDIFF(ct.hanSuDung, v_NgayHienTai) AS `soNgayConLai`
    FROM ChiTietLoThuoc ct
    INNER JOIN Thuoc t ON ct.maThuoc = t.maThuoc
    WHERE ct.hanSuDung >= v_NgayHienTai 
      AND ct.hanSuDung <= v_NgayKiemTra 
      AND ct.isActive = 1 AND ct.soLuong > 0 AND t.isActive = 1                            
    ORDER BY ct.hanSuDung ASC, ct.maThuoc;         
END //

DELIMITER ;