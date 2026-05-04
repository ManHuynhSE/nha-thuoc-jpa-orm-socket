package com.pillchill.migration.network.server.handlers;

import com.pillchill.migration.dto.CreatePhieuDoiTraCommand;
import com.pillchill.migration.dto.PhieuDoiTraItemCommand;
import com.pillchill.migration.migration.PhieuDoiTraJpaDAO;
import com.pillchill.migration.network.communication.PhieuDoiTraCreateItemPayload;
import com.pillchill.migration.network.communication.PhieuDoiTraCreatePayload;
import com.pillchill.migration.network.communication.PhieuDoiTraFilterPayload;
import com.pillchill.migration.network.communication.Request;
import com.pillchill.migration.network.communication.Response;
import com.pillchill.migration.network.communication.command.PhieuDoiTraCM;
import com.pillchill.migration.network.server.CommandHandler;

import java.util.ArrayList;
import java.util.List;

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

        try {
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
                case CREATE -> {
                    if (!(request.getData() instanceof PhieuDoiTraCreatePayload payload)
                            || payload.getItems() == null
                            || payload.getItems().isEmpty()) {
                        return Response.error("Payload tạo phiếu đổi trả không hợp lệ");
                    }
                    List<PhieuDoiTraItemCommand> items = new ArrayList<>();
                    for (PhieuDoiTraCreateItemPayload item : payload.getItems()) {
                        if (item == null) {
                            return Response.error("Chi tiết phiếu đổi trả không hợp lệ");
                        }
                        items.add(new PhieuDoiTraItemCommand(
                                item.getMaThuoc(),
                                item.getMaLo(),
                                item.getSoLuong(),
                                item.getDonGia(),
                                item.getLyDo()
                        ));
                    }
                    CreatePhieuDoiTraCommand command = new CreatePhieuDoiTraCommand(payload.getMaHoaDon(), items);
                    String maPhieuDoiTra = phieuDoiTraJpaDAO.createPhieuDoiTra(command, request.getSessionUserId());
                    return Response.success(maPhieuDoiTra, "Tạo phiếu đổi trả thành công");
                }
            }
        } catch (RuntimeException ex) {
            return Response.error(ex.getMessage());
        }
        return Response.error("Command phiếu đổi trả không hỗ trợ");
    }
}
