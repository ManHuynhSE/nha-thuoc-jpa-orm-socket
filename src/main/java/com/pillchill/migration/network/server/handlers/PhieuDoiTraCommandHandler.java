package com.pillchill.migration.network.server.handlers;

import com.pillchill.migration.migration.PhieuDoiTraJpaDAO;
import com.pillchill.migration.network.communication.PhieuDoiTraFilterPayload;
import com.pillchill.migration.network.communication.Request;
import com.pillchill.migration.network.communication.Response;
import com.pillchill.migration.network.communication.command.PhieuDoiTraCM;
import com.pillchill.migration.network.server.CommandHandler;

public class PhieuDoiTraCommandHandler implements CommandHandler {
    private final PhieuDoiTraJpaDAO phieuDoiTraJpaDAO;

    public PhieuDoiTraCommandHandler(PhieuDoiTraJpaDAO phieuDoiTraJpaDAO) {
        this.phieuDoiTraJpaDAO = phieuDoiTraJpaDAO;
    }

    @Override
    public Response handle(Request request) {
        if (request.getSessionUserId() == null || request.getSessionUserId().isBlank()) {
            return Response.error("Bạn chưa đăng nhập");
        }
        String action = request.getCommand().substring("PHIEU_DOI_TRA.".length());
        PhieuDoiTraCM cmd;
        try {
            cmd = PhieuDoiTraCM.valueOf(action);
        } catch (IllegalArgumentException e) {
            return Response.error("Command phiếu đổi trả không hỗ trợ: " + action);
        }

        switch (cmd) {
            case LIST_ALL -> {
                return Response.success(phieuDoiTraJpaDAO.getAllPhieuDoiTraViews(), "Tải danh sách phiếu đổi trả thành công");
            }
            case LIST_BY_MONTH_YEAR -> {
                if (!(request.getData() instanceof PhieuDoiTraFilterPayload payload)
                        || payload.getMonth() == null || payload.getYear() == null) {
                    return Response.error("Payload lọc phiếu đổi trả không hợp lệ");
                }
                return Response.success(
                        phieuDoiTraJpaDAO.getPhieuDoiTraViewsByMonthYear(payload.getMonth(), payload.getYear()),
                        "Tải danh sách phiếu đổi trả theo tháng/năm thành công"
                );
            }
            case LIST_CHI_TIET -> {
                if (!(request.getData() instanceof String maPhieuDoiTra) || maPhieuDoiTra.isBlank()) {
                    return Response.error("Mã phiếu đổi trả không hợp lệ");
                }
                return Response.success(
                        phieuDoiTraJpaDAO.getChiTietPhieuDoiTraByMaPhieuDoiTra(maPhieuDoiTra),
                        "Tải chi tiết phiếu đổi trả thành công"
                );
            }
        }
        return Response.error("Command phiếu đổi trả không hỗ trợ");
    }
}
