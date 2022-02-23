package com.portable.server.kit.impl;

import com.portable.server.exception.PortableException;
import com.portable.server.kit.FileKit;
import com.portable.server.util.StreamUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author shiroha
 */
@Component
public class FileKitImpl implements FileKit {

    @Override
    public void createDirIfNotExist(String dir) throws PortableException {
        File file = new File(dir);
        if (!file.exists() && !file.mkdirs()) {
            throw PortableException.of("S-04-001");
        }
    }

    @Override
    public void saveFileOrOverwrite(String filePath, InputStream inputStream) throws PortableException {
        try {
            File file = new File(filePath);
            if (!file.exists() && !file.createNewFile()) {
                throw PortableException.of("S-04-003", filePath);
            }
            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
            StreamUtils.copy(inputStream, fileOutputStream);
        } catch (IOException e) {
            throw PortableException.of("S-04-003", filePath);
        }
    }

    @Override
    public void saveFileOrOverwrite(String filePath, byte[] inputStream) throws PortableException {
        try {
            File file = new File(filePath);
            if (!file.exists() && !file.createNewFile()) {
                throw PortableException.of("S-04-003", filePath);
            }
            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
            StreamUtils.write(inputStream, fileOutputStream);
        } catch (IOException e) {
            throw PortableException.of("S-04-003", filePath);
        }
    }

    @Override
    public void appendFile(String filePath, byte[] inputStream) throws PortableException {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                throw PortableException.of("S-04-005", filePath);
            }
            FileOutputStream fileOutputStream = new FileOutputStream(filePath, true);
            StreamUtils.write(inputStream, fileOutputStream);
        } catch (IOException e) {
            throw PortableException.of("S-04-003", filePath);
        }
    }

    @Override
    public void deleteFileIfExist(String filePath) throws PortableException {
        File file = new File(filePath);
        if (file.exists() && !file.delete()) {
            throw PortableException.of("S-04-007", filePath);
        }
    }

    @Override
    public InputStream getFile(String filePath) throws PortableException {
        try {
            return new FileInputStream(filePath);
        } catch (FileNotFoundException e) {
            throw PortableException.of("S-04-005", filePath);
        }
    }
}
