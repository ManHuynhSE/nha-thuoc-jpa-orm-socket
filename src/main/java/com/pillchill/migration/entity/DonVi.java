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

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "DonVi")
public class DonVi implements Serializable {
    @Id
    @Column(name = "maDonVi", length = 50, nullable = false)
    private String maDonVi;

    @Column(name = "tenDonVi", length = 50)
    private String tenDonVi;

    @Column(name = "isActive")
    private boolean isActive;
}
