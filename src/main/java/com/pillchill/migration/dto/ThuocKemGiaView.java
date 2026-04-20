package com.pillchill.migration.dto;

import lombok.*;

import java.io.Serializable;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ThuocKemGiaView implements Serializable{
    private  String maThuoc;
    private String tenThuoc;
    private int soLuongTon;
    private double giaBan;
    private String donVi;
    private int soLuongToiThieu;
    private String maNSX;
    private boolean isActive;
}
