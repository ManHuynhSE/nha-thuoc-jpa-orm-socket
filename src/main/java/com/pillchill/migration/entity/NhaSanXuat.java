package com.pillchill.migration.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "NhaSanXuat")
public class NhaSanXuat {
    @Id
    @Column(name = "maNSX", length = 50, nullable = false)
    private String maNSX;

    @Column(name = "tenNSX", length = 100)
    private String tenNSX;

    @Column(name = "diaChiNSX", length = 200)
    private String diaChiNSX;

    @Column(name = "soDienThoai", length = 20)
    private String soDienThoai;

    @Column(name = "isActive")
    private boolean isActive;
}
