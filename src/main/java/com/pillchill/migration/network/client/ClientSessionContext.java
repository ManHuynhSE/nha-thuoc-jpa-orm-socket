package com.pillchill.migration.network.client;



public class ClientSessionContext {
    private final NetworkClient networkClient;
    private final String userId;

    public ClientSessionContext(NetworkClient networkClient, String userId) {
        this.networkClient = networkClient;
        this.userId = userId;
    }

    public NetworkClient getNetworkClient() {
        return networkClient;
    }

    public String getUserId() {
        return userId;
    }
}
