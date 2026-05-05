package com.pillchill.migration.network.server.handlers;

import com.pillchill.migration.entity.BangGia;
import com.pillchill.migration.migration.BangGiaJpaDAO;
import com.pillchill.migration.migration.ChiTietBangGiaJpaDAO;
import com.pillchill.migration.network.communication.BangGiaPayLoad;
import com.pillchill.migration.network.communication.ChiTietBangGiaPayload;
import com.pillchill.migration.network.communication.Request;
import com.pillchill.migration.network.communication.Response;
import com.pillchill.migration.network.communication.command.BangGiaCM;
import com.pillchill.migration.network.server.CommandHandler;

import java.util.ArrayList;
import java.util.List;

public class BangGiaCommandHandler implements CommandHandler {
    private final BangGiaJpaDAO bangGiaJpaDAO;
    private final ChiTietBangGiaJpaDAO chiTietBangGiaJpaDAO;

    public BangGiaCommandHandler(BangGiaJpaDAO bangGiaJpaDAO) {
        this.bangGiaJpaDAO = bangGiaJpaDAO;
        this.chiTietBangGiaJpaDAO = new ChiTietBangGiaJpaDAO();
    }

    @Override
    public Response handle(Request request) {
        if (request.getSessionUserId() == null || request.getSessionUserId().isBlank()) {
            return Response.error("Bạn chưa đăng nhập");
        }
        if (request.getCommand() == null || !request.getCommand().startsWith("BANG_GIA.")) {
            return Response.error("Command bảng giá không hợp lệ");
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
                return Response.success(toBangGiaResponseList(bangGiaJpaDAO.getAllBangGia()), "Tải danh sách bảng giá thành công");
            }
            case LIST_ALL_INACTIVE -> {
                return Response.success(toBangGiaResponseList(bangGiaJpaDAO.getAllBangGiaInactive()), "Tải danh sách bảng giá đã xóa thành công");
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
            case REACTIVE -> {
                if (!(request.getData() instanceof String maBG) || maBG.isBlank()) {
                    return Response.error("Dữ liệu khôi phục bảng giá không hợp lệ");
                }
                boolean result = bangGiaJpaDAO.reactiveBangGia(maBG);
                if (!result) {
                    return Response.error("Không thể khôi phục bảng giá đã chọn");
                }
                return Response.success(null, "Khôi phục bảng giá thành công");
            }
            case LIST_CHI_TIET -> {
                if (!(request.getData() instanceof String maBG) || maBG.isBlank()) {
                    return Response.error("Mã bảng giá không hợp lệ");
                }
                return Response.success(chiTietBangGiaJpaDAO.getChiTietBangGiaTheoMa(maBG), "Tải chi tiết bảng giá thành công");
            }
            case LIST_THUOC_KEM_GIA -> {
                return Response.success(chiTietBangGiaJpaDAO.getAllThuocKemGia(), "Tải danh sách thuốc thành công");
            }
            case ADD_CHI_TIET -> {
                if (!(request.getData() instanceof ChiTietBangGiaPayload payload)) {
                    return Response.error("Payload thêm chi tiết bảng giá không hợp lệ");
                }
                boolean result = chiTietBangGiaJpaDAO.themChiTietBangGia(
                        payload.getMaBangGia(),
                        payload.getMaThuoc(),
                        payload.getDonGia(),
                        payload.getMaDonVi(),
                        payload.isActive()
                );
                if (!result) {
                    return Response.error("Chi tiết bảng giá đã tồn tại");
                }
                return Response.success(null, "Thêm chi tiết bảng giá thành công");
            }
            case UPDATE_CHI_TIET -> {
                if (!(request.getData() instanceof ChiTietBangGiaPayload payload)) {
                    return Response.error("Payload cập nhật chi tiết bảng giá không hợp lệ");
                }
                boolean result = chiTietBangGiaJpaDAO.capNhatChiTietBangGia(
                        payload.getMaBangGia(),
                        payload.getMaThuoc(),
                        payload.getDonGia(),
                        payload.getMaDonVi(),
                        payload.isActive()
                );
                if (!result) {
                    return Response.error("Không thể cập nhật chi tiết bảng giá");
                }
                return Response.success(null, "Cập nhật chi tiết bảng giá thành công");
            }
            case DELETE_CHI_TIET -> {
                if (!(request.getData() instanceof String[] key) || key.length < 2) {
                    return Response.error("Payload xóa chi tiết bảng giá không hợp lệ");
                }
                boolean result = chiTietBangGiaJpaDAO.xoaChiTietBangGia(key[0], key[1]);
                if (!result) {
                    return Response.error("Không tìm thấy chi tiết bảng giá để xóa");
                }
                return Response.success(null, "Xóa chi tiết bảng giá thành công");
            }
        }
        return Response.error("Command bảng giá không hỗ trợ");
    }

    private List<BangGia> toBangGiaResponseList(List<BangGia> source) {
        List<BangGia> result = new ArrayList<>();
        for (BangGia item : source) {
            result.add(BangGia.builder()
                    .maBangGia(item.getMaBangGia())
                    .tenBangGia(item.getTenBangGia())
                    .loaiGia(item.getLoaiGia())
                    .ngayApDung(item.getNgayApDung())
                    .ngayKetThuc(item.getNgayKetThuc())
                    .trangThai(item.getTrangThai())
                    .ghiChu(item.getGhiChu())
                    .doUuTien(item.getDoUuTien())
                    .isActive(item.isActive())
                    .build());
        }
        return result;
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