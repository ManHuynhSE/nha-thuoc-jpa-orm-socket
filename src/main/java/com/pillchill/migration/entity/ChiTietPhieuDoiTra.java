package com.pillchill.migration.entity;

import com.pillchill.migration.entity.id.ChiTietPhieuDoiTraId;

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
@ToString(exclude = {"phieuDoiTra", "thuoc", "loThuoc"})
@Entity
@Table(name = "ChiTietPhieuDoiTra")
public class ChiTietPhieuDoiTra {
    @EmbeddedId
    private ChiTietPhieuDoiTraId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("maPhieuDoiTra")
    @JoinColumn(name = "maPhieuDoiTra")
    private PhieuDoiTra phieuDoiTra;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("maThuoc")
    @JoinColumn(name = "maThuoc")
    private Thuoc thuoc;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("maLo")
    @JoinColumn(name = "maLo")
    private LoThuoc loThuoc;

    @Column(name = "soLuong")
    private Integer soLuong;

    @Column(name = "donGia")
    private Double donGia;

    @Column(name = "lyDo", length = 200)
    private String lyDo;

    @Column(name = "isActive")
    private boolean isActive;
}
