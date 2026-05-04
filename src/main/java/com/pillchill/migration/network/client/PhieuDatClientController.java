package com.pillchill.migration.network.client;

import com.pillchill.migration.dto.ChiTietPhieuDatView;
import com.pillchill.migration.dto.PhieuDatView;
import com.pillchill.migration.network.communication.PhieuDatCreatePayload;
import com.pillchill.migration.network.communication.PhieuDatFilterPayload;
import com.pillchill.migration.network.communication.Request;
import com.pillchill.migration.network.communication.Response;
import com.pillchill.migration.network.communication.command.PhieuDatCM;

import java.util.ArrayList;
import java.util.List;

public class PhieuDatClientController {
    private final ClientSessionContext sessionContext;

    public PhieuDatClientController(ClientSessionContext sessionContext) {
        this.sessionContext = sessionContext;
    }

    public Response getAllPhieuDat() {
        Request request = new Request(
                "PHIEU_DAT." + PhieuDatCM.LIST_ALL.name(),
                null,
                sessionContext.getUserId()
        );
        return sessionContext.getNetworkClient().send(request);
    }

    public Response getPhieuDatByMonthYear(int month, int year) {
        Request request = new Request(
                "PHIEU_DAT." + PhieuDatCM.LIST_BY_MONTH_YEAR.name(),
                new PhieuDatFilterPayload(month, year),
                sessionContext.getUserId()
        );
        return sessionContext.getNetworkClient().send(request);
    }

    public Response getChiTietPhieuDatByMaPhieuDat(String maPhieuDat) {
        Request request = new Request(
                "PHIEU_DAT." + PhieuDatCM.LIST_CHI_TIET.name(),
                maPhieuDat,
                sessionContext.getUserId()
        );
        return sessionContext.getNetworkClient().send(request);
    }

    public Response createPhieuDat(PhieuDatCreatePayload payload) {
        Request request = new Request(
                "PHIEU_DAT." + PhieuDatCM.CREATE.name(),
                payload,
                sessionContext.getUserId()
        );
        return sessionContext.getNetworkClient().send(request);
    }

    public List<PhieuDatView> getAllPhieuDatItems() {
        Response response = getAllPhieuDat();
        return parsePhieuDatList(response);
    }

    public List<PhieuDatView> getPhieuDatByMonthYearItems(int month, int year) {
        Response response = getPhieuDatByMonthYear(month, year);
        return parsePhieuDatList(response);
    }

    public List<ChiTietPhieuDatView> getChiTietPhieuDatItems(String maPhieuDat) {
        Response response = getChiTietPhieuDatByMaPhieuDat(maPhieuDat);
        if (!response.isSuccess()) {
            throw new RuntimeException(response.getMessage());
        }
        Object data = response.getData();
        if (!(data instanceof List<?> rawList)) {
            throw new RuntimeException("Dữ liệu trả về không hợp lệ");
        }

        List<ChiTietPhieuDatView> result = new ArrayList<>();
        for (Object item : rawList) {
            if (!(item instanceof ChiTietPhieuDatView chiTietPhieuDatView)) {
                throw new RuntimeException("Dữ liệu trả về chứa phần tử không hợp lệ");
            }
            result.add(chiTietPhieuDatView);
        }
        return result;
    }

    private List<PhieuDatView> parsePhieuDatList(Response response) {
        if (!response.isSuccess()) {
            throw new RuntimeException(response.getMessage());
        }
        Object data = response.getData();
        if (!(data instanceof List<?> rawList)) {
            throw new RuntimeException("Dữ liệu trả về không hợp lệ");
        }

        List<PhieuDatView> result = new ArrayList<>();
        for (Object item : rawList) {
            if (!(item instanceof PhieuDatView phieuDatView)) {
                throw new RuntimeException("Dữ liệu trả về chứa phần tử không hợp lệ");
            }
            result.add(phieuDatView);
        }
        return result;
    }

    public String createPhieuDatItems(PhieuDatCreatePayload payload) {
        Response response = createPhieuDat(payload);
        if (!response.isSuccess()) {
            throw new RuntimeException(response.getMessage());
        }
        Object data = response.getData();
        if (!(data instanceof String maPhieuDat) || maPhieuDat.isBlank()) {
            throw new RuntimeException("Dữ liệu trả về không hợp lệ");
        }
        return maPhieuDat;
    }
}
