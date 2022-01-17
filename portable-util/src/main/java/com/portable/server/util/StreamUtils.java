package com.portable.server.util;

import com.portable.server.exception.PortableException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * @author shiroha
 */
public class StreamUtils {

    private static final Integer BUFFER_LEN = 4096;

    public static void copy(InputStream inputStream, OutputStream outputStream) throws PortableException {
        int bytesRead;
        try {
            for (byte[] buffer = new byte[BUFFER_LEN]; (bytesRead = inputStream.read(buffer)) != -1; ) {
                outputStream.write(buffer, 0, bytesRead);
            }
            inputStream.close();
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            throw PortableException.of("S-01-003");
        }
    }

    public static void write(String string, OutputStream outputStream) throws PortableException {
        try {
            outputStream.write(string.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw PortableException.of("S-01-003");
        }
    }

    public static void write(byte[] value, OutputStream outputStream) throws PortableException {
        try {
            outputStream.write(value);
        } catch (IOException e) {
            throw PortableException.of("S-01-003");
        }
    }

    public static String read(InputStream inputStream) throws PortableException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        int bytesRead;
        try {
            for (byte[] buffer = new byte[BUFFER_LEN]; (bytesRead = inputStream.read(buffer)) != -1; ) {
                result.write(buffer, 0, bytesRead);
            }
            inputStream.close();
            return result.toString(Constant.UTF_8);
        } catch (IOException e) {
            throw PortableException.of("S-01-003");
        }
    }

    public static String read(InputStream inputStream, Integer limit) throws PortableException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        int bytesRead;
        try {
            byte[] buffer = new byte[limit];
            bytesRead = inputStream.read(buffer);
            inputStream.close();
            result.write(buffer, 0, bytesRead);
            return result.toString(Constant.UTF_8);
        } catch (IOException e) {
            throw PortableException.of("S-01-003");
        }
    }
}
