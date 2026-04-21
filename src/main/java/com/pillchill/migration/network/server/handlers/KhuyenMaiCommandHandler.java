package com.pillchill.migration.network.server.handlers;

import com.pillchill.migration.entity.KhuyenMai;
import com.pillchill.migration.migration.KhuyenMaiJpaDAO;
import com.pillchill.migration.network.communication.CommandType;
import com.pillchill.migration.network.communication.Request;
import com.pillchill.migration.network.communication.Response;
import com.pillchill.migration.network.server.CommandHandler;

import java.util.ArrayList;
import java.util.List;

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

        CommandType commandType = request.getCommandType();
        return switch (commandType) {
            case KHUYEN_MAI_LIST_ALL -> handleList();
            case KHUYEN_MAI_ADD -> handleAdd(request);
            case KHUYEN_MAI_UPDATE -> handleUpdate(request);
            case KHUYEN_MAI_DELETE -> handleDelete(request);
            default -> Response.error("Lệnh không hợp lệ: " + commandType);
        };
    }

    private Response handleList() {
        List<KhuyenMai> allKhuyenMai = khuyenMaiJpaDAO.getAllKhuyenMai();
        List<KhuyenMai> result = new ArrayList<>();
        for (KhuyenMai item : allKhuyenMai) {
            result.add(KhuyenMai.builder()
                    .maKM(item.getMaKM())
                    .mucGiamGia(item.getMucGiamGia())
                    .ngayApDung(item.getNgayApDung())
                    .ngayKetThuc(item.getNgayKetThuc())
                    .isActive(item.isActive())
                    .build());
        }
        return Response.success(result, "Tải danh sách khuyến mãi thành công");
    }

    private Response handleAdd(Request request) {
        if (!(request.getData() instanceof KhuyenMai payload)) {
            return Response.error("Dữ liệu thêm khuyến mãi không hợp lệ");
        }
        if (payload.getMaKM() == null || payload.getMaKM().isBlank()) {
            return Response.error("Mã khuyến mãi không được để trống");
        }
        try {
            khuyenMaiJpaDAO.addKhuyenMai(payload);
            return Response.success(null, "Thêm khuyến mãi thành công");
        } catch (Exception e) {
            return Response.error("Không thể thêm khuyến mãi: " + e.getMessage());
        }
    }

    private Response handleUpdate(Request request) {
        if (!(request.getData() instanceof KhuyenMai payload)) {
            return Response.error("Dữ liệu cập nhật khuyến mãi không hợp lệ");
        }
        if (payload.getMaKM() == null || payload.getMaKM().isBlank()) {
            return Response.error("Mã khuyến mãi không được để trống");
        }
        try {
            khuyenMaiJpaDAO.updateKhuyenMai(payload);
            return Response.success(null, "Cập nhật khuyến mãi thành công");
        } catch (Exception e) {
            return Response.error("Không thể cập nhật khuyến mãi: " + e.getMessage());
        }
    }

    private Response handleDelete(Request request) {
        Object data = request.getData();
        if (!(data instanceof String maKhuyenMai) || maKhuyenMai.isBlank()) {
            return Response.error("Dữ liệu xóa khuyến mãi không hợp lệ");
        }

        try {
            khuyenMaiJpaDAO.deleteKhuyenMai(maKhuyenMai);
            return Response.success(null, "Xóa khuyến mãi thành công");
        } catch (Exception e) {
            return Response.error("Không thể xóa khuyến mãi: " + e.getMessage());
        }
    }
}
