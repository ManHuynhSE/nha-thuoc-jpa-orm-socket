package com.pillchill.migration.network.client;

import com.pillchill.migration.dto.HoaDonKemGiaDTO;
import com.pillchill.migration.entity.ChiTietHoaDon;
import com.pillchill.migration.entity.HoaDon;
import com.pillchill.migration.network.communication.HoaDonCreatePayload;
import com.pillchill.migration.network.communication.Request;
import com.pillchill.migration.network.communication.Response;
import com.pillchill.migration.network.communication.command.HoaDonCM;

import java.util.ArrayList;
import java.util.List;

public class HoaDonClientController {
    private final ClientSessionContext sessionContext;

    public HoaDonClientController(ClientSessionContext sessionContext) {
        this.sessionContext = sessionContext;
    }

    private Response send(HoaDonCM action, Object data) {
        Request request = new Request(
                "HOA_DON." + action.name(),
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
    public Response getHoaDonGanNhat() {
        Request request = new Request(
                "HOA_DON." + HoaDonCM.GET_LATEST,
                "",
                sessionContext.getUserId()
        );
        return sessionContext.getNetworkClient().send(request);
    }
    public Response createHoaDon(HoaDonCreatePayload payload) {
        Request request = new Request(
                "HOA_DON." + HoaDonCM.CREATE,
                payload,
                sessionContext.getUserId()
        );
        return sessionContext.getNetworkClient().send(request);
    }



    public List<HoaDon> getAllHoaDon() {
        return mapListResponse(send(HoaDonCM.LIST_ALL, null), HoaDon.class);
    }

    public HoaDon getHoaDonById(String maHoaDon) {
        Response response = send(HoaDonCM.GET_BY_ID, maHoaDon);
        if (!response.isSuccess()) {
            throw new RuntimeException(response.getMessage());
        }
        Object data = response.getData();
        if (data instanceof HoaDon hoaDon) {
            return hoaDon;
        }
        return null;
    }

    public List<HoaDon> getAllHoaDon5Field() {
        return mapListResponse(send(HoaDonCM.GET_5_FIELD_ALL, null), HoaDon.class);
    }



    public List<HoaDon> getAllHoaDon5Field(String maThuoc) {
        return mapListResponse(send(HoaDonCM.GET_5_FIELD_BY_THUOC, maThuoc), HoaDon.class);
    }

    public List<ChiTietHoaDon> getChiTietByMaHoaDon(String maHoaDon) {
        return mapListResponse(send(HoaDonCM.GET_CHI_TIET_BY_MA_HOA_DON, maHoaDon), ChiTietHoaDon.class);
    }

    public List<Integer> getNamCoHoaDon() {
        return mapListResponse(send(HoaDonCM.GET_NAM_CO_HOA_DON, null), Integer.class);
    }

    public List<Integer> getThangCoHoaDonTrongNam(int nam) {
        return mapListResponse(send(HoaDonCM.GET_THANG_CO_HOA_DON_TRONG_NAM, nam), Integer.class);
    }

    public List<HoaDon> getHoaDonByThangNam(int thang, int nam) {
        return mapListResponse(send(HoaDonCM.GET_BY_THANG_NAM, new int[]{thang, nam}), HoaDon.class);
    }

    public String getMaHoaDonGanNhat() {
        Response response = getHoaDonGanNhat();
        if (!response.isSuccess()) {
            throw new RuntimeException(response.getMessage());
        }
        Object data = response.getData();
        if (!(data instanceof String maHoaDon) || maHoaDon.isBlank()) {
            throw new RuntimeException("Dữ liệu trả về không hợp lệ");
        }
        return maHoaDon;
    }
}
