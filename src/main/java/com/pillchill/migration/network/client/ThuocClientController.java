package com.pillchill.migration.network.client;

import com.pillchill.migration.dto.ChiTietLoThuocView;
import com.pillchill.migration.dto.ThuocKemGiaView;
import com.pillchill.migration.dto.ThuocTheoLoView;
import com.pillchill.migration.network.communication.Request;
import com.pillchill.migration.network.communication.Response;
import com.pillchill.migration.network.communication.ThuocPayload;
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
                "THUOC."+ ThuocCM.LIST_ALL.toString(),
                null,
                sessionContext.getUserId()
        );
        return sessionContext.getNetworkClient().send(request);
    }

    public Response getAllThuocByLo() {
        Request request = new Request(
                "THUOC." + ThuocCM.LIST_BY_LO,
                null,
                sessionContext.getUserId()
        );
        return sessionContext.getNetworkClient().send(request);
    }

    public Response getAllChiTietLoThuoc() {
        Request request = new Request(
                "THUOC." + ThuocCM.LIST_CHI_TIET_LO,
                null,
                sessionContext.getUserId()
        );
        return sessionContext.getNetworkClient().send(request);
    }

    public Response createThuoc(String maThuoc, String tenThuoc, int soLuongTon, String donVi, int soLuongToiThieu, String nhaSanXuat, double giaBanDau) {
        Request request = new Request(
                "THUOC." + ThuocCM.CREATE,
                new ThuocPayload(maThuoc, tenThuoc, soLuongTon, donVi, soLuongToiThieu, nhaSanXuat, giaBanDau),
                sessionContext.getUserId()
        );
        return sessionContext.getNetworkClient().send(request);
    }

    public Response updateThuoc(String maThuoc, String tenThuoc, int soLuongTon, String donVi, int soLuongToiThieu, String nhaSanXuat) {
        Request request = new Request(
                "THUOC." + ThuocCM.UPDATE,
                new ThuocPayload(maThuoc, tenThuoc, soLuongTon, donVi, soLuongToiThieu, nhaSanXuat, null),
                sessionContext.getUserId()
        );
        return sessionContext.getNetworkClient().send(request);
    }

    public Response deleteThuoc(String maThuoc) {
        Request request = new Request(
                "THUOC." + ThuocCM.DELETE,
                maThuoc,
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

    public List<ThuocTheoLoView> getAllThuocTheoLoItems() {
        Response response = getAllThuocByLo();
        if (!response.isSuccess()) {
            throw new RuntimeException(response.getMessage());
        }
        Object data = response.getData();
        if (!(data instanceof List<?> rawList)) {
            throw new RuntimeException("Dữ liệu trả về không hợp lệ");
        }

        List<ThuocTheoLoView> result = new ArrayList<>();
        for (Object item : rawList) {
            if (!(item instanceof ThuocTheoLoView thuocItem)) {
                throw new RuntimeException("Dữ liệu trả về chứa phần tử không hợp lệ");
            }
            result.add(thuocItem);
        }
        return result;
    }

    public List<ChiTietLoThuocView> getAllChiTietLoThuocItems() {
        Response response = getAllChiTietLoThuoc();
        if (!response.isSuccess()) {
            throw new RuntimeException(response.getMessage());
        }
        Object data = response.getData();
        if (!(data instanceof List<?> rawList)) {
            throw new RuntimeException("Dữ liệu trả về không hợp lệ");
        }

        List<ChiTietLoThuocView> result = new ArrayList<>();
        for (Object item : rawList) {
            if (!(item instanceof ChiTietLoThuocView chiTietLoThuocItem)) {
                throw new RuntimeException("Dữ liệu trả về chứa phần tử không hợp lệ");
            }
            result.add(chiTietLoThuocItem);
        }
        return result;
    }
}
