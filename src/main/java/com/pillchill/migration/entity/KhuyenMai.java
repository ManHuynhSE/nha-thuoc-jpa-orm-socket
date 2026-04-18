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

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "KhuyenMai")
public class KhuyenMai {
    @Id
    @Column(name = "maKM", length = 50, nullable = false)
    private String maKM;

    @Column(name = "mucGiamGia")
    private double mucGiamGia;

    @Column(name = "ngayApDung")
    private LocalDate ngayApDung;

    @Column(name = "ngayKetThuc")
    private LocalDate ngayKetThuc;

    @Column(name = "isActive")
    private boolean isActive;
}
