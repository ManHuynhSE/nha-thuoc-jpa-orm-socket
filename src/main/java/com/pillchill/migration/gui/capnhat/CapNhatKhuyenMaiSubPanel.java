package com.pillchill.migration.gui.capnhat;

import com.formdev.flatlaf.FlatLightLaf;
import com.pillchill.migration.entity.KhuyenMai;
import com.pillchill.migration.network.client.KhuyenMaiClientController;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CapNhatKhuyenMaiSubPanel extends JPanel implements ActionListener, MouseListener {

    private final Color PRIMARY_COLOR = new Color(0, 150, 136);
    private final Color ACCENT_COLOR = new Color(255, 255, 255);
    private final Color BG_COLOR = new Color(245, 245, 245);

    private final Color BTN_ADD_COLOR = new Color(46, 204, 113);
    private final Color BTN_CLEAR_COLOR = new Color(149, 165, 166);

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

    private JButton btnKhoiPhuc;
    private JButton btnQuayLai;
    private JButton btnLamMoi;

    private DefaultTableModel dtm;
    private JTable tblKhuyenMai;

    private ArrayList<KhuyenMai> dsKhuyenMai;
    private CardLayout cardLayout;
    private JPanel mainContainer;
    private CapNhatKhuyenMaiPanel pnlCapNhatKhuyenMai;
    private final KhuyenMaiClientController khuyenMaiClientController;

    public CapNhatKhuyenMaiSubPanel(CapNhatKhuyenMaiPanel pnlCapNhatKhuyenMai, KhuyenMaiClientController khuyenMaiClientController) {
        this.pnlCapNhatKhuyenMai = pnlCapNhatKhuyenMai;
        this.khuyenMaiClientController = khuyenMaiClientController;

        FlatLightLaf.setup();
        dsKhuyenMai = new ArrayList<>();
        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);

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

        mainContainer.add(pnlMain, "DanhSach");
        cardLayout.show(mainContainer, "DanhSach");

        add(mainContainer, BorderLayout.CENTER);
        loadKhuyenMaiData();
    }

    private void initHeader() {
        lblTieuDe = new JLabel("QUẢN LÝ KHUYẾN MÃI ĐÃ XÓA", SwingConstants.CENTER);
        lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTieuDe.setForeground(PRIMARY_COLOR);
        lblTieuDe.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
    }

    private void initInputForm() {
        Font fontLabel = new Font("Segoe UI", Font.BOLD, 14);
        Font fontText = new Font("Segoe UI", Font.PLAIN, 14);

        lblMaKM = new JLabel("Mã KM:");
        lblMaKM.setFont(fontLabel);
        lblMucGiam = new JLabel("Mức giảm:");
        lblMucGiam.setFont(fontLabel);
        lblNgayApDung = new JLabel("Ngày bắt đầu:");
        lblNgayApDung.setFont(fontLabel);
        lblNgayKetThuc = new JLabel("Ngày kết thúc:");
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
        btnKhoiPhuc = createStyledButton("Khôi phục", BTN_ADD_COLOR);
        btnQuayLai = createStyledButton("Quay Lại", BTN_CLEAR_COLOR);
        btnLamMoi = createStyledButton("Làm mới", BTN_ADD_COLOR);

        btnKhoiPhuc.addActionListener(this);
        btnQuayLai.addActionListener(this);
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
        pnlButtons.add(btnKhoiPhuc);
        pnlButtons.add(btnQuayLai);
        pnlButtons.add(btnLamMoi);
        return pnlButtons;
    }

    public JScrollPane createBotPanel() {
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

    public void loadKhuyenMaiData() {
        SwingWorker<List<KhuyenMai>, Void> worker = new SwingWorker<List<KhuyenMai>, Void>() {
            @Override
            protected List<KhuyenMai> doInBackground() {
                return khuyenMaiClientController.getAllKhuyenMaiInactive();
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
                            pnlCapNhatKhuyenMai,
                            "Tải danh sách khuyến mãi đã xóa thất bại: " + e.getMessage(),
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

    public void khoiPhucKhuyenMai() {
        String maKM = txtMaKM.getText().trim();
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() {
                return khuyenMaiClientController.reactiveKhuyenMai(maKM);
            }

            @Override
            protected void done() {
                try {
                    boolean result = get();
                    if (result) {
                        JOptionPane.showMessageDialog(CapNhatKhuyenMaiSubPanel.this, "Khôi phục khuyến mãi thành công!");
                        xoaTrang();
                    } else {
                        JOptionPane.showMessageDialog(CapNhatKhuyenMaiSubPanel.this, "Khôi phục khuyến mãi không thành công!");
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(
                            pnlCapNhatKhuyenMai,
                            "Khôi phục khuyến mãi thất bại: " + e.getMessage(),
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };
        worker.execute();
    }

    public void xoaTrang() {
        txtMaKM.setText("");
        txtMucGiam.setText("");
        String today = formatDate(LocalDate.now());
        txtNgayApDung.setText(today);
        txtNgayKetThuc.setText(today);
        txtMaKM.setEnabled(true);
        tblKhuyenMai.clearSelection();
        loadKhuyenMaiData();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();

        if (o == btnKhoiPhuc) {
            String maKM = txtMaKM.getText().trim();
            if (maKM.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn khuyến mãi cần khôi phục!");
                return;
            }

            int option = JOptionPane.showConfirmDialog(
                    this,
                    "Có chắc muốn khôi phục khuyến mãi " + maKM + "?",
                    "Xác nhận",
                    JOptionPane.YES_NO_OPTION
            );

            if (option == JOptionPane.YES_OPTION) {
                khoiPhucKhuyenMai();
            }
        } else if (o == btnQuayLai) {
            pnlCapNhatKhuyenMai.quayLaiDanhSach();
        } else if (o == btnLamMoi) {
            xoaTrang();
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getSource() == tblKhuyenMai) {
            txtMaKM.setEnabled(false);
            int row = tblKhuyenMai.getSelectedRow();
            if (row >= 0) {
                txtMaKM.setText(tblKhuyenMai.getValueAt(row, 0).toString());
                txtMucGiam.setText(tblKhuyenMai.getValueAt(row, 1).toString());
                txtNgayApDung.setText(tblKhuyenMai.getValueAt(row, 2).toString());
                txtNgayKetThuc.setText(tblKhuyenMai.getValueAt(row, 3).toString());
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
