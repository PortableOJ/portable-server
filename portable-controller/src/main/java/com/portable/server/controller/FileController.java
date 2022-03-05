package com.portable.server.controller;

import com.portable.server.annotation.NeedLogin;
import com.portable.server.exception.PortableException;
import com.portable.server.model.response.Response;
import com.portable.server.service.FileService;
import com.portable.server.type.FileStoreType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author shiroha
 */
@RestController
@RequestMapping("/api/file")
public class FileController {

    @Resource
    private FileService fileService;

    @NeedLogin
    @PostMapping("/avatar")
    public Response<String> uploadAvatar(MultipartFile fileData) throws PortableException {
        try {
            return Response.ofOk(fileService.uploadAvatar(fileData.getInputStream(), fileData.getContentType()));
        } catch (IOException e) {
            throw PortableException.of("S-01-003");
        }
    }

    @NeedLogin
    @PostMapping("/image")
    public Response<String> uploadImage(MultipartFile fileData) throws PortableException {
        try {
            return Response.ofOk(fileService.uploadImage(fileData.getInputStream(), fileData.getContentType()));
        } catch (IOException e) {
            throw PortableException.of("S-01-003");
        }
    }

    @NeedLogin
    @PostMapping("/get")
    public void get(String id, FileStoreType type, HttpServletResponse response) throws PortableException {
        try {
            fileService.get(id, type, response.getOutputStream());
        } catch (IOException e) {
            throw PortableException.of("S-01-002");
        }
    }
}
