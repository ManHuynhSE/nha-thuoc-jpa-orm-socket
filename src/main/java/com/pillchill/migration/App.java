package com.pillchill.migration;

import com.pillchill.migration.db.JPAUtil;
import com.pillchill.migration.repository.impl.TaiKhoanRepository;
import com.pillchill.migration.repository.impl.ThuocRepository;

public class App {
    public static void main(String[] args) {
        TaiKhoanRepository taiKhoanRepository = new TaiKhoanRepository();
        ThuocRepository thuocRepository = new ThuocRepository();

        System.out.println("Tai khoan active: " + taiKhoanRepository.findAllActive().size());
        System.out.println("Thuoc active: " + thuocRepository.countActive());
        JPAUtil.shutdown();
    }
}
