package com.pillchill.migration.gui.timkiem;

import com.pillchill.migration.entity.KhachHang;
import com.pillchill.migration.network.client.KhachHangClientController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class TimKiemKhachHangPanel extends JPanel implements ActionListener {
    private final Color PRIMARY_COLOR = new Color(0, 150, 136);
    private final KhachHangClientController khachHangClientController;

    private JTextField txtMaKH;
    private JTextField txtTenKH;
    private JTextField txtSoDienThoai;
    private JTextField txtDiemTichLuy;
    private JButton btnXoaTrang;
    private JButton btnTimKiem;
    private JLabel lblTongSoBanGhi;

    private DefaultTableModel tableModel;
    private JTable tblKhachHang;
    private List<KhachHang> dsKhachHang;

    public TimKiemKhachHangPanel(KhachHangClientController khachHangClientController) {
        this.khachHangClientController = khachHangClientController;
        this.dsKhachHang = new ArrayList<>();
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

        JLabel lblTieuDe = new JLabel("TÌM KIẾM KHÁCH HÀNG", SwingConstants.CENTER);
        lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTieuDe.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JPanel pnlForm = new JPanel(new GridLayout(2, 2, 10, 5));

        txtMaKH = new JTextField(15);
        txtTenKH = new JTextField(15);
        txtSoDienThoai = new JTextField(15);
        txtDiemTichLuy = new JTextField(15);

        pnlForm.add(createFieldPanel("Mã khách hàng:", txtMaKH));
        pnlForm.add(createFieldPanel("Tên khách hàng:", txtTenKH));
        pnlForm.add(createFieldPanel("Số điện thoại:", txtSoDienThoai));
        pnlForm.add(createFieldPanel("Điểm tích lũy tối thiểu:", txtDiemTichLuy));

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

        String[] cols = {"Mã khách hàng", "Tên khách hàng", "Số điện thoại", "Điểm tích lũy"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblKhachHang = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(242, 242, 242));
                }
                return c;
            }
        };
        tblKhachHang.setRowHeight(35);
        tblKhachHang.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tblKhachHang.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JTableHeader header = tblKhachHang.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 40));
        DefaultTableCellRenderer centerRenderer = (DefaultTableCellRenderer) header.getDefaultRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        JScrollPane scrollPane = new JScrollPane(tblKhachHang);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        lblTongSoBanGhi = new JLabel("Tổng số bản ghi: 0");
        lblTongSoBanGhi.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        centerPanel.add(lblTongSoBanGhi, BorderLayout.SOUTH);
        return centerPanel;
    }

    private void loadDataFromServer() {
        SwingWorker<List<KhachHang>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<KhachHang> doInBackground() {
                return khachHangClientController.getAllKhachHangItems();
            }

            @Override
            protected void done() {
                try {
                    dsKhachHang = new ArrayList<>(get());
                    loadTableData(dsKhachHang);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(
                            TimKiemKhachHangPanel.this,
                            "Không tải được danh sách khách hàng: " + e.getMessage(),
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };
        worker.execute();
    }

    private void loadTableData(List<KhachHang> list) {
        tableModel.setRowCount(0);
        for (KhachHang item : list) {
            tableModel.addRow(new Object[]{
                    item.getMaKH(),
                    item.getTenKH(),
                    item.getSoDienThoai(),
                    item.getDiemTichLuy()
            });
        }
        lblTongSoBanGhi.setText("Tổng số bản ghi: " + list.size());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        if (o == btnXoaTrang) {
            txtMaKH.setText("");
            txtTenKH.setText("");
            txtSoDienThoai.setText("");
            txtDiemTichLuy.setText("");
            loadTableData(dsKhachHang);
            return;
        }

        if (o == btnTimKiem) {
            String maKHTim = txtMaKH.getText().trim().toLowerCase();
            String tenKHTim = txtTenKH.getText().trim().toLowerCase();
            String sdtTim = txtSoDienThoai.getText().trim().toLowerCase();
            String diemStr = txtDiemTichLuy.getText().trim();

            Integer diemTim = null;
            if (!diemStr.isEmpty()) {
                try {
                    diemTim = Integer.parseInt(diemStr);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Điểm tích lũy phải là số nguyên!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            List<KhachHang> ketQua = new ArrayList<>();
            for (KhachHang kh : dsKhachHang) {
                boolean match = true;

                if (!maKHTim.isEmpty() && (kh.getMaKH() == null || !kh.getMaKH().toLowerCase().contains(maKHTim))) {
                    match = false;
                }

                if (match && !tenKHTim.isEmpty() && (kh.getTenKH() == null || !kh.getTenKH().toLowerCase().contains(tenKHTim))) {
                    match = false;
                }

                if (match && !sdtTim.isEmpty() && (kh.getSoDienThoai() == null || !kh.getSoDienThoai().toLowerCase().contains(sdtTim))) {
                    match = false;
                }

                if (match && diemTim != null && (kh.getDiemTichLuy() == null || kh.getDiemTichLuy() < diemTim)) {
                    match = false;
                }

                if (match) {
                    ketQua.add(kh);
                }
            }

            if (ketQua.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy kết quả phù hợp!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                loadTableData(dsKhachHang);
            } else {
                loadTableData(ketQua);
            }
        }
    }
}
