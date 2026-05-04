package com.pillchill.migration.gui.xuly;

import com.pillchill.migration.dto.ChiTietHoaDonView;
import com.pillchill.migration.dto.HoaDonView;
import com.pillchill.migration.network.client.HoaDonClientController;
import com.pillchill.migration.network.client.PhieuDoiTraClientController;
import com.pillchill.migration.network.communication.PhieuDoiTraCreateItemPayload;
import com.pillchill.migration.network.communication.PhieuDoiTraCreatePayload;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LapPhieuTraThuocPanel extends JPanel {
    private final HoaDonClientController hoaDonClientController;
    private final PhieuDoiTraClientController phieuDoiTraClientController;
    private final String maNhanVien;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final List<HoaDonView> dsHoaDon = new ArrayList<>();
    private String maHoaDonDangChon;

    private final DefaultTableModel modelHoaDon;
    private final DefaultTableModel modelChiTiet;
    private final DefaultTableModel modelTraThuoc;
    private final JTable tblHoaDon;
    private final JTable tblChiTiet;
    private final JTable tblTraThuoc;
    private final JTextField txtSearchHoaDon;
    private final JSpinner spnSoLuongTra;
    private final JTextField txtLyDo;
    private final JLabel lblTongTienTra;

    public LapPhieuTraThuocPanel(
            HoaDonClientController hoaDonClientController,
            PhieuDoiTraClientController phieuDoiTraClientController,
            String maNhanVien
    ) {
        this.hoaDonClientController = hoaDonClientController;
        this.phieuDoiTraClientController = phieuDoiTraClientController;
        this.maNhanVien = maNhanVien;

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(20, 40, 20, 40));

        JLabel lblTitle = new JLabel("LẬP PHIẾU TRẢ THUỐC", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        add(lblTitle, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(3, 1, 0, 12));
        centerPanel.setBackground(Color.WHITE);
        add(centerPanel, BorderLayout.CENTER);

        JPanel hoaDonPanel = new JPanel(new BorderLayout(0, 8));
        hoaDonPanel.setBackground(Color.WHITE);
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.add(new JLabel("Tìm hóa đơn:"));
        txtSearchHoaDon = new JTextField();
        txtSearchHoaDon.setPreferredSize(new Dimension(280, 32));
        JButton btnTim = createButton("Tìm", new Color(240, 240, 240), Color.BLACK);
        JButton btnXoaLoc = createButton("Xóa lọc", new Color(240, 240, 240), Color.BLACK);
        searchPanel.add(txtSearchHoaDon);
        searchPanel.add(btnTim);
        searchPanel.add(btnXoaLoc);
        hoaDonPanel.add(searchPanel, BorderLayout.NORTH);

        modelHoaDon = new DefaultTableModel(
                new String[]{"Mã hóa đơn", "Ngày bán", "Nhân viên", "Khách hàng", "Ghi chú"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblHoaDon = new JTable(modelHoaDon);
        tblHoaDon.setRowHeight(32);
        tblHoaDon.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = tblHoaDon.getSelectedRow();
                if (row >= 0) {
                    maHoaDonDangChon = String.valueOf(modelHoaDon.getValueAt(row, 0));
                    loadChiTietHoaDon(maHoaDonDangChon);
                }
            }
        });
        hoaDonPanel.add(new JScrollPane(tblHoaDon), BorderLayout.CENTER);
        centerPanel.add(hoaDonPanel);

        JPanel chiTietPanel = new JPanel(new BorderLayout(0, 8));
        chiTietPanel.setBackground(Color.WHITE);
        modelChiTiet = new DefaultTableModel(
                new String[]{"Mã thuốc", "Tên thuốc", "Mã lô", "SL mua", "Đơn giá"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblChiTiet = new JTable(modelChiTiet);
        tblChiTiet.setRowHeight(32);
        chiTietPanel.add(new JScrollPane(tblChiTiet), BorderLayout.CENTER);

        JPanel addPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        addPanel.setBackground(Color.WHITE);
        spnSoLuongTra = new JSpinner(new SpinnerNumberModel(1, 1, 10000, 1));
        txtLyDo = new JTextField();
        txtLyDo.setPreferredSize(new Dimension(260, 30));
        JButton btnThem = createButton("Thêm vào phiếu trả", new Color(0, 150, 136), Color.WHITE);
        addPanel.add(new JLabel("SL trả:"));
        addPanel.add(spnSoLuongTra);
        addPanel.add(new JLabel("Lý do:"));
        addPanel.add(txtLyDo);
        addPanel.add(btnThem);
        chiTietPanel.add(addPanel, BorderLayout.SOUTH);
        centerPanel.add(chiTietPanel);

        JPanel traPanel = new JPanel(new BorderLayout(0, 8));
        traPanel.setBackground(Color.WHITE);
        modelTraThuoc = new DefaultTableModel(
                new String[]{"Mã thuốc", "Tên thuốc", "Mã lô", "SL trả", "Đơn giá", "Thành tiền", "Lý do"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblTraThuoc = new JTable(modelTraThuoc);
        tblTraThuoc.setRowHeight(32);
        traPanel.add(new JScrollPane(tblTraThuoc), BorderLayout.CENTER);

        JPanel removePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        removePanel.setBackground(Color.WHITE);
        JButton btnXoaDong = createButton("Xóa dòng", new Color(231, 76, 60), Color.WHITE);
        JButton btnXoaHet = createButton("Xóa hết", new Color(240, 240, 240), Color.BLACK);
        lblTongTienTra = new JLabel("Tổng tiền trả: 0");
        lblTongTienTra.setFont(new Font("Segoe UI", Font.BOLD, 13));
        removePanel.add(btnXoaDong);
        removePanel.add(btnXoaHet);
        removePanel.add(Box.createHorizontalStrut(20));
        removePanel.add(lblTongTienTra);
        traPanel.add(removePanel, BorderLayout.SOUTH);
        centerPanel.add(traPanel);

        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        southPanel.setBackground(Color.WHITE);
        southPanel.add(new JLabel("Nhân viên: " + maNhanVien));
        JButton btnXacNhan = createButton("Xác nhận lập phiếu trả", new Color(46, 204, 113), Color.WHITE);
        southPanel.add(btnXacNhan);
        add(southPanel, BorderLayout.SOUTH);

        btnTim.addActionListener(e -> filterHoaDon());
        btnXoaLoc.addActionListener(e -> {
            txtSearchHoaDon.setText("");
            loadHoaDonTable(dsHoaDon);
        });
        btnThem.addActionListener(e -> themVaoPhieuTra());
        btnXoaDong.addActionListener(e -> xoaDongTraThuoc());
        btnXoaHet.addActionListener(e -> {
            modelTraThuoc.setRowCount(0);
            capNhatTongTienTra();
        });
        btnXacNhan.addActionListener(e -> xacNhanLapPhieuTra());

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
        SwingWorker<List<HoaDonView>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<HoaDonView> doInBackground() {
                return hoaDonClientController.getAllHoaDonItems();
            }

            @Override
            protected void done() {
                try {
                    dsHoaDon.clear();
                    dsHoaDon.addAll(get());
                    loadHoaDonTable(dsHoaDon);
                    modelChiTiet.setRowCount(0);
                    modelTraThuoc.setRowCount(0);
                    maHoaDonDangChon = null;
                    capNhatTongTienTra();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(
                            LapPhieuTraThuocPanel.this,
                            "Không tải được danh sách hóa đơn: " + ex.getMessage(),
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };
        worker.execute();
    }

    private void loadHoaDonTable(List<HoaDonView> list) {
        modelHoaDon.setRowCount(0);
        for (HoaDonView item : list) {
            modelHoaDon.addRow(new Object[]{
                    item.getMaHoaDon(),
                    item.getNgayBan() == null ? "" : item.getNgayBan().format(dateFormatter),
                    item.getTenNhanVien() == null ? "" : item.getTenNhanVien(),
                    item.getTenKhachHang() == null ? "Khách vãng lai" : item.getTenKhachHang(),
                    item.getGhiChu() == null ? "" : item.getGhiChu()
            });
        }
    }

    private void loadChiTietHoaDon(String maHoaDon) {
        SwingWorker<List<ChiTietHoaDonView>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<ChiTietHoaDonView> doInBackground() {
                return hoaDonClientController.getChiTietHoaDonItems(maHoaDon);
            }

            @Override
            protected void done() {
                try {
                    modelChiTiet.setRowCount(0);
                    for (ChiTietHoaDonView item : get()) {
                        modelChiTiet.addRow(new Object[]{
                                item.getMaThuoc(),
                                item.getTenThuoc(),
                                item.getMaLo(),
                                item.getSoLuong(),
                                item.getDonGia()
                        });
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(
                            LapPhieuTraThuocPanel.this,
                            "Không tải được chi tiết hóa đơn: " + ex.getMessage(),
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };
        worker.execute();
    }

    private void filterHoaDon() {
        String keyword = txtSearchHoaDon.getText().trim().toLowerCase(Locale.ROOT);
        if (keyword.isBlank()) {
            loadHoaDonTable(dsHoaDon);
            return;
        }
        List<HoaDonView> filtered = new ArrayList<>();
        for (HoaDonView item : dsHoaDon) {
            boolean match = (item.getMaHoaDon() != null && item.getMaHoaDon().toLowerCase(Locale.ROOT).contains(keyword))
                    || (item.getTenKhachHang() != null && item.getTenKhachHang().toLowerCase(Locale.ROOT).contains(keyword))
                    || (item.getTenNhanVien() != null && item.getTenNhanVien().toLowerCase(Locale.ROOT).contains(keyword));
            if (match) {
                filtered.add(item);
            }
        }
        loadHoaDonTable(filtered);
    }

    private void themVaoPhieuTra() {
        if (maHoaDonDangChon == null || maHoaDonDangChon.isBlank()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn hóa đơn trước", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int selectedRow = tblChiTiet.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm từ chi tiết hóa đơn", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String maThuoc = String.valueOf(modelChiTiet.getValueAt(selectedRow, 0));
        String tenThuoc = String.valueOf(modelChiTiet.getValueAt(selectedRow, 1));
        String maLo = String.valueOf(modelChiTiet.getValueAt(selectedRow, 2));
        int soLuongDaMua = Integer.parseInt(String.valueOf(modelChiTiet.getValueAt(selectedRow, 3)));
        double donGia = toDouble(modelChiTiet.getValueAt(selectedRow, 4));
        int soLuongTra = (Integer) spnSoLuongTra.getValue();
        String lyDo = txtLyDo.getText() == null ? "" : txtLyDo.getText().trim();

        if (soLuongTra <= 0) {
            JOptionPane.showMessageDialog(this, "Số lượng trả phải lớn hơn 0", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (soLuongTra > soLuongDaMua) {
            JOptionPane.showMessageDialog(this, "Số lượng trả vượt quá số lượng đã mua", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int existingRow = findReturnRow(maThuoc, maLo);
        if (existingRow >= 0) {
            int current = Integer.parseInt(String.valueOf(modelTraThuoc.getValueAt(existingRow, 3)));
            if (current + soLuongTra > soLuongDaMua) {
                JOptionPane.showMessageDialog(this, "Tổng số lượng trả vượt quá số lượng đã mua", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int soLuongMoi = current + soLuongTra;
            modelTraThuoc.setValueAt(soLuongMoi, existingRow, 3);
            modelTraThuoc.setValueAt(donGia * soLuongMoi, existingRow, 5);
            if (!lyDo.isBlank()) {
                modelTraThuoc.setValueAt(lyDo, existingRow, 6);
            }
        } else {
            modelTraThuoc.addRow(new Object[]{
                    maThuoc,
                    tenThuoc,
                    maLo,
                    soLuongTra,
                    donGia,
                    donGia * soLuongTra,
                    lyDo
            });
        }
        capNhatTongTienTra();
    }

    private void xoaDongTraThuoc() {
        int selectedRow = tblTraThuoc.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn dòng cần xóa", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        modelTraThuoc.removeRow(selectedRow);
        capNhatTongTienTra();
    }

    private int findReturnRow(String maThuoc, String maLo) {
        for (int i = 0; i < modelTraThuoc.getRowCount(); i++) {
            String eMaThuoc = String.valueOf(modelTraThuoc.getValueAt(i, 0));
            String eMaLo = String.valueOf(modelTraThuoc.getValueAt(i, 2));
            if (eMaThuoc.equals(maThuoc) && eMaLo.equals(maLo)) {
                return i;
            }
        }
        return -1;
    }

    private double toDouble(Object value) {
        if (value instanceof Number number) {
            return number.doubleValue();
        }
        return Double.parseDouble(String.valueOf(value));
    }

    private void xacNhanLapPhieuTra() {
        if (maHoaDonDangChon == null || maHoaDonDangChon.isBlank()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn hóa đơn trước khi lập phiếu trả", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (modelTraThuoc.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Phiếu trả đang trống", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<PhieuDoiTraCreateItemPayload> items = new ArrayList<>();
        for (int i = 0; i < modelTraThuoc.getRowCount(); i++) {
            items.add(new PhieuDoiTraCreateItemPayload(
                    String.valueOf(modelTraThuoc.getValueAt(i, 0)),
                    String.valueOf(modelTraThuoc.getValueAt(i, 2)),
                    Integer.parseInt(String.valueOf(modelTraThuoc.getValueAt(i, 3))),
                    toDouble(modelTraThuoc.getValueAt(i, 4)),
                    String.valueOf(modelTraThuoc.getValueAt(i, 6))
            ));
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Xác nhận lập phiếu trả cho hóa đơn " + maHoaDonDangChon + " với " + items.size() + " dòng?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION
        );
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            PhieuDoiTraCreatePayload payload = new PhieuDoiTraCreatePayload(maHoaDonDangChon, items);
            String maPhieuDoiTra = phieuDoiTraClientController.createPhieuDoiTraItems(payload);
            JOptionPane.showMessageDialog(
                    this,
                    "Lập phiếu trả thành công: " + maPhieuDoiTra,
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE
            );
            modelTraThuoc.setRowCount(0);
            txtLyDo.setText("");
            capNhatTongTienTra();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi lập phiếu trả: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void capNhatTongTienTra() {
        double tong = 0;
        for (int i = 0; i < modelTraThuoc.getRowCount(); i++) {
            tong += toDouble(modelTraThuoc.getValueAt(i, 5));
        }
        lblTongTienTra.setText(String.format(Locale.ROOT, "Tổng tiền trả: %,.0f", tong));
    }
}
