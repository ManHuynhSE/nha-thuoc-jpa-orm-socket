package com.pillchill.migration.network.communication;

import java.io.Serializable;

public class Request implements Serializable {

    private final String command;
    private final Object data;
    private final String sessionUserId;

    public Request(String command, Object data, String sessionUserId) {
        this.command = command;
        this.data = data;
        this.sessionUserId = sessionUserId;
    }


    public String getCommand() {
        return command;
    }

    public Object getData() {
        return data;
    }

    public String getSessionUserId() {
        return sessionUserId;
    }
}