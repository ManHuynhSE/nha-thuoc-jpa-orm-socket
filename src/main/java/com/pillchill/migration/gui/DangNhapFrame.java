package com.pillchill.migration.gui;

import com.pillchill.migration.entity.TaiKhoan;
//import com.pillchill.migration.network.*;
import com.pillchill.migration.network.client.AuthClientController;
import com.pillchill.migration.network.client.ClientSessionContext;
import com.pillchill.migration.network.client.NetworkClient;
import com.pillchill.migration.network.communication.CommandType;
import com.pillchill.migration.network.communication.Response;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class DangNhapFrame extends JFrame {
    private final String host;
    private final int port;
    private JTextField txtMaNhanVien;
    private JPasswordField txtMatKhau;
    private JButton btnDangNhap;
    private JLabel lblTieuDe;
    private JLabel lblMaNhanVien;
    private JLabel lblMatKhau;
    private JLabel lblHinhAnh;
	private NetworkClient networkClient;
	
    public DangNhapFrame(String host, int port) {
        this.host = host;
        this.port = port;
        initUI();
    }

    private void initUI() {
        setTitle("Đăng Nhập - Pill & Chill");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);

        JPanel pnlMain = new JPanel(new GridLayout(1, 2));

        JPanel pnlLeft = taoTrangTrai();
        JPanel pnlRight = taoTrangPhai();

        pnlMain.add(pnlLeft);
        pnlMain.add(pnlRight);

        add(pnlMain);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (networkClient != null) {
                    networkClient.close();
                }
            }
        });

        setVisible(true);
    }

    
    private JPanel taoTrangTrai() {
        JPanel pnlLeft = new JPanel(new GridBagLayout());
        pnlLeft.setBackground(new Color(240, 245, 240));
        pnlLeft.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.LIGHT_GRAY));
        
        ImageIcon logo;
        try {
            // Đường dẫn đúng đến file logo
            URL logoUrl = getClass().getResource("/resources/image/logo.png");
            if (logoUrl != null) {
                logo = new ImageIcon(logoUrl);
                Image img = logo.getImage();
                // Anti aliasing khử răng cưa 
                Image scaledImg = img.getScaledInstance(300, 300, Image.SCALE_SMOOTH);
                logo = new ImageIcon(scaledImg);
            } else {
                // Fallback nếu không tìm thấy logo
                logo = new ImageIcon();
            }
        } catch (Exception e) {
            // Fallback nếu có lỗi
            logo = new ImageIcon();
        }
        
        lblHinhAnh = new JLabel(logo, SwingConstants.CENTER);
        lblHinhAnh.setText("NHÀ THUỐC PILL & CHILL"); // Text backup
        lblHinhAnh.setHorizontalTextPosition(SwingConstants.CENTER);
        lblHinhAnh.setVerticalTextPosition(SwingConstants.BOTTOM);
        lblHinhAnh.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblHinhAnh.setForeground(new Color(76, 175, 80));
        lblHinhAnh.setBackground(Color.WHITE);
        lblHinhAnh.setPreferredSize(new Dimension(500, 500));
        
        pnlLeft.add(lblHinhAnh);
        
        return pnlLeft;
    }
    
    private JPanel taoTrangPhai() {
    	// Dung gridbag, all components đc auto center align
        JPanel pnlRight = new JPanel(new GridBagLayout());
        pnlRight.setBackground(new Color(240, 250, 240));
        pnlRight.setBorder(new EmptyBorder(50, 60, 50, 60));
        
        lblTieuDe =  new JLabel("ĐĂNG NHẬP");
        lblTieuDe.setFont(new Font("Roboto", Font.BOLD, 30));
        lblTieuDe.setForeground((new Color(45, 152, 42)));
        
        lblMaNhanVien = new JLabel("Mã nhân viên:");
        lblMaNhanVien.setFont(new Font("Roboto", Font.BOLD, 16));
        txtMaNhanVien = taoTextField();
        
        lblMatKhau = new JLabel("Mật khẩu:");
        lblMatKhau.setFont(new Font("Roboto", Font.BOLD, 16));
        lblMatKhau.setPreferredSize(lblMaNhanVien.getPreferredSize());
        txtMatKhau = taoPasswordField();
        btnDangNhap = taoNutDangNhap();
        
        JPanel pnlMainPhai = new JPanel();
        pnlMainPhai.setBackground(new Color(240,250,240));
        pnlMainPhai.setLayout(new GridLayout(4, 1, 5, 10));
        
        // 1 la center, 0 la left, 2 la right
        JPanel r1 = rowPanel(1);
        r1.add(lblTieuDe);
        pnlMainPhai.add(r1);
        
        JPanel r2 = rowPanel(0);
        r2.add(lblMaNhanVien);
        r2.add(txtMaNhanVien);
        pnlMainPhai.add(r2);
        
        JPanel r3 = rowPanel(0);
        r3.add(lblMatKhau);
        r3.add(txtMatKhau);
        pnlMainPhai.add(r3);
        
        JPanel r4 = rowPanel(1);
        r4.add(btnDangNhap);
        pnlMainPhai.add(r4);
        handleHotKey();
        pnlRight.add(pnlMainPhai, new GridBagConstraints());



        return pnlRight;
    }
    
    private JTextField taoTextField() {
        txtMaNhanVien = new JTextField();
        txtMaNhanVien.setFont(new Font("Arial", Font.PLAIN, 16));
        txtMaNhanVien.setBackground(new Color(240,250,240));
        txtMaNhanVien.setPreferredSize(new Dimension(300, 40));
        txtMaNhanVien.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(0, 0, 0)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        txtMaNhanVien.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				txtMatKhau.requestFocus();
			}
		});
        return txtMaNhanVien;
    }
    
    private JPasswordField taoPasswordField() {
        txtMatKhau = new JPasswordField();
        txtMatKhau.setFont(new Font("Arial", Font.PLAIN, 16));
        txtMatKhau.setBackground(new Color(240,250,240));
        txtMatKhau.setPreferredSize(new Dimension(300, 40));
        txtMatKhau.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(0, 0, 0)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        return txtMatKhau;
    }
    
    private JButton taoNutDangNhap() {
        btnDangNhap = new JButton("ĐĂNG NHẬP");
        btnDangNhap.setFont(new Font("Arial", Font.BOLD, 15));
        btnDangNhap.setPreferredSize(new Dimension(300, 45));
        btnDangNhap.setBackground(new Color(45, 152, 42));

        btnDangNhap.setForeground(Color.WHITE);
        btnDangNhap.setFocusPainted(false);
        btnDangNhap.setBorderPainted(false);
        btnDangNhap.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDangNhap.setOpaque(true);
        btnDangNhap.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                Shape shape = new RoundRectangle2D.Float(0, 0, btnDangNhap.getWidth(), btnDangNhap.getHeight(), 30, 30);
                btnDangNhap.setMixingCutoutShape(shape);
        }});
        btnDangNhap.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnDangNhap.setBackground(new Color(0, 155, 60));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnDangNhap.setBackground(new Color(34, 139, 34));
            }
        });
        btnDangNhap.addActionListener( e-> {
        	validateLogin();
        });
        
        return btnDangNhap;
    }
    
    private JPanel rowPanel(int layout) {
    	JPanel a = new JPanel(new FlowLayout(layout));
    	a.setBackground(new Color(240,250,240));
    	a.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
    	return a;
    }
    
    private void handleHotKey() {
    	JRootPane rootPane = getRootPane();
    	rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
    			.put(KeyStroke.getKeyStroke("ENTER"), "enterPressed");
    	rootPane.getActionMap().put("enterPressed", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				validateLogin();
				
			}
		});
    }
    
	private void validateLogin() {
		String user = txtMaNhanVien.getText().trim();
        String pwd = new String(txtMatKhau.getPassword());

		if (user.isEmpty() || pwd.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu!", "Thông báo", JOptionPane.WARNING_MESSAGE);
			return;
		}
        btnDangNhap.setEnabled(false);

        SwingWorker<Response,Void> worker = new SwingWorker<>() {
            @Override
            protected Response doInBackground() throws Exception {
                if(networkClient==null) {
                    networkClient = new NetworkClient(host,port);
                }
                AuthClientController authClientController = new AuthClientController(networkClient);
                return authClientController.login(user,pwd);
            }
            @Override
            protected void done() {
                btnDangNhap.setEnabled(true);
                try{
                    Response response = get();
                    if(!response.isSuccess()) {
                        JOptionPane.showMessageDialog(DangNhapFrame.this, response.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    String id = response.getData().toString();
                    ClientSessionContext context = new ClientSessionContext(networkClient,id);
                    JOptionPane.showMessageDialog(DangNhapFrame.this, "Đăng nhập thành công! Xin chào "+ id, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                    new MainFrame(context);
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        worker.execute();
	}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DangNhapFrame("DESKTOP-57QN5N0", 9999));


    }
}
