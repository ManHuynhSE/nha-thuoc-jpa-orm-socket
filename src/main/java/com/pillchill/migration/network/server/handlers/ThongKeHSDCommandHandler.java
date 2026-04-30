package com.pillchill.migration.network.server.handlers;

import com.pillchill.migration.dto.LoThuocHetHan;
import com.pillchill.migration.network.communication.Request;
import com.pillchill.migration.network.communication.Response;
import com.pillchill.migration.network.communication.command.ThongKeHSDCM;
import com.pillchill.migration.network.server.CommandHandler;
import com.pillchill.migration.service.IThongKeHSDService;

import java.util.List;

public class ThongKeHSDCommandHandler implements CommandHandler {
    private final IThongKeHSDService thongKeHSDService;

    public ThongKeHSDCommandHandler(IThongKeHSDService thongKeHSDService) {
        this.thongKeHSDService = thongKeHSDService;
    }

    @Override
    public Response handle(Request request) {
        if (request.getSessionUserId() == null || request.getSessionUserId().isBlank()) {
            return Response.error("Bạn chưa đăng nhập");
        }

        if (request.getCommand() == null || !request.getCommand().startsWith("THONG_KE_HSD.")) {
            return Response.error("Command thống kê HSD không hợp lệ");
        }

        String action = request.getCommand().substring("THONG_KE_HSD.".length());
        try {
            return switch (ThongKeHSDCM.valueOf(action)) {
                case LO_THUOC_HET_HAN -> {
                    List<LoThuocHetHan> result = thongKeHSDService.getCacLoThuocHetHan();
                    yield Response.success(result, "Lấy danh sách lô thuốc hết hạn thành công");
                }
                case LO_THUOC_SAP_HET_HAN -> {
                    if (!(request.getData() instanceof Integer soNgay)) {
                        yield Response.error("Số ngày không hợp lệ");
                    }
                    List<LoThuocHetHan> result = thongKeHSDService.getCacLoThuocSapHetHan(soNgay);
                    yield Response.success(result, "Lấy danh sách lô thuốc sắp hết hạn thành công");
                }
                case XOA_CHI_TIET_LO_THUOC -> {
                    if (!(request.getData() instanceof String[] params) || params.length < 2) {
                        yield Response.error("Dữ liệu xóa lô thuốc không hợp lệ");
                    }
                    boolean success = thongKeHSDService.xoaChiTietLoThuoc(params[0], params[1]);
                    if (success) {
                        yield Response.success(true, "Xóa chi tiết lô thuốc thành công");
                    } else {
                        yield Response.error("Xóa chi tiết lô thuốc thất bại");
                    }
                }
            };
        } catch (IllegalArgumentException e) {
            return Response.error("Command thống kê HSD không hỗ trợ: " + action);
        } catch (Exception e) {
            return Response.error("Không thể xử lý thống kê HSD: " + e.getMessage());
        }
    }
}
