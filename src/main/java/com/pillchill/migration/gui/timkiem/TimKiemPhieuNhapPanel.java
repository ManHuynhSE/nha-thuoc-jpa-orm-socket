package com.pillchill.migration.gui.timkiem;

import com.pillchill.migration.dto.ChiTietPhieuNhapView;
import com.pillchill.migration.dto.PhieuNhapView;
import com.pillchill.migration.network.client.PhieuNhapClientController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TimKiemPhieuNhapPanel extends JPanel implements ActionListener {
    private final Color PRIMARY_COLOR = new Color(0, 150, 136);
    private final PhieuNhapClientController phieuNhapClientController;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private JTextField txtTuKhoaPhieuNhap;
    private JComboBox<String> cboTieuChiPhieuNhap;
    private JButton btnTimPhieuNhap;
    private JButton btnXoaPhieuNhap;

    private JTextField txtTuKhoaChiTiet;
    private JComboBox<String> cboTieuChiChiTiet;
    private JButton btnTimChiTiet;
    private JButton btnXoaChiTiet;

    private DefaultTableModel phieuNhapTableModel;
    private DefaultTableModel chiTietTableModel;
    private JTable tblPhieuNhap;
    private JTable tblChiTietPhieuNhap;
    private JLabel lblTongPhieuNhap;
    private JLabel lblTongChiTiet;

    private List<PhieuNhapView> dsPhieuNhap;
    private List<ChiTietPhieuNhapView> dsChiTietHienTai;

    public TimKiemPhieuNhapPanel(PhieuNhapClientController phieuNhapClientController) {
        this.phieuNhapClientController = phieuNhapClientController;
        this.dsPhieuNhap = new ArrayList<>();
        this.dsChiTietHienTai = new ArrayList<>();
        initUI();
        loadPhieuNhapFromServer();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));
        add(createTopPanel(), BorderLayout.NORTH);
        add(createCenterPanel(), BorderLayout.CENTER);
    }

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 10, 20));

        JLabel lblTieuDe = new JLabel("LỊCH SỬ NHẬP THUỐC", SwingConstants.CENTER);
        lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTieuDe.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        txtTuKhoaPhieuNhap = new JTextField(24);
        cboTieuChiPhieuNhap = new JComboBox<>(new String[]{"Theo mã phiếu nhập", "Theo mã nhân viên", "Theo tên nhân viên"});
        btnTimPhieuNhap = new JButton("Tìm");
        btnXoaPhieuNhap = new JButton("Xóa trắng");
        btnTimPhieuNhap.addActionListener(this);
        btnXoaPhieuNhap.addActionListener(this);

        searchPanel.add(txtTuKhoaPhieuNhap);
        searchPanel.add(btnTimPhieuNhap);
        searchPanel.add(cboTieuChiPhieuNhap);
        searchPanel.add(btnXoaPhieuNhap);

        topPanel.add(lblTieuDe, BorderLayout.NORTH);
        topPanel.add(searchPanel, BorderLayout.CENTER);
        return topPanel;
    }

    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 0, 10));
        panel.setBorder(new EmptyBorder(0, 20, 10, 20));
        panel.add(createPhieuNhapSection());
        panel.add(createChiTietSection());
        return panel;
    }

    private JPanel createPhieuNhapSection() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBorder(BorderFactory.createTitledBorder("Danh sách phiếu nhập"));

        String[] cols = {"Mã phiếu nhập", "Mã NV", "Tên NV", "Ngày nhập"};
        phieuNhapTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblPhieuNhap = createStyledTable(phieuNhapTableModel);
        tblPhieuNhap.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = tblPhieuNhap.getSelectedRow();
                if (row >= 0) {
                    String maPhieuNhap = String.valueOf(tblPhieuNhap.getValueAt(row, 0));
                    loadChiTietFromServer(maPhieuNhap);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(tblPhieuNhap);
        lblTongPhieuNhap = new JLabel("Tổng phiếu nhập: 0");
        lblTongPhieuNhap.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(lblTongPhieuNhap, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createChiTietSection() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBorder(BorderFactory.createTitledBorder("Chi tiết phiếu nhập"));

        JPanel searchDetailPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        txtTuKhoaChiTiet = new JTextField(20);
        cboTieuChiChiTiet = new JComboBox<>(new String[]{"Theo mã thuốc", "Theo mã lô", "Theo tên thuốc"});
        btnTimChiTiet = new JButton("Tìm chi tiết");
        btnXoaChiTiet = new JButton("Xóa lọc");
        btnTimChiTiet.addActionListener(this);
        btnXoaChiTiet.addActionListener(this);
        searchDetailPanel.add(txtTuKhoaChiTiet);
        searchDetailPanel.add(btnTimChiTiet);
        searchDetailPanel.add(cboTieuChiChiTiet);
        searchDetailPanel.add(btnXoaChiTiet);

        String[] cols = {"Mã thuốc", "Mã lô", "Tên thuốc", "Số lượng", "Giá nhập", "Đơn vị", "Mã NSX", "Ngày SX", "Hạn SD"};
        chiTietTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblChiTietPhieuNhap = createStyledTable(chiTietTableModel);
        JScrollPane scrollPane = new JScrollPane(tblChiTietPhieuNhap);
        lblTongChiTiet = new JLabel("Tổng dòng chi tiết: 0");
        lblTongChiTiet.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        panel.add(searchDetailPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(lblTongChiTiet, BorderLayout.SOUTH);
        return panel;
    }

    private JTable createStyledTable(DefaultTableModel tableModel) {
        JTable table = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(242, 242, 242));
                }
                return c;
            }
        };
        table.setRowHeight(34);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 36));

        DefaultTableCellRenderer centerRenderer = (DefaultTableCellRenderer) header.getDefaultRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        return table;
    }

    private void loadPhieuNhapFromServer() {
        SwingWorker<List<PhieuNhapView>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<PhieuNhapView> doInBackground() {
                return phieuNhapClientController.getAllPhieuNhapItems();
            }

            @Override
            protected void done() {
                try {
                    dsPhieuNhap = new ArrayList<>(get());
                    loadPhieuNhapTable(dsPhieuNhap);
                    clearChiTietTable();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(
                            TimKiemPhieuNhapPanel.this,
                            "Không tải được danh sách phiếu nhập: " + e.getMessage(),
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };
        worker.execute();
    }

    private void loadChiTietFromServer(String maPhieuNhapThuoc) {
        SwingWorker<List<ChiTietPhieuNhapView>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<ChiTietPhieuNhapView> doInBackground() {
                return phieuNhapClientController.getChiTietPhieuNhapItems(maPhieuNhapThuoc);
            }

            @Override
            protected void done() {
                try {
                    dsChiTietHienTai = new ArrayList<>(get());
                    loadChiTietTable(dsChiTietHienTai);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(
                            TimKiemPhieuNhapPanel.this,
                            "Không tải được chi tiết phiếu nhập: " + e.getMessage(),
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };
        worker.execute();
    }

    private void loadPhieuNhapTable(List<PhieuNhapView> list) {
        phieuNhapTableModel.setRowCount(0);
        for (PhieuNhapView item : list) {
            String ngayNhap = item.getNgayNhap() == null ? "" : item.getNgayNhap().format(dateFormatter);
            phieuNhapTableModel.addRow(new Object[]{
                    item.getMaPhieuNhapThuoc(),
                    item.getMaNhanVien() == null ? "" : item.getMaNhanVien(),
                    item.getTenNhanVien() == null ? "" : item.getTenNhanVien(),
                    ngayNhap
            });
        }
        lblTongPhieuNhap.setText("Tổng phiếu nhập: " + list.size());
    }

    private void loadChiTietTable(List<ChiTietPhieuNhapView> list) {
        chiTietTableModel.setRowCount(0);
        for (ChiTietPhieuNhapView item : list) {
            String ngaySanXuat = item.getNgaySanXuat() == null ? "" : item.getNgaySanXuat().format(dateFormatter);
            String hanSuDung = item.getHanSuDung() == null ? "" : item.getHanSuDung().format(dateFormatter);
            chiTietTableModel.addRow(new Object[]{
                    item.getMaThuoc(),
                    item.getMaLo(),
                    item.getTenThuoc() == null ? "" : item.getTenThuoc(),
                    item.getSoLuong() == null ? 0 : item.getSoLuong(),
                    item.getDonGia() == null ? "0" : String.format("%,.0f", item.getDonGia()),
                    item.getDonVi() == null ? "" : item.getDonVi(),
                    item.getMaNSX() == null ? "" : item.getMaNSX(),
                    ngaySanXuat,
                    hanSuDung
            });
        }
        lblTongChiTiet.setText("Tổng dòng chi tiết: " + list.size());
    }

    private void clearChiTietTable() {
        dsChiTietHienTai = new ArrayList<>();
        chiTietTableModel.setRowCount(0);
        lblTongChiTiet.setText("Tổng dòng chi tiết: 0");
        txtTuKhoaChiTiet.setText("");
    }

    private void applyPhieuNhapKeywordSearch() {
        String keyword = txtTuKhoaPhieuNhap.getText().trim().toLowerCase();
        if (keyword.isEmpty()) {
            loadPhieuNhapTable(dsPhieuNhap);
            clearChiTietTable();
            return;
        }

        int mode = cboTieuChiPhieuNhap.getSelectedIndex();
        List<PhieuNhapView> ketQua = new ArrayList<>();
        for (PhieuNhapView pn : dsPhieuNhap) {
            boolean match = switch (mode) {
                case 0 -> pn.getMaPhieuNhapThuoc() != null && pn.getMaPhieuNhapThuoc().toLowerCase().contains(keyword);
                case 1 -> pn.getMaNhanVien() != null && pn.getMaNhanVien().toLowerCase().contains(keyword);
                case 2 -> pn.getTenNhanVien() != null && pn.getTenNhanVien().toLowerCase().contains(keyword);
                default -> false;
            };
            if (match) {
                ketQua.add(pn);
            }
        }

        if (ketQua.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy phiếu nhập phù hợp!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            loadPhieuNhapTable(dsPhieuNhap);
        } else {
            loadPhieuNhapTable(ketQua);
        }
        clearChiTietTable();
    }

    private void applyChiTietKeywordSearch() {
        String keyword = txtTuKhoaChiTiet.getText().trim().toLowerCase();
        if (keyword.isEmpty()) {
            loadChiTietTable(dsChiTietHienTai);
            return;
        }

        int mode = cboTieuChiChiTiet.getSelectedIndex();
        List<ChiTietPhieuNhapView> ketQua = new ArrayList<>();
        for (ChiTietPhieuNhapView ct : dsChiTietHienTai) {
            boolean match = switch (mode) {
                case 0 -> ct.getMaThuoc() != null && ct.getMaThuoc().toLowerCase().contains(keyword);
                case 1 -> ct.getMaLo() != null && ct.getMaLo().toLowerCase().contains(keyword);
                case 2 -> ct.getTenThuoc() != null && ct.getTenThuoc().toLowerCase().contains(keyword);
                default -> false;
            };
            if (match) {
                ketQua.add(ct);
            }
        }

        if (ketQua.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy chi tiết phù hợp!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            loadChiTietTable(dsChiTietHienTai);
        } else {
            loadChiTietTable(ketQua);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == btnTimPhieuNhap) {
            applyPhieuNhapKeywordSearch();
            return;
        }

        if (source == btnXoaPhieuNhap) {
            txtTuKhoaPhieuNhap.setText("");
            cboTieuChiPhieuNhap.setSelectedIndex(0);
            loadPhieuNhapTable(dsPhieuNhap);
            clearChiTietTable();
            return;
        }

        if (source == btnTimChiTiet) {
            if (dsChiTietHienTai.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một phiếu nhập để xem chi tiết trước!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            applyChiTietKeywordSearch();
            return;
        }

        if (source == btnXoaChiTiet) {
            txtTuKhoaChiTiet.setText("");
            cboTieuChiChiTiet.setSelectedIndex(0);
            loadChiTietTable(dsChiTietHienTai);
        }
    }
}
