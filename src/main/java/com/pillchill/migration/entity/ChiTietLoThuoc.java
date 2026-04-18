package com.pillchill.migration.entity;

import com.pillchill.migration.entity.id.ChiTietLoThuocId;

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

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"loThuoc", "thuoc"})
@Entity
@Table(name = "ChiTietLoThuoc")
public class ChiTietLoThuoc {
    @EmbeddedId
    private ChiTietLoThuocId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("maLo")
    @JoinColumn(name = "maLo")
    private LoThuoc loThuoc;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("maThuoc")
    @JoinColumn(name = "maThuoc")
    private Thuoc thuoc;

    @Column(name = "ngaySanXuat")
    private LocalDate ngaySanXuat;

    @Column(name = "hanSuDung")
    private LocalDate hanSuDung;

    @Column(name = "soLuong")
    private int soLuong;

    @Column(name = "giaNhap")
    private double giaNhap;

    @Column(name = "isActive")
    private boolean isActive;
}
