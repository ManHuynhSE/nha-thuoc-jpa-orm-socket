package com.pillchill.migration;

import com.formdev.flatlaf.FlatLightLaf;
import com.pillchill.migration.db.JPAUtil;
import com.pillchill.migration.gui.DangNhapFrame;
import com.pillchill.migration.repository.impl.TaiKhoanRepository;
import com.pillchill.migration.repository.impl.ThuocRepository;

public class App {
    public static void main(String[] args) {
        FlatLightLaf.setup();
        DangNhapFrame dangNhapFrame = new DangNhapFrame("DESKTOP-PRP7OQL",9999);
    }
}
