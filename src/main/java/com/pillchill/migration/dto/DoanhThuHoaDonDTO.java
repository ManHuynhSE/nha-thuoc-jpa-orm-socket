package com.pillchill.migration.dto;

import java.io.Serializable;

public class DoanhThuHoaDonDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    int ngay;
    double doanhThu;
    public DoanhThuHoaDonDTO(int ngay,	double doanhThu) {
        super();
        this.ngay = ngay;
        this.doanhThu = doanhThu;
    }
    public DoanhThuHoaDonDTO() {
        super();

    }
    public int getNgay() {
        return ngay;
    }
    public void setNgay(int ngay) {
        this.ngay = ngay;
    }
    public double getDoanhThu() {
        return doanhThu;
    }
    public void setDoanhThu(double doanhThu) {
        this.doanhThu = doanhThu;
    }
}