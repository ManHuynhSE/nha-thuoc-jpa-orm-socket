package com.pillchill.migration.dto;

import com.pillchill.migration.entity.ChucVu;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NhanVienDTO implements Serializable {
    private String maNV;
    private String tenNV;
    private String chucVu;
    private String soDienThoai;
    private boolean isActive;
}
