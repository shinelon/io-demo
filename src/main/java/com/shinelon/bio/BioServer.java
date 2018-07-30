package com.shinelon.bio;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/***
 *
 * BioDemo.java
 *
 * @author syq
 *
 *         2018年7月25日
 */
public class BioServer {

    public static int port = 8089;

    public static void main(String[] args) throws IOException {
        ExecutorService executors = Executors.newFixedThreadPool(2);
        try (ServerSocket sever = new ServerSocket(port)) {
            System.out.println("server start...");
            while (true) {
                Socket socket = sever.accept();
                executors.execute(() -> {
                    try (OutputStream out = socket.getOutputStream(); InputStream is = socket.getInputStream();) {
                        byte[] bytes = new byte[1024];
                        int len;
                        StringBuilder sb = new StringBuilder();
                        while ((len = is.read(bytes)) != -1) {
                            sb.append(new String(bytes, 0, len, "UTF-8"));
                            sb.append(" ->");
                            sb.append(Thread.currentThread().getName());
                        }
                        out.write(("hello client work at->" + Thread.currentThread().getName()).getBytes("UTF-8"));
                        System.out.println("get message from client: " + sb);
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        }
    }
}
