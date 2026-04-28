package com.pillchill.migration.network.client;

import com.pillchill.migration.dto.NhanVienDTO;
import com.pillchill.migration.network.communication.Request;
import com.pillchill.migration.network.communication.Response;
import com.pillchill.migration.network.communication.command.NhanVienCM;

import java.util.ArrayList;
import java.util.List;

public class NhanVienClientController {
    private final ClientSessionContext sessionContext;

    public NhanVienClientController(ClientSessionContext sessionContext) {
        this.sessionContext = sessionContext;
    }

    public Response getAllNhanVienResponse() {
        Request request = new Request(
            "NHAN_VIEN." + NhanVienCM.LIST_ALL.name(),
                null,
                sessionContext.getUserId()
        );
        return sessionContext.getNetworkClient().send(request);
    }

    public List<NhanVienDTO> getAllNhanVien() {
        Response response = getAllNhanVienResponse();
        if (!response.isSuccess()) {
            throw new RuntimeException(response.getMessage());
        }
        Object data = response.getData();
        if (!(data instanceof List<?> rawList)) {
            throw new RuntimeException("Dữ liệu trả về không hợp lệ");
        }

        List<NhanVienDTO> result = new ArrayList<>();
        for (Object item : rawList) {
            if (!(item instanceof NhanVienDTO itemData)) {
                throw new RuntimeException("Dữ liệu trả về chứa phần tử không hợp lệ");
            }
            result.add(itemData);
        }
        return result;
    }

    public void addNhanVien(NhanVienDTO nhanVienDTO) {
        Request request = new Request(
            "NHAN_VIEN." + NhanVienCM.CREATE.name(),
                nhanVienDTO,
                sessionContext.getUserId()
        );
        Response response = sessionContext.getNetworkClient().send(request);
        if (!response.isSuccess()) {
            throw new RuntimeException(response.getMessage());
        }
    }

    public void updateNhanVien(NhanVienDTO nhanVienDTO) {
        Request request = new Request(
            "NHAN_VIEN." + NhanVienCM.UPDATE.name(),
                nhanVienDTO,
                sessionContext.getUserId()
        );
        Response response = sessionContext.getNetworkClient().send(request);
        if (!response.isSuccess()) {
            throw new RuntimeException(response.getMessage());
        }
    }

    public void deleteNhanVien(String maNhanVien) {
        Request request = new Request(
            "NHAN_VIEN." + NhanVienCM.DELETE.name(),
                maNhanVien,
                sessionContext.getUserId()
        );
        Response response = sessionContext.getNetworkClient().send(request);
        if (!response.isSuccess()) {
            throw new RuntimeException(response.getMessage());
        }
    }
}
