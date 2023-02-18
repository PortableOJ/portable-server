package com.portable.server.service.impl;

import java.io.InputStream;
import java.io.OutputStream;

import javax.annotation.Resource;

import com.portable.server.manager.ImageManager;
import com.portable.server.model.fs.FileData;
import com.portable.server.service.ImageService;
import com.portable.server.type.FileStoreType;
import com.portable.server.util.StreamUtils;

import org.springframework.stereotype.Component;

/**
 * @author shiroha
 */
@Component
public class ImageServiceImpl implements ImageService {

    @Resource
    private ImageManager imageManager;

    @Override
    public String uploadImage(InputStream inputStream, String name, String contentType) {
        return imageManager.uploadImage(inputStream, name, contentType);
    }

    @Override
    public String get(String id, FileStoreType type, OutputStream outputStream) {
        FileData fileData = imageManager.getImage(id, type);
        StreamUtils.copy(fileData.getInputStream(), outputStream);
        return fileData.getContentType();
    }
}
