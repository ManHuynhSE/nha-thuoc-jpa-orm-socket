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
import java.util.Comparator;
import java.util.List;

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
import com.pillchill.migration.dto.DoanhThuTheoThangDTO;
import com.pillchill.migration.dto.ThongKeNhanVienDTO;
import com.pillchill.migration.network.client.ThongKeNhanVienClientController;

public class ThongKeTheoNhanVienPanel extends JPanel implements ActionListener {
    private final ThongKeNhanVienClientController thongKeNhanVienClientController;

    private JComboBox<Integer> cboNam;
    private JComboBox<String> cboNhanVien;
    private CategoryChart chart;
    private XChartPanel<CategoryChart> chartPanel;
    private JPanel pnlStats;
    private JButton btnXemChiTiet;

    private int namHienTai = 2025;
    private String maNVHienTai = "ALL";
    private final DecimalFormat df = new DecimalFormat("#,###.##' VNĐ'");
    private final DecimalFormat dfPercent = new DecimalFormat("#,###.##'%' ");

    private List<ThongKeNhanVienDTO> dsThongKe = new ArrayList<>();
    private List<DoanhThuTheoThangDTO> dsDoanhThuTheoThang = new ArrayList<>();
    private boolean isInitializing = true;
    private XemChiTietDoanhThuTheoNhanVien xemChiTietDoanhThuTheoNhanVien;

    public ThongKeTheoNhanVienPanel() {
        this(null);
    }

    public ThongKeTheoNhanVienPanel(ThongKeNhanVienClientController thongKeNhanVienClientController) {
        this.thongKeNhanVienClientController = thongKeNhanVienClientController;
        initData();
        initComponents();
        initNamAsync();
    }

    private void initData() {
        dsThongKe = new ArrayList<>();
        dsDoanhThuTheoThang = new ArrayList<>();
        namHienTai = 2025;
        maNVHienTai = "ALL";
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

        JLabel lblTitle = new JLabel("Thống Kê Doanh Thu Nhân Viên");
        lblTitle.putClientProperty(FlatClientProperties.STYLE, "font:bold +10");

        JPanel pnlControls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlControls.setBackground(Color.WHITE);

        JLabel lblChonNam = new JLabel("Năm:");
        JLabel lblChonNhanVien = new JLabel("Nhân viên:");

        cboNam = new JComboBox<>();
        cboNam.setPreferredSize(new Dimension(100, 30));
        cboNam.addActionListener(this);

        cboNhanVien = new JComboBox<>();
        cboNhanVien.setPreferredSize(new Dimension(220, 30));
        cboNhanVien.addItem("Tất cả nhân viên");
        cboNhanVien.addActionListener(this);

        pnlControls.add(lblChonNam);
        pnlControls.add(cboNam);
        pnlControls.add(lblChonNhanVien);
        pnlControls.add(cboNhanVien);

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

        JLabel lblSummaryTitle = new JLabel(maNVHienTai.equals("ALL") ? "Tổng Quan Năm " + namHienTai : "Chi Tiết Nhân Viên");
        lblSummaryTitle.putClientProperty(FlatClientProperties.STYLE, "font:bold +2");

        JPanel pnlTitle = new JPanel(new BorderLayout());
        pnlTitle.setBackground(new Color(248, 250, 252));
        pnlTitle.add(lblSummaryTitle, BorderLayout.WEST);
        pnlTitle.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        panel.add(pnlTitle);
        panel.add(Box.createVerticalStrut(15));

        JPanel pnlDetailButton = new JPanel();
        btnXemChiTiet = new JButton("Xem chi tiết");
        btnXemChiTiet.addActionListener(this);
        pnlDetailButton.setBackground(new Color(248, 250, 252));
        pnlDetailButton.add(btnXemChiTiet);

        if (maNVHienTai.equals("ALL")) {
            double tongDoanhThu = dsThongKe.stream().mapToDouble(ThongKeNhanVienDTO::getDoanhThu).sum();
            int tongDonHang = dsThongKe.stream().mapToInt(ThongKeNhanVienDTO::getSoLuongDonHang).sum();
            int tongKhachHang = dsThongKe.stream().mapToInt(ThongKeNhanVienDTO::getSoLuongKhachHang).sum();
            double giaTriTrungBinh = dsThongKe.isEmpty() ? 0 : tongDoanhThu / dsThongKe.size();

            panel.add(createStatCard("Tổng doanh thu", df.format(tongDoanhThu), new Color(13, 148, 136)));
            panel.add(Box.createVerticalStrut(10));
            panel.add(createStatCard("Tổng số nhân viên", String.valueOf(dsThongKe.size()), new Color(234, 88, 12)));
            panel.add(Box.createVerticalStrut(10));
            panel.add(createStatCard("Tổng số đơn hàng", String.valueOf(tongDonHang), new Color(79, 70, 229)));
            panel.add(Box.createVerticalStrut(10));
            panel.add(createStatCard("Tổng số khách hàng", String.valueOf(tongKhachHang), new Color(219, 39, 119)));
            panel.add(Box.createVerticalStrut(10));
            panel.add(createStatCard("TB doanh thu/NV", df.format(giaTriTrungBinh), new Color(8, 145, 178)));
        } else {
            ThongKeNhanVienDTO nv = dsThongKe.stream()
                    .filter(tk -> tk.getMaNV().equals(maNVHienTai))
                    .findFirst()
                    .orElse(null);

            if (nv != null) {
                int thuHang = dsThongKe.indexOf(nv) + 1;

                panel.add(createStatCard("Nhân viên", nv.getTenNV(), new Color(13, 148, 136)));
                panel.add(Box.createVerticalStrut(10));
                panel.add(createStatCard("Doanh thu", df.format(nv.getDoanhThu()), new Color(234, 88, 12)));
                panel.add(Box.createVerticalStrut(10));
                panel.add(createStatCard("Số đơn hàng", String.valueOf(nv.getSoLuongDonHang()), new Color(79, 70, 229)));
                panel.add(Box.createVerticalStrut(10));
                panel.add(createStatCard("TB đơn hàng", df.format(nv.getGiaTriTrungBinhDonHang()), new Color(219, 39, 119)));
                panel.add(Box.createVerticalStrut(10));
                panel.add(createStatCard("Tỷ lệ đóng góp", dfPercent.format(nv.getTyLeDongGop()), new Color(8, 145, 178)));
                panel.add(Box.createVerticalStrut(10));
                panel.add(createStatCard("Thứ hạng", "#" + thuHang + " / " + dsThongKe.size(), new Color(100, 116, 139)));
                panel.add(Box.createVerticalStrut(10));
                panel.add(pnlDetailButton);
            }
        }

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
                .title("Biểu đồ thống kê")
                .xAxisTitle("Dữ liệu")
                .yAxisTitle("Doanh thu (triệu)")
                .build();

        Styler styler = chart.getStyler();
        styler.setChartBackgroundColor(Color.WHITE);
        styler.setPlotBackgroundColor(Color.WHITE);
        styler.setLegendBackgroundColor(Color.WHITE);
        styler.setLegendBorderColor(Color.WHITE);
        styler.setChartTitleFont(new Font("Segoe UI", Font.BOLD, 18));
        styler.setSeriesColors(new Color[]{new Color(59, 130, 246)});
        styler.setLegendVisible(false);

        // Không gọi updateChartSeries() ở đây - chờ data ready
        return chart;
    }

    private void updateChartSeries(CategoryChart chart) {
        if (chart == null) return;
        
        chart.getSeriesMap().clear();

        if (maNVHienTai.equals("ALL")) {
            // Guard: nếu dsThongKe rỗng, thêm placeholder
            if (dsThongKe == null || dsThongKe.isEmpty()) {
                ArrayList<String> placeholder = new ArrayList<>();
                ArrayList<Double> placeholderData = new ArrayList<>();
                placeholder.add("Chưa có dữ liệu");
                placeholderData.add(0.0);
                chart.addSeries("Doanh thu " + namHienTai, placeholder, placeholderData);
                return;
            }

            List<ThongKeNhanVienDTO> topNV = dsThongKe.stream()
                    .sorted(Comparator.comparingDouble(ThongKeNhanVienDTO::getDoanhThu).reversed())
                    .limit(5)
                    .toList();

            ArrayList<String> tenNV = new ArrayList<>();
            ArrayList<Double> doanhThu = new ArrayList<>();
            for (ThongKeNhanVienDTO tk : topNV) {
                tenNV.add(tk.getTenNV());
                doanhThu.add(tk.getDoanhThu() / 1000000.0);
            }

            if (!tenNV.isEmpty()) {
                chart.addSeries("Doanh thu " + namHienTai, tenNV, doanhThu);
            } else {
                ArrayList<String> placeholder = new ArrayList<>();
                ArrayList<Double> placeholderData = new ArrayList<>();
                placeholder.add("Chưa có dữ liệu");
                placeholderData.add(0.0);
                chart.addSeries("Doanh thu " + namHienTai, placeholder, placeholderData);
            }
        } else {
            // Guard: nếu dsDoanhThuTheoThang rỗng, thêm placeholder
            if (dsDoanhThuTheoThang == null || dsDoanhThuTheoThang.isEmpty()) {
                ArrayList<String> placeholder = new ArrayList<>();
                ArrayList<Double> placeholderData = new ArrayList<>();
                placeholder.add("Chưa có dữ liệu");
                placeholderData.add(0.0);
                String tenNV = dsThongKe.stream()
                        .filter(tk -> tk.getMaNV().equals(maNVHienTai))
                        .findFirst()
                        .map(ThongKeNhanVienDTO::getTenNV)
                        .orElse(maNVHienTai);
                chart.addSeries(tenNV, placeholder, placeholderData);
                return;
            }

            ArrayList<String> thang = new ArrayList<>();
            ArrayList<Double> doanhThu = new ArrayList<>();
            for (DoanhThuTheoThangDTO dt : dsDoanhThuTheoThang) {
                thang.add(dt.getTenThang());
                doanhThu.add(dt.getDoanhThu() / 1000000.0);
            }

            String tenNV = dsThongKe.stream()
                    .filter(tk -> tk.getMaNV().equals(maNVHienTai))
                    .findFirst()
                    .map(ThongKeNhanVienDTO::getTenNV)
                    .orElse(maNVHienTai);

            if (!thang.isEmpty()) {
                chart.addSeries(tenNV, thang, doanhThu);
            } else {
                ArrayList<String> placeholder = new ArrayList<>();
                ArrayList<Double> placeholderData = new ArrayList<>();
                placeholder.add("Chưa có dữ liệu");
                placeholderData.add(0.0);
                chart.addSeries(tenNV, placeholder, placeholderData);
            }
        }
    }

    private String getChartTitle() {
        if (maNVHienTai.equals("ALL")) {
            return "Top 5 Nhân Viên Doanh Thu Cao Nhất Năm " + namHienTai;
        }
        String tenNV = dsThongKe.stream()
                .filter(tk -> tk.getMaNV().equals(maNVHienTai))
                .findFirst()
                .map(ThongKeNhanVienDTO::getTenNV)
                .orElse(maNVHienTai);
        return "Doanh Thu Của " + tenNV + " Năm " + namHienTai;
    }

    private void refreshStatsPanel() {
        JPanel parent = (JPanel) pnlStats.getParent();
        if (parent != null) {
            parent.remove(pnlStats);
            pnlStats = createStatsPanel();
            parent.add(pnlStats, BorderLayout.WEST);
            parent.revalidate();
            parent.repaint();
        }
    }

    private void renderCurrentView() {
        if (chart == null || chartPanel == null) return;
        
        updateChartSeries(chart);
        chart.setTitle(getChartTitle());
        chart.setXAxisTitle(maNVHienTai.equals("ALL") ? "Nhân viên" : "Tháng");
        chartPanel.revalidate();
        chartPanel.repaint();
        refreshStatsPanel();
    }

    private void refreshDashboard() {
        if (thongKeNhanVienClientController == null || dsThongKe == null) {
            return;
        }

        if (maNVHienTai.equals("ALL")) {
            renderCurrentView();
            return;
        }

        loadDoanhThuTheoNhanVienAsync(maNVHienTai, namHienTai);
    }

    private void initNamAsync() {
        if (thongKeNhanVienClientController == null) {
            isInitializing = false;
            // Render placeholder chart
            if (chart != null) {
                updateChartSeries(chart);
                chartPanel.revalidate();
                chartPanel.repaint();
            }
            return;
        }

        isInitializing = true;
        SwingWorker<List<Integer>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Integer> doInBackground() {
                return thongKeNhanVienClientController.getNamCoHoaDon();
            }

            @Override
            protected void done() {
                try {
                    List<Integer> years = get();
                    cboNam.removeAllItems();
                    for (Integer year : years) {
                        cboNam.addItem(year);
                    }

                    if (!years.isEmpty()) {
                        Integer selectedYear = years.contains(namHienTai) ? namHienTai : years.get(0);
                        cboNam.setSelectedItem(selectedYear);
                        namHienTai = selectedYear;
                        loadThongKeTheoNamAsync(selectedYear, maNVHienTai);
                    } else {
                        isInitializing = false;
                        // Render placeholder chart
                        if (chart != null) {
                            updateChartSeries(chart);
                            chartPanel.revalidate();
                            chartPanel.repaint();
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    isInitializing = false;
                    // Render placeholder chart on error
                    if (chart != null) {
                        updateChartSeries(chart);
                        chartPanel.revalidate();
                        chartPanel.repaint();
                    }
                }
            }
        };
        worker.execute();
    }

    private void loadThongKeTheoNamAsync(int nam, String previousMaNV) {
        if (thongKeNhanVienClientController == null) {
            System.out.println("[DEBUG] ThongKeTheoNhanVienPanel - controller is null, skipping load");
            return;
        }

        System.out.println("[DEBUG] ThongKeTheoNhanVienPanel.loadThongKeTheoNamAsync - nam=" + nam + ", previousMaNV=" + previousMaNV);
        isInitializing = true;
        SwingWorker<List<ThongKeNhanVienDTO>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<ThongKeNhanVienDTO> doInBackground() {
                System.out.println("[DEBUG] SwingWorker.doInBackground - calling controller");
                return thongKeNhanVienClientController.getThongKeDoanhThuNhanVien(nam);
            }

            @Override
            protected void done() {
                try {
                    dsThongKe = get();
                    System.out.println("[DEBUG] SwingWorker.done - received " + dsThongKe.size() + " records");
                    dsThongKe.sort(Comparator.comparingDouble(ThongKeNhanVienDTO::getDoanhThu).reversed());

                    cboNhanVien.removeAllItems();
                    cboNhanVien.addItem("Tất cả nhân viên");
                    for (ThongKeNhanVienDTO tk : dsThongKe) {
                        cboNhanVien.addItem(tk.getMaNV() + " - " + tk.getTenNV());
                    }

                    String selectedMaNV = previousMaNV;
                    if (selectedMaNV == null || selectedMaNV.isBlank()) {
                        selectedMaNV = "ALL";
                    }

                    boolean found = false;
                    if (!"ALL".equals(selectedMaNV)) {
                        for (int i = 0; i < cboNhanVien.getItemCount(); i++) {
                            String label = cboNhanVien.getItemAt(i);
                            if (label != null && label.startsWith(selectedMaNV + " - ")) {
                                cboNhanVien.setSelectedIndex(i);
                                maNVHienTai = selectedMaNV;
                                found = true;
                                break;
                            }
                        }
                    }

                    if (!found) {
                        cboNhanVien.setSelectedIndex(0);
                        maNVHienTai = "ALL";
                    }

                    refreshDashboard();
                } catch (Exception ex) {
                    System.out.println("[DEBUG] SwingWorker exception: " + ex.getMessage());
                    ex.printStackTrace();
                } finally {
                    isInitializing = false;
                }
            }
        };
        worker.execute();
    }

    private void loadDoanhThuTheoNhanVienAsync(String maNV, int nam) {
        if (thongKeNhanVienClientController == null) {
            return;
        }

        isInitializing = true;
        SwingWorker<List<DoanhThuTheoThangDTO>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<DoanhThuTheoThangDTO> doInBackground() {
                return thongKeNhanVienClientController.getThongKeDoanhThuNhanVienTheoThang(maNV, nam);
            }

            @Override
            protected void done() {
                try {
                    dsDoanhThuTheoThang = get();
                    renderCurrentView();
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    isInitializing = false;
                }
            }
        };
        worker.execute();
    }

    public void refresh() {
        if (thongKeNhanVienClientController == null) {
            renderCurrentView();
            return;
        }

        if (cboNam != null && cboNam.getSelectedItem() != null) {
            namHienTai = (Integer) cboNam.getSelectedItem();
        }
        loadThongKeTheoNamAsync(namHienTai, maNVHienTai);
    }

    private void openDetailFrame() {
        if (xemChiTietDoanhThuTheoNhanVien != null) {
            xemChiTietDoanhThuTheoNhanVien.dispose();
            xemChiTietDoanhThuTheoNhanVien = null;
        }

        if (thongKeNhanVienClientController == null) {
            xemChiTietDoanhThuTheoNhanVien = new XemChiTietDoanhThuTheoNhanVien(namHienTai, maNVHienTai, thongKeNhanVienClientController);
            return;
        }

        xemChiTietDoanhThuTheoNhanVien = new XemChiTietDoanhThuTheoNhanVien(namHienTai, maNVHienTai, thongKeNhanVienClientController);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isInitializing) {
            return;
        }

        if (e.getSource() == cboNam) {
            Integer selectedYear = (Integer) cboNam.getSelectedItem();
            if (selectedYear != null && selectedYear != namHienTai) {
                namHienTai = selectedYear;
                loadThongKeTheoNamAsync(namHienTai, maNVHienTai);
            }
        } else if (e.getSource() == cboNhanVien) {
            String selected = (String) cboNhanVien.getSelectedItem();
            if (selected != null) {
                if (selected.equals("Tất cả nhân viên")) {
                    maNVHienTai = "ALL";
                } else {
                    maNVHienTai = selected.split(" - ")[0];
                }
                refreshDashboard();
            }
        } else if (e.getSource() == btnXemChiTiet) {
            openDetailFrame();
        }
    }
}
