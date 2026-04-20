package com.pillchill.migration.network.communication;


import java.io.Serializable;

public class Response implements Serializable {
    private final boolean success;
    private final Object data;
    private final String message;

    public Response(boolean success, Object data, String message) {
        this.success = success;
        this.data = data;
        this.message = message;
    }

    public static Response success(Object data, String message) {
        return new Response(true, data, message);
    }

    public static Response error(String message) {
        return new Response(false, null, message);
    }

    public boolean isSuccess() {
        return success;
    }

    public Object getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }
}
