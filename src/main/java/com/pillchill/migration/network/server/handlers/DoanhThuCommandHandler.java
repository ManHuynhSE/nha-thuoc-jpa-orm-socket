package com.pillchill.migration.network.server.handlers;

import com.pillchill.migration.network.communication.Request;
import com.pillchill.migration.network.communication.Response;
import com.pillchill.migration.network.communication.command.DoanhThuCM;
import com.pillchill.migration.network.server.CommandHandler;
import com.pillchill.migration.service.IDoanhThuService;

public class DoanhThuCommandHandler implements CommandHandler {
    private final IDoanhThuService doanhThuService;

    public DoanhThuCommandHandler(IDoanhThuService doanhThuService) {
        this.doanhThuService = doanhThuService;
    }

    @Override
    public Response handle(Request request) {
        if (request.getSessionUserId() == null || request.getSessionUserId().isBlank()) {
            return Response.error("Bạn chưa đăng nhập");
        }

        if (request.getCommand() == null || !request.getCommand().startsWith("DOANH_THU.")) {
            return Response.error("Command doanh thu không hợp lệ");
        }

        String action = request.getCommand().substring("DOANH_THU.".length());
        try {
            return switch (DoanhThuCM.valueOf(action)) {
                case DOANH_THU_THANG -> {
                    int[] values = extractMonthYear(request);
                    yield Response.success(doanhThuService.getDoanhThuCuaThang(values[0], values[1]), "Lấy doanh thu tháng thành công");
                }
                case SO_HOA_DON_THANG -> {
                    int[] values = extractMonthYear(request);
                    yield Response.success(doanhThuService.getSoHoaDonTheoThang(values[0], values[1]), "Lấy số hóa đơn tháng thành công");
                }
                case SO_KHACH_HANG_THANG -> {
                    int[] values = extractMonthYear(request);
                    yield Response.success(doanhThuService.getSoKhachHangCuaThang(values[0], values[1]), "Lấy số khách hàng tháng thành công");
                }
                case DOANH_THU_TRUNG_BINH_NGAY -> {
                    int[] values = extractMonthYear(request);
                    yield Response.success(doanhThuService.getDoanhThuTrungBinhTheoNgay(values[0], values[1]), "Lấy doanh thu trung bình ngày thành công");
                }
                case HOA_DON_TRONG_THANG -> {
                    int[] values = extractMonthYear(request);
                    yield Response.success(doanhThuService.getHoaDonTrongThang(values[0], values[1]), "Lấy hóa đơn trong tháng thành công");
                }
                case HOA_DON_TRONG_NAM -> {
                    int year = extractYear(request);
                    yield Response.success(doanhThuService.getHoaDonTrongNam(year), "Lấy hóa đơn trong năm thành công");
                }
                case NAM_CO_HOA_DON -> Response.success(doanhThuService.getNamCoHoaDon(), "Lấy danh sách năm có hóa đơn thành công");
                case THANG_CO_HOA_DON_TRONG_NAM -> {
                    int year = extractYear(request);
                    yield Response.success(doanhThuService.getThangCoHoaDonTrongNam(year), "Lấy danh sách tháng có hóa đơn thành công");
                }
                case DOANH_THU_THEO_NGAY -> {
                    int[] values = extractMonthYear(request);
                    yield Response.success(doanhThuService.getDoanhThuTheoNgay(values[0], values[1]), "Lấy doanh thu theo ngày thành công");
                }
            };
        } catch (IllegalArgumentException e) {
            return Response.error("Command doanh thu không hỗ trợ: " + action);
        } catch (Exception e) {
            return Response.error("Không thể xử lý doanh thu: " + e.getMessage());
        }
    }

    private int[] extractMonthYear(Request request) {
        Object data = request.getData();
        if (!(data instanceof int[] values) || values.length < 2) {
            throw new IllegalArgumentException("Dữ liệu tháng/năm không hợp lệ");
        }
        return values;
    }

    private int extractYear(Request request) {
        Object data = request.getData();
        if (!(data instanceof Integer year)) {
            throw new IllegalArgumentException("Dữ liệu năm không hợp lệ");
        }
        return year;
    }
}
