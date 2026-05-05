package com.pillchill.migration.gui.capnhat;

import com.formdev.flatlaf.FlatLightLaf;
import com.pillchill.migration.dto.BangGiaDTO;
import com.pillchill.migration.network.client.BangGiaClientController;
import com.toedter.calendar.JDateChooser;

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
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CapNhatBangGiaSubPanel extends JPanel implements ActionListener, MouseListener {

    private final Color PRIMARY_COLOR = new Color(0, 150, 136);
    private final Color ACCENT_COLOR = new Color(255, 255, 255);
    private final Color BG_COLOR = new Color(245, 245, 245);

    private final Color BTN_RESTORE_COLOR = new Color(46, 204, 113);
    private final Color BTN_REFRESH_COLOR = new Color(57, 155, 226);
    private final Color BTN_BACK_COLOR = new Color(149, 165, 166);

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private JLabel lblTieuDe;
    private JLabel lblNgayApDung;
    private JLabel lblNgayKetThuc;
    private JLabel lblMaBangGia;
    private JLabel lblTenBangGia;
    private JLabel lblGhiChu;
    private JLabel lblDoUuTien;
    private JLabel lblTrangThai;
    private JLabel lblLoaiGia;

    private JDateChooser calNgayApDung;
    private JDateChooser calNgayKetThuc;

    private JTextField txtMaBangGia;
    private JTextField txtTenBangGia;
    private JTextField txtTrangThai;
    private JTextField txtLoaiBangGia;
    private JTextField txtGhiChu;
    private JTextField txtDoUuTien;

    private DefaultTableModel dtm;
    private JTable tblBangGia;

    private JButton btnKhoiPhuc;
    private JButton btnQuayLai;
    private JButton btnLamMoi;

    private final CapNhatBangGiaPanel pnlCapNhatBangGia;
    private final BangGiaClientController bangGiaClientController;
    private ArrayList<BangGiaDTO> dsBangGia;

    public CapNhatBangGiaSubPanel(CapNhatBangGiaPanel pnlCapNhatBangGia, BangGiaClientController bangGiaClientController) {
        this.pnlCapNhatBangGia = pnlCapNhatBangGia;
        this.bangGiaClientController = bangGiaClientController;
        this.dsBangGia = new ArrayList<>();

        FlatLightLaf.setup();
        setLayout(new BorderLayout());

        initHeader();
        initInputForm();
        initButtons();

        String[] colsBangGia = {
                "Mã bảng giá", "Tên bảng giá", "Loại bảng giá", "Độ Ưu Tiên",
                "Ngày áp dụng", "Ngày kết thúc", "Trạng thái", "Ghi chú"
        };
        dtm = new DefaultTableModel(colsBangGia, 0) {
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
        loadBangGiaData();
    }

    private void initHeader() {
        lblTieuDe = new JLabel("QUẢN LÝ BẢNG GIÁ ĐÃ XÓA", SwingConstants.CENTER);
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

        lblDoUuTien = new JLabel("Độ Ưu Tiên:");
        lblDoUuTien.setFont(fontLabel);

        lblNgayApDung = new JLabel("Ngày bắt đầu:");
        lblNgayApDung.setFont(fontLabel);

        lblNgayKetThuc = new JLabel("Ngày kết thúc:");
        lblNgayKetThuc.setFont(fontLabel);

        lblLoaiGia = new JLabel("Loại bảng giá:");
        lblLoaiGia.setFont(fontLabel);

        lblTrangThai = new JLabel("Trạng thái:");
        lblTrangThai.setFont(fontLabel);

        txtMaBangGia = createReadOnlyTextField(fontText);
        txtTenBangGia = createReadOnlyTextField(fontText);
        txtGhiChu = createReadOnlyTextField(fontText);
        txtDoUuTien = createReadOnlyTextField(fontText);
        txtTrangThai = createReadOnlyTextField(fontText);
        txtLoaiBangGia = createReadOnlyTextField(fontText);

        calNgayApDung = new JDateChooser();
        calNgayApDung.setDateFormatString("dd/MM/yyyy");
        calNgayApDung.setFont(fontText);
        calNgayApDung.setEnabled(false);
        calNgayApDung.setPreferredSize(new Dimension(200, 35));

        calNgayKetThuc = new JDateChooser();
        calNgayKetThuc.setDateFormatString("dd/MM/yyyy");
        calNgayKetThuc.setFont(fontText);
        calNgayKetThuc.setEnabled(false);
        calNgayKetThuc.setPreferredSize(new Dimension(200, 35));
    }

    private JTextField createReadOnlyTextField(Font font) {
        JTextField textField = new JTextField();
        textField.setFont(font);
        textField.setPreferredSize(new Dimension(200, 35));
        textField.setEditable(false);
        return textField;
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
        pnlForm.add(lblMaBangGia, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.4;
        pnlForm.add(txtMaBangGia, gbc);

        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 0.1;
        pnlForm.add(lblTenBangGia, gbc);

        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.weightx = 0.4;
        pnlForm.add(txtTenBangGia, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.1;
        pnlForm.add(lblNgayApDung, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.4;
        pnlForm.add(calNgayApDung, gbc);

        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.weightx = 0.1;
        pnlForm.add(lblNgayKetThuc, gbc);

        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.weightx = 0.4;
        pnlForm.add(calNgayKetThuc, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.1;
        pnlForm.add(lblDoUuTien, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 0.4;
        pnlForm.add(txtDoUuTien, gbc);

        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.weightx = 0.1;
        pnlForm.add(lblGhiChu, gbc);

        gbc.gridx = 3;
        gbc.gridy = 2;
        gbc.weightx = 0.4;
        pnlForm.add(txtGhiChu, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.1;
        pnlForm.add(lblLoaiGia, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 0.4;
        pnlForm.add(txtLoaiBangGia, gbc);

        gbc.gridx = 2;
        gbc.gridy = 3;
        gbc.weightx = 0.1;
        pnlForm.add(lblTrangThai, gbc);

        gbc.gridx = 3;
        gbc.gridy = 3;
        gbc.weightx = 0.4;
        pnlForm.add(txtTrangThai, gbc);

        return pnlForm;
    }

    private void initButtons() {
        btnKhoiPhuc = createStyledButton("Khôi phục", BTN_RESTORE_COLOR);
        btnLamMoi = createStyledButton("Làm mới", BTN_REFRESH_COLOR);
        btnQuayLai = createStyledButton("Quay lại", BTN_BACK_COLOR);

        btnKhoiPhuc.addActionListener(this);
        btnLamMoi.addActionListener(this);
        btnQuayLai.addActionListener(this);
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
        pnlButtons.add(btnKhoiPhuc);
        pnlButtons.add(btnLamMoi);
        pnlButtons.add(btnQuayLai);
        return pnlButtons;
    }

    public JScrollPane createBotPanel() {
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

    public void loadBangGiaData() {
        SwingWorker<List<BangGiaDTO>, Void> worker = new SwingWorker<List<BangGiaDTO>, Void>() {
            @Override
            protected List<BangGiaDTO> doInBackground() {
                return bangGiaClientController.getAllInactiveBangGiaItems();
            }

            @Override
            protected void done() {
                try {
                    List<BangGiaDTO> items = get();
                    dsBangGia = new ArrayList<>(items);
                    dtm.setRowCount(0);
                    for (BangGiaDTO bg : items) {
                        dtm.addRow(new Object[]{
                                bg.getMaBangGia(),
                                bg.getTenBangGia(),
                                bg.getLoaiGia(),
                                bg.getDoUuTien(),
                                formatDate(bg.getNgayApDung()),
                                formatDate(bg.getNgayKetThuc()),
                                bg.getTrangThai(),
                                bg.getGhiChu()
                        });
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(
                            CapNhatBangGiaSubPanel.this,
                            "Tải danh sách bảng giá đã xóa thất bại: " + e.getMessage(),
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

    private Date toDate(LocalDate localDate) {
        if (localDate == null) {
            return null;
        }
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    private void khoiPhucBangGia() {
        final String maBangGia = txtMaBangGia.getText().trim();
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() {
                return bangGiaClientController.reactiveBangGia(maBangGia);
            }

            @Override
            protected void done() {
                try {
                    get();
                    JOptionPane.showMessageDialog(
                            CapNhatBangGiaSubPanel.this,
                            "Khôi phục bảng giá thành công!",
                            "Thông báo",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                    xoaTrang();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(
                            CapNhatBangGiaSubPanel.this,
                            "Khôi phục bảng giá thất bại: " + e.getMessage(),
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };
        worker.execute();
    }

    public void xoaTrang() {
        txtMaBangGia.setText("");
        txtTenBangGia.setText("");
        txtDoUuTien.setText("");
        txtGhiChu.setText("");
        txtLoaiBangGia.setText("");
        txtTrangThai.setText("");
        calNgayApDung.setDate(null);
        calNgayKetThuc.setDate(null);
        txtMaBangGia.setEnabled(true);
        if (tblBangGia != null) {
            tblBangGia.clearSelection();
        }
        loadBangGiaData();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();

        if (o == btnKhoiPhuc) {
            String maBangGia = txtMaBangGia.getText().trim();
            if (maBangGia.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn bảng giá cần khôi phục!");
                return;
            }

            int option = JOptionPane.showConfirmDialog(
                    this,
                    "Có chắc muốn khôi phục bảng giá " + maBangGia + "?",
                    "Xác nhận",
                    JOptionPane.YES_NO_OPTION
            );

            if (option == JOptionPane.YES_OPTION) {
                khoiPhucBangGia();
            }
        } else if (o == btnLamMoi) {
            xoaTrang();
        } else if (o == btnQuayLai) {
            pnlCapNhatBangGia.quayLaiDanhSach();
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getSource() == tblBangGia) {
            int row = tblBangGia.getSelectedRow();
            if (row >= 0) {
                txtMaBangGia.setEnabled(false);
                BangGiaDTO selected = dsBangGia.get(row);
                txtMaBangGia.setText(selected.getMaBangGia());
                txtTenBangGia.setText(selected.getTenBangGia());
                txtLoaiBangGia.setText(selected.getLoaiGia());
                txtDoUuTien.setText(String.valueOf(selected.getDoUuTien()));
                txtTrangThai.setText(selected.getTrangThai());
                txtGhiChu.setText(selected.getGhiChu());
                calNgayApDung.setDate(toDate(selected.getNgayApDung()));
                calNgayKetThuc.setDate(toDate(selected.getNgayKetThuc()));
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
