package app.DAO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import app.Entity.HoaDon;

import com.pillchill.migration.dto.CreateHoaDonCommand;
import com.pillchill.migration.dto.HoaDonItemCommand;
import com.pillchill.migration.migration.HoaDonJpaDAO;

public class HoaDonDAO {
    private final HoaDonJpaDAO delegate = new HoaDonJpaDAO();

    public HoaDon getHoaDonById(String id) {
        return toLegacy(delegate.getHoaDonById(id));
    }

    public ArrayList<HoaDon> findHoaDonByDateRange(Date fromDate, Date toDate) {
        ArrayList<HoaDon> result = new ArrayList<>();
        for (com.pillchill.migration.entity.HoaDon item : delegate.findHoaDonByDateRange(fromDate, toDate)) {
            result.add(toLegacy(item));
        }
        return result;
    }

    public String generateMaHoaDon() {
        return "HD" + DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS").format(LocalDateTime.now());
    }

    public boolean saveHoaDon(ArrayList<Object[]> chiTietData, double tongTien, String maHoaDon, String maNhanVien,
                              String maKhachHang, String maKhuyenMai, String ghiChu) {
        List<HoaDonItemCommand> items = new ArrayList<>();
        for (Object[] item : chiTietData) {
            String maThuoc = (String) item[0];
            int soLuong = (Integer) item[2];
            float donGia = ((Number) item[3]).floatValue();
            items.add(new HoaDonItemCommand(maThuoc, soLuong, donGia));
        }

        CreateHoaDonCommand command = new CreateHoaDonCommand(
                maHoaDon,
                LocalDate.now(),
                ghiChu,
                maNhanVien,
                emptyToNull(maKhachHang),
                emptyToNull(maKhuyenMai),
                0.10d,
                "VAT 10%",
                items);
        return delegate.addHoaDon(command);
    }

    public boolean saveHoaDon(ArrayList<Object[]> chiTietData, double tongTien, String maHoaDon, String maNhanVien) {
        return saveHoaDon(chiTietData, tongTien, maHoaDon, maNhanVien, null, null, null);
    }

    public boolean saveHoaDonWithMaLo(ArrayList<Object[]> chiTietData, double tongTien, String maHoaDon, String maNhanVien,
                                      String maKhachHang, String maKhuyenMai, String ghiChu) {
        return saveHoaDon(chiTietData, tongTien, maHoaDon, maNhanVien, maKhachHang, maKhuyenMai, ghiChu);
    }

    public boolean capNhatDiemTichLuy(String maHoaDon) {
        return false;
    }

    public boolean truDiemTichLuy(String maKhachHang, int diemSuDung) {
        return false;
    }

    private HoaDon toLegacy(com.pillchill.migration.entity.HoaDon source) {
        if (source == null) {
            return null;
        }
        Date ngayBan = source.getNgayBan() == null
                ? null
                : Date.from(source.getNgayBan().atStartOfDay(ZoneId.systemDefault()).toInstant());
        return new HoaDon(
                source.getMaHoaDon(),
                ngayBan,
                source.getGhiChu(),
                null,
                null,
                null,
                source.getGiaTriThue(),
                source.getTenLoaiThue(),
                source.isActive());
    }

    private String emptyToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value;
    }
}
