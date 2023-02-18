package com.portable.server.struct.impl;

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

import com.portable.server.exception.PortableErrors;
import com.portable.server.struct.PartitionHelper;
import com.portable.server.util.StreamUtils;

import org.springframework.beans.factory.annotation.Value;

/**
 * @author shiroha
 */
public class FilePartitionHelperImpl implements PartitionHelper {

    @Value("${portable.home}")
    private String homeDir;

    /**
     * 当前任务需要的工作目录
     */
    private final String dirName;

    /**
     * 当前工作路径的绝对路径
     */
    private String absoluteFileDir;

    public FilePartitionHelperImpl(String dirName) {
        this.dirName = dirName;
    }

    @PostConstruct
    private void init() {
        File file = new File(String.format("%s%s%s", homeDir, File.separator, dirName));
        this.absoluteFileDir = file.getAbsolutePath();

        if (!file.exists() && !file.mkdirs()) {
            throw PortableErrors.of("S-04-001");
        }
    }

    @Override
    public void createDirIfNotExist(String dir) {
        File file = getFile(dir);
        if (!file.exists() && !file.mkdirs()) {
            throw PortableErrors.of("S-04-001");
        }
    }

    @Override
    public void saveFileOrOverwrite(String filePath, InputStream inputStream) {
        try {
            File file = getFile(filePath);
            if (!file.exists() && !file.createNewFile()) {
                throw PortableErrors.of("S-04-003", filePath);
            }
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            StreamUtils.copy(inputStream, fileOutputStream);
        } catch (IOException e) {
            throw PortableErrors.of("S-04-003", filePath);
        }
    }

    @Override
    public OutputStream saveFileOrOverwrite(String filePath) {
        try {
            File file = getFile(filePath);
            if (!file.exists() && !file.createNewFile()) {
                throw PortableErrors.of("S-04-003", filePath);
            }
            return Files.newOutputStream(file.toPath());
        } catch (IOException e) {
            throw PortableErrors.of("S-04-003", filePath);
        }
    }

    @Override
    public void saveFileOrOverwrite(String filePath, byte[] inputStream) {
        try {
            File file = getFile(filePath);
            if (!file.exists() && !file.createNewFile()) {
                throw PortableErrors.of("S-04-003", filePath);
            }
            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
            StreamUtils.write(inputStream, fileOutputStream);
        } catch (IOException e) {
            throw PortableErrors.of("S-04-003", filePath);
        }
    }

    @Override
    public void appendFile(String filePath, byte[] inputStream) {
        try {
            File file = getFile(filePath);
            if (!file.exists()) {
                throw PortableErrors.of("S-04-005", filePath);
            }
            FileOutputStream fileOutputStream = new FileOutputStream(filePath, true);
            StreamUtils.write(inputStream, fileOutputStream);
        } catch (IOException e) {
            throw PortableErrors.of("S-04-003", filePath);
        }
    }

    @Override
    public void deleteFileIfExist(String filePath) {
        File file = getFile(filePath);
        if (file.exists() && !file.delete()) {
            throw PortableErrors.of("S-04-007", filePath);
        }
    }

    @Override
    public InputStream getFileInput(String filePath) {
        try {
            return new FileInputStream(getFile(filePath));
        } catch (FileNotFoundException e) {
            throw PortableErrors.of("S-04-005", filePath);
        }
    }

    @Override
    public List<File> getDirectoryFile() {
        File[] files = new File(absoluteFileDir).listFiles();
        if (files == null) {
            throw PortableErrors.of("S-04-009", absoluteFileDir);
        }
        return Arrays.stream(files)
                // ignore file
                .filter(file -> !file.getName().startsWith("."))
                .collect(Collectors.toList());
    }

    @Override
    public List<File> getDirectoryFile(String filePath) {
        File[] files = getFile(filePath).listFiles();
        if (files == null) {
            throw PortableErrors.of("S-04-009", filePath);
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
        return new File(String.format("%s%s%s", absoluteFileDir, File.separator, filePath));
    }
}
