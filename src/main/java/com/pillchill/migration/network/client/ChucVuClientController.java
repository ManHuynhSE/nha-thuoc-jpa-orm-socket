package com.pillchill.migration.network.client;

import com.pillchill.migration.entity.ChucVu;
import com.pillchill.migration.network.communication.Request;
import com.pillchill.migration.network.communication.Response;
import com.pillchill.migration.network.communication.command.ChucVuCM;

import java.util.ArrayList;
import java.util.List;

public class ChucVuClientController {
    private final ClientSessionContext sessionContext;

    public ChucVuClientController(ClientSessionContext sessionContext) {
        this.sessionContext = sessionContext;
    }

    public Response getAllChucVuResponse() {
        Request request = new Request(
            "CHUC_VU." + ChucVuCM.LIST_ALL.name(),
                null,
                sessionContext.getUserId()
        );
        return sessionContext.getNetworkClient().send(request);
    }

    public List<ChucVu> getAllChucVu() {
        Response response = getAllChucVuResponse();
        if (!response.isSuccess()) {
            throw new RuntimeException(response.getMessage());
        }
        Object data = response.getData();
        if (!(data instanceof List<?> rawList)) {
            throw new RuntimeException("Dữ liệu trả về không hợp lệ");
        }

        List<ChucVu> result = new ArrayList<>();
        for (Object item : rawList) {
            if (!(item instanceof ChucVu itemData)) {
                throw new RuntimeException("Dữ liệu trả về chứa phần tử không hợp lệ");
            }
            result.add(itemData);
        }
        return result;
    }

    public Response addChucVuResponse(ChucVu chucVu) {
        Request request = new Request(
            "CHUC_VU." + ChucVuCM.CREATE.name(),
                chucVu,
                sessionContext.getUserId()
        );
        return sessionContext.getNetworkClient().send(request);
    }

    public Response updateChucVuResponse(ChucVu chucVu) {
        Request request = new Request(
            "CHUC_VU." + ChucVuCM.UPDATE.name(),
                chucVu,
                sessionContext.getUserId()
        );
        return sessionContext.getNetworkClient().send(request);
    }

    public Response deleteChucVuResponse(String maChucVu) {
        Request request = new Request(
            "CHUC_VU." + ChucVuCM.DELETE.name(),
                maChucVu,
                sessionContext.getUserId()
        );
        return sessionContext.getNetworkClient().send(request);
    }
}
