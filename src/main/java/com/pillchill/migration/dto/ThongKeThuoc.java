package com.pillchill.migration.dto;

import java.io.Serializable;

/**
 * Entity class representing medicine sales statistics
 */
public class ThongKeThuoc implements Serializable {
    // Thông tin cơ bản về thuốc
    private String maThuoc;
    private String tenThuoc;
    private String donViTinh;  // hộp, viên, chai, gói,...

    // Thống kê số lượng
    private int soLuongBan;          // Tổng số lượng bán ra
    private int soLanXuatHien;       // Số lần xuất hiện trong hóa đơn
    private int tongSoHoaDon;        // Tổng số hóa đơn trong kỳ (để tính tỷ lệ)

    // Thống kê doanh thu
    private double doanhThu;         // Tổng doanh thu từ thuốc này
    private double giaTriTrungBinh;  // Giá trung bình/đơn vị

    // Thống kê so sánh
    private double tyLeDongGop;      // % trong tổng số lượng bán
    private int thuHang;             // Thứ hạng trong danh sách

    // Thông tin bổ sung (optional)
    private String nhomThuoc;        // Kháng sinh, Vitamin, OTC,...
    private int tonKhoHienTai;       // Số lượng còn trong kho

    // Constructor rỗng
    public ThongKeThuoc() {
    }

    // Constructor đầy đủ
    public ThongKeThuoc(String maThuoc, String tenThuoc, String donViTinh,
                        int soLuongBan, int soLanXuatHien, int tongSoHoaDon,
                        double doanhThu, double giaTriTrungBinh,
                        double tyLeDongGop, int thuHang) {
        this.maThuoc = maThuoc;
        this.tenThuoc = tenThuoc;
        this.donViTinh = donViTinh;
        this.soLuongBan = soLuongBan;
        this.soLanXuatHien = soLanXuatHien;
        this.tongSoHoaDon = tongSoHoaDon;
        this.doanhThu = doanhThu;
        this.giaTriTrungBinh = giaTriTrungBinh;
        this.tyLeDongGop = tyLeDongGop;
        this.thuHang = thuHang;
    }

    // Constructor cơ bản (dùng khi lấy từ DB)
    public ThongKeThuoc(String maThuoc, String tenThuoc, int soLuongBan, double doanhThu) {
        this.maThuoc = maThuoc;
        this.tenThuoc = tenThuoc;
        this.soLuongBan = soLuongBan;
        this.doanhThu = doanhThu;
    }

    // Constructor dùng cho JPA Query (vì SUM thường trả về Long và Double)
    public ThongKeThuoc(String maThuoc, String tenThuoc, Long soLuongBan, Double doanhThu) {
        this.maThuoc = maThuoc;
        this.tenThuoc = tenThuoc;
        this.soLuongBan = soLuongBan != null ? soLuongBan.intValue() : 0;
        this.doanhThu = doanhThu != null ? doanhThu : 0.0;
    }

    // Getters and Setters
    public String getMaThuoc() {
        return maThuoc;
    }

    public void setMaThuoc(String maThuoc) {
        this.maThuoc = maThuoc;
    }

    public String getTenThuoc() {
        return tenThuoc;
    }

    public void setTenThuoc(String tenThuoc) {
        this.tenThuoc = tenThuoc;
    }

    public String getDonViTinh() {
        return donViTinh;
    }

    public void setDonViTinh(String donViTinh) {
        this.donViTinh = donViTinh;
    }

    public int getSoLuongBan() {
        return soLuongBan;
    }

    public void setSoLuongBan(int soLuongBan) {
        this.soLuongBan = soLuongBan;
    }

    public int getSoLanXuatHien() {
        return soLanXuatHien;
    }

    public void setSoLanXuatHien(int soLanXuatHien) {
        this.soLanXuatHien = soLanXuatHien;
    }

    public int getTongSoHoaDon() {
        return tongSoHoaDon;
    }

    public void setTongSoHoaDon(int tongSoHoaDon) {
        this.tongSoHoaDon = tongSoHoaDon;
    }

    public double getDoanhThu() {
        return doanhThu;
    }

    public void setDoanhThu(double doanhThu) {
        this.doanhThu = doanhThu;
    }

    public double getGiaTriTrungBinh() {
        return giaTriTrungBinh;
    }

    public void setGiaTriTrungBinh(double giaTriTrungBinh) {
        this.giaTriTrungBinh = giaTriTrungBinh;
    }

    public double getTyLeDongGop() {
        return tyLeDongGop;
    }

    public void setTyLeDongGop(double tyLeDongGop) {
        this.tyLeDongGop = tyLeDongGop;
    }

    public int getThuHang() {
        return thuHang;
    }

    public void setThuHang(int thuHang) {
        this.thuHang = thuHang;
    }

    public String getNhomThuoc() {
        return nhomThuoc;
    }

    public void setNhomThuoc(String nhomThuoc) {
        this.nhomThuoc = nhomThuoc;
    }

    public int getTonKhoHienTai() {
        return tonKhoHienTai;
    }

    public void setTonKhoHienTai(int tonKhoHienTai) {
        this.tonKhoHienTai = tonKhoHienTai;
    }

    // Utility methods

    /**
     * Tính tỷ lệ xuất hiện của thuốc trong hóa đơn
     * @return % xuất hiện (VD: 34.6%)
     */
    public double getTyLeXuatHien() {
        if (tongSoHoaDon == 0) return 0;
        return (soLanXuatHien * 100.0) / tongSoHoaDon;
    }

    /**
     * Lấy text hiển thị tỷ lệ xuất hiện
     * @return VD: "18/52 đơn"
     */
    public String getTextXuatHien() {
        return soLanXuatHien + "/" + tongSoHoaDon + " đơn";
    }

    /**
     * Lấy text hiển thị số lượng bán
     * @return VD: "245 hộp"
     */
    public String getTextSoLuongBan() {
        return soLuongBan + " " + (donViTinh != null ? donViTinh : "");
    }

    @Override
    public String toString() {
        return "ThongKeThuoc{" +
                "maThuoc='" + maThuoc + '\'' +
                ", tenThuoc='" + tenThuoc + '\'' +
                ", soLuongBan=" + soLuongBan +
                ", doanhThu=" + doanhThu +
                ", tyLeDongGop=" + tyLeDongGop +
                ", thuHang=" + thuHang +
                '}';
    }
}