package com.pillchill.migration.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.URL;

import javax.swing.*;



public class MenuBarPanel extends JPanel implements ActionListener {
    

    private String maNV;
    private MainFrame parentFrame;

    private JMenuItem mniHoTro, mniDatLai, mniTaiKhoan, mniDangXuat, mniThoat;
    private JMenuItem mniCapNhatThuoc, mniCapNhatKhachHang, mniCapNhatNhanVien, mniCapNhatKhuyenMai, mniCapNhatChucVu, mniCapNhatDonVi, mniXemPhieuNhap, mniCapNhatBangGia;
    private JMenuItem mniTimKiemThuoc, mniTimKiemKhachHang, mniTimKiemNhanVien,mniTimKiemChiTietLoThuoc, mniHoaDon, mniPhieuDoiTra, mniPhieuDatThuoc;
    private JMenuItem mniLapHoaDon, mniLapPhieuDatThuoc, mniLapPhieuTraThuoc, mniNhapThuoc;
    private JMenuItem mniDoanhThuTheoThang, mniDoanhThuTheoNam, mniNhanVien, mniKhachHang, mniHanSuDung, mniThuocDuocMuaNhieu;


    private final Color TEXT_NORMAL = new Color(70, 70, 70);       
    private final Color TEXT_HOVER = new Color(0, 150, 136);       
    private final Color SEPARATOR_COLOR = new Color(220, 220, 220);
    private final Color NAVBAR_BG = Color.WHITE;                  

    public MenuBarPanel(String maNV, MainFrame parentFrame) {
        this.maNV = maNV;
        this.parentFrame = parentFrame;
        
        // Setup Panel chính
        this.setLayout(new BorderLayout());
        this.setBackground(new Color(242, 242, 242)); 
        this.setBorder(BorderFactory.createEmptyBorder(10, 15, 5, 15)); // Margin bên ngoài thanh menu

        // init cac menu item
        initMenuItems();
        
        // ve giao dien menu
        initializeMenuBar();
    }

    private void initializeMenuBar() {
//        TaiKhoanDAO tkDAO = new TaiKhoanDAO();
//        String tenNhanVien = tkDAO.getTenNhanVienByMaNV(maNV);

        // main container
        JPanel navbarPanel = new JPanel();
        navbarPanel.setLayout(new BoxLayout(navbarPanel, BoxLayout.LINE_AXIS));
        navbarPanel.setBackground(NAVBAR_BG);
//        navbarPanel.putClientProperty(FlatClientProperties.STYLE, ""
//                + "arc: 15;"
//                + "border: 0,10,0,10;" );
        
        // Font chữ menu chính
        Font fontMenu = new Font("Segoe UI", Font.BOLD, 14);

        // button
        JButton btnHeThong = createLinkButton("Hệ thống", "/resources/icon/system_icon.png", fontMenu);
        JButton btnDanhMuc = createLinkButton("Danh mục", "/resources/icon/task_icon.png", fontMenu);
        JButton btnTimKiem = createLinkButton("Tìm kiếm", "/resources/icon/magnifier_icon.png", fontMenu);
        JButton btnXuLy = createLinkButton("Xử lý", "/resources/icon/update_icon.png", fontMenu);
        JButton btnThongKe = createLinkButton("Thống kê", "/resources/icon/chart_icon.png", fontMenu);

        // gán menu con (pop up) cho cái button
        setupPopupMenu(btnHeThong, mniHoTro, mniDatLai, mniTaiKhoan, mniDangXuat, mniThoat);
        setupPopupMenu(btnDanhMuc, mniCapNhatThuoc, mniCapNhatKhachHang, mniCapNhatNhanVien, mniCapNhatKhuyenMai, mniCapNhatChucVu, mniCapNhatDonVi, mniCapNhatBangGia);
        setupPopupMenu(btnTimKiem, mniTimKiemThuoc, mniTimKiemKhachHang, mniTimKiemNhanVien,mniTimKiemChiTietLoThuoc, mniHoaDon, mniPhieuDatThuoc, mniPhieuDoiTra, mniXemPhieuNhap);
        setupPopupMenu(btnXuLy, mniLapHoaDon, mniLapPhieuDatThuoc, mniLapPhieuTraThuoc, mniNhapThuoc);
        setupPopupMenu(btnThongKe, mniDoanhThuTheoThang, mniDoanhThuTheoNam, mniNhanVien, mniKhachHang, mniHanSuDung, mniThuocDuocMuaNhieu);

        // chèn vô nav bar
        navbarPanel.add(Box.createHorizontalStrut(5));
        navbarPanel.add(btnHeThong);
        navbarPanel.add(createSeparator());
        
        navbarPanel.add(btnDanhMuc);
        navbarPanel.add(createSeparator());
        
        navbarPanel.add(btnTimKiem);
        navbarPanel.add(createSeparator());
        
        navbarPanel.add(btnXuLy);
        navbarPanel.add(createSeparator());
        
        navbarPanel.add(btnThongKe);

        // info của nhân viên
        navbarPanel.add(Box.createHorizontalGlue()); // đẩy phần sau sang phải cùng

        JLabel lblUserIcon = new JLabel(loadIcon("/resources/icon/profile.png")); // Icon user 
        JLabel lblWelcome = new JLabel("Chào, ");
        JLabel lblName = new JLabel("null");
        
        lblWelcome.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblWelcome.setForeground(Color.GRAY);
        
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblName.setForeground(TEXT_HOVER); // Tô màu tên nhân viên


        navbarPanel.add(Box.createHorizontalStrut(5));
        navbarPanel.add(lblWelcome);
        navbarPanel.add(lblName);
        navbarPanel.add(Box.createHorizontalStrut(5));
        navbarPanel.add(lblUserIcon);
        navbarPanel.add(Box.createHorizontalStrut(15));

        // Giới hạn chiều cao cho navbar 
        navbarPanel.setPreferredSize(new Dimension(1200, 55));
        navbarPanel.setMaximumSize(new Dimension(3000, 55));
        
        this.add(navbarPanel, BorderLayout.CENTER);
    }

    /**
     * Tạo button
     */
    private JButton createLinkButton(String text, String iconPath, Font font) {
        JButton btn = new JButton(" " + text);
        btn.setIcon(loadIcon(iconPath));
        btn.setFont(font);
        

        btn.setForeground(TEXT_NORMAL);
        btn.setBackground(NAVBAR_BG);
        btn.setContentAreaFilled(false); // Xóa background cho button
        btn.setBorderPainted(false);     // Xóa viền
        btn.setFocusPainted(false);      // xóa viền khi focus
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btn.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));

        // hover custom
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setForeground(TEXT_HOVER); // hover 
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setForeground(TEXT_NORMAL);
            }
        });
        return btn;
    }

    /**
     * custom gạch phân cách giữa các menu
     */
    private JComponent createSeparator() {
        JPanel pnlSep = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(SEPARATOR_COLOR);
                // Vẽ đường kẻ dọc ở giữa, cao 60% panel
                int h = getHeight();
                int lineH = (int)(h * 0.5); 
                int y = (h - lineH) / 2;
                g.drawLine(getWidth()/2, y, getWidth()/2, y + lineH);
            }
        };
        pnlSep.setOpaque(false); // trong suốt
        pnlSep.setPreferredSize(new Dimension(20, 40)); 
        pnlSep.setMaximumSize(new Dimension(20, 40));
        return pnlSep;
    }

    /**
     * Khởi tạo các JMenuItem và gán phím tắt
     */
    private void initMenuItems() {
        // HỆ THỐNG
        mniHoTro = createItem("Hỗ trợ", "/resources/icon/support_icon.png", KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
        mniDatLai = createItem("Đặt lại", "/resources/icon/reset_icon.png", KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
        mniTaiKhoan = createItem("Đổi mật khẩu", "/resources/icon/password_icon.png", KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK));
        mniDangXuat = createItem("Đăng xuất", "/resources/icon/logout_icon.png", KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.CTRL_MASK));
        mniThoat = createItem("Thoát", "/resources/icon/poweroff_icon.png", KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));

        // CẬP NHẬT
        mniCapNhatThuoc = createItem("Thuốc", "/resources/icon/drug_icon.png", KeyStroke.getKeyStroke(KeyEvent.VK_T, ActionEvent.CTRL_MASK));
        mniCapNhatKhachHang = createItem("Khách hàng", "/resources/icon/customer_icon.png", KeyStroke.getKeyStroke(KeyEvent.VK_K, ActionEvent.CTRL_MASK));
        mniCapNhatNhanVien = createItem("Nhân viên", "/resources/icon/employee_icon.png", KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
        mniCapNhatKhuyenMai = createItem("Khuyến mãi", "/resources/icon/sale_icon.png", KeyStroke.getKeyStroke(KeyEvent.VK_M, ActionEvent.CTRL_MASK));
        mniCapNhatChucVu = createItem("Chức vụ", "/resources/icon/suitcase_icon.png", KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK));
        mniCapNhatDonVi = createItem("Đơn vị", "/resources/icon/unit_icon.png", KeyStroke.getKeyStroke(KeyEvent.VK_U, ActionEvent.CTRL_MASK));
        mniCapNhatBangGia = createItem("Bảng giá", "/resources/icon/price_icon.png", KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
       

        // TÌM KIẾM
        mniTimKiemThuoc = createItem("Thuốc", "/resources/icon/drug_icon.png", KeyStroke.getKeyStroke(KeyEvent.VK_T, ActionEvent.ALT_MASK));
        mniTimKiemKhachHang = createItem("Khách hàng", "/resources/icon/customer_icon.png", KeyStroke.getKeyStroke(KeyEvent.VK_K, ActionEvent.ALT_MASK));
        mniTimKiemNhanVien = createItem("Nhân viên", "/resources/icon/employee_icon.png", KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.ALT_MASK));
        mniTimKiemChiTietLoThuoc = createItem("Lô thuốc", "/resources/icon/employee_icon.png", KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.ALT_MASK));
        mniHoaDon = createItem("Hóa đơn", "/resources/icon/bill_icon.png", KeyStroke.getKeyStroke(KeyEvent.VK_H, ActionEvent.ALT_MASK));
        mniPhieuDatThuoc = createItem("Phiếu đặt thuốc", "/resources/icon/order_icon.png", KeyStroke.getKeyStroke(KeyEvent.VK_D, ActionEvent.ALT_MASK));
        mniPhieuDoiTra = createItem("Phiếu đổi trả", "/resources/icon/cash_back_icon.png", KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.ALT_MASK));
        mniXemPhieuNhap = createItem("Phiếu nhập", "/resources/icon/import_icon.png", KeyStroke.getKeyStroke(KeyEvent.VK_I, ActionEvent.ALT_MASK));
        
        // XỬ LÝ
        mniLapHoaDon = createItem("Lập hóa đơn", "/resources/icon/bill_icon.png", KeyStroke.getKeyStroke(KeyEvent.VK_H, ActionEvent.CTRL_MASK));
        mniLapPhieuDatThuoc = createItem("Lập phiếu đặt thuốc", "/resources/icon/order_icon.png", KeyStroke.getKeyStroke(KeyEvent.VK_D, ActionEvent.CTRL_MASK));
        mniLapPhieuTraThuoc = createItem("Lập phiếu trả thuốc", "/resources/icon/refund_icon.png", KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.CTRL_MASK));
        mniNhapThuoc = createItem("Nhập thuốc", "/resources/icon/import_icon.png", KeyStroke.getKeyStroke(KeyEvent.VK_I, ActionEvent.CTRL_MASK | ActionEvent.SHIFT_MASK));

        // THỐNG KÊ
        mniDoanhThuTheoThang = createItem("Theo tháng", "/resources/icon/month_icon.png", KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.CTRL_MASK));
        mniDoanhThuTheoNam = createItem("Theo năm", "/resources/icon/year_icon.png", KeyStroke.getKeyStroke(KeyEvent.VK_2, ActionEvent.CTRL_MASK));
        mniNhanVien = createItem("Doanh thu của NV", "/resources/icon/employee_revenue_icon.png", KeyStroke.getKeyStroke(KeyEvent.VK_3, ActionEvent.CTRL_MASK));
        mniKhachHang = createItem("Doanh thu từ KH", "/resources/icon/customer_revenue_icon.png", KeyStroke.getKeyStroke(KeyEvent.VK_4, ActionEvent.CTRL_MASK));
        mniHanSuDung = createItem("Hạn sử dụng", "/resources/icon/expiration_date_icon.png", KeyStroke.getKeyStroke(KeyEvent.VK_5, ActionEvent.CTRL_MASK));
        mniThuocDuocMuaNhieu = createItem("Thuốc mua nhiều", "/resources/icon/top_icon.png", KeyStroke.getKeyStroke(KeyEvent.VK_6, ActionEvent.CTRL_MASK));
    }

    private JMenuItem createItem(String name, String iconPath, KeyStroke key) {
        JMenuItem item = new JMenuItem(name);
        item.setIcon(loadIcon(iconPath));
        item.setAccelerator(key);
        item.addActionListener(this);
        // Style cho item trong menu con
        item.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        item.setPreferredSize(new Dimension(220, 35));
        item.setBackground(Color.WHITE);
        registerGlobalHotkey(name, key, item);
        return item;
    }

    
    private void setupPopupMenu(JButton btn, JMenuItem... items) {
        JPopupMenu popup = new JPopupMenu();
        for (JMenuItem item : items) {
            if (item != null) popup.add(item);
        }
        
        btn.addActionListener(e -> {
            // Hiển thị popup ngay dưới nút
            popup.show(btn, 0, btn.getHeight() + 2);
        });
    }

    // --- SỰ KIỆN ACTION PERFORMED (Logic nghiệp vụ) ---
    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        
        // --- Cập nhật ---
//        if (o == mniCapNhatKhachHang) parentFrame.showCapNhatKhachHangPanel();
//        else if (o == mniCapNhatNhanVien) parentFrame.showCapNhatNhanVienPanel();
         if (o == mniCapNhatThuoc) parentFrame.showCapNhatThuocPanel();
//        else if (o == mniCapNhatKhuyenMai) parentFrame.showCapNhatKhuyenMaiPanel();
//        else if (o == mniCapNhatChucVu) parentFrame.showCapNhatChucVuPanel();
//        else if (o == mniCapNhatDonVi) parentFrame.showCapNhatDonViPanel();
//        else if (o == mniXemPhieuNhap) parentFrame.showPhieuNhap();
//        else if (o == mniCapNhatBangGia) parentFrame.showCapNhatBangGiaPanel();
        
        // --- Tìm kiếm ---
        else if (o == mniTimKiemThuoc) parentFrame.showTimKiemThuocPanel();
        else if (o == mniTimKiemKhachHang) parentFrame.showTimKiemKhachHangPanel();
        else if (o == mniTimKiemNhanVien) parentFrame.showTimKiemNhanVienPanel();
        else if (o == mniTimKiemChiTietLoThuoc) parentFrame.showTimKiemChiTietLoThuocPanel();
        else if (o == mniHoaDon) parentFrame.showDanhMucHoaDonPanel();
//        else if (o == mniPhieuDatThuoc) parentFrame.showDanhMucPhieuDatThuocPanel();
//        else if (o == mniPhieuDoiTra) parentFrame.showDanhMucPhieuDoiTraPanel();
        
//        // --- Xử lý ---
//        else if (o == mniLapHoaDon) parentFrame.showLapHoaDonPanel();
//        else if (o == mniLapPhieuDatThuoc) parentFrame.showLapPhieuDatThuocPanel();
//        else if (o == mniLapPhieuTraThuoc) parentFrame.showLapPhieuDoiThuocPanel();
//        else if (o == mniNhapThuoc) parentFrame.showNhapThuocPanel();
//
//        // --- Thống kê ---
//        else if (o == mniDoanhThuTheoThang) parentFrame.showThongKeTheoDoanhThuPanelTheoThang();
//        else if (o == mniDoanhThuTheoNam) parentFrame.showThongKeTheoDoanhThuPanelTheoNam();
//        else if (o == mniNhanVien) parentFrame.showThongKeTheoNhanVienPanel();
//        else if (o == mniKhachHang) parentFrame.showThongKeTheoKhachHangPanel();
//        else if (o == mniHanSuDung) parentFrame.showThongKeTheoHSDPanel();
//        else if (o == mniThuocDuocMuaNhieu) parentFrame.showThongKeTheoThuocPanel();

        // --- Hệ thống ---
        else if (o == mniDangXuat) {
            int choice = JOptionPane.showConfirmDialog(parentFrame, "Bạn có chắc muốn đăng xuất?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if(choice == JOptionPane.YES_OPTION) {
                parentFrame.dangXuatHandle();
            }
        }
//        else if (o == mniTaiKhoan) {
//            parentFrame.showTaiKhoanPanel();
//        }
        else if (o == mniDatLai) {
            int choice = JOptionPane.showConfirmDialog(parentFrame, "Bạn có chắc muốn đặt lại ứng dụng?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if(choice == JOptionPane.YES_OPTION) {
                parentFrame.resetApplication();
                JOptionPane.showMessageDialog(parentFrame, "Đặt lại ứng dụng thành công!!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
        }
        else if (o == mniThoat) {
            int choice = JOptionPane.showConfirmDialog(parentFrame, "Bạn có chắc muốn tắt ứng dụng?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if(choice == JOptionPane.YES_OPTION) System.exit(0);
        }
        else if (o == mniHoTro) {
            try {
//                URL fileURL = App.class.getResource("/resources/pdf/HuongDanSuDungPhanMem.pdf");
//                if(fileURL != null) {
//                    File file = new File(fileURL.toURI());
//                    if(file.exists() && Desktop.isDesktopSupported()) {
//                        Desktop.getDesktop().open(file);
//                    } else {
//                        JOptionPane.showMessageDialog(parentFrame, "File PDF không tồn tại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
//                    }
//                } else {
//                    JOptionPane.showMessageDialog(parentFrame, "File PDF không tồn tại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
//                }
            } catch (Exception e1) {
                JOptionPane.showMessageDialog(this, "Gặp lỗi khi mở file PDF: " + e1.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    

    private ImageIcon loadIcon(String path) {
        try {
            URL iconURL = getClass().getResource(path);
            if (iconURL != null) {
                return new ImageIcon(iconURL);
            } else {
                iconURL = getClass().getClassLoader().getResource(path.substring(1)); // bỏ dấu / đầu
                if (iconURL != null) {
                    return new ImageIcon(iconURL);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * Đăng ký phím tắt hoạt động toàn cục ( khi popup menu chưa mở)
     */
    private void registerGlobalHotkey(String name, KeyStroke keyStroke, JMenuItem item) {
        if (keyStroke == null) return;

        // get input map của panel khi frame này dc fô cus
        InputMap inputMap = this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = this.getActionMap();

        // gán phím vào keymap
        inputMap.put(keyStroke, name);

        // gán vào action map
        actionMap.put(name, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                item.doClick(); // giả lập click
            }
        });
    }
}
