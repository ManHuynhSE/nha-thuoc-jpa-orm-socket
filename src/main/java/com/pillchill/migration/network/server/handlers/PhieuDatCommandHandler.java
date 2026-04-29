package com.pillchill.migration.network.server.handlers;

import com.pillchill.migration.migration.PhieuDatJpaDAO;
import com.pillchill.migration.network.communication.PhieuDatFilterPayload;
import com.pillchill.migration.network.communication.Request;
import com.pillchill.migration.network.communication.Response;
import com.pillchill.migration.network.communication.command.PhieuDatCM;
import com.pillchill.migration.network.server.CommandHandler;

public class PhieuDatCommandHandler implements CommandHandler {
    private final PhieuDatJpaDAO phieuDatJpaDAO;

    public PhieuDatCommandHandler(PhieuDatJpaDAO phieuDatJpaDAO) {
        this.phieuDatJpaDAO = phieuDatJpaDAO;
    }

    @Override
    public Response handle(Request request) {
        if (request.getSessionUserId() == null || request.getSessionUserId().isBlank()) {
            return Response.error("Bạn chưa đăng nhập");
        }
        String action = request.getCommand().substring("PHIEU_DAT.".length());
        PhieuDatCM cmd;
        try {
            cmd = PhieuDatCM.valueOf(action);
        } catch (IllegalArgumentException e) {
            return Response.error("Command phiếu đặt không hỗ trợ: " + action);
        }

        switch (cmd) {
            case LIST_ALL -> {
                return Response.success(phieuDatJpaDAO.getAllPhieuDatViews(), "Tải danh sách phiếu đặt thành công");
            }
            case LIST_BY_MONTH_YEAR -> {
                if (!(request.getData() instanceof PhieuDatFilterPayload payload)
                        || payload.getMonth() == null || payload.getYear() == null) {
                    return Response.error("Payload lọc phiếu đặt không hợp lệ");
                }
                return Response.success(
                        phieuDatJpaDAO.getPhieuDatViewsByMonthYear(payload.getMonth(), payload.getYear()),
                        "Tải danh sách phiếu đặt theo tháng/năm thành công"
                );
            }
            case LIST_CHI_TIET -> {
                if (!(request.getData() instanceof String maPhieuDat) || maPhieuDat.isBlank()) {
                    return Response.error("Mã phiếu đặt không hợp lệ");
                }
                return Response.success(
                        phieuDatJpaDAO.getChiTietPhieuDatByMaPhieuDat(maPhieuDat),
                        "Tải chi tiết phiếu đặt thành công"
                );
            }
        }
        return Response.error("Command phiếu đặt không hỗ trợ");
    }
}
