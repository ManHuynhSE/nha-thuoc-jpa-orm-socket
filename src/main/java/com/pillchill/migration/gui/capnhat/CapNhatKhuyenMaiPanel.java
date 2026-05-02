package com.pillchill.migration.gui.capnhat;

import com.pillchill.migration.entity.KhuyenMai;
import com.pillchill.migration.network.client.KhuyenMaiClientController;
import com.pillchill.migration.network.communication.Response;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class CapNhatKhuyenMaiPanel extends JPanel implements ActionListener, MouseListener {

    private final KhuyenMaiClientController khuyenMaiClientController;

    private final Color PRIMARY_COLOR = new Color(0, 150, 136);
    private final Color ACCENT_COLOR = new Color(255, 255, 255);
    private final Color BG_COLOR = new Color(245, 245, 245);

    private final Color BTN_ADD_COLOR = new Color(46, 204, 113);
    private final Color BTN_EDIT_COLOR = new Color(241, 196, 15);
    private final Color BTN_DELETE_COLOR = new Color(231, 76, 60);
    private final Color BTN_CLEAR_COLOR = new Color(149, 165, 166);
    private final Color BTN_REFRESH_COLOR = new Color(57, 155, 226);

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private JLabel lblTieuDe;
    private JLabel lblMaKM;
    private JLabel lblMucGiam;
    private JLabel lblNgayApDung;
    private JLabel lblNgayKetThuc;

    private JTextField txtMaKM;
    private JTextField txtMucGiam;
    private JTextField txtNgayApDung;
    private JTextField txtNgayKetThuc;

    private JButton btnXoa;
    private JButton btnSua;
    private JButton btnThem;
    private JButton btnXoaTrang;
    private JButton btnLamMoi;

    private DefaultTableModel dtm;
    private JTable tblKhuyenMai;

    private ArrayList<KhuyenMai> dsKhuyenMai;

    public CapNhatKhuyenMaiPanel(KhuyenMaiClientController khuyenMaiClientController) {
        this.khuyenMaiClientController = khuyenMaiClientController;
        this.dsKhuyenMai = new ArrayList<>();

        setLayout(new BorderLayout());

        initHeader();
        initInputForm();
        initButtons();

        String[] cols = {"Mã khuyến mãi", "Mức giảm", "Ngày bắt đầu", "Ngày kết thúc"};
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
        loadKhuyenMaiData();
    }

    private void initHeader() {
        lblTieuDe = new JLabel("QUẢN LÝ KHUYẾN MÃI", SwingConstants.CENTER);
        lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTieuDe.setForeground(PRIMARY_COLOR);
        lblTieuDe.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
    }

    private void initInputForm() {
        Font fontLabel = new Font("Segoe UI", Font.BOLD, 14);
        Font fontText = new Font("Segoe UI", Font.PLAIN, 14);

        lblMaKM = new JLabel("Mã KM:");
        lblMaKM.setFont(fontLabel);

        lblMucGiam = new JLabel("Mức giảm (0-1):");
        lblMucGiam.setFont(fontLabel);

        lblNgayApDung = new JLabel("Ngày bắt đầu (dd/MM/yyyy):");
        lblNgayApDung.setFont(fontLabel);

        lblNgayKetThuc = new JLabel("Ngày kết thúc (dd/MM/yyyy):");
        lblNgayKetThuc.setFont(fontLabel);

        txtMaKM = new JTextField();
        txtMaKM.setFont(fontText);
        txtMaKM.setPreferredSize(new Dimension(200, 35));

        txtMucGiam = new JTextField();
        txtMucGiam.setFont(fontText);
        txtMucGiam.setPreferredSize(new Dimension(200, 35));

        txtNgayApDung = new JTextField();
        txtNgayApDung.setFont(fontText);
        txtNgayApDung.setPreferredSize(new Dimension(200, 35));

        txtNgayKetThuc = new JTextField();
        txtNgayKetThuc.setFont(fontText);
        txtNgayKetThuc.setPreferredSize(new Dimension(200, 35));
    }

    private JPanel createInputPanel() {
        JPanel pnlForm = new JPanel(new GridBagLayout());
        pnlForm.setBackground(ACCENT_COLOR);
        pnlForm.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(20, 50, 20, 50)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 20);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.1;
        pnlForm.add(lblMaKM, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.4;
        pnlForm.add(txtMaKM, gbc);

        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 0.1;
        pnlForm.add(lblMucGiam, gbc);

        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.weightx = 0.4;
        pnlForm.add(txtMucGiam, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.1;
        pnlForm.add(lblNgayApDung, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.4;
        pnlForm.add(txtNgayApDung, gbc);

        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.weightx = 0.1;
        pnlForm.add(lblNgayKetThuc, gbc);

        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.weightx = 0.4;
        pnlForm.add(txtNgayKetThuc, gbc);

        return pnlForm;
    }

    private void initButtons() {
        btnThem = createStyledButton("Thêm", BTN_ADD_COLOR);
        btnSua = createStyledButton("Sửa", BTN_EDIT_COLOR);
        btnXoa = createStyledButton("Xóa", BTN_DELETE_COLOR);
        btnXoaTrang = createStyledButton("Xóa trắng", BTN_CLEAR_COLOR);
        btnLamMoi = createStyledButton("Làm mới", BTN_REFRESH_COLOR);

        btnThem.addActionListener(this);
        btnSua.addActionListener(this);
        btnXoa.addActionListener(this);
        btnXoaTrang.addActionListener(this);
        btnLamMoi.addActionListener(this);
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
        pnlButtons.add(btnXoa);
        pnlButtons.add(btnXoaTrang);
        pnlButtons.add(btnLamMoi);
        return pnlButtons;
    }

    private JScrollPane createBotPanel() {
        tblKhuyenMai = new JTable(dtm) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(242, 242, 242));
                }
                return c;
            }
        };

        tblKhuyenMai.setRowHeight(35);
        tblKhuyenMai.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tblKhuyenMai.setFillsViewportHeight(true);
        tblKhuyenMai.setShowGrid(true);
        tblKhuyenMai.setGridColor(new Color(224, 224, 224));
        tblKhuyenMai.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblKhuyenMai.setSelectionBackground(new Color(178, 223, 219));
        tblKhuyenMai.setSelectionForeground(Color.BLACK);

        JTableHeader header = tblKhuyenMai.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 40));
        header.setReorderingAllowed(false);

        DefaultTableCellRenderer centerRenderer = (DefaultTableCellRenderer) header.getDefaultRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        DefaultTableCellRenderer cellCenter = new DefaultTableCellRenderer();
        cellCenter.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < tblKhuyenMai.getColumnCount(); i++) {
            tblKhuyenMai.getColumnModel().getColumn(i).setCellRenderer(cellCenter);
        }

        tblKhuyenMai.addMouseListener(this);

        JScrollPane scrollPane = new JScrollPane(tblKhuyenMai);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        scrollPane.getViewport().setBackground(Color.WHITE);
        return scrollPane;
    }

    private void loadKhuyenMaiData() {
        SwingWorker<List<KhuyenMai>, Void> worker = new SwingWorker<List<KhuyenMai>, Void>() {
            @Override
            protected List<KhuyenMai> doInBackground() {
                return khuyenMaiClientController.getAllKhuyenMai();
            }

            @Override
            protected void done() {
                try {
                    List<KhuyenMai> items = get();
                    dsKhuyenMai = new ArrayList<>(items);
                    dtm.setRowCount(0);
                    for (KhuyenMai item : items) {
                        dtm.addRow(new Object[]{
                                item.getMaKM(),
                                item.getMucGiamGia(),
                                formatDate(item.getNgayApDung()),
                                formatDate(item.getNgayKetThuc())
                        });
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(
                            CapNhatKhuyenMaiPanel.this,
                            "Tải danh sách khuyến mãi thất bại: " + e.getMessage(),
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
        txtMaKM.setText("");
        txtMucGiam.setText("");

        String today = formatDate(LocalDate.now());
        txtNgayApDung.setText(today);
        txtNgayKetThuc.setText(today);

        txtMaKM.setEnabled(true);
        tblKhuyenMai.clearSelection();
    }

    private boolean validateInput(boolean isAddingNew) {
        String maKM = txtMaKM.getText().trim();
        String mucGiamRaw = txtMucGiam.getText().trim();
        String ngayApDungRaw = txtNgayApDung.getText().trim();
        String ngayKetThucRaw = txtNgayKetThuc.getText().trim();

        if (maKM.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mã khuyến mãi không được để trống!");
            txtMaKM.requestFocus();
            return false;
        }

        if (!maKM.matches("KM\\d{3}")) {
            JOptionPane.showMessageDialog(this, "Mã khuyến mãi phải có định dạng KM + 3 chữ số (VD: KM001)!");
            txtMaKM.requestFocus();
            return false;
        }

        if (mucGiamRaw.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mức giảm không được để trống!");
            txtMucGiam.requestFocus();
            return false;
        }

        if (ngayApDungRaw.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ngày bắt đầu không được để trống!");
            txtNgayApDung.requestFocus();
            return false;
        }

        if (ngayKetThucRaw.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ngày kết thúc không được để trống!");
            txtNgayKetThuc.requestFocus();
            return false;
        }

        if (isAddingNew) {
            for (KhuyenMai item : dsKhuyenMai) {
                if (item.getMaKM().equalsIgnoreCase(maKM)) {
                    JOptionPane.showMessageDialog(this, "Mã khuyến mãi đã tồn tại!");
                    txtMaKM.requestFocus();
                    return false;
                }
            }
        }

        try {
            double mucGiam = Double.parseDouble(mucGiamRaw);
            if (mucGiam < 0 || mucGiam > 1) {
                JOptionPane.showMessageDialog(this, "Mức giảm phải nằm trong đoạn [0, 1]!");
                txtMucGiam.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Mức giảm phải là số thực hợp lệ!");
            txtMucGiam.requestFocus();
            return false;
        }

        try {
            LocalDate ngayApDung = parseDate(ngayApDungRaw, "Ngày bắt đầu");
            LocalDate ngayKetThuc = parseDate(ngayKetThucRaw, "Ngày kết thúc");
            LocalDate today = LocalDate.now();

            if (ngayApDung.isBefore(today)) {
                JOptionPane.showMessageDialog(this, "Ngày bắt đầu phải từ hôm nay trở đi!");
                txtNgayApDung.requestFocus();
                return false;
            }
            if (ngayKetThuc.isBefore(today)) {
                JOptionPane.showMessageDialog(this, "Ngày kết thúc phải từ hôm nay trở đi!");
                txtNgayKetThuc.requestFocus();
                return false;
            }
            if (ngayKetThuc.isBefore(ngayApDung)) {
                JOptionPane.showMessageDialog(this, "Ngày kết thúc phải bằng hoặc sau ngày bắt đầu!");
                txtNgayKetThuc.requestFocus();
                return false;
            }
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            return false;
        }

        return true;
    }

    private KhuyenMai buildKhuyenMaiFromInput() {
        return KhuyenMai.builder()
                .maKM(txtMaKM.getText().trim())
                .mucGiamGia(Double.parseDouble(txtMucGiam.getText().trim()))
                .ngayApDung(parseDate(txtNgayApDung.getText().trim(), "Ngày bắt đầu"))
                .ngayKetThuc(parseDate(txtNgayKetThuc.getText().trim(), "Ngày kết thúc"))
                .isActive(true)
                .build();
    }

    private void themKhuyenMai() {
        if (!validateInput(true)) {
            return;
        }

        KhuyenMai payload = buildKhuyenMaiFromInput();
        SwingWorker<Response, Void> worker = new SwingWorker<Response, Void>() {
            @Override
            protected Response doInBackground() {
                return khuyenMaiClientController.addKhuyenMaiResponse(payload);
            }

            @Override
            protected void done() {
                try {
                    Response response = get();
                    if (response.isSuccess()) {
                        JOptionPane.showMessageDialog(CapNhatKhuyenMaiPanel.this, "Thêm khuyến mãi thành công!");
                        xoaTrang();
                        loadKhuyenMaiData();
                        return;
                    }
                    JOptionPane.showMessageDialog(CapNhatKhuyenMaiPanel.this, response.getMessage());
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(CapNhatKhuyenMaiPanel.this, "Thêm khuyến mãi thất bại: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void suaKhuyenMai() {
        if (!validateInput(false)) {
            return;
        }

        KhuyenMai payload = buildKhuyenMaiFromInput();
        SwingWorker<Response, Void> worker = new SwingWorker<Response, Void>() {
            @Override
            protected Response doInBackground() {
                return khuyenMaiClientController.updateKhuyenMaiResponse(payload);
            }

            @Override
            protected void done() {
                try {
                    Response response = get();
                    if (response.isSuccess()) {
                        JOptionPane.showMessageDialog(CapNhatKhuyenMaiPanel.this, "Cập nhật khuyến mãi thành công!");
                        xoaTrang();
                        loadKhuyenMaiData();
                        return;
                    }
                    JOptionPane.showMessageDialog(CapNhatKhuyenMaiPanel.this, response.getMessage());
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(CapNhatKhuyenMaiPanel.this, "Cập nhật khuyến mãi thất bại: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void xoaKhuyenMai() {
        int selectedRow = tblKhuyenMai.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khuyến mãi cần xóa!");
            return;
        }

        String maKM = tblKhuyenMai.getValueAt(selectedRow, 0).toString();
        int option = JOptionPane.showConfirmDialog(
                this,
                "Có chắc muốn xóa khuyến mãi " + maKM + " không?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION
        );
        if (option != JOptionPane.YES_OPTION) {
            return;
        }

        SwingWorker<Response, Void> worker = new SwingWorker<Response, Void>() {
            @Override
            protected Response doInBackground() {
                return khuyenMaiClientController.deleteKhuyenMaiResponse(maKM);
            }

            @Override
            protected void done() {
                try {
                    Response response = get();
                    if (response.isSuccess()) {
                        JOptionPane.showMessageDialog(CapNhatKhuyenMaiPanel.this, "Xóa khuyến mãi thành công!");
                        xoaTrang();
                        loadKhuyenMaiData();
                        return;
                    }
                    JOptionPane.showMessageDialog(CapNhatKhuyenMaiPanel.this, response.getMessage());
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(CapNhatKhuyenMaiPanel.this, "Xóa khuyến mãi thất bại: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        if (o == btnThem) {
            themKhuyenMai();
        } else if (o == btnSua) {
            suaKhuyenMai();
        } else if (o == btnXoa) {
            xoaKhuyenMai();
        } else if (o == btnXoaTrang) {
            xoaTrang();
        } else if (o == btnLamMoi) {
            xoaTrang();
            loadKhuyenMaiData();
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getSource() == tblKhuyenMai) {
            int row = tblKhuyenMai.getSelectedRow();
            if (row >= 0) {
                txtMaKM.setText(tblKhuyenMai.getValueAt(row, 0).toString());
                txtMucGiam.setText(tblKhuyenMai.getValueAt(row, 1).toString());
                txtNgayApDung.setText(tblKhuyenMai.getValueAt(row, 2).toString());
                txtNgayKetThuc.setText(tblKhuyenMai.getValueAt(row, 3).toString());
                txtMaKM.setEnabled(false);
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
