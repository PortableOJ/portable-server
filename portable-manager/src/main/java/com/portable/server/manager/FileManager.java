package com.portable.server.manager;

import com.portable.server.exception.PortableException;

import java.io.InputStream;

public interface FileManager {

    void createDirIfNotExist(String dir) throws PortableException;

    void saveFileOrOverwrite(String filePath, InputStream inputStream) throws PortableException;

    void deleteFileIfExist(String filePath) throws PortableException;

    InputStream getFile(String filePath) throws PortableException;
}
