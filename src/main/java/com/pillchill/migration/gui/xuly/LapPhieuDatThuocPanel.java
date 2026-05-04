package com.pillchill.migration.gui.xuly;

import com.pillchill.migration.dto.ThuocTheoLoView;
import com.pillchill.migration.entity.KhachHang;
import com.pillchill.migration.network.client.KhachHangClientController;
import com.pillchill.migration.network.client.PhieuDatClientController;
import com.pillchill.migration.network.client.ThuocClientController;
import com.pillchill.migration.network.communication.PhieuDatCreateItemPayload;
import com.pillchill.migration.network.communication.PhieuDatCreatePayload;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LapPhieuDatThuocPanel extends JPanel {
    private final ThuocClientController thuocClientController;
    private final PhieuDatClientController phieuDatClientController;
    private final KhachHangClientController khachHangClientController;
    private final String maNhanVien;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final List<ThuocTheoLoView> dsThuocTheoLo = new ArrayList<>();
    private KhachHang khachHangDaChon;

    private final DefaultTableModel modelThuoc;
    private final DefaultTableModel modelGioHang;
    private final JTable tblThuoc;
    private final JTable tblGioHang;

    private final JTextField txtSearch;
    private final JSpinner spnSoLuongThem;
    private final JSpinner spnSoLuongXoa;
    private final JTextField txtSdt;
    private final JTextField txtGhiChu;
    private final JLabel lblKhachHang;
    private final JLabel lblTongDong;

    public LapPhieuDatThuocPanel(
            ThuocClientController thuocClientController,
            PhieuDatClientController phieuDatClientController,
            KhachHangClientController khachHangClientController,
            String maNhanVien
    ) {
        this.thuocClientController = thuocClientController;
        this.phieuDatClientController = phieuDatClientController;
        this.khachHangClientController = khachHangClientController;
        this.maNhanVien = maNhanVien;

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(20, 40, 20, 40));

        JLabel lblTitle = new JLabel("LẬP PHIẾU ĐẶT THUỐC", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        add(lblTitle, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 0, 12));
        centerPanel.setBackground(Color.WHITE);
        add(centerPanel, BorderLayout.CENTER);

        JPanel topPanel = new JPanel(new BorderLayout(0, 8));
        topPanel.setBackground(Color.WHITE);
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.add(new JLabel("Từ khóa:"));
        txtSearch = new JTextField();
        txtSearch.setPreferredSize(new Dimension(280, 32));
        JButton btnTim = createButton("Tìm", new Color(240, 240, 240), Color.BLACK);
        JButton btnXoaLoc = createButton("Xóa lọc", new Color(240, 240, 240), Color.BLACK);
        searchPanel.add(txtSearch);
        searchPanel.add(btnTim);
        searchPanel.add(btnXoaLoc);
        topPanel.add(searchPanel, BorderLayout.NORTH);

        modelThuoc = new DefaultTableModel(
                new String[]{"Mã thuốc", "Tên thuốc", "Mã lô", "HSD", "SL lô", "Đơn vị"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblThuoc = new JTable(modelThuoc);
        tblThuoc.setRowHeight(32);
        topPanel.add(new JScrollPane(tblThuoc), BorderLayout.CENTER);

        JPanel addPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        addPanel.setBackground(Color.WHITE);
        spnSoLuongThem = new JSpinner(new SpinnerNumberModel(1, 1, 10000, 1));
        JButton btnThem = createButton("Thêm vào phiếu", new Color(0, 150, 136), Color.WHITE);
        addPanel.add(new JLabel("Số lượng:"));
        addPanel.add(spnSoLuongThem);
        addPanel.add(btnThem);
        topPanel.add(addPanel, BorderLayout.SOUTH);
        centerPanel.add(topPanel);

        JPanel bottomPanel = new JPanel(new BorderLayout(0, 8));
        bottomPanel.setBackground(Color.WHITE);
        modelGioHang = new DefaultTableModel(
                new String[]{"Mã thuốc", "Tên thuốc", "Mã lô", "Số lượng"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblGioHang = new JTable(modelGioHang);
        tblGioHang.setRowHeight(32);
        bottomPanel.add(new JScrollPane(tblGioHang), BorderLayout.CENTER);

        JPanel removePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        removePanel.setBackground(Color.WHITE);
        spnSoLuongXoa = new JSpinner(new SpinnerNumberModel(1, 1, 10000, 1));
        JButton btnXoa = createButton("Giảm/Xóa", new Color(231, 76, 60), Color.WHITE);
        JButton btnXoaHet = createButton("Xóa hết", new Color(240, 240, 240), Color.BLACK);
        removePanel.add(new JLabel("Số lượng giảm:"));
        removePanel.add(spnSoLuongXoa);
        removePanel.add(btnXoa);
        removePanel.add(btnXoaHet);
        bottomPanel.add(removePanel, BorderLayout.SOUTH);
        centerPanel.add(bottomPanel);

        JPanel southPanel = new JPanel(new BorderLayout(10, 8));
        southPanel.setBackground(Color.WHITE);

        JPanel leftSouth = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftSouth.setBackground(Color.WHITE);
        leftSouth.add(new JLabel("SĐT khách hàng:"));
        txtSdt = new JTextField();
        txtSdt.setPreferredSize(new Dimension(180, 30));
        JButton btnTimKhachHang = createButton("Tìm KH", new Color(240, 240, 240), Color.BLACK);
        lblKhachHang = new JLabel("Khách hàng: Khách vãng lai");
        leftSouth.add(txtSdt);
        leftSouth.add(btnTimKhachHang);
        leftSouth.add(lblKhachHang);

        JPanel rightSouth = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightSouth.setBackground(Color.WHITE);
        txtGhiChu = new JTextField();
        txtGhiChu.setPreferredSize(new Dimension(260, 30));
        lblTongDong = new JLabel("Số dòng: 0");
        lblTongDong.setFont(new Font("Segoe UI", Font.BOLD, 13));
        JLabel lblNhanVien = new JLabel("Nhân viên: " + maNhanVien);
        JButton btnXacNhan = createButton("Xác nhận lập phiếu đặt", new Color(46, 204, 113), Color.WHITE);
        rightSouth.add(new JLabel("Ghi chú:"));
        rightSouth.add(txtGhiChu);
        rightSouth.add(lblTongDong);
        rightSouth.add(lblNhanVien);
        rightSouth.add(btnXacNhan);

        southPanel.add(leftSouth, BorderLayout.WEST);
        southPanel.add(rightSouth, BorderLayout.EAST);
        add(southPanel, BorderLayout.SOUTH);

        btnTim.addActionListener(e -> filterThuocTheoLo());
        btnXoaLoc.addActionListener(e -> {
            txtSearch.setText("");
            loadThuocTable(dsThuocTheoLo);
        });
        btnThem.addActionListener(e -> themVaoGioHang());
        btnXoa.addActionListener(e -> xoaKhoiGioHang());
        btnXoaHet.addActionListener(e -> {
            modelGioHang.setRowCount(0);
            capNhatThongKe();
        });
        btnTimKhachHang.addActionListener(e -> timKhachHang());
        btnXacNhan.addActionListener(e -> xacNhanLapPhieuDat());

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
        SwingWorker<List<ThuocTheoLoView>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<ThuocTheoLoView> doInBackground() {
                return thuocClientController.getAllThuocTheoLoItems();
            }

            @Override
            protected void done() {
                try {
                    dsThuocTheoLo.clear();
                    dsThuocTheoLo.addAll(get());
                    loadThuocTable(dsThuocTheoLo);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(
                            LapPhieuDatThuocPanel.this,
                            "Không tải được dữ liệu thuốc theo lô: " + ex.getMessage(),
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };
        worker.execute();
    }

    private void loadThuocTable(List<ThuocTheoLoView> list) {
        modelThuoc.setRowCount(0);
        for (ThuocTheoLoView item : list) {
            modelThuoc.addRow(new Object[]{
                    item.getMaThuoc(),
                    item.getTenThuoc(),
                    item.getMaLo(),
                    item.getHanSuDung() == null ? "" : item.getHanSuDung().format(dateFormatter),
                    item.getSoLuongLo(),
                    item.getDonVi()
            });
        }
    }

    private void filterThuocTheoLo() {
        String keyword = txtSearch.getText().trim().toLowerCase(Locale.ROOT);
        if (keyword.isBlank()) {
            loadThuocTable(dsThuocTheoLo);
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
        loadThuocTable(filtered);
    }

    private void themVaoGioHang() {
        int selectedRow = tblThuoc.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn thuốc/lô cần đặt", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int soLuongDat = (Integer) spnSoLuongThem.getValue();
        int soLuongLo = (Integer) modelThuoc.getValueAt(selectedRow, 4);
        if (soLuongDat <= 0) {
            JOptionPane.showMessageDialog(this, "Số lượng đặt phải lớn hơn 0", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String maThuoc = String.valueOf(modelThuoc.getValueAt(selectedRow, 0));
        String tenThuoc = String.valueOf(modelThuoc.getValueAt(selectedRow, 1));
        String maLo = String.valueOf(modelThuoc.getValueAt(selectedRow, 2));

        int existingRow = findCartRow(maThuoc, maLo);
        if (existingRow >= 0) {
            int current = Integer.parseInt(String.valueOf(modelGioHang.getValueAt(existingRow, 3)));
            if (current + soLuongDat > soLuongLo) {
                JOptionPane.showMessageDialog(this, "Tổng số lượng đặt vượt tồn của lô (" + soLuongLo + ")", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            modelGioHang.setValueAt(current + soLuongDat, existingRow, 3);
        } else {
            if (soLuongDat > soLuongLo) {
                JOptionPane.showMessageDialog(this, "Số lượng đặt vượt tồn của lô (" + soLuongLo + ")", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            modelGioHang.addRow(new Object[]{maThuoc, tenThuoc, maLo, soLuongDat});
        }
        capNhatThongKe();
    }

    private void xoaKhoiGioHang() {
        int selectedRow = tblGioHang.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn dòng cần giảm/xóa", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int soLuongXoa = (Integer) spnSoLuongXoa.getValue();
        int current = Integer.parseInt(String.valueOf(modelGioHang.getValueAt(selectedRow, 3)));
        if (soLuongXoa >= current) {
            modelGioHang.removeRow(selectedRow);
        } else {
            modelGioHang.setValueAt(current - soLuongXoa, selectedRow, 3);
        }
        capNhatThongKe();
    }

    private int findCartRow(String maThuoc, String maLo) {
        for (int i = 0; i < modelGioHang.getRowCount(); i++) {
            String eMaThuoc = String.valueOf(modelGioHang.getValueAt(i, 0));
            String eMaLo = String.valueOf(modelGioHang.getValueAt(i, 2));
            if (eMaThuoc.equals(maThuoc) && eMaLo.equals(maLo)) {
                return i;
            }
        }
        return -1;
    }

    private void timKhachHang() {
        String sdt = txtSdt.getText().trim();
        if (sdt.isBlank()) {
            khachHangDaChon = null;
            lblKhachHang.setText("Khách hàng: Khách vãng lai");
            return;
        }
        try {
            KhachHang khachHang = khachHangClientController.findByPhoneItem(sdt);
            if (khachHang == null) {
                khachHangDaChon = null;
                lblKhachHang.setText("Khách hàng: Không tìm thấy");
                JOptionPane.showMessageDialog(this, "Không tìm thấy khách hàng với số điện thoại đã nhập", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            khachHangDaChon = khachHang;
            lblKhachHang.setText("Khách hàng: " + khachHang.getTenKH() + " (" + khachHang.getMaKH() + ")");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tìm khách hàng: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xacNhanLapPhieuDat() {
        if (modelGioHang.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Phiếu đặt đang trống", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        PhieuDatCreatePayload payload = new PhieuDatCreatePayload();
        payload.setMaKhachHang(khachHangDaChon == null ? null : khachHangDaChon.getMaKH());
        payload.setGhiChu(txtGhiChu.getText() == null ? null : txtGhiChu.getText().trim());

        List<PhieuDatCreateItemPayload> items = new ArrayList<>();
        for (int i = 0; i < modelGioHang.getRowCount(); i++) {
            items.add(new PhieuDatCreateItemPayload(
                    String.valueOf(modelGioHang.getValueAt(i, 0)),
                    String.valueOf(modelGioHang.getValueAt(i, 2)),
                    Integer.parseInt(String.valueOf(modelGioHang.getValueAt(i, 3)))
            ));
        }
        payload.setItems(items);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Xác nhận lập phiếu đặt với " + items.size() + " dòng chi tiết?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION
        );
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            String maPhieuDat = phieuDatClientController.createPhieuDatItems(payload);
            JOptionPane.showMessageDialog(this, "Lập phiếu đặt thành công: " + maPhieuDat, "Thành công", JOptionPane.INFORMATION_MESSAGE);
            modelGioHang.setRowCount(0);
            txtGhiChu.setText("");
            capNhatThongKe();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi lập phiếu đặt: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void capNhatThongKe() {
        lblTongDong.setText("Số dòng: " + modelGioHang.getRowCount());
    }
}
