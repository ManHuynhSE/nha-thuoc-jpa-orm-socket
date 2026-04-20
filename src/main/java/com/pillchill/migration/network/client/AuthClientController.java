package com.pillchill.migration.network.client;

import com.pillchill.migration.network.communication.CommandType;
import com.pillchill.migration.network.communication.LoginPayload;
import com.pillchill.migration.network.communication.Request;
import com.pillchill.migration.network.communication.Response;


public class AuthClientController {
    private final NetworkClient networkClient;

    public AuthClientController(NetworkClient networkClient) {
        this.networkClient = networkClient;
    }

    public Response login(String username, String password) {
        Request request = new Request(
                CommandType.LOGIN,
                new LoginPayload(username, password),
                null
        );
        return networkClient.send(request);
    }
}
