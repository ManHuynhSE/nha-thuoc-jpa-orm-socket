package com.pillchill.migration.network.client;

import com.pillchill.migration.entity.KhachHang;
import com.pillchill.migration.network.communication.Request;
import com.pillchill.migration.network.communication.Response;
import com.pillchill.migration.network.communication.KhachHangPayload;
import com.pillchill.migration.network.communication.command.KhachHangCM;

import java.util.ArrayList;
import java.util.List;

public class KhachHangClientController {
    private final ClientSessionContext sessionContext;

    public KhachHangClientController(ClientSessionContext sessionContext) {
        this.sessionContext = sessionContext;
    }

    public Response getAllKhachHang() {
        Request request = new Request(
                "KHACH_HANG." + KhachHangCM.LIST_ALL.toString(),
                null,
                sessionContext.getUserId()
        );
        return sessionContext.getNetworkClient().send(request);
    }

    public Response createKhachHang(KhachHangPayload payload) {
        Request request = new Request(
                "KHACH_HANG." + KhachHangCM.CREATE,
                payload,
                sessionContext.getUserId()
        );
        return sessionContext.getNetworkClient().send(request);
    }

    public Response updateKhachHang(KhachHangPayload payload) {
        Request request = new Request(
                "KHACH_HANG." + KhachHangCM.UPDATE,
                payload,
                sessionContext.getUserId()
        );
        return sessionContext.getNetworkClient().send(request);
    }

    public Response deleteKhachHang(String maKH) {
        Request request = new Request(
                "KHACH_HANG." + KhachHangCM.DELETE,
                maKH,
                sessionContext.getUserId()
        );
        return sessionContext.getNetworkClient().send(request);
    }

    public List<KhachHang> getAllKhachHangItems() {
        Response response = getAllKhachHang();
        if (!response.isSuccess()) {
            throw new RuntimeException(response.getMessage());
        }
        Object data = response.getData();
        if (!(data instanceof List<?> rawList)) {
            throw new RuntimeException("Dữ liệu trả về không hợp lệ");
        }

        List<KhachHang> result = new ArrayList<>();
        for (Object item : rawList) {
            if (!(item instanceof KhachHang khachItem)) {
                throw new RuntimeException("Dữ liệu trả về chứa phần tử không hợp lệ");
            }
            result.add(khachItem);
        }
        return result;
    }
}
