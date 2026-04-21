package com.pillchill.migration.network.server.handlers;


import com.pillchill.migration.dto.NhanVienDTO;
import com.pillchill.migration.migration.NhanVienJpaDAO;
import com.pillchill.migration.network.communication.CommandType;
import com.pillchill.migration.network.communication.Request;
import com.pillchill.migration.network.communication.Response;
import com.pillchill.migration.network.server.CommandHandler;

import java.util.ArrayList;
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

        try {
            CommandType commandType = request.getCommandType();
            if (commandType == CommandType.NHAN_VIEN_LIST_ALL) {
                return handleList();
            }
            if (commandType == CommandType.NHAN_VIEN_ADD) {
                return handleAdd(request);
            }
            if (commandType == CommandType.NHAN_VIEN_UPDATE) {
                return handleUpdate(request);
            }
            if (commandType == CommandType.NHAN_VIEN_DELETE) {
                return handleDelete(request);
            }
            return Response.error("Lệnh không hỗ trợ: " + commandType);
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }

    private Response handleList() {
        List<NhanVienDTO> source = nhanVienJpaDAO.getAllNhanVien();
        List<NhanVienDTO> result = new ArrayList<>();
        for (NhanVienDTO item : source) {
            result.add(NhanVienDTO.builder()
                    .maNV(item.getMaNV())
                    .tenNV(item.getTenNV())
                    .soDienThoai(item.getSoDienThoai())
                    .chucVu(item.getChucVu())
                    .build());
        }
        return Response.success(result, "Tải danh sách nhân viên thành công");
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
