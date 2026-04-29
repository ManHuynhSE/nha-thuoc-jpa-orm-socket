package com.pillchill.migration.gui.timkiem;

import com.pillchill.migration.dto.ChiTietPhieuDoiTraView;
import com.pillchill.migration.dto.PhieuDoiTraView;
import com.pillchill.migration.network.client.PhieuDoiTraClientController;

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

public class TimKiemPhieuDoiTraPanel extends JPanel implements ActionListener {
    private final Color PRIMARY_COLOR = new Color(0, 150, 136);
    private final PhieuDoiTraClientController phieuDoiTraClientController;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private JTextField txtTuKhoa;
    private JComboBox<String> cboTimKiem;
    private JCheckBox chkLocThangNam;
    private JComboBox<String> cboThang;
    private JComboBox<String> cboNam;
    private JButton btnTimKiem;
    private JButton btnXoaTrang;

    private DefaultTableModel phieuDoiTraTableModel;
    private DefaultTableModel chiTietTableModel;
    private JTable tblPhieuDoiTra;
    private JTable tblChiTietPhieuDoiTra;
    private JLabel lblTongPhieuDoiTra;
    private JLabel lblTongChiTiet;

    private List<PhieuDoiTraView> dsPhieuDoiTra;

    public TimKiemPhieuDoiTraPanel(PhieuDoiTraClientController phieuDoiTraClientController) {
        this.phieuDoiTraClientController = phieuDoiTraClientController;
        this.dsPhieuDoiTra = new ArrayList<>();
        initUI();
        loadPhieuDoiTraFromServer();
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

        JLabel lblTieuDe = new JLabel("DANH MỤC PHIẾU ĐỔI TRẢ", SwingConstants.CENTER);
        lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTieuDe.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));

        txtTuKhoa = new JTextField(24);
        cboTimKiem = new JComboBox<>(new String[]{"Theo mã phiếu đổi trả", "Theo tên nhân viên", "Theo tên khách hàng"});
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
        panel.add(createPhieuDoiTraSection());
        panel.add(createChiTietSection());
        return panel;
    }

    private JPanel createPhieuDoiTraSection() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBorder(BorderFactory.createTitledBorder("Danh sách phiếu đổi trả"));

        String[] cols = {"Mã phiếu đổi trả", "Tên NV", "Tên KH", "Ngày đổi trả"};
        phieuDoiTraTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblPhieuDoiTra = createStyledTable(phieuDoiTraTableModel);
        tblPhieuDoiTra.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = tblPhieuDoiTra.getSelectedRow();
                if (row >= 0) {
                    String maPhieuDoiTra = String.valueOf(tblPhieuDoiTra.getValueAt(row, 0));
                    loadChiTietFromServer(maPhieuDoiTra);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(tblPhieuDoiTra);
        lblTongPhieuDoiTra = new JLabel("Tổng phiếu đổi trả: 0");
        lblTongPhieuDoiTra.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(lblTongPhieuDoiTra, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createChiTietSection() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBorder(BorderFactory.createTitledBorder("Chi tiết phiếu đổi trả"));

        String[] cols = {"Tên thuốc", "Mã thuốc", "Mã lô", "Số lượng", "Đơn giá", "Lý do"};
        chiTietTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblChiTietPhieuDoiTra = createStyledTable(chiTietTableModel);
        JScrollPane scrollPane = new JScrollPane(tblChiTietPhieuDoiTra);
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

    private void loadPhieuDoiTraFromServer() {
        SwingWorker<List<PhieuDoiTraView>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<PhieuDoiTraView> doInBackground() {
                if (chkLocThangNam != null && chkLocThangNam.isSelected()) {
                    int month = Integer.parseInt((String) cboThang.getSelectedItem());
                    int year = Integer.parseInt((String) cboNam.getSelectedItem());
                    return phieuDoiTraClientController.getPhieuDoiTraByMonthYearItems(month, year);
                }
                return phieuDoiTraClientController.getAllPhieuDoiTraItems();
            }

            @Override
            protected void done() {
                try {
                    dsPhieuDoiTra = new ArrayList<>(get());
                    loadPhieuDoiTraTable(dsPhieuDoiTra);
                    clearChiTietTable();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(
                            TimKiemPhieuDoiTraPanel.this,
                            "Không tải được danh sách phiếu đổi trả: " + e.getMessage(),
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };
        worker.execute();
    }

    private void loadChiTietFromServer(String maPhieuDoiTra) {
        SwingWorker<List<ChiTietPhieuDoiTraView>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<ChiTietPhieuDoiTraView> doInBackground() {
                return phieuDoiTraClientController.getChiTietPhieuDoiTraItems(maPhieuDoiTra);
            }

            @Override
            protected void done() {
                try {
                    loadChiTietTable(get());
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(
                            TimKiemPhieuDoiTraPanel.this,
                            "Không tải được chi tiết phiếu đổi trả: " + e.getMessage(),
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };
        worker.execute();
    }

    private void loadPhieuDoiTraTable(List<PhieuDoiTraView> list) {
        phieuDoiTraTableModel.setRowCount(0);
        for (PhieuDoiTraView item : list) {
            String ngayDoiTra = item.getNgayDoiTra() == null ? "" : item.getNgayDoiTra().format(dateFormatter);
            phieuDoiTraTableModel.addRow(new Object[]{
                    item.getMaPhieuDoiTra(),
                    item.getTenNhanVien() == null ? "" : item.getTenNhanVien(),
                    item.getTenKhachHang() == null ? "" : item.getTenKhachHang(),
                    ngayDoiTra
            });
        }
        lblTongPhieuDoiTra.setText("Tổng phiếu đổi trả: " + list.size());
    }

    private void loadChiTietTable(List<ChiTietPhieuDoiTraView> list) {
        chiTietTableModel.setRowCount(0);
        for (ChiTietPhieuDoiTraView item : list) {
            chiTietTableModel.addRow(new Object[]{
                    item.getTenThuoc(),
                    item.getMaThuoc(),
                    item.getMaLo(),
                    item.getSoLuong(),
                    String.format("%,.0f", item.getDonGia()),
                    item.getLyDo() == null ? "" : item.getLyDo()
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
            loadPhieuDoiTraTable(dsPhieuDoiTra);
            clearChiTietTable();
            return;
        }

        int mode = cboTimKiem.getSelectedIndex();
        List<PhieuDoiTraView> ketQua = new ArrayList<>();
        for (PhieuDoiTraView pdt : dsPhieuDoiTra) {
            boolean match = switch (mode) {
                case 0 -> pdt.getMaPhieuDoiTra() != null && pdt.getMaPhieuDoiTra().toLowerCase().contains(keyword);
                case 1 -> pdt.getTenNhanVien() != null && pdt.getTenNhanVien().toLowerCase().contains(keyword);
                case 2 -> pdt.getTenKhachHang() != null && pdt.getTenKhachHang().toLowerCase().contains(keyword);
                default -> false;
            };
            if (match) {
                ketQua.add(pdt);
            }
        }

        if (ketQua.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy phiếu đổi trả phù hợp!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            loadPhieuDoiTraTable(dsPhieuDoiTra);
        } else {
            loadPhieuDoiTraTable(ketQua);
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
            loadPhieuDoiTraFromServer();
            return;
        }

        if (source == chkLocThangNam || source == cboThang || source == cboNam) {
            if (source == cboThang || source == cboNam) {
                if (!chkLocThangNam.isSelected()) {
                    return;
                }
            }
            loadPhieuDoiTraFromServer();
        }
    }
}
