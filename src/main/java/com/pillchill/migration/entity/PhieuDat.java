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
@Table(name = "PhieuDat")
public class PhieuDat {
    @Id
    @Column(name = "maPhieuDat", length = 50, nullable = false)
    private String maPhieuDat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maNV")
    private NhanVien nhanVien;

    @Column(name = "ngayDat")
    private LocalDate ngayDat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maKH")
    private KhachHang khachHang;

    @Column(name = "ghiChu", length = 200)
    private String ghiChu;

    @Column(name = "isReceived")
    private boolean isReceived;

    @Column(name = "isActive")
    private boolean isActive;
}
