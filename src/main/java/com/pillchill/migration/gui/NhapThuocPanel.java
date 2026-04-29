package com.pillchill.migration.gui;

import com.pillchill.migration.dto.PhieuNhapImportItem;
import com.pillchill.migration.network.client.PhieuNhapClientController;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class NhapThuocPanel extends JPanel {
    private static final DateTimeFormatter DISPLAY_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final PhieuNhapClientController phieuNhapClientController;
    private final DataFormatter dataFormatter = new DataFormatter();

    private final List<PhieuNhapImportItem> dsThuoc = new ArrayList<>();
    private final DefaultTableModel dtmTable;

    private final JTextField txtTim;
    private final JComboBox<String> cboTieuChi;
    private final JLabel lblTongSoBanGhi;
    private final JLabel lblTongSoLuong;
    private final JLabel lblTongTien;
    private final JTable table;

    public NhapThuocPanel(PhieuNhapClientController phieuNhapClientController) {
        this.phieuNhapClientController = phieuNhapClientController;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JPanel centerPanel = new JPanel(new BorderLayout(0, 20));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(new EmptyBorder(20, 40, 20, 40));

        JLabel titleLabel = new JLabel("NHẬP THUỐC TỪ EXCEL", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        actionPanel.setBackground(Color.WHITE);

        txtTim = new JTextField(22);
        cboTieuChi = new JComboBox<>(new String[]{"Mã thuốc", "Tên thuốc"});

        JButton btnTim = createButton("Tìm", new Color(240, 240, 240), Color.BLACK);
        JButton btnChonFile = createButton("Chọn file Excel", new Color(52, 152, 219), Color.WHITE);
        JButton btnXoaThuoc = createButton("Xóa dòng", new Color(231, 76, 60), Color.WHITE);
        JButton btnNhapThuoc = createButton("Lưu vào CSDL", new Color(46, 204, 113), Color.WHITE);
        JButton btnXoaLoc = createButton("Xóa lọc", new Color(240, 240, 240), Color.BLACK);

        btnTim.addActionListener(e -> timKiem());
        btnXoaLoc.addActionListener(e -> {
            txtTim.setText("");
            loadData(dsThuoc);
        });
        btnChonFile.addActionListener(e -> chonFileExcel());
        btnXoaThuoc.addActionListener(e -> xoaDongDaChon());
        btnNhapThuoc.addActionListener(e -> nhapThuoc());

        actionPanel.add(txtTim);
        actionPanel.add(btnTim);
        actionPanel.add(cboTieuChi);
        actionPanel.add(btnXoaLoc);
        actionPanel.add(btnChonFile);
        actionPanel.add(btnXoaThuoc);
        actionPanel.add(btnNhapThuoc);

        JPanel topPanel = new JPanel(new BorderLayout(0, 10));
        topPanel.setBackground(Color.WHITE);
        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(actionPanel, BorderLayout.CENTER);

        String[] cols = {
                "Mã thuốc", "Mã lô", "Tên thuốc", "Số lượng", "Giá nhập",
                "Mã đơn vị", "SL tối thiểu", "Mã NSX", "Ngày sản xuất", "Hạn sử dụng"
        };
        dtmTable = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(dtmTable);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(32);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(new Color(240, 250, 240));
        header.setPreferredSize(new Dimension(header.getWidth(), 36));
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(210, 210, 210)));

        JPanel thongKePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        thongKePanel.setBackground(Color.WHITE);
        lblTongSoBanGhi = new JLabel("Tổng số bản ghi: 0");
        lblTongSoLuong = new JLabel("Tổng số lượng: 0");
        lblTongTien = new JLabel("Tổng tiền: 0 VNĐ");
        lblTongSoBanGhi.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTongSoLuong.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTongTien.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTongTien.setForeground(new Color(231, 76, 60));
        thongKePanel.add(lblTongSoBanGhi);
        thongKePanel.add(lblTongSoLuong);
        thongKePanel.add(lblTongTien);

        centerPanel.add(topPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        centerPanel.add(thongKePanel, BorderLayout.SOUTH);
        add(centerPanel, BorderLayout.CENTER);

        capNhatThongKe();
    }

    private JButton createButton(String text, Color bg, Color fg) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBackground(bg);
        button.setForeground(fg);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void chonFileExcel() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn file Excel chứa danh sách nhập thuốc");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Excel (*.xlsx, *.xls)", "xlsx", "xls"));

        if (fileChooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        List<PhieuNhapImportItem> parsedItems = docThuocTuExcel(fileChooser.getSelectedFile().getAbsolutePath());
        if (parsedItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không có dòng hợp lệ trong file Excel.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        dsThuoc.clear();
        dsThuoc.addAll(parsedItems);
        loadData(dsThuoc);
        JOptionPane.showMessageDialog(this, "Đọc thành công " + dsThuoc.size() + " dòng hợp lệ.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
    }

    private List<PhieuNhapImportItem> docThuocTuExcel(String filePath) {
        List<PhieuNhapImportItem> results = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        try (Workbook workbook = WorkbookFactory.create(new java.io.File(filePath))) {
            Sheet sheet = workbook.getSheetAt(0);
            int rowCount = sheet.getPhysicalNumberOfRows();

            for (int rowIdx = 1; rowIdx < rowCount; rowIdx++) {
                Row row = sheet.getRow(rowIdx);
                if (row == null) {
                    continue;
                }
                try {
                    PhieuNhapImportItem item = parseRow(row);
                    results.add(item);
                } catch (IllegalArgumentException ex) {
                    errors.add("Dòng " + (rowIdx + 1) + ": " + ex.getMessage());
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi đọc file Excel: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            return new ArrayList<>();
        }

        if (!errors.isEmpty()) {
            StringBuilder sb = new StringBuilder("Có ").append(errors.size()).append(" dòng lỗi:\n\n");
            int maxShow = Math.min(errors.size(), 10);
            for (int i = 0; i < maxShow; i++) {
                sb.append(errors.get(i)).append("\n");
            }
            if (errors.size() > maxShow) {
                sb.append("\n... và ").append(errors.size() - maxShow).append(" lỗi khác");
            }
            JOptionPane.showMessageDialog(this, sb.toString(), "Cảnh báo", JOptionPane.WARNING_MESSAGE);
        }
        return results;
    }

    private PhieuNhapImportItem parseRow(Row row) {
        String maThuoc = getCellText(row.getCell(0)).toUpperCase(Locale.ROOT);
        String maLo = getCellText(row.getCell(1)).toUpperCase(Locale.ROOT);
        String tenThuoc = getCellText(row.getCell(2));
        int soLuong = getInt(row.getCell(3));
        double giaNhap = getDouble(row.getCell(4));
        String maDonVi = getCellText(row.getCell(5)).toLowerCase(Locale.ROOT);
        int soLuongToiThieu = getInt(row.getCell(6));
        String maNSX = getCellText(row.getCell(7)).toUpperCase(Locale.ROOT);
        LocalDate ngaySanXuat = getDate(row.getCell(8));
        LocalDate hanSuDung = getDate(row.getCell(9));

        if (!maThuoc.matches("^T\\d{3}$")) {
            throw new IllegalArgumentException("Mã thuốc phải theo định dạng TXXX");
        }
        if (!maLo.matches("^LO\\d{3}$")) {
            throw new IllegalArgumentException("Mã lô phải theo định dạng LOXXX");
        }
        if (tenThuoc.isBlank()) {
            throw new IllegalArgumentException("Tên thuốc không được để trống");
        }
        if (soLuong <= 0) {
            throw new IllegalArgumentException("Số lượng phải lớn hơn 0");
        }
        if (giaNhap <= 0) {
            throw new IllegalArgumentException("Giá nhập phải lớn hơn 0");
        }
        if (soLuongToiThieu < 0) {
            throw new IllegalArgumentException("Số lượng tối thiểu không hợp lệ");
        }
        if (maDonVi.isBlank()) {
            throw new IllegalArgumentException("Mã đơn vị không hợp lệ");
        }
        if (!maNSX.matches("^NSX\\d{3}$")) {
            throw new IllegalArgumentException("Mã NSX phải theo định dạng NSX###");
        }
        if (ngaySanXuat == null || hanSuDung == null) {
            throw new IllegalArgumentException("Ngày sản xuất/hạn sử dụng không hợp lệ");
        }
        if (hanSuDung.isBefore(ngaySanXuat)) {
            throw new IllegalArgumentException("Hạn sử dụng phải sau ngày sản xuất");
        }
        if (hanSuDung.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Thuốc đã hết hạn sử dụng");
        }

        return new PhieuNhapImportItem(
                maThuoc, maLo, tenThuoc, soLuong, giaNhap, maDonVi, soLuongToiThieu, maNSX, ngaySanXuat, hanSuDung
        );
    }

    private String getCellText(Cell cell) {
        if (cell == null) {
            return "";
        }
        return dataFormatter.formatCellValue(cell).trim();
    }

    private int getInt(Cell cell) {
        if (cell == null) {
            return 0;
        }
        if (cell.getCellType() == CellType.NUMERIC) {
            return (int) Math.round(cell.getNumericCellValue());
        }
        String text = getCellText(cell);
        if (text.isBlank()) {
            return 0;
        }
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Giá trị số nguyên không hợp lệ: " + text);
        }
    }

    private double getDouble(Cell cell) {
        if (cell == null) {
            return 0;
        }
        if (cell.getCellType() == CellType.NUMERIC) {
            return cell.getNumericCellValue();
        }
        String text = getCellText(cell);
        if (text.isBlank()) {
            return 0;
        }
        try {
            return Double.parseDouble(text);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Giá trị số thực không hợp lệ: " + text);
        }
    }

    private LocalDate getDate(Cell cell) {
        if (cell == null) {
            return null;
        }
        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            return Instant.ofEpochMilli(cell.getDateCellValue().getTime())
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
        }
        String text = getCellText(cell);
        if (text.isBlank()) {
            return null;
        }

        try {
            return LocalDate.parse(text, DISPLAY_DATE);
        } catch (DateTimeParseException ignored) {
        }
        try {
            return LocalDate.parse(text);
        } catch (DateTimeParseException ignored) {
        }
        throw new IllegalArgumentException("Ngày không đúng định dạng: " + text);
    }

    private void loadData(List<PhieuNhapImportItem> items) {
        dtmTable.setRowCount(0);
        for (PhieuNhapImportItem item : items) {
            dtmTable.addRow(new Object[]{
                    item.getMaThuoc(),
                    item.getMaLo(),
                    item.getTenThuoc(),
                    item.getSoLuong(),
                    String.format(Locale.ROOT, "%,.0f", item.getGiaNhap()),
                    item.getMaDonVi(),
                    item.getSoLuongToiThieu(),
                    item.getMaNSX(),
                    item.getNgaySanXuat().format(DISPLAY_DATE),
                    item.getHanSuDung().format(DISPLAY_DATE)
            });
        }
        capNhatThongKe();
    }

    private void capNhatThongKe() {
        int tongSoLuong = 0;
        double tongTien = 0;
        for (PhieuNhapImportItem item : dsThuoc) {
            tongSoLuong += item.getSoLuong();
            tongTien += item.getSoLuong() * item.getGiaNhap();
        }
        lblTongSoBanGhi.setText("Tổng số bản ghi: " + dsThuoc.size());
        lblTongSoLuong.setText("Tổng số lượng: " + String.format(Locale.ROOT, "%,d", tongSoLuong));
        lblTongTien.setText("Tổng tiền: " + String.format(Locale.ROOT, "%,.0f VNĐ", tongTien));
    }

    private void timKiem() {
        String keyword = txtTim.getText().trim().toLowerCase(Locale.ROOT);
        if (keyword.isBlank()) {
            loadData(dsThuoc);
            return;
        }
        int mode = cboTieuChi.getSelectedIndex();
        List<PhieuNhapImportItem> ketQua = new ArrayList<>();
        for (PhieuNhapImportItem item : dsThuoc) {
            boolean matched = mode == 0
                    ? item.getMaThuoc().toLowerCase(Locale.ROOT).startsWith(keyword)
                    : item.getTenThuoc().toLowerCase(Locale.ROOT).contains(keyword);
            if (matched) {
                ketQua.add(item);
            }
        }
        if (ketQua.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy dòng phù hợp.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            loadData(dsThuoc);
            return;
        }
        loadData(ketQua);
    }

    private void xoaDongDaChon() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một dòng để xóa.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String maThuoc = String.valueOf(table.getValueAt(row, 0));
        String maLo = String.valueOf(table.getValueAt(row, 1));
        int confirm = JOptionPane.showConfirmDialog(this, "Xóa dòng " + maThuoc + " - " + maLo + "?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        dsThuoc.removeIf(item -> item.getMaThuoc().equals(maThuoc) && item.getMaLo().equals(maLo));
        loadData(dsThuoc);
    }

    private void nhapThuoc() {
        if (dsThuoc.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không có dữ liệu để nhập.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Xác nhận nhập " + dsThuoc.size() + " dòng vào CSDL?",
                "Xác nhận nhập thuốc",
                JOptionPane.YES_NO_OPTION
        );
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        List<PhieuNhapImportItem> payloadItems = new ArrayList<>(dsThuoc);
        SwingWorker<String, Void> worker = new SwingWorker<>() {
            @Override
            protected String doInBackground() {
                return phieuNhapClientController.importFromExcelItems(payloadItems);
            }

            @Override
            protected void done() {
                try {
                    String maPhieuNhap = get();
                    JOptionPane.showMessageDialog(
                            NhapThuocPanel.this,
                            "Nhập thuốc thành công. Mã phiếu nhập: " + maPhieuNhap,
                            "Thành công",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                    dsThuoc.clear();
                    loadData(dsThuoc);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(
                            NhapThuocPanel.this,
                            "Nhập thuốc thất bại: " + ex.getMessage(),
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };
        worker.execute();
    }
}
