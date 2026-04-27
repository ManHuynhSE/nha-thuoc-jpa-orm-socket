package com.pillchill.migration.network.server.handlers;

import com.pillchill.migration.migration.HoaDonJpaDAO;
import com.pillchill.migration.network.communication.HoaDonFilterPayload;
import com.pillchill.migration.network.communication.Request;
import com.pillchill.migration.network.communication.Response;
import com.pillchill.migration.network.communication.command.HoaDonCM;
import com.pillchill.migration.network.server.CommandHandler;

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
        }
        return Response.error("Command hóa đơn không hỗ trợ");
    }
}
