package com.pillchill.migration.network.server;

import com.pillchill.migration.migration.TaiKhoanJpaDAO;
import com.pillchill.migration.migration.ThuocJpaDAO;
import com.pillchill.migration.network.communication.CommandType;
import com.pillchill.migration.network.communication.Request;
import com.pillchill.migration.network.communication.Response;
import com.pillchill.migration.network.server.handlers.AuthCommandHandler;
import com.pillchill.migration.network.server.handlers.ThuocCommandHandler;


import java.util.EnumMap;
import java.util.Map;

public class CommandDispatcher {
    private final Map<String, CommandHandler> domainHandlers = Map.of(
            "AUTH", new AuthCommandHandler(new TaiKhoanJpaDAO()),
            "THUOC", new ThuocCommandHandler(new ThuocJpaDAO())
    );

    public void register(String command, CommandHandler handler) {
        domainHandlers.put(command, handler);
    }

    public Response dispatch(Request req) {
        String[] parts = req.getCommand().split("\\.", 2);
        if (parts.length != 2) return Response.error("commandKey không hợp lệ");
        CommandHandler handler = domainHandlers.get(parts[0]);
        if (handler == null) return Response.error("Domain không hỗ trợ");
        return handler.handle(req);
    }
}
