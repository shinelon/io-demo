package com.shinelon.bio;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/***
 *
 * BioClient.java
 *
 * @author syq
 *
 *         2018年7月25日
 */
public class BioClient {

    public static void main(String[] args) throws UnknownHostException, IOException, InterruptedException {

        ExecutorService executors = Executors.newFixedThreadPool(10);

        for (int i = 0; i < 10; i++) {
            executors.execute(() -> {
                try (Socket socket = new Socket("127.0.0.1", BioServer.port);
                        OutputStream os = socket.getOutputStream();
                        InputStream is = socket.getInputStream();) {
                    String message = "hello server";
                    os.write(message.getBytes("UTF-8"));
                    socket.shutdownOutput();
                    byte[] bytes = new byte[1024];
                    int len;
                    StringBuilder sb = new StringBuilder();
                    while ((len = is.read(bytes)) != -1) {
                        // 注意指定编码格式，发送方和接收方一定要统一，建议使用UTF-8
                        sb.append(new String(bytes, 0, len, "UTF-8"));
                    }
                    System.out.println("get message from server:  " + sb);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
        executors.awaitTermination(100L, TimeUnit.SECONDS);
    }
}
