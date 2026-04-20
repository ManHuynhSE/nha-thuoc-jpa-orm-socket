package com.pillchill.migration.network.server.handlers;

import com.pillchill.migration.entity.TaiKhoan;
import com.pillchill.migration.migration.TaiKhoanJpaDAO;
import com.pillchill.migration.network.communication.LoginPayload;
import com.pillchill.migration.network.communication.Request;
import com.pillchill.migration.network.communication.Response;

import com.pillchill.migration.network.server.CommandHandler;



public class AuthCommandHandler implements CommandHandler {
    private final TaiKhoanJpaDAO taiKhoanJpaDAO;

    public AuthCommandHandler(TaiKhoanJpaDAO taiKhoanJpaDAO) {
        this.taiKhoanJpaDAO = taiKhoanJpaDAO;
    }

    @Override
    public Response handle(Request request) {
        Object payload = request.getData();
        if (!(payload instanceof LoginPayload loginPayload)) {
            return Response.error("Payload đăng nhập không hợp lệ");
        }

        TaiKhoan taiKhoan = taiKhoanJpaDAO.kiemTraDangNhap(loginPayload.getUsername(), loginPayload.getPassword());
        if (taiKhoan == null) {
            return Response.error("Sai tài khoản/mật khẩu hoặc bị khóa");
        }
        return Response.success(taiKhoan.getMaNV(), "Đăng nhập thành công");
    }


}
