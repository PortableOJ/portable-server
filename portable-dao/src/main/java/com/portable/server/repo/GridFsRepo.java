package com.portable.server.repo;

import java.io.IOException;
import java.io.InputStream;

import javax.annotation.Resource;

import com.portable.server.exception.PortableException;
import com.portable.server.model.fs.FileData;
import com.portable.server.type.FileStoreType;

import com.mongodb.client.gridfs.model.GridFSFile;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Repository;

/**
 * @author shiroha
 */
@Repository
public class GridFsRepo {

    @Resource
    private GridFsTemplate gridFsTemplate;

    public String saveFile(InputStream inputStream, String name, String contentType, FileStoreType fileStoreType) {
        if (!fileStoreType.getContentTypePattern().matcher(contentType).matches()) {
            throw PortableException.of("A-09-001", contentType, fileStoreType);
        }
        ObjectId objectId = gridFsTemplate.store(inputStream, name, contentType, null);
        return objectId.toString();
    }

    public void deleteFile(String id) {
        gridFsTemplate.delete(new Query(Criteria.where("_id").is(id)));
    }

    public FileData getFile(String id, FileStoreType fileStoreType) {
        GridFSFile file = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(id)));
        if (file == null) {
            return fileStoreType.getFile();
        }
        try {
            GridFsResource gridFsResource = gridFsTemplate.getResource(file);
            if (!fileStoreType.getContentTypePattern().matcher(gridFsResource.getContentType()).matches()) {
                throw PortableException.of("A-09-001", gridFsResource.getContentType(), fileStoreType);
            }
            return FileData.builder()
                    .inputStream(gridFsResource.getInputStream())
                    .contentType(gridFsResource.getContentType())
                    .build();
        } catch (IOException e) {
            return fileStoreType.getFile();
        }
    }
}
