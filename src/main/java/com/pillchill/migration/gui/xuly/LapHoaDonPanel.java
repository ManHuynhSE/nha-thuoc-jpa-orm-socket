package com.pillchill.migration.gui.xuly;

import com.pillchill.migration.dto.ThuocKemGiaView;
import com.pillchill.migration.dto.ThuocTheoLoView;
import com.pillchill.migration.gui.xuly.XacNhanLapHoaDonFrame;
import com.pillchill.migration.network.client.HoaDonClientController;
import com.pillchill.migration.network.client.KhachHangClientController;
import com.pillchill.migration.network.client.KhuyenMaiClientController;
import com.pillchill.migration.network.client.PhieuDatClientController;
import com.pillchill.migration.network.client.ThuocClientController;
import com.pillchill.migration.network.communication.HoaDonCreateItemPayload;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class LapHoaDonPanel extends JPanel implements com.pillchill.migration.gui.HoaDonCallback {
    private final ThuocClientController thuocClientController;
    private final HoaDonClientController hoaDonClientController;
    private final KhachHangClientController khachHangClientController;
    private final KhuyenMaiClientController khuyenMaiClientController;
    private final PhieuDatClientController phieuDatClientController;
    private final String maNhanVien;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final List<ThuocTheoLoView> dsThuocTheoLo = new ArrayList<>();
    private final Map<String, Double> giaTheoThuoc = new HashMap<>();

    private final DefaultTableModel modelThuoc;
    private final DefaultTableModel modelGioHang;
    private final JTable tblThuoc;
    private final JTable tblGioHang;
    private final JTextField txtSearch;
    private final JSpinner spnSoLuongThem;
    private final JSpinner spnSoLuongXoa;
    private final JLabel lblTongTien;

    public LapHoaDonPanel(ThuocClientController thuocClientController,
                          HoaDonClientController hoaDonClientController,
                          KhachHangClientController khachHangClientController,
                          KhuyenMaiClientController khuyenMaiClientController,
                          PhieuDatClientController phieuDatClientController,
                          String maNhanVien) {
        this.thuocClientController = thuocClientController;
        this.hoaDonClientController = hoaDonClientController;
        this.khachHangClientController = khachHangClientController;
        this.khuyenMaiClientController = khuyenMaiClientController;
        this.phieuDatClientController = phieuDatClientController;
        this.maNhanVien = maNhanVien;

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(20, 40, 20, 40));

        JLabel lblTitle = new JLabel("LẬP HÓA ĐƠN THEO LÔ", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        add(lblTitle, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 0, 15));
        centerPanel.setBackground(Color.WHITE);
        add(centerPanel, BorderLayout.CENTER);

        JPanel topPanel = new JPanel(new BorderLayout(0, 10));
        topPanel.setBackground(Color.WHITE);
        txtSearch = new JTextField();
        JButton btnSearch = createButton("Tìm", new Color(240, 240, 240), Color.BLACK);
        JButton btnResetSearch = createButton("Xóa lọc", new Color(240, 240, 240), Color.BLACK);
        JButton btnHoaDonPhieuDat = createButton("Từ phiếu đặt", new Color(52, 152, 219), Color.WHITE);
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.add(new JLabel("Từ khóa:"));
        txtSearch.setPreferredSize(new Dimension(300, 32));
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);
        searchPanel.add(btnResetSearch);
        searchPanel.add(btnHoaDonPhieuDat);
        topPanel.add(searchPanel, BorderLayout.NORTH);

        String[] thuocCols = {"Mã thuốc", "Tên thuốc", "Mã lô", "HSD", "SL lô", "Đơn giá", "Đơn vị"};
        modelThuoc = new DefaultTableModel(thuocCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblThuoc = new JTable(modelThuoc);
        tblThuoc.setRowHeight(32);
        topPanel.add(new JScrollPane(tblThuoc), BorderLayout.CENTER);

        JPanel topActionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        topActionPanel.setBackground(Color.WHITE);
        spnSoLuongThem = new JSpinner(new SpinnerNumberModel(1, 1, 10000, 1));
        JButton btnThem = createButton("Thêm vào giỏ", new Color(0, 150, 136), Color.WHITE);
        topActionPanel.add(new JLabel("Số lượng:"));
        topActionPanel.add(spnSoLuongThem);
        topActionPanel.add(btnThem);
        topPanel.add(topActionPanel, BorderLayout.SOUTH);
        centerPanel.add(topPanel);

        JPanel bottomPanel = new JPanel(new BorderLayout(0, 10));
        bottomPanel.setBackground(Color.WHITE);
        String[] gioCols = {"Mã thuốc", "Tên thuốc", "Mã lô", "Số lượng", "Đơn giá", "Thành tiền"};
        modelGioHang = new DefaultTableModel(gioCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblGioHang = new JTable(modelGioHang);
        tblGioHang.setRowHeight(32);
        bottomPanel.add(new JScrollPane(tblGioHang), BorderLayout.CENTER);

        JPanel bottomActionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        bottomActionPanel.setBackground(Color.WHITE);
        spnSoLuongXoa = new JSpinner(new SpinnerNumberModel(1, 1, 10000, 1));
        JButton btnXoa = createButton("Xóa khỏi giỏ", new Color(231, 76, 60), Color.WHITE);
        JButton btnXoaHet = createButton("Xóa giỏ", new Color(240, 240, 240), Color.BLACK);
        bottomActionPanel.add(new JLabel("Số lượng xóa:"));
        bottomActionPanel.add(spnSoLuongXoa);
        bottomActionPanel.add(btnXoa);
        bottomActionPanel.add(btnXoaHet);
        bottomPanel.add(bottomActionPanel, BorderLayout.SOUTH);
        centerPanel.add(bottomPanel);

        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        southPanel.setBackground(Color.WHITE);
        southPanel.add(new JLabel("Nhân viên: " + maNhanVien));
        lblTongTien = new JLabel("Tổng tiền: 0 VNĐ");
        lblTongTien.setFont(new Font("Segoe UI", Font.BOLD, 14));
        southPanel.add(lblTongTien);
        JButton btnXacNhan = createButton("Xác nhận tạo hóa đơn", new Color(46, 204, 113), Color.WHITE);
        southPanel.add(btnXacNhan);
        add(southPanel, BorderLayout.SOUTH);

        btnSearch.addActionListener(e -> filterThuocTheoLo());
        btnResetSearch.addActionListener(e -> {
            txtSearch.setText("");
            loadTableThuoc(dsThuocTheoLo);
        });
        btnThem.addActionListener(e -> themVaoGioHang());
        btnXoa.addActionListener(e -> xoaKhoiGioHang());
        btnXoaHet.addActionListener(e -> {
            modelGioHang.setRowCount(0);
            capNhatTongTien();
        });
        btnXacNhan.addActionListener(e -> moFrameXacNhanHoaDon());
        btnHoaDonPhieuDat.addActionListener(e -> moFrameHoaDonTuPhieuDat());

        reloadDataFromDatabase();
    }

    private JButton createButton(String text, Color bg, Color fg) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBackground(bg);
        button.setForeground(fg);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    public void reloadDataFromDatabase() {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                List<ThuocTheoLoView> thuocTheoLo = thuocClientController.getAllThuocTheoLoItems();
                List<ThuocKemGiaView> thuocKemGia = thuocClientController.getAllThuocItems();

                dsThuocTheoLo.clear();
                dsThuocTheoLo.addAll(thuocTheoLo);
                giaTheoThuoc.clear();
                for (ThuocKemGiaView item : thuocKemGia) {
                    giaTheoThuoc.put(item.getMaThuoc(), item.getGiaBan());
                }
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    loadTableThuoc(dsThuocTheoLo);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(
                            LapHoaDonPanel.this,
                            "Không tải được dữ liệu lập hóa đơn: " + ex.getMessage(),
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };
        worker.execute();
    }

    private void loadTableThuoc(List<ThuocTheoLoView> list) {
        modelThuoc.setRowCount(0);
        for (ThuocTheoLoView item : list) {
            double giaBan = giaTheoThuoc.getOrDefault(item.getMaThuoc(), 0.0);
            modelThuoc.addRow(new Object[]{
                    item.getMaThuoc(),
                    item.getTenThuoc(),
                    item.getMaLo(),
                    item.getHanSuDung() == null ? "" : item.getHanSuDung().format(dateFormatter),
                    item.getSoLuongLo(),
                    giaBan,
                    item.getDonVi()
            });
        }
    }

    private void filterThuocTheoLo() {
        String keyword = txtSearch.getText().trim().toLowerCase(Locale.ROOT);
        if (keyword.isBlank()) {
            loadTableThuoc(dsThuocTheoLo);
            return;
        }
        List<ThuocTheoLoView> filtered = new ArrayList<>();
        for (ThuocTheoLoView item : dsThuocTheoLo) {
            boolean match = (item.getMaThuoc() != null && item.getMaThuoc().toLowerCase(Locale.ROOT).contains(keyword))
                    || (item.getTenThuoc() != null && item.getTenThuoc().toLowerCase(Locale.ROOT).contains(keyword))
                    || (item.getMaLo() != null && item.getMaLo().toLowerCase(Locale.ROOT).contains(keyword));
            if (match) {
                filtered.add(item);
            }
        }
        loadTableThuoc(filtered);
    }

    private void themVaoGioHang() {
        int selectedRow = tblThuoc.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một lô thuốc để bán.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String maThuoc = String.valueOf(modelThuoc.getValueAt(selectedRow, 0));
        String tenThuoc = String.valueOf(modelThuoc.getValueAt(selectedRow, 1));
        String maLo = String.valueOf(modelThuoc.getValueAt(selectedRow, 2));
        int soLuongLo = (int) modelThuoc.getValueAt(selectedRow, 4);
        double donGia = (double) modelThuoc.getValueAt(selectedRow, 5);
        int soLuongThem = (int) spnSoLuongThem.getValue();

        if (soLuongThem > soLuongLo) {
            JOptionPane.showMessageDialog(this, "Số lượng vượt quá tồn của lô đã chọn.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        for (int i = 0; i < modelGioHang.getRowCount(); i++) {
            String maThuocGio = String.valueOf(modelGioHang.getValueAt(i, 0));
            String maLoGio = String.valueOf(modelGioHang.getValueAt(i, 2));
            if (maThuoc.equals(maThuocGio) && maLo.equals(maLoGio)) {
                int soLuongCu = (int) modelGioHang.getValueAt(i, 3);
                int soLuongMoi = soLuongCu + soLuongThem;
                if (soLuongMoi > soLuongLo) {
                    JOptionPane.showMessageDialog(this, "Tổng số lượng trong giỏ vượt quá tồn của lô.", "Thông báo", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                modelGioHang.setValueAt(soLuongMoi, i, 3);
                modelGioHang.setValueAt(soLuongMoi * donGia, i, 5);
                capNhatTongTien();
                return;
            }
        }

        modelGioHang.addRow(new Object[]{maThuoc, tenThuoc, maLo, soLuongThem, donGia, soLuongThem * donGia});
        capNhatTongTien();
    }

    private void xoaKhoiGioHang() {
        int selectedRow = tblGioHang.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn dòng cần xóa khỏi giỏ.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int soLuongXoa = (int) spnSoLuongXoa.getValue();
        int soLuongCu = (int) modelGioHang.getValueAt(selectedRow, 3);
        double donGia = (double) modelGioHang.getValueAt(selectedRow, 4);
        int soLuongMoi = soLuongCu - soLuongXoa;
        if (soLuongMoi <= 0) {
            modelGioHang.removeRow(selectedRow);
        } else {
            modelGioHang.setValueAt(soLuongMoi, selectedRow, 3);
            modelGioHang.setValueAt(soLuongMoi * donGia, selectedRow, 5);
        }
        capNhatTongTien();

    }

    private void capNhatTongTien() {
        double tongTien = 0;
        for (int i = 0; i < modelGioHang.getRowCount(); i++) {
            tongTien += (double) modelGioHang.getValueAt(i, 5);
        }
        lblTongTien.setText(String.format("Tổng tiền: %,.0f VNĐ", tongTien));
    }

    private void moFrameXacNhanHoaDon() {
        if (modelGioHang.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Giỏ hàng đang trống.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Xác nhận tạo hóa đơn?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        List<HoaDonCreateItemPayload> items = new ArrayList<>();
        for (int i = 0; i < modelGioHang.getRowCount(); i++) {
            items.add(new HoaDonCreateItemPayload(
                    String.valueOf(modelGioHang.getValueAt(i, 0)),
                    String.valueOf(modelGioHang.getValueAt(i, 1)),
                    String.valueOf(modelGioHang.getValueAt(i, 2)),
                    (int) modelGioHang.getValueAt(i, 3),
                    ((Double) modelGioHang.getValueAt(i, 4)).floatValue()
            ));
        }

        String maHoaDon = taoMaHoaDon();
        double tongTien = getTongTienGioHang();
        XacNhanLapHoaDonFrame frame = new XacNhanLapHoaDonFrame(
                hoaDonClientController,
                khachHangClientController,
                khuyenMaiClientController,
                items,
                maHoaDon,
                maNhanVien,
                tongTien,
                this
        );
        frame.setVisible(true);
    }

    private void moFrameHoaDonTuPhieuDat() {
        LapHoaDonTuPhieuDatFrame frame = new LapHoaDonTuPhieuDatFrame(
                phieuDatClientController,
                thuocClientController,
                hoaDonClientController,
                khachHangClientController,
                khuyenMaiClientController,
                maNhanVien,
                this
        );
        frame.setVisible(true);
    }

    private String taoMaHoaDon() {
        try {
            String maHoaDonCu = hoaDonClientController.getMaHoaDonGanNhat();
            if (maHoaDonCu == null || maHoaDonCu.isBlank() || !maHoaDonCu.startsWith("HD")) {
                throw new IllegalArgumentException("Mã hóa đơn không hợp lệ");
            }
            String numberPart = maHoaDonCu.substring(2);
            int number = Integer.parseInt(numberPart);
            number++;
            return String.format("HD%05d", number);
        } catch (Exception ex) {
            long now = System.currentTimeMillis();
            return "HD" + now;
        }
    }

    private double getTongTienGioHang() {
        double tongTien = 0;
        for (int i = 0; i < modelGioHang.getRowCount(); i++) {
            tongTien += (double) modelGioHang.getValueAt(i, 5);
        }
        return tongTien;
    }

    @Override
    public void onHoaDonSuccess(String maHoaDon) {
        modelGioHang.setRowCount(0);
        capNhatTongTien();
        reloadDataFromDatabase();
        JOptionPane.showMessageDialog(this, "Đã lập hóa đơn thành công: " + maHoaDon, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }
}
