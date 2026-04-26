package com.pillchill.migration.network.server.handlers;

import com.pillchill.migration.entity.ChucVu;
import com.pillchill.migration.entity.NhanVien;
import com.pillchill.migration.migration.NhanVienJpaDAO;
import com.pillchill.migration.network.communication.Request;
import com.pillchill.migration.network.communication.Response;
import com.pillchill.migration.network.communication.NhanVienPayload;
import com.pillchill.migration.network.communication.command.NhanVienCM;
import com.pillchill.migration.network.server.CommandHandler;

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
        String action = request.getCommand().substring("NHAN_VIEN.".length());
        NhanVienCM cmd;
        try {
            cmd = NhanVienCM.valueOf(action);
        } catch (IllegalArgumentException e) {
            return Response.error("Command nhân viên không hỗ trợ: " + action);
        }

        switch (cmd) {
            case LIST_ALL -> {
                return Response.success(nhanVienJpaDAO.getAllNhanVien(), "Tải danh sách nhân viên thành công");
            }
            case CREATE -> {
//                if (!(request.getData() instanceof NhanVienPayload payload)) {
//                    return Response.error("Payload tạo nhân viên không hợp lệ");
//                }
//                NhanVien nhanVien = toNhanVienEntity(payload);
//                nhanVienJpaDAO.createNhanVien(nhanVien);
//                return Response.success(null, "Thêm nhân viên thành công");
            }
            case UPDATE -> {
                if (!(request.getData() instanceof NhanVienPayload payload)) {
                    return Response.error("Payload cập nhật nhân viên không hợp lệ");
                }
                NhanVien nhanVien = toNhanVienEntity(payload);
                nhanVienJpaDAO.updateNhanVien(nhanVien);
                return Response.success(null, "Cập nhật nhân viên thành công");
            }
            case DELETE -> {
                if (!(request.getData() instanceof String maNV) || maNV.isBlank()) {
                    return Response.error("Payload xóa nhân viên không hợp lệ");
                }
                boolean result = nhanVienJpaDAO.deactivateNhanVien(maNV);
                if (!result) {
                    return Response.error("Không thể xóa nhân viên đã chọn");
                }
                return Response.success(null, "Xóa nhân viên thành công");
            }
        }
        return Response.error("Command nhân viên không hỗ trợ");
    }

    private NhanVien toNhanVienEntity(NhanVienPayload payload) {
        return NhanVien.builder()
                .maNV(payload.getMaNV())
                .tenNV(payload.getTenNV())
                .chucVu(ChucVu.builder().maChucVu(payload.getMaChucVu()).build())
                .soDienThoai(payload.getSoDienThoai())
                .isActive(payload.isActive())
                .build();
    }
}
