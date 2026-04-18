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
@ToString(exclude = {"nhanVien", "khachHang", "khuyenMai"})
@Entity
@Table(name = "HoaDon")
public class HoaDon {
    @Id
    @Column(name = "maHoaDon", length = 50, nullable = false)
    private String maHoaDon;

    @Column(name = "ngayBan")
    private LocalDate ngayBan;

    @Column(name = "ghiChu", length = 200)
    private String ghiChu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maNV")
    private NhanVien nhanVien;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maKH")
    private KhachHang khachHang;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maKM")
    private KhuyenMai khuyenMai;

    @Column(name = "giaTriThue")
    private double giaTriThue;

    @Column(name = "tenLoaiThue", length = 50)
    private String tenLoaiThue;

    @Column(name = "isActive")
    private boolean isActive;
}
