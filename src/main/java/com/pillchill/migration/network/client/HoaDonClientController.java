package com.pillchill.migration.network.client;

import com.pillchill.migration.dto.ChiTietHoaDonView;
import com.pillchill.migration.dto.HoaDonView;
import com.pillchill.migration.network.communication.HoaDonFilterPayload;
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

    public Response getAllHoaDon() {
        Request request = new Request(
                "HOA_DON." + HoaDonCM.LIST_ALL,
                null,
                sessionContext.getUserId()
        );
        return sessionContext.getNetworkClient().send(request);
    }

    public Response getHoaDonByMonthYear(int month, int year) {
        Request request = new Request(
                "HOA_DON." + HoaDonCM.LIST_BY_MONTH_YEAR,
                new HoaDonFilterPayload(month, year),
                sessionContext.getUserId()
        );
        return sessionContext.getNetworkClient().send(request);
    }

    public Response getChiTietHoaDonByMaHoaDon(String maHoaDon) {
        Request request = new Request(
                "HOA_DON." + HoaDonCM.LIST_CHI_TIET,
                maHoaDon,
                sessionContext.getUserId()
        );
        return sessionContext.getNetworkClient().send(request);
    }

    public List<HoaDonView> getAllHoaDonItems() {
        Response response = getAllHoaDon();
        return parseHoaDonList(response);
    }

    public List<HoaDonView> getHoaDonByMonthYearItems(int month, int year) {
        Response response = getHoaDonByMonthYear(month, year);
        return parseHoaDonList(response);
    }

    public List<ChiTietHoaDonView> getChiTietHoaDonItems(String maHoaDon) {
        Response response = getChiTietHoaDonByMaHoaDon(maHoaDon);
        if (!response.isSuccess()) {
            throw new RuntimeException(response.getMessage());
        }
        Object data = response.getData();
        if (!(data instanceof List<?> rawList)) {
            throw new RuntimeException("Dữ liệu trả về không hợp lệ");
        }

        List<ChiTietHoaDonView> result = new ArrayList<>();
        for (Object item : rawList) {
            if (!(item instanceof ChiTietHoaDonView chiTietHoaDonView)) {
                throw new RuntimeException("Dữ liệu trả về chứa phần tử không hợp lệ");
            }
            result.add(chiTietHoaDonView);
        }
        return result;
    }

    private List<HoaDonView> parseHoaDonList(Response response) {
        if (!response.isSuccess()) {
            throw new RuntimeException(response.getMessage());
        }
        Object data = response.getData();
        if (!(data instanceof List<?> rawList)) {
            throw new RuntimeException("Dữ liệu trả về không hợp lệ");
        }

        List<HoaDonView> result = new ArrayList<>();
        for (Object item : rawList) {
            if (!(item instanceof HoaDonView hoaDonView)) {
                throw new RuntimeException("Dữ liệu trả về chứa phần tử không hợp lệ");
            }
            result.add(hoaDonView);
        }
        return result;
    }
}
