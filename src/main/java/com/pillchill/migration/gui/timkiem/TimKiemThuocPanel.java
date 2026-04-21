package com.pillchill.migration.gui.timkiem;

import com.pillchill.migration.dto.ThuocTheoLoView;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TimKiemThuocPanel extends JPanel implements ActionListener {
    private final Color PRIMARY_COLOR = new Color(0, 150, 136);
    private final ThuocClientController thuocClientController;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private JTextField txtMaThuoc;
    private JTextField txtTenThuoc;
    private JTextField txtMaLo;
    private JTextField txtSoLuongLo;
    private JComboBox<String> cboDonVi;
    private JComboBox<String> cboNhaSanXuat;
    private JButton btnXoaTrang;
    private JButton btnTimKiem;
    private JLabel lblTongSoBanGhi;

    private DefaultTableModel tableModel;
    private JTable tblThuoc;
    private List<ThuocTheoLoView> dsThuocTheoLo;

    public TimKiemThuocPanel(ThuocClientController thuocClientController) {
        this.thuocClientController = thuocClientController;
        this.dsThuocTheoLo = new ArrayList<>();
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

        JLabel lblTieuDe = new JLabel("TÌM KIẾM THUỐC THEO LÔ", SwingConstants.CENTER);
        lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTieuDe.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JPanel pnlForm = new JPanel(new GridLayout(3, 2, 10, 5));

        txtMaThuoc = new JTextField(15);
        txtTenThuoc = new JTextField(15);
        txtMaLo = new JTextField(15);
        txtSoLuongLo = new JTextField(15);
        cboDonVi = new JComboBox<>();
        cboNhaSanXuat = new JComboBox<>();
        cboDonVi.addItem("");
        cboNhaSanXuat.addItem("");

        pnlForm.add(createFieldPanel("Mã thuốc:", txtMaThuoc));
        pnlForm.add(createFieldPanel("Tên thuốc:", txtTenThuoc));
        pnlForm.add(createFieldPanel("Mã lô:", txtMaLo));
        pnlForm.add(createFieldPanel("Số lượng lô tối thiểu:", txtSoLuongLo));
        pnlForm.add(createFieldPanel("Đơn vị:", cboDonVi));
        pnlForm.add(createFieldPanel("Nhà sản xuất:", cboNhaSanXuat));

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

        String[] cols = {"Mã thuốc", "Tên thuốc", "Mã lô", "Hạn sử dụng", "Số lượng lô", "Đơn vị", "NSX"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblThuoc = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(242, 242, 242));
                }
                return c;
            }
        };
        tblThuoc.setRowHeight(35);
        tblThuoc.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tblThuoc.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JTableHeader header = tblThuoc.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 40));
        DefaultTableCellRenderer centerRenderer = (DefaultTableCellRenderer) header.getDefaultRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        JScrollPane scrollPane = new JScrollPane(tblThuoc);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        lblTongSoBanGhi = new JLabel("Tổng số bản ghi: 0");
        lblTongSoBanGhi.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        centerPanel.add(lblTongSoBanGhi, BorderLayout.SOUTH);
        return centerPanel;
    }

    private void loadDataFromServer() {
        SwingWorker<List<ThuocTheoLoView>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<ThuocTheoLoView> doInBackground() {
                return thuocClientController.getAllThuocTheoLoItems();
            }

            @Override
            protected void done() {
                try {
                    dsThuocTheoLo = new ArrayList<>(get());
                    loadFilterData(dsThuocTheoLo);
                    loadTableData(dsThuocTheoLo);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(
                            TimKiemThuocPanel.this,
                            "Không tải được danh sách thuốc theo lô: " + e.getMessage(),
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };
        worker.execute();
    }

    private void loadFilterData(List<ThuocTheoLoView> list) {
        Set<String> donViSet = new HashSet<>();
        Set<String> nsxSet = new HashSet<>();
        for (ThuocTheoLoView item : list) {
            if (item.getDonVi() != null && !item.getDonVi().isBlank()) {
                donViSet.add(item.getDonVi());
            }
            if (item.getMaNSX() != null && !item.getMaNSX().isBlank()) {
                nsxSet.add(item.getMaNSX());
            }
        }

        cboDonVi.removeAllItems();
        cboNhaSanXuat.removeAllItems();
        cboDonVi.addItem("");
        cboNhaSanXuat.addItem("");
        donViSet.stream().sorted().forEach(cboDonVi::addItem);
        nsxSet.stream().sorted().forEach(cboNhaSanXuat::addItem);
    }

    private void loadTableData(List<ThuocTheoLoView> list) {
        tableModel.setRowCount(0);
        for (ThuocTheoLoView item : list) {
            String hsd = item.getHanSuDung() == null ? "" : item.getHanSuDung().format(dateFormatter);
            tableModel.addRow(new Object[]{
                    item.getMaThuoc(),
                    item.getTenThuoc(),
                    item.getMaLo(),
                    hsd,
                    item.getSoLuongLo(),
                    item.getDonVi(),
                    item.getMaNSX()
            });
        }
        lblTongSoBanGhi.setText("Tổng số bản ghi: " + list.size());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        if (o == btnXoaTrang) {
            txtMaThuoc.setText("");
            txtTenThuoc.setText("");
            txtMaLo.setText("");
            txtSoLuongLo.setText("");
            cboDonVi.setSelectedIndex(0);
            cboNhaSanXuat.setSelectedIndex(0);
            loadTableData(dsThuocTheoLo);
            return;
        }

        if (o == btnTimKiem) {
            String maThuocTim = txtMaThuoc.getText().trim().toLowerCase();
            String tenThuocTim = txtTenThuoc.getText().trim().toLowerCase();
            String maLoTim = txtMaLo.getText().trim().toLowerCase();
            String soLuongStr = txtSoLuongLo.getText().trim();
            String donViTim = (String) cboDonVi.getSelectedItem();
            String nsxTim = (String) cboNhaSanXuat.getSelectedItem();

            Integer soLuongLoToiThieu = null;
            if (!soLuongStr.isEmpty()) {
                try {
                    soLuongLoToiThieu = Integer.parseInt(soLuongStr);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Số lượng lô phải là số nguyên!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            List<ThuocTheoLoView> ketQua = new ArrayList<>();
            for (ThuocTheoLoView t : dsThuocTheoLo) {
                boolean match = true;
                if (!maThuocTim.isEmpty() && (t.getMaThuoc() == null || !t.getMaThuoc().toLowerCase().contains(maThuocTim))) {
                    match = false;
                }
                if (match && !tenThuocTim.isEmpty() && (t.getTenThuoc() == null || !t.getTenThuoc().toLowerCase().contains(tenThuocTim))) {
                    match = false;
                }
                if (match && !maLoTim.isEmpty() && (t.getMaLo() == null || !t.getMaLo().toLowerCase().contains(maLoTim))) {
                    match = false;
                }
                if (match && soLuongLoToiThieu != null && t.getSoLuongLo() < soLuongLoToiThieu) {
                    match = false;
                }
                if (match && donViTim != null && !donViTim.isEmpty() && (t.getDonVi() == null || !t.getDonVi().equalsIgnoreCase(donViTim))) {
                    match = false;
                }
                if (match && nsxTim != null && !nsxTim.isEmpty() && (t.getMaNSX() == null || !t.getMaNSX().equalsIgnoreCase(nsxTim))) {
                    match = false;
                }
                if (match) {
                    ketQua.add(t);
                }
            }

            if (ketQua.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy kết quả phù hợp!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                loadTableData(dsThuocTheoLo);
            } else {
                loadTableData(ketQua);
            }
        }
    }
}
