package com.pillchill.migration.network.server.handlers;

import com.pillchill.migration.entity.KhuyenMai;
import com.pillchill.migration.migration.KhuyenMaiJpaDAO;
import com.pillchill.migration.network.communication.Request;
import com.pillchill.migration.network.communication.Response;
import com.pillchill.migration.network.communication.command.KhuyenMaiCM;
import com.pillchill.migration.network.server.CommandHandler;

public class KhuyenMaiCommandHandler implements CommandHandler {
    private final KhuyenMaiJpaDAO khuyenMaiJpaDAO;

    public KhuyenMaiCommandHandler(KhuyenMaiJpaDAO khuyenMaiJpaDAO) {
        this.khuyenMaiJpaDAO = khuyenMaiJpaDAO;
    }

    @Override
    public Response handle(Request request) {
        if (request.getSessionUserId() == null || request.getSessionUserId().isBlank()) {
            return Response.error("Bạn chưa đăng nhập");
        }
        String action = request.getCommand().substring("KHUYEN_MAI.".length());
        KhuyenMaiCM cmd;
        try {
            cmd = KhuyenMaiCM.valueOf(action);
        } catch (IllegalArgumentException e) {
            return Response.error("Command khuyến mãi không hỗ trợ: " + action);
        }

        switch (cmd) {
            case LIST_ALL -> {
                return Response.success(khuyenMaiJpaDAO.getAllKhuyenMai(), "Tải danh sách khuyến mãi thành công");
            }
            case FIND_BY_MA -> {
                if (!(request.getData() instanceof String maKM) || maKM.isBlank()) {
                    return Response.error("Mã khuyến mãi không hợp lệ");
                }
                KhuyenMai khuyenMai = khuyenMaiJpaDAO.findByMa(maKM);
                return Response.success(khuyenMai, "Tìm khuyến mãi thành công");
            }
            case VALIDATE -> {
                if (!(request.getData() instanceof String maKM) || maKM.isBlank()) {
                    return Response.error("Mã khuyến mãi không hợp lệ");
                }
                boolean isValid = khuyenMaiJpaDAO.isValid(maKM);
                return Response.success(isValid, "Kiểm tra khuyến mãi thành công");
            }
            case CREATE -> {
                return Response.error("Chức năng tạo khuyến mãi chưa được hỗ trợ");
            }
            case UPDATE -> {
                return Response.error("Chức năng cập nhật khuyến mãi chưa được hỗ trợ");
            }
            case DELETE -> {
                return Response.error("Chức năng xóa khuyến mãi chưa được hỗ trợ");
            }
        }
        return Response.error("Command khuyến mãi không hỗ trợ");
    }
}
