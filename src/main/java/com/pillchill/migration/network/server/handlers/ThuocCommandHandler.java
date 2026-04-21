package com.pillchill.migration.network.server.handlers;

import com.pillchill.migration.entity.DonVi;
import com.pillchill.migration.entity.NhaSanXuat;
import com.pillchill.migration.entity.Thuoc;
import com.pillchill.migration.migration.ThuocJpaDAO;
import com.pillchill.migration.network.communication.Request;
import com.pillchill.migration.network.communication.Response;
import com.pillchill.migration.network.communication.ThuocPayload;
import com.pillchill.migration.network.communication.command.ThuocCM;
import com.pillchill.migration.network.server.CommandHandler;

public class ThuocCommandHandler implements CommandHandler {
    private final ThuocJpaDAO thuocJpaDAO;

    public ThuocCommandHandler(ThuocJpaDAO thuocJpaDAO) {
        this.thuocJpaDAO = thuocJpaDAO;
    }



    @Override
    public Response handle(Request request) {
        if (request.getSessionUserId() == null || request.getSessionUserId().isBlank()) {
            return Response.error("Bạn chưa đăng nhập");
        }
        String action = request.getCommand().substring("THUOC.".length());
        ThuocCM cmd;
        try {
            cmd = ThuocCM.valueOf(action);
        } catch (IllegalArgumentException e) {
            return Response.error("Command thuốc không hỗ trợ: " + action);
        }

        switch (cmd) {
            case LIST_ALL -> {
                return Response.success(thuocJpaDAO.getAllThuocKemGia(), "Tải danh sách thuốc thành công");
            }
            case LIST_BY_LO -> {
                return Response.success(thuocJpaDAO.getAllThuocTheoLo(), "Tải danh sách thuốc theo lô thành công");
            }
            case CREATE -> {
                if (!(request.getData() instanceof ThuocPayload payload)) {
                    return Response.error("Payload tạo thuốc không hợp lệ");
                }
                Thuoc thuoc = toThuocEntity(payload);
                thuocJpaDAO.createThuoc(thuoc, payload.getGiaBanDau() == null ? 0.0 : payload.getGiaBanDau());
                return Response.success(null, "Thêm thuốc thành công");
            }
            case UPDATE -> {
                if (!(request.getData() instanceof ThuocPayload payload)) {
                    return Response.error("Payload cập nhật thuốc không hợp lệ");
                }
                Thuoc thuoc = toThuocEntity(payload);
                thuocJpaDAO.updateThuoc(thuoc);
                return Response.success(null, "Cập nhật thuốc thành công");
            }
            case DELETE -> {
                if (!(request.getData() instanceof String maThuoc) || maThuoc.isBlank()) {
                    return Response.error("Payload xóa thuốc không hợp lệ");
                }
                boolean result = thuocJpaDAO.deactivateThuoc(maThuoc);
                if (!result) {
                    return Response.error("Không thể xóa thuốc đã chọn");
                }
                return Response.success(null, "Xóa thuốc thành công");
            }
        }
        return Response.error("Command thuốc không hỗ trợ");
    }

    private Thuoc toThuocEntity(ThuocPayload payload) {
        return Thuoc.builder()
                .maThuoc(payload.getMaThuoc())
                .tenThuoc(payload.getTenThuoc())
                .soLuongTon(payload.getSoLuongTon())
                .donVi(DonVi.builder().tenDonVi(payload.getDonVi()).build())
                .soLuongToiThieu(payload.getSoLuongToiThieu())
                .nhaSanXuat(NhaSanXuat.builder().tenNSX(payload.getNhaSanXuat()).build())
                .isActive(true)
                .build();
    }
}
