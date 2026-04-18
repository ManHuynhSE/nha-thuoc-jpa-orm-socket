package com.pillchill.migration.entity;

import com.pillchill.migration.entity.id.ChiTietPhieuDatId;

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
@ToString(exclude = {"phieuDat", "thuoc", "loThuoc"})
@Entity
@Table(name = "ChiTietPhieuDat")
public class ChiTietPhieuDat {
    @EmbeddedId
    private ChiTietPhieuDatId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("maPhieuDat")
    @JoinColumn(name = "maPhieuDat")
    private PhieuDat phieuDat;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("maThuoc")
    @JoinColumn(name = "maThuoc")
    private Thuoc thuoc;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("maLo")
    @JoinColumn(name = "maLo")
    private LoThuoc loThuoc;

    @Column(name = "tenThuoc", length = 100)
    private String tenThuoc;

    @Column(name = "soLuong")
    private Integer soLuong;

    @Column(name = "isActive")
    private boolean isActive;
}
