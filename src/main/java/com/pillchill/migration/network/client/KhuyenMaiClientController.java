package com.pillchill.migration.network.client;

import com.pillchill.migration.entity.KhuyenMai;
import com.pillchill.migration.network.communication.Request;
import com.pillchill.migration.network.communication.Response;
import com.pillchill.migration.network.communication.command.KhuyenMaiCM;

import java.util.ArrayList;
import java.util.List;

public class KhuyenMaiClientController {
    private final ClientSessionContext sessionContext;

    public KhuyenMaiClientController(ClientSessionContext sessionContext) {
        this.sessionContext = sessionContext;
    }

    public Response getAllKhuyenMaiResponse() {
        Request request = new Request(
            "KHUYEN_MAI." + KhuyenMaiCM.LIST_ALL.name(),
                null,
                sessionContext.getUserId()
        );
        return sessionContext.getNetworkClient().send(request);
    }

    public List<KhuyenMai> getAllKhuyenMai() {
        Response response = getAllKhuyenMaiResponse();
        if (!response.isSuccess()) {
            throw new RuntimeException(response.getMessage());
        }

        Object data = response.getData();
        if (!(data instanceof List<?> rawList)) {
            throw new RuntimeException("Dữ liệu trả về không hợp lệ");
        }

        List<KhuyenMai> result = new ArrayList<>();
        for (Object item : rawList) {
            if (!(item instanceof KhuyenMai itemData)) {
                throw new RuntimeException("Dữ liệu trả về chứa phần tử không hợp lệ");
            }
            result.add(itemData);
        }
        return result;
    }

    public Response addKhuyenMaiResponse(KhuyenMai khuyenMai) {
        Request request = new Request(
            "KHUYEN_MAI." + KhuyenMaiCM.CREATE.name(),
                khuyenMai,
                sessionContext.getUserId()
        );
        return sessionContext.getNetworkClient().send(request);
    }

    public Response updateKhuyenMaiResponse(KhuyenMai khuyenMai) {
        Request request = new Request(
            "KHUYEN_MAI." + KhuyenMaiCM.UPDATE.name(),
                khuyenMai,
                sessionContext.getUserId()
        );
        return sessionContext.getNetworkClient().send(request);
    }

    public Response deleteKhuyenMaiResponse(String maKhuyenMai) {
        Request request = new Request(
            "KHUYEN_MAI." + KhuyenMaiCM.DELETE.name(),
                maKhuyenMai,
                sessionContext.getUserId()
        );
        return sessionContext.getNetworkClient().send(request);
    }
}
