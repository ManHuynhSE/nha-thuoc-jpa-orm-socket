package com.pillchill.migration.entity;

import com.pillchill.migration.entity.id.ChiTietHoaDonId;

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
@ToString(exclude = {"hoaDon", "thuoc", "loThuoc"})
@Entity
@Table(name = "ChiTietHoaDon")
public class ChiTietHoaDon {
    @EmbeddedId
    private ChiTietHoaDonId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("maHoaDon")
    @JoinColumn(name = "maHoaDon")
    private HoaDon hoaDon;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("maThuoc")
    @JoinColumn(name = "maThuoc")
    private Thuoc thuoc;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("maLo")
    @JoinColumn(name = "maLo")
    private LoThuoc loThuoc;

    @Column(name = "soLuong")
    private int soLuong;

    @Column(name = "donGia")
    private float donGia;

    @Column(name = "isActive")
    private boolean isActive;
}
