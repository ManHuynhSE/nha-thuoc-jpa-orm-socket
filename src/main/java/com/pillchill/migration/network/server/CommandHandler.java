package com.pillchill.migration.network.server;

import com.pillchill.migration.network.communication.Request;
import com.pillchill.migration.network.communication.Response;


public interface CommandHandler {
    Response handle(Request request);
}
