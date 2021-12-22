package com.portable.server.socket.model;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import static com.portable.server.util.Constant.RETURN_BYTE;

/**
 * @author shiroha
 */
public class BufferReader {

    private final ByteBuffer byteBuffer;

    private final SocketChannel socketChannel;

    private StringBuilder method;

    private StringBuilder data;

    private Integer len;

    private ReadMode mode;

    private Boolean isClose;

    public Boolean hasClosed() {
        return isClose;
    }

    public String getMethod() {
        return method.toString();
    }

    public String getData() {
        return data.toString();
    }

    public BufferReader(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
        this.byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.flip();
        isClose = false;
        init();
    }

    public void init() {
        method = new StringBuilder();
        data = new StringBuilder();
        len = 0;
        mode = ReadMode.READ_METHOD;
    }

    private Byte get() throws IOException {
        if (!byteBuffer.hasRemaining()) {
            byteBuffer.clear();
            int len = socketChannel.read(byteBuffer);
            byteBuffer.flip();
            if (len == -1) {
                this.isClose = true;
            }
            if (len <= 0) {
                return -1;
            }
        }
        return byteBuffer.get();
    }

    private Boolean readMethod() throws IOException {
        byte b;
        for (b = get(); b != RETURN_BYTE && b != -1; b = get()) {
            method.append((char) b);
        }
        if (b == RETURN_BYTE) {
            mode = ReadMode.READ_LEN;
            return false;
        }
        return true;
    }

    private Boolean readLen() throws IOException {
        byte b;
        for (b = get(); b != RETURN_BYTE && b != -1; b = get()) {
            len *= 10;
            len += b - '0';
        }
        if (b == RETURN_BYTE) {
            mode = ReadMode.READ_DATA;
            return false;
        }
        return true;
    }

    private Boolean readData() throws IOException {
        do {
            byte b = get();
            if (b == -1) {
                return true;
            }
            data.append((char) b);
        } while (--len > 0);
        mode = ReadMode.READ_LEN;
        return false;
    }

    public Boolean read() throws IOException {
        while (true) {
            switch (mode) {
                case READ_METHOD:
                    if (readMethod()) {
                        return false;
                    }
                    break;
                case READ_LEN:
                    if (readLen()) {
                        return false;
                    }
                    break;
                case READ_DATA:
                    if (len == 0) {
                        return true;
                    }
                    if (readData()) {
                        return false;
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private enum ReadMode {

        /**
         * 读取方法
         */
        READ_METHOD,

        /**
         * 读取长度
         */
        READ_LEN,

        /**
         * 读取数据
         */
        READ_DATA
    }
}
