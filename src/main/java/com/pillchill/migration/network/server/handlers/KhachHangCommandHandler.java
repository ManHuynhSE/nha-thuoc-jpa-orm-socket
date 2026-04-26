package com.pillchill.migration.network.server.handlers;

import com.pillchill.migration.entity.KhachHang;
import com.pillchill.migration.migration.KhachHangJpaDAO;
import com.pillchill.migration.network.communication.Request;
import com.pillchill.migration.network.communication.Response;
import com.pillchill.migration.network.communication.KhachHangPayload;

import com.pillchill.migration.network.communication.command.KhachHangCM;
import com.pillchill.migration.network.server.CommandHandler;

public class KhachHangCommandHandler implements CommandHandler {
    private final KhachHangJpaDAO khachHangJpaDAO;

    public KhachHangCommandHandler(KhachHangJpaDAO khachHangJpaDAO) {
        this.khachHangJpaDAO = khachHangJpaDAO;
    }

    @Override
    public Response handle(Request request) {
        if (request.getSessionUserId() == null || request.getSessionUserId().isBlank()) {
            return Response.error("Bạn chưa đăng nhập");
        }
        String action = request.getCommand().substring("KHACH_HANG.".length());
        KhachHangCM cmd;
        try {
            cmd = KhachHangCM.valueOf(action);
        } catch (IllegalArgumentException e) {
            return Response.error("Command khách hàng không hỗ trợ: " + action);
        }


        switch (cmd) {
            case LIST_ALL -> {
                return Response.success(khachHangJpaDAO.getAllKhachHang(), "Tải danh sách khách hàng thành công");
            }
            case CREATE -> {
                if (!(request.getData() instanceof KhachHangPayload payload)) {
                    return Response.error("Payload tạo khách hàng không hợp lệ");
                }
                KhachHang khachHang = toKhachHangEntity(payload);
                khachHangJpaDAO.createKhachHang(khachHang);
                return Response.success(null, "Thêm khách hàng thành công");
            }
            case UPDATE -> {
                if (!(request.getData() instanceof KhachHangPayload payload)) {
                    return Response.error("Payload cập nhật khách hàng không hợp lệ");
                }
                KhachHang khachHang = toKhachHangEntity(payload);
                khachHangJpaDAO.updateKhachHang(khachHang);
                return Response.success(null, "Cập nhật khách hàng thành công");
            }
            case DELETE -> {
                if (!(request.getData() instanceof String maKH) || maKH.isBlank()) {
                    return Response.error("Payload xóa khách hàng không hợp lệ");
                }
                boolean result = khachHangJpaDAO.deactivateKhachHang(maKH);
                if (!result) {
                    return Response.error("Không thể xóa khách hàng đã chọn");
                }
                return Response.success(null, "Xóa khách hàng thành công");
            }
        }
        return Response.error("Command khách hàng không hỗ trợ");
    }

    private KhachHang toKhachHangEntity(KhachHangPayload payload) {
        return KhachHang.builder()
                .maKH(payload.getMaKH())
                .tenKH(payload.getTenKH())
                .soDienThoai(payload.getSoDienThoai())
                .diemTichLuy(payload.getDiemTichLuy() != null ? payload.getDiemTichLuy() : 0)
                .isActive(payload.isActive())
                .build();
    }
}
