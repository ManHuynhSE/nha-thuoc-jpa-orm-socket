package com.pillchill.migration.network.server;

import com.pillchill.migration.migration.ChucVuJpaDAO;
import com.pillchill.migration.migration.DonViJpaDAO;
import com.pillchill.migration.migration.KhuyenMaiJpaDAO;
import com.pillchill.migration.migration.NhanVienJpaDAO;
import com.pillchill.migration.migration.TaiKhoanJpaDAO;
import com.pillchill.migration.migration.ThuocJpaDAO;
import com.pillchill.migration.network.communication.CommandType;
import com.pillchill.migration.network.server.handlers.AuthCommandHandler;
import com.pillchill.migration.network.server.handlers.ChucVuCommandHandler;
import com.pillchill.migration.network.server.handlers.DonViCommandHandler;
import com.pillchill.migration.network.server.handlers.KhuyenMaiCommandHandler;
import com.pillchill.migration.network.server.handlers.NhanVienCommandHandler;
import com.pillchill.migration.network.server.handlers.ThuocListCommandHandler;


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
        initHandlers();
    }

    private void initHandlers() {
        dispatcher.register(CommandType.LOGIN, new AuthCommandHandler(new TaiKhoanJpaDAO()));
        dispatcher.register(CommandType.THUOC_LIST_ALL, new ThuocListCommandHandler(new ThuocJpaDAO()));
        NhanVienCommandHandler nhanVienCommandHandler = new NhanVienCommandHandler(new NhanVienJpaDAO());
        dispatcher.register(CommandType.NHAN_VIEN_LIST_ALL, nhanVienCommandHandler);
        dispatcher.register(CommandType.NHAN_VIEN_ADD, nhanVienCommandHandler);
        dispatcher.register(CommandType.NHAN_VIEN_UPDATE, nhanVienCommandHandler);
        dispatcher.register(CommandType.NHAN_VIEN_DELETE, nhanVienCommandHandler);

        ChucVuCommandHandler chucVuCommandHandler = new ChucVuCommandHandler(new ChucVuJpaDAO());
        dispatcher.register(CommandType.CHUC_VU_LIST_ALL, chucVuCommandHandler);
        dispatcher.register(CommandType.CHUC_VU_ADD, chucVuCommandHandler);
        dispatcher.register(CommandType.CHUC_VU_UPDATE, chucVuCommandHandler);
        dispatcher.register(CommandType.CHUC_VU_DELETE, chucVuCommandHandler);

        KhuyenMaiCommandHandler khuyenMaiCommandHandler = new KhuyenMaiCommandHandler(new KhuyenMaiJpaDAO());
        dispatcher.register(CommandType.KHUYEN_MAI_LIST_ALL, khuyenMaiCommandHandler);
        dispatcher.register(CommandType.KHUYEN_MAI_ADD, khuyenMaiCommandHandler);
        dispatcher.register(CommandType.KHUYEN_MAI_UPDATE, khuyenMaiCommandHandler);
        dispatcher.register(CommandType.KHUYEN_MAI_DELETE, khuyenMaiCommandHandler);

        DonViCommandHandler donViCommandHandler = new DonViCommandHandler(new DonViJpaDAO());
        dispatcher.register(CommandType.DON_VI_LIST_ALL, donViCommandHandler);
        dispatcher.register(CommandType.DON_VI_ADD, donViCommandHandler);
        dispatcher.register(CommandType.DON_VI_UPDATE, donViCommandHandler);
        dispatcher.register(CommandType.DON_VI_DELETE, donViCommandHandler);
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
