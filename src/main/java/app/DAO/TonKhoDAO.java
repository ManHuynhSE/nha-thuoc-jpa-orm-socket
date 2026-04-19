package app.DAO;

import java.util.ArrayList;

import javax.swing.JOptionPane;

import com.pillchill.migration.migration.TonKhoJpaDAO;

public class TonKhoDAO {
    private final TonKhoJpaDAO delegate = new TonKhoJpaDAO();

    public boolean capNhatTonKhoSauKhiBan(ArrayList<Object[]> dsChiTiet) {
        // Stock deduction is already handled inside HoaDonService#createHoaDon (FIFO).
        return true;
    }

    public int getSoLuongTonKho(String maThuoc) {
        return delegate.getTongSoLuongTon(maThuoc);
    }

    public boolean dongBoSoLuongTon(String maThuoc) {
        if (maThuoc == null) {
            return true;
        }
        delegate.dongBoSoLuongTon(maThuoc);
        return true;
    }

    public int getSoLuongReserved(String maThuoc) {
        return 0;
    }

    public int getSoLuongAvailable(String maThuoc) {
        return getSoLuongTonKho(maThuoc);
    }

    public boolean kiemTraDuSoLuongAvailable(ArrayList<Object[]> dsChiTiet) {
        for (Object[] item : dsChiTiet) {
            String maThuoc = (String) item[0];
            String tenThuoc = (String) item[1];
            int soLuongCanMua = (Integer) item[2];
            int soLuongAvailable = getSoLuongAvailable(maThuoc);
            if (soLuongAvailable < soLuongCanMua) {
                JOptionPane.showMessageDialog(
                        null,
                        "Khong du so luong thuoc " + tenThuoc + " (ma " + maThuoc + ").\nCo the ban toi da: " + soLuongAvailable,
                        "Canh bao ton kho",
                        JOptionPane.WARNING_MESSAGE);
                return false;
            }
        }
        return true;
    }
}
