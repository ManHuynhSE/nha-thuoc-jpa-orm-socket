package com.pillchill.migration.gui.capnhat;

import java.awt. BorderLayout;
import java.awt.CardLayout;
import java.awt. Color;
import java.awt. Component;
import java.awt. Cursor;
import java.awt. Dimension;
import java.awt. FlowLayout;
import java. awt.Font;
import java. awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt. Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java. awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import com.formdev.flatlaf.FlatLightLaf;
import com.pillchill.migration.dto.NhanVienDTO;
import com.pillchill.migration.entity.ChucVu;
import com.pillchill.migration.entity.NhanVien;
import com.pillchill.migration.network.client.ChucVuClientController;
import com.pillchill.migration.network.client.NhanVienClientController;


public class CapNhatNhanVienSubPanel extends JPanel implements ActionListener, MouseListener {
    
    private final Color PRIMARY_COLOR = new Color(0, 150, 136);
    private final Color ACCENT_COLOR = new Color(255, 255, 255);
    private final Color BG_COLOR = new Color(245, 245, 245);
    private final Color TEXT_COLOR = new Color(51, 51, 51);
    
    private final Color BTN_ADD_COLOR = new Color(46, 204, 113);
    private final Color BTN_CLEAR_COLOR = new Color(149, 165, 166);

    private JLabel lblTieuDe;
    private JLabel lblMaNv;
    private JLabel lblTenNv;
    private JLabel lblSoDienThoai;
    private JLabel lblChucVu;

    private JTextField txtMaNv;
    private JTextField txtTenNv;
    private JTextField txtSoDienThoai;
    private JComboBox<String> cboChucVu;

    private JButton btnKhoiPhuc;
    private JButton btnQuayLai;
    private JButton btnLamMoi;
    
    private DefaultTableModel dtm;
    private JTable tblNhanVien;
    
    private ArrayList<NhanVienDTO> dsNhanVien;
    private ArrayList<ChucVu> dsChucVu;
    private Map<String, String> mapChucVu;

    private CardLayout cardLayout;
    private JPanel mainContainer;
    private CapNhatNhanVienPanel pnlCapNhatNhanVien;

    private final NhanVienClientController nhanVienClientController;
    private final ChucVuClientController chucVuClientController;
    
    public CapNhatNhanVienSubPanel(CapNhatNhanVienPanel pnlCapNhatNhanVien, NhanVienClientController nhanVienClientController, ChucVuClientController chucVuClientController) {
        this.pnlCapNhatNhanVien = pnlCapNhatNhanVien;
        FlatLightLaf.setup();
        mapChucVu = new HashMap<>();
        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);
        dsNhanVien = new ArrayList<>();
        dsChucVu = new ArrayList<>();
        
        this.nhanVienClientController = nhanVienClientController;
        this.chucVuClientController = chucVuClientController;
        setLayout(new BorderLayout());
        
        initHeader();
        initInputForm();
        initButtons();
        
        String[] cols = {"Mã nhân viên", "Tên nhân viên", "Số điện thoại", "Chức vụ"};
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
        
        JPanel pnlMain = new JPanel(new BorderLayout(10, 10));
        pnlMain.setBackground(BG_COLOR);
        pnlMain.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
  
        pnlMain.add(pnlTop, BorderLayout.NORTH);
        pnlMain.add(createBotPanel(), BorderLayout.CENTER);
        
        mainContainer.add(pnlMain, "DanhSach");
        cardLayout.show(mainContainer, "DanhSach");
        
        add(mainContainer, BorderLayout.CENTER);
        loadNhanVienData();
        loadChucVuData();
    }

    private void initHeader() {
        lblTieuDe = new JLabel("QUẢN LÝ NHÂN VIÊN ĐÃ XÓA", SwingConstants.CENTER);
        lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTieuDe.setForeground(PRIMARY_COLOR);
        lblTieuDe.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
    }

    private void initInputForm() {
        Font fontLabel = new Font("Segoe UI", Font.BOLD, 14);
        Font fontText = new Font("Segoe UI", Font.PLAIN, 14);

        lblMaNv = new JLabel("Mã NV:");
        lblMaNv.setFont(fontLabel);
        lblTenNv = new JLabel("Tên NV:");
        lblTenNv.setFont(fontLabel);
        lblSoDienThoai = new JLabel("Số điện thoại:");
        lblSoDienThoai.setFont(fontLabel);
        lblChucVu = new JLabel("Chức vụ:");
        lblChucVu.setFont(fontLabel);
        
        txtMaNv = new JTextField();
        txtMaNv.setFont(fontText);
        txtMaNv.setPreferredSize(new Dimension(200, 35));
        
        txtTenNv = new JTextField();
        txtTenNv.setFont(fontText);
        txtTenNv.setPreferredSize(new Dimension(200, 35));
        
        txtSoDienThoai = new JTextField();
        txtSoDienThoai.setFont(fontText);
        txtSoDienThoai.setPreferredSize(new Dimension(200, 35));
        
        cboChucVu = new JComboBox<>();
        cboChucVu.setFont(fontText);
        cboChucVu.setPreferredSize(new Dimension(200, 35));
    }

    private JPanel createInputPanel() {
        JPanel pnlForm = new JPanel(new GridBagLayout());
        pnlForm.setBackground(ACCENT_COLOR);
        pnlForm.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(20, 50, 20, 50)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints. HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 20);

        // Row 0
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.1;
        pnlForm.add(lblMaNv, gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.4;
        pnlForm. add(txtMaNv, gbc);
        
        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 0.1;
        pnlForm.add(lblTenNv, gbc);
        gbc.gridx = 3; gbc.gridy = 0; gbc.weightx = 0.4;
        pnlForm.add(txtTenNv, gbc);

        // Row 1
        gbc. gridx = 0; gbc.gridy = 1; gbc.weightx = 0.1;
        pnlForm.add(lblSoDienThoai, gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 0.4;
        pnlForm.add(txtSoDienThoai, gbc);
        
        gbc. gridx = 2; gbc.gridy = 1; gbc.weightx = 0.1;
        pnlForm.add(lblChucVu, gbc);
        gbc.gridx = 3; gbc.gridy = 1; gbc.weightx = 0.4;
        pnlForm.add(cboChucVu, gbc);

        return pnlForm;
    }

    private void initButtons() {
        btnKhoiPhuc = createStyledButton("Khôi phục", BTN_ADD_COLOR);
        btnQuayLai = createStyledButton("Quay Lại", BTN_CLEAR_COLOR);
        btnLamMoi = createStyledButton("Làm mới", BTN_ADD_COLOR);
        
        btnKhoiPhuc.addActionListener(this);
        btnQuayLai.addActionListener(this);
        btnLamMoi.addActionListener(this);
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(130, 45));
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
        pnlButtons.add(btnKhoiPhuc);
        pnlButtons.add(btnQuayLai);
        pnlButtons.add(btnLamMoi);
        return pnlButtons;
    }
    
    public JScrollPane createBotPanel() {
        tblNhanVien = new JTable(dtm) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(242, 242, 242));
                }
                return c;
            }
        };        
        
        tblNhanVien. setRowHeight(35);
        tblNhanVien.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tblNhanVien.setFillsViewportHeight(true);
        tblNhanVien. setShowGrid(true);
        tblNhanVien.setGridColor(new Color(224, 224, 224));
        tblNhanVien. setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblNhanVien.setSelectionBackground(new Color(178, 223, 219));
        tblNhanVien.setSelectionForeground(Color.BLACK);
        
        JTableHeader header = tblNhanVien.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 40));
        header.setReorderingAllowed(false);
        
        DefaultTableCellRenderer centerRenderer = (DefaultTableCellRenderer) header.getDefaultRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        // Center align cho tất cả các cột
        DefaultTableCellRenderer cellCenter = new DefaultTableCellRenderer();
        cellCenter.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < tblNhanVien. getColumnCount(); i++) {
            tblNhanVien. getColumnModel().getColumn(i).setCellRenderer(cellCenter);
        }

        tblNhanVien.addMouseListener(this);
        
        JScrollPane scrollPane = new JScrollPane(tblNhanVien);
        scrollPane. setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        scrollPane.getViewport().setBackground(Color.WHITE);
        return scrollPane;
    }

    public void loadNhanVienData() {
        SwingWorker<List<NhanVienDTO>, Void> worker = new SwingWorker<List<NhanVienDTO>, Void>() {
            @Override
            protected List<NhanVienDTO> doInBackground() throws Exception {
                System.out.println(nhanVienClientController.getAllInactiveNhanVien());
                return nhanVienClientController.getAllInactiveNhanVien();
            }

            @Override
            protected void done() {
                try {
                    List<NhanVienDTO> items = get();
                    dtm.setRowCount(0);
                    for(NhanVienDTO nv : items) {
                        Object[] rowData = {
                                nv.getMaNV(),
                                nv.getTenNV(),
                                nv.getSoDienThoai(),
                                nv.getChucVu()
                        };
                        dtm.addRow(rowData);
                        System.out.println(nv);
                    }
                    dsNhanVien = new ArrayList<>(items);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(
                            pnlCapNhatNhanVien,
                            "Tải danh sách nhân viên thất bại: " + e.getMessage(),
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
//                renderNhanVienTable();
            }
        };
        worker.execute();
    }
    public void loadChucVuData() {
        dsChucVu.clear();
        cboChucVu.removeAllItems();
        mapChucVu.clear();
        SwingWorker<List<ChucVu>, Void> worker = new SwingWorker<List<ChucVu>, Void>() {
            @Override
            protected List<ChucVu> doInBackground() throws Exception {
                System.out.println(chucVuClientController.getAllChucVu());
                return chucVuClientController.getAllChucVu();
            }
            @Override
            protected void done() {
                try {
                    List<ChucVu> items = get();
                    for(ChucVu item : items) {
                        dsChucVu.add(item);
                        cboChucVu.addItem(item.getTenChucVu());
                        mapChucVu.put(item.getMaChucVu(), item.getTenChucVu());
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(
                            pnlCapNhatNhanVien,
                            "Tải danh sách chức vụ thất bại: " + e.getMessage(),
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };
        worker.execute();
    }
    
    public void khoiPhucNhanVien(){
        String maNhanVien = txtMaNv.getText();
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                nhanVienClientController.reactiveNhanVien(maNhanVien);
                return true;
            }

            @Override
            protected void done() {
                try {
                    get();
                    xoaTrang();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(
                            pnlCapNhatNhanVien,
                            "Khôi phục nhân viên thất bại: " + e.getMessage(),
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };
        worker.execute();
    }
    public void xoaTrang() {
        txtMaNv.setText("");
        txtTenNv.setText("");
        txtSoDienThoai.setText("");
        if(cboChucVu. getItemCount() > 0) cboChucVu.setSelectedIndex(0);
        txtMaNv.setEnabled(true);
        tblNhanVien.clearSelection();
        loadNhanVienData();
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        
        if(o == btnKhoiPhuc) {
            String maNV = txtMaNv.getText().trim();
            
            if(maNV.isEmpty()) {
                JOptionPane. showMessageDialog(this, "Vui lòng chọn nhân viên cần khôi phục!");
                return;
            }
            
            int option = JOptionPane.showConfirmDialog(this, 
                    "Có chắc muốn khôi phục nhân viên " + maNV + "?", 
                    "Xác nhận", 
                    JOptionPane.YES_NO_OPTION);
            
            if(option == JOptionPane.YES_OPTION) {
                khoiPhucNhanVien();
//                if(result) {
//                    JOptionPane.showMessageDialog(this, "Khôi phục nhân viên thành công!");
//                    loadNhanVienData();
//                    xoaTrang();
//                } else {
//                    JOptionPane.showMessageDialog(this, "Khôi phục nhân viên không thành công!");
//                }
            }
        }
        else if(o == btnQuayLai) {
            pnlCapNhatNhanVien.quayLaiDanhSach();
        }
        else if(o == btnLamMoi) {
            xoaTrang();
        }
    }
    
    @Override   
    public void mouseClicked(MouseEvent e) {
        Object o = e.getSource();
        if(o == tblNhanVien) {
            txtMaNv.setEnabled(false);
            int row = tblNhanVien.getSelectedRow();
            if (row >= 0) {
                txtMaNv.setText(tblNhanVien.getValueAt(row, 0).toString());
                txtTenNv. setText(tblNhanVien.getValueAt(row, 1).toString());
                txtSoDienThoai.setText(tblNhanVien.getValueAt(row, 2).toString());
                String tenChucVu = tblNhanVien.getValueAt(row, 3).toString();
                cboChucVu.setSelectedItem(tenChucVu);
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}
}