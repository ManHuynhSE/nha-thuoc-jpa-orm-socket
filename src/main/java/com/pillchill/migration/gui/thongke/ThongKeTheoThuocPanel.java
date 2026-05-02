package com.pillchill.migration.gui.thongke;

import com.formdev.flatlaf.FlatClientProperties;
import com.pillchill.migration.dto.ThongKeThuoc;
import com.pillchill.migration.network.client.HoaDonClientController;
import com.pillchill.migration.network.client.ThuocClientController;
import com.toedter.calendar.JDateChooser;
import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.style.Styler;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class ThongKeTheoThuocPanel extends JPanel implements ActionListener {
    private final ThuocClientController thuocClientController;
    private final HoaDonClientController hoaDonClientController;

    private JComboBox<String> cboLoai;
    private JDateChooser calThoiGian;

    private CategoryChart chart;
    private XChartPanel<CategoryChart> chartPanel;
    private JPanel pnlStats;
    private JPanel pnlCenter;

    private JButton btnXemChiTiet;

    private String loaiThongKe = "Năm";
    private final int topN = 10;
    private final DecimalFormat df = new DecimalFormat("#,###.##' VNĐ'");

    private ArrayList<ThongKeThuoc> dsThongKe = new ArrayList<>();
    private XemChiTietThongKeThuoc xemChiTietThongKeThuoc;

    public ThongKeTheoThuocPanel() {
        this(null, null);
    }

    public ThongKeTheoThuocPanel(ThuocClientController thuocClientController, HoaDonClientController hoaDonClientController) {
        this.thuocClientController = thuocClientController;
        this.hoaDonClientController = hoaDonClientController;
        initData();
        initComponents();
        refresh();
    }

    private void initData() {
        calThoiGian = new JDateChooser();
        calThoiGian.setDateFormatString("yyyy");
        calThoiGian.setDate(new Date());
    }

    private void initComponents() {
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(Color.WHITE);

        add(createHeaderPanel(), BorderLayout.NORTH);

        pnlCenter = new JPanel(new BorderLayout(20, 0));
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

        JLabel lblTitle = new JLabel("Thống Kê Thuốc Bán Chạy");
        lblTitle.putClientProperty(FlatClientProperties.STYLE, "font:bold +10");

        JPanel pnlControls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlControls.setBackground(Color.WHITE);

        JLabel lblLoai = new JLabel("Loại:");
        String[] loaiOptions = {"Năm", "Tháng", "Ngày"};
        cboLoai = new JComboBox<>(loaiOptions);
        cboLoai.setSelectedItem("Năm");
        cboLoai.setPreferredSize(new Dimension(100, 30));
        cboLoai.addActionListener(this);

        JLabel lblThoiGian = new JLabel("Thời gian:");
        calThoiGian.setPreferredSize(new Dimension(150, 30));
        calThoiGian.addPropertyChangeListener("date", evt -> {
            if (evt.getNewValue() != null) {
                updateChart();
            }
        });

        pnlControls.add(lblLoai);
        pnlControls.add(cboLoai);
        pnlControls.add(Box.createHorizontalStrut(10));
        pnlControls.add(lblThoiGian);
        pnlControls.add(calThoiGian);
        pnlControls.add(Box.createHorizontalStrut(10));

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

        JLabel lblSummaryTitle = new JLabel("Tổng Quan");
        lblSummaryTitle.putClientProperty(FlatClientProperties.STYLE, "font:bold +2");

        JPanel pnlTitle = new JPanel(new BorderLayout());
        pnlTitle.setBackground(new Color(248, 250, 252));
        pnlTitle.add(lblSummaryTitle, BorderLayout.WEST);
        pnlTitle.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        panel.add(pnlTitle);
        panel.add(Box.createVerticalStrut(15));

        int tongSoLuong = dsThongKe.stream().mapToInt(ThongKeThuoc::getSoLuongBan).sum();
        double tongDoanhThu = dsThongKe.stream().mapToDouble(ThongKeThuoc::getDoanhThu).sum();
        int soLoaiThuoc = dsThongKe.size();
        int soHoaDon = (!dsThongKe.isEmpty() && dsThongKe.get(0) != null) ? dsThongKe.get(0).getTongSoHoaDon() : 0;

        JPanel pnlDetailButton = new JPanel();
        btnXemChiTiet = new JButton("Xem chi tiết");
        btnXemChiTiet.addActionListener(this);
        pnlDetailButton.setBackground(new Color(248, 250, 252));
        pnlDetailButton.add(btnXemChiTiet);

        panel.add(createStatCard("Tổng doanh thu", df.format(tongDoanhThu), new Color(13, 148, 136)));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createStatCard("Tổng số lượng bán", String.valueOf(tongSoLuong), new Color(234, 88, 12)));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createStatCard("Số loại thuốc", String.valueOf(soLoaiThuoc), new Color(79, 70, 229)));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createStatCard("Số hóa đơn", String.valueOf(soHoaDon), new Color(219, 39, 119)));
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
        CategoryChart categoryChart = new CategoryChartBuilder()
                .width(800)
                .height(600)
                .title(getChartTitle())
                .xAxisTitle("Thuốc")
                .yAxisTitle("Số lượng bán")
                .build();

        Styler styler = categoryChart.getStyler();
        styler.setChartBackgroundColor(Color.WHITE);
        styler.setPlotBackgroundColor(Color.WHITE);
        styler.setLegendBackgroundColor(Color.WHITE);
        styler.setLegendBorderColor(Color.WHITE);
        styler.setChartTitleFont(new Font("Segoe UI", Font.BOLD, 18));
        styler.setSeriesColors(new Color[]{new Color(59, 130, 246)});
        styler.setLegendVisible(false);

        addChartSeries(categoryChart, dsThongKe);
        return categoryChart;
    }

    private void addChartSeries(CategoryChart categoryChart, ArrayList<ThongKeThuoc> data) {
        if (data != null && !data.isEmpty()) {
            ArrayList<String> tenThuoc = new ArrayList<>();
            ArrayList<Integer> soLuongBan = new ArrayList<>();
            for (ThongKeThuoc tk : data) {
                tenThuoc.add(tk.getTenThuoc());
                soLuongBan.add(tk.getSoLuongBan());
            }
            categoryChart.addSeries("Số lượng bán", tenThuoc, soLuongBan);
            return;
        }

        categoryChart.addSeries("Không có dữ liệu", Arrays.asList("Không có dữ liệu"), Arrays.asList(0));
    }

    private String getChartTitle() {
        String title = "Top " + topN + " Thuốc Bán Chạy ";

        if (calThoiGian.getDate() == null) {
            return title;
        }

        SimpleDateFormat sdf;
        switch (loaiThongKe) {
            case "Ngày":
                sdf = new SimpleDateFormat("dd/MM/yyyy");
                return title + "Ngày " + sdf.format(calThoiGian.getDate());
            case "Tháng":
                sdf = new SimpleDateFormat("MM/yyyy");
                return title + "Tháng " + sdf.format(calThoiGian.getDate());
            case "Năm":
            default:
                sdf = new SimpleDateFormat("yyyy");
                return title + "Năm " + sdf.format(calThoiGian.getDate());
        }
    }

    private void loadData() {
        dsThongKe = new ArrayList<>();

        if (thuocClientController == null) {
            return;
        }

        if (calThoiGian == null || calThoiGian.getDate() == null) {
            calThoiGian.setDate(new Date());
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(calThoiGian.getDate());

        try {
            switch (loaiThongKe) {
                case "Ngày":
                    java.sql.Date sqlDate = new java.sql.Date(calThoiGian.getDate().getTime());
                    dsThongKe = new ArrayList<>(thuocClientController.thongKeThuocTheoNgay(sqlDate, topN));
                    break;
                case "Tháng":
                    dsThongKe = new ArrayList<>(
                            thuocClientController.thongKeThuocTheoThang(
                                    cal.get(Calendar.MONTH) + 1,
                                    cal.get(Calendar.YEAR),
                                    topN
                            )
                    );
                    break;
                case "Năm":
                default:
                    dsThongKe = new ArrayList<>(thuocClientController.thongKeThuocTheoNam(cal.get(Calendar.YEAR), topN));
                    break;
            }
        } catch (Exception e) {
            dsThongKe = new ArrayList<>();
        }
    }

    private void updateChart() {
        loadDataAsync();
    }

    private void loadDataAsync() {
        if (thuocClientController == null) {
            return;
        }

        SwingWorker<ArrayList<ThongKeThuoc>, Void> worker = new SwingWorker<ArrayList<ThongKeThuoc>, Void>() {
            @Override
            protected ArrayList<ThongKeThuoc> doInBackground() {
                loadData();
                return dsThongKe;
            }

            @Override
            protected void done() {
                try {
                    dsThongKe = get();
                    chart.getSeriesMap().clear();
                    chart.setTitle(getChartTitle());
                    addChartSeries(chart, dsThongKe);
                    chartPanel.repaint();

                    pnlCenter.remove(pnlStats);
                    pnlStats = createStatsPanel();
                    pnlCenter.add(pnlStats, BorderLayout.WEST);
                    pnlCenter.revalidate();
                    pnlCenter.repaint();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(
                            ThongKeTheoThuocPanel.this,
                            "Tải dữ liệu thống kê thất bại: " + ex.getMessage(),
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };
        worker.execute();
    }

    public void refresh() {
        updateChart();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == cboLoai) {
            loaiThongKe = (String) cboLoai.getSelectedItem();
            switch (loaiThongKe) {
                case "Năm":
                    calThoiGian.setDateFormatString("yyyy");
                    break;
                case "Tháng":
                    calThoiGian.setDateFormatString("MM/yyyy");
                    break;
                case "Ngày":
                    calThoiGian.setDateFormatString("dd/MM/yyyy");
                    break;
                default:
                    break;
            }
            updateChart();
            return;
        }

        if (source == btnXemChiTiet) {
            if (xemChiTietThongKeThuoc != null) {
                xemChiTietThongKeThuoc.dispose();
                xemChiTietThongKeThuoc = null;
            }

            Date selectedDate = calThoiGian.getDate();
            if (selectedDate == null) {
                selectedDate = new Date();
            }

            Calendar cal = Calendar.getInstance();
            cal.setTime(selectedDate);
            int ngay = cal.get(Calendar.DAY_OF_MONTH);
            int thang = cal.get(Calendar.MONTH) + 1;
            int nam = cal.get(Calendar.YEAR);

            switch (loaiThongKe) {
                case "Năm":
                    xemChiTietThongKeThuoc = new XemChiTietThongKeThuoc(nam, thuocClientController, hoaDonClientController);
                    break;
                case "Tháng":
                    xemChiTietThongKeThuoc = new XemChiTietThongKeThuoc(thang, nam, thuocClientController, hoaDonClientController);
                    break;
                case "Ngày":
                default:
                    xemChiTietThongKeThuoc = new XemChiTietThongKeThuoc(ngay, thang, nam, thuocClientController, hoaDonClientController);
                    break;
            }
        }
    }
}
