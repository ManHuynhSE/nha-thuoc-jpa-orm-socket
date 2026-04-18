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

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"nhanVien"})
@Entity
@Table(name = "PhieuNhapThuoc")
public class PhieuNhapThuoc {
    @Id
    @Column(name = "maPhieuNhapThuoc", length = 50, nullable = false)
    private String maPhieuNhapThuoc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maNV")
    private NhanVien nhanVien;

    @Column(name = "ngayNhap")
    private LocalDate ngayNhap;

    @Column(name = "isActive")
    private boolean isActive;
}
