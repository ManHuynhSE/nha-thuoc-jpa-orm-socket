package com.pillchill.migration.gui.capnhat;

import com.pillchill.migration.entity.KhachHang;
import com.pillchill.migration.network.client.KhachHangClientController;

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

public class CapNhatKhachHangSubPanel extends JPanel implements ActionListener, MouseListener {

    private final Color PRIMARY_COLOR = new Color(0, 150, 136);
    private final Color ACCENT_COLOR = new Color(255, 255, 255);
    private final Color BG_COLOR = new Color(245, 245, 245);

    private final Color BTN_RESTORE_COLOR = new Color(46, 204, 113);
    private final Color BTN_REFRESH_COLOR = new Color(57, 155, 226);
    private final Color BTN_BACK_COLOR = new Color(149, 165, 166);

    private JLabel lblTieuDe;
    private JLabel lblMaKh, lblTenKh, lblSoDienThoai, lblDiemTichLuy;
    private JTextField txtMaKh, txtTenKh, txtSoDienThoai, txtDiemTichLuy;

    private JButton btnKhoiPhuc, btnLamMoi, btnQuayLai;

    private DefaultTableModel dtm;
    private JTable tblKhachHang;

    private ArrayList<KhachHang> dsKhachHang;
    private final KhachHangClientController khachHangClientController;
    private final CapNhatKhachHangPanel pnlCapNhatKhachHang;

    public CapNhatKhachHangSubPanel(CapNhatKhachHangPanel pnlCapNhatKhachHang, KhachHangClientController khachHangClientController) {
        this.pnlCapNhatKhachHang = pnlCapNhatKhachHang;
        this.khachHangClientController = khachHangClientController;
        this.dsKhachHang = new ArrayList<>();

        setLayout(new BorderLayout());

        initHeader();
        initInputForm();
        initButtons();

        String[] cols = {"Mã khách hàng", "Tên khách hàng", "Số điện thoại", "Điểm tích lũy"};
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

        loadKhachHangData();
    }

    private void initHeader() {
        lblTieuDe = new JLabel("QUẢN LÝ KHÁCH HÀNG ĐÃ XÓA", SwingConstants.CENTER);
        lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTieuDe.setForeground(PRIMARY_COLOR);
        lblTieuDe.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
    }

    private void initInputForm() {
        Font fontLabel = new Font("Segoe UI", Font.BOLD, 14);
        Font fontText = new Font("Segoe UI", Font.PLAIN, 14);

        lblMaKh = new JLabel("Mã KH:");
        lblTenKh = new JLabel("Tên KH:");
        lblSoDienThoai = new JLabel("Số ĐT:");
        lblDiemTichLuy = new JLabel("Điểm tích lũy:");

        txtMaKh = createStyledTextField(fontText);
        txtTenKh = createStyledTextField(fontText);
        txtSoDienThoai = createStyledTextField(fontText);
        txtDiemTichLuy = createStyledTextField(fontText);

        lblMaKh.setFont(fontLabel);
        lblTenKh.setFont(fontLabel);
        lblSoDienThoai.setFont(fontLabel);
        lblDiemTichLuy.setFont(fontLabel);
    }

    private JTextField createStyledTextField(Font font) {
        JTextField txt = new JTextField();
        txt.setFont(font);
        txt.setPreferredSize(new Dimension(200, 35));
        return txt;
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

        // Hàng 1
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.1;
        pnlForm.add(lblMaKh, gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.4;
        pnlForm.add(txtMaKh, gbc);

        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 0.1;
        pnlForm.add(lblTenKh, gbc);
        gbc.gridx = 3; gbc.gridy = 0; gbc.weightx = 0.4;
        pnlForm.add(txtTenKh, gbc);

        // Hàng 2
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.1;
        pnlForm.add(lblSoDienThoai, gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 0.4;
        pnlForm.add(txtSoDienThoai, gbc);

        gbc.gridx = 2; gbc.gridy = 1; gbc.weightx = 0.1;
        pnlForm.add(lblDiemTichLuy, gbc);
        gbc.gridx = 3; gbc.gridy = 1; gbc.weightx = 0.4;
        pnlForm.add(txtDiemTichLuy, gbc);

        return pnlForm;
    }

    private void initButtons() {
        btnKhoiPhuc = createStyledButton("Khôi phục", BTN_RESTORE_COLOR);
        btnLamMoi = createStyledButton("Làm mới", BTN_REFRESH_COLOR);
        btnQuayLai = createStyledButton("Quay Lại", BTN_BACK_COLOR);

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

    private JScrollPane createBotPanel() {
        tblKhachHang = new JTable(dtm) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(242, 242, 242));
                }
                return c;
            }
        };

        tblKhachHang.setRowHeight(35);
        tblKhachHang.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tblKhachHang.setFillsViewportHeight(true);
        tblKhachHang.setShowGrid(true);
        tblKhachHang.setGridColor(new Color(224, 224, 224));
        tblKhachHang.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblKhachHang.setSelectionBackground(new Color(178, 223, 219));
        tblKhachHang.setSelectionForeground(Color.BLACK);

        JTableHeader header = tblKhachHang.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 40));
        header.setReorderingAllowed(false);

        DefaultTableCellRenderer cellCenter = new DefaultTableCellRenderer();
        cellCenter.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < tblKhachHang.getColumnCount(); i++) {
            tblKhachHang.getColumnModel().getColumn(i).setCellRenderer(cellCenter);
        }

        tblKhachHang.addMouseListener(this);

        JScrollPane scrollPane = new JScrollPane(tblKhachHang);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        scrollPane.getViewport().setBackground(Color.WHITE);
        return scrollPane;
    }

    public void loadKhachHangData() {
        SwingWorker<List<KhachHang>, Void> worker = new SwingWorker<List<KhachHang>, Void>() {
            @Override
            protected List<KhachHang> doInBackground() {
                // Gọi hàm từ controller (giả định tên hàm tương tự ChucVu)
                return khachHangClientController.getAllInactiveKhachHang();
            }

            @Override
            protected void done() {
                try {
                    List<KhachHang> items = get();
                    dsKhachHang = new ArrayList<>(items);
                    dtm.setRowCount(0);
                    for (KhachHang kh : items) {
                        dtm.addRow(new Object[]{
                            kh.getMaKH(), 
                            kh.getTenKH(), 
                            kh.getSoDienThoai(), 
                            kh.getDiemTichLuy()
                        });
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(
                            CapNhatKhachHangSubPanel.this,
                            "Tải danh sách khách hàng đã xóa thất bại: " + e.getMessage(),
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };
        worker.execute();
    }

    private void khoiPhucKhachHang() {
        final String maKH = txtMaKh.getText().trim();
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() {
                // Gọi hàm từ controller (giả định tên hàm tương tự ChucVu)
                return khachHangClientController.reactiveKhachHang(maKH);
            }

            @Override
            protected void done() {
                try {
                    boolean success = get();
                    if (success) {
                        JOptionPane.showMessageDialog(
                                CapNhatKhachHangSubPanel.this,
                                "Khôi phục khách hàng thành công!",
                                "Thông báo",
                                JOptionPane.INFORMATION_MESSAGE
                        );
                        xoaTrang();
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(
                            CapNhatKhachHangSubPanel.this,
                            "Khôi phục khách hàng thất bại: " + e.getMessage(),
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };
        worker.execute();
    }

    public void xoaTrang() {
        txtMaKh.setText("");
        txtTenKh.setText("");
        txtSoDienThoai.setText("");
        txtDiemTichLuy.setText("");
        txtMaKh.setEnabled(true);
        if (tblKhachHang != null) {
            tblKhachHang.clearSelection();
        }
        loadKhachHangData();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();

        if (o == btnKhoiPhuc) {
            String maKH = txtMaKh.getText().trim();
            if (maKH.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng cần khôi phục!");
                return;
            }

            int option = JOptionPane.showConfirmDialog(
                    this,
                    "Có chắc muốn khôi phục khách hàng " + maKH + "?",
                    "Xác nhận",
                    JOptionPane.YES_NO_OPTION
            );

            if (option == JOptionPane.YES_OPTION) {
                khoiPhucKhachHang();
            }
        } else if (o == btnLamMoi) {
            xoaTrang();
        } else if (o == btnQuayLai) {
            pnlCapNhatKhachHang.quayLaiDanhSach();
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getSource() == tblKhachHang) {
            int row = tblKhachHang.getSelectedRow();
            if (row >= 0) {
                txtMaKh.setEnabled(false);
                txtMaKh.setText(tblKhachHang.getValueAt(row, 0).toString());
                txtTenKh.setText(tblKhachHang.getValueAt(row, 1).toString());
                txtSoDienThoai.setText(tblKhachHang.getValueAt(row, 2).toString());
                txtDiemTichLuy.setText(tblKhachHang.getValueAt(row, 3).toString());
            }
        }
    }

    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
}