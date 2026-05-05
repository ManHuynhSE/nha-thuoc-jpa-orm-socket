package com.pillchill.migration.network.server.handlers;

import com.pillchill.migration.entity.KhuyenMai;
import com.pillchill.migration.migration.KhuyenMaiJpaDAO;
import com.pillchill.migration.network.communication.Request;
import com.pillchill.migration.network.communication.Response;
import com.pillchill.migration.network.communication.command.KhuyenMaiCM;
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

        if (request.getCommand() == null || !request.getCommand().startsWith("KHUYEN_MAI.")) {
            return Response.error("Command khuyến mãi không hợp lệ");
        }

        String action = request.getCommand().substring("KHUYEN_MAI.".length());
        try {
            return switch (KhuyenMaiCM.valueOf(action)) {
                case LIST_ALL -> handleList();
                case CREATE -> handleAdd(request);
                case UPDATE -> handleUpdate(request);
                case DELETE -> handleDelete(request);
                case FIND_BY_MA -> handleFindByMa(request);
                case LIST_ALL_INACTIVE -> handleInactiveList();
                case REACTIVE -> handleReactive(request);
            };
        } catch (IllegalArgumentException e) {
            return Response.error("Command khuyến mãi không hỗ trợ: " + action);
        }
    }

    private Response handleFindByMa(Request request) {
        Object data = request.getData();
        if (!(data instanceof String maKhuyenMai) || maKhuyenMai.isBlank()) {
            return Response.error("Mã khuyến mãi không hợp lệ");
        }
        try {
            KhuyenMai khuyenMai = khuyenMaiJpaDAO.getKhuyenMaiById(maKhuyenMai);
            return Response.success(khuyenMai, khuyenMai != null ? "Tìm khuyến mãi thành công" : "Không tìm thấy khuyến mãi");
        } catch (Exception e) {
            return Response.error("Không thể tìm khuyến mãi: " + e.getMessage());
        }
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

    private Response handleInactiveList() {
        List<KhuyenMai> allKhuyenMai = khuyenMaiJpaDAO.getAllKhuyenMaiInactive();
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
        return Response.success(result, "Tải danh sách khuyến mãi đã xóa thành công");
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

    private Response handleReactive(Request request) {
        Object data = request.getData();
        if (!(data instanceof String maKhuyenMai) || maKhuyenMai.isBlank()) {
            return Response.error("Dữ liệu khôi phục khuyến mãi không hợp lệ");
        }

        try {
            boolean result = khuyenMaiJpaDAO.reactivateKhuyenMai(maKhuyenMai);
            if (!result) {
                return Response.error("Không thể khôi phục khuyến mãi đã chọn");
            }
            return Response.success(null, "Khôi phục khuyến mãi thành công");
        } catch (Exception e) {
            return Response.error("Không thể khôi phục khuyến mãi: " + e.getMessage());
        }
    }


}
