package com.pillchill.migration.gui.thongke;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import com.pillchill.migration.dto.HoaDonKemGiaDTO;
import com.pillchill.migration.network.client.DoanhThuClientController;

public class XemChiTietDoanhThuTheoThangFrame extends JFrame implements MouseListener{
    private final DoanhThuClientController doanhThuClientController;

    private final Color PRIMARY_COLOR = new Color(0, 150, 136);
    private final Color BG_COLOR = new Color(245, 245, 245);

    private JTable tblHoaDon;
    
    private DefaultTableModel dtm;
    private ArrayList<HoaDonKemGiaDTO> dsHoaDon;
    private DecimalFormat df = new DecimalFormat("#,###.##");

    public XemChiTietDoanhThuTheoThangFrame(int thang, int nam) {
        this(thang, nam, null);
    }

    public XemChiTietDoanhThuTheoThangFrame(int thang, int nam, DoanhThuClientController doanhThuClientController) {
        this.doanhThuClientController = doanhThuClientController;
        setTitle("Danh Sách Hóa Đơn");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(BG_COLOR);

        // Tiêu đề frame
        JLabel lblTieuDe = new JLabel("DANH SÁCH HÓA ĐƠN THÁNG " + thang + " NĂM "+ nam, SwingConstants.CENTER);
        lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTieuDe.setForeground(new Color(0, 150, 136));
        lblTieuDe.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        
        
        add(lblTieuDe, BorderLayout.NORTH);

        add(createTablePanel(), BorderLayout.CENTER);
        
        setVisible(true);
        loadDataTheoThang(thang, nam);
    }

    public XemChiTietDoanhThuTheoThangFrame(int nam) {
        this(nam, null);
    }

    public XemChiTietDoanhThuTheoThangFrame(int nam, DoanhThuClientController doanhThuClientController) {
        this.doanhThuClientController = doanhThuClientController;
        setTitle("Danh Sách Hóa Đơn");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(BG_COLOR);

        // Tiêu đề frame
        JLabel lblTieuDe = new JLabel("DANH SÁCH HÓA ĐƠN NĂM "+ nam, SwingConstants.CENTER);
        lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTieuDe.setForeground(new Color(0, 150, 136));
        lblTieuDe.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        add(lblTieuDe, BorderLayout.NORTH);


        add(createTablePanel(), BorderLayout.CENTER);
        
        setVisible(true);
        loadDataTheoNam(nam);
    }

       
    public JScrollPane createTablePanel() {

        String[] cols = {"Mã hóa đơn", "Tên NV", "Tên KH", "Ngày lập", "Ghi chú", "Tổng tiền"};
        
        dtm = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Không cho sửa trực tiếp trên bảng
            }
        };
        tblHoaDon = new JTable(dtm) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(242, 242, 242));
                }
                return c;
            }
        };        
        
        tblHoaDon.setRowHeight(35);
        tblHoaDon.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tblHoaDon.setFillsViewportHeight(true);
        tblHoaDon.setShowGrid(true);
        tblHoaDon.setGridColor(new Color(224, 224, 224));
        tblHoaDon.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblHoaDon.setSelectionBackground(new Color(178, 223, 219));
        tblHoaDon.setSelectionForeground(Color.BLACK);
        
        JTableHeader header = tblHoaDon.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 40));
        header.setReorderingAllowed(false);
        
        DefaultTableCellRenderer centerRenderer = (DefaultTableCellRenderer) header.getDefaultRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        tblHoaDon.addMouseListener(this);
        
        JScrollPane scrollPane = new JScrollPane(tblHoaDon);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
        			BorderFactory.createEmptyBorder(0,20,20,20),
        			BorderFactory.createLineBorder(Color.black)
        		));
        
        return scrollPane;
    }

    private void loadDataTheoThang(int thang, int nam) {
        if (doanhThuClientController == null) {
            return;
        }

        SwingWorker<List<HoaDonKemGiaDTO>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<HoaDonKemGiaDTO> doInBackground() {
                return doanhThuClientController.getHoaDonTrongThang(thang, nam);
            }

            @Override
            protected void done() {
                try {
                    dsHoaDon = new ArrayList<>(get());
                    fillTableData(false);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(
                            XemChiTietDoanhThuTheoThangFrame.this,
                            "Tải danh sách hóa đơn thất bại: " + ex.getMessage(),
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };
        worker.execute();
    }

    private void loadDataTheoNam(int nam) {
        if (doanhThuClientController == null) {
            return;
        }

        SwingWorker<List<HoaDonKemGiaDTO>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<HoaDonKemGiaDTO> doInBackground() {
                return doanhThuClientController.getHoaDonTrongNam(nam);
            }

            @Override
            protected void done() {
                try {
                    dsHoaDon = new ArrayList<>(get());
                    fillTableData(true);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(
                            XemChiTietDoanhThuTheoThangFrame.this,
                            "Tải danh sách hóa đơn thất bại: " + ex.getMessage(),
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };
        worker.execute();
    }

    private void fillTableData(boolean formatTongTien) {
        dtm.setRowCount(0);
        for (HoaDonKemGiaDTO hd : dsHoaDon) {
            Object[] rowData = {
                    hd.getMaHoaDon(),
                    hd.getTenNV(),
                    hd.getTenKH(),
                    hd.getNgayBan(),
                    hd.getGhiChu(),
                    formatTongTien ? df.format(hd.getTongTien()) : hd.getTongTien()
            };
            dtm.addRow(rowData);
        }
    }


	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
}