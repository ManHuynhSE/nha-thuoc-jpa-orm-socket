package com.pillchill.migration.network.client;

import com.pillchill.migration.entity.DonVi;
import com.pillchill.migration.network.communication.Request;
import com.pillchill.migration.network.communication.Response;
import com.pillchill.migration.network.communication.command.DonViCM;

import java.util.ArrayList;
import java.util.List;

public class DonViClientController {
    private final ClientSessionContext sessionContext;

    public DonViClientController(ClientSessionContext sessionContext) {
        this.sessionContext = sessionContext;
    }

    public Response getAllDonViResponse() {
        Request request = new Request(
            "DON_VI." + DonViCM.LIST_ALL.name(),
                null,
                sessionContext.getUserId()
        );
        return sessionContext.getNetworkClient().send(request);
    }

    public List<DonVi> getAllDonVi() {
        Response response = getAllDonViResponse();
        if (!response.isSuccess()) {
            throw new RuntimeException(response.getMessage());
        }

        Object data = response.getData();
        if (!(data instanceof List<?> rawList)) {
            throw new RuntimeException("Dữ liệu trả về không hợp lệ");
        }

        List<DonVi> result = new ArrayList<>();
        for (Object item : rawList) {
            if (!(item instanceof DonVi itemData)) {
                throw new RuntimeException("Dữ liệu trả về chứa phần tử không hợp lệ");
            }
            result.add(itemData);
        }
        return result;
    }

    public Response addDonViResponse(DonVi donVi) {
        Request request = new Request(
            "DON_VI." + DonViCM.CREATE.name(),
                donVi,
                sessionContext.getUserId()
        );
        return sessionContext.getNetworkClient().send(request);
    }

    public Response updateDonViResponse(DonVi donVi) {
        Request request = new Request(
            "DON_VI." + DonViCM.UPDATE.name(),
                donVi,
                sessionContext.getUserId()
        );
        return sessionContext.getNetworkClient().send(request);
    }

    public Response deleteDonViResponse(String maDonVi) {
        Request request = new Request(
            "DON_VI." + DonViCM.DELETE.name(),
                maDonVi,
                sessionContext.getUserId()
        );
        return sessionContext.getNetworkClient().send(request);
    }
}
