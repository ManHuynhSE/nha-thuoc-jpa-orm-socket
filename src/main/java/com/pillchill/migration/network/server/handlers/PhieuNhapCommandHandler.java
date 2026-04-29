package com.pillchill.migration.network.server.handlers;

import com.pillchill.migration.migration.PhieuNhapJpaDAO;
import com.pillchill.migration.network.communication.PhieuNhapImportPayload;
import com.pillchill.migration.network.communication.Request;
import com.pillchill.migration.network.communication.Response;
import com.pillchill.migration.network.communication.command.PhieuNhapCM;
import com.pillchill.migration.network.server.CommandHandler;

public class PhieuNhapCommandHandler implements CommandHandler {
    private final PhieuNhapJpaDAO phieuNhapJpaDAO;

    public PhieuNhapCommandHandler(PhieuNhapJpaDAO phieuNhapJpaDAO) {
        this.phieuNhapJpaDAO = phieuNhapJpaDAO;
    }

    @Override
    public Response handle(Request request) {
        if (request.getSessionUserId() == null || request.getSessionUserId().isBlank()) {
            return Response.error("Bạn chưa đăng nhập");
        }
        String action = request.getCommand().substring("PHIEU_NHAP.".length());
        PhieuNhapCM cmd;
        try {
            cmd = PhieuNhapCM.valueOf(action);
        } catch (IllegalArgumentException e) {
            return Response.error("Command phiếu nhập không hỗ trợ: " + action);
        }

        try {
            switch (cmd) {
                case LIST_ALL -> {
                    return Response.success(phieuNhapJpaDAO.getAllPhieuNhapViews(), "Tải danh sách phiếu nhập thành công");
                }
                case LIST_CHI_TIET -> {
                    if (!(request.getData() instanceof String maPhieuNhapThuoc) || maPhieuNhapThuoc.isBlank()) {
                        return Response.error("Mã phiếu nhập không hợp lệ");
                    }
                    return Response.success(
                            phieuNhapJpaDAO.getChiTietPhieuNhapByMaPhieuNhap(maPhieuNhapThuoc),
                            "Tải chi tiết phiếu nhập thành công"
                    );
                }
                case IMPORT_FROM_EXCEL -> {
                    if (!(request.getData() instanceof PhieuNhapImportPayload payload)
                            || payload.getItems() == null
                            || payload.getItems().isEmpty()) {
                        return Response.error("Payload nhập thuốc không hợp lệ");
                    }
                    String maPhieuNhap = phieuNhapJpaDAO.importFromExcel(payload.getItems(), request.getSessionUserId());
                    return Response.success(maPhieuNhap, "Nhập thuốc từ Excel thành công");
                }
            }
        } catch (RuntimeException ex) {
            return Response.error(ex.getMessage());
        }
        return Response.error("Command phiếu nhập không hỗ trợ");
    }
}
