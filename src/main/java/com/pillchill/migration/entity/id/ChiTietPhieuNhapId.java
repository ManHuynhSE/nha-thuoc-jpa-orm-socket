package com.pillchill.migration.entity.id;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ChiTietPhieuNhapId implements Serializable {
    @Column(name = "maPhieuNhapThuoc", length = 50, nullable = false)
    private String maPhieuNhapThuoc;

    @Column(name = "maLo", length = 50, nullable = false)
    private String maLo;

    @Column(name = "maThuoc", length = 50, nullable = false)
    private String maThuoc;

}
