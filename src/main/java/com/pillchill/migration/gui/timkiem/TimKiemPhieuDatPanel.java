package com.pillchill.migration.gui.timkiem;

import com.pillchill.migration.dto.ChiTietPhieuDatView;
import com.pillchill.migration.dto.PhieuDatView;
import com.pillchill.migration.network.client.PhieuDatClientController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TimKiemPhieuDatPanel extends JPanel implements ActionListener {
    private final Color PRIMARY_COLOR = new Color(0, 150, 136);
    private final PhieuDatClientController phieuDatClientController;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private JTextField txtTuKhoa;
    private JComboBox<String> cboTimKiem;
    private JCheckBox chkLocThangNam;
    private JComboBox<String> cboThang;
    private JComboBox<String> cboNam;
    private JButton btnTimKiem;
    private JButton btnXoaTrang;

    private DefaultTableModel phieuDatTableModel;
    private DefaultTableModel chiTietTableModel;
    private JTable tblPhieuDat;
    private JTable tblChiTietPhieuDat;
    private JLabel lblTongPhieuDat;
    private JLabel lblTongChiTiet;

    private List<PhieuDatView> dsPhieuDat;

    public TimKiemPhieuDatPanel(PhieuDatClientController phieuDatClientController) {
        this.phieuDatClientController = phieuDatClientController;
        this.dsPhieuDat = new ArrayList<>();
        initUI();
        loadPhieuDatFromServer();
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

        JLabel lblTieuDe = new JLabel("DANH MỤC PHIẾU ĐẶT THUỐC", SwingConstants.CENTER);
        lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTieuDe.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));

        txtTuKhoa = new JTextField(24);
        cboTimKiem = new JComboBox<>(new String[]{"Theo mã phiếu đặt", "Theo tên nhân viên", "Theo tên khách hàng"});
        chkLocThangNam = new JCheckBox("Lọc theo tháng/năm");
        chkLocThangNam.setBackground(Color.WHITE);

        String[] thang = new String[12];
        for (int i = 0; i < 12; i++) {
            thang[i] = String.valueOf(i + 1);
        }
        cboThang = new JComboBox<>(thang);

        int currentYear = LocalDate.now().getYear();
        String[] nam = new String[5];
        for (int i = 0; i < 5; i++) {
            nam[i] = String.valueOf(currentYear - 2 + i);
        }
        cboNam = new JComboBox<>(nam);
        cboThang.setSelectedIndex(LocalDate.now().getMonthValue() - 1);
        cboNam.setSelectedItem(String.valueOf(currentYear));

        btnTimKiem = new JButton("Tìm");
        btnXoaTrang = new JButton("Xóa trắng");
        btnTimKiem.addActionListener(this);
        btnXoaTrang.addActionListener(this);
        chkLocThangNam.addActionListener(this);
        cboThang.addActionListener(this);
        cboNam.addActionListener(this);

        searchPanel.add(txtTuKhoa);
        searchPanel.add(btnTimKiem);
        searchPanel.add(cboTimKiem);
        searchPanel.add(chkLocThangNam);
        searchPanel.add(new JLabel("Tháng:"));
        searchPanel.add(cboThang);
        searchPanel.add(new JLabel("Năm:"));
        searchPanel.add(cboNam);
        searchPanel.add(btnXoaTrang);

        topPanel.add(lblTieuDe, BorderLayout.NORTH);
        topPanel.add(searchPanel, BorderLayout.CENTER);
        return topPanel;
    }

    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 0, 10));
        panel.setBorder(new EmptyBorder(0, 20, 10, 20));
        panel.add(createPhieuDatSection());
        panel.add(createChiTietSection());
        return panel;
    }

    private JPanel createPhieuDatSection() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBorder(BorderFactory.createTitledBorder("Danh sách phiếu đặt"));

        String[] cols = {"Mã phiếu đặt", "Tên NV", "Tên KH", "Ngày đặt", "Đã nhận", "Ghi chú"};
        phieuDatTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblPhieuDat = createStyledTable(phieuDatTableModel);
        tblPhieuDat.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = tblPhieuDat.getSelectedRow();
                if (row >= 0) {
                    String maPhieuDat = String.valueOf(tblPhieuDat.getValueAt(row, 0));
                    loadChiTietFromServer(maPhieuDat);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(tblPhieuDat);
        lblTongPhieuDat = new JLabel("Tổng phiếu đặt: 0");
        lblTongPhieuDat.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(lblTongPhieuDat, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createChiTietSection() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBorder(BorderFactory.createTitledBorder("Chi tiết phiếu đặt"));

        String[] cols = {"Tên thuốc", "Mã thuốc", "Mã lô", "Số lượng"};
        chiTietTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblChiTietPhieuDat = createStyledTable(chiTietTableModel);
        JScrollPane scrollPane = new JScrollPane(tblChiTietPhieuDat);
        lblTongChiTiet = new JLabel("Tổng dòng chi tiết: 0");
        lblTongChiTiet.setFont(new Font("Segoe UI", Font.PLAIN, 13));

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

    private void loadPhieuDatFromServer() {
        SwingWorker<List<PhieuDatView>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<PhieuDatView> doInBackground() {
                if (chkLocThangNam != null && chkLocThangNam.isSelected()) {
                    int month = Integer.parseInt((String) cboThang.getSelectedItem());
                    int year = Integer.parseInt((String) cboNam.getSelectedItem());
                    return phieuDatClientController.getPhieuDatByMonthYearItems(month, year);
                }
                return phieuDatClientController.getAllPhieuDatItems();
            }

            @Override
            protected void done() {
                try {
                    dsPhieuDat = new ArrayList<>(get());
                    loadPhieuDatTable(dsPhieuDat);
                    clearChiTietTable();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(
                            TimKiemPhieuDatPanel.this,
                            "Không tải được danh sách phiếu đặt: " + e.getMessage(),
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };
        worker.execute();
    }

    private void loadChiTietFromServer(String maPhieuDat) {
        SwingWorker<List<ChiTietPhieuDatView>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<ChiTietPhieuDatView> doInBackground() {
                return phieuDatClientController.getChiTietPhieuDatItems(maPhieuDat);
            }

            @Override
            protected void done() {
                try {
                    loadChiTietTable(get());
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(
                            TimKiemPhieuDatPanel.this,
                            "Không tải được chi tiết phiếu đặt: " + e.getMessage(),
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };
        worker.execute();
    }

    private void loadPhieuDatTable(List<PhieuDatView> list) {
        phieuDatTableModel.setRowCount(0);
        for (PhieuDatView item : list) {
            String ngayDat = item.getNgayDat() == null ? "" : item.getNgayDat().format(dateFormatter);
            String daNhan = item.isReceived() ? "Đã nhận" : "Chưa nhận";
            phieuDatTableModel.addRow(new Object[]{
                    item.getMaPhieuDat(),
                    item.getTenNhanVien() == null ? "" : item.getTenNhanVien(),
                    item.getTenKhachHang() == null ? "" : item.getTenKhachHang(),
                    ngayDat,
                    daNhan,
                    item.getGhiChu() == null ? "" : item.getGhiChu()
            });
        }
        lblTongPhieuDat.setText("Tổng phiếu đặt: " + list.size());
    }

    private void loadChiTietTable(List<ChiTietPhieuDatView> list) {
        chiTietTableModel.setRowCount(0);
        for (ChiTietPhieuDatView item : list) {
            chiTietTableModel.addRow(new Object[]{
                    item.getTenThuoc(),
                    item.getMaThuoc(),
                    item.getMaLo(),
                    item.getSoLuong()
            });
        }
        lblTongChiTiet.setText("Tổng dòng chi tiết: " + list.size());
    }

    private void clearChiTietTable() {
        chiTietTableModel.setRowCount(0);
        lblTongChiTiet.setText("Tổng dòng chi tiết: 0");
    }

    private void applyKeywordSearch() {
        String keyword = txtTuKhoa.getText().trim().toLowerCase();
        if (keyword.isEmpty()) {
            loadPhieuDatTable(dsPhieuDat);
            clearChiTietTable();
            return;
        }

        int mode = cboTimKiem.getSelectedIndex();
        List<PhieuDatView> ketQua = new ArrayList<>();
        for (PhieuDatView pd : dsPhieuDat) {
            boolean match = switch (mode) {
                case 0 -> pd.getMaPhieuDat() != null && pd.getMaPhieuDat().toLowerCase().contains(keyword);
                case 1 -> pd.getTenNhanVien() != null && pd.getTenNhanVien().toLowerCase().contains(keyword);
                case 2 -> pd.getTenKhachHang() != null && pd.getTenKhachHang().toLowerCase().contains(keyword);
                default -> false;
            };
            if (match) {
                ketQua.add(pd);
            }
        }

        if (ketQua.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy phiếu đặt phù hợp!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            loadPhieuDatTable(dsPhieuDat);
        } else {
            loadPhieuDatTable(ketQua);
        }
        clearChiTietTable();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == btnTimKiem) {
            applyKeywordSearch();
            return;
        }

        if (source == btnXoaTrang) {
            txtTuKhoa.setText("");
            chkLocThangNam.setSelected(false);
            loadPhieuDatFromServer();
            return;
        }

        if (source == chkLocThangNam || source == cboThang || source == cboNam) {
            if (source == cboThang || source == cboNam) {
                if (!chkLocThangNam.isSelected()) {
                    return;
                }
            }
            loadPhieuDatFromServer();
        }
    }
}
