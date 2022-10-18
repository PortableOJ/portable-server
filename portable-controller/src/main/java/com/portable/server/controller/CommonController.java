package com.portable.server.controller;

import java.io.IOException;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.constraints.NotBlank;

import com.portable.server.constant.Constant;
import com.portable.server.exception.PortableException;
import com.portable.server.model.response.Response;
import com.portable.server.service.CommonService;
import com.portable.server.util.RequestSessionConstant;

import com.alibaba.fastjson.JSONObject;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author shiroha
 */
@Validated
@RestController
@RequestMapping("/api/common")
public class CommonController {

    @Resource
    private CommonService commonService;

    @GetMapping("/version")
    public Response<String> getVersionName() {
        return Response.ofOk(commonService.getVersionName());
    }

    @GetMapping("/enum")
    public Response<Map<String, JSONObject>> getEnumDesc(@NotBlank(message = "A-06-001") String name) {
        return Response.ofOk(commonService.getEnumDesc(name));
    }

    @GetMapping("/captcha")
    public void getCaptcha(HttpServletRequest request, HttpServletResponse response) {
        try {
            String code = commonService.getCaptcha(response.getOutputStream());
            response.setContentType(Constant.CAPTCHA_CONTENT_TYPE);
            HttpSession httpSession = request.getSession();
            httpSession.setAttribute(RequestSessionConstant.CAPTCHA, code);
        } catch (IOException e) {
            throw PortableException.of("S-01-002");
        }
    }
}
