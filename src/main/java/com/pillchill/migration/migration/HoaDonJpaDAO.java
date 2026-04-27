package com.pillchill.migration.migration;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;

import com.pillchill.migration.dto.ChiTietHoaDonView;
import com.pillchill.migration.dto.CreateHoaDonCommand;
import com.pillchill.migration.dto.HoaDonView;
import com.pillchill.migration.entity.HoaDon;
import com.pillchill.migration.service.IHoaDonService;
import com.pillchill.migration.service.impl.HoaDonService;

public class HoaDonJpaDAO {
    private final IHoaDonService hoaDonService;

    public HoaDonJpaDAO(IHoaDonService hoaDonService) {
        this.hoaDonService = hoaDonService;
    }

    public HoaDonJpaDAO() {
        this.hoaDonService = new HoaDonService();
    }

    public ArrayList<HoaDon> findHoaDonByDateRange(Date fromDate, Date toDate) {
        LocalDate from = new java.sql.Date(fromDate.getTime()).toLocalDate();
        LocalDate to = new java.sql.Date(toDate.getTime()).toLocalDate();
        return new ArrayList<>(hoaDonService.findHoaDonByDateRange(from, to));
    }

    public boolean addHoaDon(CreateHoaDonCommand command) {
        hoaDonService.createHoaDon(command);
        return true;
    }

    public ArrayList<HoaDonView> getAllHoaDonViews() {
        return new ArrayList<>(hoaDonService.getAllHoaDonViews());
    }

    public ArrayList<HoaDonView> getHoaDonViewsByMonthYear(int month, int year) {
        return new ArrayList<>(hoaDonService.getHoaDonViewsByMonthYear(month, year));
    }

    public ArrayList<ChiTietHoaDonView> getChiTietHoaDonByMaHoaDon(String maHoaDon) {
        return new ArrayList<>(hoaDonService.getChiTietHoaDonByMaHoaDon(maHoaDon));
    }

    public HoaDon getHoaDonById(String id) {
        return hoaDonService.getHoaDonById(id).orElse(null);
    }
}
