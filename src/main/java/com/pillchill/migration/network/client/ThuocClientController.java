package com.pillchill.migration.network.client;

import com.pillchill.migration.dto.ThuocKemGiaView;
import com.pillchill.migration.network.communication.Request;
import com.pillchill.migration.network.communication.Response;
import com.pillchill.migration.network.communication.command.ThuocCM;


import java.util.ArrayList;
import java.util.List;

public class ThuocClientController {
    private final com.pillchill.migration.network.client.ClientSessionContext sessionContext;

    public ThuocClientController(com.pillchill.migration.network.client.ClientSessionContext sessionContext) {
        this.sessionContext = sessionContext;
    }

    public Response getAllThuoc() {
        Request request = new Request(
                "THUOC." + ThuocCM.LIST_ALL.name(),
                null,
                sessionContext.getUserId()
        );
        return sessionContext.getNetworkClient().send(request);
    }

    public List<ThuocKemGiaView> getAllThuocItems() {
        Response response = getAllThuoc();
        if (!response.isSuccess()) {
            throw new RuntimeException(response.getMessage());
        }
        Object data = response.getData();
        if (!(data instanceof List<?> rawList)) {
            throw new RuntimeException("Dữ liệu trả về không hợp lệ");
        }

        List<ThuocKemGiaView> result = new ArrayList<>();
        for (Object item : rawList) {
            if (!(item instanceof ThuocKemGiaView thuocItem)) {
                throw new RuntimeException("Dữ liệu trả về chứa phần tử không hợp lệ");
            }
            result.add(thuocItem);
        }
        return result;
    }
}
