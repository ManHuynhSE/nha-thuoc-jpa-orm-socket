package com.pillchill.migration.gui.capnhat;

import com.pillchill.migration.entity.DonVi;
import com.pillchill.migration.network.client.DonViClientController;
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

public class CapNhatDonViPanel extends JPanel implements ActionListener, MouseListener {

    private final DonViClientController donViClientController;

    private final Color PRIMARY_COLOR = new Color(0, 150, 136);
    private final Color ACCENT_COLOR = new Color(255, 255, 255);
    private final Color BG_COLOR = new Color(245, 245, 245);

    private final Color BTN_ADD_COLOR = new Color(46, 204, 113);
    private final Color BTN_EDIT_COLOR = new Color(241, 196, 15);
    private final Color BTN_DELETE_COLOR = new Color(231, 76, 60);
    private final Color BTN_CLEAR_COLOR = new Color(149, 165, 166);
    private final Color BTN_REFRESH_COLOR = new Color(57, 155, 226);

    private JLabel lblTieuDe;
    private JLabel lblMaDonVi;
    private JLabel lblTenDonVi;

    private JTextField txtMaDonVi;
    private JTextField txtTenDonVi;

    private JButton btnXoa;
    private JButton btnSua;
    private JButton btnThem;
    private JButton btnXoaTrang;
    private JButton btnLamMoi;

    private DefaultTableModel dtm;
    private JTable tblDonVi;

    private ArrayList<DonVi> dsDonVi;

    public CapNhatDonViPanel(DonViClientController donViClientController) {
        this.donViClientController = donViClientController;
        this.dsDonVi = new ArrayList<>();

        setLayout(new BorderLayout());

        initHeader();
        initInputForm();
        initButtons();

        String[] cols = {"Mã đơn vị", "Tên đơn vị"};
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
        loadDonViData();
    }

    private void initHeader() {
        lblTieuDe = new JLabel("QUẢN LÝ ĐƠN VỊ", SwingConstants.CENTER);
        lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTieuDe.setForeground(PRIMARY_COLOR);
        lblTieuDe.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
    }

    private void initInputForm() {
        Font fontLabel = new Font("Segoe UI", Font.BOLD, 14);
        Font fontText = new Font("Segoe UI", Font.PLAIN, 14);

        lblMaDonVi = new JLabel("Mã đơn vị:");
        lblMaDonVi.setFont(fontLabel);

        lblTenDonVi = new JLabel("Tên đơn vị:");
        lblTenDonVi.setFont(fontLabel);

        txtMaDonVi = new JTextField();
        txtMaDonVi.setFont(fontText);
        txtMaDonVi.setPreferredSize(new Dimension(200, 35));

        txtTenDonVi = new JTextField();
        txtTenDonVi.setFont(fontText);
        txtTenDonVi.setPreferredSize(new Dimension(200, 35));
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
        pnlForm.add(lblMaDonVi, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.9;
        pnlForm.add(txtMaDonVi, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.1;
        pnlForm.add(lblTenDonVi, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.9;
        pnlForm.add(txtTenDonVi, gbc);

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
        tblDonVi = new JTable(dtm) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(242, 242, 242));
                }
                return c;
            }
        };

        tblDonVi.setRowHeight(35);
        tblDonVi.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tblDonVi.setFillsViewportHeight(true);
        tblDonVi.setShowGrid(true);
        tblDonVi.setGridColor(new Color(224, 224, 224));
        tblDonVi.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblDonVi.setSelectionBackground(new Color(178, 223, 219));
        tblDonVi.setSelectionForeground(Color.BLACK);

        JTableHeader header = tblDonVi.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 40));
        header.setReorderingAllowed(false);

        DefaultTableCellRenderer centerRenderer = (DefaultTableCellRenderer) header.getDefaultRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        DefaultTableCellRenderer cellCenter = new DefaultTableCellRenderer();
        cellCenter.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < tblDonVi.getColumnCount(); i++) {
            tblDonVi.getColumnModel().getColumn(i).setCellRenderer(cellCenter);
        }

        tblDonVi.addMouseListener(this);

        JScrollPane scrollPane = new JScrollPane(tblDonVi);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        scrollPane.getViewport().setBackground(Color.WHITE);
        return scrollPane;
    }

    private void loadDonViData() {
        SwingWorker<List<DonVi>, Void> worker = new SwingWorker<List<DonVi>, Void>() {
            @Override
            protected List<DonVi> doInBackground() {
                return donViClientController.getAllDonVi();
            }

            @Override
            protected void done() {
                try {
                    List<DonVi> items = get();
                    dsDonVi = new ArrayList<>(items);
                    dtm.setRowCount(0);
                    for (DonVi item : items) {
                        dtm.addRow(new Object[]{item.getMaDonVi(), item.getTenDonVi()});
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(
                            CapNhatDonViPanel.this,
                            "Tải danh sách đơn vị thất bại: " + e.getMessage(),
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };
        worker.execute();
    }

    private void xoaTrang() {
        txtMaDonVi.setText("");
        txtTenDonVi.setText("");
        txtMaDonVi.setEnabled(true);
        tblDonVi.clearSelection();
    }

    private boolean validateInput(boolean isAddingNew) {
        String maDonVi = txtMaDonVi.getText().trim();
        String tenDonVi = txtTenDonVi.getText().trim();

        if (maDonVi.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mã đơn vị không được để trống!");
            txtMaDonVi.requestFocus();
            return false;
        }

        if (tenDonVi.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên đơn vị không được để trống!");
            txtTenDonVi.requestFocus();
            return false;
        }

        if (isAddingNew) {
            for (DonVi item : dsDonVi) {
                if (item.getMaDonVi().equalsIgnoreCase(maDonVi)) {
                    JOptionPane.showMessageDialog(this, "Mã đơn vị đã tồn tại!");
                    txtMaDonVi.requestFocus();
                    return false;
                }
            }
        }

        return true;
    }

    private DonVi buildDonViFromInput() {
        return DonVi.builder()
                .maDonVi(txtMaDonVi.getText().trim())
                .tenDonVi(txtTenDonVi.getText().trim())
                .isActive(true)
                .build();
    }

    private void themDonVi() {
        if (!validateInput(true)) {
            return;
        }

        DonVi payload = buildDonViFromInput();
        SwingWorker<Response, Void> worker = new SwingWorker<Response, Void>() {
            @Override
            protected Response doInBackground() {
                return donViClientController.addDonViResponse(payload);
            }

            @Override
            protected void done() {
                try {
                    Response response = get();
                    if (response.isSuccess()) {
                        JOptionPane.showMessageDialog(CapNhatDonViPanel.this, "Thêm đơn vị thành công!");
                        xoaTrang();
                        loadDonViData();
                        return;
                    }
                    JOptionPane.showMessageDialog(CapNhatDonViPanel.this, response.getMessage());
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(CapNhatDonViPanel.this, "Thêm đơn vị thất bại: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void suaDonVi() {
        if (!validateInput(false)) {
            return;
        }

        DonVi payload = buildDonViFromInput();
        SwingWorker<Response, Void> worker = new SwingWorker<Response, Void>() {
            @Override
            protected Response doInBackground() {
                return donViClientController.updateDonViResponse(payload);
            }

            @Override
            protected void done() {
                try {
                    Response response = get();
                    if (response.isSuccess()) {
                        JOptionPane.showMessageDialog(CapNhatDonViPanel.this, "Cập nhật đơn vị thành công!");
                        xoaTrang();
                        loadDonViData();
                        return;
                    }
                    JOptionPane.showMessageDialog(CapNhatDonViPanel.this, response.getMessage());
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(CapNhatDonViPanel.this, "Cập nhật đơn vị thất bại: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void xoaDonVi() {
        int selectedRow = tblDonVi.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn đơn vị cần xóa!");
            return;
        }

        String maDonVi = tblDonVi.getValueAt(selectedRow, 0).toString();
        int option = JOptionPane.showConfirmDialog(
                this,
                "Có chắc muốn xóa đơn vị " + maDonVi + " không?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION
        );

        if (option != JOptionPane.YES_OPTION) {
            return;
        }

        SwingWorker<Response, Void> worker = new SwingWorker<Response, Void>() {
            @Override
            protected Response doInBackground() {
                return donViClientController.deleteDonViResponse(maDonVi);
            }

            @Override
            protected void done() {
                try {
                    Response response = get();
                    if (response.isSuccess()) {
                        JOptionPane.showMessageDialog(CapNhatDonViPanel.this, "Xóa đơn vị thành công!");
                        xoaTrang();
                        loadDonViData();
                        return;
                    }
                    JOptionPane.showMessageDialog(CapNhatDonViPanel.this, response.getMessage());
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(CapNhatDonViPanel.this, "Xóa đơn vị thất bại: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        if (o == btnThem) {
            themDonVi();
        } else if (o == btnSua) {
            suaDonVi();
        } else if (o == btnXoa) {
            xoaDonVi();
        } else if (o == btnXoaTrang) {
            xoaTrang();
        } else if (o == btnLamMoi) {
            xoaTrang();
            loadDonViData();
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getSource() == tblDonVi) {
            int row = tblDonVi.getSelectedRow();
            if (row >= 0) {
                txtMaDonVi.setText(tblDonVi.getValueAt(row, 0).toString());
                txtTenDonVi.setText(tblDonVi.getValueAt(row, 1).toString());
                txtMaDonVi.setEnabled(false);
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
