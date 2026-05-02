package com.pillchill.migration.gui.xuly;

import com.pillchill.migration.entity.KhachHang;
import com.pillchill.migration.entity.KhuyenMai;
import com.pillchill.migration.gui.HoaDonCallback;
import com.pillchill.migration.network.client.HoaDonClientController;
import com.pillchill.migration.network.client.KhachHangClientController;
import com.pillchill.migration.network.client.KhuyenMaiClientController;
import com.pillchill.migration.network.communication.HoaDonCreateItemPayload;
import com.pillchill.migration.network.communication.HoaDonCreatePayload;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class XacNhanLapHoaDonFrame extends JFrame implements ActionListener {
    private final Color PRIMARY_COLOR = new Color(0, 150, 136);
    private final Color BG_COLOR = Color.WHITE;
    private final Color BTN_ADD_COLOR = new Color(46, 204, 113);
    private final Color BTN_NEUTRAL_COLOR = new Color(240, 240, 240);

    private final HoaDonClientController hoaDonClientController;
    private final KhachHangClientController khachHangClientController;
    private final KhuyenMaiClientController khuyenMaiClientController;
    private final HoaDonCallback hoaDonCallback;

    private final List<HoaDonCreateItemPayload> dsChiTietData;
    private final String maHoaDon;
    private final String maNhanVien;
    private final double tongTien;

    private KhachHang khachHangHienTai;
    private KhuyenMai khuyenMaiApDung;
    private double tienGiam = 0;

    // Left Panel
    private JLabel lblTenKhachHang, lblDiemThanhVien, lblLichSuMua;
    private JTextField txtSDT, txtMaKhuyenMai, txtNhanVienLap, txtNgayMua;
    private JTextArea txtGhiChu;
    private JButton btnTimKhachHang, btnApDungKhuyenMai;
    private JButton btnQuayVe, btnXacNhan;

    // Table
    private JTable tblChiTiet;
    private DefaultTableModel modelChiTiet;

    // Right Panel - Payment info
    private JTextField txtTongTien, txtThue, txtTienGiam, txtTongCong, txtTienNhan, txtTienThua;

    private DecimalFormat df = new DecimalFormat("#,##0.00");

    public XacNhanLapHoaDonFrame(HoaDonClientController hoaDonClientController,
                                 KhachHangClientController khachHangClientController,
                                 KhuyenMaiClientController khuyenMaiClientController,
                                 List<HoaDonCreateItemPayload> dsChiTietData,
                                 String maHoaDon, String maNhanVien, double tongTien,
                                 HoaDonCallback hoaDonCallback) {
        this.hoaDonClientController = hoaDonClientController;
        this.khachHangClientController = khachHangClientController;
        this.khuyenMaiClientController = khuyenMaiClientController;
        this.hoaDonCallback = hoaDonCallback;
        this.dsChiTietData = dsChiTietData;
        this.maHoaDon = maHoaDon;
        this.maNhanVien = maNhanVien;
        this.tongTien = tongTien;

        setTitle("Xác Nhận Lập Hóa Đơn");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setSize(1200, 750);
        setResizable(false);
        getContentPane().setBackground(BG_COLOR);

        initializeUI();
        updatePaymentInfo();
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

    private void initializeUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(BG_COLOR);
        mainPanel.setBorder(new EmptyBorder(20, 40, 20, 40));

        JLabel lblTitle = new JLabel("XÁC NHẬN HÓA ĐƠN", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        mainPanel.add(lblTitle, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new BorderLayout(15, 15));
        contentPanel.setBackground(BG_COLOR);

        contentPanel.add(createLeftPanel(), BorderLayout.WEST);
        contentPanel.add(createCenterPanel(), BorderLayout.CENTER);
        contentPanel.add(createRightPanel(), BorderLayout.EAST);

        mainPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(createButtonPanel(), BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private JPanel createLeftPanel() {
        JPanel leftPanel = new JPanel(new GridBagLayout());
        leftPanel.setBackground(BG_COLOR);
        leftPanel.setBorder(new EmptyBorder(0, 0, 0, 10));
        leftPanel.setPreferredSize(new Dimension(300, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 0, 5, 0);

        // Customer section
        JLabel lblKhachHang = new JLabel("THÔNG TIN KHÁCH HÀNG");
        lblKhachHang.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblKhachHang.setForeground(PRIMARY_COLOR);
        leftPanel.add(lblKhachHang, gbc);

        gbc.gridy++;
        JPanel pnlSDT = new JPanel(new BorderLayout(5, 0));
        pnlSDT.setBackground(BG_COLOR);
        txtSDT = new JTextField();
        txtSDT.setPreferredSize(new Dimension(200, 32));
        btnTimKhachHang = createButton("Tìm", BTN_NEUTRAL_COLOR, Color.BLACK);
        btnTimKhachHang.addActionListener(this);
        pnlSDT.add(txtSDT, BorderLayout.CENTER);
        pnlSDT.add(btnTimKhachHang, BorderLayout.EAST);
        leftPanel.add(pnlSDT, gbc);

        gbc.gridy++;
        leftPanel.add(createLabel("Tên Khách Hàng:"), gbc);

        gbc.gridy++;
        lblTenKhachHang = createValueLabel("---");
        leftPanel.add(lblTenKhachHang, gbc);

        gbc.gridy++;
        leftPanel.add(createLabel("Điểm Thành Viên:"), gbc);

        gbc.gridy++;
        lblDiemThanhVien = createValueLabel("0");
        leftPanel.add(lblDiemThanhVien, gbc);

        gbc.gridy++;
        leftPanel.add(createLabel("Lịch Sử Mua:"), gbc);

        gbc.gridy++;
        lblLichSuMua = new JLabel("Chưa có");
        lblLichSuMua.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        leftPanel.add(lblLichSuMua, gbc);

        // Promotion section
        gbc.gridy++;
        gbc.insets = new Insets(20, 0, 5, 0);
        JLabel lblKhuyenMai = new JLabel("KHUYẾN MẠI");
        lblKhuyenMai.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblKhuyenMai.setForeground(PRIMARY_COLOR);
        leftPanel.add(lblKhuyenMai, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(5, 0, 5, 0);
        JPanel pnlKhuyenMai = new JPanel(new BorderLayout(5, 0));
        pnlKhuyenMai.setBackground(BG_COLOR);
        txtMaKhuyenMai = new JTextField();
        txtMaKhuyenMai.setPreferredSize(new Dimension(200, 32));
        btnApDungKhuyenMai = createButton("Áp Dụng", PRIMARY_COLOR, Color.WHITE);
        btnApDungKhuyenMai.addActionListener(this);
        pnlKhuyenMai.add(txtMaKhuyenMai, BorderLayout.CENTER);
        pnlKhuyenMai.add(btnApDungKhuyenMai, BorderLayout.EAST);
        leftPanel.add(pnlKhuyenMai, gbc);

        // Employee & Date
        gbc.gridy++;
        gbc.insets = new Insets(20, 0, 5, 0);
        leftPanel.add(createLabel("Nhân Viên Lập:"), gbc);

        gbc.gridy++;
        gbc.insets = new Insets(5, 0, 5, 0);
        txtNhanVienLap = new JTextField(maNhanVien);
        txtNhanVienLap.setPreferredSize(new Dimension(200, 32));
        txtNhanVienLap.setEditable(false);
        leftPanel.add(txtNhanVienLap, gbc);

        gbc.gridy++;
        leftPanel.add(createLabel("Ngày Mua:"), gbc);

        gbc.gridy++;
        txtNgayMua = new JTextField(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        txtNgayMua.setPreferredSize(new Dimension(200, 32));
        txtNgayMua.setEditable(false);
        leftPanel.add(txtNgayMua, gbc);

        // Notes
        gbc.gridy++;
        gbc.insets = new Insets(20, 0, 5, 0);
        leftPanel.add(createLabel("Ghi Chú:"), gbc);

        gbc.gridy++;
        gbc.insets = new Insets(5, 0, 5, 0);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        txtGhiChu = new JTextArea(3, 20);
        txtGhiChu.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtGhiChu.setLineWrap(true);
        txtGhiChu.setWrapStyleWord(true);
        leftPanel.add(new JScrollPane(txtGhiChu), gbc);

        // Filler
        gbc.gridy++;
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.weighty = 1.0;
        leftPanel.add(Box.createGlue(), gbc);

        return leftPanel;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return label;
    }

    private JLabel createValueLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        return label;
    }

    private JPanel createCenterPanel() {
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(BG_COLOR);

        // Table Header
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 10));
        headerPanel.setBackground(BG_COLOR);
        JLabel lblTitle = new JLabel("Chi Tiết Hóa Đơn");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        headerPanel.add(lblTitle);
        centerPanel.add(headerPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {"Mã Thuốc", "Tên Thuốc", "Mã Lô", "Số Lượng", "Đơn Giá", "Thành Tiền"};
        modelChiTiet = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblChiTiet = new JTable(modelChiTiet);
        tblChiTiet.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblChiTiet.setRowHeight(32); // Đồng bộ với LapHoaDonPanel
        tblChiTiet.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblChiTiet.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tblChiTiet.getTableHeader().setBackground(new Color(240, 240, 240));

        loadChiTietData();

        centerPanel.add(new JScrollPane(tblChiTiet), BorderLayout.CENTER);

        return centerPanel;
    }

    private JPanel createRightPanel() {
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(BG_COLOR);
        rightPanel.setBorder(new LineBorder(new Color(220, 220, 220), 1, true));
        rightPanel.setPreferredSize(new Dimension(280, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(15, 15, 5, 15);

        // Title
        JLabel lblThanhToan = new JLabel("THANH TOÁN");
        lblThanhToan.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblThanhToan.setForeground(PRIMARY_COLOR);
        rightPanel.add(lblThanhToan, gbc);

        // Tổng Tiền
        gbc.gridy++;
        rightPanel.add(createLabel("Tổng Tiền:"), gbc);

        gbc.gridy++;
        gbc.insets = new Insets(5, 15, 10, 15);
        txtTongTien = createPaymentField();
        rightPanel.add(txtTongTien, gbc);

        // Thuế
        gbc.gridy++;
        gbc.insets = new Insets(5, 15, 5, 15);
        rightPanel.add(createLabel("Thuế (10%):"), gbc);

        gbc.gridy++;
        txtThue = createPaymentField();
        rightPanel.add(txtThue, gbc);

        // Tiền Giảm
        gbc.gridy++;
        rightPanel.add(createLabel("Tiền Giảm:"), gbc);

        gbc.gridy++;
        txtTienGiam = createPaymentField();
        rightPanel.add(txtTienGiam, gbc);

        // Tổng Cộng
        gbc.gridy++;
        gbc.insets = new Insets(15, 15, 5, 15);
        JLabel lblTongCong = new JLabel("TỔNG CỘNG:");
        lblTongCong.setFont(new Font("Segoe UI", Font.BOLD, 14));
        rightPanel.add(lblTongCong, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(5, 15, 10, 15);
        txtTongCong = createPaymentField();
        txtTongCong.setFont(new Font("Segoe UI", Font.BOLD, 16));
        txtTongCong.setBackground(new Color(255, 250, 210));
        rightPanel.add(txtTongCong, gbc);

        // Tiền Nhận
        gbc.gridy++;
        gbc.insets = new Insets(15, 15, 5, 15);
        rightPanel.add(createLabel("Tiền Nhận:"), gbc);

        gbc.gridy++;
        gbc.insets = new Insets(5, 15, 10, 15);
        txtTienNhan = new JTextField();
        txtTienNhan.setPreferredSize(new Dimension(200, 32));
        txtTienNhan.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtTienNhan.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { updateTienThua(); }
            @Override
            public void removeUpdate(DocumentEvent e) { updateTienThua(); }
            @Override
            public void changedUpdate(DocumentEvent e) { updateTienThua(); }
        });
        rightPanel.add(txtTienNhan, gbc);

        // Tiền Thừa
        gbc.gridy++;
        rightPanel.add(createLabel("Tiền Thừa:"), gbc);

        gbc.gridy++;
        txtTienThua = createPaymentField();
        txtTienThua.setFont(new Font("Segoe UI", Font.BOLD, 14));
        txtTienThua.setBackground(new Color(230, 255, 230));
        rightPanel.add(txtTienThua, gbc);

        // Filler
        gbc.gridy++;
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.weighty = 1.0;
        rightPanel.add(Box.createGlue(), gbc);

        return rightPanel;
    }

    private JTextField createPaymentField() {
        JTextField field = new JTextField();
        field.setEditable(false);
        field.setPreferredSize(new Dimension(200, 32));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return field;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        buttonPanel.setBackground(BG_COLOR);

        btnQuayVe = createButton("Hủy / Quay Về", BTN_NEUTRAL_COLOR, Color.BLACK);
        btnQuayVe.addActionListener(this);
        buttonPanel.add(btnQuayVe);

        btnXacNhan = createButton("Hoàn Tất Hóa Đơn", BTN_ADD_COLOR, Color.WHITE);
        btnXacNhan.addActionListener(this);
        buttonPanel.add(btnXacNhan);

        return buttonPanel;
    }

    private void loadChiTietData() {
        for (HoaDonCreateItemPayload item : dsChiTietData) {
            double thanhTien = item.getSoLuong() * item.getDonGia();
            modelChiTiet.addRow(new Object[]{
                    item.getMaThuoc(),
                    item.getTenThuoc(),
                    item.getMaLo(),
                    item.getSoLuong(),
                    String.format("%.0f", item.getDonGia()),
                    String.format("%.0f", thanhTien)
            });
        }
    }

    private void updatePaymentInfo() {
        double thue = tongTien * 0.1d;
        double tongCong = tongTien + thue - tienGiam;

        txtTongTien.setText(df.format(tongTien));
        txtThue.setText(df.format(thue));
        txtTienGiam.setText(df.format(tienGiam));
        txtTongCong.setText(df.format(tongCong));
        txtTienThua.setText("0.00");
    }

    private void updateTienThua() {
        try {
            String tienNhanStr = txtTienNhan.getText().trim();
            if (tienNhanStr.isEmpty()) {
                txtTienThua.setText("0.00");
                return;
            }

            double tienNhan = Double.parseDouble(tienNhanStr);
            double thue = tongTien * 0.1d;
            double tongCong = tongTien + thue - tienGiam;
            double tienThua = tienNhan - tongCong;

            txtTienThua.setText(df.format(tienThua));
        } catch (NumberFormatException e) {
            txtTienThua.setText("0.00");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnQuayVe) {
            dispose();
        } else if (e.getSource() == btnTimKhachHang) {
            timKhachHang();
        } else if (e.getSource() == btnApDungKhuyenMai) {
            apDungKhuyenMai();
        } else if (e.getSource() == btnXacNhan) {
            xacNhanHoaDon();
        }
    }

    private void timKhachHang() {
        String sdt = txtSDT.getText().trim();
        if (sdt.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số điện thoại!", "Thông Báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            khachHangHienTai = khachHangClientController.findByPhoneItem(sdt);
            if (khachHangHienTai != null) {
                lblTenKhachHang.setText(khachHangHienTai.getTenKH());
                lblDiemThanhVien.setText(String.valueOf(khachHangHienTai.getDiemTichLuy() != null ? khachHangHienTai.getDiemTichLuy() : 0));
                lblLichSuMua.setText("Đã thêm vào hóa đơn");
                JOptionPane.showMessageDialog(this, "Tìm thấy khách hàng: " + khachHangHienTai.getTenKH(), "Thành Công", JOptionPane.INFORMATION_MESSAGE);
            } else {
                int result = JOptionPane.showConfirmDialog(this,
                        "Không tìm thấy khách hàng!\n\nBạn có muốn thêm khách hàng mới không?",
                        "Xác Nhận",
                        JOptionPane.YES_NO_OPTION);

                if (result == JOptionPane.YES_OPTION) {
                    themKhachHangMoi(sdt);
                } else {
                    lblTenKhachHang.setText("---");
                    lblDiemThanhVien.setText("0");
                    lblLichSuMua.setText("Chưa có");
                    khachHangHienTai = null;
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tìm khách hàng: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void themKhachHangMoi(String sdt) {
        JDialog dialog = new JDialog(this, "Thêm Khách Hàng Mới", true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setSize(450, 350);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BG_COLOR);
        panel.setBorder(new EmptyBorder(15, 20, 15, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 5, 0);
        gbc.gridx = 0;

        // Mã Khách Hàng (auto-generated)
        gbc.gridy = 0;
        panel.add(createLabel("Mã Khách Hàng:"), gbc);
        gbc.gridy++;
        gbc.insets = new Insets(5, 0, 5, 0);
        JTextField txtMaKH = new JTextField();
        txtMaKH.setText("KH_" + System.currentTimeMillis());
        txtMaKH.setEditable(false);
        txtMaKH.setPreferredSize(new Dimension(200, 32));
        panel.add(txtMaKH, gbc);

        // Số Điện Thoại
        gbc.gridy++;
        gbc.insets = new Insets(10, 0, 5, 0);
        panel.add(createLabel("Số Điện Thoại:"), gbc);
        gbc.gridy++;
        gbc.insets = new Insets(5, 0, 5, 0);
        JTextField txtSDTKH = new JTextField(sdt);
        txtSDTKH.setPreferredSize(new Dimension(200, 32));
        panel.add(txtSDTKH, gbc);

        // Tên Khách Hàng
        gbc.gridy++;
        gbc.insets = new Insets(10, 0, 5, 0);
        panel.add(createLabel("Tên Khách Hàng:"), gbc);
        gbc.gridy++;
        gbc.insets = new Insets(5, 0, 15, 0);
        JTextField txtTenKH = new JTextField();
        txtTenKH.setPreferredSize(new Dimension(200, 32));
        panel.add(txtTenKH, gbc);

        // Buttons
        gbc.gridy++;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        btnPanel.setBackground(BG_COLOR);

        JButton btnSave = createButton("Lưu", BTN_ADD_COLOR, Color.WHITE);
        btnSave.addActionListener(e -> {
            String tenKH = txtTenKH.getText().trim();

            if (tenKH.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng nhập tên khách hàng!", "Thông Báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                // Create and save new customer
                KhachHang newKH = new KhachHang();
                newKH.setMaKH(txtMaKH.getText());
                newKH.setSoDienThoai(txtSDTKH.getText().trim());
                newKH.setTenKH(tenKH);
                newKH.setDiemTichLuy(0);
                newKH.setActive(true);

                // Save via controller
                khachHangClientController.createItem(newKH);

                // Set customer for current invoice
                khachHangHienTai = newKH;
                lblTenKhachHang.setText(newKH.getTenKH());
                lblDiemThanhVien.setText("0");
                lblLichSuMua.setText("Khách hàng mới");

                JOptionPane.showMessageDialog(dialog, "Thêm khách hàng thành công!", "Thành Công", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Lỗi thêm khách hàng: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
        btnPanel.add(btnSave);

        JButton btnCancel = createButton("Hủy", BTN_NEUTRAL_COLOR, Color.BLACK);
        btnCancel.addActionListener(e -> dialog.dispose());
        btnPanel.add(btnCancel);

        panel.add(btnPanel, gbc);

        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }

    private void apDungKhuyenMai() {
        String maKhuyenMai = txtMaKhuyenMai.getText().trim();
        if (maKhuyenMai.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập mã khuyến mại!", "Thông Báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
//            khuyenMaiApDung = khuyenMaiClientController.findByMaItem(maKhuyenMai);
            if (khuyenMaiApDung != null) {
                // Validate if promotion is effective
                if (!isPromotionValid(khuyenMaiApDung)) {
                    JOptionPane.showMessageDialog(this, "Khuyến mại này không còn hiệu lực!", "Thông Báo", JOptionPane.WARNING_MESSAGE);
                    khuyenMaiApDung = null;
                    tienGiam = 0;
                    return;
                }

                // Calculate discount
                tienGiam = tongTien * (khuyenMaiApDung.getMucGiamGia() / 100.0d);
                updatePaymentInfo();
                JOptionPane.showMessageDialog(this, "Áp dụng khuyến mại thành công!\nGiảm: " + df.format(tienGiam), "Thành Công", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Không tìm thấy khuyến mại!", "Thông Báo", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi áp dụng khuyến mại: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean isPromotionValid(KhuyenMai khuyenMai) {
        LocalDate today = LocalDate.now();
        LocalDate ngayApDung = khuyenMai.getNgayApDung();
        LocalDate ngayKetThuc = khuyenMai.getNgayKetThuc();

        if (ngayApDung != null && today.isBefore(ngayApDung)) {
            return false;
        }

        if (ngayKetThuc != null && today.isAfter(ngayKetThuc)) {
            return false;
        }

        return true;
    }

    private void xacNhanHoaDon() {
        String tienNhanStr = txtTienNhan.getText().trim();
        if (tienNhanStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tiền nhận!", "Thông Báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            double tienNhan = Double.parseDouble(tienNhanStr);
            double thue = tongTien * 0.1d;
            double tongCong = tongTien + thue - tienGiam;
            double tienThua = tienNhan - tongCong;

            if (tienThua < 0) {
                JOptionPane.showMessageDialog(this, "Tiền nhận không đủ!", "Thông Báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Create payload
            HoaDonCreatePayload payload = new HoaDonCreatePayload();
            payload.setMaHoaDon(maHoaDon);

            payload.setMaKhachHang(khachHangHienTai != null ? khachHangHienTai.getMaKH() : null);
            payload.setMaKhuyenMai(khuyenMaiApDung != null ? khuyenMaiApDung.getMaKM() : null);
            payload.setGhiChu(txtGhiChu.getText().trim());
            payload.setItems(dsChiTietData);

            // Save to server
            hoaDonClientController.createHoaDon(payload);

            JOptionPane.showMessageDialog(this, "Lập hóa đơn thành công!", "Thông Báo", JOptionPane.INFORMATION_MESSAGE);

            if (hoaDonCallback != null) {
                hoaDonCallback.onHoaDonSuccess(maHoaDon);
            }

            dispose();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Tiền nhận phải là số!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi lập hóa đơn: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}