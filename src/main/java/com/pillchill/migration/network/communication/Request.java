package com.pillchill.migration.network.communication;

import java.io.Serializable;

public class Request implements Serializable {

    private final CommandType commandType;
    private final Object data;
    private final String sessionUserId;

    public Request(CommandType commandType, Object data, String sessionUserId) {
        this.commandType = commandType;
        this.data = data;
        this.sessionUserId = sessionUserId;
    }

    public CommandType getCommandType() {
        return commandType;
    }

    public Object getData() {
        return data;
    }

    public String getSessionUserId() {
        return sessionUserId;
    }
}
