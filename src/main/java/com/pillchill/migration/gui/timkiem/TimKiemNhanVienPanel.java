package com.pillchill.migration.gui.timkiem;

import com.pillchill.migration.entity.NhanVien;
import com.pillchill.migration.entity.ChucVu;
import com.pillchill.migration.network.client.NhanVienClientController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TimKiemNhanVienPanel extends JPanel implements ActionListener {
    private final Color PRIMARY_COLOR = new Color(0, 150, 136);
    private final NhanVienClientController nhanVienClientController;

    private JTextField txtMaNV;
    private JTextField txtTenNV;
    private JTextField txtSoDienThoai;
    private JComboBox<String> cboChucVu;
    private JButton btnXoaTrang;
    private JButton btnTimKiem;
    private JLabel lblTongSoBanGhi;

    private DefaultTableModel tableModel;
    private JTable tblNhanVien;
    private List<NhanVien> dsNhanVien;

    public TimKiemNhanVienPanel(NhanVienClientController nhanVienClientController) {
        this.nhanVienClientController = nhanVienClientController;
        this.dsNhanVien = new ArrayList<>();
        initUI();
        loadDataFromServer();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        add(createTopPanel(), BorderLayout.NORTH);
        add(createCenterPanel(), BorderLayout.CENTER);
    }

    private JPanel createTopPanel() {
        JPanel pnlMain = new JPanel(new BorderLayout());
        pnlMain.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 30));

        JLabel lblTieuDe = new JLabel("TÌM KIẾM NHÂN VIÊN", SwingConstants.CENTER);
        lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTieuDe.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JPanel pnlForm = new JPanel(new GridLayout(2, 2, 10, 5));

        txtMaNV = new JTextField(15);
        txtTenNV = new JTextField(15);
        txtSoDienThoai = new JTextField(15);
        cboChucVu = new JComboBox<>();
        cboChucVu.addItem("");

        pnlForm.add(createFieldPanel("Mã nhân viên:", txtMaNV));
        pnlForm.add(createFieldPanel("Tên nhân viên:", txtTenNV));
        pnlForm.add(createFieldPanel("Số điện thoại:", txtSoDienThoai));
        pnlForm.add(createFieldPanel("Chức vụ:", cboChucVu));

        btnXoaTrang = new JButton("Xóa trắng");
        btnTimKiem = new JButton("Tìm kiếm");
        btnXoaTrang.addActionListener(this);
        btnTimKiem.addActionListener(this);

        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlButtons.add(btnXoaTrang);
        pnlButtons.add(btnTimKiem);

        pnlMain.add(lblTieuDe, BorderLayout.NORTH);
        pnlMain.add(pnlForm, BorderLayout.CENTER);
        pnlMain.add(pnlButtons, BorderLayout.SOUTH);
        return pnlMain;
    }

    private JPanel createFieldPanel(String label, JComponent input) {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lbl.setPreferredSize(new Dimension(170, 0));
        panel.add(lbl, BorderLayout.WEST);
        panel.add(input, BorderLayout.CENTER);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        return panel;
    }

    private JPanel createCenterPanel() {
        JPanel centerPanel = new JPanel(new BorderLayout(0, 20));
        centerPanel.setBorder(new EmptyBorder(20, 40, 20, 40));

        String[] cols = {"Mã nhân viên", "Tên nhân viên", "Chức vụ", "Số điện thoại"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblNhanVien = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(242, 242, 242));
                }
                return c;
            }
        };
        tblNhanVien.setRowHeight(35);
        tblNhanVien.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tblNhanVien.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JTableHeader header = tblNhanVien.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 40));
        DefaultTableCellRenderer centerRenderer = (DefaultTableCellRenderer) header.getDefaultRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        JScrollPane scrollPane = new JScrollPane(tblNhanVien);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        lblTongSoBanGhi = new JLabel("Tổng số bản ghi: 0");
        lblTongSoBanGhi.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        centerPanel.add(lblTongSoBanGhi, BorderLayout.SOUTH);
        return centerPanel;
    }

    private void loadDataFromServer() {
        SwingWorker<List<NhanVien>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<NhanVien> doInBackground() {
                return nhanVienClientController.getAllNhanVienItems();
            }

            @Override
            protected void done() {
                try {
                    dsNhanVien = new ArrayList<>(get());
                    loadFilterData(dsNhanVien);
                    loadTableData(dsNhanVien);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(
                            TimKiemNhanVienPanel.this,
                            "Không tải được danh sách nhân viên: " + e.getMessage(),
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };
        worker.execute();
    }

    private void loadFilterData(List<NhanVien> list) {
        Set<String> chucVuSet = new HashSet<>();
        for (NhanVien item : list) {
            if (item.getChucVu() != null && item.getChucVu().getTenChucVu() != null && !item.getChucVu().getTenChucVu().isBlank()) {
                chucVuSet.add(item.getChucVu().getTenChucVu());
            }
        }

        cboChucVu.removeAllItems();
        cboChucVu.addItem("");
        chucVuSet.stream().sorted().forEach(cboChucVu::addItem);
    }

    private void loadTableData(List<NhanVien> list) {
        tableModel.setRowCount(0);
        for (NhanVien item : list) {
            String tenChucVu = item.getChucVu() != null ? item.getChucVu().getTenChucVu() : "";
            tableModel.addRow(new Object[]{
                    item.getMaNV(),
                    item.getTenNV(),
                    tenChucVu,
                    item.getSoDienThoai()
            });
        }
        lblTongSoBanGhi.setText("Tổng số bản ghi: " + list.size());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        if (o == btnXoaTrang) {
            txtMaNV.setText("");
            txtTenNV.setText("");
            txtSoDienThoai.setText("");
            cboChucVu.setSelectedIndex(0);
            loadTableData(dsNhanVien);
            return;
        }

        if (o == btnTimKiem) {
            String maNVTim = txtMaNV.getText().trim().toLowerCase();
            String tenNVTim = txtTenNV.getText().trim().toLowerCase();
            String sdtTim = txtSoDienThoai.getText().trim().toLowerCase();
            String chucVuChon = (String) cboChucVu.getSelectedItem();

            List<NhanVien> ketQua = new ArrayList<>();
            for (NhanVien nv : dsNhanVien) {
                boolean match = true;

                if (!maNVTim.isEmpty() && (nv.getMaNV() == null || !nv.getMaNV().toLowerCase().contains(maNVTim))) {
                    match = false;
                }

                if (match && !tenNVTim.isEmpty() && (nv.getTenNV() == null || !nv.getTenNV().toLowerCase().contains(tenNVTim))) {
                    match = false;
                }

                if (match && !sdtTim.isEmpty()) {
                    String sdt = nv.getSoDienThoai() == null ? "" : nv.getSoDienThoai();
                    if (!sdt.toLowerCase().contains(sdtTim)) {
                        match = false;
                    }
                }

                if (match && chucVuChon != null && !chucVuChon.isEmpty()) {
                    String tenChucVu = nv.getChucVu() != null ? nv.getChucVu().getTenChucVu() : "";
                    if (!chucVuChon.equalsIgnoreCase(tenChucVu)) {
                        match = false;
                    }
                }

                if (match) {
                    ketQua.add(nv);
                }
            }

            if (ketQua.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy kết quả phù hợp!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                loadTableData(dsNhanVien);
            } else {
                loadTableData(ketQua);
            }
        }
    }
}
