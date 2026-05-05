package com.pillchill.migration.gui.capnhat;

import java.awt.BorderLayout;
import java.awt.CardLayout;
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
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

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
import javax.swing.SwingWorker;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import com.formdev.flatlaf.FlatLightLaf;
import com.pillchill.migration.entity.Thuoc;
import com.pillchill.migration.network.client.ThuocClientController;


public class CapNhatThuocSubPanel extends JPanel implements ActionListener, MouseListener {
    
    private final Color PRIMARY_COLOR = new Color(0, 150, 136);
    private final Color ACCENT_COLOR = new Color(255, 255, 255);
    private final Color BG_COLOR = new Color(245, 245, 245);
    private final Color TEXT_COLOR = new Color(51, 51, 51);
    
    private final Color BTN_ADD_COLOR = new Color(46, 204, 113);
    private final Color BTN_CLEAR_COLOR = new Color(149, 165, 166);

    private JLabel lblTieuDe;
    private JLabel lblMaThuoc;
    private JLabel lblTenThuoc;
    private JLabel lblSoLuongTon;
    private JLabel lblDonVi;
    private JLabel lblNhaSanXuat;

    private JTextField txtMaThuoc;
    private JTextField txtTenThuoc;
    private JTextField txtSoLuongTon;
    private JComboBox<String> cboDonVi;
    private JComboBox<String> cboNhaSanXuat;


    private JButton btnKhoiPhuc;
    private JButton btnQuayLai;
    private JButton btnLamMoi;
    
    
    private DefaultTableModel dtm;
    private JTable tblThuoc;
    
    private ArrayList<Thuoc> dsThuoc;
    private Set<String> dsDonVi;
    private Set<String> dsNhaSanXuat;
    private Map<String, String> mapNhaSanXuat;
    private CardLayout cardLayout;
    private JPanel mainContainer;
    private CapNhatThuocPanel pnlCapNhatThuoc;
    private final ThuocClientController thuocClientController;
    
    public CapNhatThuocSubPanel(CapNhatThuocPanel pnlCapNhatThuoc, ThuocClientController thuocClientController) {
        this.pnlCapNhatThuoc = pnlCapNhatThuoc;
        this.thuocClientController = thuocClientController;
        FlatLightLaf.setup();
        mapNhaSanXuat = new HashMap<>();
        dsThuoc = new ArrayList<>();
        dsDonVi = new LinkedHashSet<>();
        dsNhaSanXuat = new LinkedHashSet<>();
        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);
        
        setLayout(new BorderLayout());

        initHeader();
        initInputForm();
        initButtons();
        
        String[] cols = {"Mã thuốc" , "Tên thuốc", "Số lượng", "Đơn vị", "Nhà SX"};
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
        JPanel pnlMain = new JPanel(new BorderLayout(10,10));
        pnlMain.setBackground(BG_COLOR);
        pnlMain.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
  
        pnlMain.add(pnlTop, BorderLayout.NORTH);
        pnlMain.add(createBotPanel(), BorderLayout.CENTER);
        
        mainContainer.add(pnlMain, "DanhSach");
        cardLayout.show(mainContainer, "DanhSach");
        
        add(mainContainer, BorderLayout.CENTER);
        loadThuocData();
    }

    private void initHeader() {
        lblTieuDe = new JLabel("QUẢN LÝ THUỐC ĐÃ XÓA", SwingConstants.CENTER);
        lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTieuDe.setForeground(PRIMARY_COLOR);
    }

    private void initInputForm() {
        Font fontLabel = new Font("Segoe UI", Font.BOLD, 14);
        Font fontText = new Font("Segoe UI", Font.PLAIN, 14);

        lblMaThuoc = new JLabel("Mã thuốc:");
        lblMaThuoc.setFont(fontLabel);
        lblTenThuoc = new JLabel("Tên thuốc:");
        lblTenThuoc.setFont(fontLabel);
        lblSoLuongTon = new JLabel("Số lượng:");
        lblSoLuongTon.setFont(fontLabel);
        lblDonVi = new JLabel("Đơn vị:");
        lblDonVi.setFont(fontLabel);
        lblNhaSanXuat = new JLabel("Nhà sản xuất:");
        lblNhaSanXuat.setFont(fontLabel);
        
        txtMaThuoc = new JTextField();
        txtMaThuoc.setFont(fontText);
        txtMaThuoc.setPreferredSize(new Dimension(200, 35));

        txtTenThuoc = new JTextField();
        txtTenThuoc.setFont(fontText);
        txtTenThuoc.setPreferredSize(new Dimension(200, 35));

        txtSoLuongTon = new JTextField();
        txtSoLuongTon.setFont(fontText);
        txtSoLuongTon.setPreferredSize(new Dimension(200, 35));

        
        cboDonVi = new JComboBox<>();
        cboDonVi.setFont(fontText);
        cboDonVi.setPreferredSize(new Dimension(200, 35));
        
        cboNhaSanXuat = new JComboBox<>();
        cboNhaSanXuat.setFont(fontText);
        cboNhaSanXuat.setPreferredSize(new Dimension(200, 35));
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

        // Row 0
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.1;
        pnlForm.add(lblMaThuoc, gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.4;
        pnlForm.add(txtMaThuoc, gbc);
        
        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 0.1;
        pnlForm.add(lblTenThuoc, gbc);
        gbc.gridx = 3; gbc.gridy = 0; gbc.weightx = 0.4;
        pnlForm.add(txtTenThuoc, gbc);

        // Row 1
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.1;
        pnlForm.add(lblSoLuongTon, gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 0.4;
        pnlForm.add(txtSoLuongTon, gbc);
        
       

        // Row 2
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.1;
        pnlForm.add(lblDonVi, gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 0.4;
        pnlForm.add(cboDonVi, gbc);
        
        gbc.gridx = 2; gbc.gridy = 1; gbc.weightx = 0.1;
        pnlForm.add(lblNhaSanXuat, gbc);
        gbc.gridx = 3; gbc.gridy = 1; gbc.weightx = 0.4;
        pnlForm.add(cboNhaSanXuat, gbc);

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
        btn.setPreferredSize(new Dimension(120, 40));
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
        tblThuoc = new JTable(dtm) {
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
        tblThuoc.setFillsViewportHeight(true);
        tblThuoc.setShowGrid(true);
        tblThuoc.setGridColor(new Color(224, 224, 224));
        tblThuoc.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblThuoc.setSelectionBackground(new Color(178, 223, 219));
        tblThuoc.setSelectionForeground(Color.BLACK);
        
        JTableHeader header = tblThuoc.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 40));
        header.setReorderingAllowed(false);
        
        DefaultTableCellRenderer centerRenderer = (DefaultTableCellRenderer) header.getDefaultRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        tblThuoc.getColumnModel().getColumn(1).setPreferredWidth(150);
        tblThuoc.getColumnModel().getColumn(4).setPreferredWidth(150);

        tblThuoc.addMouseListener(this);
        
        JScrollPane scrollPane = new JScrollPane(tblThuoc);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        scrollPane.getViewport().setBackground(Color.WHITE);
        return scrollPane;
    }
    
    public void loadThuocData() {
        SwingWorker<List<Thuoc>, Void> worker = new SwingWorker<List<Thuoc>, Void>() {
            @Override
            protected List<Thuoc> doInBackground() {
                return thuocClientController.getAllInactiveThuoc();
            }

            @Override
            protected void done() {
                try {
                    List<Thuoc> items = get();
                    dsThuoc = new ArrayList<>(items);
                    dsDonVi.clear();
                    dsNhaSanXuat.clear();
                    mapNhaSanXuat.clear();
                    dtm.setRowCount(0);

                    for (Thuoc thuoc : items) {
                        String tenDonVi = thuoc.getDonVi() != null ? thuoc.getDonVi().getTenDonVi() : "";
                        String tenNSX = thuoc.getNhaSanXuat() != null ? thuoc.getNhaSanXuat().getTenNSX() : "";
                        Object[] rowData = {
                                thuoc.getMaThuoc(),
                                thuoc.getTenThuoc(),
                                thuoc.getSoLuongTon(),
                                tenDonVi,
                                tenNSX
                        };
                        dtm.addRow(rowData);
                        if (!tenDonVi.isBlank()) {
                            dsDonVi.add(tenDonVi);
                        }
                        if (!tenNSX.isBlank()) {
                            dsNhaSanXuat.add(tenNSX);
                        }
                    }
                    loadDonViData();
                    loadNhaSanXuatData();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(
                            pnlCapNhatThuoc,
                            "Tải danh sách thuốc đã xóa thất bại: " + e.getMessage(),
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };
        worker.execute();
    }

    private void loadDonViData() {
        cboDonVi.removeAllItems();
        for (String tenDonVi : dsDonVi) {
            cboDonVi.addItem(tenDonVi);
        }
    }
    
    public void loadNhaSanXuatData() {
        cboNhaSanXuat.removeAllItems();
        for (String tenNSX : dsNhaSanXuat) {
            cboNhaSanXuat.addItem(tenNSX);
        }
    }

    public void khoiPhucThuoc() {
        String maThuoc = txtMaThuoc.getText().trim();
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() {
                return thuocClientController.reactivateThuoc(maThuoc);
            }

            @Override
            protected void done() {
                try {
                    boolean result = get();
                    if (result) {
                        JOptionPane.showMessageDialog(CapNhatThuocSubPanel.this, "Khôi phục thuốc thành công!");
                        xoaTrang();
                    } else {
                        JOptionPane.showMessageDialog(CapNhatThuocSubPanel.this, "Khôi phục thuốc không thành công!");
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(
                            pnlCapNhatThuoc,
                            "Khôi phục thuốc thất bại: " + e.getMessage(),
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };
        worker.execute();
    }
    
    public void xoaTrang() {
        txtMaThuoc.setText("");
        txtTenThuoc.setText("");
        txtSoLuongTon.setText("0");
        if (cboDonVi.getItemCount() > 0) {
            cboDonVi.setSelectedIndex(0);
        }
        if (cboNhaSanXuat.getItemCount() > 0) {
            cboNhaSanXuat.setSelectedIndex(0);
        }
        txtMaThuoc.setEnabled(true);
        txtSoLuongTon.setEnabled(true);
        tblThuoc.clearSelection();
        loadThuocData();
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        if(o == btnKhoiPhuc) {
            String maThuoc = txtMaThuoc.getText().trim();
            if (maThuoc.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn thuốc cần khôi phục!");
                return;
            }
           
            int option = JOptionPane.showConfirmDialog(this, 
                    "Có chắc muốn khôi phục thuốc " + maThuoc + "?", 
                    "Xác nhận", 
                    JOptionPane.YES_NO_OPTION);
            
            if(option == JOptionPane.YES_OPTION) {
                khoiPhucThuoc();
            }
        }
        else if(o == btnQuayLai) {
           pnlCapNhatThuoc.quayLaiDanhSach();
        }
        else if (o == btnLamMoi) {
            xoaTrang();
        }
        
    }
    
    private boolean validateInput(boolean isAddingNew) {
        if (txtMaThuoc.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mã thuốc không được để trống!");
            txtMaThuoc.requestFocus();
            return false;
        }
        if (txtTenThuoc.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên thuốc không được để trống!");
            txtTenThuoc.requestFocus();
            return false;
        }
        
        
        String maThuoc = txtMaThuoc.getText().trim();
        if(isAddingNew) {
            for(Thuoc item : dsThuoc) {
                if(item.getMaThuoc().equalsIgnoreCase(maThuoc)) {
                    JOptionPane.showMessageDialog(this, "Mã thuốc không được trùng!");
                    txtMaThuoc.requestFocus();
                    return false;
                }
            }
        }
        if (!maThuoc.matches("T\\d{3}")) {
            JOptionPane.showMessageDialog(this, "Mã thuốc phải có định dạng T kèm 3 ký số (Ví dụ: T001)!");
            txtMaThuoc.requestFocus();
            return false;
        }
    
       
        
        return true;
    }
    
    @Override   
    public void mouseClicked(MouseEvent e) {
        Object o = e.getSource();
        if(o == tblThuoc) {
            txtMaThuoc.setEnabled(false);
            txtSoLuongTon.setEnabled(false);
            int row = tblThuoc.getSelectedRow();
            if (row >= 0) {
                txtMaThuoc.setText(tblThuoc.getValueAt(row, 0).toString());
                txtTenThuoc.setText(tblThuoc.getValueAt(row, 1).toString());
                txtSoLuongTon.setText(tblThuoc.getValueAt(row, 2).toString());
              
                cboDonVi.setSelectedItem(tblThuoc.getValueAt(row, 3).toString());
                cboNhaSanXuat.setSelectedItem(tblThuoc.getValueAt(row, 4).toString());
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