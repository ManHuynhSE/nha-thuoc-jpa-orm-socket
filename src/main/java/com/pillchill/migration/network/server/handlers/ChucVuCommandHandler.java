package com.pillchill.migration.network.server.handlers;

import com.pillchill.migration.entity.ChucVu;
import com.pillchill.migration.migration.ChucVuJpaDAO;
import com.pillchill.migration.network.communication.Request;
import com.pillchill.migration.network.communication.Response;
import com.pillchill.migration.network.communication.command.ChucVuCM;
import com.pillchill.migration.network.server.CommandHandler;

import java.util.ArrayList;
import java.util.List;

public class ChucVuCommandHandler implements CommandHandler {
    private final ChucVuJpaDAO chucVuJpaDAO;

    public ChucVuCommandHandler(ChucVuJpaDAO chucVuJpaDAO) {
        this.chucVuJpaDAO = chucVuJpaDAO;
    }

    @Override
    public Response handle(Request request) {
        if (request.getSessionUserId() == null || request.getSessionUserId().isBlank()) {
            return Response.error("Bạn chưa đăng nhập");
        }

        if (request.getCommand() == null || !request.getCommand().startsWith("CHUC_VU.")) {
            return Response.error("Command chức vụ không hợp lệ");
        }

        String action = request.getCommand().substring("CHUC_VU.".length());
        try {
            return switch (ChucVuCM.valueOf(action)) {
                case LIST_ALL -> handleList();
                case CREATE -> handleAdd(request);
                case UPDATE -> handleUpdate(request);
                case DELETE -> handleDelete(request);
            };
        } catch (IllegalArgumentException e) {
            return Response.error("Command chức vụ không hỗ trợ: " + action);
        }
    }

    private Response handleList() {
        List<ChucVu> allChucVu = chucVuJpaDAO.getAllChucVu();
        List<ChucVu> result = new ArrayList<>();
        for (ChucVu item : allChucVu) {
            result.add(ChucVu.builder()
                    .maChucVu(item.getMaChucVu())
                    .isActive(item.isActive())
                    .tenChucVu(item.getTenChucVu())
                    .build());
        }
        return Response.success(result, "Tải danh sách chức vụ thành công");
    }

    private Response handleAdd(Request request) {
        Object data = request.getData();
        if (!(data instanceof ChucVu chucVu)) {
            return Response.error("Dữ liệu thêm chức vụ không hợp lệ");
        }
        if (chucVu.getMaChucVu() == null || chucVu.getMaChucVu().isBlank()) {
            return Response.error("Mã chức vụ không được để trống");
        }
        if (chucVu.getTenChucVu() == null || chucVu.getTenChucVu().isBlank()) {
            return Response.error("Tên chức vụ không được để trống");
        }
        try {
            chucVuJpaDAO.addChucVu(chucVu);
            return Response.success(null, "Thêm chức vụ thành công");
        } catch (Exception e) {
            return Response.error("Không thể thêm chức vụ: " + e.getMessage());
        }
    }

    private Response handleUpdate(Request request) {
        Object data = request.getData();
        if (!(data instanceof ChucVu chucVu)) {
            return Response.error("Dữ liệu cập nhật chức vụ không hợp lệ");
        }
        if (chucVu.getMaChucVu() == null || chucVu.getMaChucVu().isBlank()) {
            return Response.error("Mã chức vụ không được để trống");
        }
        if (chucVu.getTenChucVu() == null || chucVu.getTenChucVu().isBlank()) {
            return Response.error("Tên chức vụ không được để trống");
        }
        try {
            chucVuJpaDAO.updateChucVu(chucVu);
            return Response.success(null, "Cập nhật chức vụ thành công");
        } catch (Exception e) {
            return Response.error("Không thể cập nhật chức vụ: " + e.getMessage());
        }
    }

    private Response handleDelete(Request request) {
        Object data = request.getData();
        if (!(data instanceof String maChucVu) || maChucVu.isBlank()) {
            return Response.error("Dữ liệu xóa chức vụ không hợp lệ");
        }

        try {
            chucVuJpaDAO.deleteChucVu(maChucVu);
            return Response.success(null, "Xóa chức vụ thành công");
        } catch (Exception e) {
            return Response.error("Không thể xóa chức vụ: " + e.getMessage());
        }
    }
}