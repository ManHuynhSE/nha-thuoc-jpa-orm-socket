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
@Table(name = "BangGia")
public class BangGia {
    @Id
    @Column(name = "maBangGia", length = 50, nullable = false)
    private String maBangGia;

    @Column(name = "tenBangGia", length = 100)
    private String tenBangGia;

    @Column(name = "loaiGia", length = 50)
    private String loaiGia;

    @Column(name = "ngayApDung")
    private LocalDate ngayApDung;

    @Column(name = "ngayKetThuc")
    private LocalDate ngayKetThuc;

    @Column(name = "trangThai", length = 50)
    private String trangThai;

    @Column(name = "ghiChu", length = 100)
    private String ghiChu;

    @Column(name = "doUuTien")
    private Integer doUuTien;

    @Column(name = "isActive")
    private boolean isActive;
}
