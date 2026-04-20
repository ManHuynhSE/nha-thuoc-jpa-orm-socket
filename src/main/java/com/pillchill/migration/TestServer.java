package com.pillchill.migration;

import com.pillchill.migration.network.server.Server;


public class TestServer {
    public static void main(String[] args) {
        new Server(9999).start();
    }
}
