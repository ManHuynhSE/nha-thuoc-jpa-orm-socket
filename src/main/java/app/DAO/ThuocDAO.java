package app.DAO;

import java.util.ArrayList;

import app.DTO.ThuocKemGiaDTO;

import com.pillchill.migration.dto.ThuocKemGiaView;
import com.pillchill.migration.migration.ThuocJpaDAO;

public class ThuocDAO {
    private final ThuocJpaDAO delegate = new ThuocJpaDAO();

    public ArrayList<ThuocKemGiaDTO> getAllThuocKemGia() {
        ArrayList<ThuocKemGiaDTO> result = new ArrayList<>();
        for (ThuocKemGiaView item : delegate.getAllThuocKemGia()) {
            result.add(toLegacy(item));
        }
        return result;
    }

    public ThuocKemGiaDTO getThuocKemGiaById(String id) {
        for (ThuocKemGiaDTO item : getAllThuocKemGia()) {
            if (item.getMaThuoc().equals(id)) {
                return item;
            }
        }
        return null;
    }

    private ThuocKemGiaDTO toLegacy(ThuocKemGiaView source) {
        return new ThuocKemGiaDTO(
                source.maThuoc(),
                source.tenThuoc(),
                source.soLuongTon(),
                source.giaBan(),
                source.donVi(),
                source.soLuongToiThieu(),
                source.maNSX(),
                source.isActive());
    }
}
