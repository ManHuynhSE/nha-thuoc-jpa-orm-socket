package com.pillchill.migration.network.client;

import com.pillchill.migration.dto.ChiTietPhieuNhapView;
import com.pillchill.migration.dto.PhieuNhapImportItem;
import com.pillchill.migration.dto.PhieuNhapView;
import com.pillchill.migration.network.communication.PhieuNhapImportPayload;
import com.pillchill.migration.network.communication.Request;
import com.pillchill.migration.network.communication.Response;
import com.pillchill.migration.network.communication.command.PhieuNhapCM;

import java.util.ArrayList;
import java.util.List;

public class PhieuNhapClientController {
    private final ClientSessionContext sessionContext;

    public PhieuNhapClientController(ClientSessionContext sessionContext) {
        this.sessionContext = sessionContext;
    }

    public Response getAllPhieuNhap() {
        Request request = new Request(
                "PHIEU_NHAP." + PhieuNhapCM.LIST_ALL.name(),
                null,
                sessionContext.getUserId()
        );
        return sessionContext.getNetworkClient().send(request);
    }

    public Response getChiTietPhieuNhapByMaPhieuNhap(String maPhieuNhapThuoc) {
        Request request = new Request(
                "PHIEU_NHAP." + PhieuNhapCM.LIST_CHI_TIET.name(),
                maPhieuNhapThuoc,
                sessionContext.getUserId()
        );
        return sessionContext.getNetworkClient().send(request);
    }

    public Response importFromExcel(List<PhieuNhapImportItem> items) {
        Request request = new Request(
                "PHIEU_NHAP." + PhieuNhapCM.IMPORT_FROM_EXCEL.name(),
                new PhieuNhapImportPayload(items),
                sessionContext.getUserId()
        );
        return sessionContext.getNetworkClient().send(request);
    }

    public List<PhieuNhapView> getAllPhieuNhapItems() {
        Response response = getAllPhieuNhap();
        if (!response.isSuccess()) {
            throw new RuntimeException(response.getMessage());
        }
        Object data = response.getData();
        if (!(data instanceof List<?> rawList)) {
            throw new RuntimeException("Dữ liệu trả về không hợp lệ");
        }

        List<PhieuNhapView> result = new ArrayList<>();
        for (Object item : rawList) {
            if (!(item instanceof PhieuNhapView phieuNhapView)) {
                throw new RuntimeException("Dữ liệu trả về chứa phần tử không hợp lệ");
            }
            result.add(phieuNhapView);
        }
        return result;
    }

    public List<ChiTietPhieuNhapView> getChiTietPhieuNhapItems(String maPhieuNhapThuoc) {
        Response response = getChiTietPhieuNhapByMaPhieuNhap(maPhieuNhapThuoc);
        if (!response.isSuccess()) {
            throw new RuntimeException(response.getMessage());
        }
        Object data = response.getData();
        if (!(data instanceof List<?> rawList)) {
            throw new RuntimeException("Dữ liệu trả về không hợp lệ");
        }

        List<ChiTietPhieuNhapView> result = new ArrayList<>();
        for (Object item : rawList) {
            if (!(item instanceof ChiTietPhieuNhapView chiTietPhieuNhapView)) {
                throw new RuntimeException("Dữ liệu trả về chứa phần tử không hợp lệ");
            }
            result.add(chiTietPhieuNhapView);
        }
        return result;
    }

    public String importFromExcelItems(List<PhieuNhapImportItem> items) {
        Response response = importFromExcel(items);
        if (!response.isSuccess()) {
            throw new RuntimeException(response.getMessage());
        }
        Object data = response.getData();
        if (data == null) {
            throw new RuntimeException("Không nhận được mã phiếu nhập sau khi lưu");
        }
        if (!(data instanceof String maPhieuNhap)) {
            throw new RuntimeException("Dữ liệu trả về không hợp lệ");
        }
        return maPhieuNhap;
    }
}
