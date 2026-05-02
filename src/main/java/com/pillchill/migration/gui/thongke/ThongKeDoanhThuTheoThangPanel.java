package com.pillchill.migration.gui.thongke;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

import com.pillchill.migration.dto.DoanhThuHoaDonDTO;
import com.pillchill.migration.network.client.DoanhThuClientController;
import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.style.Styler;

import com.formdev.flatlaf.FlatClientProperties;


public class ThongKeDoanhThuTheoThangPanel extends JPanel implements ActionListener {
    private final DoanhThuClientController doanhThuClientController;

    private JComboBox<Integer> cboNam;
    private JComboBox<Integer> cboThang;

    private JLabel lblTongDoanhThu;
    private CategoryChart chart;
    private XChartPanel<CategoryChart> chartPanel;
    private JPanel pnlStats;

    private Integer[] days;
    private Double[] revenue;
    private int namDuocChon;
    private int thangDuocChon;
    private double tongDoanhThuThang;
    private int soHoaDonThang;
    private int soKhachHangCuaThang;
    private double giaTriTrungBinh;
    private double doanhThuTrungBinhNgay;

    private final DecimalFormat df = new DecimalFormat("#,###.##' VND'");

    
    private JButton btnDetail;
    private XemChiTietDoanhThuTheoThangFrame xemChiTietDoanhThuTheoThangFrame;
    private boolean isInitializing = true;
    private final Map<Integer, ArrayList<Integer>> mapListThangCuaNam = new HashMap<Integer, ArrayList<Integer>>();

    public ThongKeDoanhThuTheoThangPanel() {
        this(null);
    }

    public ThongKeDoanhThuTheoThangPanel(DoanhThuClientController doanhThuClientController) {
        this.doanhThuClientController = doanhThuClientController;
        initData();
        initComponents();
        initCombobox();
    }

    private void initData() {
        thangDuocChon = 9;
        namDuocChon = 2025;
        YearMonth ym = YearMonth.of(namDuocChon, thangDuocChon);
        int daysInMonth = ym.lengthOfMonth();
        days = new Integer[daysInMonth];
        revenue = new Double[daysInMonth];
        for (int i = 0; i < daysInMonth; i++) {
            days[i] = i + 1;
            revenue[i] = 0.0;
        }
    }

    private void initComponents() {
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(Color.WHITE);

        add(createHeaderPanel(), BorderLayout.NORTH);
        
        JPanel pnlCenter = new JPanel(new BorderLayout(20, 0));
        pnlCenter.setBackground(Color.WHITE);
        
        pnlStats = createStatsPanel();
        pnlCenter.add(pnlStats, BorderLayout.WEST);

        chart = createChart();
        chartPanel = new XChartPanel<>(chart);
        chartPanel.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true));
        pnlCenter.add(chartPanel, BorderLayout.CENTER);

        add(pnlCenter, BorderLayout.CENTER);

    }

    private JPanel createHeaderPanel() {
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel("Thống Kê Doanh Thu Theo Tháng");
        lblTitle.putClientProperty(FlatClientProperties.STYLE, "font:bold +10");
        
        JPanel pnlControls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlControls.setBackground(Color.WHITE);

        JLabel lblChonNam = new JLabel("Năm:");
        JLabel lblChonThang = new JLabel("Tháng:");
        
        cboNam = new JComboBox<>();
        cboThang = new JComboBox<>();
        cboNam.setPreferredSize(new Dimension(100, 30));
        cboThang.setPreferredSize(new Dimension(80, 30));
        
        for (int i = 2020; i <= 2025; i++) cboNam.addItem(i);
        for (int i = 1; i <= 12; i++) cboThang.addItem(i);

        cboNam.setSelectedItem(namDuocChon);
        cboThang.setSelectedItem(thangDuocChon);

        cboNam.addActionListener(this);
        cboThang.addActionListener(this);

        pnlControls.add(lblChonThang);
        pnlControls.add(cboThang);
        pnlControls.add(lblChonNam);
        pnlControls.add(cboNam);

        pnlHeader.add(lblTitle, BorderLayout.WEST);
        pnlHeader.add(pnlControls, BorderLayout.EAST);

        return pnlHeader;
    }

    private JPanel createStatsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(248, 250, 252));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.putClientProperty(FlatClientProperties.STYLE, "arc: 15");
        panel.setPreferredSize(new Dimension(320, 0));

        JLabel lblSummaryTitle = new JLabel("Tổng Quan Tháng " + thangDuocChon + "/" + namDuocChon);
        lblSummaryTitle.putClientProperty(FlatClientProperties.STYLE, "font:bold +2");
//        lblSummaryTitle.setAlignmentX(DEBUG_GRAPHICS_OPTION);
        
        // Wrap label in a panel to force left alignment if needed in BoxLayout
        JPanel pnlTitle = new JPanel(new BorderLayout());
        pnlTitle.setBackground(new Color(248, 250, 252));
        pnlTitle.add(lblSummaryTitle, BorderLayout.WEST);
        pnlTitle.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        
        panel.add(pnlTitle);
        panel.add(Box.createVerticalStrut(15));

        panel.add(createStatCard("Tổng doanh thu", df.format(tongDoanhThuThang), new Color(13, 148, 136)));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createStatCard("Tổng số hóa đơn", String.valueOf(soHoaDonThang), new Color(234, 88, 12)));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createStatCard("Khách hàng mua", String.valueOf(soKhachHangCuaThang), new Color(79, 70, 229)));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createStatCard("Trung bình/Hóa đơn", df.format(giaTriTrungBinh), new Color(219, 39, 119)));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createStatCard("Trung bình/Ngày", df.format(doanhThuTrungBinhNgay), new Color(8, 145, 178)));
        
        JPanel pnlDetailButton = new JPanel();
        btnDetail = new JButton("Xem chi tiết");
        btnDetail.addActionListener(this);
        pnlDetailButton.setBackground(new Color(248, 250, 252));
        pnlDetailButton.add(btnDetail);
        
        panel.add(Box.createVerticalStrut(10));
        panel.add(pnlDetailButton);
        
        
        // Push content to top
        panel.add(Box.createVerticalGlue());

        lblTongDoanhThu = new JLabel(); 
        
        return panel;
    }

    private JPanel createStatCard(String title, String value, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 4, 0, 0, accentColor),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        //error
//        card.putClientProperty(FlatClientProperties.STYLE, "arc: 8; dropShadow: true");
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80)); 

        JLabel lblTitle = new JLabel(title);
        lblTitle.setForeground(Color.GRAY);
        
        JLabel lblValue = new JLabel(value);
        lblValue.putClientProperty(FlatClientProperties.STYLE, "font:bold +2");

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(lblValue, BorderLayout.CENTER);
        return card;
    }

    private CategoryChart createChart() {
        CategoryChart chart = new CategoryChartBuilder()
                .width(800)
                .height(600)
                .title("Biểu Đồ Doanh Thu Tháng " + thangDuocChon)
                .xAxisTitle("Ngày")
                .yAxisTitle("Doanh Thu (Triệu VNĐ)")
                .build();
        
        Styler styler = chart.getStyler();
        styler.setChartBackgroundColor(Color.WHITE);
        styler.setPlotBackgroundColor(Color.WHITE);
        styler.setLegendBackgroundColor(Color.WHITE);
        styler.setLegendBorderColor(Color.WHITE);
        
        styler.setChartTitleFont(new Font("Segoe UI", Font.BOLD, 18));
//        styler.setAxisTitleFont(new Font("Segoe UI", Font.PLAIN, 14));
//        styler.setAxisTickLabelsFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        styler.setSeriesColors(new Color[]{new Color(59, 130, 246)});
        
//        styler.setOverlapped(false);
        styler.setLegendVisible(false);
//        styler.setDefaultSeriesRenderStyle(CategorySeriesRenderStyle.Bar);
//        styler.setPlotGridLinesVisible(true);
//        styler.setPlotGridLinesColor(new Color(240, 240, 240));

        chart.addSeries("Doanh thu", Arrays.asList(days), Arrays.asList(revenue));

        return chart;
    }

    private void loadDoanhThuTheoNamData(int thang, int nam, List<DoanhThuHoaDonDTO> dsDT) {
        YearMonth ym = YearMonth.of(nam, thang);
        int daysInMonth = ym.lengthOfMonth();
        days = new Integer[daysInMonth];
        revenue = new Double[daysInMonth];

        for (int i = 0; i < daysInMonth; i++) {
            days[i] = i + 1;
            revenue[i] = 0.0;
        }

        for (DoanhThuHoaDonDTO item : dsDT) {
            int ngay = item.getNgay();
            if (ngay >= 1 && ngay <= daysInMonth) {
                days[ngay - 1] = ngay;
                revenue[ngay - 1] = item.getDoanhThu() / 1000000.0;
            }
        }
    }

    private void updateChart(int thang, int nam) {
        loadDashboardDataAsync(thang, nam);
    }

    private void renderChart(int thang, int nam) {
        chart.getSeriesMap().clear();
        chart.addSeries("Doanh thu", Arrays.asList(days), Arrays.asList(revenue));
        chart.setTitle("Biểu Đồ Doanh Thu Tháng " + thang + "/" + nam);

        chartPanel.repaint();
        chartPanel.revalidate();
    }

    private void loadDashboardDataAsync(int thang, int nam) {
        if (doanhThuClientController == null) {
            return;
        }

        SwingWorker<DashboardData, Void> worker = new SwingWorker<>() {
            @Override
            protected DashboardData doInBackground() {
                List<DoanhThuHoaDonDTO> dsDT = doanhThuClientController.getDoanhThuTheoNgay(thang, nam);
                double tong = doanhThuClientController.getDoanhThuCuaThang(thang, nam);
                int soHoaDon = doanhThuClientController.getSoHoaDonTheoThang(thang, nam);
                int soKhachHang = doanhThuClientController.getSoKhachHangCuaThang(thang, nam);
                double trungBinhNgay = doanhThuClientController.getDoanhThuTrungBinhTheoNgay(thang, nam);
                return new DashboardData(dsDT, tong, soHoaDon, soKhachHang, trungBinhNgay);
            }

            @Override
            protected void done() {
                try {
                    DashboardData data = get();
                    tongDoanhThuThang = data.tongDoanhThuThang();
                    soHoaDonThang = data.soHoaDonThang();
                    soKhachHangCuaThang = data.soKhachHangCuaThang();
                    doanhThuTrungBinhNgay = data.doanhThuTrungBinhNgay();
                    giaTriTrungBinh = soHoaDonThang > 0 ? tongDoanhThuThang / soHoaDonThang : 0;

                    loadDoanhThuTheoNamData(thang, nam, data.doanhThuTheoNgay());
                    renderChart(thang, nam);
                    refreshStatsPanel();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(
                            ThongKeDoanhThuTheoThangPanel.this,
                            "Tải dữ liệu thống kê thất bại: " + ex.getMessage(),
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };
        worker.execute();
    }

    private void updateThangComboBox(int namDuocChon) {
        isInitializing = true;
        ArrayList<Integer> listThang = mapListThangCuaNam.get(namDuocChon);
        cboThang.removeAllItems();
        if (listThang != null) {
            for (int i : listThang) {
                cboThang.addItem(i);
            }
        }
        cboThang.revalidate();
        cboThang.repaint();
        if (cboThang.getItemCount() > 0) {
            cboThang.setSelectedIndex(0);
        }
        isInitializing = false;
    }

    private void initCombobox() {
        if (doanhThuClientController == null) {
            isInitializing = false;
            return;
        }

        isInitializing = true;
        SwingWorker<Map<Integer, ArrayList<Integer>>, Void> worker = new SwingWorker<>() {
            @Override
            protected Map<Integer, ArrayList<Integer>> doInBackground() {
                ArrayList<Integer> listNam = new ArrayList<>(doanhThuClientController.getNamCoHoaDon());
                Map<Integer, ArrayList<Integer>> result = new HashMap<>();
                for (int i : listNam) {
                    result.put(i, new ArrayList<>(doanhThuClientController.getThangCoHoaDonTrongNam(i)));
                }
                return result;
            }

            @Override
            protected void done() {
                try {
                    mapListThangCuaNam.clear();
                    mapListThangCuaNam.putAll(get());

                    cboNam.removeAllItems();
                    for (Integer nam : mapListThangCuaNam.keySet()) {
                        cboNam.addItem(nam);
                    }

                    if (cboNam.getItemCount() > 0) {
                        cboNam.setSelectedIndex(0);
                        namDuocChon = (Integer) cboNam.getSelectedItem();
                        updateThangComboBox(namDuocChon);
                        thangDuocChon = (Integer) cboThang.getSelectedItem();
                        loadDashboardDataAsync(thangDuocChon, namDuocChon);
                    } else {
                        isInitializing = false;
                    }
                } catch (Exception ex) {
                    isInitializing = false;
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(
                            ThongKeDoanhThuTheoThangPanel.this,
                            "Tải dữ liệu năm/tháng thất bại: " + ex.getMessage(),
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };
        worker.execute();
    }

    public void refresh() {
        updateChart(thangDuocChon, namDuocChon);
    }
    
    public void refreshStatsPanel() {
        JPanel parent = (JPanel) pnlStats.getParent();
        if (parent != null) {
            parent.remove(pnlStats);
            pnlStats = createStatsPanel();
            parent.add(pnlStats, BorderLayout.WEST);
            parent.revalidate();
            parent.repaint();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isInitializing) return;
        if (e.getSource() == cboNam) {
            namDuocChon = (Integer) cboNam.getSelectedItem();
            updateThangComboBox(namDuocChon);
            if (cboThang.getSelectedItem() != null) {
                thangDuocChon = (Integer) cboThang.getSelectedItem();
                updateChart(thangDuocChon, namDuocChon);
            }
        } else if (e.getSource() == cboThang) {
            thangDuocChon = (Integer) cboThang.getSelectedItem();
            updateChart(thangDuocChon, namDuocChon);
        }
        else if (e.getSource() == btnDetail) {
            if (xemChiTietDoanhThuTheoThangFrame != null) {

                xemChiTietDoanhThuTheoThangFrame.dispose();
                xemChiTietDoanhThuTheoThangFrame = null;
            }
            xemChiTietDoanhThuTheoThangFrame = new XemChiTietDoanhThuTheoThangFrame(thangDuocChon, namDuocChon, doanhThuClientController);
        }
    }

    private record DashboardData(
            List<DoanhThuHoaDonDTO> doanhThuTheoNgay,
            double tongDoanhThuThang,
            int soHoaDonThang,
            int soKhachHangCuaThang,
            double doanhThuTrungBinhNgay
    ) {
    }
}