package com.pillchill.migration.network.server.handlers;


import com.pillchill.migration.dto.NhanVienDTO;
import com.pillchill.migration.migration.NhanVienJpaDAO;
import com.pillchill.migration.network.communication.Request;
import com.pillchill.migration.network.communication.Response;
import com.pillchill.migration.network.communication.command.NhanVienCM;
import com.pillchill.migration.network.server.CommandHandler;

import java.util.List;

public class NhanVienCommandHandler implements CommandHandler {
    private final NhanVienJpaDAO nhanVienJpaDAO;

    public NhanVienCommandHandler(NhanVienJpaDAO nhanVienJpaDAO) {
        this.nhanVienJpaDAO = nhanVienJpaDAO;
    }



    @Override
    public Response handle(Request request) {
        if (request.getSessionUserId() == null || request.getSessionUserId().isBlank()) {
            return Response.error("Bạn chưa đăng nhập");
        }

        if (request.getCommand() == null || !request.getCommand().startsWith("NHAN_VIEN.")) {
            return Response.error("Command nhân viên không hợp lệ");
        }

        String action = request.getCommand().substring("NHAN_VIEN.".length());
        try {
            return switch (NhanVienCM.valueOf(action)) {
                case LIST_ALL -> handleList();
                case CREATE -> handleAdd(request);
                case UPDATE -> handleUpdate(request);
                case DELETE -> handleDelete(request);
            };
        } catch (IllegalArgumentException e) {
            return Response.error("Command nhân viên không hỗ trợ: " + action);
        }
    }

    private Response handleList() {
        return Response.success(nhanVienJpaDAO.getAllNhanVien(), "Tải danh sách nhân viên thành công");
    }

    private Response handleAdd(Request request) {
        if (!(request.getData() instanceof NhanVienDTO payload)) {
            return Response.error("Dữ liệu thêm nhân viên không hợp lệ");
        }
        NhanVienDTO created = nhanVienJpaDAO.addNhanVien(payload);
        return Response.success(created, "Thêm nhân viên thành công");
    }

    private Response handleUpdate(Request request) {
        if (!(request.getData() instanceof NhanVienDTO payload)) {
            return Response.error("Dữ liệu cập nhật nhân viên không hợp lệ");
        }
        NhanVienDTO updated = nhanVienJpaDAO.updateNhanVien(payload);
        return Response.success(updated, "Cập nhật nhân viên thành công");
    }

    private Response handleDelete(Request request) {
        Object payload = request.getData();
        if (!(payload instanceof String maNhanVien) || maNhanVien.isBlank()) {
            return Response.error("Mã nhân viên cần xóa không hợp lệ");
        }
        boolean deleted = nhanVienJpaDAO.deleteNhanVien(maNhanVien);
        if (!deleted) {
            return Response.error("Xóa nhân viên không thành công");
        }
        return Response.success(true, "Xóa nhân viên thành công");
    }
}
