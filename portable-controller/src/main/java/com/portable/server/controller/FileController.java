package com.portable.server.controller;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import com.portable.server.annotation.NeedLogin;
import com.portable.server.annotation.PermissionRequirement;
import com.portable.server.model.response.Response;
import com.portable.server.service.ImageService;
import com.portable.server.type.FileStoreType;
import com.portable.server.type.PermissionType;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author shiroha
 */
@Validated
@RestController
@RequestMapping("/api/file")
public class FileController {

    @Resource
    private ImageService imageService;

    private static final Long IMAGE_FILE_MAX_SIZE = 1024 * 1024 * 20L;

    @NeedLogin(normal = true)
    @PostMapping("/image")
    @PermissionRequirement(PermissionType.UPLOAD_FILE)
    public Response<String> uploadImage(MultipartFile fileData) {
        if (IMAGE_FILE_MAX_SIZE.compareTo(fileData.getSize()) < 0) {
            throw PortableErrors.of("A-09-002", IMAGE_FILE_MAX_SIZE);
        }
        try {
            return Response.ofOk(imageService.uploadImage(fileData.getInputStream(), fileData.getOriginalFilename(), fileData.getContentType()));
        } catch (IOException e) {
            throw PortableErrors.of("S-01-003");
        }
    }

    @GetMapping("/get")
    public void get(String id, FileStoreType type, HttpServletResponse response) {
        try {
            String fileType = imageService.get(id, type, response.getOutputStream());
            response.setContentType(fileType);
        } catch (IOException e) {
            throw PortableErrors.of("S-01-002");
        }
    }
}
