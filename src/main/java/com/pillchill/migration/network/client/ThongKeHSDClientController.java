package com.pillchill.migration.network.client;

import com.pillchill.migration.dto.LoThuocHetHan;
import com.pillchill.migration.network.communication.Request;
import com.pillchill.migration.network.communication.Response;
import com.pillchill.migration.network.communication.command.ThongKeHSDCM;

import java.util.ArrayList;
import java.util.List;

public class ThongKeHSDClientController {
    private final ClientSessionContext sessionContext;

    public ThongKeHSDClientController(ClientSessionContext sessionContext) {
        this.sessionContext = sessionContext;
    }

    private Response send(ThongKeHSDCM action, Object data) {
        Request request = new Request(
                "THONG_KE_HSD." + action.name(),
                data,
                sessionContext.getUserId()
        );
        return sessionContext.getNetworkClient().send(request);
    }

    private List<LoThuocHetHan> mapListResponse(Response response) {
        if (!response.isSuccess()) {
            throw new RuntimeException(response.getMessage());
        }
        Object data = response.getData();
        if (!(data instanceof List<?> rawList)) {
            throw new RuntimeException("Dữ liệu trả về không hợp lệ");
        }

        List<LoThuocHetHan> result = new ArrayList<>();
        for (Object item : rawList) {
            if (!(item instanceof LoThuocHetHan dto)) {
                throw new RuntimeException("Dữ liệu trả về chứa phần tử không hợp lệ");
            }
            result.add(dto);
        }
        return result;
    }

    public List<LoThuocHetHan> getCacLoThuocHetHan() {
        return mapListResponse(send(ThongKeHSDCM.LO_THUOC_HET_HAN, null));
    }

    public List<LoThuocHetHan> getCacLoThuocSapHetHan(int soNgay) {
        return mapListResponse(send(ThongKeHSDCM.LO_THUOC_SAP_HET_HAN, soNgay));
    }

    public boolean xoaChiTietLoThuoc(String maLo, String maThuoc) {
        Response response = send(ThongKeHSDCM.XOA_CHI_TIET_LO_THUOC, new String[]{maLo, maThuoc});
        if (!response.isSuccess()) {
            throw new RuntimeException(response.getMessage());
        }
        return (Boolean) response.getData();
    }
}
