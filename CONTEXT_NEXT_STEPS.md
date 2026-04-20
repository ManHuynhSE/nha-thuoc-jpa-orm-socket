# CONTEXT HANDOFF - NETWORK FLOW + GUI (LOGIN -> MAIN -> CAP NHAT THUOC)

Updated: 2026-04-20  
Module: `jpa-orm-migration`

## 1) Tóm tắt phiên làm việc này

- Đã phân tích mẫu socket request/response từ project tham chiếu (`Demo-Vaccine-Jpa-Socket`) để chốt hướng kiến trúc cho project hiện tại.
- Đã áp dụng hướng **NetworkClient dùng chung + truyền session context qua GUI** vào code chính của project.
- Đã tách server xử lý command theo kiểu **Dispatcher + Handler**, không còn dồn logic vào một hàm `process` lớn.
- Đã nối thử 2 nghiệp vụ chạy theo network:
  1. `LOGIN`
  2. `THUOC_LIST_ALL` (dùng cho màn cập nhật thuốc).

## 2) Những gì đã có trong code hiện tại

### 2.1 Network communication model
- Package: `src/main/java/com/pillchill/migration/network/communication`
- Các class:
  - `CommandType` (đang có `LOGIN`, `THUOC_LIST_ALL`)
  - `Request` (`commandType`, `data`, `sessionUserId`)
  - `Response` (`success`, `data`, `message`)
  - `LoginPayload`

### 2.2 Client side network
- Package: `src/main/java/com/pillchill/migration/network/client`
- `NetworkClient`:
  - Giữ **persistent socket** + `ObjectInputStream/ObjectOutputStream` trong cùng phiên GUI.
  - `send(Request)` là synchronized, request/response kiểu đồng bộ.
- `AuthClientController`:
  - Gói request login gửi lên server.
- `ClientSessionContext`:
  - Giữ `NetworkClient` + `userId` sau login để truyền qua các màn hình.
- `ThuocClientController`:
  - Gọi command `THUOC_LIST_ALL` với `sessionUserId`.
  - Parse `Response.data` về `List<ThuocKemGiaView>`.

### 2.3 Server side command routing
- Package: `src/main/java/com/pillchill/migration/network/server`
- `Server`:
  - Mở `ServerSocket`, dùng thread pool.
  - Mỗi client connection vào `handleClient`.
  - Trong `handleClient` có vòng lặp đọc request nhiều lần trên cùng socket.
- `CommandDispatcher`:
  - `Map<CommandType, CommandHandler>`.
  - Nhận request, route đúng handler theo `commandType`.
- `CommandHandler` interface + handlers:
  - `AuthCommandHandler`: dùng `TaiKhoanJpaDAO.kiemTraDangNhap(...)`.
  - `ThuocListCommandHandler`: dùng `ThuocJpaDAO.getAllThuocKemGia()` và trả danh sách.

### 2.4 GUI integration đã làm

#### `DangNhapFrame`
- Đã chuyển sang nhận `host/port` trong constructor.
- Đăng nhập chạy bằng `SwingWorker` (không block EDT).
- Tạo `NetworkClient` lần đầu khi login.
- Login thành công:
  - Tạo `ClientSessionContext(networkClient, userId)`.
  - Mở `MainFrame(context)`.
- Có đóng `networkClient` khi đóng cửa sổ login.

#### `MainFrame`
- Constructor đổi sang nhận `ClientSessionContext`.
- Giữ context trong frame để truyền tiếp cho panel.
- `showCapNhatThuocPanel()` tạo panel bằng:
  - `new CapNhatThuocPanel(new ThuocClientController(sessionContext))`.

#### `CapNhatThuocPanel`
- Constructor đã nhận `ThuocClientController`.
- `loadThuocData()` dùng `SwingWorker` gọi `thuocClientController.getAllThuocItems()`.
- Data table đang lấy từ server response thay vì gọi thẳng DAO cho load danh sách.

## 3) Flow network hiện tại (để phiên sau đọc nhanh)

1. GUI Login lấy `username/password`.
2. `AuthClientController.login(...)` tạo `Request(LOGIN, LoginPayload, sessionUserId=null)`.
3. `NetworkClient.send()` ghi request qua socket, chờ `readObject()` response.
4. Server `handleClient` nhận request -> `CommandDispatcher.dispatch(...)`.
5. `AuthCommandHandler` xác thực qua `TaiKhoanJpaDAO` -> trả `Response(success, userId, message)`.
6. Client nhận response:
   - Nếu fail: báo lỗi.
   - Nếu success: tạo `ClientSessionContext` và mở `MainFrame`.
7. Màn `CapNhatThuocPanel` gọi `ThuocClientController.getAllThuocItems()`.
8. Client gửi `Request(THUOC_LIST_ALL, null, sessionUserId=userId)`.
9. Server route qua `ThuocListCommandHandler`, gọi `ThuocJpaDAO.getAllThuocKemGia()`, trả list.
10. Panel cập nhật JTable từ response.

## 4) Lưu ý kỹ thuật quan trọng

- `NetworkClient.send()` hiện là đồng bộ; async chưa làm.
- Với mô hình async, vẫn cần một read-loop ở background thread để ghép response với request tương ứng (future/promise).
- Server-side session check hiện chỉ kiểm tra `sessionUserId` non-empty; chưa có token/session store thật.
- Host login hiện đang hard-code ở `DangNhapFrame.main()` (`DESKTOP-57QN5N0`, `9999`).
- Trong môi trường CLI hiện tại không có `mvn` trong PATH nên chưa verify build/test bằng command line.

## 5) Có thêm prototype tách riêng

- Có một bản thử nghiệm trong folder:
  - `prototype/networkdemo/**`
- Mục tiêu: minh họa dispatcher/handler và command flow tách biệt.
- Lưu ý: folder này nằm ngoài `src/main/java` (không thuộc main source mặc định của Maven), nên cần quyết định:
  - Giữ để tham khảo kiến trúc, hoặc
  - Di chuyển vào source chính / xóa nếu không dùng.

## 6) Việc cần làm tiếp theo (ưu tiên)

1. **Ổn định entrypoint server/client**
   - Chốt class chạy server chính thức cho package `com.pillchill.migration.network.server`.
   - Chuyển host/port ra config.

2. **Chuẩn hóa vòng đời NetworkClient**
   - Chốt nơi đóng connection (logout/app close).
   - Tránh đóng client ở login frame nếu đã chuyển ownership sang main frame.

3. **Migrate command theo module**
   - Tạo thêm command + handler + client controller cho các màn còn lại (hóa đơn, tồn kho, ...).
   - Mỗi module có handler riêng, không mở rộng switch lớn.

4. **Nâng session/auth**
   - Thay `sessionUserId` thuần bằng session token có lưu trạng thái ở server.

5. **Dọn code chưa dùng**
   - Xóa import/field thừa (ví dụ các chỗ còn giữ DAO cũ nhưng không dùng).
   - Quyết định số phận folder `prototype/networkdemo`.

6. **Verify khi có tool**
   - Chạy `mvn clean compile`
   - Chạy `mvn test`
