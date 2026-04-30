package com.pillchill.migration.network.client;

import com.pillchill.migration.dto.DoanhThuHoaDonDTO;
import com.pillchill.migration.dto.HoaDonKemGiaDTO;
import com.pillchill.migration.network.communication.Request;
import com.pillchill.migration.network.communication.Response;
import com.pillchill.migration.network.communication.command.DoanhThuCM;

import java.util.ArrayList;
import java.util.List;

public class DoanhThuClientController {
    private final ClientSessionContext sessionContext;

    public DoanhThuClientController(ClientSessionContext sessionContext) {
        this.sessionContext = sessionContext;
    }

    private Response send(DoanhThuCM action, Object data) {
        Request request = new Request(
                "DOANH_THU." + action.name(),
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

    public double getDoanhThuCuaThang(int thang, int nam) {
        Response response = send(DoanhThuCM.DOANH_THU_THANG, new int[]{thang, nam});
        if (!response.isSuccess()) {
            throw new RuntimeException(response.getMessage());
        }
        Object data = response.getData();
        return (data instanceof Number number) ? number.doubleValue() : 0d;
    }

    public int getSoHoaDonTheoThang(int thang, int nam) {
        Response response = send(DoanhThuCM.SO_HOA_DON_THANG, new int[]{thang, nam});
        if (!response.isSuccess()) {
            throw new RuntimeException(response.getMessage());
        }
        Object data = response.getData();
        return (data instanceof Number number) ? number.intValue() : 0;
    }

    public int getSoKhachHangCuaThang(int thang, int nam) {
        Response response = send(DoanhThuCM.SO_KHACH_HANG_THANG, new int[]{thang, nam});
        if (!response.isSuccess()) {
            throw new RuntimeException(response.getMessage());
        }
        Object data = response.getData();
        return (data instanceof Number number) ? number.intValue() : 0;
    }

    public double getDoanhThuTrungBinhTheoNgay(int thang, int nam) {
        Response response = send(DoanhThuCM.DOANH_THU_TRUNG_BINH_NGAY, new int[]{thang, nam});
        if (!response.isSuccess()) {
            throw new RuntimeException(response.getMessage());
        }
        Object data = response.getData();
        return (data instanceof Number number) ? number.doubleValue() : 0d;
    }

    public List<HoaDonKemGiaDTO> getHoaDonTrongThang(int thang, int nam) {
        return mapListResponse(send(DoanhThuCM.HOA_DON_TRONG_THANG, new int[]{thang, nam}), HoaDonKemGiaDTO.class);
    }

    public List<HoaDonKemGiaDTO> getHoaDonTrongNam(int nam) {
        return mapListResponse(send(DoanhThuCM.HOA_DON_TRONG_NAM, nam), HoaDonKemGiaDTO.class);
    }

    public List<Integer> getNamCoHoaDon() {
        return mapListResponse(send(DoanhThuCM.NAM_CO_HOA_DON, null), Integer.class);
    }

    public List<Integer> getThangCoHoaDonTrongNam(int nam) {
        return mapListResponse(send(DoanhThuCM.THANG_CO_HOA_DON_TRONG_NAM, nam), Integer.class);
    }

    public List<DoanhThuHoaDonDTO> getDoanhThuTheoNgay(int thang, int nam) {
        return mapListResponse(send(DoanhThuCM.DOANH_THU_THEO_NGAY, new int[]{thang, nam}), DoanhThuHoaDonDTO.class);
    }
}
