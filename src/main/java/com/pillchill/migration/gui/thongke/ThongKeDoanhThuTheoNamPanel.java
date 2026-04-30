package com.pillchill.migration.gui.thongke;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.style.Styler;

import com.formdev.flatlaf.FlatClientProperties;
import com.pillchill.migration.dto.HoaDonKemGiaDTO;
import com.pillchill.migration.network.client.DoanhThuClientController;

public class ThongKeDoanhThuTheoNamPanel extends JPanel implements ActionListener {

    private final DoanhThuClientController doanhThuClientController;

    private JComboBox<Integer> cboNam;
    private CategoryChart chart;
    private XChartPanel<CategoryChart> chartPanel;
    private JPanel pnlStats;

    private String[] months;
    private Double[] revenue;
    private int namDuocChon;

    private double tongDoanhThuNam;
    private int soHoaDonNam;
    private int soKhachHangNam;
    private double giaTriTrungBinh;
    private double doanhThuTrungBinhThang;

    private final DecimalFormat df = new DecimalFormat("#,###.##' VND'");

    private JButton btnDetail;
    private XemChiTietDoanhThuTheoThangFrame xemChiTietDoanhThuTheoNamFrame;
    private boolean isInitializing = true;

    public ThongKeDoanhThuTheoNamPanel() {
        this(null);
    }

    public ThongKeDoanhThuTheoNamPanel(DoanhThuClientController doanhThuClientController) {
        this.doanhThuClientController = doanhThuClientController;
        initData();
        initComponents();
        initCombobox();
    }

    private void initData() {
        namDuocChon = 2025;
        initializeEmptyChartData();
        resetStats();
    }

    private void resetStats() {
        tongDoanhThuNam = 0d;
        soHoaDonNam = 0;
        soKhachHangNam = 0;
        giaTriTrungBinh = 0d;
        doanhThuTrungBinhThang = 0d;
    }

    private void initializeEmptyChartData() {
        months = new String[12];
        revenue = new Double[12];
        for (int i = 0; i < 12; i++) {
            months[i] = "Tháng " + (i + 1);
            revenue[i] = 0d;
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

        JLabel lblTitle = new JLabel("Thống Kê Doanh Thu Theo Năm");
        lblTitle.putClientProperty(FlatClientProperties.STYLE, "font:bold +10");

        JPanel pnlControls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlControls.setBackground(Color.WHITE);

        JLabel lblChonNam = new JLabel("Năm:");

        cboNam = new JComboBox<>();
        cboNam.setPreferredSize(new Dimension(100, 30));
        cboNam.addActionListener(this);

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

        JLabel lblSummaryTitle = new JLabel("Tổng Quan Năm " + namDuocChon);
        lblSummaryTitle.putClientProperty(FlatClientProperties.STYLE, "font:bold +2");

        JPanel pnlTitle = new JPanel(new BorderLayout());
        pnlTitle.setBackground(new Color(248, 250, 252));
        pnlTitle.add(lblSummaryTitle, BorderLayout.WEST);
        pnlTitle.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        panel.add(pnlTitle);
        panel.add(Box.createVerticalStrut(15));

        panel.add(createStatCard("Tổng doanh thu", df.format(tongDoanhThuNam), new Color(13, 148, 136)));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createStatCard("Tổng số hóa đơn", String.valueOf(soHoaDonNam), new Color(234, 88, 12)));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createStatCard("Khách hàng mua", String.valueOf(soKhachHangNam), new Color(79, 70, 229)));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createStatCard("Trung bình/Hóa đơn", df.format(giaTriTrungBinh), new Color(219, 39, 119)));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createStatCard("Trung bình/Tháng", df.format(doanhThuTrungBinhThang), new Color(8, 145, 178)));

        JPanel pnlDetailButton = new JPanel();
        btnDetail = new JButton("Xem chi tiết");
        btnDetail.addActionListener(this);
        pnlDetailButton.setBackground(new Color(248, 250, 252));
        pnlDetailButton.add(btnDetail);

        panel.add(Box.createVerticalStrut(10));
        panel.add(pnlDetailButton);
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private JPanel createStatCard(String title, String value, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, accentColor),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
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
                .title("Biểu Đồ Doanh Thu Năm " + namDuocChon)
                .xAxisTitle("Tháng")
                .yAxisTitle("Doanh Thu (Triệu VNĐ)")
                .build();

        Styler styler = chart.getStyler();
        styler.setChartBackgroundColor(Color.WHITE);
        styler.setPlotBackgroundColor(Color.WHITE);
        styler.setLegendBackgroundColor(Color.WHITE);
        styler.setLegendBorderColor(Color.WHITE);
        styler.setChartTitleFont(new Font("Segoe UI", Font.BOLD, 18));
        styler.setSeriesColors(new Color[]{new Color(59, 130, 246)});
        styler.setLegendVisible(false);

        chart.addSeries("Doanh thu", Arrays.asList(months), Arrays.asList(revenue));
        return chart;
    }

    private void loadDashboardDataAsync(int nam) {
        if (doanhThuClientController == null) {
            return;
        }

        isInitializing = true;
        SwingWorker<DashboardData, Void> worker = new SwingWorker<>() {
            @Override
            protected DashboardData doInBackground() {
                List<HoaDonKemGiaDTO> dsHoaDon = doanhThuClientController.getHoaDonTrongNam(nam);
                return buildDashboardData(nam, dsHoaDon);
            }

            @Override
            protected void done() {
                try {
                    DashboardData data = get();
                    applyDashboardData(data);
                    renderChart();
                    refreshStatsPanel();
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    isInitializing = false;
                }
            }
        };
        worker.execute();
    }

    private DashboardData buildDashboardData(int nam, List<HoaDonKemGiaDTO> dsHoaDon) {
        Map<Integer, Double> doanhThuTheoThang = new HashMap<>();
        Set<String> khachHang = new HashSet<>();
        double tongDoanhThu = 0d;

        for (HoaDonKemGiaDTO hoaDon : dsHoaDon) {
            if (hoaDon == null) {
                continue;
            }

            tongDoanhThu += hoaDon.getTongTien();

            if (hoaDon.getNgayBan() != null) {
                int thang = hoaDon.getNgayBan().toLocalDate().getMonthValue();
                doanhThuTheoThang.merge(thang, hoaDon.getTongTien(), Double::sum);
            }

            if (hoaDon.getTenKH() != null && !hoaDon.getTenKH().isBlank()) {
                khachHang.add(hoaDon.getTenKH().trim());
            }
        }

        String[] monthLabels = new String[12];
        Double[] monthRevenue = new Double[12];
        for (int i = 1; i <= 12; i++) {
            monthLabels[i - 1] = "Tháng " + i;
            monthRevenue[i - 1] = doanhThuTheoThang.getOrDefault(i, 0d) / 1000000.0;
        }

        long soThangCoDoanhThu = doanhThuTheoThang.values().stream()
                .filter(value -> value != null && value > 0)
                .count();

        int soHoaDon = dsHoaDon.size();
        double trungBinhHoaDon = soHoaDon > 0 ? tongDoanhThu / soHoaDon : 0d;
        double trungBinhThang = soThangCoDoanhThu > 0 ? tongDoanhThu / soThangCoDoanhThu : 0d;

        return new DashboardData(
                nam,
                monthLabels,
                monthRevenue,
                tongDoanhThu,
                soHoaDon,
                khachHang.size(),
                trungBinhHoaDon,
                trungBinhThang
        );
    }

    private void applyDashboardData(DashboardData data) {
        namDuocChon = data.nam();
        months = data.monthLabels();
        revenue = data.monthRevenue();
        tongDoanhThuNam = data.tongDoanhThuNam();
        soHoaDonNam = data.soHoaDonNam();
        soKhachHangNam = data.soKhachHangNam();
        giaTriTrungBinh = data.giaTriTrungBinh();
        doanhThuTrungBinhThang = data.doanhThuTrungBinhThang();
    }

    private void renderChart() {
        chart.getSeriesMap().clear();
        chart.addSeries("Doanh thu", Arrays.asList(months), Arrays.asList(revenue));
        chart.setTitle("Biểu Đồ Doanh Thu Năm " + namDuocChon);
        chartPanel.repaint();
        chartPanel.revalidate();
    }

    public void refresh() {
        if (cboNam != null && cboNam.getSelectedItem() != null) {
            namDuocChon = (Integer) cboNam.getSelectedItem();
            loadDashboardDataAsync(namDuocChon);
        }
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

    private void initCombobox() {
        if (doanhThuClientController == null) {
            isInitializing = false;
            return;
        }

        isInitializing = true;
        SwingWorker<List<Integer>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Integer> doInBackground() {
                return new ArrayList<>(doanhThuClientController.getNamCoHoaDon());
            }

            @Override
            protected void done() {
                try {
                    List<Integer> listNam = get();
                    listNam.sort(Integer::compareTo);

                    cboNam.removeAllItems();
                    for (Integer nam : listNam) {
                        cboNam.addItem(nam);
                    }

                    if (cboNam.getItemCount() > 0) {
                        Integer selectedYear = listNam.contains(namDuocChon) ? namDuocChon : listNam.get(0);
                        cboNam.setSelectedItem(selectedYear);
                        namDuocChon = selectedYear;
                        loadDashboardDataAsync(selectedYear);
                    } else {
                        isInitializing = false;
                    }
                } catch (Exception ex) {
                    isInitializing = false;
                    ex.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isInitializing) {
            return;
        }

        if (e.getSource() == cboNam) {
            Integer selectedYear = (Integer) cboNam.getSelectedItem();
            if (selectedYear != null && selectedYear != namDuocChon) {
                namDuocChon = selectedYear;
                loadDashboardDataAsync(namDuocChon);
            }
        } else if (e.getSource() == btnDetail) {
            if (xemChiTietDoanhThuTheoNamFrame != null) {
                xemChiTietDoanhThuTheoNamFrame.dispose();
                xemChiTietDoanhThuTheoNamFrame = null;
            }
            xemChiTietDoanhThuTheoNamFrame = new XemChiTietDoanhThuTheoThangFrame(namDuocChon, doanhThuClientController);
        }
    }

    private record DashboardData(
            int nam,
            String[] monthLabels,
            Double[] monthRevenue,
            double tongDoanhThuNam,
            int soHoaDonNam,
            int soKhachHangNam,
            double giaTriTrungBinh,
            double doanhThuTrungBinhThang
    ) {
    }
}