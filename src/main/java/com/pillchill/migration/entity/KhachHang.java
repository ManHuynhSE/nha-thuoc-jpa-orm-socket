package com.pillchill.migration.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "KhachHang")
public class KhachHang implements Serializable {
    @Id
    @Column(name = "maKH", length = 50, nullable = false)
    private String maKH;

    @Column(name = "tenKH", length = 100)
    private String tenKH;

    @Column(name = "soDienThoai", length = 20)
    private String soDienThoai;

    @Column(name = "diemTichLuy")
    private Integer diemTichLuy;

    @Column(name = "isActive")
    private boolean isActive;
}
