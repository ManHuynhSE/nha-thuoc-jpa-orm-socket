package com.pillchill.migration.migration;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;

import com.pillchill.migration.dto.CreateHoaDonCommand;
import com.pillchill.migration.entity.HoaDon;
import com.pillchill.migration.repository.IHoaDonRepository;
import com.pillchill.migration.repository.impl.HoaDonRepository;
import com.pillchill.migration.service.IHoaDonService;
import com.pillchill.migration.service.impl.HoaDonService;

public class HoaDonJpaDAO {
    private final IHoaDonRepository hoaDonRepository;
    private final IHoaDonService hoaDonService;

    public HoaDonJpaDAO(IHoaDonRepository hoaDonRepository, IHoaDonService hoaDonService) {
        this.hoaDonRepository = hoaDonRepository;
        this.hoaDonService = hoaDonService;
    }

    public HoaDonJpaDAO() {
        this.hoaDonRepository = new HoaDonRepository();
        this.hoaDonService = new HoaDonService();
    }

    public ArrayList<HoaDon> findHoaDonByDateRange(Date fromDate, Date toDate) {
        LocalDate from = new java.sql.Date(fromDate.getTime()).toLocalDate();
        LocalDate to = new java.sql.Date(toDate.getTime()).toLocalDate();
        return new ArrayList<>(hoaDonRepository.findByDateRange(from, to));
    }

    public boolean addHoaDon(CreateHoaDonCommand command) {
        hoaDonService.createHoaDon(command);
        return true;
    }

    public HoaDon getHoaDonById(String id) {
        return hoaDonRepository.findById(id).orElse(null);
    }
}
