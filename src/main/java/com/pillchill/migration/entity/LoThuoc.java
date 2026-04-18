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
@ToString(exclude = {"nhaSanXuat"})
@Entity
@Table(name = "LoThuoc")
public class LoThuoc {
    @Id
    @Column(name = "maLo", length = 50, nullable = false)
    private String maLo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maNSX")
    private NhaSanXuat nhaSanXuat;

    @Column(name = "isActive")
    private boolean isActive;
}
