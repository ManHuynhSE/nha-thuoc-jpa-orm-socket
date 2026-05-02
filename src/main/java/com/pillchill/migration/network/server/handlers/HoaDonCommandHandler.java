package com.pillchill.migration.network.server.handlers;

import com.pillchill.migration.dto.ChiTietHoaDonView;
import com.pillchill.migration.dto.CreateHoaDonCommand;
import com.pillchill.migration.dto.HoaDonItemCommand;
import com.pillchill.migration.dto.HoaDonView;
import com.pillchill.migration.entity.HoaDon;
import com.pillchill.migration.migration.HoaDonJpaDAO;
import com.pillchill.migration.network.communication.*;
import com.pillchill.migration.network.communication.command.HoaDonCM;
import com.pillchill.migration.network.server.CommandHandler;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class HoaDonCommandHandler implements CommandHandler {
    private final HoaDonJpaDAO hoaDonJpaDAO;

    public HoaDonCommandHandler() {
        this.hoaDonJpaDAO = new HoaDonJpaDAO();
    }

    public HoaDonCommandHandler(HoaDonJpaDAO hoaDonJpaDAO) {
        this.hoaDonJpaDAO = hoaDonJpaDAO;
    }

    @Override
    public Response handle(Request request) {
        if (request.getSessionUserId() == null || request.getSessionUserId().isBlank()) {
            return Response.error("Bạn chưa đăng nhập");
        }

        if (request.getCommand() == null || !request.getCommand().startsWith("HOA_DON.")) {
            return Response.error("Command hóa đơn không hợp lệ");
        }

        String action = request.getCommand().substring("HOA_DON.".length());
        try {
            return switch (HoaDonCM.valueOf(action)) {
                case LIST_ALL, GET_5_FIELD_ALL -> {
                    List<HoaDon> result = hoaDonJpaDAO.findAllActiveHoaDon();
                    yield Response.success(result, "Lấy danh sách hóa đơn thành công");
                }
                case LIST_ALL_VIEW -> {
                    yield Response.success(hoaDonJpaDAO.getAllHoaDonViews(), "Tải danh sách hóa đơn thành công");
                }
                case GET_BY_ID -> {
                    String maHoaDon = (String) request.getData();
                    Optional<HoaDon> result = hoaDonJpaDAO.getHoaDonById(maHoaDon) != null 
                        ? Optional.of(hoaDonJpaDAO.getHoaDonById(maHoaDon))
                        : Optional.empty();
                    yield Response.success(result.orElse(null), result.isPresent() ? "Lấy hóa đơn thành công" : "Không tìm thấy hóa đơn");
                }
                case GET_5_FIELD_BY_THUOC -> {
                    String maThuoc = (String) request.getData();
                    List<HoaDon> result = hoaDonJpaDAO.findHoaDonByThuoc(maThuoc);
                    yield Response.success(result, "Lấy danh sách hóa đơn theo thuốc thành công");
                }
                case GET_BY_THANG_NAM -> {
                    int[] values = extractMonthYear(request);
                    List<HoaDonView> result = hoaDonJpaDAO.getHoaDonViewsByMonthYear(values[0], values[1]);
                    yield Response.success(result, "Lấy danh sách hóa đơn theo tháng/năm thành công");
                }
                case GET_CHI_TIET_BY_MA_HOA_DON -> {
                    String maHoaDon = (String) request.getData();
                    List<ChiTietHoaDonView> result = hoaDonJpaDAO.getChiTietHoaDonByMaHoaDon(maHoaDon);
                    yield Response.success(result, "Lấy chi tiết hóa đơn thành công");
                }
                case GET_NAM_CO_HOA_DON -> {
                    List<Integer> result = hoaDonJpaDAO.findNamCoHoaDon();
                    yield Response.success(result, "Lấy danh sách năm có hóa đơn thành công");
                }
                case GET_THANG_CO_HOA_DON_TRONG_NAM -> {
                    int nam = (int) request.getData();
                    List<Integer> result = hoaDonJpaDAO.findThangCoHoaDonTrongNam(nam);
                    yield Response.success(result, "Lấy danh sách tháng có hóa đơn thành công");
                }
                case GET_LATEST -> {
                    yield Response.success(
                            hoaDonJpaDAO.getLatestHoaDon(),
                            "Lấy mã hóa đơn mới nhất thành công"
                    );
                }
                case CREATE -> {
                    if (!(request.getData() instanceof HoaDonCreatePayload payload)
                            || payload.getItems() == null
                            || payload.getItems().isEmpty()) {
                        yield Response.error("Payload tạo hóa đơn không hợp lệ");
                    }
                    String maHoaDon = payload.getMaHoaDon();
                    if (maHoaDon == null || maHoaDon.isBlank()) {
                        yield Response.error("Mã hóa đơn không hợp lệ");
                    }

                    List<HoaDonItemCommand> items = new ArrayList<>();
                    for (HoaDonCreateItemPayload item : payload.getItems()) {
                        if (item == null
                                || item.getMaThuoc() == null
                                || item.getMaThuoc().isBlank()
                                || item.getMaLo() == null
                                || item.getMaLo().isBlank()
                                || item.getSoLuong() <= 0) {
                            yield Response.error("Chi tiết hóa đơn không hợp lệ");
                        }
                        items.add(new HoaDonItemCommand(
                                item.getMaThuoc().trim(),
                                item.getMaLo().trim(),
                                item.getSoLuong(),
                                item.getDonGia()
                        ));
                    }

                    CreateHoaDonCommand command = new CreateHoaDonCommand(
                            maHoaDon.trim(),
                            LocalDate.now(),
                            payload.getGhiChu(),
                            request.getSessionUserId(),
                            payload.getMaKhachHang(),
                            payload.getMaKhuyenMai(),
                            0.10d,
                            "VAT 10%",
                            items
                    );
                    hoaDonJpaDAO.addHoaDon(command);
                    yield Response.success(maHoaDon.trim(), "Tạo hóa đơn thành công");
                }
                case LIST_BY_MONTH_YEAR -> {
                    if (!(request.getData() instanceof HoaDonFilterPayload payload)
                            || payload.getMonth() == null || payload.getYear() == null) {
                        yield Response.error("Payload lọc hóa đơn không hợp lệ");
                    }
                    yield Response.success(
                            hoaDonJpaDAO.getHoaDonViewsByMonthYear(payload.getMonth(), payload.getYear()),
                            "Tải danh sách hóa đơn theo tháng/năm thành công"
                    );
                }
                case LIST_CHI_TIET -> {
                    if (!(request.getData() instanceof String maHoaDon) || maHoaDon.isBlank()) {
                        yield Response.error("Mã hóa đơn không hợp lệ");
                    }
                    yield Response.success(
                            hoaDonJpaDAO.getChiTietHoaDonByMaHoaDon(maHoaDon),
                            "Tải chi tiết hóa đơn thành công"
                    );
                }
            };
        } catch (IllegalArgumentException e) {
            return Response.error("Command hóa đơn không hỗ trợ: " + action);
        } catch (Exception e) {
            return Response.error("Không thể xử lý hóa đơn: " + e.getMessage());
        }
    }

    private int[] extractMonthYear(Request request) {
        Object data = request.getData();
        if (!(data instanceof int[] values) || values.length < 2) {
            throw new IllegalArgumentException("Dữ liệu tháng/năm không hợp lệ");
        }
        return values;
    }
}

