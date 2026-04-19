package app.DAO;

import java.util.ArrayList;

import app.Entity.TaiKhoan;

import com.pillchill.migration.migration.TaiKhoanJpaDAO;

public class TaiKhoanDAO {
    private final TaiKhoanJpaDAO delegate = new TaiKhoanJpaDAO();

    public TaiKhoan kiemTraDangNhap(String maNV, String matKhau) {
        return toLegacy(delegate.kiemTraDangNhap(maNV, matKhau));
    }

    public TaiKhoan getTaiKhoanById(String maNV) {
        return toLegacy(delegate.getTaiKhoanById(maNV));
    }

    public ArrayList<TaiKhoan> getAllTaiKhoan() {
        ArrayList<TaiKhoan> result = new ArrayList<>();
        for (com.pillchill.migration.entity.TaiKhoan item : delegate.getAllTaiKhoan()) {
            result.add(toLegacy(item));
        }
        return result;
    }

    private TaiKhoan toLegacy(com.pillchill.migration.entity.TaiKhoan source) {
        if (source == null) {
            return null;
        }
        return new TaiKhoan(source.getMaNV(), source.getMatKhau(), source.isActive());
    }
}
