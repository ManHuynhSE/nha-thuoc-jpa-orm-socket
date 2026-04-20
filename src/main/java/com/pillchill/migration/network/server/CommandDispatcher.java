package com.pillchill.migration.network.server;

import com.pillchill.migration.network.communication.CommandType;
import com.pillchill.migration.network.communication.Request;
import com.pillchill.migration.network.communication.Response;


import java.util.EnumMap;
import java.util.Map;

public class CommandDispatcher {
    private final Map<CommandType, CommandHandler> handlers = new EnumMap<>(CommandType.class);

    public void register(CommandType commandType, CommandHandler handler) {
        handlers.put(commandType, handler);
    }

    public Response dispatch(Request request) {
        if (request == null || request.getCommandType() == null) {
            return Response.error("Request không hợp lệ");
        }
        CommandHandler handler = handlers.get(request.getCommandType());
        if (handler == null) {
            return Response.error("Command không hỗ trợ: " + request.getCommandType());
        }
        return handler.handle(request);
    }
}
