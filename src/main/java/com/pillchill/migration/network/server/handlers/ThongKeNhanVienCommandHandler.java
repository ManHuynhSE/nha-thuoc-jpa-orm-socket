package com.pillchill.migration.network.server.handlers;

import com.pillchill.migration.dto.DoanhThuTheoThangDTO;
import com.pillchill.migration.dto.HoaDonKemGiaDTO;
import com.pillchill.migration.dto.ThongKeNhanVienDTO;
import com.pillchill.migration.migration.ThongKeNhanVienJpaDAO;
import com.pillchill.migration.network.communication.Request;
import com.pillchill.migration.network.communication.Response;
import com.pillchill.migration.network.communication.command.ThongKeNhanVienCM;
import com.pillchill.migration.network.server.CommandHandler;

import java.util.List;

public class ThongKeNhanVienCommandHandler implements CommandHandler {
    private final ThongKeNhanVienJpaDAO thongKeNhanVienJpaDAO;

    public ThongKeNhanVienCommandHandler(ThongKeNhanVienJpaDAO thongKeNhanVienJpaDAO) {
        this.thongKeNhanVienJpaDAO = thongKeNhanVienJpaDAO;
    }

    @Override
    public Response handle(Request request) {
        if (request.getSessionUserId() == null || request.getSessionUserId().isBlank()) {
            return Response.error("Bạn chưa đăng nhập");
        }

        if (request.getCommand() == null || !request.getCommand().startsWith("THONG_KE_NHAN_VIEN.")) {
            return Response.error("Command thống kê nhân viên không hợp lệ");
        }

        String action = request.getCommand().substring("THONG_KE_NHAN_VIEN.".length());
        try {
            return switch (ThongKeNhanVienCM.valueOf(action)) {
                case NAM_CO_HOA_DON -> Response.success(thongKeNhanVienJpaDAO.getNamCoHoaDon(), "Lấy danh sách năm có hóa đơn thành công");
                case THONG_KE_DOANH_THU -> handleThongKeDoanhThu(request);
                case THONG_KE_DOANH_THU_THEO_THANG -> handleThongKeDoanhThuTheoThang(request);
                case HOA_DON_TRONG_NAM -> handleHoaDonTrongNam(request);
            };
        } catch (IllegalArgumentException e) {
            return Response.error("Command thống kê nhân viên không hỗ trợ: " + action);
        } catch (Exception e) {
            return Response.error("Không thể xử lý thống kê nhân viên: " + e.getMessage());
        }
    }

    private Response handleThongKeDoanhThu(Request request) {
        int nam = extractYear(request.getData());
        List<ThongKeNhanVienDTO> result = thongKeNhanVienJpaDAO.getThongKeDoanhThuNhanVien(nam);
        return Response.success(result, "Lấy thống kê doanh thu nhân viên thành công");
    }

    private Response handleThongKeDoanhThuTheoThang(Request request) {
        Object[] payload = extractEmployeeYear(request.getData());
        String maNV = (String) payload[0];
        int nam = (Integer) payload[1];
        List<DoanhThuTheoThangDTO> result = thongKeNhanVienJpaDAO.getThongKeDoanhThuNhanVienTheoThang(maNV, nam);
        return Response.success(result, "Lấy doanh thu theo tháng của nhân viên thành công");
    }

    private Response handleHoaDonTrongNam(Request request) {
        Object[] payload = extractEmployeeYear(request.getData());
        String maNV = (String) payload[0];
        int nam = (Integer) payload[1];
        List<HoaDonKemGiaDTO> result = thongKeNhanVienJpaDAO.getHoaDonTrongNamCuaNhanVien(nam, maNV);
        return Response.success(result, "Lấy hóa đơn trong năm của nhân viên thành công");
    }

    private int extractYear(Object data) {
        if (!(data instanceof Integer year)) {
            throw new IllegalArgumentException("Dữ liệu năm không hợp lệ");
        }
        return year;
    }

    private Object[] extractEmployeeYear(Object data) {
        if (!(data instanceof Object[] payload) || payload.length < 2 || !(payload[0] instanceof String) || !(payload[1] instanceof Integer)) {
            throw new IllegalArgumentException("Dữ liệu nhân viên/năm không hợp lệ");
        }
        return payload;
    }
}
