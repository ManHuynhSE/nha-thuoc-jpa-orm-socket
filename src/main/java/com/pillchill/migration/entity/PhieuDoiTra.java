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
@ToString(exclude = {"nhanVien", "khachHang"})
@Entity
@Table(name = "PhieuDoiTra")
public class PhieuDoiTra {
    @Id
    @Column(name = "maPhieuDoiTra", length = 50, nullable = false)
    private String maPhieuDoiTra;

    @Column(name = "ngayDoiTra")
    private LocalDate ngayDoiTra;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maNV")
    private NhanVien nhanVien;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maKH")
    private KhachHang khachHang;

    @Column(name = "isActive")
    private boolean isActive;
}
