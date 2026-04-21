package com.pillchill.migration.network.server.handlers;

import com.pillchill.migration.dto.ThuocKemGiaView;
import com.pillchill.migration.migration.ThuocJpaDAO;
import com.pillchill.migration.network.communication.Request;
import com.pillchill.migration.network.communication.Response;
import com.pillchill.migration.network.communication.command.ThuocCM;
import com.pillchill.migration.network.server.CommandHandler;



import java.util.ArrayList;
import java.util.List;

public class ThuocCommandHandler implements CommandHandler {
    private final ThuocJpaDAO thuocJpaDAO;

    public ThuocCommandHandler(ThuocJpaDAO thuocJpaDAO) {
        this.thuocJpaDAO = thuocJpaDAO;
    }



    @Override
    public Response handle(Request request) {
//        if (request.getSessionUserId() == null || request.getSessionUserId().isBlank()) {
//            return Response.error("Bạn chưa đăng nhập");
//        }
        String action = request.getCommand().substring("THUOC.".length());
        ThuocCM cmd = ThuocCM.valueOf(action);

        switch (cmd) {
            case LIST_ALL -> {
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
            case CREATE ->{}
            case DELETE -> {}
            case UPDATE -> {}
        }
        return null;
    }
}
