package com.portable.server.manager.impl;

import java.io.InputStream;

import javax.annotation.Resource;

import com.portable.server.manager.GridFsManager;
import com.portable.server.model.fs.FileData;
import com.portable.server.repo.GridFsRepo;
import com.portable.server.type.FileStoreType;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

/**
 * @author shiroha
 */
@Component
public class GridFsManagerImpl implements GridFsManager {

    @Resource
    private GridFsRepo gridFsRepo;

    @Override
    public @NotNull String uploadImage(InputStream inputStream, String name, String contentType) {
        return gridFsRepo.saveFile(inputStream, name, contentType, FileStoreType.IMAGE);
    }

    @Override
    public @NotNull String uploadAvatar(String lastId, InputStream inputStream, String name, String contentType) {
        if (lastId != null) {
            gridFsRepo.deleteFile(lastId);
        }
        return gridFsRepo.saveFile(inputStream, name, contentType, FileStoreType.AVATAR);
    }

    @Override
    public @NotNull FileData get(String id, FileStoreType type) {
        return gridFsRepo.getFile(id, type);
    }
}
