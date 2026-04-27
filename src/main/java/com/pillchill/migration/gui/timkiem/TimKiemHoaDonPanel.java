package com.pillchill.migration.gui.timkiem;

import com.pillchill.migration.dto.ChiTietHoaDonView;
import com.pillchill.migration.dto.HoaDonView;
import com.pillchill.migration.network.client.HoaDonClientController;

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

public class TimKiemHoaDonPanel extends JPanel implements ActionListener {
    private final Color PRIMARY_COLOR = new Color(0, 150, 136);
    private final HoaDonClientController hoaDonClientController;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private JTextField txtTuKhoa;
    private JComboBox<String> cboTimKiem;
    private JCheckBox chkLocThangNam;
    private JComboBox<String> cboThang;
    private JComboBox<String> cboNam;
    private JButton btnTimKiem;
    private JButton btnXoaTrang;

    private DefaultTableModel hoaDonTableModel;
    private DefaultTableModel chiTietTableModel;
    private JTable tblHoaDon;
    private JTable tblChiTietHoaDon;
    private JLabel lblTongHoaDon;
    private JLabel lblTongChiTiet;

    private List<HoaDonView> dsHoaDon;

    public TimKiemHoaDonPanel(HoaDonClientController hoaDonClientController) {
        this.hoaDonClientController = hoaDonClientController;
        this.dsHoaDon = new ArrayList<>();
        initUI();
        loadHoaDonFromServer();
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

        JLabel lblTieuDe = new JLabel("DANH MỤC HÓA ĐƠN", SwingConstants.CENTER);
        lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTieuDe.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));

        txtTuKhoa = new JTextField(24);
        cboTimKiem = new JComboBox<>(new String[]{"Theo mã hóa đơn", "Theo tên nhân viên", "Theo tên khách hàng"});
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

        panel.add(createHoaDonSection());
        panel.add(createChiTietSection());
        return panel;
    }

    private JPanel createHoaDonSection() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBorder(BorderFactory.createTitledBorder("Danh sách hóa đơn"));

        String[] cols = {"Mã hóa đơn", "Tên NV", "Tên KH", "Ngày lập", "Ghi chú"};
        hoaDonTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblHoaDon = createStyledTable(hoaDonTableModel);
        tblHoaDon.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = tblHoaDon.getSelectedRow();
                if (row >= 0) {
                    String maHoaDon = String.valueOf(tblHoaDon.getValueAt(row, 0));
                    loadChiTietFromServer(maHoaDon);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(tblHoaDon);
        lblTongHoaDon = new JLabel("Tổng hóa đơn: 0");
        lblTongHoaDon.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(lblTongHoaDon, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createChiTietSection() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBorder(BorderFactory.createTitledBorder("Chi tiết hóa đơn"));

        String[] cols = {"Tên thuốc", "Mã thuốc", "Mã lô", "Số lượng", "Đơn giá"};
        chiTietTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblChiTietHoaDon = createStyledTable(chiTietTableModel);
        JScrollPane scrollPane = new JScrollPane(tblChiTietHoaDon);
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

    private void loadHoaDonFromServer() {
        SwingWorker<List<HoaDonView>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<HoaDonView> doInBackground() {
                if (chkLocThangNam != null && chkLocThangNam.isSelected()) {
                    int month = Integer.parseInt((String) cboThang.getSelectedItem());
                    int year = Integer.parseInt((String) cboNam.getSelectedItem());
                    return hoaDonClientController.getHoaDonByMonthYearItems(month, year);
                }
                return hoaDonClientController.getAllHoaDonItems();
            }

            @Override
            protected void done() {
                try {
                    dsHoaDon = new ArrayList<>(get());
                    loadHoaDonTable(dsHoaDon);
                    clearChiTietTable();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(
                            TimKiemHoaDonPanel.this,
                            "Không tải được danh sách hóa đơn: " + e.getMessage(),
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };
        worker.execute();
    }

    private void loadChiTietFromServer(String maHoaDon) {
        SwingWorker<List<ChiTietHoaDonView>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<ChiTietHoaDonView> doInBackground() {
                return hoaDonClientController.getChiTietHoaDonItems(maHoaDon);
            }

            @Override
            protected void done() {
                try {
                    loadChiTietTable(get());
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(
                            TimKiemHoaDonPanel.this,
                            "Không tải được chi tiết hóa đơn: " + e.getMessage(),
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };
        worker.execute();
    }

    private void loadHoaDonTable(List<HoaDonView> list) {
        hoaDonTableModel.setRowCount(0);
        for (HoaDonView item : list) {
            String ngayLap = item.getNgayBan() == null ? "" : item.getNgayBan().format(dateFormatter);
            hoaDonTableModel.addRow(new Object[]{
                    item.getMaHoaDon(),
                    item.getTenNhanVien() == null ? "" : item.getTenNhanVien(),
                    item.getTenKhachHang() == null ? "" : item.getTenKhachHang(),
                    ngayLap,
                    item.getGhiChu() == null ? "" : item.getGhiChu()
            });
        }
        lblTongHoaDon.setText("Tổng hóa đơn: " + list.size());
    }

    private void loadChiTietTable(List<ChiTietHoaDonView> list) {
        chiTietTableModel.setRowCount(0);
        for (ChiTietHoaDonView item : list) {
            chiTietTableModel.addRow(new Object[]{
                    item.getTenThuoc(),
                    item.getMaThuoc(),
                    item.getMaLo(),
                    item.getSoLuong(),
                    String.format("%,.0f", item.getDonGia())
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
            loadHoaDonTable(dsHoaDon);
            clearChiTietTable();
            return;
        }

        int mode = cboTimKiem.getSelectedIndex();
        List<HoaDonView> ketQua = new ArrayList<>();
        for (HoaDonView hd : dsHoaDon) {
            boolean match = switch (mode) {
                case 0 -> hd.getMaHoaDon() != null && hd.getMaHoaDon().toLowerCase().contains(keyword);
                case 1 -> hd.getTenNhanVien() != null && hd.getTenNhanVien().toLowerCase().contains(keyword);
                case 2 -> hd.getTenKhachHang() != null && hd.getTenKhachHang().toLowerCase().contains(keyword);
                default -> false;
            };
            if (match) {
                ketQua.add(hd);
            }
        }

        if (ketQua.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy hóa đơn phù hợp!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            loadHoaDonTable(dsHoaDon);
        } else {
            loadHoaDonTable(ketQua);
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
            loadHoaDonFromServer();
            return;
        }

        if (source == chkLocThangNam || source == cboThang || source == cboNam) {
            if (source == cboThang || source == cboNam) {
                if (!chkLocThangNam.isSelected()) {
                    return;
                }
            }
            loadHoaDonFromServer();
        }
    }
}
