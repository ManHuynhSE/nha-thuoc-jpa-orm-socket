USE master
GO

-- Kiểm tra nếu database tồn tại thì xóa (để làm sạch hoàn toàn)
IF EXISTS (SELECT name FROM sys.databases WHERE name = N'QuanLyNhaThuoc')
BEGIN
    ALTER DATABASE QuanLyNhaThuoc SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    DROP DATABASE QuanLyNhaThuoc;
END
GO

CREATE DATABASE QuanLyNhaThuoc;
GO

USE QuanLyNhaThuoc;
GO

-- ===============================================
-- TẠO CẤU TRÚC BẢNG
-- ===============================================

-- Tạo bảng NhaSanXuat
CREATE TABLE NhaSanXuat (
    maNSX VARCHAR(50) PRIMARY KEY,
    tenNSX NVARCHAR(100),
    diaChiNSX NVARCHAR(200),
    soDienThoai VARCHAR(20),
	isActive bit DEFAULT 1,
);
GO

-- Tạo bảng LoThuoc
CREATE TABLE LoThuoc (
    maLo VARCHAR(50) PRIMARY KEY,
    maNSX VARCHAR(50),
	isActive bit DEFAULT 1,
    FOREIGN KEY (maNSX) REFERENCES NhaSanXuat(maNSX)
);
GO

-- Tạo bảng DonVi
CREATE TABLE DonVi (
    maDonVi VARCHAR(50) PRIMARY KEY,
    tenDonVi NVARCHAR(50),
	isActive bit DEFAULT 1,
);
GO

-- Tạo bảng Thuoc
CREATE TABLE Thuoc (
    maThuoc VARCHAR(50) PRIMARY KEY,
    tenThuoc NVARCHAR(100),
    soLuongTon INT DEFAULT 0, -- Tổng số lượng tồn
    maDonVi VARCHAR(50),
    soLuongToiThieu INT,
	maNSX VARCHAR(50),
	isActive bit DEFAULT 1,
	FOREIGN KEY (maNSX) REFERENCES NhaSanXuat(maNSX),
	FOREIGN KEY (maDonVi) REFERENCES DonVi(maDonVi),
);
GO

-- Tạo bảng BangGia
CREATE TABLE BangGia (
    maBangGia VARCHAR(50) PRIMARY KEY,
    tenBangGia NVARCHAR(100),
    loaiGia NVARCHAR(50),
	ngayApDung DATE,
	ngayKetThuc DATE,
	trangThai NVARCHAR(50),
	ghiChu NVARCHAR(100),
    doUuTien INT,
	isActive bit DEFAULT 1,
);
GO

-- Tạo bảng ChiTietBangGia
CREATE TABLE ChiTietBangGia (
    maBangGia VARCHAR(50),
    maThuoc VARCHAR(50),
    donGia FLOAT,
	maDonVi VARCHAR(50),
	isActive bit DEFAULT 1,
	PRIMARY KEY (maBangGia, maThuoc),
	FOREIGN KEY (maBangGia) REFERENCES BangGia(maBangGia),
	FOREIGN KEY (maThuoc) REFERENCES Thuoc(maThuoc),
    FOREIGN KEY (maDonVi) REFERENCES DonVi(maDonVi)
);
GO

-- Tạo bảng ChiTietLoThuoc (Chi tiết tồn kho theo lô)
CREATE TABLE ChiTietLoThuoc (
    maLo VARCHAR(50),
    maThuoc VARCHAR(50),
    ngaySanXuat DATE,
    hanSuDung DATE,
    soLuong INT NOT NULL,
    giaNhap FLOAT NOT NULL,
    isActive BIT DEFAULT 1,
    PRIMARY KEY (maLo, maThuoc),
    FOREIGN KEY (maLo) REFERENCES LoThuoc(maLo),
    FOREIGN KEY (maThuoc) REFERENCES Thuoc(maThuoc)
);
GO

-- Tạo bảng KhachHang
CREATE TABLE KhachHang (
    maKH VARCHAR(50) PRIMARY KEY,
    tenKH NVARCHAR(100),
    soDienThoai VARCHAR(20),
    diemTichLuy INT,
	isActive bit DEFAULT 1,
);
GO

-- TẠO BẢNG CHUCVU
CREATE TABLE ChucVu (
    maChucVu VARCHAR(50) PRIMARY KEY,
    tenChucVu NVARCHAR(100) NOT NULL,
	isActive bit DEFAULT 1,
);
GO

-- TẠO BẢNG NHANVIEN
CREATE TABLE NhanVien (
    maNV VARCHAR(50) PRIMARY KEY,
    tenNV NVARCHAR(100),
    maChucVu VARCHAR(50), 
    soDienThoai VARCHAR(20),
	isActive bit DEFAULT 1,
	FOREIGN KEY (maChucVu) REFERENCES ChucVu(maChucVu)
);
GO

-- Tạo bảng TaiKhoan
CREATE TABLE TaiKhoan(
	maNV VARCHAR(50) PRIMARY KEY,
	matKhau VARCHAR(50),
	isActive bit DEFAULT 1,
	FOREIGN KEY (maNV)	REFERENCES NhanVien(maNV)
);
GO

-- Tạo bảng KhuyenMai
CREATE TABLE KhuyenMai (
    maKM VARCHAR(50) PRIMARY KEY,
    mucGiamGia FLOAT,
	ngayApDung DATE,
	ngayKetThuc DATE,
	isActive bit DEFAULT 1
);
GO

-- TẠO BẢNG HOADON
CREATE TABLE HoaDon (
    maHoaDon VARCHAR(50) PRIMARY KEY,
    ngayBan DATE,
    ghiChu NVARCHAR(200),
    maNV VARCHAR(50),
    maKH VARCHAR(50),
	maKM VARCHAR(50),
	giaTriThue FLOAT, 
	tenLoaiThue NVARCHAR(50), 
	isActive bit DEFAULT 1,
    FOREIGN KEY (maNV) REFERENCES NhanVien(maNV),
    FOREIGN KEY (maKH) REFERENCES KhachHang(maKH),
	FOREIGN KEY (maKM) REFERENCES KhuyenMai(maKM)
);
GO

-- Tạo bảng ChiTietHoaDon
CREATE TABLE ChiTietHoaDon (
    maHoaDon VARCHAR(50),
    maThuoc VARCHAR(50),
    maLo VARCHAR(50),
    soLuong INT,
	donGia FLOAT,
	isActive bit DEFAULT 1,
    PRIMARY KEY (maHoaDon, maThuoc, maLo),
    FOREIGN KEY (maHoaDon) REFERENCES HoaDon(maHoaDon),
    FOREIGN KEY (maThuoc) REFERENCES Thuoc(maThuoc),
	FOREIGN KEY (maLo) REFERENCES LoThuoc(maLo)
);
GO

-- Tạo bảng PhieuDoiTra
CREATE TABLE PhieuDoiTra (
    maPhieuDoiTra VARCHAR(50) PRIMARY KEY,
    ngayDoiTra DATE,
	maNV VARCHAR(50),
	maKH VARCHAR(50),
	isActive bit DEFAULT 1,
    FOREIGN KEY (maNV) REFERENCES NhanVien(maNV),
    FOREIGN KEY (maKH) REFERENCES KhachHang(maKH)
);
GO

-- Tạo bảng ChiTietPhieuDoiTra
CREATE TABLE ChiTietPhieuDoiTra (
    maPhieuDoiTra VARCHAR(50),
    maThuoc VARCHAR(50),
    soLuong INT,
    donGia FLOAT,
    maLo VARCHAR(50),
    lyDo NVARCHAR(200),
	isActive bit DEFAULT 1,
    PRIMARY KEY (maPhieuDoiTra, maThuoc, maLo),
    FOREIGN KEY (maPhieuDoiTra) REFERENCES PhieuDoiTra(maPhieuDoiTra),
    FOREIGN KEY (maThuoc) REFERENCES Thuoc(maThuoc),
    FOREIGN KEY (maLo) REFERENCES LoThuoc(maLo)
);
GO

-- Tạo bảng PhieuDat
CREATE TABLE PhieuDat (
    maPhieuDat VARCHAR(50) PRIMARY KEY,
    maNV VARCHAR(50),
    ngayDat DATE,
    maKH VARCHAR(50),
	ghiChu NVARCHAR(200),
    isReceived BIT DEFAULT 0, -- 0: Chưa nhận, 1: Đã nhận
	isActive bit DEFAULT 1,
    FOREIGN KEY (maNV) REFERENCES NhanVien(maNV),
    FOREIGN KEY (maKH) REFERENCES KhachHang(maKH)
);
GO

-- Tạo bảng ChiTietPhieuDat
CREATE TABLE ChiTietPhieuDat (
    maPhieuDat VARCHAR(50),
    maThuoc VARCHAR(50),
    maLo VARCHAR(50), 
    tenThuoc NVARCHAR(100),
    soLuong INT,
	isActive bit DEFAULT 1,
    PRIMARY KEY (maPhieuDat, maThuoc, maLo),
    FOREIGN KEY (maPhieuDat) REFERENCES PhieuDat(maPhieuDat),
    FOREIGN KEY (maThuoc) REFERENCES Thuoc(maThuoc),
	FOREIGN KEY (maLo) REFERENCES LoThuoc(maLo)
);
GO

-- Tạo bảng PhieuNhapThuoc
CREATE TABLE PhieuNhapThuoc (
    maPhieuNhapThuoc VARCHAR(50) PRIMARY KEY,
    maNV VARCHAR(50),
    ngayNhap DATE,
	isActive bit DEFAULT 1,
    FOREIGN KEY (maNV) REFERENCES NhanVien(maNV)
);
GO

-- Tạo bảng ChiTietPhieuNhap
CREATE TABLE ChiTietPhieuNhap (
    maPhieuNhapThuoc VARCHAR(50),
    maLo VARCHAR(50),
	maThuoc VARCHAR(50),
	soLuong INT,
	donGia FLOAT,
	isActive bit DEFAULT 1,
    PRIMARY KEY (maPhieuNhapThuoc, maLo, maThuoc),
    FOREIGN KEY (maPhieuNhapThuoc) REFERENCES PhieuNhapThuoc(maPhieuNhapThuoc),
	FOREIGN KEY (maThuoc) REFERENCES Thuoc(maThuoc),
    FOREIGN KEY (maLo) REFERENCES LoThuoc(maLo)
);
GO

-- ===============================================
-- CHÈN DỮ LIỆU MẪU
-- ===============================================

-- Chèn dữ liệu vào bảng NhaSanXuat
INSERT INTO NhaSanXuat (maNSX, tenNSX, diaChiNSX, soDienThoai) VALUES
('NSX001', N'Công ty Cổ phần Dược Hậu Giang', N'288 Bis Nguyễn Văn Cừ, P. An Hòa, Q. Ninh Kiều, Cần Thơ', '0292123456'),
('NSX002', N'Công ty Cổ phần Dược phẩm Imexpharm', N'Số 4 Đường 30/4, P.1, TP. Cao Lãnh, Đồng Tháp', '0277123456'),
('NSX003', N'Công ty Cổ phần Dược phẩm OPC', N'1017 Hồng Bàng, P.12, Q.6, TP.HCM', '028123456'),
('NSX004', N'Công ty Cổ phần Dược phẩm Traphaco', N'75 Yên Ninh, Ba Đình, Hà Nội', '024123456'),
('NSX005', N'Công ty Cổ phần Dược Mekophar', N'297/5 Lý Thường Kiệt, P.15, Q.11, TP.HCM', '028123457'),
('NSX006', N'Sanofi-Aventis Việt Nam', N'Tòa nhà Mê Linh Point, 2 Ngô Đức Kế, Q.1, TP.HCM', '028123458'),
('NSX007', N'Novartis Việt Nam', N'Tòa nhà Metropolitan, 235 Đồng Khởi, Q.1, TP.HCM', '028123459'),
('NSX008', N'GlaxoSmithKline (GSK) Việt Nam', N'Tòa nhà The Metropolitan, 235 Đồng Khởi, Q.1, TP.HCM', '028123460'),
('NSX009', N'Công ty Dược phẩm Nam Hà', N'415 Hàn Thuyên, Nam Định', '0228123456'),
('NSX010', N'Công ty Cổ phần Pymepharco', N'166-170 Nguyễn Huệ, Tuy Hòa, Phú Yên', '0257123456');
GO

-- Chèn dữ liệu vào bảng KhachHang
INSERT INTO KhachHang (maKH, tenKH, soDienThoai, diemTichLuy) VALUES
('KH001', N'Nguyễn Văn An', '0901234567', 150),
('KH002', N'Trần Thị Bình', '0912345678', 200),
('KH003', N'Lê Văn Cường', '0923456789', 100),
('KH004', N'Phạm Thị Dung', '0934567890', 300),
('KH005', N'Võ Văn Em', '0945678901', 50),
('KH006', N'Hoàng Thị Giang', '0956789012', 180),
('KH007', N'Đặng Văn Hải', '0967890123', 220),
('KH008', N'Bùi Thị Lan', '0978901234', 90),
('KH009', N'Đỗ Văn Minh', '0989012345', 270),
('KH010', N'Ngô Thị Nga', '0990123456', 120),
('KH011', N'Trịnh Văn Phát', '0901234568', 160),
('KH012', N'Mai Thị Quyên', '0912345679', 210),
('KH013', N'Vũ Văn Sơn', '0923456780', 110),
('KH014', N'Lý Thị Tâm', '0934567891', 320),
('KH015', N'Phan Văn Ứng', '0945678902', 60),
('KH016', N'Dương Thị Vân', '0956789013', 190),
('KH017', N'Huỳnh Văn Xuân', '0967890124', 230),
('KH018', N'Châu Thị Yến', '0978901235', 95),
('KH019', N'Đinh Văn Anh', '0989012346', 280),
('KH020', N'Lương Thị Bảo', '0990123457', 130),
('KH021', N'Tống Văn Cẩm', '0901234569', 170),
('KH022', N'Lâm Thị Diệu', '0912345670', 220),
('KH023', N'Thạch Văn Giang', '0923456781', 120),
('KH024', N'Phùng Thị Hằng', '0934567892', 330),
('KH025', N'Quách Văn Khôi', '0945678903', 70),
('KH026', N'Thái Thị Linh', '0956789014', 200),
('KH027', N'Hà Văn Mạnh', '0967890125', 240),
('KH028', N'Lưu Thị Ngọc', '0978901236', 100),
('KH029', N'Đường Văn Phú', '0989012347', 290),
('KH030', N'Mạc Thị Quỳnh', '0990123458', 140),
('KH031', N'Tạ Văn Sáng', '0901234570', 180),
('KH032', N'Tiêu Thị Thảo', '0912345671', 230),
('KH033', N'Chu Văn Uy', '0923456782', 130),
('KH034', N'Trình Thị Vân', '0934567893', 340),
('KH035', N'Diệp Văn Xuân', '0945678904', 80);
GO

-- Chèn dữ liệu vào bảng ChucVu
INSERT INTO ChucVu (maChucVu, tenChucVu) VALUES
('QLY', N'Quản Lý'),
('BHA', N'Nhân Viên Bán Hàng'),
('KHO', N'Nhân Viên Kho'),
('KTO', N'Kế Toán');
GO

-- Chèn dữ liệu vào bảng NhanVien
INSERT INTO NhanVien (maNV, tenNV, maChucVu, soDienThoai) VALUES
('NV001', N'Trương Minh Hùng', 'QLY', '0901111345'),
('NV002', N'Nguyễn Thị Lan Anh', 'BHA', '0902222123'),
('NV003', N'Phạm Văn Bình', 'QLY', '0903333333'),
('NV004', N'Lê Thị Cẩm Tú', 'BHA', '0904444678'),
('NV005', N'Võ Đình Danh', 'QLY', '0904555555'),
('NV006', N'Hoàng Thị Hồng', 'BHA', '0905666666'),
('NV007', N'Đặng Minh Khôi', 'QLY', '0907777777'),
('NV008', N'Bùi Thị Ngọc Mai', 'BHA', '0907888888'),
('NV009', N'Trần Quốc Thái', 'QLY', '0909999999'),
('NV010', N'Ngô Phương Thảo', 'BHA', '0900012200');
GO

-- Chèn dữ liệu vào bảng TaiKhoan
INSERT INTO TaiKhoan (maNV, matKhau) VALUES
('NV001', 'admin123'),
('NV002', 'duocsi123'),
('NV003', 'duocsi456'),
('NV004', 'banhang123'),
('NV005', 'banhang456'),
('NV006', 'kho123'),
('NV007', 'ketoan123'),
('NV008', 'duocsi789'),
('NV009', 'depchai123'),
('NV010', 'banhang000');
GO

-- Chèn dữ liệu vào bảng KhuyenMai
INSERT INTO KhuyenMai (maKM, mucGiamGia, ngayApDung, ngayKetThuc) VALUES
('KM001', 0.05, '2025-05-01', '2025-05-15'),
('KM002', 0.10, '2025-06-01', '2025-06-15'),
('KM003', 0.15, '2025-07-01', '2025-07-15'),
('KM004', 0.07, '2025-08-01', '2025-08-15'),
('KM005', 0.12, '2025-09-01', '2025-09-15'),
('KM006', 0.05, '2025-05-16', '2025-05-31'),
('KM007', 0.08, '2025-06-16', '2025-06-30'),
('KM008', 0.10, '2025-07-16', '2025-07-31'),
('KM009', 0.06, '2025-08-16', '2025-08-31'),
('KM010', 0.20, '2025-09-16', '2025-09-30');
GO

-- Chèn dữ liệu vào bảng LoThuoc
INSERT INTO LoThuoc (maLo, maNSX) VALUES
('LO001', 'NSX001'), ('LO002', 'NSX002'), ('LO003', 'NSX003'), ('LO004', 'NSX004'),
('LO005', 'NSX005'), ('LO006', 'NSX006'), ('LO007', 'NSX007'), ('LO008', 'NSX008'),
('LO009', 'NSX009'), ('LO010', 'NSX010'), ('LO011', 'NSX001'), ('LO012', 'NSX002'),
('LO013', 'NSX003'), ('LO014', 'NSX004'), ('LO015', 'NSX005'), ('LO016', 'NSX006'),
('LO017', 'NSX007'), ('LO018', 'NSX008'), ('LO019', 'NSX009'), ('LO020', 'NSX010'),
('LO021', 'NSX001'), ('LO022', 'NSX002'), ('LO023', 'NSX003'), ('LO024', 'NSX004'),
('LO025', 'NSX005'), ('LO026', 'NSX001'), ('LO027', 'NSX002'), ('LO028', 'NSX003'),
('LO029', 'NSX004'), ('LO030', 'NSX005'), ('LO031', 'NSX001'), ('LO032', 'NSX002'),
('LO033', 'NSX003'), ('LO034', 'NSX004'), ('LO035', 'NSX005'), ('LO999', 'NSX001'); 
GO

-- Chèn dữ liệu vào bảng DonVi
INSERT INTO DonVi (maDonVi, tenDonVi) VALUES
('chai', N'Chai'), ('vien', N'Viên'), ('tuyp', N'Tuýp'), ('ong', N'Ống'),('binh', N'Bình'),('goi', N'Gói')
GO

-- Chèn dữ liệu bảng Thuoc
INSERT INTO Thuoc (maThuoc, tenThuoc, soLuongTon, maDonVi, soLuongToiThieu, maNSX, isActive) VALUES
('T001', N'Paracetamol 500mg', 0, 'vien', 100, 'NSX001', 1),
('T002', N'Efferalgan 500mg', 0, 'vien', 80, 'NSX001', 1),
('T003', N'Ibuprofen 400mg', 0, 'vien', 60, 'NSX002', 1),
('T004', N'Aspirin 100mg', 0, 'goi', 90, 'NSX002', 1),
('T005', N'Mobic 7.5mg', 0, 'vien', 50, 'NSX003', 1),
('T006', N'Amoxicillin 500mg', 0, 'vien', 70, 'NSX003', 1),
('T007', N'Augmentin 625mg', 0, 'goi', 40, 'NSX004', 1),
('T008', N'Cefuroxime ', 0, 'chai', 35, 'NSX004', 1),
('T009', N'Azithromycin 250mg', 0, 'vien', 45, 'NSX005', 1),
('T010', N'Ciprofloxacin 500mg', 0, 'vien', 55, 'NSX005', 1),
('T011', N'Omeprazole 20mg', 0, 'vien', 60, 'NSX006', 1),
('T012', N'Nexium 40mg', 0, 'vien', 40, 'NSX006', 1),
('T013', N'Pantoprazole 40mg', 0, 'vien', 45, 'NSX007', 1),
('T014', N'Maalox', 0, 'chai', 80, 'NSX007', 1),
('T015', N'Phosphalugel', 0, 'goi', 70, 'NSX008', 1),
('T016', N'Smecta', 0, 'goi', 90, 'NSX008', 1),
('T017', N'Imodium 2mg', 0, 'vien', 60, 'NSX009', 1),
('T018', N'Bioflora', 0, 'goi', 50, 'NSX009', 1),
('T019', N'Lacteol Fort', 0, 'goi', 70, 'NSX010', 1),
('T020', N'Enterogermina', 0, 'ong', 65, 'NSX010', 1),
('T021', N'Vitamin C 500mg', 0, 'binh', 120, 'NSX001', 1),
('T022', N'Vitamin E 400UI', 0, 'vien', 95, 'NSX001', 1),
('T023', N'Multivitamin', 0, 'vien', 80, 'NSX002', 1),
('T024', N'Canxi D3', 0, 'vien', 100, 'NSX002', 1),
('T025', N'Zinc 20mg', 0, 'vien', 75, 'NSX003', 1),
('T026', N'Loratadine 10mg', 0, 'vien', 85, 'NSX003', 1),
('T027', N'Cetirizine 10mg', 0, 'vien', 90, 'NSX004', 1),
('T028', N'Fexofenadine 60mg', 0, 'vien', 70, 'NSX004', 1),
('T029', N'Claritine', 0, 'vien', 60, 'NSX005', 1),
('T030', N'Telfast 180mg', 0, 'binh', 50, 'NSX005', 1),
('T031', N'Amlodipine 5mg', 0, 'binh', 45, 'NSX006', 1),
('T032', N'Amlodipine 10mg', 0, 'vien', 40, 'NSX006', 1),
('T033', N'Bisoprolol 2.5mg', 0, 'vien', 35, 'NSX007', 1),
('T034', N'Enalapril 5mg', 0, 'vien', 38, 'NSX007', 1),
('T035', N'Losartan 50mg', 0, 'vien', 42, 'NSX008', 1),
('T036', N'Metformin 500mg', 0, 'chai', 48, 'NSX008', 1),
('T037', N'Metformin 850mg', 0, 'vien', 45, 'NSX009', 1),
('T038', N'Glibenclamide 5mg', 0, 'vien', 40, 'NSX009', 1),
('T039', N'Acarbose 50mg', 0, 'vien', 35, 'NSX010', 1),
('T040', N'Januvia 100mg', 0, 'vien', 30, 'NSX010', 1),
('T041', N'Atorvastatin 10mg', 0, 'vien', 48, 'NSX001', 1),
('T042', N'Atorvastatin 20mg', 0, 'vien', 45, 'NSX001', 1),
('T043', N'Rosuvastatin 10mg', 0, 'vien', 42, 'NSX002', 1),
('T044', N'Simvastatin 20mg', 0, 'vien', 40, 'NSX002', 1),
('T045', N'Lipitor 10mg', 0, 'vien', 38, 'NSX003', 1),
('T046', N'Meloxicam 7.5mg', 0, 'vien', 50, 'NSX003', 1),
('T047', N'Diclofenac 50mg', 0, 'vien', 55, 'NSX004', 1),
('T048', N'Celecoxib 200mg', 0, 'vien', 45, 'NSX004', 1),
('T049', N'Methylprednisolone 16mg', 0, 'vien', 40, 'NSX005', 1),
('T050', N'Prednisone 5mg', 0, 'chai', 50, 'NSX005', 1),
('T051', N'Salbutamol 2mg', 0, 'vien', 60, 'NSX001', 1),
('T052', N'Ventolin inhaler', 0, 'binh', 35, 'NSX001', 1),
('T053', N'Seretide 250mcg', 0, 'binh', 30, 'NSX002', 1),
('T054', N'Combivent', 0, 'binh', 32, 'NSX002', 1),
('T055', N'Bromhexine 8mg', 0, 'vien', 70, 'NSX003', 1),
('T056', N'Ambroxol 30mg', 0, 'vien', 75, 'NSX003', 1),
('T057', N'N-acetylcysteine 200mg', 0, 'vien', 60, 'NSX004', 1),
('T058', N'Bisolvon', 0, 'chai', 65, 'NSX004', 1),
('T059', N'Fluconazole 150mg', 0, 'vien', 40, 'NSX005', 1),
('T060', N'Ketoconazole 200mg', 0, 'vien', 45, 'NSX005', 1),
('T061', N'Metronidazole 250mg', 0, 'vien', 80, 'NSX006', 1),
('T062', N'Albendazole 400mg', 0, 'vien', 60, 'NSX006', 1),
('T063', N'Mebendazole 500mg', 0, 'vien', 55, 'NSX007', 1),
('T064', N'Domperidone 10mg', 0, 'vien', 65, 'NSX007', 1),
('T065', N'Metoclopramide 10mg', 0, 'vien', 70, 'NSX008', 1),
('T066', N'Ondansetron 8mg', 0, 'vien', 50, 'NSX008', 1),
('T067', N'Dexamethasone 0.5mg', 0, 'vien', 45, 'NSX009', 1),
('T068', N'Betamethasone 0.5mg', 0, 'vien', 40, 'NSX009', 1),
('T069', N'Hydrocortisone 10mg', 0, 'tuyp', 35, 'NSX010', 1),
('T070', N'Triamcinolone 4mg', 0, 'vien', 30, 'NSX010', 1),
('T071', N'Furosemide 40mg', 0, 'vien', 45, 'NSX001', 1),
('T072', N'Spironolactone 25mg', 0, 'vien', 40, 'NSX001', 1),
('T073', N'Indapamide 1.5mg', 0, 'vien', 35, 'NSX002', 1),
('T074', N'Hydrochlorothiazide 25mg', 0, 'vien', 40, 'NSX002', 1),
('T075', N'Isosorbide dinitrate 10mg', 0, 'vien', 35, 'NSX003', 1),
('T076', N'Nitroglycerin 0.5mg', 0, 'vien', 30, 'NSX003', 1),
('T077', N'Digoxin 0.25mg', 0, 'vien', 25, 'NSX004', 1),
('T078', N'Verapamil 40mg', 0, 'vien', 30, 'NSX004', 1),
('T079', N'Diltiazem 60mg', 0, 'vien', 28, 'NSX005', 1),
('T080', N'Atenolol 50mg', 0, 'goi', 32, 'NSX005', 1),
('T081', N'Propranolol 40mg', 0, 'goi', 35, 'NSX006', 1),
('T082', N'Carvedilol 6.25mg', 0, 'vien', 30, 'NSX006', 1),
('T083', N'Tamsulosin 0.4mg', 0, 'vien', 25, 'NSX007', 1),
('T084', N'Sildenafil 50mg', 0, 'vien', 20, 'NSX007', 1),
('T085', N'Tadalafil 10mg', 0, 'vien', 18, 'NSX008', 1),
('T086', N'Finasteride 5mg', 0, 'goi', 22, 'NSX008', 1),
('T087', N'Ethinylestradiol 35mcg', 0, 'vien', 30, 'NSX009', 1),
('T088', N'Medroxyprogesterone 5mg', 0, 'vien', 28, 'NSX009', 1),
('T089', N'Clomiphene citrate 50mg', 0, 'vien', 22, 'NSX010', 1),
('T090', N'Levothyroxine 50mcg', 0, 'vien', 35, 'NSX010', 1),
('T091', N'Levothyroxine 100mcg', 0, 'vien', 32, 'NSX001', 1),
('T092', N'Methimazole 5mg', 0, 'vien', 28, 'NSX001', 1),
('T093', N'Gliclazide 30ml', 0, 'ong', 35, 'NSX002', 1),
('T094', N'Pioglitazone 15mg', 0, 'vien', 30, 'NSX002', 1),
('T095', N'Gabapentin 300mg', 0, 'vien', 25, 'NSX003', 1),
('T096', N'Pregabalin 75mg', 0, 'vien', 22, 'NSX003', 1),
('T097', N'Alprazolam 0.5mg', 0, 'vien', 30, 'NSX004', 1),
('T098', N'Diazepam 5mg', 0, 'vien', 25, 'NSX004', 1),
('T099', N'Fluoxetine 20mg', 0, 'chai', 28, 'NSX005', 1),
('T100', N'Sertraline 50mg', 0, 'vien', 26, 'NSX005', 1),
('T101', N'Esomeprazole 20ml', 0, 'ong', 32, 'NSX001', 1),
('T102', N'Lansoprazole 30ml', 0, 'vien', 29, 'NSX002', 1),
('T103', N'Rabeprazole 20ml', 0, 'ong', 27, 'NSX003', 1),
('T104', N'Famotidine 40mg', 0, 'vien', 31, 'NSX004', 1),
('T105', N'Ranitidine 150mg', 0, 'vien', 35, 'NSX005', 1);
GO

-- Chèn dữ liệu bảng BangGia
INSERT INTO BangGia (maBangGia, tenBangGia, loaiGia, ngayApDung, ngayKetThuc, trangThai, ghiChu,doUuTien, isActive) VALUES
('BG001', N'Bảng giá chuẩn', N'Giá bán lẻ', '2025-01-01', '2099-01-01', N'Đang áp dụng', N'Bảng giá mặc định',0, 1);
GO

-- Chèn dữ liệu bảng ChiTietBangGia
INSERT INTO ChiTietBangGia (maBangGia, maThuoc, donGia, maDonVi, isActive) VALUES
('BG001', 'T001', 5000, 'vien', 1),
('BG001', 'T002', 7500, 'vien', 1),
('BG001', 'T003', 8000, 'vien', 1),
('BG001', 'T004', 6500, 'goi', 1),
('BG001', 'T005', 12000, 'vien', 1),
('BG001', 'T006', 15000, 'vien', 1),
('BG001', 'T007', 25000, 'goi', 1),
('BG001', 'T008', 22000, 'chai', 1),
('BG001', 'T009', 18000, 'vien', 1),
('BG001', 'T010', 16000, 'vien', 1),
('BG001', 'T011', 14000, 'vien', 1),
('BG001', 'T012', 32000, 'vien', 1),
('BG001', 'T013', 28000, 'vien', 1),
('BG001', 'T014', 8500, 'chai', 1),
('BG001', 'T015', 9500, 'goi', 1),
('BG001', 'T016', 7000, 'goi', 1),
('BG001', 'T017', 12500, 'vien', 1),
('BG001', 'T018', 11000, 'goi', 1),
('BG001', 'T019', 9000, 'goi', 1),
('BG001', 'T020', 10500, 'ong', 1),
('BG001', 'T021', 8000, 'binh', 1),      
('BG001', 'T022', 12000, 'vien', 1),
('BG001', 'T023', 15000, 'vien', 1),
('BG001', 'T024', 13500, 'vien', 1),
('BG001', 'T025', 9500, 'vien', 1),
('BG001', 'T026', 7500, 'vien', 1),
('BG001', 'T027', 8000, 'vien', 1),
('BG001', 'T028', 12500, 'vien', 1),
('BG001', 'T029', 14000, 'vien', 1),
('BG001', 'T030', 18000, 'binh', 1),      
('BG001', 'T031', 8500, 'binh', 1),     
('BG001', 'T032', 10500, 'vien', 1),
('BG001', 'T033', 12000, 'vien', 1),
('BG001', 'T034', 9500, 'vien', 1),
('BG001', 'T035', 11000, 'vien', 1),
('BG001', 'T036', 7000, 'chai', 1),      
('BG001', 'T037', 8500, 'vien', 1),
('BG001', 'T038', 6500, 'vien', 1),
('BG001', 'T039', 13000, 'vien', 1),
('BG001', 'T040', 28000, 'vien', 1),
('BG001', 'T041', 15000, 'vien', 1),
('BG001', 'T042', 19000, 'vien', 1),
('BG001', 'T043', 22000, 'vien', 1),
('BG001', 'T044', 14000, 'vien', 1),
('BG001', 'T045', 26000, 'vien', 1),
('BG001', 'T046', 11000, 'vien', 1),
('BG001', 'T047', 8500, 'vien', 1),
('BG001', 'T048', 16000, 'vien', 1),
('BG001', 'T049', 18000, 'vien', 1),
('BG001', 'T050', 9500, 'chai', 1),    
('BG001', 'T051', 7500, 'vien', 1),
('BG001', 'T052', 85000, 'binh', 1),
('BG001', 'T053', 145000, 'binh', 1),
('BG001', 'T054', 120000, 'binh', 1),
('BG001', 'T055', 6500, 'vien', 1),
('BG001', 'T056', 7000, 'vien', 1),
('BG001', 'T057', 12000, 'vien', 1),
('BG001', 'T058', 9500, 'chai', 1),
('BG001', 'T059', 18000, 'vien', 1),
('BG001', 'T060', 16000, 'vien', 1),
('BG001', 'T061', 5500, 'vien', 1),
('BG001', 'T062', 7500, 'vien', 1),
('BG001', 'T063', 8000, 'vien', 1),
('BG001', 'T064', 9500, 'vien', 1),
('BG001', 'T065', 6000, 'vien', 1),
('BG001', 'T066', 14000, 'vien', 1),
('BG001', 'T067', 7500, 'vien', 1),
('BG001', 'T068', 8500, 'vien', 1),
('BG001', 'T069', 9500, 'tuyp', 1),
('BG001', 'T070', 12000, 'vien', 1),
('BG001', 'T071', 6500, 'vien', 1),
('BG001', 'T072', 9500, 'vien', 1),
('BG001', 'T073', 11000, 'vien', 1),
('BG001', 'T074', 7500, 'vien', 1),
('BG001', 'T075', 10500, 'vien', 1),
('BG001', 'T076', 12500, 'vien', 1),
('BG001', 'T077', 8500, 'vien', 1),
('BG001', 'T078', 11000, 'vien', 1),
('BG001', 'T079', 12500, 'vien', 1),
('BG001', 'T080', 9500, 'goi', 1),   
('BG001', 'T081', 7500, 'goi', 1),      
('BG001', 'T082', 13500, 'vien', 1),
('BG001', 'T083', 16000, 'vien', 1),
('BG001', 'T084', 25000, 'vien', 1),
('BG001', 'T085', 28000, 'vien', 1),
('BG001', 'T086', 18000, 'goi', 1),     
('BG001', 'T087', 12000, 'vien', 1),
('BG001', 'T088', 14000, 'vien', 1),
('BG001', 'T089', 22000, 'vien', 1),
('BG001', 'T090', 9500, 'vien', 1),
('BG001', 'T091', 11000, 'vien', 1),
('BG001', 'T092', 13500, 'vien', 1),
('BG001', 'T093', 15000, 'ong', 1),
('BG001', 'T094', 18000, 'vien', 1),
('BG001', 'T095', 16000, 'vien', 1),
('BG001', 'T096', 22000, 'vien', 1),
('BG001', 'T097', 8500, 'vien', 1),
('BG001', 'T098', 7000, 'vien', 1),
('BG001', 'T099', 12500, 'chai', 1),
('BG001', 'T100', 14000, 'vien', 1),
('BG001', 'T101', 16500, 'ong', 1),
('BG001', 'T102', 15500, 'vien', 1),
('BG001', 'T103', 17500, 'ong', 1),
('BG001', 'T104', 9500, 'vien', 1),
('BG001', 'T105', 8500, 'vien', 1);
GO

-- Chèn dữ liệu vào bảng ChiTietLoThuoc
INSERT INTO ChiTietLoThuoc (maLo, maThuoc, ngaySanXuat, hanSuDung, soLuong, giaNhap)
VALUES
('LO001', 'T001', '2025-01-10', '2027-01-10', 100, 5000),
('LO001', 'T002', '2025-01-15', '2027-01-15', 120, 4500),
('LO001', 'T051', '2025-01-20', '2027-01-20', 80, 6000),
('LO001', 'T052', '2025-01-25', '2027-01-25', 200, 5200),
('LO001', 'T101', '2025-02-01', '2027-02-01', 150, 5500),

('LO002', 'T003', '2025-01-12', '2027-01-12', 90, 4700),
('LO002', 'T004', '2025-01-17', '2027-01-17', 100, 4900),
('LO002', 'T053', '2025-01-22', '2027-01-22', 110, 5100),
('LO002', 'T054', '2025-01-27', '2027-01-27', 130, 5300),
('LO002', 'T102', '2025-02-02', '2027-02-02', 70, 5000),

('LO003', 'T005', '2025-01-05', '2027-01-05', 80, 4600),
('LO003', 'T006', '2025-01-07', '2027-01-07', 120, 4800),
('LO003', 'T055', '2025-01-09', '2027-01-09', 150, 5100),
('LO003', 'T056', '2025-01-11', '2027-01-11', 130, 4900),
('LO003', 'T103', '2025-01-13', '2027-01-13', 160, 5200),

('LO004', 'T007', '2025-02-01', '2027-02-01', 100, 5000),
('LO004', 'T008', '2025-02-03', '2027-02-03', 120, 4800),
('LO005', 'T009', '2025-02-05', '2027-02-05', 150, 4700),
('LO005', 'T010', '2025-02-07', '2027-02-07', 140, 4600),
('LO006', 'T011', '2025-02-09', '2027-02-09', 130, 4900),
('LO006', 'T012', '2025-02-11', '2027-02-11', 110, 5000),
('LO007', 'T013', '2025-02-13', '2027-02-13', 100, 5200),
('LO007', 'T014', '2025-02-15', '2027-02-15', 120, 5100),
('LO008', 'T015', '2025-02-17', '2027-02-17', 150, 5300),
('LO008', 'T016', '2025-02-19', '2027-02-19', 160, 5400),
('LO009', 'T017', '2025-02-21', '2027-02-21', 140, 5500),
('LO009', 'T018', '2025-02-23', '2027-02-23', 130, 5600),
('LO010', 'T019', '2025-02-25', '2027-02-25', 110, 5800),
('LO010', 'T020', '2025-02-27', '2027-02-27', 90, 6000),

('LO011', 'T021', '2025-03-01', '2027-03-01', 120, 5000),
('LO011', 'T022', '2025-03-05', '2027-03-05', 95, 5200),
('LO012', 'T023', '2025-03-08', '2027-03-08', 80, 5400),
('LO012', 'T024', '2025-03-12', '2027-03-12', 100, 5600),
('LO013', 'T025', '2025-03-15', '2027-03-15', 75, 5800),
('LO013', 'T026', '2025-03-18', '2027-03-18', 85, 6000),
('LO014', 'T027', '2025-03-21', '2027-03-21', 90, 6200),
('LO014', 'T028', '2025-03-24', '2027-03-24', 70, 6400),
('LO015', 'T029', '2025-03-27', '2026-03-27', 60, 6600),
('LO015', 'T030', '2025-03-30', '2026-03-30', 50, 6800),
('LO016', 'T031', '2025-04-01', '2026-04-01', 45, 7000),
('LO016', 'T032', '2025-04-04', '2026-04-04', 40, 7200),
('LO017', 'T033', '2025-04-07', '2026-04-07', 35, 7400),
('LO017', 'T034', '2025-04-10', '2026-04-10', 38, 7600),
('LO018', 'T035', '2025-04-13', '2026-04-13', 42, 7800),
('LO018', 'T036', '2025-04-16', '2026-04-16', 48, 8000),
('LO019', 'T037', '2025-04-19', '2026-04-19', 45, 8200),
('LO019', 'T038', '2025-04-22', '2026-04-22', 40, 8400),
('LO020', 'T039', '2025-04-25', '2026-04-25', 35, 8600),
('LO020', 'T040', '2025-04-28', '2026-04-28', 30, 8800),
('LO021', 'T041', '2025-05-01', '2026-05-01', 48, 9000),
('LO021', 'T042', '2025-05-04', '2027-05-04', 45, 9200),
('LO022', 'T043', '2025-05-07', '2027-05-07', 42, 9400),
('LO022', 'T044', '2025-05-10', '2027-05-10', 40, 9600),
('LO023', 'T045', '2025-05-13', '2027-05-13', 38, 9800),
('LO023', 'T046', '2025-05-16', '2027-05-16', 50, 10000),
('LO024', 'T047', '2025-05-19', '2027-05-19', 55, 10200),
('LO024', 'T048', '2025-05-22', '2027-05-22', 45, 10400),
('LO025', 'T049', '2025-05-25', '2027-05-25', 40, 10600),
('LO025', 'T050', '2025-05-28', '2027-05-28', 50, 10800),

-- Dữ liệu bổ sung cho các thuốc khác
('LO004', 'T057', '2025-06-21', '2027-06-21', 60, 9000),
('LO004', 'T058', '2025-06-24', '2027-06-24', 65, 7500),
('LO005', 'T059', '2025-06-27', '2027-06-27', 40, 15000),
('LO005', 'T060', '2025-06-30', '2027-06-30', 45, 13000),
('LO006', 'T061', '2025-07-01', '2027-07-01', 80, 4000),
('LO006', 'T062', '2025-07-04', '2027-07-04', 60, 6000),
('LO007', 'T063', '2025-07-07', '2027-07-07', 55, 6500),
('LO007', 'T064', '2025-07-10', '2027-07-10', 65, 7000),
('LO008', 'T065', '2025-07-13', '2027-07-13', 70, 4500),
('LO008', 'T066', '2025-07-16', '2027-07-16', 50, 11000),
('LO009', 'T067', '2025-07-19', '2027-07-19', 45, 5500),
('LO009', 'T068', '2025-07-22', '2027-07-22', 40, 6500),
('LO010', 'T069', '2025-07-25', '2027-07-25', 35, 7500),
('LO010', 'T070', '2025-07-28', '2027-07-28', 30, 9000),
('LO011', 'T071', '2025-08-01', '2027-08-01', 45, 5000),
('LO011', 'T072', '2025-08-04', '2027-08-04', 40, 7500),
('LO012', 'T073', '2025-08-07', '2027-08-07', 35, 8500),
('LO012', 'T074', '2025-08-10', '2027-08-10', 40, 6000),
('LO013', 'T075', '2025-08-13', '2027-08-13', 35, 9000),
('LO013', 'T076', '2025-08-16', '2027-08-16', 30, 10500),
('LO014', 'T077', '2025-08-19', '2027-08-19', 25, 6500),
('LO014', 'T078', '2025-08-22', '2027-08-22', 30, 9000),
('LO015', 'T079', '2025-08-25', '2027-08-25', 28, 10000),
('LO015', 'T080', '2025-08-28', '2027-08-28', 32, 7500),
('LO016', 'T081', '2025-09-01', '2027-09-01', 35, 6000),
('LO016', 'T082', '2025-09-04', '2027-09-04', 30, 11000),
('LO017', 'T083', '2025-09-07', '2027-09-07', 25, 12000),
('LO017', 'T084', '2025-09-10', '2027-09-10', 20, 20000),
('LO018', 'T085', '2025-09-13', '2027-09-13', 18, 22000),
('LO018', 'T086', '2025-09-16', '2027-09-16', 22, 14000),
('LO019', 'T087', '2025-09-19', '2027-09-19', 30, 9500),
('LO019', 'T088', '2025-09-22', '2027-09-22', 28, 11000),
('LO020', 'T089', '2025-09-25', '2027-09-25', 22, 18000),
('LO020', 'T090', '2025-09-28', '2027-09-28', 35, 7500),
('LO021', 'T091', '2025-10-01', '2027-10-01', 32, 9000),
('LO021', 'T092', '2025-10-04', '2027-10-04', 28, 10500),
('LO022', 'T093', '2025-10-07', '2027-10-07', 35, 12000),
('LO022', 'T094', '2025-10-10', '2027-10-10', 30, 14000),
('LO023', 'T095', '2025-10-13', '2027-10-13', 25, 13000),
('LO023', 'T096', '2025-10-16', '2027-10-16', 22, 18000),
('LO024', 'T097', '2025-10-19', '2027-10-19', 30, 6500),
('LO024', 'T098', '2025-10-22', '2027-10-22', 25, 5000),
('LO025', 'T099', '2025-10-25', '2027-10-25', 28, 10000),
('LO025', 'T100', '2025-10-28', '2027-10-28', 26, 11500),
('LO004', 'T104', '2025-11-10', '2027-11-10', 31, 7500), 
('LO005', 'T105', '2025-11-13', '2027-11-13', 35, 6500), 

-- DỮ LIỆU BỔ SUNG ĐỂ KIỂM TRA ĐỒNG BỘ TỒN KHO
('LO999', 'T001', '2025-10-15', '2027-10-15', 2000, 4800), -- Thêm 2000 viên Paracetamol
('LO999', 'T007', '2025-10-15', '2027-10-15', 500, 20000),

-- Lô thuốc ĐÃ HẾT HẠN và SẮP HẾT HẠN (Giữ nguyên như cũ)
('LO026', 'T001', '2024-09-19', '2025-09-19', 150, 4500),
('LO026', 'T021', '2024-09-19', '2025-09-19', 100, 7000),
('LO027', 'T003', '2024-09-29', '2025-09-29', 200, 7200),
('LO027', 'T011', '2024-09-29', '2025-09-29', 120, 12500),
('LO027', 'T026', '2024-09-29', '2025-09-29', 80, 6800),
('LO028', 'T006', '2024-10-19', '2025-10-19', 180, 13500),
('LO028', 'T016', '2024-10-19', '2025-10-19', 90, 6200),
('LO028', 'T031', '2024-10-19', '2025-10-19', 60, 7800),
('LO029', 'T002', '2024-11-08', '2025-11-08', 220, 6800),
('LO029', 'T012', '2024-11-08', '2025-11-08', 140, 28000),
('LO029', 'T027', '2024-11-08', '2025-11-08', 95, 7200),
('LO030', 'T007', '2024-11-18', '2025-11-18', 160, 22000),
('LO030', 'T017', '2024-11-18', '2025-11-18', 110, 11200),
('LO030', 'T032', '2024-11-18', '2025-11-18', 75, 9500),
('LO030', 'T041', '2024-11-18', '2025-11-18', 85, 13500),
('LO031', 'T008', '2024-12-18', '2025-12-18', 190, 19800),
('LO031', 'T018', '2024-12-18', '2025-12-18', 130, 9900),
('LO031', 'T033', '2024-12-18', '2025-12-18', 70, 10800),
('LO032', 'T009', '2024-09-19', '2025-09-19', 170, 16200),
('LO032', 'T036', '2024-09-19', '2025-09-19', 140, 6300),
('LO033', 'T010', '2024-09-29', '2025-09-29', 155, 14400),
('LO033', 'T037', '2024-09-29', '2025-09-29', 125, 7650),
('LO034', 'T013', '2024-11-08', '2025-11-08', 135, 25200),
('LO034', 'T038', '2024-11-08', '2025-11-08', 115, 5850),
('LO035', 'T014', '2024-11-18', '2025-11-18', 145, 7650),
('LO035', 'T039', '2024-11-18', '2025-11-18', 105, 11700); 
GO

-- -- Chèn dữ liệu vào bảng HoaDon (Đã chỉnh sửa để dùng giaTriThue)
INSERT INTO HoaDon (maHoaDon, ngayBan, ghiChu, maNV, maKH, maKM, giaTriThue, tenLoaiThue) VALUES
('HD00001', '2025-05-02', N'Khách hàng mua thuốc cho gia đình', 'NV004', 'KH001', 'KM001', 0.05, N'VAT 5%'),
('HD00002', '2025-05-05', N'Khách hàng mua thuốc theo toa bác sĩ', 'NV009', 'KH005', 'KM001', 0.05, N'VAT 5%'),
('HD00003', '2025-05-08', N'Khách hàng mua thuốc định kỳ hàng tháng', 'NV002', 'KH010', 'KM001', 0.05, N'VAT 5%'),
('HD00004', '2025-05-12', N'Khách hàng mua thuốc cho người thân', 'NV004', 'KH015', 'KM001', 0.05, N'VAT 5%'),
('HD00005', '2025-05-15', N'Khách hàng mua thuốc cho bệnh mãn tính', 'NV009', 'KH020', 'KM001', 0.08, N'VAT 8%'),
('HD00006', '2025-05-18', N'Khách hàng mua thuốc theo đơn', 'NV003', 'KH025', 'KM006', 0.05, N'VAT 5%'),
('HD00007', '2025-05-22', N'Khách hàng mua thuốc không theo đơn', 'NV005', 'KH030', 'KM006', 0.05, N'VAT 5%'),
('HD00008', '2025-05-25', N'Khách hàng mua vitamin tăng sức đề kháng', 'NV002', 'KH002', 'KM006', 0.05, N'VAT 5%'),
('HD00009', '2025-05-28', N'Khách hàng mua thuốc chữa cảm cúm', 'NV010', 'KH007', 'KM006', 0.05, N'VAT 5%'),
('HD00010', '2025-05-31', N'Khách hàng mua thuốc kháng sinh', 'NV008', 'KH012', 'KM006', 0.08, N'VAT 8%'),

('HD00011', '2025-06-03', N'Khách hàng mua thuốc hạ sốt', 'NV005', 'KH017', 'KM002', 0.05, N'VAT 5%'),
('HD00012', '2025-06-06', N'Khách hàng mua thuốc điều trị dị ứng', 'NV003', 'KH022', 'KM002', 0.05, N'VAT 5%'),
('HD00013', '2025-06-09', N'Khách hàng mua thuốc đau dạ dày', 'NV010', 'KH027', 'KM002', 0.05, N'VAT 5%'),
('HD00014', '2025-06-12', N'Khách hàng mua thuốc trị táo bón', 'NV002', 'KH032', 'KM002', 0.05, N'VAT 5%'),
('HD00015', '2025-06-15', N'Khách hàng mua thuốc theo toa bác sĩ', 'NV004', 'KH003', 'KM002', 0.08, N'VAT 8%'),
('HD00016', '2025-06-18', N'Khách hàng mua thuốc chữa viêm họng', 'NV009', 'KH008', 'KM007', 0.05, N'VAT 5%'),
('HD00017', '2025-06-21', N'Khách hàng mua thuốc giảm đau', 'NV008', 'KH013', 'KM007', 0.05, N'VAT 5%'),
('HD00018', '2025-06-24', N'Khách hàng mua thuốc hạ huyết áp', 'NV005', 'KH018', 'KM007', 0.05, N'VAT 5%'),
('HD00019', '2025-06-27', N'Khách hàng mua thuốc tiểu đường', 'NV003', 'KH023', 'KM007', 0.05, N'VAT 5%'),
('HD00020', '2025-06-30', N'Khách hàng mua thuốc giảm mỡ máu', 'NV010', 'KH028', 'KM007', 0.08, N'VAT 8%'),

('HD00021', '2025-07-03', N'Khách hàng mua thuốc chống dị ứng', 'NV002', 'KH033', 'KM003', 0.05, N'VAT 5%'),
('HD00022', '2025-07-06', N'Khách hàng mua thuốc giảm đau khớp', 'NV004', 'KH004', 'KM003', 0.05, N'VAT 5%'),
('HD00023', '2025-07-09', N'Khách hàng mua thuốc trị viêm xoang', 'NV009', 'KH009', 'KM003', 0.05, N'VAT 5%'),
('HD00024', '2025-07-12', N'Khách hàng mua thuốc chữa đau răng', 'NV008', 'KH014', 'KM003', 0.05, N'VAT 5%'),
('HD00025', '2025-07-15', N'Khách hàng mua vitamin tổng hợp', 'NV005', 'KH019', 'KM003', 0.08, N'VAT 8%'),
('HD00026', '2025-07-18', N'Khách hàng mua thuốc ho', 'NV003', 'KH024', 'KM008', 0.05, N'VAT 5%'),
('HD00027', '2025-07-21', N'Khách hàng mua thuốc mũi', 'NV010', 'KH029', 'KM008', 0.05, N'VAT 5%'),
('HD00028', '2025-07-24', N'Khách hàng mua thuốc theo đơn', 'NV002', 'KH034', 'KM008', 0.05, N'VAT 5%'),
('HD00029', '2025-07-27', N'Khách hàng mua thuốc trị nấm', 'NV004', 'KH001', 'KM008', 0.05, N'VAT 5%'),
('HD00030', '2025-07-30', N'Khách hàng mua thuốc kháng sinh', 'NV009', 'KH005', 'KM008', 0.08, N'VAT 8%'),

('HD00031', '2025-08-02', N'Khách hàng mua thuốc chữa đau bụng', 'NV008', 'KH010', 'KM004', 0.05, N'VAT 5%'),
('HD00032', '2025-08-05', N'Khách hàng mua thuốc trị tiêu chảy', 'NV005', 'KH015', 'KM004', 0.05, N'VAT 5%'),
('HD00033', '2025-08-08', N'Khách hàng mua thuốc theo toa bác sĩ', 'NV003', 'KH020', 'KM004', 0.05, N'VAT 5%'),
('HD00034', '2025-08-11', N'Khách hàng mua thuốc tim mạch', 'NV010', 'KH025', 'KM004', 0.05, N'VAT 5%'),
('HD00035', '2025-08-14', N'Khách hàng mua thuốc mua định kỳ', 'NV002', 'KH030', 'KM004', 0.08, N'VAT 8%'),
('HD00036', '2025-08-17', N'Khách hàng mua thuốc giảm mỡ máu', 'NV004', 'KH002', 'KM009', 0.05, N'VAT 5%'),
('HD00037', '2025-08-20', N'Khách hàng mua thuốc kháng viêm', 'NV009', 'KH007', 'KM009', 0.05, N'VAT 5%'),
('HD00038', '2025-08-23', N'Khách hàng mua thuốc đường tiêu hóa', 'NV008', 'KH012', 'KM009', 0.05, N'VAT 5%'),
('HD00039', '2025-08-26', N'Khách hàng mua thuốc kháng histamine', 'NV005', 'KH017', 'KM009', 0.05, N'VAT 5%'),
('HD00040', '2025-08-29', N'Khách hàng mua thuốc chống buồn nôn', 'NV003', 'KH022', 'KM009', 0.08, N'VAT 8%'),

('HD00041', '2025-09-01', N'Khách hàng mua thuốc giảm đau', 'NV010', 'KH027', 'KM005', 0.05, N'VAT 5%'),
('HD00042', '2025-09-04', N'Khách hàng mua thuốc kháng sinh', 'NV002', 'KH032', 'KM005', 0.05, N'VAT 5%'),
('HD00043', '2025-09-07', N'Khách hàng mua thuốc chữa dị ứng', 'NV004', 'KH003', 'KM005', 0.05, N'VAT 5%'),
('HD00044', '2025-09-10', N'Khách hàng mua thuốc hạ sốt', 'NV009', 'KH008', 'KM005', 0.05, N'VAT 5%'),
('HD00045', '2025-09-13', N'Khách hàng mua thuốc theo đơn', 'NV008', 'KH013', 'KM005', 0.08, N'VAT 8%'),
('HD00046', '2025-09-16', N'Khách hàng mua thuốc trị đau đầu', 'NV005', 'KH018', 'KM010', 0.05, N'VAT 5%'),
('HD00047', '2025-09-19', N'Khách hàng mua thuốc mua định kỳ', 'NV003', 'KH023', 'KM010', 0.05, N'VAT 5%'),
('HD00048', '2025-09-22', N'Khách hàng mua thuốc điều trị đau khớp', 'NV010', 'KH028', 'KM010', 0.05, N'VAT 5%'),
('HD00049', '2025-09-25', N'Khách hàng mua thuốc giảm mỡ máu', 'NV002', 'KH033', 'KM010', 0.05, N'VAT 5%'),
('HD00050', '2025-09-28', N'Khách hàng mua thuốc theo toa bác sĩ', 'NV004', 'KH004', 'KM010', 0.08, N'VAT 8%');
GO

-- ===============================================
-- DỮ LIỆU MẪU CHO THÁNG 1-4 NĂM 2025
-- ===============================================

-- THÁNG 1/2025
INSERT INTO HoaDon (maHoaDon, ngayBan, ghiChu, maNV, maKH, maKM, giaTriThue, tenLoaiThue) VALUES
('HD00101', '2025-01-03', N'Khách mua thuốc cảm cúm đầu năm', 'NV002', 'KH001', 'KM001', 0.10, N'VAT 10%'),
('HD00102', '2025-01-05', N'Khách mua vitamin tăng sức đề kháng', 'NV004', 'KH005', 'KM001', 0.10, N'VAT 10%'),
('HD00103', '2025-01-08', N'Khách mua thuốc tiểu đường', 'NV008', 'KH010', 'KM001', 0.10, N'VAT 10%'),
('HD00104', '2025-01-10', N'Khách mua thuốc huyết áp', 'NV010', 'KH015', 'KM001', 0.10, N'VAT 10%'),
('HD00105', '2025-01-12', N'Khách mua thuốc dạ dày', 'NV002', 'KH020', 'KM002', 0.10, N'VAT 10%'),
('HD00106', '2025-01-15', N'Khách mua kháng sinh', 'NV004', 'KH025', 'KM002', 0.10, N'VAT 10%'),
('HD00107', '2025-01-18', N'Khách mua thuốc giảm đau', 'NV008', 'KH030', 'KM002', 0.10, N'VAT 10%'),
('HD00108', '2025-01-20', N'Khách mua thuốc chống dị ứng', 'NV010', 'KH002', 'KM002', 0.10, N'VAT 10%'),
('HD00109', '2025-01-23', N'Khách mua thuốc ho', 'NV002', 'KH007', 'KM003', 0.10, N'VAT 10%'),
('HD00110', '2025-01-25', N'Khách mua thuốc hạ sốt', 'NV004', 'KH012', 'KM003', 0.10, N'VAT 10%'),
('HD00111', '2025-01-28', N'Khách mua thuốc giảm mỡ máu', 'NV008', 'KH017', 'KM003', 0.10, N'VAT 10%'),
('HD00112', '2025-01-30', N'Khách mua thuốc tim mạch', 'NV010', 'KH022', 'KM003', 0.10, N'VAT 10%');

-- THÁNG 2/2025
INSERT INTO HoaDon (maHoaDon, ngayBan, ghiChu, maNV, maKH, maKM, giaTriThue, tenLoaiThue) VALUES
('HD00113', '2025-02-02', N'Khách mua thuốc viêm họng', 'NV002', 'KH003', 'KM004', 0.10, N'VAT 10%'),
('HD00114', '2025-02-04', N'Khách mua thuốc mũi', 'NV004', 'KH008', 'KM004', 0.10, N'VAT 10%'),
('HD00115', '2025-02-07', N'Khách mua vitamin cho trẻ em', 'NV008', 'KH013', 'KM004', 0.10, N'VAT 10%'),
('HD00116', '2025-02-09', N'Khách mua thuốc tiêu hóa', 'NV010', 'KH018', 'KM004', 0.10, N'VAT 10%'),
('HD00117', '2025-02-12', N'Khách mua thuốc kháng viêm', 'NV002', 'KH023', 'KM005', 0.10, N'VAT 10%'),
('HD00118', '2025-02-14', N'Khách mua thuốc đau khớp', 'NV004', 'KH028', 'KM005', 0.10, N'VAT 10%'),
('HD00119', '2025-02-16', N'Khách mua thuốc bổ gan', 'NV008', 'KH033', 'KM005', 0.10, N'VAT 10%'),
('HD00120', '2025-02-18', N'Khách mua thuốc chống nôn', 'NV010', 'KH004', 'KM005', 0.10, N'VAT 10%'),
('HD00121', '2025-02-21', N'Khách mua thuốc hô hấp', 'NV002', 'KH009', 'KM006', 0.10, N'VAT 10%'),
('HD00122', '2025-02-23', N'Khách mua thuốc dị ứng da', 'NV004', 'KH014', 'KM006', 0.10, N'VAT 10%'),
('HD00123', '2025-02-25', N'Khách mua thuốc mắt', 'NV008', 'KH019', 'KM006', 0.10, N'VAT 10%'),
('HD00124', '2025-02-27', N'Khách mua thuốc thần kinh', 'NV010', 'KH024', 'KM006', 0.10, N'VAT 10%');

-- THÁNG 3/2025
INSERT INTO HoaDon (maHoaDon, ngayBan, ghiChu, maNV, maKH, maKM, giaTriThue, tenLoaiThue) VALUES
('HD00125', '2025-03-02', N'Khách mua thuốc cao huyết áp', 'NV002', 'KH006', 'KM007', 0.10, N'VAT 10%'),
('HD00126', '2025-03-05', N'Khách mua thuốc đái tháo đường', 'NV004', 'KH011', 'KM007', 0.10, N'VAT 10%'),
('HD00127', '2025-03-07', N'Khách mua vitamin tổng hợp', 'NV008', 'KH016', 'KM007', 0.10, N'VAT 10%'),
('HD00128', '2025-03-10', N'Khách mua thuốc giảm cholesterol', 'NV010', 'KH021', 'KM007', 0.10, N'VAT 10%'),
('HD00129', '2025-03-12', N'Khách mua kháng sinh cao cấp', 'NV002', 'KH026', 'KM008', 0.10, N'VAT 10%'),
('HD00130', '2025-03-15', N'Khách mua thuốc chống viêm xoang', 'NV004', 'KH031', 'KM008', 0.10, N'VAT 10%'),
('HD00131', '2025-03-18', N'Khách mua thuốc giảm đau đầu', 'NV008', 'KH035', 'KM008', 0.10, N'VAT 10%'),
('HD00132', '2025-03-20', N'Khách mua thuốc trị táo bón', 'NV010', 'KH001', 'KM008', 0.10, N'VAT 10%'),
('HD00133', '2025-03-23', N'Khách mua thuốc trị tiêu chảy', 'NV002', 'KH007', 'KM009', 0.10, N'VAT 10%'),
('HD00134', '2025-03-25', N'Khách mua thuốc chữa đau răng', 'NV004', 'KH012', 'KM009', 0.10, N'VAT 10%'),
('HD00135', '2025-03-27', N'Khách mua thuốc bổ sung canxi', 'NV008', 'KH017', 'KM009', 0.10, N'VAT 10%'),
('HD00136', '2025-03-30', N'Khách mua thuốc hen suyễn', 'NV010', 'KH022', 'KM009', 0.10, N'VAT 10%');

-- THÁNG 4/2025
INSERT INTO HoaDon (maHoaDon, ngayBan, ghiChu, maNV, maKH, maKM, giaTriThue, tenLoaiThue) VALUES
('HD00137', '2025-04-02', N'Khách mua combo thuốc cảm', 'NV002', 'KH003', 'KM010', 0.10, N'VAT 10%'),
('HD00138', '2025-04-05', N'Khách mua thuốc trị nấm', 'NV004', 'KH008', 'KM010', 0.10, N'VAT 10%'),
('HD00139', '2025-04-08', N'Khách mua thuốc mãn tính', 'NV008', 'KH013', 'KM010', 0.10, N'VAT 10%'),
('HD00140', '2025-04-10', N'Khách mua thuốc tăng cường miễn dịch', 'NV010', 'KH018', 'KM010', 0.10, N'VAT 10%'),
('HD00141', '2025-04-12', N'Khách mua thuốc giảm cân', 'NV002', 'KH023', 'KM001', 0.10, N'VAT 10%'),
('HD00142', '2025-04-15', N'Khách mua thuốc bổ thận', 'NV004', 'KH028', 'KM001', 0.10, N'VAT 10%'),
('HD00143', '2025-04-18', N'Khách mua thuốc chống lão hóa', 'NV008', 'KH033', 'KM001', 0.10, N'VAT 10%'),
('HD00144', '2025-04-20', N'Khách mua thuốc bổ máu', 'NV010', 'KH005', 'KM001', 0.10, N'VAT 10%'),
('HD00145', '2025-04-23', N'Khách mua thuốc điều trị gút', 'NV002', 'KH010', 'KM002', 0.10, N'VAT 10%'),
('HD00146', '2025-04-25', N'Khách mua thuốc trị mất ngủ', 'NV004', 'KH015', 'KM002', 0.10, N'VAT 10%'),
('HD00147', '2025-04-27', N'Khách mua thuốc bổ não', 'NV008', 'KH020', 'KM002', 0.10, N'VAT 10%'),
('HD00148', '2025-04-29', N'Khách mua thuốc chống stress', 'NV010', 'KH025', 'KM002', 0.10, N'VAT 10%');
GO

-- ===============================================
-- DỮ LIỆU MẪU CHO THÁNG 5-8 NĂM 2025
-- ===============================================

-- THÁNG 5/2025 - Doanh thu mục tiêu: ~3.8 triệu
INSERT INTO HoaDon (maHoaDon, ngayBan, ghiChu, maNV, maKH, maKM, giaTriThue, tenLoaiThue) VALUES
('HD00149', '2025-05-03', N'Khách mua thuốc điều trị mãn tính', 'NV002', 'KH001', 'KM003', 0.10, N'VAT 10%'),
('HD00150', '2025-05-06', N'Khách mua combo thuốc tim mạch', 'NV004', 'KH005', 'KM003', 0.10, N'VAT 10%'),
('HD00151', '2025-05-09', N'Khách mua thuốc tiểu đường cao cấp', 'NV008', 'KH010', 'KM003', 0.10, N'VAT 10%'),
('HD00152', '2025-05-12', N'Khách mua kháng sinh nhập khẩu', 'NV010', 'KH015', 'KM004', 0.10, N'VAT 10%'),
('HD00153', '2025-05-15', N'Khách mua thuốc dạ dày cao cấp', 'NV002', 'KH020', 'KM004', 0.10, N'VAT 10%'),
('HD00154', '2025-05-18', N'Khách mua vitamin nhập khẩu', 'NV004', 'KH025', 'KM004', 0.10, N'VAT 10%'),
('HD00155', '2025-05-21', N'Khách mua thuốc giảm cholesterol', 'NV008', 'KH030', 'KM005', 0.10, N'VAT 10%'),
('HD00156', '2025-05-24', N'Khách mua thuốc huyết áp cao cấp', 'NV010', 'KH002', 'KM005', 0.10, N'VAT 10%'),
('HD00157', '2025-05-27', N'Khách mua combo sức khỏe', 'NV002', 'KH007', 'KM005', 0.10, N'VAT 10%'),
('HD00158', '2025-05-30', N'Khách mua thuốc bổ gan cao cấp', 'NV004', 'KH012', 'KM005', 0.10, N'VAT 10%');

-- THÁNG 6/2025 - Doanh thu mục tiêu: ~4.2 triệu
INSERT INTO HoaDon (maHoaDon, ngayBan, ghiChu, maNV, maKH, maKM, giaTriThue, tenLoaiThue) VALUES
('HD00159', '2025-06-02', N'Khách mua thuốc điều trị đau khớp', 'NV002', 'KH003', 'KM006', 0.10, N'VAT 10%'),
('HD00160', '2025-06-05', N'Khách mua thuốc thần kinh cao cấp', 'NV004', 'KH008', 'KM006', 0.10, N'VAT 10%'),
('HD00161', '2025-06-08', N'Khách mua kháng sinh đặc trị', 'NV008', 'KH013', 'KM006', 0.10, N'VAT 10%'),
('HD00162', '2025-06-11', N'Khách mua thuốc tim mạch nhập khẩu', 'NV010', 'KH018', 'KM007', 0.10, N'VAT 10%'),
('HD00163', '2025-06-14', N'Khách mua combo vitamin cao cấp', 'NV002', 'KH023', 'KM007', 0.10, N'VAT 10%'),
('HD00164', '2025-06-17', N'Khách mua thuốc giảm mỡ máu', 'NV004', 'KH028', 'KM007', 0.10, N'VAT 10%'),
('HD00165', '2025-06-20', N'Khách mua thuốc chống viêm cao cấp', 'NV008', 'KH033', 'KM008', 0.10, N'VAT 10%'),
('HD00166', '2025-06-23', N'Khách mua thuốc dạ dày đặc trị', 'NV010', 'KH004', 'KM008', 0.10, N'VAT 10%'),
('HD00167', '2025-06-26', N'Khách mua thuốc hô hấp cao cấp', 'NV002', 'KH009', 'KM008', 0.10, N'VAT 10%'),
('HD00168', '2025-06-29', N'Khách mua thuốc bổ thận cao cấp', 'NV004', 'KH014', 'KM008', 0.10, N'VAT 10%');

-- THÁNG 7/2025 - Doanh thu mục tiêu: ~3.5 triệu
INSERT INTO HoaDon (maHoaDon, ngayBan, ghiChu, maNV, maKH, maKM, giaTriThue, tenLoaiThue) VALUES
('HD00169', '2025-07-02', N'Khách mua thuốc chống lão hóa', 'NV002', 'KH006', 'KM009', 0.10, N'VAT 10%'),
('HD00170', '2025-07-05', N'Khách mua thuốc bổ máu cao cấp', 'NV004', 'KH011', 'KM009', 0.10, N'VAT 10%'),
('HD00171', '2025-07-08', N'Khách mua kháng sinh cao cấp', 'NV008', 'KH016', 'KM009', 0.10, N'VAT 10%'),
('HD00172', '2025-07-11', N'Khách mua thuốc điều trị gút', 'NV010', 'KH021', 'KM010', 0.10, N'VAT 10%'),
('HD00173', '2025-07-14', N'Khách mua thuốc giảm đau khớp', 'NV002', 'KH026', 'KM010', 0.10, N'VAT 10%'),
('HD00174', '2025-07-17', N'Khách mua vitamin tổng hợp nhập khẩu', 'NV004', 'KH031', 'KM010', 0.10, N'VAT 10%'),
('HD00175', '2025-07-20', N'Khách mua thuốc mất ngủ cao cấp', 'NV008', 'KH035', 'KM001', 0.10, N'VAT 10%'),
('HD00176', '2025-07-23', N'Khách mua thuốc bổ não', 'NV010', 'KH001', 'KM001', 0.10, N'VAT 10%'),
('HD00177', '2025-07-26', N'Khách mua thuốc tim mạch đặc trị', 'NV002', 'KH007', 'KM001', 0.10, N'VAT 10%'),
('HD00178', '2025-07-29', N'Khách mua combo chăm sóc sức khỏe', 'NV004', 'KH012', 'KM001', 0.10, N'VAT 10%');

-- THÁNG 8/2025 - Doanh thu mục tiêu: ~2.8 triệu
INSERT INTO HoaDon (maHoaDon, ngayBan, ghiChu, maNV, maKH, maKM, giaTriThue, tenLoaiThue) VALUES
('HD00179', '2025-08-02', N'Khách mua thuốc huyết áp nhập khẩu', 'NV002', 'KH003', 'KM002', 0.10, N'VAT 10%'),
('HD00180', '2025-08-05', N'Khách mua thuốc tiểu đường đặc trị', 'NV004', 'KH008', 'KM002', 0.10, N'VAT 10%'),
('HD00181', '2025-08-08', N'Khách mua kháng sinh thế hệ mới', 'NV008', 'KH013', 'KM002', 0.10, N'VAT 10%'),
('HD00182', '2025-08-11', N'Khách mua thuốc chống viêm đặc trị', 'NV010', 'KH018', 'KM003', 0.10, N'VAT 10%'),
('HD00183', '2025-08-14', N'Khách mua thuốc dạ dày nhập khẩu', 'NV002', 'KH023', 'KM003', 0.10, N'VAT 10%'),
('HD00184', '2025-08-17', N'Khách mua vitamin cao cấp', 'NV004', 'KH028', 'KM003', 0.10, N'VAT 10%'),
('HD00185', '2025-08-20', N'Khách mua thuốc giảm cholesterol', 'NV008', 'KH033', 'KM004', 0.10, N'VAT 10%'),
('HD00186', '2025-08-23', N'Khách mua thuốc bổ gan đặc trị', 'NV010', 'KH005', 'KM004', 0.10, N'VAT 10%'),
('HD00187', '2025-08-26', N'Khách mua thuốc thần kinh trung ương', 'NV002', 'KH010', 'KM004', 0.10, N'VAT 10%'),
('HD00188', '2025-08-29', N'Khách mua combo sức khỏe toàn diện', 'NV004', 'KH015', 'KM004', 0.10, N'VAT 10%');
GO

-- ===============================================
-- DỮ LIỆU MẪU CHO THÁNG 9-12 NĂM 2024
-- ===============================================

-- THÁNG 9/2024 - Doanh thu mục tiêu: ~5 triệu
INSERT INTO HoaDon (maHoaDon, ngayBan, ghiChu, maNV, maKH, maKM, giaTriThue, tenLoaiThue) VALUES
('HD00051', '2024-09-02', N'Khách mua thuốc cảm cúm', 'NV002', 'KH001', NULL, 0.10, N'VAT 10%'),
('HD00052', '2024-09-05', N'Khách mua vitamin tổng hợp', 'NV004', 'KH003', NULL, 0.10, N'VAT 10%'),
('HD00053', '2024-09-08', N'Khách mua thuốc theo đơn', 'NV008', 'KH005', NULL, 0.10, N'VAT 10%'),
('HD00054', '2024-09-10', N'Khách mua thuốc huyết áp', 'NV010', 'KH007', NULL, 0.10, N'VAT 10%'),
('HD00055', '2024-09-12', N'Khách mua thuốc tiểu đường', 'NV002', 'KH009', NULL, 0.10, N'VAT 10%'),
('HD00056', '2024-09-15', N'Khách mua thuốc dạ dày', 'NV004', 'KH011', NULL, 0.10, N'VAT 10%'),
('HD00057', '2024-09-18', N'Khách mua kháng sinh', 'NV008', 'KH013', NULL, 0.10, N'VAT 10%'),
('HD00058', '2024-09-20', N'Khách mua thuốc giảm đau', 'NV010', 'KH015', NULL, 0.10, N'VAT 10%'),
('HD00059', '2024-09-22', N'Khách mua thuốc chống dị ứng', 'NV002', 'KH017', NULL, 0.10, N'VAT 10%'),
('HD00060', '2024-09-25', N'Khách mua thuốc giảm mỡ máu', 'NV004', 'KH019', NULL, 0.10, N'VAT 10%'),
('HD00061', '2024-09-27', N'Khách mua thuốc ho', 'NV008', 'KH021', NULL, 0.10, N'VAT 10%'),
('HD00062', '2024-09-29', N'Khách mua thuốc hạ sốt', 'NV010', 'KH023', NULL, 0.10, N'VAT 10%');

-- THÁNG 10/2024 - Doanh thu mục tiêu: ~4.5 triệu
INSERT INTO HoaDon (maHoaDon, ngayBan, ghiChu, maNV, maKH, maKM, giaTriThue, tenLoaiThue) VALUES
('HD00063', '2024-10-01', N'Khách mua thuốc tim mạch', 'NV002', 'KH002', NULL, 0.10, N'VAT 10%'),
('HD00064', '2024-10-04', N'Khách mua thuốc khớp', 'NV004', 'KH004', NULL, 0.10, N'VAT 10%'),
('HD00065', '2024-10-07', N'Khách mua thuốc viêm xoang', 'NV008', 'KH006', NULL, 0.10, N'VAT 10%'),
('HD00066', '2024-10-09', N'Khách mua thuốc đường hô hấp', 'NV010', 'KH008', NULL, 0.10, N'VAT 10%'),
('HD00067', '2024-10-12', N'Khách mua thuốc mắt', 'NV002', 'KH010', NULL, 0.10, N'VAT 10%'),
('HD00068', '2024-10-14', N'Khách mua thuốc dị ứng da', 'NV004', 'KH012', NULL, 0.10, N'VAT 10%'),
('HD00069', '2024-10-17', N'Khách mua thuốc tiêu hóa', 'NV008', 'KH014', NULL, 0.10, N'VAT 10%'),
('HD00070', '2024-10-19', N'Khách mua vitamin cho trẻ', 'NV010', 'KH016', NULL, 0.10, N'VAT 10%'),
('HD00071', '2024-10-22', N'Khách mua thuốc kháng viêm', 'NV002', 'KH018', NULL, 0.10, N'VAT 10%'),
('HD00072', '2024-10-24', N'Khách mua thuốc chống nôn', 'NV004', 'KH020', NULL, 0.10, N'VAT 10%'),
('HD00073', '2024-10-27', N'Khách mua thuốc bổ sung sắt', 'NV008', 'KH022', NULL, 0.10, N'VAT 10%'),
('HD00074', '2024-10-30', N'Khách mua thuốc hen suyễn', 'NV010', 'KH024', NULL, 0.10, N'VAT 10%');

-- THÁNG 11/2024 - Doanh thu mục tiêu: ~6 triệu
INSERT INTO HoaDon (maHoaDon, ngayBan, ghiChu, maNV, maKH, maKM, giaTriThue, tenLoaiThue) VALUES
('HD00075', '2024-11-02', N'Khách mua combo thuốc cảm', 'NV002', 'KH025', NULL, 0.10, N'VAT 10%'),
('HD00076', '2024-11-05', N'Khách mua thuốc mãn tính', 'NV004', 'KH027', NULL, 0.10, N'VAT 10%'),
('HD00077', '2024-11-07', N'Khách mua thuốc cao huyết áp', 'NV008', 'KH029', NULL, 0.10, N'VAT 10%'),
('HD00078', '2024-11-09', N'Khách mua kháng sinh cao cấp', 'NV010', 'KH031', NULL, 0.10, N'VAT 10%'),
('HD00079', '2024-11-12', N'Khách mua thuốc dạ dày cao cấp', 'NV002', 'KH033', NULL, 0.10, N'VAT 10%'),
('HD00080', '2024-11-14', N'Khách mua vitamin nhập khẩu', 'NV004', 'KH035', NULL, 0.10, N'VAT 10%'),
('HD00081', '2024-11-16', N'Khách mua thuốc giảm cholesterol', 'NV008', 'KH001', NULL, 0.10, N'VAT 10%'),
('HD00082', '2024-11-19', N'Khách mua thuốc đái tháo đường', 'NV010', 'KH003', NULL, 0.10, N'VAT 10%'),
('HD00083', '2024-11-21', N'Khách mua thuốc giảm đau khớp', 'NV002', 'KH005', NULL, 0.10, N'VAT 10%'),
('HD00084', '2024-11-23', N'Khách mua thuốc hô hấp', 'NV004', 'KH007', NULL, 0.10, N'VAT 10%'),
('HD00085', '2024-11-26', N'Khách mua thuốc thần kinh', 'NV008', 'KH009', NULL, 0.10, N'VAT 10%'),
('HD00086', '2024-11-28', N'Khách mua thuốc bổ gan', 'NV010', 'KH011', NULL, 0.10, N'VAT 10%'),
('HD00087', '2024-11-30', N'Khách mua combo sức khỏe', 'NV002', 'KH013', NULL, 0.10, N'VAT 10%');

-- THÁNG 12/2024 - Doanh thu mục tiêu: ~5.5 triệu
INSERT INTO HoaDon (maHoaDon, ngayBan, ghiChu, maNV, maKH, maKM, giaTriThue, tenLoaiThue) VALUES
('HD00088', '2024-12-01', N'Khách mua thuốc chống cảm', 'NV002', 'KH015', NULL, 0.10, N'VAT 10%'),
('HD00089', '2024-12-04', N'Khách mua thuốc tăng cường miễn dịch', 'NV004', 'KH017', NULL, 0.10, N'VAT 10%'),
('HD00090', '2024-12-06', N'Khách mua thuốc viêm họng', 'NV008', 'KH019', NULL, 0.10, N'VAT 10%'),
('HD00091', '2024-12-09', N'Khách mua thuốc mũi', 'NV010', 'KH021', NULL, 0.10, N'VAT 10%'),
('HD00092', '2024-12-11', N'Khách mua thuốc giảm ho', 'NV002', 'KH023', NULL, 0.10, N'VAT 10%'),
('HD00093', '2024-12-13', N'Khách mua thuốc hạ sốt trẻ em', 'NV004', 'KH025', NULL, 0.10, N'VAT 10%'),
('HD00094', '2024-12-16', N'Khách mua thuốc tiêu hóa', 'NV008', 'KH027', NULL, 0.10, N'VAT 10%'),
('HD00095', '2024-12-18', N'Khách mua thuốc giảm đau đầu', 'NV010', 'KH029', NULL, 0.10, N'VAT 10%'),
('HD00096', '2024-12-20', N'Khách mua vitamin mùa đông', 'NV002', 'KH031', NULL, 0.10, N'VAT 10%'),
('HD00097', '2024-12-22', N'Khách mua thuốc dị ứng thời tiết', 'NV004', 'KH033', NULL, 0.10, N'VAT 10%'),
('HD00098', '2024-12-25', N'Khách mua thuốc định kỳ', 'NV008', 'KH035', NULL, 0.10, N'VAT 10%'),
('HD00099', '2024-12-27', N'Khách mua thuốc mãn tính cuối năm', 'NV010', 'KH002', NULL, 0.10, N'VAT 10%');

GO

-- Chèn dữ liệu vào bảng ChiTietHoaDon (ĐÃ CẬP NHẬT với maLo)
INSERT INTO ChiTietHoaDon (maHoaDon, maThuoc, maLo, soLuong, donGia) VALUES
-- Hóa đơn 1
('HD00001', 'T001', 'LO001', 2, 5000),
('HD00001', 'T021', 'LO011', 1, 8000),
('HD00001', 'T026', 'LO013', 1, 7500),
-- Hóa đơn 2
('HD00002', 'T006', 'LO003', 1, 15000),
('HD00002', 'T011', 'LO006', 2, 14000),
-- Hóa đơn 3
('HD00003', 'T031', 'LO016', 2, 8500),
('HD00003', 'T036', 'LO018', 1, 7000),
('HD00003', 'T041', 'LO021', 1, 15000),
-- Hóa đơn 4
('HD00004', 'T002', 'LO001', 1, 7500),
('HD00004', 'T014', 'LO007', 1, 8500),
('HD00004', 'T022', 'LO011', 2, 12000),
-- Hóa đơn 5
('HD00005', 'T007', 'LO004', 1, 25000),
('HD00005', 'T029', 'LO015', 2, 14000),
-- Thêm các chi tiết hóa đơn khác
('HD00006', 'T003', 'LO002', 2, 8000),
('HD00006', 'T016', 'LO008', 3, 7000),
('HD00007', 'T009', 'LO005', 1, 18000),
('HD00007', 'T020', 'LO010', 1, 10500),
('HD00007', 'T023', 'LO012', 1, 15000),
('HD00008', 'T021', 'LO011', 3, 8000),
('HD00008', 'T022', 'LO011', 2, 12000),
('HD00008', 'T024', 'LO012', 1, 13500),
('HD00009', 'T001', 'LO001', 2, 5000),
('HD00009', 'T006', 'LO003', 1, 15000),
('HD00009', 'T056', 'LO003', 1, 7000),
('HD00010', 'T007', 'LO004', 1, 25000),
('HD00010', 'T008', 'LO004', 1, 22000),
('HD00010', 'T010', 'LO005', 1, 16000),
('HD00011', 'T001', 'LO001', 2, 5000),
('HD00011', 'T002', 'LO001', 1, 7500),
('HD00011', 'T004', 'LO002', 1, 6500),
('HD00012', 'T026', 'LO013', 1, 7500),
('HD00012', 'T027', 'LO014', 1, 8000),
('HD00012', 'T030', 'LO015', 1, 18000),
('HD00013', 'T011', 'LO006', 1, 14000),
('HD00013', 'T012', 'LO006', 1, 32000),
('HD00013', 'T014', 'LO007', 1, 8500),
('HD00014', 'T016', 'LO008', 2, 7000),
('HD00014', 'T018', 'LO009', 1, 11000),
('HD00014', 'T020', 'LO010', 1, 10500),
('HD00015', 'T031', 'LO016', 2, 8500),
('HD00015', 'T033', 'LO017', 1, 12000),
('HD00015', 'T035', 'LO018', 1, 11000),
('HD00016', 'T006', 'LO003', 1, 15000),
('HD00016', 'T056', 'LO003', 1, 7000),
('HD00016', 'T055', 'LO003', 2, 6500),
('HD00017', 'T046', 'LO023', 1, 11000),
('HD00017', 'T047', 'LO024', 2, 8500),
('HD00017', 'T003', 'LO002', 1, 8000),
('HD00018', 'T031', 'LO016', 2, 8500),
('HD00018', 'T032', 'LO016', 1, 10500),
('HD00018', 'T034', 'LO017', 1, 9500),
('HD00019', 'T036', 'LO018', 2, 7000),
('HD00019', 'T037', 'LO019', 1, 8500),
('HD00019', 'T040', 'LO020', 1, 28000),
('HD00020', 'T041', 'LO021', 1, 15000),
('HD00020', 'T043', 'LO022', 1, 22000),
('HD00020', 'T045', 'LO023', 1, 26000),
('HD00021', 'T026', 'LO013', 1, 7500),
('HD00021', 'T028', 'LO014', 1, 12500),
('HD00021', 'T029', 'LO015', 1, 14000),
('HD00022', 'T046', 'LO023', 2, 11000),
('HD00022', 'T048', 'LO024', 1, 16000),
('HD00022', 'T050', 'LO025', 1, 9500),
('HD00023', 'T055', 'LO003', 2, 6500),
('HD00023', 'T056', 'LO003', 1, 7000),
('HD00023', 'T057', 'LO004', 1, 12000),
('HD00024', 'T001', 'LO001', 1, 5000),
('HD00024', 'T047', 'LO024', 1, 8500),
('HD00024', 'T049', 'LO025', 1, 18000),
('HD00025', 'T021', 'LO011', 2, 8000),
('HD00025', 'T023', 'LO012', 1, 15000),
('HD00025', 'T025', 'LO013', 1, 9500),
('HD00026', 'T055', 'LO003', 2, 6500),
('HD00026', 'T056', 'LO003', 1, 7000),
('HD00026', 'T058', 'LO004', 1, 9500),
('HD00027', 'T057', 'LO004', 1, 12000),
('HD00027', 'T058', 'LO004', 1, 9500),
('HD00027', 'T055', 'LO003', 1, 6500),
('HD00028', 'T031', 'LO016', 1, 8500),
('HD00028', 'T036', 'LO018', 1, 7000),
('HD00028', 'T041', 'LO021', 1, 15000),
('HD00028', 'T046', 'LO023', 1, 11000),
('HD00029', 'T059', 'LO005', 1, 18000),
('HD00029', 'T060', 'LO005', 1, 16000),
('HD00030', 'T006', 'LO003', 1, 15000),
('HD00030', 'T008', 'LO004', 1, 22000),
('HD00030', 'T009', 'LO005', 1, 18000),
('HD00031', 'T011', 'LO006', 2, 14000),
('HD00031', 'T013', 'LO007', 1, 28000),
('HD00031', 'T014', 'LO007', 1, 8500),
('HD00032', 'T016', 'LO008', 2, 7000),
('HD00032', 'T017', 'LO009', 1, 12500),
('HD00032', 'T019', 'LO010', 1, 9000),
('HD00033', 'T036', 'LO018', 1, 7000),
('HD00033', 'T039', 'LO020', 1, 13000),
('HD00033', 'T042', 'LO021', 1, 19000),
('HD00034', 'T031', 'LO016', 1, 8500),
('HD00034', 'T033', 'LO017', 1, 12000),
('HD00034', 'T076', 'LO013', 1, 12500),
('HD00034', 'T078', 'LO014', 1, 11000),
('HD00035', 'T041', 'LO021', 1, 15000),
('HD00035', 'T044', 'LO022', 1, 14000),
('HD00035', 'T036', 'LO018', 1, 7000),
('HD00035', 'T038', 'LO019', 1, 6500),
('HD00036', 'T043', 'LO022', 1, 22000),
('HD00036', 'T044', 'LO022', 1, 14000),
('HD00036', 'T045', 'LO023', 1, 26000),
('HD00037', 'T046', 'LO023', 2, 11000),
('HD00037', 'T047', 'LO024', 1, 8500),
('HD00037', 'T048', 'LO024', 1, 16000),
('HD00038', 'T011', 'LO006', 1, 14000),
('HD00038', 'T016', 'LO008', 1, 7000),
('HD00038', 'T020', 'LO010', 1, 10500),
('HD00039', 'T026', 'LO013', 2, 7500),
('HD00039', 'T027', 'LO014', 1, 8000),
('HD00039', 'T029', 'LO015', 1, 14000),
('HD00040', 'T064', 'LO007', 1, 9500),
('HD00040', 'T065', 'LO008', 1, 6000),
('HD00040', 'T066', 'LO008', 1, 14000),
-- Thêm chi tiết hóa đơn cho hóa đơn HD00041 - HD00050
('HD00041', 'T023', 'LO012', 2, 35000),
('HD00041', 'T045', 'LO023', 1, 72500),
('HD00041', 'T012', 'LO006', 3, 12000),
('HD00041', 'T078', 'LO014', 1, 98000),
('HD00041', 'T056', 'LO003', 2, 45000),
('HD00041', 'T089', 'LO020', 1, 125000),
('HD00041', 'T034', 'LO017', 1, 65000),
('HD00041', 'T067', 'LO009', 2, 28000),
('HD00042', 'T015', 'LO008', 2, 42000),
('HD00042', 'T032', 'LO016', 1, 85000),
('HD00042', 'T058', 'LO004', 3, 18000),
('HD00042', 'T091', 'LO021', 1, 135000),
('HD00042', 'T007', 'LO004', 2, 26000),
('HD00042', 'T063', 'LO007', 1, 75000),
('HD00043', 'T019', 'LO010', 1, 58000),
('HD00043', 'T037', 'LO019', 2, 32000),
('HD00043', 'T052', 'LO001', 1, 145000),
('HD00043', 'T084', 'LO017', 1, 92000),
('HD00043', 'T021', 'LO011', 3, 15000),
('HD00043', 'T048', 'LO024', 2, 48000),
('HD00043', 'T073', 'LO012', 1, 105000),
('HD00043', 'T095', 'LO023', 1, 78000),
('HD00043', 'T005', 'LO003', 2, 22000),
('HD00044', 'T042', 'LO021', 1, 62000),
('HD00044', 'T081', 'LO016', 2, 38000),
('HD00044', 'T027', 'LO014', 1, 115000),
('HD00044', 'T069', 'LO010', 3, 25000),
('HD00044', 'T053', 'LO002', 1, 95000),
('HD00045', 'T011', 'LO006', 2, 32000),
('HD00045', 'T038', 'LO019', 1, 85000),
('HD00045', 'T074', 'LO012', 1, 118000),
('HD00045', 'T029', 'LO015', 3, 28000),
('HD00045', 'T047', 'LO024', 1, 75000),
('HD00045', 'T062', 'LO006', 2, 42000),
('HD00045', 'T093', 'LO022', 1, 155000),
('HD00046', 'T003', 'LO002', 2, 18000),
('HD00046', 'T024', 'LO012', 1, 65000),
('HD00046', 'T041', 'LO021', 1, 95000),
('HD00046', 'T057', 'LO004', 3, 22000),
('HD00046', 'T082', 'LO016', 1, 112000),
('HD00046', 'T018', 'LO009', 2, 28000),
('HD00046', 'T035', 'LO018', 1, 78000),
('HD00046', 'T064', 'LO007', 1, 48000),
('HD00046', 'T099', 'LO025', 2, 32000),
('HD00046', 'T076', 'LO013', 1, 125000),
('HD00047', 'T008', 'LO004', 2, 25000),
('HD00047', 'T031', 'LO016', 1, 78000),
('HD00047', 'T059', 'LO005', 3, 19000),
('HD00047', 'T086', 'LO018', 1, 95000),
('HD00047', 'T046', 'LO023', 2, 45000),
('HD00047', 'T071', 'LO011', 1, 88000),
('HD00048', 'T013', 'LO007', 1, 35000),
('HD00048', 'T028', 'LO014', 2, 42000),
('HD00048', 'T055', 'LO003', 1, 68000),
('HD00048', 'T079', 'LO015', 1, 115000),
('HD00048', 'T004', 'LO002', 3, 15000),
('HD00048', 'T039', 'LO020', 1, 75000),
('HD00048', 'T072', 'LO011', 2, 58000),
('HD00048', 'T097', 'LO024', 1, 135000),
('HD00049', 'T017', 'LO009', 2, 28000),
('HD00049', 'T043', 'LO022', 1, 95000),
('HD00049', 'T068', 'LO009', 1, 48000),
('HD00049', 'T092', 'LO021', 1, 145000),
('HD00049', 'T025', 'LO013', 3, 22000),
('HD00049', 'T051', 'LO001', 2, 65000),
('HD00049', 'T087', 'LO019', 1, 118000),
('HD00050', 'T006', 'LO003', 2, 19000),
('HD00050', 'T033', 'LO017', 1, 85000),
('HD00050', 'T054', 'LO002', 1, 78000),
('HD00050', 'T017', 'LO009', 2, 28000);

GO

-- ===============================================
-- CHI TIẾT HÓA ĐƠN CHO THÁNG 9/2024
-- Tổng doanh thu mục tiêu: ~5 triệu
-- ===============================================
INSERT INTO ChiTietHoaDon (maHoaDon, maThuoc, maLo, soLuong, donGia) VALUES
-- HD00051: ~250k
('HD00051', 'T001', 'LO001', 10, 5000),
('HD00051', 'T021', 'LO011', 5, 8000),
('HD00051', 'T055', 'LO003', 10, 6500),
('HD00051', 'T056', 'LO003', 8, 7000),

-- HD00052: ~180k
('HD00052', 'T023', 'LO012', 5, 15000),
('HD00052', 'T024', 'LO012', 3, 13500),
('HD00052', 'T025', 'LO013', 5, 9500),

-- HD00053: ~550k
('HD00053', 'T007', 'LO004', 5, 25000),
('HD00053', 'T012', 'LO006', 5, 32000),
('HD00053', 'T041', 'LO021', 10, 15000),
('HD00053', 'T046', 'LO023', 8, 11000),

-- HD00054: ~320k
('HD00054', 'T031', 'LO016', 15, 8500),
('HD00054', 'T033', 'LO017', 8, 12000),
('HD00054', 'T035', 'LO018', 5, 11000),

-- HD00055: ~420k
('HD00055', 'T036', 'LO018', 20, 7000),
('HD00055', 'T037', 'LO019', 10, 8500),
('HD00055', 'T093', 'LO022', 8, 15000),

-- HD00056: ~380k
('HD00056', 'T011', 'LO006', 10, 14000),
('HD00056', 'T013', 'LO007', 5, 28000),
('HD00056', 'T016', 'LO008', 15, 7000),

-- HD00057: ~680k
('HD00057', 'T006', 'LO003', 15, 15000),
('HD00057', 'T008', 'LO004', 10, 22000),
('HD00057', 'T009', 'LO005', 12, 18000),

-- HD00058: ~290k
('HD00058', 'T003', 'LO002', 12, 8000),
('HD00058', 'T046', 'LO023', 10, 11000),
('HD00058', 'T047', 'LO024', 8, 8500),

-- HD00059: ~240k
('HD00059', 'T026', 'LO013', 15, 7500),
('HD00059', 'T027', 'LO014', 12, 8000),
('HD00059', 'T029', 'LO015', 4, 14000),

-- HD00060: ~580k
('HD00060', 'T042', 'LO021', 10, 19000),
('HD00060', 'T043', 'LO022', 8, 22000),
('HD00060', 'T044', 'LO022', 12, 14000),

-- HD00061: ~170k
('HD00061', 'T055', 'LO003', 12, 6500),
('HD00061', 'T056', 'LO003', 10, 7000),
('HD00061', 'T057', 'LO004', 4, 12000),

-- HD00062: ~130k
('HD00062', 'T001', 'LO001', 15, 5000),
('HD00062', 'T002', 'LO001', 8, 7500);

GO

-- ===============================================
-- CHI TIẾT HÓA ĐƠN CHO THÁNG 10/2024
-- ===============================================

INSERT INTO ChiTietHoaDon (maHoaDon, maThuoc, maLo, soLuong, donGia) VALUES
-- HD00063: ~450k
('HD00063', 'T031', 'LO016', 18, 8500),
('HD00063', 'T076', 'LO013', 10, 12500),
('HD00063', 'T078', 'LO014', 8, 11000),
('HD00063', 'T041', 'LO021', 6, 15000),

-- HD00064: ~350k
('HD00064', 'T046', 'LO023', 15, 11000),
('HD00064', 'T048', 'LO024', 6, 16000),
('HD00064', 'T095', 'LO023', 5, 16000),

-- HD00065: ~280k
('HD00065', 'T055', 'LO003', 20, 6500),
('HD00065', 'T056', 'LO003', 12, 7000),
('HD00065', 'T026', 'LO013', 10, 7500),

-- HD00066: ~520k
('HD00066', 'T052', 'LO001', 3, 85000),
('HD00066', 'T053', 'LO002', 1, 145000),
('HD00066', 'T057', 'LO004', 10, 12000),

-- HD00067: ~190k
('HD00067', 'T064', 'LO007', 12, 9500),
('HD00067', 'T065', 'LO008', 10, 6000),
('HD00067', 'T021', 'LO011', 6, 8000),

-- HD00068: ~240k
('HD00068', 'T026', 'LO013', 15, 7500),
('HD00068', 'T027', 'LO014', 10, 8000),
('HD00068', 'T067', 'LO009', 8, 7500),

-- HD00069: ~380k
('HD00069', 'T011', 'LO006', 12, 14000),
('HD00069', 'T016', 'LO008', 20, 7000),
('HD00069', 'T018', 'LO009', 8, 11000),

-- HD00070: ~220k
('HD00070', 'T021', 'LO011', 12, 8000),
('HD00070', 'T023', 'LO012', 4, 15000),
('HD00070', 'T024', 'LO012', 5, 13500),

-- HD00071: ~310k
('HD00071', 'T046', 'LO023', 12, 11000),
('HD00071', 'T047', 'LO024', 15, 8500),
('HD00071', 'T003', 'LO002', 8, 8000),

-- HD00072: ~180k
('HD00072', 'T064', 'LO007', 10, 9500),
('HD00072', 'T065', 'LO008', 8, 6000),
('HD00072', 'T066', 'LO008', 4, 14000),

-- HD00073: ~270k
('HD00073', 'T021', 'LO011', 15, 8000),
('HD00073', 'T024', 'LO012', 8, 13500),
('HD00073', 'T025', 'LO013', 6, 9500),

-- HD00074: ~620k
('HD00074', 'T052', 'LO001', 4, 85000),
('HD00074', 'T053', 'LO002', 1, 145000),
('HD00074', 'T057', 'LO004', 8, 12000);

GO

-- ===============================================
-- CHI TIẾT HÓA ĐƠN CHO THÁNG 11/2024
-- ===============================================
INSERT INTO ChiTietHoaDon (maHoaDon, maThuoc, maLo, soLuong, donGia) VALUES
-- HD00075: ~480k
('HD00075', 'T001', 'LO001', 20, 5000),
('HD00075', 'T006', 'LO003', 10, 15000),
('HD00075', 'T055', 'LO003', 15, 6500),
('HD00075', 'T021', 'LO011', 12, 8000),

-- HD00076: ~720k
('HD00076', 'T031', 'LO016', 20, 8500),
('HD00076', 'T036', 'LO018', 25, 7000),
('HD00076', 'T041', 'LO021', 15, 15000),
('HD00076', 'T042', 'LO021', 8, 19000),

-- HD00077: ~560k
('HD00077', 'T031', 'LO016', 18, 8500),
('HD00077', 'T032', 'LO016', 12, 10500),
('HD00077', 'T033', 'LO017', 15, 12000),
('HD00077', 'T034', 'LO017', 10, 9500),

-- HD00078: ~850k
('HD00078', 'T007', 'LO004', 15, 25000),
('HD00078', 'T008', 'LO004', 10, 22000),
('HD00078', 'T009', 'LO005', 15, 18000),

-- HD00079: ~640k
('HD00079', 'T012', 'LO006', 10, 32000),
('HD00079', 'T013', 'LO007', 8, 28000),
('HD00079', 'T101', 'LO001', 8, 16500),

-- HD00080: ~420k
('HD00080', 'T023', 'LO012', 12, 15000),
('HD00080', 'T024', 'LO012', 10, 13500),
('HD00080', 'T022', 'LO011', 8, 12000),
('HD00080', 'T025', 'LO013', 6, 9500),

-- HD00081: ~520k
('HD00081', 'T041', 'LO021', 12, 15000),
('HD00081', 'T043', 'LO022', 8, 22000),
('HD00081', 'T044', 'LO022', 10, 14000),

-- HD00082: ~680k
('HD00082', 'T036', 'LO018', 30, 7000),
('HD00082', 'T037', 'LO019', 20, 8500),
('HD00082', 'T093', 'LO022', 15, 15000),

-- HD00083: ~450k
('HD00083', 'T046', 'LO023', 18, 11000),
('HD00083', 'T047', 'LO024', 15, 8500),
('HD00083', 'T048', 'LO024', 8, 16000),

-- HD00084: ~380k
('HD00084', 'T055', 'LO003', 20, 6500),
('HD00084', 'T056', 'LO003', 18, 7000),
('HD00084', 'T057', 'LO004', 8, 12000),

-- HD00085: ~340k
('HD00085', 'T097', 'LO024', 20, 8500),
('HD00085', 'T098', 'LO024', 15, 7000),
('HD00085', 'T099', 'LO025', 8, 12500),

-- HD00086: ~290k
('HD00086', 'T071', 'LO011', 18, 6500),
('HD00086', 'T072', 'LO011', 10, 9500),
('HD00086', 'T073', 'LO012', 5, 11000),

-- HD00087: ~520k
('HD00087', 'T021', 'LO011', 25, 8000),
('HD00087', 'T023', 'LO012', 10, 15000),
('HD00087', 'T041', 'LO021', 8, 15000),
('HD00087', 'T031', 'LO016', 12, 8500);

GO

-- ===============================================
-- CHI TIẾT HÓA ĐƠN CHO THÁNG 12/2024
-- ===============================================
INSERT INTO ChiTietHoaDon (maHoaDon, maThuoc, maLo, soLuong, donGia) VALUES
-- HD00088: ~380k
('HD00088', 'T001', 'LO001', 25, 5000),
('HD00088', 'T021', 'LO011', 15, 8000),
('HD00088', 'T055', 'LO003', 12, 6500),

-- HD00089: ~420k
('HD00089', 'T021', 'LO011', 20, 8000),
('HD00089', 'T022', 'LO011', 10, 12000),
('HD00089', 'T023', 'LO012', 8, 15000),
('HD00089', 'T024', 'LO012', 6, 13500),

-- HD00090: ~340k
('HD00090', 'T055', 'LO003', 20, 6500),
('HD00090', 'T056', 'LO003', 15, 7000),
('HD00090', 'T026', 'LO013', 12, 7500),
('HD00090', 'T027', 'LO014', 8, 8000),

-- HD00091: ~290k
('HD00091', 'T057', 'LO004', 12, 12000),
('HD00091', 'T058', 'LO004', 10, 9500),
('HD00091', 'T026', 'LO013', 8, 7500),

-- HD00092: ~360k
('HD00092', 'T055', 'LO003', 18, 6500),
('HD00092', 'T056', 'LO003', 20, 7000),
('HD00092', 'T057', 'LO004', 8, 12000),

-- HD00093: ~250k
('HD00093', 'T001', 'LO001', 20, 5000),
('HD00093', 'T002', 'LO001', 15, 7500),
('HD00093', 'T021', 'LO011', 5, 8000),

-- HD00094: ~450k
('HD00094', 'T011', 'LO006', 15, 14000),
('HD00094', 'T016', 'LO008', 20, 7000),
('HD00094', 'T017', 'LO009', 8, 12500),

-- HD00095: ~320k
('HD00095', 'T003', 'LO002', 15, 8000),
('HD00095', 'T046', 'LO023', 12, 11000),
('HD00095', 'T047', 'LO024', 8, 8500),

-- HD00096: ~480k
('HD00096', 'T021', 'LO011', 20, 8000),
('HD00096', 'T023', 'LO012', 10, 15000),
('HD00096', 'T024', 'LO012', 12, 13500),

-- HD00097: ~370k
('HD00097', 'T026', 'LO013', 20, 7500),
('HD00097', 'T027', 'LO014', 15, 8000),
('HD00097', 'T029', 'LO015', 6, 14000),

-- HD00098: ~720k
('HD00098', 'T031', 'LO016', 25, 8500),
('HD00098', 'T036', 'LO018', 30, 7000),
('HD00098', 'T041', 'LO021', 15, 15000),

-- HD00099: ~680k
('HD00099', 'T036', 'LO018', 35, 7000),
('HD00099', 'T037', 'LO019', 20, 8500),
('HD00099', 'T041', 'LO021', 15, 15000);


GO

-- ===============================================
-- CHI TIẾT HÓA ĐƠN CHO THÁNG 1-4/2025
-- ===============================================
INSERT INTO ChiTietHoaDon (maHoaDon, maThuoc, maLo, soLuong, donGia) VALUES
-- THÁNG 1/2025
-- HD00101: ~280k
('HD00101', 'T001', 'LO001', 15, 5000),
('HD00101', 'T021', 'LO011', 10, 8000),
('HD00101', 'T055', 'LO003', 15, 6500),

-- HD00102: ~350k
('HD00102', 'T056', 'LO003', 20, 7000),
('HD00102', 'T057', 'LO004', 12, 12000),
('HD00102', 'T026', 'LO013', 10, 7500),

-- HD00103: ~420k
('HD00103', 'T031', 'LO016', 20, 8500),
('HD00103', 'T036', 'LO018', 15, 7000),
('HD00103', 'T041', 'LO021', 10, 15000),

-- HD00104: ~380k
('HD00104', 'T011', 'LO006', 12, 14000),
('HD00104', 'T016', 'LO008', 18, 7000),
('HD00104', 'T021', 'LO011', 10, 8000),

-- HD00105: ~320k
('HD00105', 'T046', 'LO023', 15, 11000),
('HD00105', 'T047', 'LO024', 12, 8500),
('HD00105', 'T003', 'LO002', 8, 8000),

-- HD00106: ~290k
('HD00106', 'T023', 'LO012', 8, 15000),
('HD00106', 'T024', 'LO012', 6, 13500),
('HD00106', 'T025', 'LO013', 8, 9500),

-- HD00107: ~250k
('HD00107', 'T055', 'LO003', 18, 6500),
('HD00107', 'T056', 'LO003', 12, 7000),
('HD00107', 'T026', 'LO013', 8, 7500),

-- HD00108: ~310k
('HD00108', 'T064', 'LO007', 15, 9500),
('HD00108', 'T065', 'LO008', 12, 6000),
('HD00108', 'T066', 'LO008', 8, 14000),

-- HD00109: ~270k
('HD00109', 'T021', 'LO011', 15, 8000),
('HD00109', 'T022', 'LO011', 8, 12000),
('HD00109', 'T027', 'LO014', 6, 8000),

-- HD00110: ~340k
('HD00110', 'T001', 'LO001', 20, 5000),
('HD00110', 'T006', 'LO003', 8, 15000),
('HD00110', 'T021', 'LO011', 12, 8000),

-- HD00111: ~380k
('HD00111', 'T031', 'LO016', 18, 8500),
('HD00111', 'T032', 'LO016', 10, 10500),
('HD00111', 'T026', 'LO013', 10, 7500),

-- HD00112: ~420k
('HD00112', 'T041', 'LO021', 12, 15000),
('HD00112', 'T042', 'LO021', 8, 19000),
('HD00112', 'T021', 'LO011', 8, 8000),

-- THÁNG 2/2025
-- HD00113: ~290k
('HD00113', 'T055', 'LO003', 20, 6500),
('HD00113', 'T056', 'LO003', 15, 7000),
('HD00113', 'T057', 'LO004', 6, 12000),

-- HD00114: ~330k
('HD00114', 'T026', 'LO013', 18, 7500),
('HD00114', 'T027', 'LO014', 12, 8000),
('HD00114', 'T029', 'LO015', 5, 14000),

-- HD00115: ~280k
('HD00115', 'T001', 'LO001', 18, 5000),
('HD00115', 'T002', 'LO001', 12, 7500),
('HD00115', 'T021', 'LO011', 8, 8000),

-- HD00116: ~350k
('HD00116', 'T011', 'LO006', 10, 14000),
('HD00116', 'T016', 'LO008', 15, 7000),
('HD00116', 'T018', 'LO009', 8, 11000),

-- HD00117: ~310k
('HD00117', 'T046', 'LO023', 12, 11000),
('HD00117', 'T047', 'LO024', 15, 8500),
('HD00117', 'T003', 'LO002', 6, 8000),

-- HD00118: ~380k
('HD00118', 'T031', 'LO016', 20, 8500),
('HD00118', 'T033', 'LO017', 10, 12000),
('HD00118', 'T035', 'LO018', 6, 11000),

-- HD00119: ~420k
('HD00119', 'T012', 'LO006', 6, 32000),
('HD00119', 'T013', 'LO007', 5, 28000),
('HD00119', 'T021', 'LO011', 10, 8000),

-- HD00120: ~270k
('HD00120', 'T064', 'LO007', 12, 9500),
('HD00120', 'T065', 'LO008', 10, 6000),
('HD00120', 'T067', 'LO009', 8, 7500),

-- HD00121: ~340k
('HD00121', 'T036', 'LO018', 20, 7000),
('HD00121', 'T037', 'LO019', 12, 8500),
('HD00121', 'T021', 'LO011', 10, 8000),

-- HD00122: ~290k
('HD00122', 'T055', 'LO003', 18, 6500),
('HD00122', 'T056', 'LO003', 16, 7000),
('HD00122', 'T026', 'LO013', 8, 7500),

-- HD00123: ~310k
('HD00123', 'T021', 'LO011', 18, 8000),
('HD00123', 'T023', 'LO012', 6, 15000),
('HD00123', 'T024', 'LO012', 4, 13500),

-- HD00124: ~380k
('HD00124', 'T041', 'LO021', 10, 15000),
('HD00124', 'T043', 'LO022', 6, 22000),
('HD00124', 'T021', 'LO011', 10, 8000),

-- THÁNG 3/2025
-- HD00125: ~420k
('HD00125', 'T031', 'LO016', 20, 8500),
('HD00125', 'T036', 'LO018', 18, 7000),
('HD00125', 'T041', 'LO021', 8, 15000),

-- HD00126: ~350k
('HD00126', 'T011', 'LO006', 12, 14000),
('HD00126', 'T016', 'LO008', 18, 7000),
('HD00126', 'T017', 'LO009', 6, 12500),

-- HD00127: ~290k
('HD00127', 'T055', 'LO003', 20, 6500),
('HD00127', 'T056', 'LO003', 18, 7000),
('HD00127', 'T021', 'LO011', 8, 8000),

-- HD00128: ~380k
('HD00128', 'T031', 'LO016', 18, 8500),
('HD00128', 'T032', 'LO016', 12, 10500),
('HD00128', 'T034', 'LO017', 8, 9500),

-- HD00129: ~450k
('HD00129', 'T007', 'LO004', 8, 25000),
('HD00129', 'T008', 'LO004', 6, 22000),
('HD00129', 'T041', 'LO021', 8, 15000),

-- HD00130: ~320k
('HD00130', 'T046', 'LO023', 15, 11000),
('HD00130', 'T047', 'LO024', 12, 8500),
('HD00130', 'T048', 'LO024', 4, 16000),

-- HD00131: ~280k
('HD00131', 'T055', 'LO003', 18, 6500),
('HD00131', 'T056', 'LO003', 15, 7000),
('HD00131', 'T026', 'LO013', 10, 7500),

-- HD00132: ~340k
('HD00132', 'T021', 'LO011', 20, 8000),
('HD00132', 'T022', 'LO011', 10, 12000),
('HD00132', 'T023', 'LO012', 4, 15000),

-- HD00133: ~310k
('HD00133', 'T026', 'LO013', 18, 7500),
('HD00133', 'T027', 'LO014', 12, 8000),
('HD00133', 'T029', 'LO015', 4, 14000),

-- HD00134: ~270k
('HD00134', 'T001', 'LO001', 20, 5000),
('HD00134', 'T002', 'LO001', 15, 7500),
('HD00134', 'T003', 'LO002', 6, 8000),

-- HD00135: ~380k
('HD00135', 'T036', 'LO018', 22, 7000),
('HD00135', 'T037', 'LO019', 14, 8500),
('HD00135', 'T021', 'LO011', 10, 8000),

-- HD00136: ~420k
('HD00136', 'T041', 'LO021', 12, 15000),
('HD00136', 'T042', 'LO021', 8, 19000),
('HD00136', 'T044', 'LO022', 6, 14000),

-- THÁNG 4/2025
-- HD00137: ~350k
('HD00137', 'T001', 'LO001', 20, 5000),
('HD00137', 'T006', 'LO003', 10, 15000),
('HD00137', 'T021', 'LO011', 10, 8000),

-- HD00138: ~290k
('HD00138', 'T055', 'LO003', 20, 6500),
('HD00138', 'T056', 'LO003', 18, 7000),
('HD00138', 'T026', 'LO013', 8, 7500),

-- HD00139: ~420k
('HD00139', 'T031', 'LO016', 20, 8500),
('HD00139', 'T036', 'LO018', 20, 7000),
('HD00139', 'T041', 'LO021', 10, 15000),

-- HD00140: ~380k
('HD00140', 'T011', 'LO006', 12, 14000),
('HD00140', 'T016', 'LO008', 20, 7000),
('HD00140', 'T018', 'LO009', 8, 11000),

-- HD00141: ~310k
('HD00141', 'T046', 'LO023', 14, 11000),
('HD00141', 'T047', 'LO024', 12, 8500),
('HD00141', 'T003', 'LO002', 8, 8000),

-- HD00142: ~340k
('HD00142', 'T021', 'LO011', 20, 8000),
('HD00142', 'T023', 'LO012', 6, 15000),
('HD00142', 'T024', 'LO012', 5, 13500),

-- HD00143: ~380k
('HD00143', 'T031', 'LO016', 18, 8500),
('HD00143', 'T032', 'LO016', 12, 10500),
('HD00143', 'T026', 'LO013', 10, 7500),

-- HD00144: ~290k
('HD00144', 'T055', 'LO003', 20, 6500),
('HD00144', 'T056', 'LO003', 16, 7000),
('HD00144', 'T057', 'LO004', 6, 12000),

-- HD00145: ~420k
('HD00145', 'T036', 'LO018', 25, 7000),
('HD00145', 'T037', 'LO019', 15, 8500),
('HD00145', 'T041', 'LO021', 8, 15000),

-- HD00146: ~350k
('HD00146', 'T011', 'LO006', 10, 14000),
('HD00146', 'T013', 'LO007', 4, 28000),
('HD00146', 'T021', 'LO011', 10, 8000),

-- HD00147: ~320k
('HD00147', 'T046', 'LO023', 15, 11000),
('HD00147', 'T047', 'LO024', 10, 8500),
('HD00147', 'T048', 'LO024', 5, 16000),

-- HD00148: ~380k
('HD00148', 'T041', 'LO021', 10, 15000),
('HD00148', 'T043', 'LO022', 6, 22000),
('HD00148', 'T044', 'LO022', 8, 14000);

--=====================================================================
-- DỮ LIỆU CHI TIẾT HÓA ĐƠN CHO THÁNG 5-8 NĂM 2025 (HD00149-HD00188)
--=====================================================================
INSERT INTO ChiTietHoaDon (maHoaDon, maThuoc, maLo, soLuong, donGia) VALUES
-- THÁNG 5/2025
-- HD00149: ~420k
('HD00149', 'T013', 'LO007', 6, 28000),
('HD00149', 'T041', 'LO021', 10, 15000),
('HD00149', 'T007', 'LO004', 5, 25000),

-- HD00150: ~380k
('HD00150', 'T043', 'LO022', 8, 22000),
('HD00150', 'T012', 'LO006', 5, 32000), 
('HD00150', 'T021', 'LO011', 10, 8000),

-- HD00151: ~360k
('HD00151', 'T036', 'LO018', 20, 7000),
('HD00151', 'T041', 'LO021', 8, 15000),
('HD00151', 'T046', 'LO023', 10, 11000),

-- HD00152: ~390k
('HD00152', 'T007', 'LO004', 6, 25000),
('HD00152', 'T008', 'LO004', 5, 22000),
('HD00152', 'T041', 'LO021', 6, 15000),

-- HD00153: ~370k
('HD00153', 'T012', 'LO006', 5, 32000),
('HD00153', 'T013', 'LO007', 4, 28000),
('HD00153', 'T046', 'LO023', 8, 11000),

-- HD00154: ~400k
('HD00154', 'T023', 'LO012', 10, 15000),
('HD00154', 'T043', 'LO022', 6, 22000),
('HD00154', 'T044', 'LO022', 8, 14000),

-- HD00155: ~380k
('HD00155', 'T041', 'LO021', 10, 15000),
('HD00155', 'T042', 'LO021', 6, 19000),
('HD00155', 'T036', 'LO018', 12, 7000),

-- HD00156: ~350k
('HD00156', 'T031', 'LO016', 15, 8500),
('HD00156', 'T033', 'LO017', 8, 12000),
('HD00156', 'T041', 'LO021', 6, 15000),

-- HD00157: ~390k
('HD00157', 'T007', 'LO004', 6, 25000),
('HD00157', 'T043', 'LO022', 5, 22000),
('HD00157', 'T046', 'LO023', 10, 11000),

-- HD00158: ~370k
('HD00158', 'T012', 'LO006', 5, 32000),
('HD00158', 'T023', 'LO012', 8, 15000),
('HD00158', 'T031', 'LO016', 10, 8500),

-- THÁNG 6/2025
-- HD00159: ~450k
('HD00159', 'T013', 'LO007', 7, 28000),
('HD00159', 'T043', 'LO022', 6, 22000),
('HD00159', 'T041', 'LO021', 8, 15000),
('HD00159', 'T046', 'LO023', 5, 11000),

-- HD00160: ~430k
('HD00160', 'T007', 'LO004', 7, 25000),
('HD00160', 'T012', 'LO006', 6, 32000),
('HD00160', 'T041', 'LO021', 6, 15000),
('HD00160', 'T021', 'LO011', 8, 8000),

-- HD00161: ~410k
('HD00161', 'T008', 'LO004', 8, 22000),
('HD00161', 'T023', 'LO012', 8, 15000),
('HD00161', 'T041', 'LO021', 6, 15000),
('HD00161', 'T031', 'LO016', 10, 8500),

-- HD00162: ~440k
('HD00162', 'T013', 'LO007', 6, 28000),
('HD00162', 'T043', 'LO022', 7, 22000),
('HD00162', 'T036', 'LO018', 10, 7000),
('HD00162', 'T046', 'LO023', 8, 11000),

-- HD00163: ~420k
('HD00163', 'T012', 'LO006', 6, 32000),
('HD00163', 'T041', 'LO021', 10, 15000),
('HD00163', 'T023', 'LO012', 5, 15000),
('HD00163', 'T031', 'LO016', 8, 8500),

-- HD00164: ~400k
('HD00164', 'T043', 'LO022', 8, 22000),
('HD00164', 'T044', 'LO022', 10, 14000),
('HD00164', 'T041', 'LO021', 6, 15000),

-- HD00165: ~430k
('HD00165', 'T007', 'LO004', 7, 25000),
('HD00165', 'T012', 'LO006', 6, 32000),
('HD00165', 'T041', 'LO021', 6, 15000),
('HD00165', 'T046', 'LO023', 6, 11000),

-- HD00166: ~410k
('HD00166', 'T013', 'LO007', 6, 28000),
('HD00166', 'T023', 'LO012', 8, 15000),
('HD00166', 'T043', 'LO022', 5, 22000),
('HD00166', 'T031', 'LO016', 10, 8500),

-- HD00167: ~440k
('HD00167', 'T012', 'LO006', 6, 32000),
('HD00167', 'T041', 'LO021', 12, 15000),
('HD00167', 'T044', 'LO022', 8, 14000),

-- HD00168: ~420k
('HD00168', 'T043', 'LO022', 9, 22000),
('HD00168', 'T013', 'LO007', 5, 28000),
('HD00168', 'T046', 'LO023', 8, 11000),

-- THÁNG 7/2025
-- HD00169: ~360k
('HD00169', 'T012', 'LO006', 8, 32000),
('HD00169', 'T041', 'LO021', 5, 15000),
('HD00169', 'T021', 'LO011', 10, 8000),

-- HD00170: ~350k
('HD00170', 'T044', 'LO022', 10, 14000),
('HD00170', 'T023', 'LO012', 8, 15000),
('HD00170', 'T031', 'LO016', 8, 8500),

-- HD00171: ~340k
('HD00171', 'T011', 'LO006', 10, 14000),
('HD00171', 'T041', 'LO021', 8, 15000),
('HD00171', 'T036', 'LO018', 8, 7000),

-- HD00172: ~370k
('HD00172', 'T043', 'LO022', 7, 22000),
('HD00172', 'T012', 'LO006', 6, 32000),
('HD00172', 'T046', 'LO023', 6, 11000),

-- HD00173: ~350k
('HD00173', 'T024', 'LO012', 10, 13500),
('HD00173', 'T044', 'LO022', 8, 14000),
('HD00173', 'T047', 'LO024', 8, 8500),

-- HD00174: ~330k
('HD00174', 'T023', 'LO012', 8, 15000),
('HD00174', 'T041', 'LO021', 6, 15000),
('HD00174', 'T021', 'LO011', 10, 8000),

-- HD00175: ~360k
('HD00175', 'T011', 'LO006', 10, 14000),
('HD00175', 'T012', 'LO006', 8, 32000),
('HD00175', 'T031', 'LO016', 6, 8500),

-- HD00176: ~350k
('HD00176', 'T044', 'LO022', 10, 14000),
('HD00176', 'T041', 'LO021', 8, 15000),
('HD00176', 'T036', 'LO018', 8, 7000),

-- HD00177: ~340k
('HD00177', 'T023', 'LO012', 8, 15000),
('HD00177', 'T012', 'LO006', 6, 32000),
('HD00177', 'T046', 'LO023', 6, 11000),

-- HD00178: ~360k
('HD00178', 'T043', 'LO022', 6, 22000),
('HD00178', 'T011', 'LO006', 8, 14000),
('HD00178', 'T031', 'LO016', 8, 8500),

-- THÁNG 8/2025
-- HD00179: ~290k
('HD00179', 'T012', 'LO006', 6, 32000),
('HD00179', 'T041', 'LO021', 5, 15000),
('HD00179', 'T021', 'LO011', 8, 8000),

-- HD00180: ~280k
('HD00180', 'T044', 'LO022', 8, 14000),
('HD00180', 'T023', 'LO012', 6, 15000),
('HD00180', 'T031', 'LO016', 6, 8500),

-- HD00181: ~270k
('HD00181', 'T011', 'LO006', 8, 14000),
('HD00181', 'T041', 'LO021', 6, 15000),
('HD00181', 'T036', 'LO018', 6, 7000),

-- HD00182: ~300k
('HD00182', 'T043', 'LO022', 6, 22000),
('HD00182', 'T012', 'LO006', 5, 32000),
('HD00182', 'T046', 'LO023', 5, 11000),

-- HD00183: ~280k
('HD00183', 'T024', 'LO012', 8, 13500),
('HD00183', 'T044', 'LO022', 6, 14000),
('HD00183', 'T047', 'LO024', 6, 8500),

-- HD00184: ~260k
('HD00184', 'T023', 'LO012', 6, 15000),
('HD00184', 'T041', 'LO021', 5, 15000),
('HD00184', 'T021', 'LO011', 6, 8000),

-- HD00185: ~290k
('HD00185', 'T011', 'LO006', 8, 14000),
('HD00185', 'T012', 'LO006', 5, 32000),
('HD00185', 'T031', 'LO016', 6, 8500),

-- HD00186: ~280k
('HD00186', 'T044', 'LO022', 8, 14000),
('HD00186', 'T041', 'LO021', 6, 15000),
('HD00186', 'T036', 'LO018', 6, 7000),

-- HD00187: ~270k
('HD00187', 'T023', 'LO012', 6, 15000),
('HD00187', 'T012', 'LO006', 5, 32000),
('HD00187', 'T046', 'LO023', 5, 11000),

-- HD00188: ~290k
('HD00188', 'T043', 'LO022', 5, 22000),
('HD00188', 'T011', 'LO006', 6, 14000),
('HD00188', 'T031', 'LO016', 6, 8500);


-- ===============================================
-- DỮ LIỆU PHIẾU ĐẶT (ĐÃ SỬA: isReceived = 1)
-- ===============================================
INSERT INTO PhieuDat (maPhieuDat, maNV, ngayDat, maKH, ghiChu, isReceived) VALUES
('PD001', 'NV004', '2025-09-01', 'KH001', N'Khách hẹn lấy sau 5h chiều', 1),
('PD002', 'NV005', '2025-09-02', 'KH004', N'Gọi điện trước khi giao', 1),
('PD003', 'NV009', '2025-09-03', 'KH007', N'Khách quen, giảm giá nếu có thể', 1),
('PD004', 'NV010', '2025-09-05', 'KH012', NULL, 1),
('PD005', 'NV004', '2025-09-06', 'KH015', N'Đơn thuốc bác sĩ A', 1),
('PD006', 'NV005', '2025-09-08', 'KH019', N'Lấy thuốc cho mẹ', 1),
('PD007', 'NV009', '2025-09-10', 'KH024', NULL, 1),
('PD008', 'NV010', '2025-09-11', 'KH028', N'Giao hàng nhanh', 1),
('PD009', 'NV004', '2025-09-13', 'KH002', NULL, 1),
('PD010', 'NV005', '2025-09-14', 'KH006', N'Kiểm tra lại thuốc dạ dày', 1),
('PD011', 'NV009', '2025-09-16', 'KH010', N'Đơn đặt hàng tháng', 1),
('PD012', 'NV010', '2025-09-17', 'KH014', NULL, 1),
('PD013', 'NV004', '2025-09-19', 'KH018', N'Khách hàng cần tư vấn thêm', 1),
('PD014', 'NV005', '2025-09-20', 'KH022', NULL, 1),
('PD015', 'NV009', '2025-09-22', 'KH026', N'Giao cho người nhà tên B', 1),
('PD016', 'NV010', '2025-09-23', 'KH030', NULL, 1),
('PD017', 'NV004', '2025-09-25', 'KH034', N'Thuốc tiểu đường, kiểm tra kỹ', 1),
('PD018', 'NV005', '2025-09-26', 'KH003', N'Đơn thuốc hen', 1),
('PD019', 'NV009', '2025-09-28', 'KH009', NULL, 1),
('PD020', 'NV010', '2025-09-29', 'KH016', N'Khách hàng VIP', 1);
GO

-- Insert dữ liệu vào bảng ChiTietPhieuDat
INSERT INTO ChiTietPhieuDat (maPhieuDat, maThuoc, maLo, tenThuoc, soLuong) VALUES
('PD001', 'T001', 'LO001', N'Paracetamol 500mg', 5),
('PD001', 'T021', 'LO011', N'Vitamin C 500mg', 3),
('PD001', 'T026', 'LO013', N'Loratadine 10mg', 2),
('PD002', 'T006', 'LO003', N'Amoxicillin 500mg', 4),
('PD002', 'T011', 'LO006', N'Omeprazole 20mg', 3),
('PD002', 'T014', 'LO007', N'Maalox', 2),
('PD003', 'T016', 'LO008', N'Smecta', 6),
('PD003', 'T017', 'LO009', N'Imodium 2mg', 2),
('PD003', 'T019', 'LO010', N'Lacteol Fort', 3),
('PD004', 'T031', 'LO016', N'Amlodipine 5mg', 3),
('PD004', 'T033', 'LO017', N'Bisoprolol 2.5mg', 2),
('PD004', 'T041', 'LO021', N'Atorvastatin 10mg', 2),
('PD005', 'T036', 'LO018', N'Metformin 500mg', 5),
('PD005', 'T038', 'LO019', N'Glibenclamide 5mg', 3),
('PD005', 'T024', 'LO012', N'Canxi D3', 2),
('PD006', 'T027', 'LO014', N'Cetirizine 10mg', 4),
('PD006', 'T029', 'LO015', N'Claritine', 2),
('PD006', 'T022', 'LO011', N'Vitamin E 400UI', 3),
('PD007', 'T007', 'LO004', N'Augmentin 625mg', 3),
('PD007', 'T009', 'LO005', N'Azithromycin 250mg', 2),
('PD007', 'T012', 'LO006', N'Nexium 40mg', 2),
('PD008', 'T055', 'LO003', N'Bromhexine 8mg', 4),
('PD008', 'T056', 'LO003', N'Ambroxol 30mg', 3),
('PD008', 'T051', 'LO001', N'Salbutamol 2mg', 2),
('PD008', 'T052', 'LO001', N'Ventolin inhaler', 1),
('PD009', 'T003', 'LO002', N'Ibuprofen 400mg', 3),
('PD009', 'T046', 'LO023', N'Meloxicam 7.5mg', 2),
('PD009', 'T047', 'LO024', N'Diclofenac 50mg', 3),
('PD010', 'T012', 'LO006', N'Nexium 40mg', 2),
('PD010', 'T013', 'LO007', N'Pantoprazole 40mg', 2),
('PD010', 'T101', 'LO001', N'Esomeprazole 20mg', 3),
('PD011', 'T023', 'LO012', N'Multivitamin', 5),
('PD011', 'T024', 'LO012', N'Canxi D3', 4),
('PD011', 'T025', 'LO013', N'Zinc 20mg', 3),
('PD012', 'T042', 'LO021', N'Atorvastatin 20mg', 3),
('PD012', 'T043', 'LO022', N'Rosuvastatin 10mg', 2),
('PD012', 'T044', 'LO022', N'Simvastatin 20mg', 2),
('PD013', 'T059', 'LO005', N'Fluconazole 150mg', 2),
('PD013', 'T062', 'LO006', N'Albendazole 400mg', 4),
('PD013', 'T063', 'LO007', N'Mebendazole 500mg', 3),
('PD014', 'T001', 'LO001', N'Paracetamol 500mg', 8),
('PD014', 'T002', 'LO001', N'Efferalgan 500mg', 5),
('PD014', 'T004', 'LO002', N'Aspirin 100mg', 3),
('PD015', 'T071', 'LO011', N'Furosemide 40mg', 3),
('PD015', 'T072', 'LO011', N'Spironolactone 25mg', 2),
('PD015', 'T080', 'LO015', N'Atenolol 50mg', 2),
('PD016', 'T064', 'LO007', N'Domperidone 10mg', 4),
('PD016', 'T065', 'LO008', N'Metoclopramide 10mg', 3),
('PD016', 'T066', 'LO008', N'Ondansetron 8mg', 2),
('PD017', 'T037', 'LO019', N'Metformin 850mg', 4),
('PD017', 'T039', 'LO020', N'Acarbose 50mg', 2),
('PD017', 'T093', 'LO022', N'Gliclazide 30mg', 2),
('PD018', 'T052', 'LO001', N'Ventolin inhaler', 2),
('PD018', 'T053', 'LO002', N'Seretide 250mcg', 1),
('PD018', 'T054', 'LO002', N'Combivent', 1),
('PD019', 'T016', 'LO008', N'Smecta', 8),
('PD019', 'T018', 'LO009', N'Bioflora', 4),
('PD019', 'T020', 'LO010', N'Enterogermina', 3),
('PD020', 'T032', 'LO016', N'Amlodipine 10mg', 3),
('PD020', 'T034', 'LO017', N'Enalapril 5mg', 2),
('PD020', 'T035', 'LO018', N'Losartan 50mg', 2),
('PD020', 'T041', 'LO021', N'Atorvastatin 10mg', 2);
GO

-- Insert dữ liệu vào bảng PhieuNhapThuoc (Không thay đổi)
INSERT INTO PhieuNhapThuoc (maPhieuNhapThuoc, maNV, ngayNhap) VALUES
('PNT001', 'NV006', '2025-08-01'),
('PNT002', 'NV003', '2025-08-05'),
('PNT003', 'NV006', '2025-08-10'),
('PNT004', 'NV008', '2025-08-15'),
('PNT005', 'NV009', '2025-08-20'),
('PNT006', 'NV001', '2025-08-25'),
('PNT007', 'NV002', '2025-09-01'),
('PNT008', 'NV004', '2025-09-05'),
('PNT009', 'NV005', '2025-09-10'),
('PNT010', 'NV007', '2025-09-15'),
('PNT011', 'NV007', '2025-09-18'),
('PNT012', 'NV008', '2025-09-20'),
('PNT013', 'NV009', '2025-09-22'),
('PNT014', 'NV002', '2025-09-24'),
('PNT015', 'NV001', '2025-09-26');
GO

-- Insert dữ liệu vào bảng ChiTietPhieuNhap
INSERT INTO ChiTietPhieuNhap (maPhieuNhapThuoc, maLo, maThuoc, soLuong, donGia) VALUES
('PNT001', 'LO001', 'T001', 500, 3500),
('PNT001', 'LO011', 'T021', 600, 5500),
('PNT002', 'LO003', 'T006', 300, 10000),
('PNT002', 'LO004', 'T007', 250, 17000),
('PNT002', 'LO005', 'T009', 200, 12000),
('PNT003', 'LO006', 'T011', 400, 9000),
('PNT003', 'LO007', 'T013', 300, 6000),
('PNT004', 'LO008', 'T016', 500, 5000),
('PNT004', 'LO009', 'T017', 350, 8000),
('PNT004', 'LO010', 'T020', 400, 6500),
('PNT005', 'LO011', 'T022', 700, 5500),
('PNT005', 'LO012', 'T023', 500, 10000),
('PNT005', 'LO013', 'T025', 450, 6500),
('PNT006', 'LO013', 'T026', 400, 5000),
('PNT006', 'LO014', 'T027', 350, 9000),
('PNT006', 'LO015', 'T029', 300, 12000),
('PNT007', 'LO016', 'T031', 300, 6000),
('PNT007', 'LO017', 'T033', 250, 8000),
('PNT007', 'LO018', 'T035', 280, 7500),
('PNT008', 'LO018', 'T036', 320, 5000),
('PNT008', 'LO019', 'T037', 280, 9500),
('PNT008', 'LO020', 'T040', 220, 20000),
('PNT009', 'LO021', 'T041', 300, 11000),
('PNT009', 'LO022', 'T043', 280, 16000),
('PNT009', 'LO023', 'T045', 250, 18000),
('PNT010', 'LO023', 'T046', 350, 8000),
('PNT010', 'LO024', 'T047', 300, 11000),
('PNT010', 'LO025', 'T049', 280, 13000),
('PNT011', 'LO001', 'T002', 800, 3500),
('PNT011', 'LO002', 'T003', 600, 5500),
('PNT012', 'LO003', 'T055', 400, 10000),
('PNT012', 'LO004', 'T057', 300, 17000),
('PNT013', 'LO006', 'T062', 500, 9000),
('PNT013', 'LO007', 'T064', 400, 6000),
('PNT014', 'LO001', 'T051', 400, 3500),
('PNT014', 'LO002', 'T053', 350, 5500),
('PNT014', 'LO003', 'T056', 300, 10000),
('PNT014', 'LO004', 'T058', 200, 17000),
('PNT015', 'LO005', 'T060', 300, 12000),
('PNT015', 'LO010', 'T069', 350, 6500),
('PNT015', 'LO015', 'T080', 250, 12000),
('PNT015', 'LO020', 'T090', 180, 20000);
GO

-- Insert dữ liệu vào bảng PhieuDoiTra
INSERT INTO PhieuDoiTra (maPhieuDoiTra, ngayDoiTra, maNV, maKH) VALUES
('PDT001', '2025-09-03', 'NV002', 'KH001'),
('PDT002', '2025-09-07', 'NV003', 'KH004'),
('PDT003', '2025-09-09', 'NV008', 'KH007'),
('PDT004', '2025-09-12', 'NV002', 'KH012'),
('PDT005', '2025-09-14', 'NV003', 'KH015'),
('PDT006', '2025-09-16', 'NV008', 'KH002'),
('PDT007', '2025-09-18', 'NV002', 'KH019'),
('PDT008', '2025-09-21', 'NV003', 'KH024'),
('PDT009', '2025-09-23', 'NV008', 'KH006'),
('PDT010', '2025-09-25', 'NV002', 'KH028'),
('PDT011', '2025-09-26', 'NV003', 'KH010'),
('PDT012', '2025-09-27', 'NV008', 'KH014'),
('PDT013', '2025-09-28', 'NV002', 'KH022'),
('PDT014', '2025-09-29', 'NV003', 'KH018'),
('PDT015', '2025-09-30', 'NV008', 'KH026');
GO

-- Insert dữ liệu vào bảng ChiTietPhieuDoiTra
INSERT INTO ChiTietPhieuDoiTra (maPhieuDoiTra, maThuoc, soLuong, donGia, maLo, lyDo) VALUES
('PDT001', 'T001', 2, 5000, 'LO001', N'Thuốc sắp hết hạn sử dụng'),
('PDT001', 'T021', 1, 8000, 'LO011', N'Thuốc sắp hết hạn sử dụng'),
('PDT002', 'T006', 1, 15000, 'LO003', N'Khách hàng bị dị ứng với thuốc'),
('PDT003', 'T016', 2, 7000, 'LO008', N'Thuốc không có hiệu quả như mong đợi'),
('PDT003', 'T017', 1, 12500, 'LO009', N'Bác sĩ đổi đơn thuốc'),
('PDT004', 'T031', 1, 8500, 'LO016', N'Khách hàng mua nhầm liều lượng'),
('PDT004', 'T041', 1, 15000, 'LO021', N'Bác sĩ kê đơn thuốc khác'),
('PDT005', 'T036', 2, 7000, 'LO018', N'Viên thuốc bị móp méo'),
('PDT006', 'T027', 2, 8000, 'LO014', N'Khách hàng bị buồn ngủ sau khi uống'),
('PDT006', 'T029', 1, 14000, 'LO015', N'Đổi sang thuốc chống dị ứng thế hệ mới'),
('PDT007', 'T007', 1, 25000, 'LO004', N'Khách hàng mua thừa, chưa sử dụng'),
('PDT007', 'T012', 1, 32000, 'LO006', N'Mua trùng với đơn thuốc cũ'),
('PDT008', 'T052', 1, 85000, 'LO001', N'Bác sĩ đổi phác đồ điều trị'),
('PDT008', 'T055', 1, 6500, 'LO003', N'Thuốc không phù hợp với triệu chứng'),
('PDT009', 'T003', 1, 8000, 'LO002', N'Khách hàng bị đau dạ dày sau khi uống'),
('PDT009', 'T046', 1, 11000, 'LO023', N'Gây tác dụng phụ không mong muốn'),
('PDT010', 'T056', 2, 7000, 'LO003', N'Phát hiện thuốc còn hạn dùng ngắn'),
('PDT010', 'T058', 1, 9500, 'LO004', N'Thuốc sắp hết hạn sử dụng'),
('PDT011', 'T023', 2, 15000, 'LO012', N'Khách hàng mua nhầm loại vitamin'),
('PDT011', 'T024', 1, 13500, 'LO012', N'Đổi sang sản phẩm phù hợp hơn'),
('PDT012', 'T042', 1, 19000, 'LO021', N'Khách hàng bị đau cơ sau khi dùng'),
('PDT012', 'T043', 1, 22000, 'LO022', N'Không dung nạp với thuốc'),
('PDT013', 'T037', 2, 8500, 'LO019', N'Bác sĩ điều chỉnh liều lượng'),
('PDT013', 'T039', 1, 13000, 'LO020', N'Thay đổi phác đồ điều trị tiểu đường'),
('PDT014', 'T011', 2, 14000, 'LO006', N'Bao bì thuốc bị rách'),
('PDT014', 'T013', 1, 28000, 'LO007', N'Lọ thuốc bị vỡ seal niêm phong'),
('PDT015', 'T026', 3, 7500, 'LO013', N'Khách hàng khỏi bệnh, còn thừa thuốc'),
('PDT015', 'T022', 1, 12000, 'LO011', N'Mua thừa, chưa mở hộp');
GO

-- ===============================================
-- CẬP NHẬT SỐ LƯỢNG TỒN (THỦ CÔNG)
-- ===============================================
UPDATE T
SET T.soLuongTon = ISNULL(Sub.TotalQuantity, 0)
FROM Thuoc T
LEFT JOIN (
    SELECT 
        maThuoc,
        SUM(soLuong) AS TotalQuantity
    FROM ChiTietLoThuoc
    WHERE isActive = 1
    GROUP BY maThuoc
) AS Sub ON T.maThuoc = Sub.maThuoc;
GO

-- ===============================================
-- TRIGGER & PROCEDURES
-- ===============================================

CREATE TRIGGER tr_CapNhatSoLuongTon
ON ChiTietLoThuoc
AFTER INSERT, UPDATE, DELETE
AS
BEGIN
    SET NOCOUNT ON;
    
    DECLARE @AffectedMaThuoc TABLE (maThuoc VARCHAR(50));
    
    INSERT INTO @AffectedMaThuoc (maThuoc)
    SELECT DISTINCT maThuoc FROM INSERTED
    UNION
    SELECT DISTINCT maThuoc FROM DELETED;

    UPDATE Thuoc
    SET soLuongTon = ISNULL((
        SELECT SUM(soLuong)
        FROM ChiTietLoThuoc
        WHERE ChiTietLoThuoc.maThuoc = Thuoc.maThuoc 
          AND ChiTietLoThuoc.isActive = 1
    ), 0)
    WHERE maThuoc IN (SELECT maThuoc FROM @AffectedMaThuoc);
END
GO

CREATE TRIGGER tr_CapNhatDiemTichLuy
ON HoaDon
AFTER INSERT
AS
BEGIN
    SET NOCOUNT ON;
    UPDATE KhachHang
    SET diemTichLuy = ISNULL(diemTichLuy, 0) + ISNULL((
        SELECT FLOOR(SUM(ct.soLuong * ct.donGia) / 1000)
        FROM ChiTietHoaDon ct
        WHERE ct.maHoaDon = i.maHoaDon
          AND ct.isActive = 1
    ), 0)
    FROM KhachHang kh
    INNER JOIN INSERTED i ON kh.maKH = i.maKH
    WHERE i.maKH IS NOT NULL;
END
GO

CREATE TRIGGER tr_DongBoAnNhanVien
ON NhanVien
AFTER UPDATE
AS
BEGIN
    SET NOCOUNT ON;
    UPDATE TaiKhoan
    SET isActive = 0
    FROM TaiKhoan tk
    INNER JOIN INSERTED i ON tk.maNV = i.maNV
    WHERE i.isActive = 0 AND tk.isActive = 1;
    
    UPDATE TaiKhoan
    SET isActive = 1
    FROM TaiKhoan tk
    INNER JOIN INSERTED i ON tk.maNV = i.maNV
    WHERE i.isActive = 1 AND tk.isActive = 0;
END
GO

--================================================--

-- 			PROCEDURE GỌI UPDATE HẠN SỬ DỤNG / CẬP NHẬT TRẠNG THÁI BẢNG GIÁ

--================================================--

-- PROCEDURE: Cập nhật thuốc hết hạn sử dụng
CREATE PROCEDURE sp_CapNhatThuocHetHan
AS
BEGIN
    SET NOCOUNT ON;
    BEGIN TRY
        BEGIN TRANSACTION;
        UPDATE ChiTietLoThuoc
        SET isActive = 0
        WHERE hanSuDung < GETDATE() AND isActive = 1;
        COMMIT TRANSACTION;
    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0 ROLLBACK TRANSACTION;
    END CATCH
END;
GO

-- PROCEDURE: Cập nhật trạng thái bảng giá kết thúc
CREATE PROCEDURE sp_CapNhatTrangThaiBangGia
AS
BEGIN
    SET NOCOUNT ON;
    BEGIN TRY
        BEGIN TRANSACTION;
        UPDATE BangGia
        SET trangThai = N'Đã kết thúc'
        WHERE ngayKetThuc < GETDATE() AND isActive = 1;

		UPDATE BangGia
        SET trangThai = N'Đang áp dụng'
        WHERE ngayApDung <= GETDATE()
          AND ngayKetThuc >= GETDATE() AND isActive = 1;

        COMMIT TRANSACTION;
    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0 ROLLBACK TRANSACTION;
    END CATCH
END;
GO


--================================================--



-- Các Procedure thống kê khác (Giữ nguyên)
CREATE PROCEDURE sp_ThongKeDoanhThuTheoThang
    @Nam INT
AS
BEGIN
    SELECT 
        MONTH(ngayBan) AS Thang,
        SUM(ct.soLuong * ct.donGia) AS DoanhThu
    FROM HoaDon hd
    INNER JOIN ChiTietHoaDon ct ON hd.maHoaDon = ct.maHoaDon
    WHERE YEAR(ngayBan) = @Nam AND hd.isActive = 1 AND ct.isActive = 1
    GROUP BY MONTH(ngayBan)
    ORDER BY Thang;
END
GO

CREATE PROCEDURE sp_ThongKeDoanhThuNhanVien
    @MaNV VARCHAR(50),
    @Nam INT
AS
BEGIN
    DECLARE @Thang TABLE (Thang INT);
    INSERT INTO @Thang VALUES (1),(2),(3),(4),(5),(6),(7),(8),(9),(10),(11),(12);

    SELECT 
        nv.maNV,
        nv.tenNV,
        t.Thang AS 'Tháng',
        ISNULL(COUNT(DISTINCT hd.maHoaDon), 0) AS 'Số hóa đơn',
        ISNULL(SUM(cthd.soLuong * cthd.donGia), 0) AS 'Doanh thu'
    FROM @Thang t
    LEFT JOIN HoaDon hd ON MONTH(hd.ngayBan) = t.Thang 
                        AND YEAR(hd.ngayBan) = @Nam 
                        AND hd.maNV = @MaNV
                        AND hd.isActive = 1
    LEFT JOIN ChiTietHoaDon cthd ON hd.maHoaDon = cthd.maHoaDon AND cthd.isActive = 1
    CROSS JOIN NhanVien nv WHERE nv.maNV = @MaNV
    GROUP BY nv.maNV, nv.tenNV, t.Thang
    ORDER BY t.Thang;
END
GO

CREATE PROCEDURE sp_ThongKeThueTrongNam
    @Nam INT
AS
BEGIN
    SELECT
        MONTH(hd.ngayBan) AS [Thang],
        SUM(hd.giaTriThue * (cthd.soLuong * cthd.donGia)) AS [giaTriThue]
    FROM HoaDon hd
    INNER JOIN ChiTietHoaDon cthd ON hd.maHoaDon = cthd.maHoaDon
    WHERE YEAR(hd.ngayBan) = @Nam AND hd.isActive = 1 AND cthd.isActive = 1
    GROUP BY MONTH(hd.ngayBan)
    ORDER BY [Thang];
END
GO

CREATE PROCEDURE sp_GetSoHoaDonTheoNam
	@Nam int
AS
BEGIN
    SELECT YEAR(ngayBan) AS [Nam], COUNT(maHoaDon) AS [SoHoaDon]
    FROM HoaDon WhERE isActive = 1 and YEAR(ngayBan) = @Nam
    GROUP BY YEAR(ngayBan)
END
GO

CREATE PROCEDURE sp_GetSoHoaDonTheoThang
    @Thang INT, @Nam INT
AS
BEGIN
    SELECT MONTH(ngayBan) AS [Thang], YEAR(ngayBan) AS [Nam], COUNT(maHoaDon) AS [SoHoaDon]
    FROM HoaDon
    WHERE YEAR(ngayBan) = @Nam AND isActive = 1 AND MONTH(ngayBan) = @Thang
    GROUP BY MONTH(ngayBan), YEAR(ngayBan)
    ORDER BY [Thang];
END
GO

CREATE PROCEDURE sp_ThongKeDoanhThuTheoNgay
    @Thang INT, @Nam INT
AS
BEGIN
    SELECT
        DAY(ngayBan) AS [Ngay],
		MONTH(ngayBan) AS [Thang],
        YEAR(ngayBan) AS [Nam],
        SUM(soLuong * donGia) AS [doanhThu]
    FROM HoaDon HD JOIN ChiTietHoaDon CTHD on HD.maHoaDon = CTHD.maHoaDon	
    WHERE YEAR(ngayBan) = @Nam and MONTH(ngayBan) = @Thang AND HD.isActive = 1
    GROUP BY DAY(ngayBan), MONTH(ngayBan), YEAR(ngayBan)
    ORDER BY [Ngay];
END
GO

CREATE PROCEDURE sp_GetDoanhThuCuaNam
    @Nam INT
AS
BEGIN
    SELECT
        @Nam AS [Nam],
        ISNULL(SUM(cthd.soLuong * cthd.donGia), 0) AS [TongDoanhThu]
    FROM HoaDon hd
    INNER JOIN ChiTietHoaDon cthd ON hd.maHoaDon = cthd.maHoaDon
    WHERE YEAR(hd.ngayBan) = @Nam AND hd.isActive = 1 AND cthd.isActive = 1;
END
GO

CREATE PROCEDURE sp_GetDoanhThuCuaThang
    @Thang INT, @Nam INT
AS
BEGIN
    SELECT
        @Thang AS [Thang], @Nam AS [Nam],
        ISNULL(SUM(cthd.soLuong * cthd.donGia), 0) AS [TongDoanhThu]
    FROM HoaDon hd
    INNER JOIN ChiTietHoaDon cthd ON hd.maHoaDon = cthd.maHoaDon
    WHERE YEAR(hd.ngayBan) = @Nam AND MONTH(hd.ngayBan) = @Thang AND hd.isActive = 1 AND cthd.isActive = 1;
END
GO



-- PROCEDURE Lấy về hóa đơn trong tháng x năm y
-- Param: Tháng và Năm cần thống kê
-- Return: data toàn bộ hóa đơn kèm tính sẵn tổng tiền


-- new procedure right over here



--==============================  THUỐC  ==============================--


-- PROCEDURE Lấy danh sách thuốc kèm giá
-- Param: none
-- Return: danh sách thuốc kèm giá ( db cũ )
CREATE PROCEDURE sp_LayDanhSachThuocKemGia
AS
BEGIN
    SET NOCOUNT ON;

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
        JOIN BANGGIA BG 
            ON BG.maBangGia = CTBG.maBangGia
        JOIN Thuoc T
            ON T.maThuoc = CTBG.maThuoc
		JOIN DonVi DV
			ON DV.maDonVi = T.maDonVi
        WHERE BG.trangThai = N'Đang áp dụng'
          AND GETDATE() >= BG.ngayApDung
          AND GETDATE() <= BG.ngayKetThuc
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
END;
GO

-- PROCEDURE Lấy danh sách thuốc kèm giá cũ và giá hiện tại
-- Param: none
-- Return: danh sách thuốc kèm giá chuẩn và giá hiện tại 
CREATE PROCEDURE sp_LayDanhSachThuocKemGiaChuanVaGiaHienTai
AS
BEGIN
    SET NOCOUNT ON;

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
        JOIN BANGGIA BG 
            ON BG.maBangGia = CTBG.maBangGia
        JOIN Thuoc T
            ON T.maThuoc = CTBG.maThuoc
		JOIN DonVi DV
			ON DV.maDonVi = T.maDonVi
        WHERE BG.trangThai = N'Đang áp dụng'
          AND GETDATE() >= BG.ngayApDung
          AND GETDATE() <= BG.ngayKetThuc
    )
    SELECT 
		RP.maThuoc,
		RP.tenThuoc,
		RP.tenDonVi,
		CTBG.donGia as giaChuan,
		RP.donGia as giaHienTai
    FROM RankedPrice RP JOIN ChiTietBangGia CTBG ON RP.maThuoc = CTBG.maThuoc
    WHERE rn = 1 AND CTBG.maBangGia = 'BG001';
END;
GO

-- PROCEDURE Lấy danh sách thuốc kèm giá và kèm tên nhà sản xuất
-- Param: none
-- Return: danh sách thuốc kèm giá ( db cũ )
CREATE PROCEDURE sp_LayDanhSachThuocKemGiaKemTenNhaSanXuat
AS
BEGIN
    SET NOCOUNT ON;

    -- CTE lấy giá ưu tiên cao nhất cho mỗi thuốc (nếu có)
    WITH RankedPrice AS (
        SELECT
            CTBG.maThuoc,
            CTBG.donGia,
            ROW_NUMBER() OVER (
                PARTITION BY CTBG.maThuoc
                ORDER BY 
                    BG.doUuTien DESC,
                    BG. ngayApDung DESC,
                    BG.maBangGia DESC
            ) AS rn
        FROM ChiTietBangGia CTBG
        JOIN BangGia BG 
            ON BG.maBangGia = CTBG.maBangGia
        WHERE BG.trangThai = N'Đang áp dụng'
          AND GETDATE() >= BG.ngayApDung
          AND GETDATE() <= BG.ngayKetThuc
          AND CTBG.isActive = 1
    )
    
    -- Lấy toàn bộ thuốc, LEFT JOIN với giá
    SELECT 
        T. maThuoc,
        T.tenThuoc,
        T.soLuongTon,
        ISNULL(RP.donGia, 0) AS donGia,  -- Nếu không có giá thì = 0
        DV.tenDonVi,
        T.soLuongToiThieu,
        NSX.tenNSX,
        T. isActive
    FROM Thuoc T
    LEFT JOIN DonVi DV
        ON DV. maDonVi = T.maDonVi
    LEFT JOIN NhaSanXuat NSX
        ON NSX.maNSX = T.maNSX
    LEFT JOIN RankedPrice RP
        ON RP.maThuoc = T. maThuoc AND RP.rn = 1
	where T.isActive = 1
    ORDER BY T.maThuoc;
END;
GO



-- PROCEDURE:  Cập nhật chi tiết bảng giá BG001 cho thuốc
-- Param: @maThuoc - Mã thuốc cần cập nhật
--        @giaBase - Giá cơ bản của thuốc
-- Return:  Thông báo kết quả
CREATE PROCEDURE sp_CapNhatGiaThuocBase
    @maThuoc VARCHAR(50),
    @giaBase FLOAT
AS
BEGIN
    BEGIN TRY
        -- Kiểm tra thuốc có tồn tại không
        IF NOT EXISTS (SELECT 1 FROM Thuoc WHERE maThuoc = @maThuoc)
        BEGIN
            RAISERROR(N'Mã thuốc không tồn tại trong hệ thống! ', 16, 1);
            RETURN;
        END
        
        -- Kiểm tra giá hợp lệ
        IF @giaBase < 0
        BEGIN
            RAISERROR(N'Giá không được âm!', 16, 1);
            RETURN;
        END
        
        -- Lấy mã đơn vị của thuốc
        DECLARE @maDonVi VARCHAR(50);
        SELECT @maDonVi = maDonVi FROM Thuoc WHERE maThuoc = @maThuoc;
        
        -- Kiểm tra chi tiết bảng giá đã tồn tại chưa
        IF EXISTS (
            SELECT 1 
            FROM ChiTietBangGia 
            WHERE maBangGia = 'BG001' AND maThuoc = @maThuoc
        )
        BEGIN
            -- Cập nhật giá nếu đã tồn tại
            UPDATE ChiTietBangGia
            SET donGia = @giaBase,
                maDonVi = @maDonVi,
                isActive = 1
            WHERE maBangGia = 'BG001' 
              AND maThuoc = @maThuoc;
              
            PRINT N'Đã cập nhật giá thuốc ' + @maThuoc + N' thành ' + CAST(@giaBase AS NVARCHAR(20));
        END
        ELSE
        BEGIN
            -- Thêm mới nếu chưa tồn tại
            INSERT INTO ChiTietBangGia (maBangGia, maThuoc, donGia, maDonVi, isActive)
            VALUES ('BG001', @maThuoc, @giaBase, @maDonVi, 1);
            
            PRINT N'Đã thêm mới giá thuốc ' + @maThuoc + N' với giá ' + CAST(@giaBase AS NVARCHAR(20));
        END
        
    END TRY
    BEGIN CATCH
        DECLARE @ErrorMessage NVARCHAR(4000) = ERROR_MESSAGE();
        RAISERROR(@ErrorMessage, 16, 1);
    END CATCH
END
GO

-- PROCEDURE Lấy thuốc by id kèm giá
-- Param: none
-- Return: thuốc kèm giá ( db cũ )
CREATE PROCEDURE sp_LayThuocKemGiaById
    @maThuoc VARCHAR(20)
AS
BEGIN
    SET NOCOUNT ON;

    SELECT TOP 1
        T.maThuoc,
        T.tenThuoc,
        T.soLuongTon,
        CTBG.donGia,
        DV.tenDonVi,
        T.soLuongToiThieu,
        T.maNSX,
        T.isActive
    FROM chiTietBangGia CTBG
    JOIN BANGGIA BG 
        ON BG.maBangGia = CTBG.maBangGia
    JOIN Thuoc T 
        ON T.maThuoc = CTBG.maThuoc
	JOIN DonVi DV
		ON DV.maDonVi = T.maDonVi
    WHERE CTBG.maThuoc = @maThuoc
      AND BG.trangThai = N'Đang áp dụng'
      AND GETDATE() BETWEEN BG.ngayApDung AND BG.ngayKetThuc
    ORDER BY 
        BG.doUuTien DESC,
        BG.ngayApDung DESC,
        BG.maBangGia DESC;
END;
GO



--==============================  KHÁCH HÀNG ==============================--



-- PROCEDURE Thống kê số lượng khách hàng đã mua hàng trong năm
-- Param: Năm cần thống kê
-- Return: Năm, Số khách hàng (đếm khách hàng duy nhất)
GO
CREATE PROCEDURE sp_GetSoKhachHangCuaNam
    @Nam INT
AS
BEGIN
    SELECT @Nam AS [Nam], COUNT(DISTINCT hd.maKH) AS [SoKhachHang]
    FROM HoaDon hd
    WHERE YEAR(hd.ngayBan) = @Nam AND hd.isActive = 1 AND hd.maKH IS NOT NULL;
END
GO

CREATE PROCEDURE sp_GetSoKhachHangCuaThang
    @Thang INT, @Nam INT
AS
BEGIN
    SELECT @Thang AS [Thang], @Nam AS [Nam], COUNT(DISTINCT hd.maKH) AS [SoKhachHang]
    FROM HoaDon hd
    WHERE YEAR(hd.ngayBan) = @Nam AND MONTH(hd.ngayBan) = @Thang AND hd.isActive = 1 AND hd.maKH IS NOT NULL;
END
GO

CREATE PROCEDURE sp_GetDoanhThuTrungBinhTheoNgay
    @Thang INT, @Nam INT
AS
BEGIN
    DECLARE @TongDoanhThu FLOAT;
    DECLARE @SoNgayTrongThang INT;
    
    SELECT @TongDoanhThu = ISNULL(SUM(cthd.soLuong * cthd.donGia), 0)
    FROM HoaDon hd
    INNER JOIN ChiTietHoaDon cthd ON hd.maHoaDon = cthd.maHoaDon
    WHERE YEAR(hd.ngayBan) = @Nam AND MONTH(hd.ngayBan) = @Thang AND hd.isActive = 1 AND cthd.isActive = 1;
    
    SET @SoNgayTrongThang = DAY(EOMONTH(DATEFROMPARTS(@Nam, @Thang, 1)));
    
    SELECT @Thang AS [Thang], @Nam AS [Nam], @SoNgayTrongThang AS [SoNgayTrongThang], @TongDoanhThu AS [TongDoanhThu],
        CASE WHEN @SoNgayTrongThang > 0 THEN @TongDoanhThu / @SoNgayTrongThang ELSE 0 END AS [DoanhThuTrungBinhMoiNgay];
END
GO

CREATE PROCEDURE sp_GetDoanhThuTrungBinhTheoThang
    @Nam INT
AS
BEGIN
    DECLARE @TongDoanhThu FLOAT;
    DECLARE @SoThangDaQua INT;
    DECLARE @ThangHienTai INT = MONTH(GETDATE());
    DECLARE @NamHienTai INT = YEAR(GETDATE());
    
    IF @Nam < @NamHienTai SET @SoThangDaQua = 12;
    ELSE IF @Nam = @NamHienTai SET @SoThangDaQua = @ThangHienTai;
    ELSE SET @SoThangDaQua = 0;
    
    SELECT @TongDoanhThu = ISNULL(SUM(cthd.soLuong * cthd.donGia), 0)
    FROM HoaDon hd
    INNER JOIN ChiTietHoaDon cthd ON hd.maHoaDon = cthd.maHoaDon
    WHERE YEAR(hd.ngayBan) = @Nam AND hd.isActive = 1 AND cthd.isActive = 1;
    
    SELECT @Nam AS [Nam], @SoThangDaQua AS [SoThangDaQua], @TongDoanhThu AS [TongDoanhThu],
        CASE WHEN @SoThangDaQua > 0 THEN @TongDoanhThu / @SoThangDaQua ELSE 0 END AS [DoanhThuTrungBinhMoiThang];
END
GO

--==============================  HẠN SỬ DỤNG ==============================--



-- PROCEDURE Thống kê các lô thuốc đã hết hạn sử dụng 
-- Param: 
-- Return: các lô thuốc đã hết hạn sử dụng 
CREATE PROCEDURE sp_GetLoThuocDaHetHan
AS
BEGIN
    SELECT ct.maLo, t.maThuoc, t.tenThuoc, ct.ngaySanXuat, ct.hanSuDung, ct.soLuong AS 'soLuongTon', DATEDIFF(DAY, ct.hanSuDung, Getdate()) AS 'soNgayDaHetHan'
    FROM ChiTietLoThuoc ct
    INNER JOIN Thuoc t ON ct.maThuoc = t.maThuoc
    WHERE ct.hanSuDung < getdate() AND ct.isActive = 0
    ORDER BY ct.hanSuDung ASC;
END
GO

-- PROCEDURE Thống kê các lô thuốc đã hết hạn sử dụng 
-- Param: Số ngày
-- Return: các lô thuốc sẽ hết hạn trong N ngày
CREATE PROCEDURE sp_GetLoThuocSapHetHan
    @SoNgay INT = 30
AS
BEGIN
    DECLARE @NgayHienTai DATE = CAST(GETDATE() AS DATE);
    DECLARE @NgayKiemTra DATE = DATEADD(DAY, @SoNgay, @NgayHienTai);
    
    SELECT ct.maLo, t.maThuoc, t.tenThuoc, ct.ngaySanXuat, ct.hanSuDung, ct.soLuong AS 'soLuongTon', DATEDIFF(DAY, @NgayHienTai, ct.hanSuDung) AS [soNgayConLai]
    FROM ChiTietLoThuoc ct
    INNER JOIN Thuoc t ON ct.maThuoc = t.maThuoc
    WHERE ct.hanSuDung >= @NgayHienTai AND ct.hanSuDung <= @NgayKiemTra AND ct.isActive = 1 AND ct.soLuong > 0 AND t.isActive = 1                            
    ORDER BY ct.hanSuDung ASC, ct.maThuoc;         
END
GO