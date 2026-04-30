package com.pillchill.migration.network.client;

import com.pillchill.migration.dto.DoanhThuTheoThangDTO;
import com.pillchill.migration.dto.HoaDonKemGiaDTO;
import com.pillchill.migration.dto.ThongKeNhanVienDTO;
import com.pillchill.migration.network.communication.Request;
import com.pillchill.migration.network.communication.Response;
import com.pillchill.migration.network.communication.command.ThongKeNhanVienCM;

import java.util.ArrayList;
import java.util.List;

public class ThongKeNhanVienClientController {
    private final ClientSessionContext sessionContext;

    public ThongKeNhanVienClientController(ClientSessionContext sessionContext) {
        this.sessionContext = sessionContext;
    }

    private Response send(ThongKeNhanVienCM action, Object data) {
        Request request = new Request(
                "THONG_KE_NHAN_VIEN." + action.name(),
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

    public List<Integer> getNamCoHoaDon() {
        return mapListResponse(send(ThongKeNhanVienCM.NAM_CO_HOA_DON, null), Integer.class);
    }

    public List<ThongKeNhanVienDTO> getThongKeDoanhThuNhanVien(int nam) {
        System.out.println("[DEBUG] ThongKeNhanVienClientController.getThongKeDoanhThuNhanVien - nam=" + nam);
        Response response = send(ThongKeNhanVienCM.THONG_KE_DOANH_THU, nam);
        System.out.println("[DEBUG] Response received - success=" + response.isSuccess() + ", data class=" + (response.getData() != null ? response.getData().getClass().getSimpleName() : "null"));
        return mapListResponse(response, ThongKeNhanVienDTO.class);
    }

    public List<DoanhThuTheoThangDTO> getThongKeDoanhThuNhanVienTheoThang(String maNV, int nam) {
        return mapListResponse(send(ThongKeNhanVienCM.THONG_KE_DOANH_THU_THEO_THANG, new Object[]{maNV, nam}), DoanhThuTheoThangDTO.class);
    }

    public List<HoaDonKemGiaDTO> getHoaDonTrongNamCuaNhanVien(int nam, String maNV) {
        return mapListResponse(send(ThongKeNhanVienCM.HOA_DON_TRONG_NAM, new Object[]{maNV, nam}), HoaDonKemGiaDTO.class);
    }
}
