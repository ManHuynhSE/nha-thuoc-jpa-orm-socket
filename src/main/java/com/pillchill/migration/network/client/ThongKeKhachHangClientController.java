package com.pillchill.migration.network.client;

import com.pillchill.migration.dto.HoaDonKemGiaDTO;
import com.pillchill.migration.dto.ThongKeKhachHangDTO;
import com.pillchill.migration.network.communication.Request;
import com.pillchill.migration.network.communication.Response;
import com.pillchill.migration.network.communication.command.ThongKeKhachHangCM;

import java.util.ArrayList;
import java.util.List;

public class ThongKeKhachHangClientController {
    private final ClientSessionContext sessionContext;

    public ThongKeKhachHangClientController(ClientSessionContext sessionContext) {
        this.sessionContext = sessionContext;
    }

    private Response send(ThongKeKhachHangCM action, Object data) {
        Request request = new Request(
                "THONG_KE_KHACH_HANG." + action.name(),
                data,
                sessionContext.getUserId()
        );
        return sessionContext.getNetworkClient().send(request);
    }

    private <T> List<T> mapListResponse(Response response, Class<T> elementType) {
        if (!response.isSuccess()) {
            throw new RuntimeException(response.getMessage());
        }
        Object data = response.getData();
        if (!(data instanceof List<?> rawList)) {
            throw new RuntimeException("Dữ liệu trả về không hợp lệ");
        }

        List<T> result = new ArrayList<>();
        for (Object item : rawList) {
            if (!elementType.isInstance(item)) {
                throw new RuntimeException("Dữ liệu trả về chứa phần tử không hợp lệ");
            }
            result.add(elementType.cast(item));
        }
        return result;
    }

    public List<ThongKeKhachHangDTO> getDoanhThuTatCaKhachHang() {
        return mapListResponse(send(ThongKeKhachHangCM.DOANH_THU_TAT_CA, null), ThongKeKhachHangDTO.class);
    }

    public List<ThongKeKhachHangDTO> getDoanhThuKhachHangTheoThangNam(int thang, int nam) {
        return mapListResponse(send(ThongKeKhachHangCM.DOANH_THU_THEO_THANG_NAM, new Object[]{thang, nam}), ThongKeKhachHangDTO.class);
    }

    public List<ThongKeKhachHangDTO> timKiemTheoMaKH(String maKH) {
        return mapListResponse(send(ThongKeKhachHangCM.TIM_KIEM_THEO_MA, maKH), ThongKeKhachHangDTO.class);
    }

    public List<ThongKeKhachHangDTO> timKiemTheoTenKH(String tenKH) {
        return mapListResponse(send(ThongKeKhachHangCM.TIM_KIEM_THEO_TEN, tenKH), ThongKeKhachHangDTO.class);
    }

    public List<ThongKeKhachHangDTO> timKiemTheoSoDienThoai(String soDienThoai) {
        return mapListResponse(send(ThongKeKhachHangCM.TIM_KIEM_THEO_SDT, soDienThoai), ThongKeKhachHangDTO.class);
    }

    public List<HoaDonKemGiaDTO> getHoaDonTheoKhachHang(String maKH) {
        return mapListResponse(send(ThongKeKhachHangCM.HOA_DON_THEO_KHACH_HANG, maKH), HoaDonKemGiaDTO.class);
    }
}
