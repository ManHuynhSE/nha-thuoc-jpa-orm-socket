package com.pillchill.migration.gui.timkiem;

import com.pillchill.migration.dto.ChiTietLoThuocView;
import com.pillchill.migration.network.client.ThuocClientController;

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

public class TimKiemChiTietLoThuocPanel extends JPanel implements ActionListener {
    private final Color PRIMARY_COLOR = new Color(0, 150, 136);
    private final ThuocClientController thuocClientController;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private JTextField txtMaThuoc;
    private JTextField txtMaLo;
    private JTextField txtSoLuong;
    private JTextField txtGiaNhap;
    private JButton btnXoaTrang;
    private JButton btnTimKiem;
    private JLabel lblTongSoBanGhi;

    private DefaultTableModel tableModel;
    private JTable table;
    private List<ChiTietLoThuocView> dsChiTietLoThuoc;

    public TimKiemChiTietLoThuocPanel(ThuocClientController thuocClientController) {
        this.thuocClientController = thuocClientController;
        this.dsChiTietLoThuoc = new ArrayList<>();
        initUI();
        loadDataFromServer();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        add(createTopPanel(), BorderLayout.NORTH);
        add(createCenterPanel(), BorderLayout.CENTER);

        getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke("ENTER"), "clickSearch");
        getActionMap().put("clickSearch", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnTimKiem.doClick();
            }
        });
    }

    private JPanel createTopPanel() {
        JPanel pnlMain = new JPanel(new BorderLayout());
        pnlMain.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 30));

        JLabel lblTieuDe = new JLabel("TÌM KIẾM CHI TIẾT LÔ THUỐC", SwingConstants.CENTER);
        lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTieuDe.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JPanel pnlForm = new JPanel(new GridLayout(2, 2, 10, 5));
        txtMaThuoc = new JTextField(15);
        txtMaLo = new JTextField(15);
        txtSoLuong = new JTextField(15);
        txtGiaNhap = new JTextField(15);

        pnlForm.add(createFieldPanel("Mã thuốc:", txtMaThuoc));
        pnlForm.add(createFieldPanel("Mã lô:", txtMaLo));
        pnlForm.add(createFieldPanel("Số lượng tối thiểu:", txtSoLuong));
        pnlForm.add(createFieldPanel("Giá nhập tối thiểu:", txtGiaNhap));

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

        String[] cols = {"Mã lô", "Mã thuốc", "Tên thuốc", "Số lượng", "Giá nhập", "Ngày SX", "Hạn SD"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel) {
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
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 40));
        DefaultTableCellRenderer centerRenderer = (DefaultTableCellRenderer) header.getDefaultRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        DefaultTableCellRenderer cellCenter = new DefaultTableCellRenderer();
        cellCenter.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(cellCenter);
        }

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        lblTongSoBanGhi = new JLabel("Tổng số bản ghi: 0");
        lblTongSoBanGhi.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        centerPanel.add(lblTongSoBanGhi, BorderLayout.SOUTH);
        return centerPanel;
    }

    private void loadDataFromServer() {
        SwingWorker<List<ChiTietLoThuocView>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<ChiTietLoThuocView> doInBackground() {
                return thuocClientController.getAllChiTietLoThuocItems();
            }

            @Override
            protected void done() {
                try {
                    dsChiTietLoThuoc = new ArrayList<>(get());
                    loadTableData(dsChiTietLoThuoc);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(
                            TimKiemChiTietLoThuocPanel.this,
                            "Không tải được danh sách chi tiết lô thuốc: " + e.getMessage(),
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };
        worker.execute();
    }

    private void loadTableData(List<ChiTietLoThuocView> list) {
        tableModel.setRowCount(0);
        for (ChiTietLoThuocView item : list) {
            String ngaySx = item.getNgaySanXuat() == null ? "" : item.getNgaySanXuat().format(dateFormatter);
            String hanSd = item.getHanSuDung() == null ? "" : item.getHanSuDung().format(dateFormatter);
            tableModel.addRow(new Object[]{
                    item.getMaLo(),
                    item.getMaThuoc(),
                    item.getTenThuoc(),
                    item.getSoLuong(),
                    String.format("%,.0f", item.getGiaNhap()),
                    ngaySx,
                    hanSd
            });
        }
        lblTongSoBanGhi.setText("Tổng số bản ghi: " + list.size());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        if (o == btnXoaTrang) {
            txtMaThuoc.setText("");
            txtMaLo.setText("");
            txtSoLuong.setText("");
            txtGiaNhap.setText("");
            loadTableData(dsChiTietLoThuoc);
            return;
        }

        if (o == btnTimKiem) {
            String maThuocTim = txtMaThuoc.getText().trim().toLowerCase();
            String maLoTim = txtMaLo.getText().trim().toLowerCase();
            String soLuongStr = txtSoLuong.getText().trim();
            String giaNhapStr = txtGiaNhap.getText().trim();

            Integer soLuongTim = null;
            Double giaNhapTim = null;

            if (!soLuongStr.isEmpty()) {
                try {
                    soLuongTim = Integer.parseInt(soLuongStr);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Số lượng phải là số nguyên!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            if (!giaNhapStr.isEmpty()) {
                try {
                    giaNhapTim = Double.parseDouble(giaNhapStr);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Giá nhập phải là số!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            List<ChiTietLoThuocView> ketQua = new ArrayList<>();
            for (ChiTietLoThuocView item : dsChiTietLoThuoc) {
                boolean match = true;
                if (!maThuocTim.isEmpty() && (item.getMaThuoc() == null || !item.getMaThuoc().toLowerCase().contains(maThuocTim))) {
                    match = false;
                }
                if (match && !maLoTim.isEmpty() && (item.getMaLo() == null || !item.getMaLo().toLowerCase().contains(maLoTim))) {
                    match = false;
                }
                if (match && soLuongTim != null && item.getSoLuong() < soLuongTim) {
                    match = false;
                }
                if (match && giaNhapTim != null && item.getGiaNhap() < giaNhapTim) {
                    match = false;
                }
                if (match) {
                    ketQua.add(item);
                }
            }

            if (ketQua.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy kết quả phù hợp!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                loadTableData(dsChiTietLoThuoc);
            } else {
                loadTableData(ketQua);
            }
        }
    }
}
