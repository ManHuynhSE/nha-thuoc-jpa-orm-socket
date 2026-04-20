package com.pillchill.migration.network.client;

import com.pillchill.migration.network.communication.Request;
import com.pillchill.migration.network.communication.Response;


import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class NetworkClient implements Closeable {
    private final Socket socket;
    private final ObjectOutputStream outputStream;
    private final ObjectInputStream inputStream;

    public NetworkClient(String host, int port) {
        try {
            this.socket = new Socket(host, port);
            this.outputStream = new ObjectOutputStream(socket.getOutputStream());
            this.inputStream = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException("Không thể kết nối tới server: " + e.getMessage(), e);
        }
    }

    public synchronized Response send(Request request) {
        try {
            outputStream.writeObject(request);
            outputStream.flush();
            return (Response) inputStream.readObject();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi gửi/nhận request: " + e.getMessage(), e);
        }
    }

    @Override
    public void close() {
        try {
            inputStream.close();
        } catch (IOException ignored) {
        }
        try {
            outputStream.close();
        } catch (IOException ignored) {
        }
        try {
            socket.close();
        } catch (IOException ignored) {
        }
    }
}
