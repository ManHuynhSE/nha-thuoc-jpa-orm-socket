package com.pillchill.migration.entity;

import com.pillchill.migration.entity.id.ChiTietBangGiaId;

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
@ToString(exclude = {"bangGia", "thuoc", "donVi"})
@Entity
@Table(name = "ChiTietBangGia")
public class ChiTietBangGia {
    @EmbeddedId
    private ChiTietBangGiaId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("maBangGia")
    @JoinColumn(name = "maBangGia")
    private BangGia bangGia;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("maThuoc")
    @JoinColumn(name = "maThuoc")
    private Thuoc thuoc;

    @Column(name = "donGia")
    private double donGia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maDonVi")
    private DonVi donVi;

    @Column(name = "isActive")
    private boolean isActive;
}
