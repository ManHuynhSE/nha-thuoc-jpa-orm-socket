package com.pillchill.migration.gui.capnhat;

import com.pillchill.migration.dto.ThuocBangGiaView;
import com.pillchill.migration.network.client.BangGiaClientController;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.border.TitledBorder;
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class CapNhatChiTietBangGiaSubPanel extends JPanel implements ActionListener {

    private final Color PRIMARY_COLOR = new Color(0, 150, 136);
    private final Color BG_COLOR = new Color(245, 245, 245);
    private final Color TEXT_COLOR = new Color(51, 51, 51);

    private final Color BTN_ADD_COLOR = new Color(46, 204, 113);
    private final Color BTN_BACK_COLOR = new Color(70, 70, 70);
    private final Color BTN_CLEAR_COLOR = new Color(149, 165, 166);

    private JLabel lblTieuDe;
    private JTextField txtTim;
    private JComboBox<String> cboTieuChi;
    private String tieuChi = "Tên thuốc";

    private JTable tblThuoc;
    private DefaultTableModel dtmThuoc;
    private JTable tblThuocDaChon;
    private DefaultTableModel dtmThuocDaChon;
    private ArrayList<ThuocBangGiaView> dsThuoc;
    private ArrayList<ThuocBangGiaView> dsThuocDaChon;

    private JButton btnTim;
    private JButton btnReset;
    private JButton btnChon;
    private JButton btnBoChon;
    private JButton btnXacNhan;
    private JButton btnQuayLai;

    private final CapNhatChiTietBangGiaPanel parentPanel;
    private final BangGiaClientController bangGiaClientController;

    public CapNhatChiTietBangGiaSubPanel(
            CapNhatChiTietBangGiaPanel parentPanel,
            BangGiaClientController bangGiaClientController
    ) {
        this.parentPanel = parentPanel;
        this.bangGiaClientController = bangGiaClientController;
        this.dsThuoc = new ArrayList<>();
        this.dsThuocDaChon = new ArrayList<>();

        setLayout(new BorderLayout());
        setBackground(BG_COLOR);

        initComponents();
        loadThuocData();
    }

    private void initComponents() {
        lblTieuDe = new JLabel("CHỌN THUỐC CẦN THÊM VÀO BẢNG GIÁ", SwingConstants.CENTER);
        lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTieuDe.setForeground(PRIMARY_COLOR);
        lblTieuDe.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JPanel searchPanel = createSearchPanel();
        JPanel tablePanel = createTablePanel();
        JPanel buttonPanel = createButtonPanel();

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(BG_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel topPanel = new JPanel(new BorderLayout(0, 15));
        topPanel.setBackground(BG_COLOR);
        topPanel.add(lblTieuDe, BorderLayout.NORTH);
        topPanel.add(searchPanel, BorderLayout.CENTER);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(tablePanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 15));
        searchPanel.setBackground(BG_COLOR);

        String[] tieuChiTim = {"Tên thuốc", "Mã thuốc"};
        cboTieuChi = new JComboBox<>(tieuChiTim);
        cboTieuChi.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cboTieuChi.setBackground(Color.WHITE);
        cboTieuChi.setForeground(TEXT_COLOR);
        cboTieuChi.setFocusable(false);
        cboTieuChi.setPreferredSize(new Dimension(150, 40));
        cboTieuChi.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        cboTieuChi.addActionListener(this);

        txtTim = new JTextField(25);
        txtTim.setText("Nhập từ khóa tìm kiếm...");
        txtTim.setForeground(Color.GRAY);
        txtTim.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        txtTim.setBackground(Color.WHITE);
        txtTim.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        txtTim.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (txtTim.getText().equals("Nhập từ khóa tìm kiếm...")) {
                    txtTim.setText("");
                    txtTim.setForeground(Color.BLACK);
                    txtTim.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                }
            }

            public void focusLost(java.awt.event.FocusEvent evt) {
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

        searchPanel.add(cboTieuChi);
        searchPanel.add(txtTim);
        searchPanel.add(btnTim);
        searchPanel.add(btnReset);

        return searchPanel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_COLOR);

        String[] colsThuoc = {"Mã thuốc", "Tên thuốc", "Mã đơn vị", "Giá chuẩn", "Giá hiện tại"};

        dtmThuoc = new DefaultTableModel(colsThuoc, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblThuoc = createCustomTable(dtmThuoc);
        JScrollPane scrollPaneThuoc = new JScrollPane(tblThuoc);
        scrollPaneThuoc.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                "Danh sách thuốc có sẵn",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                new Font("Segoe UI", Font.BOLD, 14),
                PRIMARY_COLOR
        ));
        scrollPaneThuoc.getViewport().setBackground(Color.WHITE);

        dtmThuocDaChon = new DefaultTableModel(colsThuoc, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblThuocDaChon = createCustomTable(dtmThuocDaChon);
        JScrollPane scrollPaneDaChon = new JScrollPane(tblThuocDaChon);
        scrollPaneDaChon.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                "Danh sách thuốc đã chọn",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                new Font("Segoe UI", Font.BOLD, 14),
                PRIMARY_COLOR
        ));
        scrollPaneDaChon.getViewport().setBackground(Color.WHITE);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollPaneThuoc, scrollPaneDaChon);
        splitPane.setResizeWeight(0.5);
        splitPane.setDividerSize(10);
        splitPane.setBorder(null);

        panel.add(splitPane, BorderLayout.CENTER);
        return panel;
    }

    private JTable createCustomTable(DefaultTableModel model) {
        JTable table = new JTable(model) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(242, 242, 242));
                }
                return c;
            }
        };

        table.setRowHeight(35);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setFillsViewportHeight(true);
        table.setShowGrid(true);
        table.setGridColor(new Color(224, 224, 224));
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        table.setSelectionBackground(new Color(178, 223, 219));
        table.setSelectionForeground(Color.BLACK);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 40));
        header.setReorderingAllowed(false);

        DefaultTableCellRenderer centerRenderer = (DefaultTableCellRenderer) header.getDefaultRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        DefaultTableCellRenderer cellCenter = new DefaultTableCellRenderer();
        cellCenter.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(cellCenter);
        }

        return table;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        panel.setBackground(BG_COLOR);

        btnChon = new JButton("Thêm xuống");
        btnChon.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnChon.setPreferredSize(new Dimension(150, 45));
        btnChon.setBackground(BTN_ADD_COLOR);
        btnChon.setForeground(Color.WHITE);
        btnChon.setBorderPainted(false);
        btnChon.setFocusPainted(false);
        btnChon.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnChon.addActionListener(this);

        btnBoChon = new JButton("Bỏ chọn");
        btnBoChon.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnBoChon.setPreferredSize(new Dimension(150, 45));
        btnBoChon.setBackground(new Color(231, 76, 60));
        btnBoChon.setForeground(Color.WHITE);
        btnBoChon.setBorderPainted(false);
        btnBoChon.setFocusPainted(false);
        btnBoChon.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBoChon.addActionListener(this);

        btnXacNhan = new JButton("Xác nhận");
        btnXacNhan.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnXacNhan.setPreferredSize(new Dimension(150, 45));
        btnXacNhan.setBackground(PRIMARY_COLOR);
        btnXacNhan.setForeground(Color.WHITE);
        btnXacNhan.setBorderPainted(false);
        btnXacNhan.setFocusPainted(false);
        btnXacNhan.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnXacNhan.addActionListener(this);

        btnQuayLai = new JButton("Quay lại");
        btnQuayLai.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnQuayLai.setPreferredSize(new Dimension(150, 45));
        btnQuayLai.setBackground(BTN_BACK_COLOR);
        btnQuayLai.setForeground(Color.WHITE);
        btnQuayLai.setBorderPainted(false);
        btnQuayLai.setFocusPainted(false);
        btnQuayLai.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnQuayLai.addActionListener(this);

        panel.add(btnChon);
        panel.add(btnBoChon);
        panel.add(btnXacNhan);
        panel.add(btnQuayLai);

        return panel;
    }

    private void loadThuocData() {
        SwingWorker<List<ThuocBangGiaView>, Void> worker = new SwingWorker<List<ThuocBangGiaView>, Void>() {
            @Override
            protected List<ThuocBangGiaView> doInBackground() {
                return bangGiaClientController.getAllThuocKemGiaItems();
            }

            @Override
            protected void done() {
                try {
                    List<ThuocBangGiaView> items = get();
                    dsThuoc = new ArrayList<>(items);
                    dsThuocDaChon.clear();
                    updateTables();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(
                            CapNhatChiTietBangGiaSubPanel.this,
                            "Tải danh sách thuốc thất bại: " + e.getMessage(),
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };
        worker.execute();
    }

    private void updateTables() {
        dtmThuoc.setRowCount(0);
        String timString = txtTim.getText().toLowerCase().trim();
        boolean isSearching = !timString.equals("nhập từ khóa tìm kiếm...") && !timString.isBlank();

        for (ThuocBangGiaView thuoc : dsThuoc) {
            boolean isSelected = dsThuocDaChon.stream()
                    .anyMatch(t -> t.getMaThuoc().equals(thuoc.getMaThuoc()));

            if (!isSelected) {
                boolean match = true;
                if (isSearching) {
                    if (tieuChi.equals("Tên thuốc")) {
                        match = thuoc.getTenThuoc().toLowerCase().contains(timString);
                    } else if (tieuChi.equals("Mã thuốc")) {
                        match = thuoc.getMaThuoc().toLowerCase().startsWith(timString);
                    }
                }

                if (match) {
                    dtmThuoc.addRow(new Object[]{
                            thuoc.getMaThuoc(),
                            thuoc.getTenThuoc(),
                            thuoc.getMaDonVi(),
                            String.format("%.0f", thuoc.getGiaChuan()),
                            String.format("%.0f", thuoc.getGiaHienTai())
                    });
                }
            }
        }

        dtmThuocDaChon.setRowCount(0);
        for (ThuocBangGiaView thuoc : dsThuocDaChon) {
            dtmThuocDaChon.addRow(new Object[]{
                    thuoc.getMaThuoc(),
                    thuoc.getTenThuoc(),
                    thuoc.getMaDonVi(),
                    String.format("%.0f", thuoc.getGiaChuan()),
                    String.format("%.0f", thuoc.getGiaHienTai())
            });
        }
    }

    private void themThuoc() {
        int[] selectedRows = tblThuoc.getSelectedRows();

        if (selectedRows.length == 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Vui lòng chọn ít nhất một thuốc để thêm!",
                    "Thông báo",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        for (int rowIndex : selectedRows) {
            String maThuoc = tblThuoc.getValueAt(rowIndex, 0).toString();

            ThuocBangGiaView thuoc = dsThuoc.stream()
                    .filter(t -> t.getMaThuoc().equals(maThuoc))
                    .findFirst()
                    .orElse(null);

            if (thuoc != null) {
                dsThuocDaChon.add(thuoc);
            }
        }

        updateTables();
    }

    private void boChonThuoc() {
        int[] selectedRows = tblThuocDaChon.getSelectedRows();

        if (selectedRows.length == 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Vui lòng chọn ít nhất một thuốc để bỏ chọn!",
                    "Thông báo",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        ArrayList<ThuocBangGiaView> toRemove = new ArrayList<>();
        for (int rowIndex : selectedRows) {
            String maThuoc = tblThuocDaChon.getValueAt(rowIndex, 0).toString();

            ThuocBangGiaView thuoc = dsThuocDaChon.stream()
                    .filter(t -> t.getMaThuoc().equals(maThuoc))
                    .findFirst()
                    .orElse(null);

            if (thuoc != null) {
                toRemove.add(thuoc);
            }
        }

        dsThuocDaChon.removeAll(toRemove);
        updateTables();
    }

    private void xacNhan() {
        if (dsThuocDaChon.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Vui lòng chọn ít nhất một thuốc!",
                    "Thông báo",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Xác nhận thêm " + dsThuocDaChon.size() + " thuốc vào bảng giá?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            parentPanel.nhanDanhSachThuocDaChon(new ArrayList<>(dsThuocDaChon));
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();

        if (o == btnTim) {
            updateTables();
        } else if (o == btnReset) {
            txtTim.setText("Nhập từ khóa tìm kiếm...");
            txtTim.setForeground(Color.GRAY);
            txtTim.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            cboTieuChi.setSelectedIndex(0);
            tieuChi = "Tên thuốc";
            loadThuocData();
            txtTim.requestFocus();
        } else if (o == cboTieuChi) {
            tieuChi = cboTieuChi.getSelectedItem().toString();
        } else if (o == btnChon) {
            themThuoc();
        } else if (o == btnBoChon) {
            boChonThuoc();
        } else if (o == btnXacNhan) {
            xacNhan();
        } else if (o == btnQuayLai) {
            int option = JOptionPane.showConfirmDialog(
                    this,
                    "Bạn có chắc muốn quay lại? Danh sách thuốc đã chọn sẽ bị hủy.",
                    "Xác nhận",
                    JOptionPane.YES_NO_OPTION
            );

            if (option == JOptionPane.YES_OPTION) {
                dsThuocDaChon.clear();
                parentPanel.quayLaiDanhSach();
            }
        }
    }
}
