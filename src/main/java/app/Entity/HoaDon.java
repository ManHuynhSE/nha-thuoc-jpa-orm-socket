package app.Entity;

import java.util.Date;

public class HoaDon {
    private String maHoaDon;
    private Date ngayBan;
    private String ghiChu;
    private String maNV;
    private String maKH;
    private String maKM;
    private double giaTriThue;
    private String tenLoaiThue;
    private boolean isActive;

    public HoaDon() {
    }

    public HoaDon(String maHoaDon, Date ngayBan, String ghiChu, String maNV, String maKH, String maKM,
                  double giaTriThue, String tenLoaiThue, boolean isActive) {
        this.maHoaDon = maHoaDon;
        this.ngayBan = ngayBan;
        this.ghiChu = ghiChu;
        this.maNV = maNV;
        this.maKH = maKH;
        this.maKM = maKM;
        this.giaTriThue = giaTriThue;
        this.tenLoaiThue = tenLoaiThue;
        this.isActive = isActive;
    }

    public String getMaHoaDon() {
        return maHoaDon;
    }

    public Date getNgayBan() {
        return ngayBan;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public String getMaNV() {
        return maNV;
    }

    public String getMaKH() {
        return maKH;
    }

    public String getMaKM() {
        return maKM;
    }

    public double getGiaTriThue() {
        return giaTriThue;
    }

    public String getTenLoaiThue() {
        return tenLoaiThue;
    }

    public boolean isIsActive() {
        return isActive;
    }
}
