package com.portable.server.kit.impl;

import com.portable.server.exception.PortableException;
import com.portable.server.kit.FileKit;
import com.portable.server.util.StreamUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author shiroha
 */
@Component
public class FileKitImpl implements FileKit {

    @Value("${portable.home}")
    private String homeDir;

    @PostConstruct
    private void init() throws PortableException {
        File file = new File(homeDir);
        if (!file.exists() && !file.mkdirs()) {
            throw PortableException.of("S-04-001");
        }
    }

    @Override
    public void createDirIfNotExist(String dir) throws PortableException {
        File file = getFile(dir);
        if (!file.exists() && !file.mkdirs()) {
            throw PortableException.of("S-04-001");
        }
    }

    @Override
    public void saveFileOrOverwrite(String filePath, InputStream inputStream) throws PortableException {
        try {
            File file = getFile(filePath);
            if (!file.exists() && !file.createNewFile()) {
                throw PortableException.of("S-04-003", filePath);
            }
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            StreamUtils.copy(inputStream, fileOutputStream);
        } catch (IOException e) {
            throw PortableException.of("S-04-003", filePath);
        }
    }

    @Override
    public OutputStream saveFileOrOverwrite(String filePath) throws PortableException {
        try {
            File file = getFile(filePath);
            if (!file.exists() && !file.createNewFile()) {
                throw PortableException.of("S-04-003", filePath);
            }
            return new FileOutputStream(file);
        } catch (IOException e) {
            throw PortableException.of("S-04-003", filePath);
        }
    }

    @Override
    public void saveFileOrOverwrite(String filePath, byte[] inputStream) throws PortableException {
        try {
            File file = getFile(filePath);
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
            File file = getFile(filePath);
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
        File file = getFile(filePath);
        if (file.exists() && !file.delete()) {
            throw PortableException.of("S-04-007", filePath);
        }
    }

    @Override
    public InputStream getFileInput(String filePath) throws PortableException {
        try {
            return new FileInputStream(getFile(filePath));
        } catch (FileNotFoundException e) {
            throw PortableException.of("S-04-005", filePath);
        }
    }

    @Override
    public List<File> getDirectoryFile(String filePath) throws PortableException {
        File[] files = getFile(filePath).listFiles();
        if (files == null) {
            throw PortableException.of("S-04-009", filePath);
        }
        return Arrays.stream(files)
                // sorry for testing on macbook
                .filter(file -> !".DS_Store".equals(file.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public Boolean moveFile(String from, String to) {
        File file = getFile(from);
        return file.renameTo(getFile(to));
    }

    private File getFile(String filePath) {
        return new File(String.format("%s%s%s", homeDir, File.separator, filePath));
    }
}
