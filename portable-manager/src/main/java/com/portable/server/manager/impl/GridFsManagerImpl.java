package com.portable.server.manager.impl;

import com.portable.server.exception.PortableException;
import com.portable.server.manager.GridFsManager;
import com.portable.server.model.fs.FileData;
import com.portable.server.repo.GridFsRepo;
import com.portable.server.type.FileStoreType;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.InputStream;

/**
 * @author shiroha
 */
@Component
public class GridFsManagerImpl implements GridFsManager {

    @Resource
    private GridFsRepo gridFsRepo;

    @Override
    public String uploadImage(InputStream inputStream, String name, String contentType) throws PortableException {
        return gridFsRepo.saveFile(inputStream, name, contentType, FileStoreType.IMAGE);
    }

    @Override
    public String uploadAvatar(String lastId, InputStream inputStream, String name, String contentType) throws PortableException {
        if (lastId != null) {
            gridFsRepo.deleteFile(lastId);
        }
        return gridFsRepo.saveFile(inputStream, name, contentType, FileStoreType.AVATAR);
    }

    @Override
    public FileData get(String id, FileStoreType type) throws PortableException {
        return gridFsRepo.getFile(id, type);
    }
}
