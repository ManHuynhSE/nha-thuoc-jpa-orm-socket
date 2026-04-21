package com.pillchill.migration.network.server;

import com.pillchill.migration.migration.TaiKhoanJpaDAO;
import com.pillchill.migration.migration.ThuocJpaDAO;
import com.pillchill.migration.network.communication.Request;
import com.pillchill.migration.network.communication.Response;
import com.pillchill.migration.network.server.handlers.AuthCommandHandler;
import com.pillchill.migration.network.server.handlers.ThuocCommandHandler;


import java.util.HashMap;
import java.util.Map;

public class CommandDispatcher {
    private final Map<String, CommandHandler> domainHandlers = new HashMap<>();

    public CommandDispatcher() {
        domainHandlers.put("AUTH", new AuthCommandHandler(new TaiKhoanJpaDAO()));
        domainHandlers.put("THUOC", new ThuocCommandHandler(new ThuocJpaDAO()));
    }

    public void register(String command, CommandHandler handler) {
        domainHandlers.put(command, handler);
    }

    public Response dispatch(Request req) {
        if (req == null || req.getCommand() == null) {
            return Response.error("Request không hợp lệ");
        }
        String[] parts = req.getCommand().split("\\.", 2);
        if (parts.length != 2) return Response.error("commandKey không hợp lệ");
        CommandHandler handler = domainHandlers.get(parts[0]);
        if (handler == null) return Response.error("Domain không hỗ trợ");
        return handler.handle(req);
    }
}
