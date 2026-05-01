package com.pillchill.migration.network.server.handlers;

import com.pillchill.migration.dto.ThongKeThuoc;
import com.pillchill.migration.dto.ThuocKemGiaView;
import com.pillchill.migration.entity.Thuoc;
import com.pillchill.migration.migration.ThuocJpaDAO;
import com.pillchill.migration.network.communication.Request;
import com.pillchill.migration.network.communication.Response;
import com.pillchill.migration.network.communication.command.ThuocCM;
import com.pillchill.migration.network.server.CommandHandler;

import java.sql.Date;
import java.util.List;

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

        if (request.getCommand() == null || !request.getCommand().startsWith("THUOC.")) {
            return Response.error("Command thuốc không hợp lệ");
        }

        String action = request.getCommand().substring("THUOC.".length());
        try {
            return switch (ThuocCM.valueOf(action)) {
                case THONG_KE_THEO_NGAY -> {
                    Object[] values = extractDateAndTopN(request);
                    Date ngay = (Date) values[0];
                    int topN = (int) values[1];
                    List<ThongKeThuoc> result = thuocJpaDAO.thongKeThuocTheoNgay(ngay, topN);
                    yield Response.success(result, "Lấy thống kê thuốc theo ngày thành công");
                }
                case THONG_KE_THEO_THANG -> {
                    int[] values = extractMonthYearAndTopN(request);
                    List<ThongKeThuoc> result = thuocJpaDAO.thongKeThuocTheoThang(values[0], values[1], values[2]);
                    yield Response.success(result, "Lấy thống kê thuốc theo tháng thành công");
                }
                case THONG_KE_THEO_NAM -> {
                    int[] values = extractYearAndTopN(request);
                    List<ThongKeThuoc> result = thuocJpaDAO.thongKeThuocTheoNam(values[0], values[1]);
                    yield Response.success(result, "Lấy thống kê thuốc theo năm thành công");
                }
                case TONG_DOANH_THU_THUOC_NGAY -> {
                    Date ngay = extractDate(request);
                    double result = thuocJpaDAO.getTongDoanhThuThuocTheoNgay(ngay);
                    yield Response.success(result, "Lấy tổng doanh thu thuốc theo ngày thành công");
                }
                case TONG_DOANH_THU_THUOC_THANG -> {
                    int[] values = extractMonthYear(request);
                    double result = thuocJpaDAO.getTongDoanhThuThuocTheoThang(values[0], values[1]);
                    yield Response.success(result, "Lấy tổng doanh thu thuốc theo tháng thành công");
                }
                case TONG_DOANH_THU_THUOC_NAM -> {
                    int year = extractYear(request);
                    double result = thuocJpaDAO.getTongDoanhThuThuocTheoNam(year);
                    yield Response.success(result, "Lấy tổng doanh thu thuốc theo năm thành công");
                }
                case GET_BY_ID -> {
                    String maThuoc = (String) request.getData();
                    Thuoc result = thuocJpaDAO.getThuocById(maThuoc);
                    yield Response.success(result, result != null ? "Lấy thuốc thành công" : "Không tìm thấy thuốc");
                }
                case LIST_ALL -> {
                    List<ThuocKemGiaView> result = thuocJpaDAO.getAllThuocKemGia();
                    yield Response.success(result, "Lấy danh sách thuốc thành công");
                }
                case CREATE -> {
                    Thuoc payload = (Thuoc) request.getData();
                    boolean success = thuocJpaDAO.addThuoc(payload);
                    yield success ? Response.success(null, "Thêm thuốc thành công") 
                                  : Response.error("Không thể thêm thuốc");
                }
                case UPDATE -> {
                    Thuoc payload = (Thuoc) request.getData();
                    boolean success = thuocJpaDAO.updateThuoc(payload);
                    yield success ? Response.success(null, "Cập nhật thuốc thành công") 
                                  : Response.error("Không thể cập nhật thuốc");
                }
                case DELETE -> {
                    String maThuoc = (String) request.getData();
                    boolean success = thuocJpaDAO.deleteThuoc(maThuoc);
                    yield success ? Response.success(null, "Xóa thuốc thành công") 
                                  : Response.error("Không thể xóa thuốc");
                }
            };
        } catch (IllegalArgumentException e) {
            return Response.error("Command thuốc không hỗ trợ: " + action);
        } catch (Exception e) {
            return Response.error("Không thể xử lý thuốc: " + e.getMessage());
        }
    }

    private Date extractDate(Request request) {
        Object data = request.getData();
        if (!(data instanceof Date date)) {
            throw new IllegalArgumentException("Dữ liệu ngày không hợp lệ");
        }
        return date;
    }

    private int[] extractMonthYear(Request request) {
        Object data = request.getData();
        if (!(data instanceof int[] values) || values.length < 2) {
            throw new IllegalArgumentException("Dữ liệu tháng/năm không hợp lệ");
        }
        return values;
    }

    private Object[] extractDateAndTopN(Request request) {
        Object data = request.getData();
        if (!(data instanceof Object[] values) || values.length < 2) {
            throw new IllegalArgumentException("Dữ liệu ngày/topN không hợp lệ");
        }
        return values;
    }

    private int[] extractMonthYearAndTopN(Request request) {
        Object data = request.getData();
        if (!(data instanceof int[] values) || values.length < 3) {
            throw new IllegalArgumentException("Dữ liệu tháng/năm/topN không hợp lệ");
        }
        return values;
    }

    private int[] extractYearAndTopN(Request request) {
        Object data = request.getData();
        if (!(data instanceof int[] values) || values.length < 2) {
            throw new IllegalArgumentException("Dữ liệu năm/topN không hợp lệ");
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
