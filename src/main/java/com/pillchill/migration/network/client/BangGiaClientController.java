package com.pillchill.migration.network.client;

import com.pillchill.migration.dto.BangGiaDTO;
import com.pillchill.migration.entity.BangGia;
import com.pillchill.migration.entity.KhachHang;
import com.pillchill.migration.network.communication.BangGiaPayLoad;
import com.pillchill.migration.network.communication.Request;
import com.pillchill.migration.network.communication.Response;
import com.pillchill.migration.network.communication.command.BangGiaCM;

import java.util.ArrayList;
import java.util.List;

public class BangGiaClientController {
    private final ClientSessionContext sessionContext;

    public BangGiaClientController(ClientSessionContext sessionContext) {
        this.sessionContext = sessionContext;
    }

    public Response getAllBangGia() {
        Request request = new Request(
                "BANG_GIA." + BangGiaCM.LIST_ALL.name(),
                null,
                sessionContext.getUserId()
        );
        return sessionContext.getNetworkClient().send(request);
    }

    public Response createBangGia(BangGiaPayLoad payload) {
        Request request = new Request(
                "BANG_GIA." + BangGiaCM.CREATE.name(),
                payload,
                sessionContext.getUserId()
        );
        return sessionContext.getNetworkClient().send(request);
    }

    public Response updateBangGia(BangGiaPayLoad payload) {
        Request request = new Request(
                "BANG_GIA." + BangGiaCM.UPDATE.name(),
                payload,
                sessionContext.getUserId()
        );
        return sessionContext.getNetworkClient().send(request);
    }

    public Response deleteBangGia(String maBG) {
        Request request = new Request(
                "BANG_GIA." + BangGiaCM.DELETE.name(),
                maBG,
                sessionContext.getUserId()
        );
        return sessionContext.getNetworkClient().send(request);
    }

    public List<BangGiaDTO> getAllBangGiaItems() {
        Response response = getAllBangGia();
        if (!response.isSuccess()) {
            throw new RuntimeException(response.getMessage());
        }
        Object data = response.getData();
        if (!(data instanceof List<?> rawList)) {
            throw new RuntimeException("Dữ liệu trả về không hợp lệ");
        }

        List<BangGiaDTO> result = new ArrayList<>();
        for (Object item : rawList) {
            if (item instanceof BangGia entity) {
                BangGiaDTO dto = new BangGiaDTO(
                        entity.getMaBangGia(),
                        entity.getTenBangGia(),
                        entity.getLoaiGia(),
                        entity.getNgayApDung(),
                        entity.getNgayKetThuc(),
                        entity.getTrangThai(),
                        entity.getGhiChu(),
                        entity.getDoUuTien(),
                        entity.isActive()
                );
                result.add(dto);
            } else {
                throw new RuntimeException("Dữ liệu trả về chứa phần tử không hợp lệ");
            }
        }
        return result;
    }

    public Response findByMa(String maBG) {
        Request request = new Request(
                "BANG_GIA." + BangGiaCM.FIND_BY_MA.name(),
                maBG,
                sessionContext.getUserId()
        );
        return sessionContext.getNetworkClient().send(request);
    }

    public BangGia findByMaItem(String maBG) {
        Response response = findByMa(maBG);
        if (!response.isSuccess()) {
            throw new RuntimeException(response.getMessage());
        }
        Object data = response.getData();
        if (data == null) {
            return null;
        }
        if (!(data instanceof BangGia bangGia)) {
            throw new RuntimeException("Dữ liệu trả về không hợp lệ");
        }
        return bangGia;
    }

    public BangGia createItem(BangGia bangGia) {
        BangGiaPayLoad payload = new BangGiaPayLoad();
        payload.setMaBangGia(bangGia.getMaBangGia());
        payload.setTenBangGia(bangGia.getTenBangGia());
        payload.setLoaiGia(bangGia.getLoaiGia());
        payload.setGhiChu(bangGia.getGhiChu());
        payload.setNgayApDung(bangGia.getNgayApDung());
        payload.setNgayKetThuc(bangGia.getNgayKetThuc());
        payload.setTrangThai(bangGia.getTrangThai());
        payload.setDoUuTien(bangGia.getDoUuTien());
        payload.setActive(bangGia.isActive());

        Response response = createBangGia(payload);
        if (!response.isSuccess()) {
            throw new RuntimeException(response.getMessage());
        }

        Object data = response.getData();
        if (data == null) {
            return bangGia;
        }
        if (!(data instanceof BangGia result)) {
            throw new RuntimeException("Dữ liệu trả về không hợp lệ");
        }
        return result;
    }



}
