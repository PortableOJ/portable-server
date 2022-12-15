package com.portable.server.persistent.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import com.portable.server.exception.PortableException;
import com.portable.server.persistent.PartitionHelper;
import com.portable.server.util.StreamUtils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author shiroha
 */
@Component
public class FilePartitionHelperImpl implements PartitionHelper {

    @Value("${portable.home}")
    private String homeDir;

    @PostConstruct
    private void init() {
        File file = new File(homeDir);
        if (!file.exists() && !file.mkdirs()) {
            throw PortableException.of("S-04-001");
        }
    }

    @Override
    public void createDirIfNotExist(String dir) {
        File file = getFile(dir);
        if (!file.exists() && !file.mkdirs()) {
            throw PortableException.of("S-04-001");
        }
    }

    @Override
    public void saveFileOrOverwrite(String filePath, InputStream inputStream) {
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
    public OutputStream saveFileOrOverwrite(String filePath) {
        try {
            File file = getFile(filePath);
            if (!file.exists() && !file.createNewFile()) {
                throw PortableException.of("S-04-003", filePath);
            }
            return Files.newOutputStream(file.toPath());
        } catch (IOException e) {
            throw PortableException.of("S-04-003", filePath);
        }
    }

    @Override
    public void saveFileOrOverwrite(String filePath, byte[] inputStream) {
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
    public void appendFile(String filePath, byte[] inputStream) {
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
    public void deleteFileIfExist(String filePath) {
        File file = getFile(filePath);
        if (file.exists() && !file.delete()) {
            throw PortableException.of("S-04-007", filePath);
        }
    }

    @Override
    public InputStream getFileInput(String filePath) {
        try {
            return new FileInputStream(getFile(filePath));
        } catch (FileNotFoundException e) {
            throw PortableException.of("S-04-005", filePath);
        }
    }

    @Override
    public List<File> getDirectoryFile(String filePath) {
        File[] files = getFile(filePath).listFiles();
        if (files == null) {
            throw PortableException.of("S-04-009", filePath);
        }
        return Arrays.stream(files)
                // ignore file
                .filter(file -> !file.getName().startsWith("."))
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
