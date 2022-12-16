package com.portable.server.manager.impl.dev;

import java.io.File;
import java.io.InputStream;
import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.portable.server.exception.PortableException;
import com.portable.server.manager.ImageManager;
import com.portable.server.model.fs.FileData;
import com.portable.server.persistent.PartitionHelper;
import com.portable.server.type.FileStoreType;

import org.jetbrains.annotations.NotNull;

/**
 * @author shiroha
 */
public class ImageDevManagerImpl implements ImageManager {

    public static final String IMAGE_DIR = "image_dev";

    @Resource(name = "imageMapper")
    private PartitionHelper imageMapper;

    @PostConstruct
    public void init() {
        imageMapper.createDirIfNotExist(IMAGE_DIR);
    }

    @Override
    public @NotNull String uploadImage(InputStream inputStream, String name, String contentType) {
        imageMapper.saveFileOrOverwrite(createImageFilePath(name), inputStream);
        return name;
    }

    @Override
    public @NotNull String replaceImage(String removeId, InputStream inputStream, String name, String contentType) {
        imageMapper.saveFileOrOverwrite(createImageFilePath(name), inputStream);
        if (!Objects.equals(removeId, name)) {
            imageMapper.deleteFileIfExist(createImageFilePath(name));
        }
        return name;
    }

    @Override
    public @NotNull FileData getImage(String id, FileStoreType type) {
        try {
            FileData fileData = FileData.builder().build();
            InputStream inputStream = imageMapper.getFileInput(createImageFilePath(id));
            fileData.setInputStream(inputStream);
            return fileData;
        } catch (PortableException e) {
            return type.getFile();
        }
    }

    private String createImageFilePath(String fileName) {
        return IMAGE_DIR + File.separator + fileName;
    }
}
