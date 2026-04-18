package com.pillchill.migration.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
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
@ToString(exclude = {"nhanVien"})
@Entity
@Table(name = "TaiKhoan")
public class TaiKhoan {
    @Id
    @Column(name = "maNV", length = 50, nullable = false)
    private String maNV;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "maNV")
    private NhanVien nhanVien;

    @Column(name = "matKhau", length = 50)
    private String matKhau;

    @Column(name = "isActive")
    private boolean isActive;
}
