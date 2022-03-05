package com.portable.server.service.impl;

import com.portable.server.exception.PortableException;
import com.portable.server.manager.GridFsManager;
import com.portable.server.model.fs.FileData;
import com.portable.server.service.FileService;
import com.portable.server.type.FileStoreType;
import com.portable.server.util.StreamUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author shiroha
 */
@Component
public class FileServiceImpl implements FileService {

    @Resource
    private GridFsManager gridFsManager;

    @Override
    public String uploadImage(InputStream inputStream, String name, String contentType) throws PortableException {
        return gridFsManager.uploadImage(inputStream, name, contentType);
    }

    @Override
    public String get(String id, FileStoreType type, OutputStream outputStream) throws PortableException {
        FileData fileData = gridFsManager.get(id, type);
        StreamUtils.copy(fileData.getInputStream(), outputStream);
        return fileData.getContentType();
    }
}
