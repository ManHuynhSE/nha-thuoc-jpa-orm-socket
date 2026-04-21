package com.pillchill.migration.network.server.handlers;

import com.pillchill.migration.entity.DonVi;
import com.pillchill.migration.migration.DonViJpaDAO;
import com.pillchill.migration.network.communication.CommandType;
import com.pillchill.migration.network.communication.Request;
import com.pillchill.migration.network.communication.Response;
import com.pillchill.migration.network.server.CommandHandler;

import java.util.ArrayList;
import java.util.List;

public class DonViCommandHandler implements CommandHandler {
    private final DonViJpaDAO donViJpaDAO;

    public DonViCommandHandler(DonViJpaDAO donViJpaDAO) {
        this.donViJpaDAO = donViJpaDAO;
    }

    @Override
    public Response handle(Request request) {
        if (request.getSessionUserId() == null || request.getSessionUserId().isBlank()) {
            return Response.error("Bạn chưa đăng nhập");
        }

        CommandType commandType = request.getCommandType();
        return switch (commandType) {
            case DON_VI_LIST_ALL -> handleList();
            case DON_VI_ADD -> handleAdd(request);
            case DON_VI_UPDATE -> handleUpdate(request);
            case DON_VI_DELETE -> handleDelete(request);
            default -> Response.error("Lệnh không hợp lệ: " + commandType);
        };
    }

    private Response handleList() {
        List<DonVi> allDonVi = donViJpaDAO.getAllDonVi();
        List<DonVi> result = new ArrayList<>();
        for (DonVi item : allDonVi) {
            result.add(DonVi.builder()
                    .maDonVi(item.getMaDonVi())
                    .tenDonVi(item.getTenDonVi())
                    .isActive(item.isActive())
                    .build());
        }
        return Response.success(result, "Tải danh sách đơn vị thành công");
    }

    private Response handleAdd(Request request) {
        if (!(request.getData() instanceof DonVi payload)) {
            return Response.error("Dữ liệu thêm đơn vị không hợp lệ");
        }
        if (payload.getMaDonVi() == null || payload.getMaDonVi().isBlank()) {
            return Response.error("Mã đơn vị không được để trống");
        }
        if (payload.getTenDonVi() == null || payload.getTenDonVi().isBlank()) {
            return Response.error("Tên đơn vị không được để trống");
        }
        try {
            donViJpaDAO.addDonVi(payload);
            return Response.success(null, "Thêm đơn vị thành công");
        } catch (Exception e) {
            return Response.error("Không thể thêm đơn vị: " + e.getMessage());
        }
    }

    private Response handleUpdate(Request request) {
        if (!(request.getData() instanceof DonVi payload)) {
            return Response.error("Dữ liệu cập nhật đơn vị không hợp lệ");
        }
        if (payload.getMaDonVi() == null || payload.getMaDonVi().isBlank()) {
            return Response.error("Mã đơn vị không được để trống");
        }
        if (payload.getTenDonVi() == null || payload.getTenDonVi().isBlank()) {
            return Response.error("Tên đơn vị không được để trống");
        }
        try {
            donViJpaDAO.updateDonVi(payload);
            return Response.success(null, "Cập nhật đơn vị thành công");
        } catch (Exception e) {
            return Response.error("Không thể cập nhật đơn vị: " + e.getMessage());
        }
    }

    private Response handleDelete(Request request) {
        Object data = request.getData();
        if (!(data instanceof String maDonVi) || maDonVi.isBlank()) {
            return Response.error("Dữ liệu xóa đơn vị không hợp lệ");
        }

        try {
            donViJpaDAO.deleteDonVi(maDonVi);
            return Response.success(null, "Xóa đơn vị thành công");
        } catch (Exception e) {
            return Response.error("Không thể xóa đơn vị: " + e.getMessage());
        }
    }
}
