package com.pillchill.migration.network.server.handlers;

import com.pillchill.migration.dto.ThuocKemGiaView;
import com.pillchill.migration.migration.ThuocJpaDAO;
import com.pillchill.migration.network.communication.Request;
import com.pillchill.migration.network.communication.Response;
import com.pillchill.migration.network.server.CommandHandler;



import java.util.ArrayList;
import java.util.List;

public class ThuocListCommandHandler implements CommandHandler {
    private final ThuocJpaDAO thuocJpaDAO;

    public ThuocListCommandHandler(ThuocJpaDAO thuocJpaDAO) {
        this.thuocJpaDAO = thuocJpaDAO;
    }



    @Override
    public Response handle(Request request) {
        if (request.getSessionUserId() == null || request.getSessionUserId().isBlank()) {
            return Response.error("Bạn chưa đăng nhập");
        }

        List<com.pillchill.migration.dto.ThuocKemGiaView> thuocKemGiaViews = thuocJpaDAO.getAllThuocKemGia();
        List<ThuocKemGiaView> result = new ArrayList<>();
        for (com.pillchill.migration.dto.ThuocKemGiaView item : thuocKemGiaViews) {
            result.add(new ThuocKemGiaView(
                    item.getMaThuoc(),
                    item.getTenThuoc(),
                    item.getSoLuongTon(),
                    item.getGiaBan(),
                    item.getDonVi(),
                    0,
                    item.getMaNSX(),true
            ));
        }
        return Response.success(result, "Tải danh sách thuốc thành công");
    }
}
