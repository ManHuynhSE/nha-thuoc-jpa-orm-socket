package com.pillchill.migration.gui.capnhat;

import com.formdev.flatlaf.FlatLightLaf;
import com.pillchill.migration.dto.ChiTietBangGiaView;
import com.pillchill.migration.dto.ThuocBangGiaView;
import com.pillchill.migration.entity.BangGia;
import com.pillchill.migration.network.client.BangGiaClientController;
import com.pillchill.migration.network.communication.ChiTietBangGiaPayload;
import com.pillchill.migration.network.communication.Response;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

public class CapNhatChiTietBangGiaPanel extends JPanel implements ActionListener, MouseListener {

    private final Color PRIMARY_COLOR = new Color(0, 150, 136);
    private final Color ACCENT_COLOR = new Color(255, 255, 255);
    private final Color BG_COLOR = new Color(245, 245, 245);
    private final Color TEXT_COLOR = new Color(51, 51, 51);

    private final Color BTN_ADD_COLOR = new Color(46, 204, 113);
    private final Color BTN_DELETE_COLOR = new Color(231, 76, 60);
    private final Color BTN_CLEAR_COLOR = new Color(149, 165, 166);
    private final Color BTN_BACK_COLOR = new Color(70, 70, 70);

    private final String maBangGia;
    private final CapNhatBangGiaPanel parentPanel;
    private final BangGiaClientController bangGiaClientController;

    private ArrayList<ChiTietBangGiaView> dsChiTietBangGia;
    private CardLayout cardLayout;
    private JPanel mainContainer;
    private BangGia bangGia;

    private JLabel lblTieuDe;
    private JLabel lblMaThuoc;
    private JLabel lblTenThuoc;
    private JTextField txtTim;
    private JTextField txtMaThuoc;
    private JTextField txtTenThuoc;

    private JButton btnQuayLai;
    private JButton btnTim;
    private JButton btnReset;
    private JButton btnLuu;
    private JButton btnThem;
    private JButton btnXoa;

    private JComboBox<String> cboTieuChi;
    private String tieuChi = "Mã thuốc";
    private JTable tblChiTietBangGia;
    private DefaultTableModel dtmChiTietBangGia;
    private JPanel pnlBangGiaInfoHost;



    public CapNhatChiTietBangGiaPanel(
            String maBangGia,
            CapNhatBangGiaPanel parentPanel,
            BangGiaClientController bangGiaClientController
    ) {
        FlatLightLaf.setup();
        this.maBangGia = maBangGia;
        this.parentPanel = parentPanel;
        this.bangGiaClientController = bangGiaClientController;
        this.dsChiTietBangGia = new ArrayList<>();

        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);

        setLayout(new BorderLayout());

        initHeader();
        initInputForm();
        initButtons();

        String[] colsChiTietBangGia = {"Mã bảng giá", "Mã thuốc", "Tên thuốc", "Giá bán", "Mã đơn vị"};
        dtmChiTietBangGia = new DefaultTableModel(colsChiTietBangGia, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3;
            }

            @Override
            public void setValueAt(Object aValue, int row, int column) {
                if (column == 3) {
                    try {
                        double val = Double.parseDouble(aValue.toString());
                        if (val < 0) {
                            return;
                        }
                        super.setValueAt(aValue, row, column);
                    } catch (NumberFormatException e) {
                        super.setValueAt("0", row, column);
                    }
                } else {
                    super.setValueAt(aValue, row, column);
                }
            }
        };

        JPanel pnlTop = new JPanel(new BorderLayout(0, 15));
        pnlTop.setBackground(BG_COLOR);
        pnlTop.add(lblTieuDe, BorderLayout.NORTH);
        pnlTop.add(createSearchPanel(), BorderLayout.CENTER);

        JPanel pnlMain = new JPanel(new BorderLayout(10, 10));
        pnlMain.setBackground(BG_COLOR);
        pnlMain.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        pnlMain.add(pnlTop, BorderLayout.NORTH);
        pnlMain.add(createBotPanel(), BorderLayout.CENTER);

        mainContainer.add(pnlMain, "DanhSach");
        cardLayout.show(mainContainer, "DanhSach");

        add(mainContainer, BorderLayout.CENTER);
        loadChiTietBangGiaData();
    }

    private void initHeader() {
        lblTieuDe = new JLabel("QUẢN LÝ CHI TIẾT BẢNG GIÁ", SwingConstants.CENTER);
        lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTieuDe.setForeground(PRIMARY_COLOR);
        lblTieuDe.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
    }

    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 15));
        searchPanel.setBackground(BG_COLOR);

        btnQuayLai = new JButton("← Quay lại");
        btnQuayLai.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnQuayLai.setPreferredSize(new Dimension(120, 40));
        btnQuayLai.setBackground(BTN_BACK_COLOR);
        btnQuayLai.setForeground(Color.WHITE);
        btnQuayLai.setBorderPainted(false);
        btnQuayLai.setFocusPainted(false);
        btnQuayLai.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnQuayLai.addActionListener(this);

        txtTim = new JTextField(25);
        txtTim.setText("Nhập từ khóa tìm kiếm...");
        txtTim.setForeground(Color.GRAY);
        txtTim.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        txtTim.setBackground(Color.WHITE);
        txtTim.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        txtTim.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent evt) {
                if (txtTim.getText().equals("Nhập từ khóa tìm kiếm...")) {
                    txtTim.setText("");
                    txtTim.setForeground(Color.BLACK);
                    txtTim.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                }
            }

            public void focusLost(FocusEvent evt) {
                if (txtTim.getText().isEmpty()) {
                    txtTim.setText("Nhập từ khóa tìm kiếm...");
                    txtTim.setForeground(Color.GRAY);
                    txtTim.setFont(new Font("Segoe UI", Font.ITALIC, 14));
                }
            }
        });

        btnTim = new JButton("Tìm");
        btnTim.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnTim.setPreferredSize(new Dimension(90, 40));
        btnTim.setBackground(PRIMARY_COLOR);
        btnTim.setForeground(Color.WHITE);
        btnTim.setBorderPainted(false);
        btnTim.setFocusPainted(false);
        btnTim.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnTim.addActionListener(this);

        btnReset = new JButton("Làm mới");
        btnReset.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnReset.setPreferredSize(new Dimension(100, 40));
        btnReset.setBackground(BTN_CLEAR_COLOR);
        btnReset.setForeground(Color.WHITE);
        btnReset.setBorderPainted(false);
        btnReset.setFocusPainted(false);
        btnReset.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnReset.addActionListener(this);

        String[] tieuChiTim = {"Mã thuốc", "Mã đơn vị", "Tên thuốc"};
        cboTieuChi = new JComboBox<>(tieuChiTim);
        cboTieuChi.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cboTieuChi.setBackground(Color.WHITE);
        cboTieuChi.setForeground(TEXT_COLOR);
        cboTieuChi.setFocusable(false);
        cboTieuChi.setPreferredSize(new Dimension(150, 40));
        cboTieuChi.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        cboTieuChi.addActionListener(this);

        searchPanel.add(btnQuayLai);
        searchPanel.add(txtTim);
        searchPanel.add(btnTim);
        searchPanel.add(btnReset);
        searchPanel.add(cboTieuChi);

        return searchPanel;
    }

    private void initInputForm() {
        Font fontLabel = new Font("Segoe UI", Font.BOLD, 14);
        Font fontText = new Font("Segoe UI", Font.PLAIN, 14);

        lblMaThuoc = new JLabel("Mã thuốc:");
        lblMaThuoc.setFont(fontLabel);

        lblTenThuoc = new JLabel("Tên thuốc:");
        lblTenThuoc.setFont(fontLabel);

        txtMaThuoc = new JTextField();
        txtMaThuoc.setFont(fontText);
        txtMaThuoc.setPreferredSize(new Dimension(200, 35));
        txtMaThuoc.setEditable(false);
        txtMaThuoc.setBackground(new Color(240, 240, 240));

        txtTenThuoc = new JTextField();
        txtTenThuoc.setFont(fontText);
        txtTenThuoc.setPreferredSize(new Dimension(200, 35));
        txtTenThuoc.setEditable(false);
        txtTenThuoc.setBackground(new Color(240, 240, 240));
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
        pnlForm.add(lblMaThuoc, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.4;
        pnlForm.add(txtMaThuoc, gbc);

        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 0.1;
        pnlForm.add(lblTenThuoc, gbc);

        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.weightx = 0.4;
        pnlForm.add(txtTenThuoc, gbc);

        return pnlForm;
    }

    private void initButtons() {
        btnThem = createStyledButton("Chọn thuốc cần thêm", BTN_ADD_COLOR);
        btnThem.setPreferredSize(new Dimension(180, 45));

        btnXoa = createStyledButton("Xóa", BTN_DELETE_COLOR);
        btnLuu = createStyledButton("Lưu", PRIMARY_COLOR);

        btnThem.addActionListener(this);
        btnXoa.addActionListener(this);
        btnLuu.addActionListener(this);
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(160, 45));
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
        pnlButtons.add(btnXoa);
        pnlButtons.add(btnLuu);
        return pnlButtons;
    }

    private JPanel createBangGiaInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        TitledBorder border = BorderFactory.createTitledBorder("Thông tin bảng giá");
        border.setTitleFont(new Font("Segoe UI", Font.BOLD, 14));
        panel.setBorder(border);

        if (bangGia == null) {
            addRow(panel, new GridBagConstraints(), 0, "Thông báo:", "Đang tải thông tin bảng giá...");
            return panel;
        }

        int row = 0;
        row = addRow(panel, new GridBagConstraints(), row, "Mã bảng giá:", bangGia.getMaBangGia());
        row = addRow(panel, new GridBagConstraints(), row, "Tên bảng giá:", bangGia.getTenBangGia());
        row = addRow(panel, new GridBagConstraints(), row, "Loại bảng giá:", bangGia.getLoaiGia());
        row = addRow(panel, new GridBagConstraints(), row, "Độ ưu tiên:", String.valueOf(bangGia.getDoUuTien()));
        row = addRow(panel, new GridBagConstraints(), row, "Ngày áp dụng:", String.valueOf(bangGia.getNgayApDung()));
        row = addRow(panel, new GridBagConstraints(), row, "Ngày kết thúc:", String.valueOf(bangGia.getNgayKetThuc()));
        row = addRow(panel, new GridBagConstraints(), row, "Trạng thái:", bangGia.getTrangThai());
        addRow(panel, new GridBagConstraints(), row, "Ghi chú:", bangGia.getGhiChu());

        return panel;
    }

    private int addRow(JPanel panel, GridBagConstraints gbc, int row, String labelText, String valueText) {
        gbc.insets = new Insets(8, 10, 8, 10);

        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JTextArea ta = new JTextArea(valueText == null ? "" : valueText);
        ta.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        ta.setLineWrap(true);
        ta.setWrapStyleWord(true);
        ta.setEditable(false);
        ta.setOpaque(false);
        ta.setBorder(null);
        ta.setRows(1);

        panel.add(ta, gbc);

        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;

        return row + 1;
    }

    public JPanel createBotPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 15));
        panel.setBackground(BG_COLOR);

        JPanel panelSubBot = new JPanel(new BorderLayout(5, 15));
        panelSubBot.setBackground(BG_COLOR);

        JPanel panelAddButton = new JPanel(new BorderLayout(5, 15));
        panelAddButton.setBackground(BG_COLOR);

        tblChiTietBangGia = new JTable(dtmChiTietBangGia) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(242, 242, 242));
                }
                return c;
            }
        };

        tblChiTietBangGia.addMouseListener(this);
        tblChiTietBangGia.setRowHeight(35);
        tblChiTietBangGia.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tblChiTietBangGia.setFillsViewportHeight(true);
        tblChiTietBangGia.setShowGrid(true);
        tblChiTietBangGia.setGridColor(new Color(224, 224, 224));
        tblChiTietBangGia.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblChiTietBangGia.setSelectionBackground(new Color(178, 223, 219));
        tblChiTietBangGia.setSelectionForeground(Color.BLACK);

        JTableHeader headerChiTietBangGia = tblChiTietBangGia.getTableHeader();
        headerChiTietBangGia.setFont(new Font("Segoe UI", Font.BOLD, 14));
        headerChiTietBangGia.setBackground(PRIMARY_COLOR);
        headerChiTietBangGia.setForeground(Color.WHITE);
        headerChiTietBangGia.setPreferredSize(new Dimension(headerChiTietBangGia.getWidth(), 40));
        headerChiTietBangGia.setReorderingAllowed(false);

        DefaultTableCellRenderer centerRendererChiTietBangGia = (DefaultTableCellRenderer) headerChiTietBangGia.getDefaultRenderer();
        centerRendererChiTietBangGia.setHorizontalAlignment(JLabel.CENTER);

        DefaultTableCellRenderer cellCenterChiTietBangGia = new DefaultTableCellRenderer();
        cellCenterChiTietBangGia.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < tblChiTietBangGia.getColumnCount(); i++) {
            tblChiTietBangGia.getColumnModel().getColumn(i).setCellRenderer(cellCenterChiTietBangGia);
        }

        JScrollPane scrollPaneChiTietBangGia = new JScrollPane(tblChiTietBangGia);
        scrollPaneChiTietBangGia.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        scrollPaneChiTietBangGia.getViewport().setBackground(Color.WHITE);

        panelAddButton.add(createInputPanel(), BorderLayout.CENTER);
        panelAddButton.add(createButtonPanel(), BorderLayout.SOUTH);

        panelSubBot.add(panelAddButton, BorderLayout.NORTH);
        panelSubBot.add(scrollPaneChiTietBangGia, BorderLayout.CENTER);

        pnlBangGiaInfoHost = new JPanel(new BorderLayout());
        pnlBangGiaInfoHost.setBackground(BG_COLOR);
        pnlBangGiaInfoHost.add(createBangGiaInfoPanel(), BorderLayout.CENTER);

        panel.add(pnlBangGiaInfoHost, BorderLayout.WEST);
        panel.add(panelSubBot, BorderLayout.CENTER);

        return panel;
    }

    public void loadChiTietBangGiaData() {
        SwingWorker<List<ChiTietBangGiaView>, Void> worker = new SwingWorker<List<ChiTietBangGiaView>, Void>() {
            @Override
            protected List<ChiTietBangGiaView> doInBackground() {
                bangGia = bangGiaClientController.findByMaItem(maBangGia);
                return bangGiaClientController.getChiTietBangGiaItems(maBangGia);
            }

            @Override
            protected void done() {
                try {
                    List<ChiTietBangGiaView> items = get();
                    dsChiTietBangGia = new ArrayList<>(items);
                    dtmChiTietBangGia.setRowCount(0);

                    for (ChiTietBangGiaView ctbg : dsChiTietBangGia) {
                        dtmChiTietBangGia.addRow(new Object[]{
                                ctbg.getMaBangGia(),
                                ctbg.getMaThuoc(),
                                ctbg.getTenThuoc(),
                                ctbg.getDonGia(),
                                ctbg.getMaDonVi()
                        });
                    }

                    refreshBangGiaInfoPanel();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(
                            CapNhatChiTietBangGiaPanel.this,
                            "Tải chi tiết bảng giá thất bại: " + e.getMessage(),
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };
        worker.execute();
    }

    private void refreshBangGiaInfoPanel() {
        if (pnlBangGiaInfoHost != null) {
            pnlBangGiaInfoHost.removeAll();
            pnlBangGiaInfoHost.add(createBangGiaInfoPanel(), BorderLayout.CENTER);
            pnlBangGiaInfoHost.revalidate();
            pnlBangGiaInfoHost.repaint();
        }
    }

    public void loadKetQuaTim(ArrayList<ChiTietBangGiaView> ketQua) {
        dtmChiTietBangGia.setRowCount(0);
        for (ChiTietBangGiaView ct : ketQua) {
            dtmChiTietBangGia.addRow(new Object[]{
                    ct.getMaBangGia(),
                    ct.getMaThuoc(),
                    ct.getTenThuoc(),
                    ct.getDonGia(),
                    ct.getMaDonVi()
            });
        }
    }

    public void xoaTrang() {
        txtMaThuoc.setText("");
        txtTenThuoc.setText("");
        if (tblChiTietBangGia != null) {
            tblChiTietBangGia.clearSelection();
        }
        loadChiTietBangGiaData();
    }

    public void nhanDanhSachThuocDaChon(ArrayList<ThuocBangGiaView> listThuoc) {
        if (listThuoc == null || listThuoc.isEmpty()) {
            return;
        }

        ArrayList<ThuocBangGiaView> dsThuoc = new ArrayList<>(listThuoc);
        int soThuocThemThanhCong = 0;
        int soThuocDaTonTai = 0;

        if (dsChiTietBangGia == null) {
            dsChiTietBangGia = new ArrayList<>();
        }

        for (ThuocBangGiaView thuoc : dsThuoc) {
            boolean daTonTai = dsChiTietBangGia.stream()
                    .anyMatch(ct -> ct.getMaThuoc().equals(thuoc.getMaThuoc()));

            if (daTonTai) {
                soThuocDaTonTai++;
                continue;
            }

            ChiTietBangGiaPayload payload = new ChiTietBangGiaPayload(
                    maBangGia,
                    thuoc.getMaThuoc(),
                    thuoc.getGiaHienTai(),
                    thuoc.getMaDonVi(),
                    true
            );

            try {
                Response response = bangGiaClientController.addChiTietBangGia(payload);
                if (response.isSuccess()) {
                    soThuocThemThanhCong++;
                }
            } catch (Exception ignored) {
                // Đếm số lượng theo kết quả cuối cùng
            }
        }

        StringBuilder message = new StringBuilder();
        if (soThuocThemThanhCong > 0) {
            message.append("Đã thêm thành công ").append(soThuocThemThanhCong).append(" thuốc vào bảng giá!\n");
        }
        if (soThuocDaTonTai > 0) {
            message.append(soThuocDaTonTai).append(" thuốc đã tồn tại trong bảng giá.\n");
        }
        if (soThuocThemThanhCong == 0 && soThuocDaTonTai == 0) {
            message.append("Không có thuốc nào được thêm vào!");
        }

        JOptionPane.showMessageDialog(this, message.toString(), "Thông báo", JOptionPane.INFORMATION_MESSAGE);

        loadChiTietBangGiaData();
        quayLaiDanhSach();
    }

    public void quayLaiDanhSach() {
        try {
            mainContainer.remove(mainContainer.getComponent(1));
        } catch (Exception ex) {
            // Không có panel con
        }
        cardLayout.show(mainContainer, "DanhSach");
        loadChiTietBangGiaData();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();

        if (o == btnQuayLai) {
            parentPanel.quayLaiDanhSach();
        } else if (o == btnTim) {
            ArrayList<ChiTietBangGiaView> ketQuaTim = new ArrayList<>();
            String timString = txtTim.getText().toLowerCase().trim();

            if (timString.equals("nhập từ khóa tìm kiếm...") || timString.isBlank()) {
                loadChiTietBangGiaData();
            } else {
                if (tieuChi.equals("Mã thuốc")) {
                    for (ChiTietBangGiaView ct : dsChiTietBangGia) {
                        if (ct.getMaThuoc().toLowerCase().startsWith(timString)) {
                            ketQuaTim.add(ct);
                        }
                    }
                } else if (tieuChi.equals("Mã đơn vị")) {
                    for (ChiTietBangGiaView ct : dsChiTietBangGia) {
                        if (ct.getMaDonVi() != null && ct.getMaDonVi().toLowerCase().startsWith(timString)) {
                            ketQuaTim.add(ct);
                        }
                    }
                } else {
                    for (ChiTietBangGiaView ct : dsChiTietBangGia) {
                        if (ct.getTenThuoc() != null && ct.getTenThuoc().toLowerCase().contains(timString)) {
                            ketQuaTim.add(ct);
                        }
                    }
                }

                if (ketQuaTim.isEmpty()) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Không tìm thấy kết quả nào!",
                            "Thông báo",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                    loadChiTietBangGiaData();
                    txtTim.setText("Nhập từ khóa tìm kiếm...");
                    txtTim.setForeground(Color.GRAY);
                    txtTim.setFont(new Font("Segoe UI", Font.ITALIC, 14));
                } else {
                    loadKetQuaTim(ketQuaTim);
                }
            }
        } else if (o == btnReset) {
            txtTim.setText("Nhập từ khóa tìm kiếm...");
            txtTim.setForeground(Color.GRAY);
            txtTim.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            cboTieuChi.setSelectedIndex(0);
            tieuChi = "Mã thuốc";
            loadChiTietBangGiaData();
            txtTim.requestFocus();
        } else if (o == cboTieuChi) {
            tieuChi = cboTieuChi.getSelectedItem().toString();
        } else if (o == btnXoa) {
            int selectedRow = tblChiTietBangGia.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn chi tiết bảng giá cần xóa!");
                return;
            }

            String maThuoc = tblChiTietBangGia.getValueAt(selectedRow, 1).toString();
            String tenThuoc = tblChiTietBangGia.getValueAt(selectedRow, 2).toString();

            int option = JOptionPane.showConfirmDialog(
                    this,
                    "Có chắc muốn xóa thuốc \"" + tenThuoc + "\" khỏi bảng giá?",
                    "Xác nhận",
                    JOptionPane.YES_NO_OPTION
            );

            if (option == JOptionPane.YES_OPTION) {
                try {
                    Response response = bangGiaClientController.deleteChiTietBangGia(maBangGia, maThuoc);
                    if (response.isSuccess()) {
                        JOptionPane.showMessageDialog(this, "Xóa chi tiết bảng giá thành công!");
                        loadChiTietBangGiaData();
                        xoaTrang();
                    } else {
                        JOptionPane.showMessageDialog(this, response.getMessage());
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Xóa chi tiết bảng giá thất bại: " + ex.getMessage());
                }
            }
        } else if (o == btnThem) {
            JPanel pnlChonThuoc = new CapNhatChiTietBangGiaSubPanel(this, bangGiaClientController);

            try {
                mainContainer.remove(mainContainer.getComponent(1));
            } catch (Exception ex) {
                // Không có panel con cũ
            }

            mainContainer.add(pnlChonThuoc, "ChiTiet");
            cardLayout.show(mainContainer, "ChiTiet");
        } else if (o == btnLuu) {
            boolean allSuccess = true;
            for (int i = 0; i < tblChiTietBangGia.getRowCount(); i++) {
                String maThuoc = tblChiTietBangGia.getValueAt(i, 1).toString();
                String maDonVi = tblChiTietBangGia.getValueAt(i, 4).toString();

                double giaBan;
                try {
                    giaBan = Double.parseDouble(tblChiTietBangGia.getValueAt(i, 3).toString());
                } catch (NumberFormatException ex) {
                    giaBan = 0;
                }

                ChiTietBangGiaPayload payload = new ChiTietBangGiaPayload(
                        maBangGia,
                        maThuoc,
                        giaBan,
                        maDonVi,
                        true
                );

                try {
                    Response response = bangGiaClientController.updateChiTietBangGia(payload);
                    if (!response.isSuccess()) {
                        allSuccess = false;
                    }
                } catch (Exception ex) {
                    allSuccess = false;
                }
            }

            if (allSuccess) {
                JOptionPane.showMessageDialog(this, "Lưu thay đổi thành công!");
                parentPanel.quayLaiDanhSach();
            } else {
                JOptionPane.showMessageDialog(this, "Có lỗi xảy ra khi lưu một số chi tiết!");
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getSource() == tblChiTietBangGia) {
            int selectedRow = tblChiTietBangGia.getSelectedRow();
            if (selectedRow != -1) {
                txtMaThuoc.setText(tblChiTietBangGia.getValueAt(selectedRow, 1).toString());
                txtTenThuoc.setText(tblChiTietBangGia.getValueAt(selectedRow, 2).toString());
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
