package com.pillchill.migration.network.client;

import com.pillchill.migration.entity.NhaSanXuat;
import com.pillchill.migration.network.communication.Request;
import com.pillchill.migration.network.communication.Response;
import com.pillchill.migration.network.communication.command.NhaSanXuatCM;

import java.util.ArrayList;
import java.util.List;

public class NhaSanXuatClientController {
    private final ClientSessionContext sessionContext;

    public NhaSanXuatClientController(ClientSessionContext sessionContext) {
        this.sessionContext = sessionContext;
    }

    public Response getAllNhaSanXuatResponse() {
        Request request = new Request(
            "NHA_SAN_XUAT." + NhaSanXuatCM.LIST_ALL.name(),
                null,
                sessionContext.getUserId()
        );
        return sessionContext.getNetworkClient().send(request);
    }

    public List<NhaSanXuat> getAllNhaSanXuat() {
        Response response = getAllNhaSanXuatResponse();
        if (!response.isSuccess()) {
            throw new RuntimeException(response.getMessage());
        }

        Object data = response.getData();
        if (!(data instanceof List<?> rawList)) {
            throw new RuntimeException("Dữ liệu trả về không hợp lệ");
        }

        List<NhaSanXuat> result = new ArrayList<>();
        for (Object item : rawList) {
            if (!(item instanceof NhaSanXuat itemData)) {
                throw new RuntimeException("Dữ liệu trả về chứa phần tử không hợp lệ");
            }
            result.add(itemData);
        }
        return result;
    }

    public Response addNhaSanXuatResponse(NhaSanXuat nhaSanXuat) {
        Request request = new Request(
            "NHA_SAN_XUAT." + NhaSanXuatCM.CREATE.name(),
                nhaSanXuat,
                sessionContext.getUserId()
        );
        return sessionContext.getNetworkClient().send(request);
    }

    public Response updateNhaSanXuatResponse(NhaSanXuat nhaSanXuat) {
        Request request = new Request(
            "NHA_SAN_XUAT." + NhaSanXuatCM.UPDATE.name(),
                nhaSanXuat,
                sessionContext.getUserId()
        );
        return sessionContext.getNetworkClient().send(request);
    }

    public Response deleteNhaSanXuatResponse(String maNSX) {
        Request request = new Request(
            "NHA_SAN_XUAT." + NhaSanXuatCM.DELETE.name(),
                maNSX,
                sessionContext.getUserId()
        );
        return sessionContext.getNetworkClient().send(request);
    }
}
