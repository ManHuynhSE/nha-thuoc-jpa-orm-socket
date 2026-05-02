package com.pillchill.migration.gui.thongke;

import com.formdev.flatlaf.FlatLightLaf;
import com.pillchill.migration.dto.ThongKeThuoc;
import com.pillchill.migration.network.client.HoaDonClientController;
import com.pillchill.migration.network.client.ThuocClientController;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.DecimalFormat;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class XemChiTietThongKeThuoc extends JFrame implements MouseListener {
    private final ThuocClientController thuocClientController;
    private final HoaDonClientController hoaDonClientController;

    private final Color PRIMARY_COLOR = new Color(0, 150, 136);
    private final Color BG_COLOR = new Color(245, 245, 245);

    private JTable tblHoaDon;
    private DefaultTableModel dtm;
    private final ArrayList<ThongKeThuoc> dsThongKe = new ArrayList<>();
    private final DecimalFormat df = new DecimalFormat("#,###.##");

    private int ngay;
    private int thang;
    private int nam;

    private CardLayout cardLayout;
    private JPanel mainContainer;

    public XemChiTietThongKeThuoc(int ngay, int thang, int nam, ThuocClientController thuocClientController, HoaDonClientController hoaDonClientController) {
        this.thuocClientController = thuocClientController;
        this.hoaDonClientController = hoaDonClientController;
        this.ngay = ngay;
        this.thang = thang;
        this.nam = nam;
        initFrame("DANH SÁCH THUỐC BÁN CHẠY " + ngay + " THÁNG " + thang + " NĂM " + nam);
    }

    public XemChiTietThongKeThuoc(int thang, int nam, ThuocClientController thuocClientController, HoaDonClientController hoaDonClientController) {
        this.thuocClientController = thuocClientController;
        this.hoaDonClientController = hoaDonClientController;
        this.thang = thang;
        this.nam = nam;
        initFrame("DANH SÁCH THUỐC BÁN CHẠY THÁNG " + thang + " NĂM " + nam);
    }

    public XemChiTietThongKeThuoc(int nam, ThuocClientController thuocClientController, HoaDonClientController hoaDonClientController) {
        this.thuocClientController = thuocClientController;
        this.hoaDonClientController = hoaDonClientController;
        this.nam = nam;
        initFrame("DANH SÁCH THUỐC BÁN CHẠY NĂM " + nam);
    }

    private void initFrame(String tieuDe) {
        FlatLightLaf.setup();

        setTitle("Danh Sách Thuốc");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(BG_COLOR);

        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);

        JPanel danhSachPanel = taoDanhSachPanel(tieuDe);
        mainContainer.add(danhSachPanel, "DanhSach");

        setLayout(new BorderLayout());
        add(mainContainer, BorderLayout.CENTER);

        cardLayout.show(mainContainer, "DanhSach");
        loadThongKeData();
        setVisible(true);
    }

    private JPanel taoDanhSachPanel(String tieuDe) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BG_COLOR);

        JLabel lblTieuDe = new JLabel(tieuDe, SwingConstants.CENTER);
        lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTieuDe.setForeground(PRIMARY_COLOR);
        lblTieuDe.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));

        panel.add(lblTieuDe, BorderLayout.NORTH);
        panel.add(createTablePanel(), BorderLayout.CENTER);

        return panel;
    }

    public JScrollPane createTablePanel() {
        String[] cols = {"Mã thuốc", "Tên thuốc", "Số lượng bán", "Doanh thu"};

        dtm = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
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
        tblHoaDon.addMouseListener(this);

        JTableHeader header = tblHoaDon.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 40));
        header.setReorderingAllowed(false);

        DefaultTableCellRenderer centerRenderer = (DefaultTableCellRenderer) header.getDefaultRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        DefaultTableCellRenderer cellCenter = new DefaultTableCellRenderer();
        cellCenter.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < 4; i++) {
            tblHoaDon.getColumnModel().getColumn(i).setCellRenderer(cellCenter);
        }

        JScrollPane scrollPane = new JScrollPane(tblHoaDon);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(0, 20, 20, 20),
                BorderFactory.createLineBorder(new Color(220, 220, 220))
        ));
        scrollPane.getViewport().setBackground(Color.WHITE);

        return scrollPane;
    }

    private void loadThongKeData() {
        dsThongKe.clear();
        dtm.setRowCount(0);

        if (thuocClientController == null) {
            return;
        }

        try {
            List<ThongKeThuoc> result;
            if (ngay > 0) {
                String dateValue = String.format("%04d-%02d-%02d", nam, thang, ngay);
                result = thuocClientController.thongKeThuocTheoNgay(Date.valueOf(dateValue), 0);
            } else if (thang > 0) {
                result = thuocClientController.thongKeThuocTheoThang(thang, nam, 0);
            } else {
                result = thuocClientController.thongKeThuocTheoNam(nam, 0);
            }

            dsThongKe.addAll(result);
            for (ThongKeThuoc item : dsThongKe) {
                dtm.addRow(new Object[]{
                        item.getMaThuoc(),
                        item.getTenThuoc(),
                        item.getSoLuongBan(),
                        df.format(item.getDoanhThu())
                });
            }
        } catch (Exception ignored) {
            // Keep table empty when API fails in detail dialog.
        }
    }

    public void quayLaiDanhSach() {
        cardLayout.show(mainContainer, "DanhSach");
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getSource() != tblHoaDon || e.getClickCount() != 2) {
            return;
        }

        int row = tblHoaDon.getSelectedRow();
        if (row < 0) {
            return;
        }

        String maThuoc = tblHoaDon.getValueAt(row, 0).toString();
        JPanel chiTietPanel = new DanhMucHoaDon(
                this,
                maThuoc,
                thang,
                ngay,
                nam,
                thuocClientController,
                hoaDonClientController
        );

        if (mainContainer.getComponentCount() > 1) {
            mainContainer.remove(1);
        }

        mainContainer.add(chiTietPanel, "ChiTiet");
        cardLayout.show(mainContainer, "ChiTiet");
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }
}
