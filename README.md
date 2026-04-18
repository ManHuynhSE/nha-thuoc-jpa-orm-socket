# JPA ORM Migration Project (không dùng socket)

Project này là backend JPA mới để thay thế dần tầng JDBC/DAO cũ trong `nha-thuoc-pill-and-chill-java-swing`.
Thiết kế tham chiếu theo Demo-Vaccine-Jpa-Socket ở phần `repository/service/mapper` (không dùng socket).

## Mục tiêu đã triển khai

1. Dựng project Maven riêng với Hibernate + MariaDB JDBC.
2. Cấu hình `persistence.xml` + `db/JPAUtil`.
3. Thêm Lombok để giảm boilerplate cho entity và ID class.
4. Map toàn bộ bảng chính trong `DatabaseScript.sql` sang JPA entity:
   - `NhaSanXuat`, `LoThuoc`, `DonVi`, `Thuoc`, `BangGia`, `ChiTietBangGia`
   - `ChiTietLoThuoc`, `KhachHang`, `ChucVu`, `NhanVien`, `TaiKhoan`
   - `KhuyenMai`, `HoaDon`, `ChiTietHoaDon`
   - `PhieuDoiTra`, `ChiTietPhieuDoiTra`
   - `PhieuDat`, `ChiTietPhieuDat`
   - `PhieuNhapThuoc`, `ChiTietPhieuNhap`
5. Map quan hệ JPA đầy đủ:
   - `@ManyToOne`: Thuoc-DonVi, Thuoc-NhaSanXuat, NhanVien-ChucVu, HoaDon-(NhanVien/KhachHang/KhuyenMai),...
   - `@EmbeddedId + @MapsId`: toàn bộ bảng chi tiết có khóa ghép.
   - `@OneToOne + @MapsId`: TaiKhoan-NhanVien.
6. Tạo ID class cho toàn bộ bảng khóa ghép.
7. Tạo repository JPA theo kiểu demo:
   - Interface ở `repository/*`
   - Implement ở `repository/impl/*`
   - Dùng `RepositoryTemplate.execute(...)` cho transaction.
8. Tạo service theo kiểu demo:
   - Interface ở `service/*`
   - Implement ở `service/impl/*`
9. Tạo service nghiệp vụ lập hóa đơn + trừ tồn kho FIFO + đồng bộ tồn.
10. Tạo DataMapper (Jackson) dùng map DTO/entity kiểu generic.
11. Tạo adapter DAO-style để tích hợp dần vào Swing cũ:
   - `TaiKhoanJpaDAO`
   - `ThuocJpaDAO`
   - `HoaDonJpaDAO`

## Cách chạy

```powershell
cd jpa-orm-migration
mvn clean compile
mvn exec:java
```

## Cấu hình DB

Chỉnh trực tiếp trong `src/main/resources/META-INF/persistence.xml`.
Persistence unit mặc định đang dùng: `mariadb-pu`.

## Lộ trình thay thế trong Swing cũ

1. Đăng nhập: thay `TaiKhoanDAO` bằng `TaiKhoanJpaDAO`.
2. Danh mục thuốc/lập hóa đơn: thay `ThuocDAO`, `HoaDonDAO`, `TonKhoDAO` bằng adapter/service JPA tương ứng.
3. Các màn còn lại migrate theo từng cụm nghiệp vụ.
4. Khi hoàn tất, gỡ `ConnectDB` và DAO JDBC.
