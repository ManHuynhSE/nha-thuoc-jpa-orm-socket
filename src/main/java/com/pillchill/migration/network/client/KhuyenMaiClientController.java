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

    public Response getAllKhuyenMai() {
        Request request = new Request(
                "KHUYEN_MAI." + KhuyenMaiCM.LIST_ALL.toString(),
                null,
                sessionContext.getUserId()
        );
        return sessionContext.getNetworkClient().send(request);
    }

    public Response findByMa(String maKM) {
        Request request = new Request(
                "KHUYEN_MAI." + KhuyenMaiCM.FIND_BY_MA,
                maKM,
                sessionContext.getUserId()
        );
        return sessionContext.getNetworkClient().send(request);
    }

    public Response validate(String maKM) {
        Request request = new Request(
                "KHUYEN_MAI." + KhuyenMaiCM.VALIDATE,
                maKM,
                sessionContext.getUserId()
        );
        return sessionContext.getNetworkClient().send(request);
    }

    public List<KhuyenMai> getAllKhuyenMaiItems() {
        Response response = getAllKhuyenMai();
        if (!response.isSuccess()) {
            throw new RuntimeException(response.getMessage());
        }
        Object data = response.getData();
        if (!(data instanceof List<?> rawList)) {
            throw new RuntimeException("Dữ liệu trả về không hợp lệ");
        }

        List<KhuyenMai> result = new ArrayList<>();
        for (Object item : rawList) {
            if (!(item instanceof KhuyenMai khuyenMai)) {
                throw new RuntimeException("Dữ liệu trả về chứa phần tử không hợp lệ");
            }
            result.add(khuyenMai);
        }
        return result;
    }

    public KhuyenMai findByMaItem(String maKM) {
        Response response = findByMa(maKM);
        if (!response.isSuccess()) {
            throw new RuntimeException(response.getMessage());
        }
        Object data = response.getData();
        if (data == null) {
            return null;
        }
        if (!(data instanceof KhuyenMai khuyenMai)) {
            throw new RuntimeException("Dữ liệu trả về không hợp lệ");
        }
        return khuyenMai;
    }

    public boolean validateItem(String maKM) {
        Response response = validate(maKM);
        if (!response.isSuccess()) {
            return false;
        }
        Object data = response.getData();
        if (!(data instanceof Boolean result)) {
            return false;
        }
        return result;
    }
}
