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
                "KHACH_HANG." + KhachHangCM.LIST_ALL.name(),
                null,
                sessionContext.getUserId()
        );
        return sessionContext.getNetworkClient().send(request);
    }

    public Response createKhachHang(KhachHangPayload payload) {
        Request request = new Request(
                "KHACH_HANG." + KhachHangCM.CREATE.name(),
                payload,
                sessionContext.getUserId()
        );
        return sessionContext.getNetworkClient().send(request);
    }

    public Response updateKhachHang(KhachHangPayload payload) {
        Request request = new Request(
                "KHACH_HANG." + KhachHangCM.UPDATE.name(),
                payload,
                sessionContext.getUserId()
        );
        return sessionContext.getNetworkClient().send(request);
    }

    public Response deleteKhachHang(String maKH) {
        Request request = new Request(
                "KHACH_HANG." + KhachHangCM.DELETE.name(),
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

    public Response findByPhone(String phone) {
        Request request = new Request(
                "KHACH_HANG." + KhachHangCM.FIND_BY_PHONE.name(),
                phone,
                sessionContext.getUserId()
        );
        return sessionContext.getNetworkClient().send(request);
    }

    public Response findByMa(String maKH) {
        Request request = new Request(
                "KHACH_HANG." + KhachHangCM.FIND_BY_MA.name(),
                maKH,
                sessionContext.getUserId()
        );
        return sessionContext.getNetworkClient().send(request);
    }

    public KhachHang findByPhoneItem(String phone) {
        Response response = findByPhone(phone);
        if (!response.isSuccess()) {
            throw new RuntimeException(response.getMessage());
        }
        Object data = response.getData();
        if (data == null) {
            return null;
        }
        if (!(data instanceof KhachHang khachHang)) {
            throw new RuntimeException("Dữ liệu trả về không hợp lệ");
        }
        return khachHang;
    }

    public KhachHang findByMaItem(String maKH) {
        Response response = findByMa(maKH);
        if (!response.isSuccess()) {
            throw new RuntimeException(response.getMessage());
        }
        Object data = response.getData();
        if (data == null) {
            return null;
        }
        if (!(data instanceof KhachHang khachHang)) {
            throw new RuntimeException("Dữ liệu trả về không hợp lệ");
        }
        return khachHang;
    }

    public KhachHang createItem(KhachHang khachHang) {
        KhachHangPayload payload = new KhachHangPayload();
        payload.setMaKH(khachHang.getMaKH());
        payload.setTenKH(khachHang.getTenKH());
        payload.setSoDienThoai(khachHang.getSoDienThoai());
        payload.setDiemTichLuy(khachHang.getDiemTichLuy());
        payload.setActive(khachHang.isActive());

        Response response = createKhachHang(payload);
        if (!response.isSuccess()) {
            throw new RuntimeException(response.getMessage());
        }

        Object data = response.getData();
        if (data == null) {
            return khachHang;
        }
        if (!(data instanceof KhachHang result)) {
            throw new RuntimeException("Dữ liệu trả về không hợp lệ");
        }
        return result;
    }
}
