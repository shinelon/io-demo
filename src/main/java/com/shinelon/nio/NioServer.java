package com.shinelon.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/***
 *
 * NioServer.java
 *
 * @author syq
 *
 *         2018年7月25日
 */
public class NioServer {

    public static final int port = 8089;

    public static void main(String[] args) throws IOException {

        Selector selector = Selector.open();
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);
        ssc.socket().bind(new InetSocketAddress("127.0.0.1", port));
        ssc.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("server start...");
        while (true) {
            int n = selector.select();
            Set<SelectionKey> readyKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = readyKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                // 避免重复处理相同的SelectionKey
                iterator.remove();
                // 客户端请求连接事件
                if (key.isAcceptable()) {
                    ServerSocketChannel server = (ServerSocketChannel) key.channel();
                    // 获得客户端连接通道
                    SocketChannel channel = server.accept();
                    channel.configureBlocking(false);
                    // 向客户端发消息
                    channel.write(ByteBuffer.wrap(new String(" hello client ").getBytes()));
                    // 在与客户端连接成功后，为客户端通道注册SelectionKey.OP_READ事件。
                    channel.register(selector, SelectionKey.OP_READ);
                    printWihtThread("客户端请求连接事件");
                } else if (key.isReadable()) {// 有可读数据事件
                    // 获取客户端传输数据可读取消息通道。
                    SocketChannel channel = (SocketChannel) key.channel();
                    // 创建读取数据缓冲器
                    ByteBuffer buffer = ByteBuffer.allocate(12);
                    int read = channel.read(buffer);
                    byte[] data = buffer.array();
                    String message = new String(data);
                    printWihtThread("receive message from client :" + message);
                    ByteBuffer outbuffer = ByteBuffer.wrap((" ").getBytes());
                    channel.write(outbuffer);
                    channel.close();
                } else if (key.isWritable()) {
                    printWihtThread("客户端写事件");
                }

            }
        }
    }

    private static void printWihtThread(String msg) {
        System.out.println("Thread name [" + Thread.currentThread().getName() + "] " + msg);
    }
}
