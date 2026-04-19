package app.DTO;

public class ThuocKemGiaDTO {
    private String maThuoc;
    private String tenThuoc;
    private int soLuongTon;
    private double giaBan;
    private String donVi;
    private int soLuongToiThieu;
    private String maNSX;
    private boolean isActive;

    public ThuocKemGiaDTO() {
    }

    public ThuocKemGiaDTO(String maThuoc, String tenThuoc, int soLuongTon, double giaBan,
                          String donVi, int soLuongToiThieu, String maNSX, boolean isActive) {
        this.maThuoc = maThuoc;
        this.tenThuoc = tenThuoc;
        this.soLuongTon = soLuongTon;
        this.giaBan = giaBan;
        this.donVi = donVi;
        this.soLuongToiThieu = soLuongToiThieu;
        this.maNSX = maNSX;
        this.isActive = isActive;
    }

    public String getMaThuoc() {
        return maThuoc;
    }

    public String getTenThuoc() {
        return tenThuoc;
    }

    public int getSoLuongTon() {
        return soLuongTon;
    }

    public double getGiaBan() {
        return giaBan;
    }

    public String getDonVi() {
        return donVi;
    }

    public int getSoLuongToiThieu() {
        return soLuongToiThieu;
    }

    public String getMaNSX() {
        return maNSX;
    }

    public boolean isIsActive() {
        return isActive;
    }
}
