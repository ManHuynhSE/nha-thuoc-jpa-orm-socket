package com.pillchill.migration.network.server.handlers;

import com.pillchill.migration.entity.NhaSanXuat;
import com.pillchill.migration.migration.NhaSanXuatJpaDAO;
import com.pillchill.migration.network.communication.Request;
import com.pillchill.migration.network.communication.Response;
import com.pillchill.migration.network.communication.command.NhaSanXuatCM;
import com.pillchill.migration.network.server.CommandHandler;

import java.util.ArrayList;
import java.util.List;

public class NhaSanXuatCommandHandler implements CommandHandler {
    private final NhaSanXuatJpaDAO nhaSanXuatJpaDAO;

    public NhaSanXuatCommandHandler(NhaSanXuatJpaDAO nhaSanXuatJpaDAO) {
        this.nhaSanXuatJpaDAO = nhaSanXuatJpaDAO;
    }

    @Override
    public Response handle(Request request) {
        if (request.getSessionUserId() == null || request.getSessionUserId().isBlank()) {
            return Response.error("Bạn chưa đăng nhập");
        }

        if (request.getCommand() == null || !request.getCommand().startsWith("NHA_SAN_XUAT.")) {
            return Response.error("Command nhà sản xuất không hợp lệ");
        }

        String action = request.getCommand().substring("NHA_SAN_XUAT.".length());
        try {
            return switch (NhaSanXuatCM.valueOf(action)) {
                case LIST_ALL -> handleList();
                case CREATE -> handleAdd(request);
                case UPDATE -> handleUpdate(request);
                case DELETE -> handleDelete(request);
            };
        } catch (IllegalArgumentException e) {
            return Response.error("Command nhà sản xuất không hỗ trợ: " + action);
        }
    }

    private Response handleList() {
        List<NhaSanXuat> allNhaSanXuat = nhaSanXuatJpaDAO.getAllNhaSanXuat();
        List<NhaSanXuat> result = new ArrayList<>();
        for (NhaSanXuat item : allNhaSanXuat) {
            result.add(NhaSanXuat.builder()
                    .maNSX(item.getMaNSX())
                    .tenNSX(item.getTenNSX())
                    .diaChiNSX(item.getDiaChiNSX())
                    .soDienThoai(item.getSoDienThoai())
                    .isActive(item.isActive())
                    .build());
        }
        return Response.success(result, "Tải danh sách nhà sản xuất thành công");
    }

    private Response handleAdd(Request request) {
        if (!(request.getData() instanceof NhaSanXuat payload)) {
            return Response.error("Dữ liệu thêm nhà sản xuất không hợp lệ");
        }
        if (payload.getMaNSX() == null || payload.getMaNSX().isBlank()) {
            return Response.error("Mã nhà sản xuất không được để trống");
        }
        if (payload.getTenNSX() == null || payload.getTenNSX().isBlank()) {
            return Response.error("Tên nhà sản xuất không được để trống");
        }
        try {
            nhaSanXuatJpaDAO.addNhaSanXuat(payload);
            return Response.success(null, "Thêm nhà sản xuất thành công");
        } catch (Exception e) {
            return Response.error("Không thể thêm nhà sản xuất: " + e.getMessage());
        }
    }

    private Response handleUpdate(Request request) {
        if (!(request.getData() instanceof NhaSanXuat payload)) {
            return Response.error("Dữ liệu cập nhật nhà sản xuất không hợp lệ");
        }
        if (payload.getMaNSX() == null || payload.getMaNSX().isBlank()) {
            return Response.error("Mã nhà sản xuất không được để trống");
        }
        if (payload.getTenNSX() == null || payload.getTenNSX().isBlank()) {
            return Response.error("Tên nhà sản xuất không được để trống");
        }
        try {
            nhaSanXuatJpaDAO.updateNhaSanXuat(payload);
            return Response.success(null, "Cập nhật nhà sản xuất thành công");
        } catch (Exception e) {
            return Response.error("Không thể cập nhật nhà sản xuất: " + e.getMessage());
        }
    }

    private Response handleDelete(Request request) {
        Object data = request.getData();
        if (!(data instanceof String maNSX) || maNSX.isBlank()) {
            return Response.error("Dữ liệu xóa nhà sản xuất không hợp lệ");
        }

        try {
            nhaSanXuatJpaDAO.deleteNhaSanXuat(maNSX);
            return Response.success(null, "Xóa nhà sản xuất thành công");
        } catch (Exception e) {
            return Response.error("Không thể xóa nhà sản xuất: " + e.getMessage());
        }
    }
}
