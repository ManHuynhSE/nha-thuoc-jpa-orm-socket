package app.Entity;

public class TaiKhoan {
    private String maNV;
    private String matKhau;
    private boolean isActive;

    public TaiKhoan() {
    }

    public TaiKhoan(String maNV, String matKhau, boolean isActive) {
        this.maNV = maNV;
        this.matKhau = matKhau;
        this.isActive = isActive;
    }

    public String getMaNV() {
        return maNV;
    }

    public String getMatKhau() {
        return matKhau;
    }

    public boolean isIsActive() {
        return isActive;
    }
}
