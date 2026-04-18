package com.pillchill.migration.entity;

import com.pillchill.migration.entity.id.ChiTietPhieuNhapId;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"phieuNhapThuoc", "loThuoc", "thuoc"})
@Entity
@Table(name = "ChiTietPhieuNhap")
public class ChiTietPhieuNhap {
    @EmbeddedId
    private ChiTietPhieuNhapId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("maPhieuNhapThuoc")
    @JoinColumn(name = "maPhieuNhapThuoc")
    private PhieuNhapThuoc phieuNhapThuoc;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("maLo")
    @JoinColumn(name = "maLo")
    private LoThuoc loThuoc;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("maThuoc")
    @JoinColumn(name = "maThuoc")
    private Thuoc thuoc;

    @Column(name = "soLuong")
    private Integer soLuong;

    @Column(name = "donGia")
    private Double donGia;

    @Column(name = "isActive")
    private boolean isActive;
}
