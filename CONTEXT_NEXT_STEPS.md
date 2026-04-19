# CONTEXT HANDOFF - JPA ORM MIGRATION + GUI BRIDGE

Updated: 2026-04-19
Module: `jpa-orm-migration`
DB target: **MariaDB**

## 1) Mục tiêu đã chốt trong phiên này
- Dùng project mới `jpa-orm-migration` làm nơi tích hợp dần GUI cũ.
- Chuẩn hóa kiến trúc: **adapter -> service -> repository**.
- Login frame phải validate qua JPA/MariaDB, không gọi `ConnectDB` trong `DangNhapFrame`.

## 2) Những gì đã làm

### 2.1 Core JPA/Migration
- Sửa `HoaDonService` để trừ tồn FIFO đúng theo **nhiều lô** (không còn dồn vào 1 lô cuối).
- Thêm `TonKhoJpaDAO`.
- Thêm integration test `JpaMigrationIntegrationTest` cho:
  - login
  - list thuốc + giá
  - tạo hóa đơn + trừ tồn FIFO
- Đổi `hibernate.hbm2ddl.auto` sang `validate`.

### 2.2 Refactor kiến trúc adapter
- `migration/TaiKhoanJpaDAO` đã đổi sang gọi service:
  - `IAuthService/AuthService`
  - `ITaiKhoanService/TaiKhoanService`
- `migration/ThuocJpaDAO` đã đổi sang gọi `IThuocService/ThuocService`.
- `migration/TonKhoJpaDAO` đã đổi sang gọi `ITonKhoService/TonKhoService`.
- `migration/HoaDonJpaDAO` đã đổi sang gọi `IHoaDonService/HoaDonService` cho read + write.
- Bổ sung service interfaces/impl mới:
  - `ITaiKhoanService`, `IThuocService`, `ITonKhoService`
  - `TaiKhoanService`, `ThuocService`, `TonKhoService`
- Mở rộng `IHoaDonService` + `HoaDonService`:
  - `getHoaDonById`
  - `findHoaDonByDateRange`

### 2.3 Bridge package `app.*` trong project mới
Đã thêm các lớp tương thích ở `src/main/java/app`:
- `app.DAO`: `TaiKhoanDAO`, `ThuocDAO`, `HoaDonDAO`, `TonKhoDAO`
- `app.DTO`: `ThuocKemGiaDTO`
- `app.Entity`: `TaiKhoan`, `HoaDon`

Mục đích: cho GUI cũ gọi chữ ký quen thuộc (`app.DAO/...`) nhưng backend đi qua JPA migration.

### 2.4 Login frame
- File: `src/main/java/com/pillchill/migration/gui/DangNhapFrame.java`
- Đã bỏ `ConnectDB.connect()` trong `validateLogin()`.
- Login hiện gọi `TaiKhoanJpaDAO` (qua JPA/MariaDB) và có thông báo lỗi kết nối rõ ràng.

## 3) Trạng thái hiện tại cần lưu ý
- Trong module mới hiện có `com/pillchill/migration/gui/DangNhapFrame.java`.
- Trong `src/main/java/app` hiện có `DAO/DTO/Entity`, **chưa có `app/GUI` ở trạng thái hiện tại**.
- Có nhiều file build output trong `target/` thay đổi do quá trình làm việc.
- Môi trường này thiếu `mvn`/`javac` trong PATH nên chưa chạy verify bằng CLI tại đây.

## 4) Việc cần làm tiếp theo (ưu tiên)
1. **Chuẩn hóa GUI location trong project mới**
   - Quyết định 1 trong 2:
   - A) Dùng `com.pillchill.migration.gui.*` làm GUI package chính, hoặc
   - B) Copy full GUI cũ vào `app.GUI` rồi fix import cho đồng nhất.

2. **Nếu chọn copy full GUI vào project mới**
   - Copy toàn bộ `src/app/GUI/*.java` từ project cũ sang module mới.
   - Resolve dependencies thiếu trong module mới (`KhachHangDAO`, `NhanVienDAO`, `KhuyenMaiDAO`, ...), hiện chưa migrate đủ.

3. **Hoàn thiện bridge/service cho các màn ngoài login/bán hàng**
   - Các nghiệp vụ chưa migrate: điểm tích lũy, khuyến mãi chi tiết, phiếu đặt/đổi trả/nhập, thống kê.

4. **Dọn trạng thái build**
   - Không commit file sinh ra trong `target/`.

5. **Chạy verify khi máy có tool**
   - `mvn clean compile`
   - `mvn test`

## 5) File quan trọng nên đọc trước khi tiếp tục
- `src/main/java/com/pillchill/migration/migration/*.java`
- `src/main/java/com/pillchill/migration/service/*.java`
- `src/main/java/com/pillchill/migration/service/impl/*.java`
- `src/main/java/com/pillchill/migration/gui/DangNhapFrame.java`
- `src/main/java/app/DAO/*.java`
- `src/test/java/com/pillchill/migration/integration/JpaMigrationIntegrationTest.java`
- `src/main/resources/META-INF/persistence.xml`
