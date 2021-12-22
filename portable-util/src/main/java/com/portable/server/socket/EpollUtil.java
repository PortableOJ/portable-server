package com.portable.server.socket;

import com.portable.server.socket.model.AbstractEpollResponse;
import com.portable.server.socket.model.BufferReader;
import com.portable.server.util.Constant;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

/**
 * @author shiroha
 */
public class EpollUtil {

    private static final Integer BUFFER_LEN = 4096;
    private static final ByteBuffer RETURN_BUFFER = ByteBuffer.allocate(1);

    private static Selector selector;

    private static EpollManager epollManager;

    static {
        RETURN_BUFFER.put(Constant.RETURN_BYTE);
    }

    public static void initEpollSocket(Integer port, EpollManager epollManager) throws Exception {
        EpollUtil.epollManager = epollManager;

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(port));
        serverSocketChannel.configureBlocking(false);
        selector = Selector.open();

        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (selector.select() > 0) {
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey selectionKey = iterator.next();
                iterator.remove();
                if (selectionKey.isAcceptable()) {
                    acceptHandler(selectionKey);
                } else if (selectionKey.isReadable()) {
                    readHandler(selectionKey);
                }
            }
        }
    }

    private static void readHandler(SelectionKey key) {
        SocketChannel client = (SocketChannel) key.channel();
        BufferReader buffer = (BufferReader) key.attachment();
        try {
            while (buffer.read()) {
                Object response = epollManager.call(client.getRemoteAddress().toString(), buffer.getMethod(), buffer.getData());
                if (response == null) {
                    writeFail(client);
                    writeEnd(client);
                } else if (response instanceof AbstractEpollResponse) {
                    writeSuccess(client);
                    write(client, ((AbstractEpollResponse) response).toResponse());
                    writeEnd(client);
                } else if (response instanceof File) {
                    writeSuccess(client);
                    writeFile(client, (File) response);
                } else if (response instanceof String) {
                    writeSuccess(client);
                    write(client, ((String) response).getBytes(StandardCharsets.UTF_8));
                    writeEnd(client);
                } else {
                    writeFail(client);
                    writeEnd(client);
                }
                buffer.init();
            }
            if (buffer.hasClosed()) {
                client.close();
                key.cancel();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void acceptHandler(SelectionKey selectionKey) throws IOException {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) selectionKey.channel();
        SocketChannel accept = serverSocketChannel.accept();
        System.out.println(accept.getRemoteAddress());
        accept.configureBlocking(false);
        BufferReader bufferReader = new BufferReader(accept);
        accept.register(selector, SelectionKey.OP_READ, bufferReader);
    }

    private static void write(SocketChannel channel, byte[] data) throws IOException {
        if (data.length == 0) {
            return;
        }
        channel.write(ByteBuffer.wrap(Integer.valueOf(data.length).toString().getBytes(StandardCharsets.UTF_8)));

        writeReturn(channel);

        channel.write(ByteBuffer.wrap(data));
    }

    private static void write(SocketChannel channel, byte[] data, int bytesLen) throws IOException {
        if (bytesLen == 0) {
            return;
        }
        channel.write(ByteBuffer.wrap(Integer.valueOf(bytesLen).toString().getBytes(StandardCharsets.UTF_8)));
        writeReturn(channel);
        channel.write(ByteBuffer.wrap(data));
    }

    private static void writeEnd(SocketChannel channel) throws IOException {
        channel.write(ByteBuffer.wrap("0\n".getBytes(StandardCharsets.UTF_8)));
    }

    private static void writeSuccess(SocketChannel channel) throws IOException {
        channel.write(ByteBuffer.wrap("0\n".getBytes(StandardCharsets.UTF_8)));
    }

    private static void writeFail(SocketChannel channel) throws IOException {
        channel.write(ByteBuffer.wrap("1\n".getBytes(StandardCharsets.UTF_8)));
    }

    private static void writeFile(SocketChannel channel, File file) throws IOException {
        InputStream inputStream = new FileInputStream(file);
        int bytesRead;
        for (byte[] buffer = new byte[BUFFER_LEN]; (bytesRead = inputStream.read(buffer)) > 0; ) {
            write(channel, buffer, bytesRead);
        }
    }

    private synchronized static void writeReturn(SocketChannel channel) throws IOException {
        RETURN_BUFFER.flip();
        channel.write(RETURN_BUFFER);
    }
}
