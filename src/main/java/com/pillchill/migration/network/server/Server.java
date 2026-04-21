package com.pillchill.migration.network.server;

import java.io.EOFException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private final int port;
    private final CommandDispatcher dispatcher;

    public Server(int port) {
        this.port = port;
        this.dispatcher = new CommandDispatcher();
    }



    public void start() {
        ExecutorService executor = Executors.newFixedThreadPool(10);
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Prototype server listening at port " + port);
            while (true) {
                Socket socket = serverSocket.accept();
                executor.submit(() -> handleClient(socket));
            }
        } catch (Exception e) {
            throw new RuntimeException("Lỗi server: " + e.getMessage(), e);
        } finally {
            executor.shutdown();
        }
    }

    private void handleClient(Socket socket) {
        try (socket;
             ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream())) {

            while (true) {
                Object raw;
                try {
                    raw = inputStream.readObject();
                } catch (EOFException eofException) {
                    break;
                }
                outputStream.writeObject(dispatcher.dispatch((com.pillchill.migration.network.communication.Request) raw));
                outputStream.flush();
            }
        } catch (Exception e) {
            throw new RuntimeException("Lỗi xử lý client: " + e.getMessage(), e);
        }
    }
}
