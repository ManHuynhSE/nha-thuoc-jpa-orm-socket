package com.pillchill.migration.gui.thongke;

import com.pillchill.migration.dto.LoThuocHetHan;
import com.pillchill.migration.network.client.ThongKeHSDClientController;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;


public class ThongKeTheoHSDPanel extends JPanel implements ActionListener, MouseListener {

    private final Color PRIMARY_COLOR = new Color(0, 150, 136);
    private final Color ACCENT_COLOR = new Color(255, 255, 255);
    private final Color BG_COLOR = new Color(245, 245, 245);
    private final Color TEXT_COLOR = new Color(51, 51, 51);

    private final Color BTN_INFO_COLOR = new Color(52, 152, 219);
    private final Color BTN_DELETE_COLOR = new Color(231, 76, 60);
    private final Color BTN_CLEAR_COLOR = new Color(149, 165, 166);

    private final ThongKeHSDClientController thongKeHSDClientController;

    private JButton btnThongKe, btnLamMoi, btnXoaLoThuoc;
    private JLabel lblTieuDe, lblLoaiThongKe, lblSoNgay;
    private JComboBox<String> cboLoaiThongKe;
    private JTextField txtSoNgay;
    private DefaultTableModel dtm;
    private JTable tblLoThuoc;
    private List<LoThuocHetHan> dsLoThuocHetHan;
    private SimpleDateFormat dateFormat;

    private boolean isInitializing = false;

    public ThongKeTheoHSDPanel() {
        this(null);
    }

    public ThongKeTheoHSDPanel(ThongKeHSDClientController thongKeHSDClientController) {
        this.thongKeHSDClientController = thongKeHSDClientController;
        this.dsLoThuocHetHan = new ArrayList<>();
        dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        setLayout(new BorderLayout(10, 10));
        setBackground(BG_COLOR);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        initHeader();
        initInputForm();
        initButtons();

        String[] cols = {"Mã lô", "Mã thuốc", "Tên thuốc", "Ngày sản xuất", "Hạn sử dụng", "Số lượng tồn", "Trạng thái"};
        dtm = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JPanel pnlTop = new JPanel(new BorderLayout(0, 15));
        pnlTop.setBackground(BG_COLOR);
        pnlTop.add(lblTieuDe, BorderLayout.NORTH);
        pnlTop.add(createInputPanel(), BorderLayout.CENTER);
        pnlTop.add(createButtonPanel(), BorderLayout.SOUTH);

        add(pnlTop, BorderLayout.NORTH);
        add(createBotPanel(), BorderLayout.CENTER);

        loadLoThuocHetHanAsync();
    }

    private void initHeader() {
        lblTieuDe = new JLabel("THỐNG KÊ THUỐC HẾT HẠN SỬ DỤNG", SwingConstants.CENTER);
        lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTieuDe.setForeground(PRIMARY_COLOR);
    }

    private void initInputForm() {
        Font fontLabel = new Font("Segoe UI", Font.BOLD, 14);
        Font fontText = new Font("Segoe UI", Font.PLAIN, 14);

        lblLoaiThongKe = new JLabel("Loại thống kê:");
        lblLoaiThongKe.setFont(fontLabel);

        lblSoNgay = new JLabel("Số ngày kiểm tra:");
        lblSoNgay.setFont(fontLabel);

        cboLoaiThongKe = new JComboBox<>();
        cboLoaiThongKe.addItem("Thuốc đã hết hạn");
        cboLoaiThongKe.addItem("Thuốc sắp hết hạn");
        cboLoaiThongKe.setFont(fontText);
        cboLoaiThongKe.addActionListener(this);

        txtSoNgay = new JTextField("30");
        txtSoNgay.setFont(fontText);
        txtSoNgay.setEnabled(false);
    }

    private JPanel createInputPanel() {
        JPanel pnlForm = new JPanel(new GridBagLayout());
        pnlForm.setBackground(ACCENT_COLOR);
        pnlForm.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(20, 50, 20, 50)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 20);

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.1;
        pnlForm.add(lblLoaiThongKe, gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.4;
        pnlForm.add(cboLoaiThongKe, gbc);

        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 0.1;
        pnlForm.add(lblSoNgay, gbc);
        gbc.gridx = 3; gbc.gridy = 0; gbc.weightx = 0.4;
        pnlForm.add(txtSoNgay, gbc);

        return pnlForm;
    }

    private void initButtons() {
        btnThongKe = createStyledButton("Thống kê", BTN_INFO_COLOR);
        btnLamMoi = createStyledButton("Làm mới", BTN_CLEAR_COLOR);
        btnXoaLoThuoc = createStyledButton("Xóa lô thuốc", BTN_DELETE_COLOR);

        btnThongKe.addActionListener(this);
        btnLamMoi.addActionListener(this);
        btnXoaLoThuoc.addActionListener(this);
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(140, 40));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JPanel createButtonPanel() {
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        pnlButtons.setBackground(BG_COLOR);
        pnlButtons.add(btnThongKe);
        pnlButtons.add(btnLamMoi);
        pnlButtons.add(btnXoaLoThuoc);
        return pnlButtons;
    }

    public JScrollPane createBotPanel() {
        tblLoThuoc = new JTable(dtm) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(242, 242, 242));
                }
                return c;
            }
        };

        tblLoThuoc.setRowHeight(35);
        tblLoThuoc.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tblLoThuoc.setFillsViewportHeight(true);
        tblLoThuoc.setShowGrid(true);
        tblLoThuoc.setGridColor(new Color(224, 224, 224));
        tblLoThuoc.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblLoThuoc.setSelectionBackground(new Color(178, 223, 219));
        tblLoThuoc.setSelectionForeground(Color.BLACK);

        JTableHeader header = tblLoThuoc.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 40));
        header.setReorderingAllowed(false);

        DefaultTableCellRenderer centerRenderer = (DefaultTableCellRenderer) header.getDefaultRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        tblLoThuoc.addMouseListener(this);

        JScrollPane scrollPane = new JScrollPane(tblLoThuoc);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        scrollPane.getViewport().setBackground(Color.WHITE);
        return scrollPane;
    }

    // ===== ASYNC DATA LOADING METHODS =====

    private void loadLoThuocHetHanAsync() {
        if (thongKeHSDClientController == null) return;

        isInitializing = true;
        SwingWorker<List<LoThuocHetHan>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<LoThuocHetHan> doInBackground() {
                return thongKeHSDClientController.getCacLoThuocHetHan();
            }

            @Override
            protected void done() {
                try {
                    dsLoThuocHetHan = get();
                    hienThiDuLieuLenBang();
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    isInitializing = false;
                }
            }
        };
        worker.execute();
    }

    private void loadLoThuocSapHetHanAsync(int soNgay) {
        if (thongKeHSDClientController == null) return;

        isInitializing = true;
        SwingWorker<List<LoThuocHetHan>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<LoThuocHetHan> doInBackground() {
                return thongKeHSDClientController.getCacLoThuocSapHetHan(soNgay);
            }

            @Override
            protected void done() {
                try {
                    dsLoThuocHetHan = get();
                    hienThiDuLieuLenBang();
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    isInitializing = false;
                }
            }
        };
        worker.execute();
    }

    private void xoaLoThuocAsync(String maLo, String maThuoc, String tenThuoc) {
        if (thongKeHSDClientController == null) return;

        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() {
                return thongKeHSDClientController.xoaChiTietLoThuoc(maLo, maThuoc);
            }

            @Override
            protected void done() {
                try {
                    boolean success = get();
                    if (success) {
                        JOptionPane.showMessageDialog(ThongKeTheoHSDPanel.this,
                                "Đã xóa vĩnh viễn chi tiết lô thuốc thành công!\n\n" +
                                        "Mã lô: " + maLo + "\n" +
                                        "Mã thuốc: " + maThuoc,
                                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                        refreshCurrentView();
                    } else {
                        JOptionPane.showMessageDialog(ThongKeTheoHSDPanel.this,
                                "Xóa chi tiết lô thuốc thất bại!\n\n",
                                "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(ThongKeTheoHSDPanel.this,
                            "Lỗi khi xóa: " + ex.getMessage(),
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    // ===== HELPER METHODS =====

    private void hienThiDuLieuLenBang() {
        dtm.setRowCount(0);
        String loaiThongKe = (String) cboLoaiThongKe.getSelectedItem();

        for (LoThuocHetHan thuoc : dsLoThuocHetHan) {
            String trangThai;
            if ("Thuốc đã hết hạn".equals(loaiThongKe)) {
                int soNgayQuaHan = thuoc.getSoNgayDaHetHan();
                trangThai = "Quá hạn " + soNgayQuaHan + " ngày";
            } else {
                int soNgayConLai = thuoc.getSoNgayDaHetHan();
                trangThai = "Còn " + soNgayConLai + " ngày";
            }

            Object[] rowData = {
                    thuoc.getMaLo(),
                    thuoc.getMaThuoc(),
                    thuoc.getTenThuoc(),
                    dateFormat.format(thuoc.getNgaySanXuat()),
                    dateFormat.format(thuoc.getHanSuDung()),
                    thuoc.getSoLuongTon(),
                    trangThai
            };
            dtm.addRow(rowData);
        }
    }

    private void thongKe() {
        String loaiThongKe = (String) cboLoaiThongKe.getSelectedItem();

        if ("Thuốc đã hết hạn".equals(loaiThongKe)) {
            loadLoThuocHetHanAsync();
        } else {
            try {
                int soNgay = Integer.parseInt(txtSoNgay.getText().trim());
                if (soNgay <= 0) {
                    JOptionPane.showMessageDialog(this, "Số ngày phải lớn hơn 0!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    txtSoNgay.requestFocus();
                    return;
                }
                loadLoThuocSapHetHanAsync(soNgay);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập số ngày hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                txtSoNgay.requestFocus();
            }
        }
    }

    private void lamMoi() {
        cboLoaiThongKe.setSelectedIndex(0);
        txtSoNgay.setText("30");
        txtSoNgay.setEnabled(false);
        loadLoThuocHetHanAsync();
    }

    private void xoaLoThuoc() {
        int selectedRow = tblLoThuoc.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn lô thuốc cần xóa!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String maLo = tblLoThuoc.getValueAt(selectedRow, 0).toString();
        String maThuoc = tblLoThuoc.getValueAt(selectedRow, 1).toString();
        String tenThuoc = tblLoThuoc.getValueAt(selectedRow, 2).toString();
        String ngaySanXuatStr = tblLoThuoc.getValueAt(selectedRow, 3).toString();
        String hanSuDungStr = tblLoThuoc.getValueAt(selectedRow, 4).toString();
        String soLuongTon = tblLoThuoc.getValueAt(selectedRow, 5).toString();

        // Kiểm tra hạn sử dụng
        String trangThaiHSD = "";
        try {
            java.util.Date hanSuDung = dateFormat.parse(hanSuDungStr);
            java.util.Date ngayHienTai = new java.util.Date();

            if (hanSuDung.before(ngayHienTai)) {
                long soNgayQuaHan = (ngayHienTai.getTime() - hanSuDung.getTime()) / (1000 * 60 * 60 * 24);
                trangThaiHSD = "Lô thuốc này ĐÃ HẾT HẠN " + soNgayQuaHan + " ngày";
            } else {
                long soNgayConLai = (hanSuDung.getTime() - ngayHienTai.getTime()) / (1000 * 60 * 60 * 24);
                trangThaiHSD = "Lô thuốc này CÒN HẠN SỬ DỤNG (" + soNgayConLai + " ngày nữa)";
            }
        } catch (Exception ex) {
            trangThaiHSD = "Không xác định được hạn sử dụng";
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn XÓA VĨNH VIỄN chi tiết lô thuốc này?\n\n" +
                        "Mã lô: " + maLo + "\n" +
                        "Mã thuốc: " + maThuoc + "\n" +
                        "Tên thuốc: " + tenThuoc + "\n" +
                        "Ngày sản xuất: " + ngaySanXuatStr + "\n" +
                        "Hạn sử dụng: " + hanSuDungStr + "\n" +
                        "Số lượng tồn: " + soLuongTon + "\n\n" +
                        trangThaiHSD + "\n\n",
                "Xác nhận", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            xoaLoThuocAsync(maLo, maThuoc, tenThuoc);
        }
    }

    private void refreshCurrentView() {
        String loaiThongKe = (String) cboLoaiThongKe.getSelectedItem();
        if ("Thuốc đã hết hạn".equals(loaiThongKe)) {
            loadLoThuocHetHanAsync();
        } else {
            try {
                int soNgay = Integer.parseInt(txtSoNgay.getText().trim());
                loadLoThuocSapHetHanAsync(soNgay);
            } catch (NumberFormatException ex) {
                loadLoThuocHetHanAsync();
            }
        }
    }

    public void refresh() {
        refreshCurrentView();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isInitializing) return;

        Object o = e.getSource();

        if (o == cboLoaiThongKe) {
            String loaiThongKe = (String) cboLoaiThongKe.getSelectedItem();
            if ("Thuốc sắp hết hạn".equals(loaiThongKe)) {
                txtSoNgay.setEnabled(true);
                lblSoNgay.setEnabled(true);
            } else {
                txtSoNgay.setEnabled(false);
                lblSoNgay.setEnabled(true);
            }
        } else if (o == btnThongKe) {
            thongKe();
        } else if (o == btnLamMoi) {
            lamMoi();
        } else if (o == btnXoaLoThuoc) {
            xoaLoThuoc();
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}
}