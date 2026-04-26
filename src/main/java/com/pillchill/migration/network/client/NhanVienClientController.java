package com.pillchill.migration.network.client;

import com.pillchill.migration.entity.NhanVien;
import com.pillchill.migration.network.communication.Request;
import com.pillchill.migration.network.communication.Response;
import com.pillchill.migration.network.communication.NhanVienPayload;
import com.pillchill.migration.network.communication.command.NhanVienCM;

import java.util.ArrayList;
import java.util.List;

public class NhanVienClientController {
    private final ClientSessionContext sessionContext;

    public NhanVienClientController(ClientSessionContext sessionContext) {
        this.sessionContext = sessionContext;
    }

    public Response getAllNhanVien() {
        Request request = new Request(
                "NHAN_VIEN." + NhanVienCM.LIST_ALL.toString(),
                null,
                sessionContext.getUserId()
        );
        return sessionContext.getNetworkClient().send(request);
    }

    public Response createNhanVien(NhanVienPayload payload) {
        Request request = new Request(
                "NHAN_VIEN." + NhanVienCM.CREATE,
                payload,
                sessionContext.getUserId()
        );
        return sessionContext.getNetworkClient().send(request);
    }

    public Response updateNhanVien(NhanVienPayload payload) {
        Request request = new Request(
                "NHAN_VIEN." + NhanVienCM.UPDATE,
                payload,
                sessionContext.getUserId()
        );
        return sessionContext.getNetworkClient().send(request);
    }

    public Response deleteNhanVien(String maNV) {
        Request request = new Request(
                "NHAN_VIEN." + NhanVienCM.DELETE,
                maNV,
                sessionContext.getUserId()
        );
        return sessionContext.getNetworkClient().send(request);
    }

    public List<NhanVien> getAllNhanVienItems() {
        Response response = getAllNhanVien();
        if (!response.isSuccess()) {
            throw new RuntimeException(response.getMessage());
        }
        Object data = response.getData();
        if (!(data instanceof List<?> rawList)) {
            throw new RuntimeException("Dữ liệu trả về không hợp lệ");
        }

        List<NhanVien> result = new ArrayList<>();
        for (Object item : rawList) {
            if (!(item instanceof NhanVien nhanVienItem)) {
                throw new RuntimeException("Dữ liệu trả về chứa phần tử không hợp lệ");
            }
            result.add(nhanVienItem);
        }
        return result;
    }
}
