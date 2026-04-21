package com.pillchill.migration.gui.capnhat;

import com.pillchill.migration.entity.ChucVu;
import com.pillchill.migration.network.client.ChucVuClientController;
import com.pillchill.migration.network.communication.Response;

import java.awt.BorderLayout;
import java.awt.CardLayout;
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

public class CapNhatChucVuPanel extends JPanel implements ActionListener, MouseListener {

    private final ChucVuClientController chucVuClientController;

    private final Color PRIMARY_COLOR = new Color(0, 150, 136);
    private final Color ACCENT_COLOR = new Color(255, 255, 255);
    private final Color BG_COLOR = new Color(245, 245, 245);

    private final Color BTN_ADD_COLOR = new Color(46, 204, 113);
    private final Color BTN_EDIT_COLOR = new Color(241, 196, 15);
    private final Color BTN_DELETE_COLOR = new Color(231, 76, 60);
    private final Color BTN_CLEAR_COLOR = new Color(149, 165, 166);
    private final Color BTN_REFRESH_COLOR = new Color(57, 155, 226);

    private JLabel lblTieuDe;
    private JLabel lblMaChucVu;
    private JLabel lblTenChucVu;

    private JTextField txtMaChucVu;
    private JTextField txtTenChucVu;

    private JButton btnXoa;
    private JButton btnSua;
    private JButton btnThem;
    private JButton btnXoaTrang;
    private JButton btnLamMoi;

    private DefaultTableModel dtm;
    private JTable tblChucVu;

    private ArrayList<ChucVu> dsChucVu;
    private CardLayout cardLayout;
    private JPanel mainContainer;

    public CapNhatChucVuPanel(ChucVuClientController chucVuClientController) {
        this.chucVuClientController = chucVuClientController;
        this.dsChucVu = new ArrayList<>();
        this.cardLayout = new CardLayout();
        this.mainContainer = new JPanel(cardLayout);

        setLayout(new BorderLayout());

        initHeader();
        initInputForm();
        initButtons();

        String[] cols = {"Mã chức vụ", "Tên chức vụ"};
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

        mainContainer.add(pnlMain, "DanhSach");
        cardLayout.show(mainContainer, "DanhSach");

        add(mainContainer, BorderLayout.CENTER);
        loadChucVuData();
    }

    private void initHeader() {
        lblTieuDe = new JLabel("QUẢN LÝ CHỨC VỤ", SwingConstants.CENTER);
        lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTieuDe.setForeground(PRIMARY_COLOR);
        lblTieuDe.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
    }

    private void initInputForm() {
        Font fontLabel = new Font("Segoe UI", Font.BOLD, 14);
        Font fontText = new Font("Segoe UI", Font.PLAIN, 14);

        lblMaChucVu = new JLabel("Mã chức vụ:");
        lblMaChucVu.setFont(fontLabel);

        lblTenChucVu = new JLabel("Tên chức vụ:");
        lblTenChucVu.setFont(fontLabel);

        txtMaChucVu = new JTextField();
        txtMaChucVu.setFont(fontText);
        txtMaChucVu.setPreferredSize(new Dimension(200, 35));

        txtTenChucVu = new JTextField();
        txtTenChucVu.setFont(fontText);
        txtTenChucVu.setPreferredSize(new Dimension(200, 35));
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
        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.1;
        pnlForm.add(lblMaChucVu, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.9;
        pnlForm.add(txtMaChucVu, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.1;
        pnlForm.add(lblTenChucVu, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.9;
        pnlForm.add(txtTenChucVu, gbc);

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

    public JScrollPane createBotPanel() {
        tblChucVu = new JTable(dtm) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(242, 242, 242));
                }
                return c;
            }
        };

        tblChucVu.setRowHeight(35);
        tblChucVu.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tblChucVu.setFillsViewportHeight(true);
        tblChucVu.setShowGrid(true);
        tblChucVu.setGridColor(new Color(224, 224, 224));
        tblChucVu.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblChucVu.setSelectionBackground(new Color(178, 223, 219));
        tblChucVu.setSelectionForeground(Color.BLACK);

        JTableHeader header = tblChucVu.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 40));
        header.setReorderingAllowed(false);

        DefaultTableCellRenderer centerRenderer = (DefaultTableCellRenderer) header.getDefaultRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        DefaultTableCellRenderer cellCenter = new DefaultTableCellRenderer();
        cellCenter.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < tblChucVu.getColumnCount(); i++) {
            tblChucVu.getColumnModel().getColumn(i).setCellRenderer(cellCenter);
        }

        tblChucVu.addMouseListener(this);

        JScrollPane scrollPane = new JScrollPane(tblChucVu);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        scrollPane.getViewport().setBackground(Color.WHITE);
        return scrollPane;
    }

    public void loadChucVuData() {
        SwingWorker<List<ChucVu>, Void> worker = new SwingWorker<List<ChucVu>, Void>() {
            @Override
            protected List<ChucVu> doInBackground() {
                return chucVuClientController.getAllChucVu();
            }

            @Override
            protected void done() {
                try {
                    List<ChucVu> items = get();
                    dsChucVu = new ArrayList<>(items);
                    dtm.setRowCount(0);
                    for (ChucVu cv : items) {
                        dtm.addRow(new Object[]{cv.getMaChucVu(), cv.getTenChucVu()});
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(
                            CapNhatChucVuPanel.this,
                            "Tải danh sách chức vụ thất bại: " + e.getMessage(),
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };
        worker.execute();
    }

    public void xoaTrang() {
        txtMaChucVu.setText("");
        txtTenChucVu.setText("");
        txtMaChucVu.setEnabled(true);
        tblChucVu.clearSelection();
    }

    private boolean validateInput(boolean isAddingNew) {
        if (txtMaChucVu.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mã chức vụ không được để trống!");
            txtMaChucVu.requestFocus();
            return false;
        }

        if (txtTenChucVu.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên chức vụ không được để trống!");
            txtTenChucVu.requestFocus();
            return false;
        }

        if (isAddingNew) {
            String maMoi = txtMaChucVu.getText().trim();
            for (ChucVu cv : dsChucVu) {
                if (cv.getMaChucVu().equalsIgnoreCase(maMoi)) {
                    JOptionPane.showMessageDialog(this, "Mã chức vụ đã tồn tại!");
                    txtMaChucVu.requestFocus();
                    return false;
                }
            }
        }

        return true;
    }

    private void themChucVu() {
        if (!validateInput(true)) {
            return;
        }

        ChucVu chucVu = ChucVu.builder()
                .maChucVu(txtMaChucVu.getText().trim())
                .tenChucVu(txtTenChucVu.getText().trim())
                .isActive(true)
                .build();

        SwingWorker<Response, Void> worker = new SwingWorker<Response, Void>() {
            @Override
            protected Response doInBackground() {
                return chucVuClientController.addChucVuResponse(chucVu);
            }

            @Override
            protected void done() {
                try {
                    Response response = get();
                    if (response.isSuccess()) {
                        JOptionPane.showMessageDialog(CapNhatChucVuPanel.this, "Thêm chức vụ thành công!");
                        xoaTrang();
                        loadChucVuData();
                        return;
                    }
                    JOptionPane.showMessageDialog(CapNhatChucVuPanel.this, response.getMessage());
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(CapNhatChucVuPanel.this, "Thêm chức vụ thất bại: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void suaChucVu() {
        if (!validateInput(false)) {
            return;
        }

        ChucVu chucVu = ChucVu.builder()
                .maChucVu(txtMaChucVu.getText().trim())
                .tenChucVu(txtTenChucVu.getText().trim())
                .isActive(true)
                .build();

        SwingWorker<Response, Void> worker = new SwingWorker<Response, Void>() {
            @Override
            protected Response doInBackground() {
                return chucVuClientController.updateChucVuResponse(chucVu);
            }

            @Override
            protected void done() {
                try {
                    Response response = get();
                    if (response.isSuccess()) {
                        JOptionPane.showMessageDialog(CapNhatChucVuPanel.this, "Cập nhật chức vụ thành công!");
                        xoaTrang();
                        loadChucVuData();
                        return;
                    }
                    JOptionPane.showMessageDialog(CapNhatChucVuPanel.this, response.getMessage());
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(CapNhatChucVuPanel.this, "Cập nhật chức vụ thất bại: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void xoaChucVu() {
        int selectedRow = tblChucVu.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn chức vụ cần xóa!");
            return;
        }

        String maChucVu = tblChucVu.getValueAt(selectedRow, 0).toString();

        int option = JOptionPane.showConfirmDialog(
                this,
                "Có chắc muốn xóa chức vụ " + maChucVu + " không?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION
        );

        if (option != JOptionPane.YES_OPTION) {
            return;
        }

        SwingWorker<Response, Void> worker = new SwingWorker<Response, Void>() {
            @Override
            protected Response doInBackground() {
                return chucVuClientController.deleteChucVuResponse(maChucVu);
            }

            @Override
            protected void done() {
                try {
                    Response response = get();
                    if (response.isSuccess()) {
                        JOptionPane.showMessageDialog(CapNhatChucVuPanel.this, "Xóa chức vụ thành công!");
                        xoaTrang();
                        loadChucVuData();
                        return;
                    }
                    JOptionPane.showMessageDialog(CapNhatChucVuPanel.this, response.getMessage());
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(CapNhatChucVuPanel.this, "Xóa chức vụ thất bại: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        if (o == btnThem) {
            themChucVu();
        } else if (o == btnSua) {
            suaChucVu();
        } else if (o == btnXoa) {
            xoaChucVu();
        } else if (o == btnXoaTrang) {
            xoaTrang();
        } else if (o == btnLamMoi) {
            xoaTrang();
            loadChucVuData();
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getSource() == tblChucVu) {
            int row = tblChucVu.getSelectedRow();
            if (row >= 0) {
                txtMaChucVu.setText(tblChucVu.getValueAt(row, 0).toString());
                txtTenChucVu.setText(tblChucVu.getValueAt(row, 1).toString());
                txtMaChucVu.setEnabled(false);
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
