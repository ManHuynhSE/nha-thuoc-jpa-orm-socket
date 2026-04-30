package com.pillchill.migration.gui.thongke;

import com.pillchill.migration.dto.HoaDonKemGiaDTO;
import com.pillchill.migration.dto.ThongKeKhachHangDTO;
import com.pillchill.migration.network.client.ThongKeKhachHangClientController;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.List;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;


public class ThongKeTheoKhachHangPanel extends JPanel implements ActionListener {

    private final Color PRIMARY_COLOR = new Color(0, 150, 136);
    private final Color BG_COLOR = new Color(245, 245, 245);
    private final Color ACCENT_COLOR = new Color(255, 255, 255);
    private final Color TEXT_COLOR = new Color(51, 51, 51);

    private final ThongKeKhachHangClientController thongKeKhachHangClientController;

    private DefaultTableModel dtmThongKe;
    private JTextField searchField;
    private JButton searchButton;
    private JComboBox<String> cboTimKiem;
    private JComboBox<String> cboThang;
    private JComboBox<String> cboNam;
    private JRadioButton radTimKiemThangNam;
    private JTable tblThongKe;
    private final DecimalFormat currencyFormat = new DecimalFormat("#,### 'VNĐ'");

    private boolean isInitializing = false;

    public ThongKeTheoKhachHangPanel() {
        this(null);
    }

    public ThongKeTheoKhachHangPanel(ThongKeKhachHangClientController thongKeKhachHangClientController) {
        this.thongKeKhachHangClientController = thongKeKhachHangClientController;

        // Table init
        String[] colsThongKe = {"Mã khách hàng", "Tên khách hàng", "SĐT khách hàng", "Tổng tiền"};
        dtmThongKe = new DefaultTableModel(colsThongKe, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Frame
        setLayout(new GridLayout(1, 1, 10, 10));
        add(createTopPanel());

        loadKhachHangDataAsync();

        setBackground(BG_COLOR);
        setVisible(true);
    }


    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        searchPanel.setBackground(BG_COLOR);

        searchField = new JTextField(25);
        searchField.setText("Nhập từ khóa...");
        searchField.setForeground(Color.GRAY);
        searchField.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        searchField.setBackground(Color.WHITE);
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        // PlaceHolder
        searchField.addFocusListener(new FocusListener() {

            @Override
            public void focusGained(FocusEvent e) {
                if (searchField.getText().equals("Nhập từ khóa...")) {
                    searchField.setText("");
                    searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                    searchField.setForeground(TEXT_COLOR);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (searchField.getText().equals("")) {
                    searchField.setText("Nhập từ khóa...");
                    searchField.setFont(new Font("Segoe UI", Font.ITALIC, 14));
                    searchField.setForeground(Color.GRAY);
                }
            }

        });

        searchButton = new JButton("Tìm");
        searchButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        searchButton.setPreferredSize(new Dimension(100, 40));
        searchButton.setBackground(PRIMARY_COLOR);
        searchButton.setForeground(Color.WHITE);
        searchButton.setBorderPainted(false);
        searchButton.setFocusPainted(false);
        searchButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // ComboBox
        String[] kieuTimKiem = {"Theo mã khách hàng", "Theo tên khách hàng", "Theo SĐT khách hàng"};
        cboTimKiem = new JComboBox<>(kieuTimKiem);
        cboTimKiem.setSelectedIndex(0);
        cboTimKiem.setPreferredSize(new Dimension(180, 40));
        cboTimKiem.setBackground(Color.WHITE);
        cboTimKiem.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cboTimKiem.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));

        // ComboBox Date
        JLabel lblThang = new JLabel("Tháng: ");
        lblThang.setFont(new Font("Segoe UI", Font.BOLD, 14));
        String[] thang = "1 2 3 4 5 6 7 8 9 10 11 12".split(" ");
        cboThang = new JComboBox<>(thang);
        LocalDate today = LocalDate.now();
        cboThang.setSelectedIndex(today.getMonthValue() - 1);
        cboThang.setPreferredSize(new Dimension(70, 40));
        cboThang.setBackground(Color.WHITE);
        cboThang.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cboThang.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));

        JLabel lblNam = new JLabel("Năm: ");
        lblNam.setFont(new Font("Segoe UI", Font.BOLD, 14));
        String[] nam = "2024 2025".split(" ");
        cboNam = new JComboBox<>(nam);
        cboNam.setSelectedIndex(1);
        cboNam.setPreferredSize(new Dimension(80, 40));
        cboNam.setBackground(Color.WHITE);
        cboNam.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cboNam.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));

        radTimKiemThangNam = new JRadioButton("Lọc theo Tháng/Năm");
        radTimKiemThangNam.setFont(new Font("Segoe UI", Font.BOLD, 14));
        radTimKiemThangNam.setBackground(BG_COLOR);
        radTimKiemThangNam.setFocusPainted(false);

        searchPanel.add(Box.createHorizontalStrut(15));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(cboTimKiem);
        searchPanel.add(radTimKiemThangNam);
        searchPanel.add(lblThang);
        searchPanel.add(cboThang);
        searchPanel.add(lblNam);
        searchPanel.add(cboNam);

        searchButton.addActionListener(this);
        cboThang.addActionListener(this);
        cboNam.addActionListener(this);
        radTimKiemThangNam.addActionListener(this);

        return searchPanel;
    }

    public JPanel createTopPanel() {

        JPanel pnlTop = new JPanel();
        pnlTop.setLayout(new BorderLayout());
        pnlTop.setBackground(BG_COLOR);

        tblThongKe = new JTable(dtmThongKe) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(242, 242, 242));
                }
                return c;
            }
        };
        tblThongKe.setBackground(Color.WHITE);
        tblThongKe.setGridColor(new Color(224, 224, 224));
        tblThongKe.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tblThongKe.setRowHeight(35);
        tblThongKe.setSelectionBackground(new Color(178, 223, 219));
        tblThongKe.setSelectionForeground(Color.BLACK);
        tblThongKe.setShowGrid(true);
        tblThongKe.setIntercellSpacing(new Dimension(1, 1));

        tblThongKe.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
                    int row = tblThongKe.rowAtPoint(e.getPoint());
                    if (row >= 0) {
                        String maKH = (String) dtmThongKe.getValueAt(row, 0);
                        String tenKH = (String) dtmThongKe.getValueAt(row, 1);
                        showHoaDonCuaKhachHangAsync(maKH, tenKH);
                    }
                }
            }
        });

        JTableHeader header = tblThongKe.getTableHeader();
        header.setPreferredSize(new Dimension(header.getWidth(), 40));
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(tblThongKe);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(0, 50, 5, 50),
                BorderFactory.createLineBorder(new Color(220, 220, 220))
        ));
        scrollPane.getViewport().setBackground(Color.WHITE);

        JLabel lblThongKe = new JLabel("THỐNG KÊ DOANH THU THEO KHÁCH HÀNG", SwingConstants.CENTER);
        lblThongKe.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblThongKe.setForeground(PRIMARY_COLOR);
        lblThongKe.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JPanel pnlTieuDe = new JPanel(new GridLayout(2, 1, 0, 0));
        pnlTieuDe.setBackground(BG_COLOR);
        pnlTieuDe.add(lblThongKe);
        pnlTieuDe.add(createSearchPanel());

        pnlTop.add(pnlTieuDe, BorderLayout.NORTH);
        pnlTop.add(scrollPane, BorderLayout.CENTER);

        return pnlTop;
    }

    // ===== ASYNC DATA LOADING METHODS =====

    public void loadKhachHangDataAsync() {
        if (thongKeKhachHangClientController == null) return;

        isInitializing = true;
        SwingWorker<List<ThongKeKhachHangDTO>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<ThongKeKhachHangDTO> doInBackground() {
                return thongKeKhachHangClientController.getDoanhThuTatCaKhachHang();
            }

            @Override
            protected void done() {
                try {
                    List<ThongKeKhachHangDTO> dsKhachHang = get();
                    populateTable(dsKhachHang);
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    isInitializing = false;
                }
            }
        };
        worker.execute();
    }

    public void loadKhachHangDataTheoThangNamAsync() {
        if (thongKeKhachHangClientController == null) return;

        int thang = cboThang.getSelectedIndex() + 1;
        String txtNam = (String) cboNam.getSelectedItem();
        int nam = Integer.parseInt(txtNam);

        isInitializing = true;
        SwingWorker<List<ThongKeKhachHangDTO>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<ThongKeKhachHangDTO> doInBackground() {
                return thongKeKhachHangClientController.getDoanhThuKhachHangTheoThangNam(thang, nam);
            }

            @Override
            protected void done() {
                try {
                    List<ThongKeKhachHangDTO> dsKhachHang = get();
                    populateTable(dsKhachHang);
                    if (dsKhachHang.isEmpty()) {
                        JOptionPane.showMessageDialog(ThongKeTheoKhachHangPanel.this,
                                "Không có khách hàng nào có hóa đơn trong tháng " + thang + "/" + nam);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    isInitializing = false;
                }
            }
        };
        worker.execute();
    }

    public void loadKhachHangDataTheoMaKHAsync() {
        if (thongKeKhachHangClientController == null) return;

        String maKH = searchField.getText().trim();
        if (maKH.isEmpty() || maKH.equals("Nhập từ khóa...")) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập mã khách hàng");
            return;
        }

        isInitializing = true;
        SwingWorker<List<ThongKeKhachHangDTO>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<ThongKeKhachHangDTO> doInBackground() {
                return thongKeKhachHangClientController.timKiemTheoMaKH(maKH);
            }

            @Override
            protected void done() {
                try {
                    List<ThongKeKhachHangDTO> dsKhachHang = get();
                    populateTable(dsKhachHang);
                    if (dsKhachHang.isEmpty()) {
                        JOptionPane.showMessageDialog(ThongKeTheoKhachHangPanel.this,
                                "Không tìm thấy khách hàng có mã: " + maKH);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    isInitializing = false;
                }
            }
        };
        worker.execute();
    }

    public void loadKhachHangDataTheoTenAsync() {
        if (thongKeKhachHangClientController == null) return;

        String tenKH = searchField.getText().trim();
        if (tenKH.isEmpty() || tenKH.equals("Nhập từ khóa...")) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên khách hàng");
            return;
        }

        isInitializing = true;
        SwingWorker<List<ThongKeKhachHangDTO>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<ThongKeKhachHangDTO> doInBackground() {
                return thongKeKhachHangClientController.timKiemTheoTenKH(tenKH);
            }

            @Override
            protected void done() {
                try {
                    List<ThongKeKhachHangDTO> dsKhachHang = get();
                    populateTable(dsKhachHang);
                    if (dsKhachHang.isEmpty()) {
                        JOptionPane.showMessageDialog(ThongKeTheoKhachHangPanel.this,
                                "Không tìm thấy khách hàng có tên: " + tenKH);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    isInitializing = false;
                }
            }
        };
        worker.execute();
    }

    public void loadKhachHangDataTheoSoDienThoaiAsync() {
        if (thongKeKhachHangClientController == null) return;

        String soDienThoai = searchField.getText().trim();
        if (soDienThoai.isEmpty() || soDienThoai.equals("Nhập từ khóa...")) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số điện thoại");
            return;
        }

        isInitializing = true;
        SwingWorker<List<ThongKeKhachHangDTO>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<ThongKeKhachHangDTO> doInBackground() {
                return thongKeKhachHangClientController.timKiemTheoSoDienThoai(soDienThoai);
            }

            @Override
            protected void done() {
                try {
                    List<ThongKeKhachHangDTO> dsKhachHang = get();
                    populateTable(dsKhachHang);
                    if (dsKhachHang.isEmpty()) {
                        JOptionPane.showMessageDialog(ThongKeTheoKhachHangPanel.this,
                                "Không tìm thấy khách hàng có số điện thoại: " + soDienThoai);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    isInitializing = false;
                }
            }
        };
        worker.execute();
    }

    private void showHoaDonCuaKhachHangAsync(String maKH, String tenKH) {
        if (thongKeKhachHangClientController == null) return;

        SwingWorker<List<HoaDonKemGiaDTO>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<HoaDonKemGiaDTO> doInBackground() {
                return thongKeKhachHangClientController.getHoaDonTheoKhachHang(maKH);
            }

            @Override
            protected void done() {
                try {
                    List<HoaDonKemGiaDTO> hoaDons = get();
                    if (hoaDons.isEmpty()) {
                        JOptionPane.showMessageDialog(ThongKeTheoKhachHangPanel.this,
                                "Khách hàng " + tenKH + " chưa có hóa đơn.");
                        return;
                    }
                    showHoaDonDialog(hoaDons, tenKH);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    // ===== HELPER METHODS =====

    private void populateTable(List<ThongKeKhachHangDTO> dsKhachHang) {
        DecimalFormat df = new DecimalFormat("#,###");
        dtmThongKe.setRowCount(0);
        for (ThongKeKhachHangDTO kh : dsKhachHang) {
            String tongTienFormatted = df.format(kh.getTongTien()) + " VNĐ";
            Object[] rowData = {
                    kh.getMaKH(),
                    kh.getTenKH(),
                    kh.getSoDienThoai(),
                    tongTienFormatted
            };
            dtmThongKe.addRow(rowData);
        }
    }

    private void showHoaDonDialog(List<HoaDonKemGiaDTO> hoaDons, String tenKH) {
        Window window = SwingUtilities.getWindowAncestor(this);
        JDialog dialog;
        if (window instanceof Frame) {
            dialog = new JDialog((Frame) window, "Hóa đơn của " + tenKH, true);
        } else if (window instanceof Dialog) {
            dialog = new JDialog((Dialog) window, "Hóa đơn của " + tenKH, true);
        } else {
            dialog = new JDialog((Frame) null, "Hóa đơn của " + tenKH, true);
        }
        dialog.setSize(850, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        String[] cols = {"Mã hóa đơn", "Ngày lập", "Nhân viên lập", "Tổng tiền", "Ghi chú"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable tblHoaDon = new JTable(model) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(242, 242, 242));
                }
                return c;
            }
        };

        tblHoaDon.setRowHeight(32);
        tblHoaDon.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tblHoaDon.setSelectionBackground(new Color(178, 223, 219));
        tblHoaDon.setSelectionForeground(Color.BLACK);
        tblHoaDon.setGridColor(new Color(224, 224, 224));
        tblHoaDon.setShowGrid(true);

        JTableHeader header = tblHoaDon.getTableHeader();
        header.setPreferredSize(new Dimension(header.getWidth(), 40));
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setReorderingAllowed(false);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        for (HoaDonKemGiaDTO hd : hoaDons) {
            Object[] rowData = {
                    hd.getMaHoaDon(),
                    sdf.format(hd.getNgayBan()),
                    hd.getTenNV(),
                    currencyFormat.format(hd.getTongTien()),
                    hd.getGhiChu()
            };
            model.addRow(rowData);
        }

        JScrollPane scrollPane = new JScrollPane(tblHoaDon);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 10),
                BorderFactory.createLineBorder(new Color(220, 220, 220))));
        scrollPane.getViewport().setBackground(Color.WHITE);

        dialog.add(scrollPane, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    public void refresh() {
        if (thongKeKhachHangClientController == null) return;

        if (radTimKiemThangNam != null && radTimKiemThangNam.isSelected()) {
            loadKhachHangDataTheoThangNamAsync();
        } else {
            loadKhachHangDataAsync();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isInitializing) return;

        Object o = e.getSource();
        if (o == searchButton) {
            if (cboTimKiem.getSelectedIndex() == 0) {
                loadKhachHangDataTheoMaKHAsync();
            } else if (cboTimKiem.getSelectedIndex() == 1) {
                loadKhachHangDataTheoTenAsync();
            } else if (cboTimKiem.getSelectedIndex() == 2) {
                loadKhachHangDataTheoSoDienThoaiAsync();
            }

        } else if (o == radTimKiemThangNam) {
            if (radTimKiemThangNam.isSelected()) {
                loadKhachHangDataTheoThangNamAsync();
            } else {
                loadKhachHangDataAsync();
            }
        } else if (o == cboThang || o == cboNam) {
            if (radTimKiemThangNam.isSelected()) {
                loadKhachHangDataTheoThangNamAsync();
            }
        }
    }
}