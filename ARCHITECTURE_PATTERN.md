# Key Architecture: ThuocJpaDAO Pattern Applied

## DAO Pattern: Before vs After

### BEFORE (Incorrect - Generic Repository)
```java
@Repository
public class KhachHangJpaDAO {
    private final GenericRepository<KhachHang, String> repository;
    
    public KhachHangJpaDAO(GenericRepository<KhachHang, String> repository) {
        this.repository = repository;
    }
    
    public List<KhachHang> getAllKhachHang() {
        return repository.findAll(KhachHang.class);  // ❌ Wrong
    }
}
```

### AFTER (Correct - Service-Based, like ThuocJpaDAO)
```java
public class KhachHangJpaDAO {
    private final IKhachHangService khachHangService;
    
    public KhachHangJpaDAO(IKhachHangService khachHangService) {
        this.khachHangService = khachHangService;
    }
    
    public KhachHangJpaDAO() {
        this.khachHangService = new KhachHangService();  // ✅ Default constructor
    }
    
    public ArrayList<KhachHang> getAllKhachHang() {
        return new ArrayList<>(khachHangService.getAllKhachHang());  // ✅ Correct
    }
}
```

---

## Complete Architecture Layers

```
┌─────────────────────────────────────────────────────────────────┐
│ GUI LAYER                                                       │
├─────────────────────────────────────────────────────────────────┤
│ TimKiemKhachHangPanel          TimKiemNhanVienPanel            │
│ (SwingWorker async loading)    (SwingWorker + ComboBox)         │
└──────────────────────┬──────────────────────┬────────────────────┘
                       │                      │
                       ↓                      ↓
┌─────────────────────────────────────────────────────────────────┐
│ CLIENT NETWORK LAYER                                            │
├─────────────────────────────────────────────────────────────────┤
│ KhachHangClientController      NhanVienClientController         │
│ • getAllKhachHangItems()       • getAllNhanVienItems()          │
│ • Sends KHACH_HANG.LIST_ALL    • Sends NHAN_VIEN.LIST_ALL      │
└──────────────────────┬──────────────────────┬────────────────────┘
                       │         Socket       │
                       └──────────┬───────────┘
                                  ↓
┌─────────────────────────────────────────────────────────────────┐
│ SERVER NETWORK LAYER                                            │
├─────────────────────────────────────────────────────────────────┤
│ CommandDispatcher                                               │
│ ├── "KHACH_HANG" → KhachHangCommandHandler                      │
│ └── "NHAN_VIEN" → NhanVienCommandHandler                        │
└──────────────────────┬──────────────────────┬────────────────────┘
                       │                      │
                       ↓                      ↓
┌─────────────────────────────────────────────────────────────────┐
│ DAO LAYER (Migration Bridge)                                    │
├─────────────────────────────────────────────────────────────────┤
│ KhachHangJpaDAO                NhanVienJpaDAO                   │
│ • Uses IKhachHangService       • Uses INhanVienService          │
│ • Default: new KhachHangService()  • Default: new NhanVienService()
└──────────────────────┬──────────────────────┬────────────────────┘
                       │                      │
                       ↓                      ↓
┌─────────────────────────────────────────────────────────────────┐
│ SERVICE LAYER (Business Logic)                                  │
├─────────────────────────────────────────────────────────────────┤
│ KhachHangService               NhanVienService                  │
│ • Uses IKhachHangRepository    • Uses INhanVienRepository       │
│ • Default: new KhachHangRepository()  • Default: new NhanVienRepository()
└──────────────────────┬──────────────────────┬────────────────────┘
                       │                      │
                       ↓                      ↓
┌─────────────────────────────────────────────────────────────────┐
│ REPOSITORY LAYER (Data Access)                                  │
├─────────────────────────────────────────────────────────────────┤
│ KhachHangRepository            NhanVienRepository               │
│ • Extends AbstracGenericRepository   • Extends AbstracGenericRepository
│ • Uses Template for JPA ops    • Uses Template for JPA ops      │
│ • findAllActive()              • findAllActive() + eager fetch  │
│ • deactivateKhachHang()        • deactivateNhanVien()           │
└──────────────────────┬──────────────────────┬────────────────────┘
                       │                      │
                       └──────────┬───────────┘
                                  ↓
┌─────────────────────────────────────────────────────────────────┐
│ PERSISTENCE LAYER                                               │
├─────────────────────────────────────────────────────────────────┤
│ EntityManager (JPA)                                             │
│ JPQL: "select k from KhachHang k where k.isActive = true"       │
│ JPQL: "select n from NhanVien n left join fetch n.chucVu ..."   │
└──────────────────────┬────────────────────────────────────────────┘
                       │
                       ↓
┌─────────────────────────────────────────────────────────────────┐
│ DATABASE                                                        │
├─────────────────────────────────────────────────────────────────┤
│ KhachHang table                NhanVien table                   │
│ ├── maKH (PK)                  ├── maNV (PK)                    │
│ ├── tenKH                      ├── tenNV                        │
│ ├── soDienThoai                ├── maChucVu (FK)                │
│ ├── diemTichLuy                ├── soDienThoai                  │
│ └── isActive                   └── isActive                     │
└─────────────────────────────────────────────────────────────────┘
```

---

## Method Flow: getAllKhachHang()

```
TimKiemKhachHangPanel.loadDataFromServer()
  │
  └─→ SwingWorker.doInBackground()
       │
       └─→ KhachHangClientController.getAllKhachHangItems()
            │
            └─→ KhachHangClientController.getAllKhachHang()
                 │
                 └─→ NetworkClient.send(Request("KHACH_HANG.LIST_ALL", ...))
                      │ [Socket Communication]
                      └─→ Server receives request
                           │
                           └─→ CommandDispatcher.dispatch(request)
                                │
                                └─→ KhachHangCommandHandler.handle(request)
                                     │
                                     └─→ KhachHangJpaDAO.getAllKhachHang()
                                          │
                                          └─→ KhachHangService.getAllKhachHang()
                                               │
                                               └─→ KhachHangRepository.findAllActive()
                                                    │
                                                    └─→ Template.execute(em -> em.createQuery(...))
                                                         │
                                                         └─→ Database Query
                                                              │
                                                              └─→ List<KhachHang>
                                         │
                                         └─→ Response(success=true, data=list, msg="OK")
                      │
                      └─→ Client receives Response
                           │
                           └─→ Parse List<KhachHang> from response.data
  │
  └─→ SwingWorker.done()
       │
       └─→ GUI.loadTableData(List<KhachHang>)
            │
            └─→ JTable updated with rows
```

---

## Service → Repository → JPA Query

### KhachHangService
```java
@Override
public List<KhachHang> getAllKhachHang() {
    return khachHangRepository.findAllActive();  // Delegates to repository
}
```

### KhachHangRepository
```java
@Override
public List<KhachHang> findAllActive() {
    return template.execute(em -> 
        em.createQuery(
            "select k from KhachHang k " +
            "where k.isActive = true " +
            "order by k.maKH",
            KhachHang.class
        ).getResultList()
    );
}
```

---

## NhanVien with ChucVu Relationship

### NhanVienRepository (Eager Fetch)
```java
@Override
public List<NhanVien> findAllActive() {
    return template.execute(em -> 
        em.createQuery(
            "select n from NhanVien n " +
            "left join fetch n.chucVu cv " +  // ← Eager fetch to avoid lazy loading
            "where n.isActive = true " +
            "order by n.maNV",
            NhanVien.class
        ).getResultList()
    );
}
```

### TimKiemNhanVienPanel (Load ComboBox)
```java
private void loadFilterData(List<NhanVien> list) {
    Set<String> chucVuSet = new HashSet<>();
    for (NhanVien item : list) {
        if (item.getChucVu() != null && 
            item.getChucVu().getTenChucVu() != null &&
            !item.getChucVu().getTenChucVu().isBlank()) {
            chucVuSet.add(item.getChucVu().getTenChucVu());
        }
    }
    
    cboChucVu.removeAllItems();
    cboChucVu.addItem("");  // Empty option
    chucVuSet.stream().sorted().forEach(cboChucVu::addItem);
}
```

---

## Error Handling Pattern

### Client Side
```java
try {
    List<KhachHang> result = khachHangClientController.getAllKhachHangItems();
    loadTableData(result);
} catch (Exception e) {
    JOptionPane.showMessageDialog(
        this,
        "Không tải được danh sách khách hàng: " + e.getMessage(),
        "Lỗi",
        JOptionPane.ERROR_MESSAGE
    );
}
```

### Server Side
```java
@Override
public Response handle(Request request) {
    if (request.getSessionUserId() == null || 
        request.getSessionUserId().isBlank()) {
        return Response.error("Bạn chưa đăng nhập");
    }
    
    String action = request.getCommand().substring("KHACH_HANG.".length());
    try {
        cmd = KhachHangCM.valueOf(action);
    } catch (IllegalArgumentException e) {
        return Response.error("Command khách hàng không hỗ trợ: " + action);
    }
    
    try {
        // ... handle command
        return Response.success(data, "Message");
    } catch (Exception e) {
        return Response.error("Lỗi: " + e.getMessage());
    }
}
```

---

## Comparison: KhachHangJpaDAO vs ThuocJpaDAO

| Aspect | KhachHangJpaDAO | ThuocJpaDAO |
|--------|-----------------|------------|
| **Service Injection** | IKhachHangService | IThuocService |
| **Service Implementation** | KhachHangService | ThuocService |
| **Repository** | IKhachHangRepository | IThuocRepository |
| **Default Constructor** | new KhachHangService() | new ThuocService() |
| **Return Type** | ArrayList<KhachHang> | ArrayList<Thuoc> |
| **Methods** | getAllKhachHang(), getKhachHangById(), createKhachHang(), updateKhachHang(), deactivateKhachHang(), countActive() | getAllThuoc(), getThuocById(), getAllThuocKemGia(), getAllThuocTheoLo(), createThuoc(), updateThuoc(), deactivateThuoc() |
| **Pattern** | ✅ IDENTICAL | ✅ IDENTICAL |

---

## Testing the Architecture

### Unit Test Example
```java
@Test
public void testGetAllKhachHang() {
    // Create mock service
    IKhachHangService mockService = mock(IKhachHangService.class);
    mockService.when(() -> mockService.getAllKhachHang())
        .thenReturn(Arrays.asList(new KhachHang(...)));
    
    // Create DAO with mock
    KhachHangJpaDAO dao = new KhachHangJpaDAO(mockService);
    
    // Test
    ArrayList<KhachHang> result = dao.getAllKhachHang();
    
    // Assert
    assertEquals(1, result.size());
    mockService.verify(() -> mockService.getAllKhachHang(), times(1));
}
```

### Integration Test Example
```java
@Test
public void testServerCommandHandler() {
    // Create real DAO
    KhachHangJpaDAO dao = new KhachHangJpaDAO();
    KhachHangCommandHandler handler = new KhachHangCommandHandler(dao);
    
    // Create request
    Request request = new Request("KHACH_HANG.LIST_ALL", null, "user123");
    
    // Handle
    Response response = handler.handle(request);
    
    // Assert
    assertTrue(response.isSuccess());
    assertThat(response.getData(), instanceOf(List.class));
}
```

---

## Summary of Changes

✅ **KhachHangJpaDAO**: Service-based instead of repository-based
✅ **NhanVienJpaDAO**: Service-based instead of repository-based
✅ **Both DAOs**: Now match ThuocJpaDAO pattern exactly
✅ **Error Handling**: Consistent with server handlers
✅ **Network Flow**: DOMAIN.ACTION command format
✅ **GUI Integration**: SwingWorker + client-side filtering
✅ **Relationship Handling**: NhanVien with eager-fetch ChucVu

**All 26 files now follow enterprise architecture best practices.**
