package com.pillchill.migration.gui.capnhat;

import com.pillchill.migration.dto.BangGiaDTO;
import com.pillchill.migration.entity.BangGia;
import com.pillchill.migration.entity.KhuyenMai;
import com.pillchill.migration.network.client.BangGiaClientController;
import com.pillchill.migration.network.client.KhuyenMaiClientController;
import com.pillchill.migration.network.communication.BangGiaPayLoad;
import com.pillchill.migration.network.communication.KhachHangPayload;
import com.pillchill.migration.network.communication.Response;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CapNhatBangGiaPanel extends JPanel implements ActionListener, MouseListener {
    private final BangGiaClientController bangGiaClientController;

    private final Color PRIMARY_COLOR = new Color(0, 150, 136);
    private final Color ACCENT_COLOR = new Color(255, 255, 255);
    private final Color BG_COLOR = new Color(245, 245, 245);

    private final Color BTN_ADD_COLOR = new Color(46, 204, 113);
    private final Color BTN_EDIT_COLOR = new Color(241, 196, 15);
    private final Color BTN_DELETE_COLOR = new Color(231, 76, 60);
    private final Color BTN_CLEAR_COLOR = new Color(149, 165, 166);
    private final Color BTN_REFRESH_COLOR = new Color(57, 155, 226);
    private final Color BTN_EDIT_DETAIL_COLOR = new Color(57, 155, 226);

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private JLabel lblTieuDe;
    private JLabel lblngayApDung;
    private JLabel lblNgayKetThuc;
    private JLabel lblMaBangGia;
    private JLabel lblTenBangGia;
    private JLabel lblGhiChu;
    private JLabel lblDoUuTien;
    private JLabel lblLoaiGia;
    private JLabel lblTrangThai;

    private JTextField txtMaBangGia;
    private JTextField txtTenBangGia;
    private JTextField txtGhiChu;
    private JTextField txtDoUuTien;
    private JComboBox<String> cboTrangThai;
    private JTextField txtLoaiBangGia;

    private JDateChooser calNgayApDung;
    private JDateChooser calNgayKetThuc;

    private JButton btnXoa;
    private JButton btnSua;
    private JButton btnSuaChiTiet;
    private JButton btnThem;
    private JButton btnXoaTrang;
    private JButton btnKhuyenMaiDaXoa;
    private JButton btnXemChiTiet;

    private CardLayout cardLayout;
    private JPanel mainContainer;
    private DefaultTableModel dtm;
    private JTable tblBangGia;

    Date today;

    private ArrayList<BangGiaDTO> dsBangGia;

    public CapNhatBangGiaPanel(BangGiaClientController bangGiaClientController) {
        this.bangGiaClientController = bangGiaClientController;
        this.dsBangGia = new ArrayList<>();

        setLayout(new BorderLayout());

        initHeader();
        initInputForm();
        initButtons();

        String[] cols = {"Mã bảng giá", "Tên bảng giá", "Loại bảng giá", "Độ Ưu Tiên", "Ngày áp dụng", "Ngày kết thúc", "Trạng thái", "Ghi chú"};
        dtm = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JPanel pnlTop = new JPanel(new BorderLayout(0, 15));
        pnlTop.setBackground(BG_COLOR);
        pnlTop.add(lblTieuDe, BorderLayout.NORTH);
        pnlTop.add(createInputPanel(), BorderLayout.CENTER);
        pnlTop.add(createButtonPanel(), BorderLayout.SOUTH);

        JPanel pnlMain = new JPanel(new BorderLayout(10, 10));
        pnlMain.setBackground(BG_COLOR);
        pnlMain.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        pnlMain.add(pnlTop, BorderLayout.NORTH);
        pnlMain.add(createBotPanel(), BorderLayout.CENTER);

        add(pnlMain, BorderLayout.CENTER);

        xoaTrang();
//        loadBangGiaData();
    }

    private void initHeader() {
        lblTieuDe = new JLabel("QUẢN LÝ BẢNG GIÁ", SwingConstants.CENTER);
        lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTieuDe.setForeground(PRIMARY_COLOR);
        lblTieuDe.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
    }

    private void initInputForm() {
        Font fontLabel = new Font("Segoe UI", Font.BOLD, 14);
        Font fontText = new Font("Segoe UI", Font.PLAIN, 14);

        lblMaBangGia = new JLabel("Mã bảng giá:");
        lblMaBangGia.setFont(fontLabel);

        lblTenBangGia = new JLabel("Tên bảng giá:");
        lblTenBangGia.setFont(fontLabel);

        lblGhiChu = new JLabel("Ghi chú:");
        lblGhiChu.setFont(fontLabel);

        lblDoUuTien = new JLabel("Độ Ưu Tiên");
        lblDoUuTien.setFont(fontLabel);

        lblngayApDung = new JLabel("Ngày áp dụng (dd/MM/yyyy):");
        lblngayApDung.setFont(fontLabel);

        lblNgayKetThuc = new JLabel("Ngày kết thúc (dd/MM/yyyy):");
        lblNgayKetThuc. setFont(fontLabel);

        lblLoaiGia = new JLabel("Loại bảng giá:");
        lblLoaiGia.setFont(fontLabel);

        lblTrangThai = new JLabel("Trạng thái:");
        lblTrangThai.setFont(fontLabel);

        txtMaBangGia = new JTextField();
        txtMaBangGia.setFont(fontText);
        txtMaBangGia.setPreferredSize(new Dimension(200, 35));

        txtTenBangGia = new JTextField();
        txtTenBangGia.setFont(fontText);
        txtTenBangGia.setPreferredSize(new Dimension(200, 35));

        txtGhiChu = new JTextField();
        txtGhiChu.setFont(fontText);
        txtGhiChu.setPreferredSize(new Dimension(200, 35));

        txtDoUuTien = new JTextField("0");
        txtDoUuTien.setFont(fontText);
        txtDoUuTien.setPreferredSize(new Dimension(200, 35));

        cboTrangThai = new JComboBox<String>();
        cboTrangThai.addItem("Chưa áp dụng");
        cboTrangThai.addItem("Đang áp dụng");
        cboTrangThai.addItem("Đã kết thúc");
        cboTrangThai.setSelectedIndex(0);
        cboTrangThai.setPreferredSize(new Dimension(200, 35));

        txtLoaiBangGia = new JTextField();
        txtLoaiBangGia.setFont(fontText);
        txtLoaiBangGia.setPreferredSize(new Dimension(200, 35));

        calNgayApDung = new JDateChooser();
        calNgayApDung.setDateFormatString("dd/MM/yyyy");
        calNgayApDung.setFont(fontText);
        calNgayApDung.setPreferredSize(new Dimension(200, 35));

        calNgayKetThuc = new JDateChooser();
        calNgayKetThuc.setDateFormatString("dd/MM/yyyy");
        calNgayKetThuc.setFont(fontText);
        calNgayKetThuc.setPreferredSize(new Dimension(200, 35));
    }

    private JPanel createInputPanel() {
        JPanel pnlForm = new JPanel(new GridBagLayout());
        pnlForm.setBackground(ACCENT_COLOR);
        pnlForm.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(20, 50, 20, 50)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints. HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 20);

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.1;
        pnlForm.add(lblMaBangGia, gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc. weightx = 0.4;
        pnlForm. add(txtMaBangGia, gbc);

        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 0.1;
        pnlForm.add(lblTenBangGia, gbc);
        gbc.gridx = 3; gbc.gridy = 0; gbc.weightx = 0.4;
        pnlForm.add(txtTenBangGia, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.1;
        pnlForm.add(lblngayApDung, gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc. weightx = 0.4;
        pnlForm.add(calNgayApDung, gbc);

        gbc.gridx = 2; gbc.gridy = 1; gbc.weightx = 0.1;
        pnlForm.add(lblNgayKetThuc, gbc);
        gbc.gridx = 3; gbc.gridy = 1; gbc.weightx = 0.4;
        pnlForm.add(calNgayKetThuc, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.1;
        pnlForm.add(lblDoUuTien, gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc. weightx = 0.4;
        pnlForm. add(txtDoUuTien, gbc);

        gbc.gridx = 2; gbc.gridy = 2; gbc.weightx = 0.1;
        pnlForm.add(lblGhiChu, gbc);
        gbc.gridx = 3; gbc.gridy = 2; gbc.weightx = 0.4;
        pnlForm.add(txtGhiChu, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.1;
        pnlForm.add(lblLoaiGia, gbc);
        gbc.gridx = 1; gbc.gridy = 3; gbc.weightx = 0.4;
        pnlForm.add(txtLoaiBangGia, gbc);

        gbc.gridx = 2; gbc.gridy = 3; gbc.weightx = 0.1;
        pnlForm.add(lblTrangThai, gbc);
        gbc.gridx = 3; gbc.gridy = 3; gbc.weightx = 0.4;
        pnlForm.add(cboTrangThai,gbc);

        return pnlForm;
    }

    private void initButtons() {
        btnThem = createStyledButton("Thêm", BTN_ADD_COLOR);
        btnSua = createStyledButton("Sửa", BTN_EDIT_COLOR);
        btnSuaChiTiet = createStyledButton("Sửa chi tiết", BTN_EDIT_DETAIL_COLOR);
        btnXoa = createStyledButton("Xóa", BTN_DELETE_COLOR);
        btnXoaTrang = createStyledButton("Xóa trắng", BTN_CLEAR_COLOR);
        btnKhuyenMaiDaXoa = createStyledButton("Bảng giá đã xóa", Color.pink);
        btnKhuyenMaiDaXoa.setPreferredSize(new Dimension(180, 45));
        btnXemChiTiet = createStyledButton("Xem chi tiết", BTN_ADD_COLOR);

        btnThem.addActionListener(this);
        btnSua.addActionListener(this);
        btnSuaChiTiet.addActionListener(this);
        btnXoa.addActionListener(this);
        btnXoaTrang.addActionListener(this);
        btnKhuyenMaiDaXoa.addActionListener(this);
        btnXemChiTiet.addActionListener(this);
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(130, 45));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JPanel createButtonPanel() {
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        pnlButtons.setBackground(BG_COLOR);
        pnlButtons.add(btnThem);
        pnlButtons.add(btnSua);
        pnlButtons.add(btnSuaChiTiet);
        pnlButtons.add(btnXoa);
        pnlButtons.add(btnXoaTrang);
        pnlButtons.add(btnKhuyenMaiDaXoa);
        pnlButtons.add(btnXemChiTiet);
        return pnlButtons;
    }

    private JScrollPane createBotPanel() {
        tblBangGia = new JTable(dtm) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(242, 242, 242));
                }
                return c;
            }
        };

        tblBangGia.setRowHeight(35);
        tblBangGia.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tblBangGia.setFillsViewportHeight(true);
        tblBangGia.setShowGrid(true);
        tblBangGia.setGridColor(new Color(224, 224, 224));
        tblBangGia.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblBangGia.setSelectionBackground(new Color(178, 223, 219));
        tblBangGia.setSelectionForeground(Color.BLACK);

        JTableHeader header = tblBangGia.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 40));
        header.setReorderingAllowed(false);

        DefaultTableCellRenderer centerRenderer = (DefaultTableCellRenderer) header.getDefaultRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        DefaultTableCellRenderer cellCenter = new DefaultTableCellRenderer();
        cellCenter.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < tblBangGia.getColumnCount(); i++) {
            tblBangGia.getColumnModel().getColumn(i).setCellRenderer(cellCenter);
        }

        tblBangGia.addMouseListener(this);

        JScrollPane scrollPane = new JScrollPane(tblBangGia);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        scrollPane.getViewport().setBackground(Color.WHITE);
        return scrollPane;
    }

    private void loadBangGiaData() {
        SwingWorker<List<BangGiaDTO>, Void> worker = new SwingWorker<List<BangGiaDTO>, Void>() {
            @Override
            protected List<BangGiaDTO> doInBackground() {
                return bangGiaClientController.getAllBangGiaItems();
            }

            @Override
            protected void done() {
                try {
                    List<BangGiaDTO> items = get();
                    dsBangGia = new ArrayList<>(items);
                    dtm.setRowCount(0);
                    for (BangGiaDTO item : items) {
                        dtm.addRow(new Object[]{
                                item.getMaBangGia(),
                                item.getTenBangGia(),
                                item.getLoaiGia(),
                                item.getDoUuTien(),
                                formatDate(item.getNgayApDung()),
                                formatDate(item.getNgayKetThuc()),
                                item.getTrangThai(),
                                item.getGhiChu()
                        });
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(
                            CapNhatBangGiaPanel.this,
                            "Tải danh sách bảng giá thất bại: " + e.getMessage(),
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };
        worker.execute();
    }

    private String formatDate(LocalDate date) {
        if (date == null) {
            return "";
        }
        return date.format(DATE_FORMATTER);
    }

    private LocalDate parseDate(String rawDate, String fieldName) {
        try {
            return LocalDate.parse(rawDate, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(fieldName + " không đúng định dạng dd/MM/yyyy");
        }
    }

    private void xoaTrang() {
        txtMaBangGia.setText("");
        txtTenBangGia.setText("");
        calNgayApDung.setDate(today);
        calNgayKetThuc.setDate(today);
        txtDoUuTien.setText("0");
        txtGhiChu.setText("");
        txtLoaiBangGia.setText("");
        txtMaBangGia.setEnabled(true);
        tblBangGia. clearSelection();
        loadBangGiaData();
    }

    public void quayLaiDanhSach() {
        cardLayout.show(mainContainer, "DanhSach");
        loadBangGiaData();
    }

    private boolean validateInput(boolean isAddingNew) {
        if (txtMaBangGia.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mã bảng giá không được để trống!");
            txtMaBangGia.requestFocus();
            return false;
        }
        if (txtTenBangGia.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên bảng giá không được để trống!");
            txtTenBangGia.requestFocus();
            return false;
        }
        if (txtLoaiBangGia.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Loại bảng giá không được để trống!");
            txtLoaiBangGia.requestFocus();
            return false;
        }

        String maBangGia = txtMaBangGia.getText().trim();
        if(isAddingNew) {
            for(BangGiaDTO item : dsBangGia) {
                if(item.getMaBangGia().equalsIgnoreCase(maBangGia)) {
                    JOptionPane.showMessageDialog(this, "Mã bảng giá không được trùng!");
                    txtMaBangGia.requestFocus();
                    return false;
                }
            }
        }
        if (! maBangGia.matches("BG\\d{3}")) {
            JOptionPane.showMessageDialog(this, "Mã bảng giá phải có định dạng KM kèm 3 ký số (Ví dụ: BG001)!");
            txtMaBangGia.requestFocus();
            return false;
        }

        String tenBangGia = txtTenBangGia.getText();
        if (tenBangGia.length() > 50 && !tenBangGia.trim().equalsIgnoreCase("")) {
            JOptionPane.showMessageDialog(this, "Tên bảng giá chỉ được nhập <50 ký tự!");
            txtTenBangGia.requestFocus();
            return false;
        }

        String loaiBangGia = txtLoaiBangGia.getText();
        if (loaiBangGia.length() > 50 && !loaiBangGia.trim().equalsIgnoreCase("")) {
            JOptionPane.showMessageDialog(this, "Loại bảng giá chỉ được nhập <50 ký tự!");
            txtLoaiBangGia.requestFocus();
            return false;
        }

        try {
            int doUuTien = Integer.parseInt(txtDoUuTien.getText().trim());
            if (doUuTien < 0 || doUuTien > 10) {
                JOptionPane.showMessageDialog(this, "Độ ưu tiên phải là số nguyên không âm và thuộc [0,10]!");
                txtDoUuTien.requestFocus();
                return false;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Độ ưu tiên phải là số nguyên!");
            txtDoUuTien.requestFocus();
            return false;
        }

        Date apDung = calNgayApDung.getDate();
        Date ketThuc = calNgayKetThuc.getDate();

        if(apDung == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn ngày áp dụng!");
            calNgayApDung.requestFocus();
            return false;
        }
        if(ketThuc == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn ngày kết thúc!");
            calNgayKetThuc.requestFocus();
            return false;
        }

        if (apDung.before(today)) {
            JOptionPane.showMessageDialog(this, "Ngày áp dụng phải là hôm nay hoặc sau hôm nay!");
            calNgayApDung.requestFocus();
            return false;
        }
        if (ketThuc.before(today)) {
            JOptionPane.showMessageDialog(this, "Ngày kết thúc phải là hôm nay hoặc sau hôm nay!");
            calNgayKetThuc.requestFocus();
            return false;
        }
        if (ketThuc.before(apDung)) {
            JOptionPane.showMessageDialog(this, "Ngày kết thúc phải bằng hoặc sau ngày áp dụng!");
            calNgayKetThuc.requestFocus();
            return false;
        }

        String ghiChu = txtGhiChu.getText();
        if (ghiChu.length() > 50 && !ghiChu.trim().equalsIgnoreCase("")) {
            JOptionPane.showMessageDialog(this, "Ghi chú chỉ được nhập <50 ký tự!");
            txtGhiChu.requestFocus();
            return false;
        }
        return true;
    }

    private LocalDate transformDateToLocalDate(Date date){
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    private void themBangGia() {
        if (!validateInput(true)) {
            return;
        }

        BangGiaPayLoad payload = new BangGiaPayLoad(
                txtMaBangGia.getText().trim(),
                txtTenBangGia.getText().trim(),
                txtLoaiBangGia.getText().trim(),
                transformDateToLocalDate(calNgayApDung.getDate()),
                transformDateToLocalDate(calNgayKetThuc.getDate()),
                cboTrangThai.getSelectedItem().toString(),
                txtGhiChu.getText(),
                Integer.parseInt(txtDoUuTien.getText()),
                true
        );
        SwingWorker<Response, Void> worker = new SwingWorker<Response, Void>() {
            @Override
            protected Response doInBackground() {
                return bangGiaClientController.createBangGia(payload);
            }

            @Override
            protected void done() {
                try {
                    Response response = get();
                    if (response.isSuccess()) {
                        JOptionPane.showMessageDialog(CapNhatBangGiaPanel.this, "Thêm bảng giá thành công!");
                        xoaTrang();
                        loadBangGiaData();
                        return;
                    }
                    JOptionPane.showMessageDialog(CapNhatBangGiaPanel.this, response.getMessage());
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(CapNhatBangGiaPanel.this, "Thêm bảng giá thất bại: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void suaBangGia() {
        if (!validateInput(false)) {
            return;
        }

        BangGiaPayLoad payload = new BangGiaPayLoad(
                txtMaBangGia.getText().trim(),
                txtTenBangGia.getText().trim(),
                txtLoaiBangGia.getText().trim(),
                transformDateToLocalDate(calNgayApDung.getDate()),
                transformDateToLocalDate(calNgayKetThuc.getDate()),
                cboTrangThai.getSelectedItem().toString(),
                txtGhiChu.getText(),
                Integer.parseInt(txtDoUuTien.getText()),
                true
        );
        SwingWorker<Response, Void> worker = new SwingWorker<Response, Void>() {
            @Override
            protected Response doInBackground() {
                return bangGiaClientController.updateBangGia(payload);
            }

            @Override
            protected void done() {
                try {
                    Response response = get();
                    if (response.isSuccess()) {
                        JOptionPane.showMessageDialog(CapNhatBangGiaPanel.this, "Cập nhật bảng giá thành công!");
                        xoaTrang();
                        loadBangGiaData();
                        return;
                    }
                    JOptionPane.showMessageDialog(CapNhatBangGiaPanel.this, response.getMessage());
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(CapNhatBangGiaPanel.this, "Cập nhật bảng giá thất bại: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void xoaBangGia() {
        int selectedRow = tblBangGia.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn bảng giá cần xóa!");
            return;
        }

        String maBG = tblBangGia.getValueAt(selectedRow, 0).toString();
        int option = JOptionPane.showConfirmDialog(
                this,
                "Có chắc muốn xóa bảng giá " + maBG + " không?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION
        );
        if (option != JOptionPane.YES_OPTION) {
            return;
        }

        SwingWorker<Response, Void> worker = new SwingWorker<Response, Void>() {
            @Override
            protected Response doInBackground() {
                return bangGiaClientController.deleteBangGia(maBG);
            }

            @Override
            protected void done() {
                try {
                    Response response = get();
                    if (response.isSuccess()) {
                        JOptionPane.showMessageDialog(CapNhatBangGiaPanel.this, "Xóa bảng giá thành công!");
                        xoaTrang();
                        loadBangGiaData();
                        return;
                    }
                    JOptionPane.showMessageDialog(CapNhatBangGiaPanel.this, response.getMessage());
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(CapNhatBangGiaPanel.this, "Xóa bảng giá thất bại: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        if (o == btnThem) {
            themBangGia();
        } else if (o == btnSua) {
            suaBangGia();
        } else if (o == btnXoa) {
            xoaBangGia();
        } else if (o == btnXoaTrang) {
            xoaTrang();
        }
//        else if (o == btnLamMoi) {
//            xoaTrang();
//            loadKhuyenMaiData();
//        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        Object o = e.getSource();
        if(o == tblBangGia) {
            int row = tblBangGia.getSelectedRow();
            if (row >= 0) {
                txtMaBangGia.setText(tblBangGia. getValueAt(row, 0).toString());
                txtTenBangGia.setText(tblBangGia.getValueAt(row, 1).toString());
                txtLoaiBangGia.setText(tblBangGia. getValueAt(row, 2).toString());
                txtDoUuTien.setText(tblBangGia. getValueAt(row, 3).toString());
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    String dateApDungString = tblBangGia.getValueAt(row, 4).toString();
                    String dateKetThucString = tblBangGia. getValueAt(row, 5).toString();
                    calNgayApDung.setDate(sdf.parse(dateApDungString));
                    calNgayKetThuc.setDate(sdf.parse(dateKetThucString));
                } catch (Exception e2) {
                    e2.printStackTrace();
                }

                cboTrangThai.setSelectedItem(tblBangGia. getValueAt(row, 6).toString());
                txtGhiChu.setText(tblBangGia. getValueAt(row, 7).toString());

                txtMaBangGia.setEnabled(false);
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

}
