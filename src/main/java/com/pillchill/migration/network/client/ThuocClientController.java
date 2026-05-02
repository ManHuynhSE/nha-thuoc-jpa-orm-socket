package com.pillchill.migration.network.client;

import com.pillchill.migration.dto.ThongKeThuoc;
import com.pillchill.migration.dto.ThuocKemGiaView;
import com.pillchill.migration.entity.Thuoc;
import com.pillchill.migration.network.communication.Request;
import com.pillchill.migration.network.communication.Response;
import com.pillchill.migration.network.communication.command.ThuocCM;


import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class ThuocClientController {
    private final com.pillchill.migration.network.client.ClientSessionContext sessionContext;

    public ThuocClientController(com.pillchill.migration.network.client.ClientSessionContext sessionContext) {
        this.sessionContext = sessionContext;
    }

    private Response send(ThuocCM action, Object data) {
        Request request = new Request(
                "THUOC." + action.name(),
                data,
                sessionContext.getUserId()
        );
        return sessionContext.getNetworkClient().send(request);
    }

    private <T> List<T> mapListResponse(Response response, Class<T> elementType) {
        if (!response.isSuccess()) {
            throw new RuntimeException(response.getMessage());
        }
        Object data = response.getData();
        if (!(data instanceof List<?> rawList)) {
            throw new RuntimeException("Dữ liệu trả về không hợp lệ");
        }

        List<T> result = new ArrayList<>();
        for (Object item : rawList) {
            if (!elementType.isInstance(item)) {
                throw new RuntimeException("Dữ liệu trả về chứa phần tử không hợp lệ");
            }
            result.add(elementType.cast(item));
        }
        return result;
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

    public List<ThongKeThuoc> thongKeThuocTheoNgay(Date ngay, int topN) {
        return mapListResponse(
                send(ThuocCM.THONG_KE_THEO_NGAY, new Object[]{ngay, topN}),
                ThongKeThuoc.class
        );
    }

    public List<ThongKeThuoc> thongKeThuocTheoThang(int thang, int nam, int topN) {
        return mapListResponse(
                send(ThuocCM.THONG_KE_THEO_THANG, new int[]{thang, nam, topN}),
                ThongKeThuoc.class
        );
    }

    public List<ThongKeThuoc> thongKeThuocTheoNam(int nam, int topN) {
        return mapListResponse(
                send(ThuocCM.THONG_KE_THEO_NAM, new int[]{nam, topN}),
                ThongKeThuoc.class
        );
    }

    public double getTongDoanhThuThuocTheoNgay(Date ngay) {
        Response response = send(ThuocCM.TONG_DOANH_THU_THUOC_NGAY, ngay);
        if (!response.isSuccess()) {
            throw new RuntimeException(response.getMessage());
        }
        Object data = response.getData();
        return (data instanceof Number number) ? number.doubleValue() : 0d;
    }

    public double getTongDoanhThuThuocTheoThang(int thang, int nam) {
        Response response = send(ThuocCM.TONG_DOANH_THU_THUOC_THANG, new int[]{thang, nam});
        if (!response.isSuccess()) {
            throw new RuntimeException(response.getMessage());
        }
        Object data = response.getData();
        return (data instanceof Number number) ? number.doubleValue() : 0d;
    }

    public double getTongDoanhThuThuocTheoNam(int nam) {
        Response response = send(ThuocCM.TONG_DOANH_THU_THUOC_NAM, nam);
        if (!response.isSuccess()) {
            throw new RuntimeException(response.getMessage());
        }
        Object data = response.getData();
        return (data instanceof Number number) ? number.doubleValue() : 0d;
    }

    public Thuoc getThuocById(String maThuoc) {
        Response response = send(ThuocCM.GET_BY_ID, maThuoc);
        if (!response.isSuccess()) {
            throw new RuntimeException(response.getMessage());
        }
        Object data = response.getData();
        if (data instanceof Thuoc thuoc) {
            return thuoc;
        }
        return null;
    }
}

