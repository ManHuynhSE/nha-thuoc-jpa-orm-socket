package com.pillchill.migration.network.server.handlers;

import com.pillchill.migration.dto.CreateHoaDonCommand;
import com.pillchill.migration.dto.HoaDonItemCommand;
import com.pillchill.migration.network.communication.HoaDonCreateItemPayload;
import com.pillchill.migration.network.communication.HoaDonCreatePayload;
import com.pillchill.migration.migration.HoaDonJpaDAO;
import com.pillchill.migration.network.communication.HoaDonFilterPayload;
import com.pillchill.migration.network.communication.Request;
import com.pillchill.migration.network.communication.Response;
import com.pillchill.migration.network.communication.command.HoaDonCM;
import com.pillchill.migration.network.server.CommandHandler;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class HoaDonCommandHandler implements CommandHandler {
    private final HoaDonJpaDAO hoaDonJpaDAO;

    public HoaDonCommandHandler(HoaDonJpaDAO hoaDonJpaDAO) {
        this.hoaDonJpaDAO = hoaDonJpaDAO;
    }

    @Override
    public Response handle(Request request) {
        if (request.getSessionUserId() == null || request.getSessionUserId().isBlank()) {
            return Response.error("Bạn chưa đăng nhập");
        }
        String action = request.getCommand().substring("HOA_DON.".length());
        HoaDonCM cmd;
        try {
            cmd = HoaDonCM.valueOf(action);
        } catch (IllegalArgumentException e) {
            return Response.error("Command hóa đơn không hỗ trợ: " + action);
        }

        try {
            switch (cmd) {
                case LIST_ALL -> {
                    return Response.success(hoaDonJpaDAO.getAllHoaDonViews(), "Tải danh sách hóa đơn thành công");
                }
                case LIST_BY_MONTH_YEAR -> {
                    if (!(request.getData() instanceof HoaDonFilterPayload payload)
                            || payload.getMonth() == null || payload.getYear() == null) {
                        return Response.error("Payload lọc hóa đơn không hợp lệ");
                    }
                    return Response.success(
                            hoaDonJpaDAO.getHoaDonViewsByMonthYear(payload.getMonth(), payload.getYear()),
                            "Tải danh sách hóa đơn theo tháng/năm thành công"
                    );
                }
                case LIST_CHI_TIET -> {
                    if (!(request.getData() instanceof String maHoaDon) || maHoaDon.isBlank()) {
                        return Response.error("Mã hóa đơn không hợp lệ");
                    }
                    return Response.success(
                            hoaDonJpaDAO.getChiTietHoaDonByMaHoaDon(maHoaDon),
                            "Tải chi tiết hóa đơn thành công"
                    );
                }
                case CREATE -> {
                    if (!(request.getData() instanceof HoaDonCreatePayload payload)
                            || payload.getItems() == null
                            || payload.getItems().isEmpty()) {
                        return Response.error("Payload tạo hóa đơn không hợp lệ");
                    }
                    String maHoaDon = payload.getMaHoaDon();
                    if (maHoaDon == null || maHoaDon.isBlank()) {
                        return Response.error("Mã hóa đơn không hợp lệ");
                    }

                    List<HoaDonItemCommand> items = new ArrayList<>();
                    for (HoaDonCreateItemPayload item : payload.getItems()) {
                        if (item == null
                                || item.getMaThuoc() == null
                                || item.getMaThuoc().isBlank()
                                || item.getMaLo() == null
                                || item.getMaLo().isBlank()
                                || item.getSoLuong() <= 0) {
                            return Response.error("Chi tiết hóa đơn không hợp lệ");
                        }
                        items.add(new HoaDonItemCommand(
                                item.getMaThuoc().trim(),
                                item.getMaLo().trim(),
                                item.getSoLuong(),
                                item.getDonGia()
                        ));
                    }

                    CreateHoaDonCommand command = new CreateHoaDonCommand(
                            maHoaDon.trim(),
                            LocalDate.now(),
                            payload.getGhiChu(),
                            request.getSessionUserId(),
                            payload.getMaKhachHang(),
                            payload.getMaKhuyenMai(),
                            0.10d,
                            "VAT 10%",
                            items
                    );
                    hoaDonJpaDAO.addHoaDon(command);
                    return Response.success(maHoaDon.trim(), "Tạo hóa đơn thành công");
                }
                case GET_LATEST -> {
                    return Response.success(
                            hoaDonJpaDAO.getLatestHoaDon(),
                            "Lấy mã hóa đơn mới nhất thành công"
                    );
                }
            }
        } catch (RuntimeException ex) {
            return Response.error(ex.getMessage());
        }
        return Response.error("Command hóa đơn không hỗ trợ");
    }
}
