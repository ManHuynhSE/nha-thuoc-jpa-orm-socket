package com.pillchill.migration.network.server.handlers;

import com.pillchill.migration.dto.CreatePhieuDatCommand;
import com.pillchill.migration.dto.PhieuDatItemCommand;
import com.pillchill.migration.migration.PhieuDatJpaDAO;
import com.pillchill.migration.network.communication.PhieuDatCreateItemPayload;
import com.pillchill.migration.network.communication.PhieuDatCreatePayload;
import com.pillchill.migration.network.communication.PhieuDatFilterPayload;
import com.pillchill.migration.network.communication.Request;
import com.pillchill.migration.network.communication.Response;
import com.pillchill.migration.network.communication.command.PhieuDatCM;
import com.pillchill.migration.network.server.CommandHandler;

import java.util.ArrayList;
import java.util.List;

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

        try {
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
                case CREATE -> {
                    if (!(request.getData() instanceof PhieuDatCreatePayload payload)
                            || payload.getItems() == null
                            || payload.getItems().isEmpty()) {
                        return Response.error("Payload tạo phiếu đặt không hợp lệ");
                    }
                    List<PhieuDatItemCommand> items = new ArrayList<>();
                    for (PhieuDatCreateItemPayload item : payload.getItems()) {
                        if (item == null) {
                            return Response.error("Chi tiết phiếu đặt không hợp lệ");
                        }
                        items.add(new PhieuDatItemCommand(item.getMaThuoc(), item.getMaLo(), item.getSoLuong()));
                    }
                    CreatePhieuDatCommand command = new CreatePhieuDatCommand(
                            payload.getMaKhachHang(),
                            payload.getGhiChu(),
                            items
                    );
                    String maPhieuDat = phieuDatJpaDAO.createPhieuDat(command, request.getSessionUserId());
                    return Response.success(maPhieuDat, "Tạo phiếu đặt thành công");
                }
            }
        } catch (RuntimeException ex) {
            return Response.error(ex.getMessage());
        }
        return Response.error("Command phiếu đặt không hỗ trợ");
    }
}
