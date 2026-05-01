package com.pillchill.migration.network.server.handlers;

import com.pillchill.migration.dto.HoaDonKemGiaDTO;
import com.pillchill.migration.dto.ThongKeKhachHangDTO;
import com.pillchill.migration.migration.ThongKeKhachHangJpaDAO;
import com.pillchill.migration.network.communication.Request;
import com.pillchill.migration.network.communication.Response;
import com.pillchill.migration.network.communication.command.ThongKeKhachHangCM;
import com.pillchill.migration.network.server.CommandHandler;

import java.util.List;

public class ThongKeKhachHangCommandHandler implements CommandHandler {
    private final ThongKeKhachHangJpaDAO thongKeKhachHangJpaDAO;

    public ThongKeKhachHangCommandHandler(ThongKeKhachHangJpaDAO thongKeKhachHangJpaDAO) {
        this.thongKeKhachHangJpaDAO = thongKeKhachHangJpaDAO;
    }

    @Override
    public Response handle(Request request) {
        if (request.getSessionUserId() == null || request.getSessionUserId().isBlank()) {
            return Response.error("Bạn chưa đăng nhập");
        }

        if (request.getCommand() == null || !request.getCommand().startsWith("THONG_KE_KHACH_HANG.")) {
            return Response.error("Command thống kê khách hàng không hợp lệ");
        }

        String action = request.getCommand().substring("THONG_KE_KHACH_HANG.".length());
        try {
            return switch (ThongKeKhachHangCM.valueOf(action)) {
                case DOANH_THU_TAT_CA -> {
                    List<ThongKeKhachHangDTO> result = thongKeKhachHangJpaDAO.getDoanhThuTatCaKhachHang();
                    yield Response.success(result, "Lấy doanh thu tất cả khách hàng thành công");
                }
                case DOANH_THU_THEO_THANG_NAM -> handleDoanhThuTheoThangNam(request);
                case TIM_KIEM_THEO_MA -> handleTimKiemTheoMa(request);
                case TIM_KIEM_THEO_TEN -> handleTimKiemTheoTen(request);
                case TIM_KIEM_THEO_SDT -> handleTimKiemTheoSdt(request);
                case HOA_DON_THEO_KHACH_HANG -> handleHoaDonTheoKhachHang(request);
            };
        } catch (IllegalArgumentException e) {
            return Response.error("Command thống kê khách hàng không hỗ trợ: " + action);
        } catch (Exception e) {
            return Response.error("Không thể xử lý thống kê khách hàng: " + e.getMessage());
        }
    }

    private Response handleDoanhThuTheoThangNam(Request request) {
        Object[] payload = extractIntPair(request.getData());
        int thang = (Integer) payload[0];
        int nam = (Integer) payload[1];
        List<ThongKeKhachHangDTO> result = thongKeKhachHangJpaDAO.getDoanhThuKhachHangTheoThangNam(thang, nam);
        return Response.success(result, "Lấy doanh thu khách hàng theo tháng/năm thành công");
    }

    private Response handleTimKiemTheoMa(Request request) {
        String maKH = extractString(request.getData());
        List<ThongKeKhachHangDTO> result = thongKeKhachHangJpaDAO.timKiemTheoMaKH(maKH);
        return Response.success(result, "Tìm kiếm theo mã khách hàng thành công");
    }

    private Response handleTimKiemTheoTen(Request request) {
        String tenKH = extractString(request.getData());
        List<ThongKeKhachHangDTO> result = thongKeKhachHangJpaDAO.timKiemTheoTenKH(tenKH);
        return Response.success(result, "Tìm kiếm theo tên khách hàng thành công");
    }

    private Response handleTimKiemTheoSdt(Request request) {
        String sdt = extractString(request.getData());
        List<ThongKeKhachHangDTO> result = thongKeKhachHangJpaDAO.timKiemTheoSoDienThoai(sdt);
        return Response.success(result, "Tìm kiếm theo số điện thoại thành công");
    }

    private Response handleHoaDonTheoKhachHang(Request request) {
        String maKH = extractString(request.getData());
        List<HoaDonKemGiaDTO> result = thongKeKhachHangJpaDAO.getHoaDonTheoKhachHang(maKH);
        return Response.success(result, "Lấy hóa đơn của khách hàng thành công");
    }

    private String extractString(Object data) {
        if (!(data instanceof String str) || str.isBlank()) {
            throw new IllegalArgumentException("Dữ liệu chuỗi không hợp lệ");
        }
        return str;
    }

    private Object[] extractIntPair(Object data) {
        if (!(data instanceof Object[] payload) || payload.length < 2
                || !(payload[0] instanceof Integer) || !(payload[1] instanceof Integer)) {
            throw new IllegalArgumentException("Dữ liệu tháng/năm không hợp lệ");
        }
        return payload;
    }
}
