package com.pillchill.migration.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"chucVu"})
@Entity
@Table(name = "NhanVien")
public class NhanVien implements Serializable {
    @Id
    @Column(name = "maNV", length = 50, nullable = false)
    private String maNV;

    @Column(name = "tenNV", length = 100)
    private String tenNV;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maChucVu")
    private ChucVu chucVu;

    @Column(name = "soDienThoai", length = 20)
    private String soDienThoai;

    @Column(name = "isActive")
    private boolean isActive;
}
