package com.pillchill.migration.gui.thongke;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.pillchill.migration.entity.ChiTietHoaDon;
import com.pillchill.migration.entity.HoaDon;
import com.pillchill.migration.entity.KhachHang;
import com.pillchill.migration.entity.NhanVien;
import com.pillchill.migration.entity.Thuoc;
import com.pillchill.migration.network.client.HoaDonClientController;
import com.pillchill.migration.network.client.ThuocClientController;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DanhMucHoaDon extends JPanel implements ActionListener, MouseListener {

    private final Color PRIMARY_COLOR = new Color(0, 150, 136);
    private final Color BG_COLOR = new Color(245, 245, 245);

    private final ThuocClientController thuocClientController;
    private final HoaDonClientController hoaDonClientController;

    private JButton btnXuatHoaDon;

    private DefaultTableModel dtmHoaDon;
    private JTable tblHoaDon;

    private final ArrayList<HoaDon> dsHoaDon = new ArrayList<>();
    private JLabel lblHoaDon;

    private DefaultTableModel dtmChiTietHoaDon;
    private final ArrayList<ChiTietHoaDon> dsChiTietHoaDon = new ArrayList<>();
    private JTable tblChiTietHoaDon;

    private JButton searchButton;
    private JComboBox<String> cboTimKiem;

    private JComboBox<String> cboThang;

    private JComboBox<String> cboNam;

    private JTextField searchField;

    private JRadioButton radTimKiemThangNam;
    private XemChiTietThongKeThuoc parent;

    private String maThuocFilter;
    private int thangFilter;
    private int ngayFilter;
    private int namFilter;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public DanhMucHoaDon() {
        this(null, null, 0, 0, 0, null, null);
    }

    public DanhMucHoaDon(XemChiTietThongKeThuoc parent,
                         String maThuoc,
                         int thang,
                         int ngay,
                         int nam,
                         ThuocClientController thuocClientController,
                         HoaDonClientController hoaDonClientController) {
        this.parent = parent;
        this.thuocClientController = thuocClientController;
        this.hoaDonClientController = hoaDonClientController;
        this.maThuocFilter = maThuoc;
        this.thangFilter = thang;
        this.ngayFilter = ngay;
        this.namFilter = nam;

        String[] colsHoaDon = {"Mã hóa đơn", "Tên NV", "Tên KH", "Ngày lập", "Ghi chú"};
        dtmHoaDon = new DefaultTableModel(colsHoaDon, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        String[] colsChiTietHoaDon = {"Tên thuốc", "Mã thuốc", "Mã lô", "Số lượng", "Đơn giá"};
        dtmChiTietHoaDon = new DefaultTableModel(colsChiTietHoaDon, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        setLayout(new GridLayout(2, 1, 10, 10));
        setBackground(BG_COLOR);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(createTopPanelForDetail(maThuoc));
        add(createBotPanel());

        loadHoaDonData(maThuocFilter, thangFilter, ngayFilter, namFilter);
    }

    public JPanel createBotPanel() {
        JPanel pnlBot = new JPanel(new BorderLayout());
        pnlBot.setBackground(BG_COLOR);

        tblChiTietHoaDon = new JTable(dtmChiTietHoaDon) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(242, 242, 242));
                }
                return c;
            }
        };

        tblChiTietHoaDon.setRowHeight(35);
        tblChiTietHoaDon.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tblChiTietHoaDon.setFillsViewportHeight(true);
        tblChiTietHoaDon.setShowGrid(true);
        tblChiTietHoaDon.setGridColor(new Color(224, 224, 224));
        tblChiTietHoaDon.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblChiTietHoaDon.setSelectionBackground(new Color(178, 223, 219));
        tblChiTietHoaDon.setSelectionForeground(Color.BLACK);

        JTableHeader header = tblChiTietHoaDon.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 40));
        header.setReorderingAllowed(false);

        DefaultTableCellRenderer centerRenderer = (DefaultTableCellRenderer) header.getDefaultRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        JScrollPane scrollPane = new JScrollPane(tblChiTietHoaDon);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        scrollPane.getViewport().setBackground(Color.WHITE);

        lblHoaDon = new JLabel("CHI TIẾT HÓA ĐƠN", SwingConstants.CENTER);
        lblHoaDon.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblHoaDon.setForeground(PRIMARY_COLOR);
        lblHoaDon.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        pnlBot.add(lblHoaDon, BorderLayout.NORTH);
        pnlBot.add(scrollPane, BorderLayout.CENTER);

        JPanel pnlButton = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlButton.setBackground(BG_COLOR);
        btnXuatHoaDon = new JButton("Xuất hóa đơn");
        btnXuatHoaDon.setBackground(new Color(46, 204, 113));
        btnXuatHoaDon.setForeground(Color.WHITE);
        btnXuatHoaDon.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnXuatHoaDon.setPreferredSize(new Dimension(150, 40));
        btnXuatHoaDon.setFocusPainted(false);
        btnXuatHoaDon.setBorderPainted(false);
        btnXuatHoaDon.setCursor(new Cursor(Cursor.HAND_CURSOR));

        pnlButton.add(btnXuatHoaDon);
        pnlButton.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        pnlBot.add(pnlButton, BorderLayout.SOUTH);

        btnXuatHoaDon.addActionListener(this);

        return pnlBot;
    }

    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        searchPanel.setBackground(BG_COLOR);

        searchField = new JTextField(25);
        searchField.setText("Nhập từ khóa...");
        searchField.setForeground(Color.GRAY);
        searchField.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        searchField.setBackground(Color.WHITE);
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        searchField.setPreferredSize(new Dimension(250, 35));

        searchField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if ("Nhập từ khóa...".equals(searchField.getText())) {
                    searchField.setText("");
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("Nhập từ khóa...");
                }
            }
        });

        searchButton = new JButton("Tìm");
        searchButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        searchButton.setPreferredSize(new Dimension(80, 35));
        searchButton.setBackground(PRIMARY_COLOR);
        searchButton.setForeground(Color.WHITE);
        searchButton.setBorderPainted(false);
        searchButton.setFocusPainted(false);
        searchButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        String[] kieuTimKiem = {"Theo mã hóa đơn", "Theo tên nhân viên", "Theo tên khách hàng"};
        cboTimKiem = new JComboBox<>(kieuTimKiem);
        cboTimKiem.setSelectedIndex(0);
        cboTimKiem.setPreferredSize(new Dimension(180, 35));
        cboTimKiem.setBackground(Color.WHITE);
        cboTimKiem.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JLabel lblThang = new JLabel("Tháng: ");
        lblThang.setFont(new Font("Segoe UI", Font.BOLD, 14));
        String[] thang = "1 2 3 4 5 6 7 8 9 10 11 12".split(" ");
        cboThang = new JComboBox<>(thang);
        LocalDate today = LocalDate.now();
        cboThang.setSelectedIndex(today.getMonthValue() - 1);
        cboThang.setPreferredSize(new Dimension(60, 35));
        cboThang.setBackground(Color.WHITE);
        cboThang.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JLabel lblNam = new JLabel("Năm: ");
        lblNam.setFont(new Font("Segoe UI", Font.BOLD, 14));
        List<Integer> danhSachNam = getDanhSachNamCoHoaDon();
        String[] namValues;
        if (danhSachNam.isEmpty()) {
            namValues = new String[]{String.valueOf(today.getYear())};
        } else {
            namValues = danhSachNam.stream().map(String::valueOf).toArray(String[]::new);
        }
        cboNam = new JComboBox<>(namValues);
        cboNam.setPreferredSize(new Dimension(80, 35));
        cboNam.setBackground(Color.WHITE);
        cboNam.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        radTimKiemThangNam = new JRadioButton("Lọc theo Tháng/Năm");
        radTimKiemThangNam.setFont(new Font("Segoe UI", Font.BOLD, 14));
        radTimKiemThangNam.setBackground(BG_COLOR);

        searchPanel.add(Box.createHorizontalStrut(15));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(cboTimKiem);
        searchPanel.add(radTimKiemThangNam);
        searchPanel.add(lblThang);
        searchPanel.add(cboThang);
        searchPanel.add(lblNam);
        searchPanel.add(cboNam);

        radTimKiemThangNam.addActionListener(this);
        searchButton.addActionListener(this);
        cboThang.addActionListener(this);
        cboNam.addActionListener(this);

        return searchPanel;
    }

    public JPanel createTopPanelForDetail(String maThuoc) {
        JPanel pnlTop = new JPanel(new BorderLayout());
        pnlTop.setBackground(BG_COLOR);

        tblHoaDon = new JTable(dtmHoaDon) {
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

        JScrollPane scrollPane = new JScrollPane(tblHoaDon);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        scrollPane.getViewport().setBackground(Color.WHITE);

        JButton btnQuayLai = new JButton("← Quay lại");
        btnQuayLai.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnQuayLai.setForeground(Color.BLACK);
        btnQuayLai.setPreferredSize(new Dimension(120, 40));
        btnQuayLai.setBorderPainted(false);
        btnQuayLai.setFocusPainted(false);
        btnQuayLai.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnQuayLai.addActionListener(e -> {
            if (parent != null) {
                parent.quayLaiDanhSach();
            }
        });

        JPanel pnlLeft = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlLeft.setBackground(BG_COLOR);
        pnlLeft.setPreferredSize(new Dimension(120, 40));
        pnlLeft.add(btnQuayLai);

        JPanel pnlRight = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlRight.setBackground(BG_COLOR);
        pnlRight.setPreferredSize(new Dimension(120, 40));

        String tenThuoc = resolveTenThuoc(maThuoc);
        lblHoaDon = new JLabel("HÓA ĐƠN CỦA THUỐC " + tenThuoc, SwingConstants.CENTER);
        lblHoaDon.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblHoaDon.setForeground(PRIMARY_COLOR);

        JPanel pnlTieuDe = new JPanel(new BorderLayout());
        pnlTieuDe.setBackground(BG_COLOR);
        pnlTieuDe.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        pnlTieuDe.add(pnlLeft, BorderLayout.WEST);
        pnlTieuDe.add(lblHoaDon, BorderLayout.CENTER);
        pnlTieuDe.add(pnlRight, BorderLayout.EAST);

        JPanel topWithSearch = new JPanel(new BorderLayout());
        topWithSearch.setBackground(BG_COLOR);
        topWithSearch.add(pnlTieuDe, BorderLayout.NORTH);
        topWithSearch.add(createSearchPanel(), BorderLayout.SOUTH);

        pnlTop.add(topWithSearch, BorderLayout.NORTH);
        pnlTop.add(scrollPane, BorderLayout.CENTER);

        tblHoaDon.addMouseListener(this);

        return pnlTop;
    }

    private List<Integer> getDanhSachNamCoHoaDon() {
        if (hoaDonClientController == null) {
            return new ArrayList<>();
        }

        try {
            return new ArrayList<>(hoaDonClientController.getNamCoHoaDon());
        } catch (Exception ignored) {
            return new ArrayList<>();
        }
    }

    private String resolveTenThuoc(String maThuoc) {
        if (maThuoc == null || maThuoc.isBlank() || thuocClientController == null) {
            return "-";
        }

        try {
            Thuoc thuoc = thuocClientController.getThuocById(maThuoc);
            return thuoc == null ? maThuoc : thuoc.getTenThuoc();
        } catch (Exception ignored) {
            return maThuoc;
        }
    }

    private String toDisplayDate(LocalDate localDate) {
        if (localDate == null) {
            return "";
        }
        return DATE_FORMATTER.format(localDate);
    }

    private String getNhanVienDisplay(HoaDon hd) {
        NhanVien nhanVien = hd.getNhanVien();
        if (nhanVien == null) {
            return "";
        }
        String ten = nhanVien.getTenNV() == null ? "" : nhanVien.getTenNV();
        String ma = nhanVien.getMaNV() == null ? "" : nhanVien.getMaNV();
        return ten.isBlank() ? ma : ten + " (" + ma + ")";
    }

    private String getKhachHangDisplay(HoaDon hd) {
        KhachHang khachHang = hd.getKhachHang();
        if (khachHang == null) {
            return "";
        }
        String ten = khachHang.getTenKH() == null ? "" : khachHang.getTenKH();
        String ma = khachHang.getMaKH() == null ? "" : khachHang.getMaKH();
        return ten.isBlank() ? ma : ten + " (" + ma + ")";
    }

    private void renderHoaDonToTable(List<HoaDon> hoaDons) {
        dsHoaDon.clear();
        dsHoaDon.addAll(hoaDons);

        dtmHoaDon.setRowCount(0);
        dtmChiTietHoaDon.setRowCount(0);

        for (HoaDon hd : dsHoaDon) {
            Object[] rowData = {
                    hd.getMaHoaDon(),
                    getNhanVienDisplay(hd),
                    getKhachHangDisplay(hd),
                    toDisplayDate(hd.getNgayBan()),
                    hd.getGhiChu() == null ? "" : hd.getGhiChu()
            };
            dtmHoaDon.addRow(rowData);
        }
    }

    public void loadHoaDonData(String maThuoc, int thang, int ngay, int nam) {
        if (hoaDonClientController == null) {
            renderHoaDonToTable(new ArrayList<>());
            return;
        }

        try {
            List<HoaDon> source = hoaDonClientController.getAllHoaDon5Field(maThuoc);
            ArrayList<HoaDon> filtered = new ArrayList<>();

            for (HoaDon hd : source) {
                LocalDate ngayBan = hd.getNgayBan();
                if (ngayBan == null) {
                    continue;
                }

                boolean match = true;
                if (nam != 0 && ngayBan.getYear() != nam) {
                    match = false;
                }
                if (match && thang != 0 && ngayBan.getMonthValue() != thang) {
                    match = false;
                }
                if (match && ngay != 0 && ngayBan.getDayOfMonth() != ngay) {
                    match = false;
                }

                if (match) {
                    filtered.add(hd);
                }
            }

            renderHoaDonToTable(filtered);
        } catch (Exception e) {
            renderHoaDonToTable(new ArrayList<>());
        }
    }

    public void loadChiTietHoaDonData(String maHoaDon) {
        dtmChiTietHoaDon.setRowCount(0);
        dsChiTietHoaDon.clear();

        if (hoaDonClientController == null) {
            return;
        }

        try {
            List<ChiTietHoaDon> chiTietList = hoaDonClientController.getChiTietByMaHoaDon(maHoaDon);
            dsChiTietHoaDon.addAll(chiTietList);

            for (ChiTietHoaDon ct : dsChiTietHoaDon) {
                String maThuoc = ct.getId() == null ? "" : ct.getId().getMaThuoc();
                String maLo = ct.getId() == null ? "" : ct.getId().getMaLo();
                String tenThuoc = resolveTenThuoc(maThuoc);

                Object[] rowData = {
                        tenThuoc,
                        maThuoc,
                        maLo,
                        ct.getSoLuong(),
                        ct.getDonGia()
                };
                dtmChiTietHoaDon.addRow(rowData);
            }
        } catch (Exception ignored) {
            // Keep detail table empty on failure.
        }
    }

    public void timKiemHoaDonTheoThangNam() {
        if (hoaDonClientController == null) {
            return;
        }

        try {
            int nam = Integer.parseInt((String) cboNam.getSelectedItem());
            int thang = cboThang.getSelectedIndex() + 1;
            List<HoaDon> result = hoaDonClientController.getHoaDonByThangNam(thang, nam);
            renderHoaDonToTable(result);
        } catch (Exception ignored) {
            renderHoaDonToTable(new ArrayList<>());
        }
    }

    public void timKiemHoaDonTheoMaHoaDon() {
        String maHoaDon = searchField.getText();
        if (maHoaDon == null) {
            maHoaDon = "";
        }

        String value = maHoaDon.trim().toLowerCase();
        dtmHoaDon.setRowCount(0);
        dtmChiTietHoaDon.setRowCount(0);

        for (HoaDon hd : dsHoaDon) {
            if (hd.getMaHoaDon() != null && hd.getMaHoaDon().toLowerCase().contains(value)) {
                dtmHoaDon.addRow(new Object[]{
                        hd.getMaHoaDon(),
                        getNhanVienDisplay(hd),
                        getKhachHangDisplay(hd),
                        toDisplayDate(hd.getNgayBan()),
                        hd.getGhiChu() == null ? "" : hd.getGhiChu()
                });
            }
        }
    }

    public void timKiemHoaDonTheoTenNhanVien() {
        String tenNhanVien = searchField.getText();
        if (tenNhanVien == null) {
            tenNhanVien = "";
        }

        String value = tenNhanVien.trim().toLowerCase();
        dtmHoaDon.setRowCount(0);
        dtmChiTietHoaDon.setRowCount(0);

        for (HoaDon hd : dsHoaDon) {
            String display = getNhanVienDisplay(hd).toLowerCase();
            if (display.contains(value)) {
                dtmHoaDon.addRow(new Object[]{
                        hd.getMaHoaDon(),
                        getNhanVienDisplay(hd),
                        getKhachHangDisplay(hd),
                        toDisplayDate(hd.getNgayBan()),
                        hd.getGhiChu() == null ? "" : hd.getGhiChu()
                });
            }
        }
    }

    public void timKiemHoaDonTheoTenKhachHang() {
        String tenKhachHang = searchField.getText();
        if (tenKhachHang == null) {
            tenKhachHang = "";
        }

        String value = tenKhachHang.trim().toLowerCase();
        dtmHoaDon.setRowCount(0);
        dtmChiTietHoaDon.setRowCount(0);

        for (HoaDon hd : dsHoaDon) {
            String display = getKhachHangDisplay(hd).toLowerCase();
            if (display.contains(value)) {
                dtmHoaDon.addRow(new Object[]{
                        hd.getMaHoaDon(),
                        getNhanVienDisplay(hd),
                        getKhachHangDisplay(hd),
                        toDisplayDate(hd.getNgayBan()),
                        hd.getGhiChu() == null ? "" : hd.getGhiChu()
                });
            }
        }
    }

    private HoaDon findSelectedHoaDon() {
        int selectedRow = tblHoaDon.getSelectedRow();
        if (selectedRow < 0) {
            return null;
        }

        String maHoaDon = tblHoaDon.getValueAt(selectedRow, 0).toString();
        for (HoaDon hd : dsHoaDon) {
            if (hd.getMaHoaDon().equals(maHoaDon)) {
                return hd;
            }
        }
        return null;
    }

    private void xuatHoaDonPDF() {
        try {
            int selectedRow = tblHoaDon.getSelectedRow();
            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn hóa đơn trước khi xuất PDF.");
                return;
            }

            String defaultDir = "D:\\PTUD\\PDF\\HoaDon";
            File directory = new File(defaultDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            String maHoaDon = tblHoaDon.getValueAt(selectedRow, 0).toString();
            HoaDon hd = hoaDonClientController == null ? null : hoaDonClientController.getHoaDonById(maHoaDon);
            if (hd == null) {
                JOptionPane.showMessageDialog(this, "Không thể tải dữ liệu hóa đơn để xuất PDF.");
                return;
            }

            if (dsChiTietHoaDon.isEmpty()) {
                loadChiTietHoaDonData(maHoaDon);
            }

            String fileName = "HoaDon_" + maHoaDon + ".pdf";
            String filePath = defaultDir + "\\" + fileName;

            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            BaseFont baseFont = BaseFont.createFont("c:/windows/fonts/arial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            com.itextpdf.text.Font titleFont = new com.itextpdf.text.Font(baseFont, 18, com.itextpdf.text.Font.BOLD);
            com.itextpdf.text.Font headerFont = new com.itextpdf.text.Font(baseFont, 12, com.itextpdf.text.Font.BOLD);
            com.itextpdf.text.Font normalFont = new com.itextpdf.text.Font(baseFont, 11, com.itextpdf.text.Font.NORMAL);
            com.itextpdf.text.Font totalFont = new com.itextpdf.text.Font(baseFont, 12, com.itextpdf.text.Font.BOLD);

            Paragraph title = new Paragraph("HÓA ĐƠN BÁN THUỐC", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            Paragraph shopInfo = new Paragraph("NHÀ THUỐC PILL & CHILL\nĐịa chỉ: 12 Nguyễn Văn Bảo, P.4, Q.Gò Vấp, TP.HCM\nHotline: 0987654321", normalFont);
            shopInfo.setAlignment(Element.ALIGN_CENTER);
            document.add(shopInfo);

            document.add(new Paragraph("\n"));

            String currentDate = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());
            Paragraph invoiceInfo = new Paragraph();
            invoiceInfo.add(new Chunk("Mã hóa đơn: " + maHoaDon + "\n", headerFont));
            invoiceInfo.add(new Chunk("Ngày lập: " + currentDate + "\n", normalFont));

            NhanVien nv = hd.getNhanVien();
            String tenNhanVien = nv == null || nv.getTenNV() == null ? "Nhân viên" : nv.getTenNV();
            String maNhanVien = nv == null || nv.getMaNV() == null ? "" : nv.getMaNV();
            invoiceInfo.add(new Chunk("Nhân viên lập: " + tenNhanVien + (maNhanVien.isBlank() ? "" : " (" + maNhanVien + ")") + "\n", normalFont));

            KhachHang kh = hd.getKhachHang();
            if (kh != null) {
                String tenKhachHang = kh.getTenKH() == null ? "Khách hàng" : kh.getTenKH();
                String sdtKhachHang = kh.getSoDienThoai() == null ? "" : kh.getSoDienThoai();
                invoiceInfo.add(new Chunk("Khách hàng: " + tenKhachHang + "\n", normalFont));
                invoiceInfo.add(new Chunk("Số điện thoại: " + sdtKhachHang + "\n", normalFont));
            }

            document.add(invoiceInfo);
            document.add(new Paragraph("\n"));

            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);
            table.setWidths(new float[]{1f, 3f, 1f, 1.5f, 2f});

            addTableHeader(table, headerFont);

            java.text.DecimalFormat decimalFormat = new java.text.DecimalFormat("#,###");
            double tongTien = 0;
            for (ChiTietHoaDon item : dsChiTietHoaDon) {
                String maThuoc = item.getId() == null ? "" : item.getId().getMaThuoc();
                String tenThuoc = resolveTenThuoc(maThuoc);
                int soLuong = item.getSoLuong();
                float donGia = item.getDonGia();
                double thanhTien = soLuong * donGia;
                tongTien += thanhTien;

                table.addCell(new PdfPCell(new Phrase(maThuoc, normalFont)));
                table.addCell(new PdfPCell(new Phrase(tenThuoc, normalFont)));
                table.addCell(new PdfPCell(new Phrase(String.valueOf(soLuong), normalFont)));
                table.addCell(new PdfPCell(new Phrase(decimalFormat.format(donGia), normalFont)));
                table.addCell(new PdfPCell(new Phrase(decimalFormat.format(thanhTien), normalFont)));
            }

            document.add(table);

            double tyLeThue = 0.10;
            String tenThue = "VAT (10%)";
            double tienThue = tongTien * tyLeThue;
            double tongThanhToan = tongTien + tienThue;

            Paragraph summary = new Paragraph();
            summary.add(new Chunk("Tổng tiền hàng: " + decimalFormat.format(tongTien) + " VNĐ\n", normalFont));
            summary.add(new Chunk("Thuế " + tenThue + ": " + decimalFormat.format(tienThue) + " VNĐ\n", normalFont));
            summary.add(new Chunk("Tổng thanh toán: " + decimalFormat.format(tongThanhToan) + " VNĐ\n", totalFont));
            summary.setAlignment(Element.ALIGN_RIGHT);
            document.add(summary);

            document.add(new Paragraph("\n"));

            Paragraph signature = new Paragraph();
            signature.add(new Chunk("Xác nhận của khách hàng                                                             Người lập hóa đơn\n\n\n\n", normalFont));
            signature.add(new Chunk("                                                                                             " + tenNhanVien, normalFont));
            signature.setAlignment(Element.ALIGN_CENTER);
            document.add(signature);

            String ghiChuText = hd.getGhiChu();
            if (ghiChuText != null && !ghiChuText.isEmpty()) {
                document.add(new Paragraph("\n"));
                Paragraph ghiChuParagraph = new Paragraph("Ghi chú: " + ghiChuText, normalFont);
                ghiChuParagraph.setAlignment(Element.ALIGN_LEFT);
                document.add(ghiChuParagraph);
            }

            Paragraph note = new Paragraph("\n\nLưu ý: \n- Hóa đơn chỉ có thể xuất trong ngày.", normalFont);
            document.add(note);

            document.close();

            JOptionPane.showMessageDialog(this,
                    "Đã xuất hóa đơn PDF thành công!\nVị trí: " + filePath,
                    "Xuất hóa đơn",
                    JOptionPane.INFORMATION_MESSAGE);

            try {
                File pdfFile = new File(filePath);
                if (pdfFile.exists()) {
                    if (Desktop.isDesktopSupported()) {
                        Desktop.getDesktop().open(pdfFile);
                    } else {
                        new ProcessBuilder("cmd", "/c", "start", "", filePath).start();
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Không thể tự động mở file PDF: " + ex.getMessage(),
                        "Cảnh báo",
                        JOptionPane.WARNING_MESSAGE);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Lỗi khi xuất hóa đơn PDF: " + e.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addTableHeader(PdfPTable table, com.itextpdf.text.Font font) {
        String[] headers = {"Mã thuốc", "Tên thuốc", "Số lượng", "Đơn giá", "Thành tiền"};

        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, font));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(new BaseColor(240, 240, 240));
            cell.setPadding(5);
            table.addCell(cell);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == searchButton) {
            if (cboTimKiem.getSelectedIndex() == 0) {
                timKiemHoaDonTheoMaHoaDon();
            } else if (cboTimKiem.getSelectedIndex() == 1) {
                timKiemHoaDonTheoTenNhanVien();
            } else if (cboTimKiem.getSelectedIndex() == 2) {
                timKiemHoaDonTheoTenKhachHang();
            }
            return;
        }

        if (source == radTimKiemThangNam) {
            if (radTimKiemThangNam.isSelected()) {
                timKiemHoaDonTheoThangNam();
            } else {
                loadHoaDonData(maThuocFilter, thangFilter, ngayFilter, namFilter);
            }
            return;
        }

        if ((source == cboThang || source == cboNam) && radTimKiemThangNam.isSelected()) {
            timKiemHoaDonTheoThangNam();
            return;
        }

        if (source == btnXuatHoaDon) {
            xuatHoaDonPDF();
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int row = tblHoaDon.getSelectedRow();
        if (row != -1) {
            String maHoaDon = tblHoaDon.getValueAt(row, 0).toString();
            loadChiTietHoaDonData(maHoaDon);
            findSelectedHoaDon();
        }
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
