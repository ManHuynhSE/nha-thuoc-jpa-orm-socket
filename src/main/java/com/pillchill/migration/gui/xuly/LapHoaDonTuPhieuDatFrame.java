package com.pillchill.migration.gui.xuly;

import com.pillchill.migration.dto.ChiTietPhieuDatView;
import com.pillchill.migration.dto.PhieuDatView;
import com.pillchill.migration.dto.ThuocKemGiaView;
import com.pillchill.migration.entity.KhachHang;
import com.pillchill.migration.gui.xuly.HoaDonCallback;
import com.pillchill.migration.network.client.HoaDonClientController;
import com.pillchill.migration.network.client.KhachHangClientController;
import com.pillchill.migration.network.client.KhuyenMaiClientController;
import com.pillchill.migration.network.client.PhieuDatClientController;
import com.pillchill.migration.network.client.ThuocClientController;
import com.pillchill.migration.network.communication.HoaDonCreateItemPayload;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LapHoaDonTuPhieuDatFrame extends JFrame {
    private final PhieuDatClientController phieuDatClientController;
    private final ThuocClientController thuocClientController;
    private final HoaDonClientController hoaDonClientController;
    private final KhachHangClientController khachHangClientController;
    private final KhuyenMaiClientController khuyenMaiClientController;
    private final String maNhanVien;
    private final HoaDonCallback hoaDonCallback;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final DecimalFormat moneyFormatter = new DecimalFormat("#,##0");

    private final DefaultTableModel modelPhieuDat;
    private final DefaultTableModel modelChiTiet;
    private final JTable tblPhieuDat;
    private final JTable tblChiTiet;
    private final JTextField txtSearch;
    private final JTextField txtTongTien;

    private final List<PhieuDatView> dsPhieuDat = new ArrayList<>();
    private final List<ChiTietPhieuDatView> dsChiTiet = new ArrayList<>();
    private final Map<String, Double> giaTheoThuoc = new HashMap<>();
    private PhieuDatView phieuDatDuocChon;

    public LapHoaDonTuPhieuDatFrame(PhieuDatClientController phieuDatClientController,
                                    ThuocClientController thuocClientController,
                                    HoaDonClientController hoaDonClientController,
                                    KhachHangClientController khachHangClientController,
                                    KhuyenMaiClientController khuyenMaiClientController,
                                    String maNhanVien,
                                    HoaDonCallback hoaDonCallback) {
        this.phieuDatClientController = phieuDatClientController;
        this.thuocClientController = thuocClientController;
        this.hoaDonClientController = hoaDonClientController;
        this.khachHangClientController = khachHangClientController;
        this.khuyenMaiClientController = khuyenMaiClientController;
        this.maNhanVien = maNhanVien;
        this.hoaDonCallback = hoaDonCallback;

        setTitle("Lập hóa đơn từ phiếu đặt");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1300, 760);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(12, 14, 6, 14));
        JLabel lblTitle = new JLabel("LẬP HÓA ĐƠN TỪ PHIẾU ĐẶT", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        topPanel.add(lblTitle, BorderLayout.NORTH);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        txtSearch = new JTextField(28);
        JButton btnTim = new JButton("Tìm");
        JButton btnLamMoi = new JButton("Làm mới");
        searchPanel.add(new JLabel("Từ khóa:"));
        searchPanel.add(txtSearch);
        searchPanel.add(btnTim);
        searchPanel.add(btnLamMoi);
        topPanel.add(searchPanel, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 0, 12));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 14));

        modelPhieuDat = new DefaultTableModel(new String[]{"Mã phiếu đặt", "Nhân viên", "Khách hàng", "Ngày đặt", "Ghi chú"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblPhieuDat = new JTable(modelPhieuDat);
        tblPhieuDat.setRowHeight(32);
        centerPanel.add(new JScrollPane(tblPhieuDat));

        modelChiTiet = new DefaultTableModel(new String[]{"Mã thuốc", "Tên thuốc", "Mã lô", "Số lượng", "Đơn giá", "Thành tiền"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblChiTiet = new JTable(modelChiTiet);
        tblChiTiet.setRowHeight(32);
        centerPanel.add(new JScrollPane(tblChiTiet));
        add(centerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 14, 12, 14));
        txtTongTien = new JTextField(16);
        txtTongTien.setEditable(false);
        txtTongTien.setHorizontalAlignment(JTextField.RIGHT);
        txtTongTien.setText("0 VNĐ");
        JButton btnXacNhan = new JButton("Xác nhận lập hóa đơn");
        JButton btnDong = new JButton("Đóng");
        bottomPanel.add(new JLabel("Tổng tiền:"));
        bottomPanel.add(txtTongTien);
        bottomPanel.add(btnXacNhan);
        bottomPanel.add(btnDong);
        add(bottomPanel, BorderLayout.SOUTH);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        tblPhieuDat.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        tblChiTiet.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        tblChiTiet.getColumnModel().getColumn(4).setCellRenderer(rightAlignRenderer());
        tblChiTiet.getColumnModel().getColumn(5).setCellRenderer(rightAlignRenderer());

        btnTim.addActionListener(e -> filterPhieuDat());
        btnLamMoi.addActionListener(e -> {
            txtSearch.setText("");
            loadPhieuDatTable(dsPhieuDat);
        });
        btnDong.addActionListener(e -> dispose());
        btnXacNhan.addActionListener(e -> xacNhanLapHoaDon());
        tblPhieuDat.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                onPhieuDatSelected();
            }
        });

        loadData();
    }

    private DefaultTableCellRenderer rightAlignRenderer() {
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setHorizontalAlignment(SwingConstants.RIGHT);
        return renderer;
    }

    private void loadData() {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                giaTheoThuoc.clear();
                for (ThuocKemGiaView thuoc : thuocClientController.getAllThuocItems()) {
                    giaTheoThuoc.put(thuoc.getMaThuoc(), thuoc.getGiaBan());
                }

                dsPhieuDat.clear();
                for (PhieuDatView phieuDat : phieuDatClientController.getAllPhieuDatItems()) {
                    if (!phieuDat.isReceived()) {
                        dsPhieuDat.add(phieuDat);
                    }
                }
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    loadPhieuDatTable(dsPhieuDat);
                    modelChiTiet.setRowCount(0);
                    dsChiTiet.clear();
                    txtTongTien.setText("0 VNĐ");
                    phieuDatDuocChon = null;
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(
                            LapHoaDonTuPhieuDatFrame.this,
                            "Không tải được dữ liệu phiếu đặt: " + ex.getMessage(),
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };
        worker.execute();
    }

    private void loadPhieuDatTable(List<PhieuDatView> data) {
        modelPhieuDat.setRowCount(0);
        for (PhieuDatView phieuDat : data) {
            modelPhieuDat.addRow(new Object[]{
                    phieuDat.getMaPhieuDat(),
                    phieuDat.getTenNhanVien() == null ? "" : phieuDat.getTenNhanVien(),
                    phieuDat.getTenKhachHang() == null ? "Khách vãng lai" : phieuDat.getTenKhachHang(),
                    phieuDat.getNgayDat() == null ? "" : phieuDat.getNgayDat().format(dateFormatter),
                    phieuDat.getGhiChu() == null ? "" : phieuDat.getGhiChu()
            });
        }
    }

    private void onPhieuDatSelected() {
        int selected = tblPhieuDat.getSelectedRow();
        if (selected < 0) {
            return;
        }
        String maPhieuDat = String.valueOf(modelPhieuDat.getValueAt(selected, 0));
        phieuDatDuocChon = findPhieuDatByMa(maPhieuDat);
        if (phieuDatDuocChon == null) {
            return;
        }

        SwingWorker<List<ChiTietPhieuDatView>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<ChiTietPhieuDatView> doInBackground() {
                return phieuDatClientController.getChiTietPhieuDatItems(maPhieuDat);
            }

            @Override
            protected void done() {
                try {
                    dsChiTiet.clear();
                    dsChiTiet.addAll(get());
                    loadChiTietTable();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(
                            LapHoaDonTuPhieuDatFrame.this,
                            "Không tải được chi tiết phiếu đặt: " + ex.getMessage(),
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };
        worker.execute();
    }

    private void loadChiTietTable() {
        modelChiTiet.setRowCount(0);
        double tongTien = 0d;
        for (ChiTietPhieuDatView chiTiet : dsChiTiet) {
            double donGia = giaTheoThuoc.getOrDefault(chiTiet.getMaThuoc(), 0d);
            double thanhTien = donGia * chiTiet.getSoLuong();
            tongTien += thanhTien;
            modelChiTiet.addRow(new Object[]{
                    chiTiet.getMaThuoc(),
                    chiTiet.getTenThuoc(),
                    chiTiet.getMaLo(),
                    chiTiet.getSoLuong(),
                    moneyFormatter.format(donGia),
                    moneyFormatter.format(thanhTien)
            });
        }
        txtTongTien.setText(moneyFormatter.format(tongTien) + " VNĐ");
    }

    private PhieuDatView findPhieuDatByMa(String maPhieuDat) {
        for (PhieuDatView item : dsPhieuDat) {
            if (item.getMaPhieuDat() != null && item.getMaPhieuDat().equalsIgnoreCase(maPhieuDat)) {
                return item;
            }
        }
        return null;
    }

    private void filterPhieuDat() {
        String keyword = txtSearch.getText().trim().toLowerCase();
        if (keyword.isBlank()) {
            loadPhieuDatTable(dsPhieuDat);
            return;
        }

        List<PhieuDatView> filtered = new ArrayList<>();
        for (PhieuDatView item : dsPhieuDat) {
            boolean match = (item.getMaPhieuDat() != null && item.getMaPhieuDat().toLowerCase().contains(keyword))
                    || (item.getTenNhanVien() != null && item.getTenNhanVien().toLowerCase().contains(keyword))
                    || (item.getTenKhachHang() != null && item.getTenKhachHang().toLowerCase().contains(keyword));
            if (match) {
                filtered.add(item);
            }
        }
        loadPhieuDatTable(filtered);
    }

    private void xacNhanLapHoaDon() {
        if (phieuDatDuocChon == null || dsChiTiet.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn phiếu đặt có chi tiết.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Xác nhận lập hóa đơn từ phiếu đặt đã chọn?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        List<HoaDonCreateItemPayload> items = new ArrayList<>();
        for (ChiTietPhieuDatView item : dsChiTiet) {
            float donGia = giaTheoThuoc.getOrDefault(item.getMaThuoc(), 0d).floatValue();
            items.add(new HoaDonCreateItemPayload(
                    item.getMaThuoc(),
                    item.getTenThuoc(),
                    item.getMaLo(),
                    item.getSoLuong(),
                    donGia
            ));
        }

        KhachHang khachHangMacDinh = null;
        if (phieuDatDuocChon.getMaKhachHang() != null && !phieuDatDuocChon.getMaKhachHang().isBlank()) {
            try {
                khachHangMacDinh = khachHangClientController.findByMaItem(phieuDatDuocChon.getMaKhachHang());
            } catch (Exception ignored) {
                khachHangMacDinh = null;
            }
        }

        String maHoaDon = taoMaHoaDon();
        double tongTien = 0d;
        for (HoaDonCreateItemPayload item : items) {
            tongTien += item.getSoLuong() * item.getDonGia();
        }

        XacNhanLapHoaDonFrame frame = new XacNhanLapHoaDonFrame(
                hoaDonClientController,
                khachHangClientController,
                khuyenMaiClientController,
                items,
                maHoaDon,
                maNhanVien,
                tongTien,
                hoaDonCallback,
                phieuDatDuocChon.getMaPhieuDat(),
                khachHangMacDinh
        );
        frame.setVisible(true);
        dispose();
    }

    private String taoMaHoaDon() {
        try {
            String maHoaDonCu = hoaDonClientController.getMaHoaDonGanNhat();
            if (maHoaDonCu == null || maHoaDonCu.isBlank() || !maHoaDonCu.startsWith("HD")) {
                throw new IllegalArgumentException("Mã hóa đơn không hợp lệ");
            }
            int number = Integer.parseInt(maHoaDonCu.substring(2)) + 1;
            return String.format("HD%05d", number);
        } catch (Exception ex) {
            return "HD" + System.currentTimeMillis();
        }
    }
}
