package com.pillchill.migration.network.client;

import com.pillchill.migration.dto.ChiTietPhieuDoiTraView;
import com.pillchill.migration.dto.PhieuDoiTraView;
import com.pillchill.migration.network.communication.PhieuDoiTraCreatePayload;
import com.pillchill.migration.network.communication.PhieuDoiTraFilterPayload;
import com.pillchill.migration.network.communication.Request;
import com.pillchill.migration.network.communication.Response;
import com.pillchill.migration.network.communication.command.PhieuDoiTraCM;

import java.util.ArrayList;
import java.util.List;

public class PhieuDoiTraClientController {
    private final ClientSessionContext sessionContext;

    public PhieuDoiTraClientController(ClientSessionContext sessionContext) {
        this.sessionContext = sessionContext;
    }

    public Response getAllPhieuDoiTra() {
        Request request = new Request(
                "PHIEU_DOI_TRA." + PhieuDoiTraCM.LIST_ALL.name(),
                null,
                sessionContext.getUserId()
        );
        return sessionContext.getNetworkClient().send(request);
    }

    public Response getPhieuDoiTraByMonthYear(int month, int year) {
        Request request = new Request(
                "PHIEU_DOI_TRA." + PhieuDoiTraCM.LIST_BY_MONTH_YEAR.name(),
                new PhieuDoiTraFilterPayload(month, year),
                sessionContext.getUserId()
        );
        return sessionContext.getNetworkClient().send(request);
    }

    public Response getChiTietPhieuDoiTraByMaPhieuDoiTra(String maPhieuDoiTra) {
        Request request = new Request(
                "PHIEU_DOI_TRA." + PhieuDoiTraCM.LIST_CHI_TIET.name(),
                maPhieuDoiTra,
                sessionContext.getUserId()
        );
        return sessionContext.getNetworkClient().send(request);
    }

    public Response createPhieuDoiTra(PhieuDoiTraCreatePayload payload) {
        Request request = new Request(
                "PHIEU_DOI_TRA." + PhieuDoiTraCM.CREATE.name(),
                payload,
                sessionContext.getUserId()
        );
        return sessionContext.getNetworkClient().send(request);
    }

    public List<PhieuDoiTraView> getAllPhieuDoiTraItems() {
        Response response = getAllPhieuDoiTra();
        return parsePhieuDoiTraList(response);
    }

    public List<PhieuDoiTraView> getPhieuDoiTraByMonthYearItems(int month, int year) {
        Response response = getPhieuDoiTraByMonthYear(month, year);
        return parsePhieuDoiTraList(response);
    }

    public List<ChiTietPhieuDoiTraView> getChiTietPhieuDoiTraItems(String maPhieuDoiTra) {
        Response response = getChiTietPhieuDoiTraByMaPhieuDoiTra(maPhieuDoiTra);
        if (!response.isSuccess()) {
            throw new RuntimeException(response.getMessage());
        }
        Object data = response.getData();
        if (!(data instanceof List<?> rawList)) {
            throw new RuntimeException("Dữ liệu trả về không hợp lệ");
        }

        List<ChiTietPhieuDoiTraView> result = new ArrayList<>();
        for (Object item : rawList) {
            if (!(item instanceof ChiTietPhieuDoiTraView chiTietPhieuDoiTraView)) {
                throw new RuntimeException("Dữ liệu trả về chứa phần tử không hợp lệ");
            }
            result.add(chiTietPhieuDoiTraView);
        }
        return result;
    }

    private List<PhieuDoiTraView> parsePhieuDoiTraList(Response response) {
        if (!response.isSuccess()) {
            throw new RuntimeException(response.getMessage());
        }
        Object data = response.getData();
        if (!(data instanceof List<?> rawList)) {
            throw new RuntimeException("Dữ liệu trả về không hợp lệ");
        }

        List<PhieuDoiTraView> result = new ArrayList<>();
        for (Object item : rawList) {
            if (!(item instanceof PhieuDoiTraView phieuDoiTraView)) {
                throw new RuntimeException("Dữ liệu trả về chứa phần tử không hợp lệ");
            }
            result.add(phieuDoiTraView);
        }
        return result;
    }

    public String createPhieuDoiTraItems(PhieuDoiTraCreatePayload payload) {
        Response response = createPhieuDoiTra(payload);
        if (!response.isSuccess()) {
            throw new RuntimeException(response.getMessage());
        }
        Object data = response.getData();
        if (!(data instanceof String maPhieuDoiTra) || maPhieuDoiTra.isBlank()) {
            throw new RuntimeException("Dữ liệu trả về không hợp lệ");
        }
        return maPhieuDoiTra;
    }
}
