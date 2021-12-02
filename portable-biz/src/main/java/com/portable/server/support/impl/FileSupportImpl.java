package com.portable.server.support.impl;

import com.portable.server.exception.PortableException;
import com.portable.server.manager.FileManager;
import com.portable.server.support.FileSupport;
import com.portable.server.type.LanguageType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.InputStream;

@Component
public class FileSupportImpl implements FileSupport {

    @Value("${portable.home}")
    private String homeDir;

    @Value("${portable.problem}")
    private String problemDir;

    @Value("${portable.solution}")
    private String solutionDir;

    @Resource
    private FileManager fileManager;

    @PostConstruct
    private void init() throws PortableException {
        fileManager.createDirIfNotExist(homeDir);
        fileManager.createDirIfNotExist(homeDir + problemDir);
        fileManager.createDirIfNotExist(homeDir + solutionDir);
    }
}
