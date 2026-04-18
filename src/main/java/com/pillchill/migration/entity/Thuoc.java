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

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"donVi", "nhaSanXuat"})
@Entity
@Table(name = "Thuoc")
public class Thuoc {
    @Id
    @Column(name = "maThuoc", length = 50, nullable = false)
    private String maThuoc;

    @Column(name = "tenThuoc", length = 100)
    private String tenThuoc;

    @Column(name = "soLuongTon")
    private int soLuongTon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maDonVi")
    private DonVi donVi;

    @Column(name = "soLuongToiThieu")
    private Integer soLuongToiThieu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maNSX")
    private NhaSanXuat nhaSanXuat;

    @Column(name = "isActive")
    private boolean isActive;
}
