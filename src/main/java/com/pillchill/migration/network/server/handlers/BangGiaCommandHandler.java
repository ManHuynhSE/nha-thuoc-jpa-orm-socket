package com.pillchill.migration.network.server.handlers;

import com.pillchill.migration.dto.BangGiaDTO;
import com.pillchill.migration.entity.BangGia;
import com.pillchill.migration.entity.KhachHang;
import com.pillchill.migration.migration.BangGiaJpaDAO;
import com.pillchill.migration.network.communication.BangGiaPayLoad;
import com.pillchill.migration.network.communication.KhachHangPayload;
import com.pillchill.migration.network.communication.Request;
import com.pillchill.migration.network.communication.Response;
import com.pillchill.migration.network.communication.command.BangGiaCM;
import com.pillchill.migration.network.communication.command.KhachHangCM;
import com.pillchill.migration.network.server.CommandHandler;

public class BangGiaCommandHandler implements CommandHandler {
    private final BangGiaJpaDAO bangGiaJpaDAO;

    public BangGiaCommandHandler(BangGiaJpaDAO bangGiaJpaDAO) {
        this.bangGiaJpaDAO = bangGiaJpaDAO;
    }

    @Override
    public Response handle(Request request) {
        if (request.getSessionUserId() == null || request.getSessionUserId().isBlank()) {
            return Response.error("Bạn chưa đăng nhập");
        }
        String action = request.getCommand().substring("BANG_GIA.".length());
        BangGiaCM cmd;
        try {
            cmd = BangGiaCM.valueOf(action);
        } catch (IllegalArgumentException e) {
            return Response.error("Command bảng giá không hỗ trợ: " + action);
        }


        switch (cmd) {
            case LIST_ALL -> {
                return Response.success(bangGiaJpaDAO.getAllBangGia(), "Tải danh sách bảng giá thành công");
            }
            case CREATE -> {
                if (!(request.getData() instanceof BangGiaPayLoad payload)) {
                    return Response.error("Payload tạo bảng giá không hợp lệ");
                }
                BangGia bangGia = toBangGiaEntity(payload);
                bangGiaJpaDAO.addBangGia(bangGia);
                return Response.success(null, "Thêm bảng giá thành công");
            }
            case UPDATE -> {
                if (!(request.getData() instanceof BangGiaPayLoad payload)) {
                    return Response.error("Payload cập nhật bảng giá không hợp lệ");
                }
                BangGia bangGia = toBangGiaEntity(payload);
                bangGiaJpaDAO.updateBangGia(bangGia);
                return Response.success(null, "Cập nhật bảng giá thành công");
            }
            case DELETE -> {
                if (!(request.getData() instanceof String maBG) || maBG.isBlank()) {
                    return Response.error("Payload xóa bảng giá không hợp lệ");
                }
                boolean result = bangGiaJpaDAO.deleteBangGia(maBG);
                if (!result) {
                    return Response.error("Không thể xóa bảng giá đã chọn");
                }
                return Response.success(null, "Xóa bảng giá thành công");
            }
            case FIND_BY_MA -> {
                if (!(request.getData() instanceof String maBG) || maBG.isBlank()) {
                    return Response.error("Mã khách hàng không hợp lệ");
                }
                BangGia bangGia = bangGiaJpaDAO.findBangGiaById(maBG);
                return Response.success(bangGia, "Tìm bảng giá thành công");
            }
        }
        return Response.error("Command bảng giá không hỗ trợ");
    }

    private BangGia toBangGiaEntity(BangGiaPayLoad payload) {
        return BangGia.builder()
                .maBangGia(payload.getMaBangGia())
                .tenBangGia(payload.getTenBangGia())
                .loaiGia(payload.getLoaiGia())
                .ngayApDung(payload.getNgayApDung())
                .ngayKetThuc(payload.getNgayKetThuc())
                .trangThai(payload.getTrangThai())
                .ghiChu(payload.getGhiChu())
                .doUuTien(payload.getDoUuTien())
                .isActive(payload.isActive())
                .build();
    }
}
