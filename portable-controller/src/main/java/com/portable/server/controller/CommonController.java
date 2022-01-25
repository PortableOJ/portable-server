package com.portable.server.controller;

import com.alibaba.fastjson.JSONObject;
import com.portable.server.exception.PortableException;
import com.portable.server.model.response.Response;
import com.portable.server.service.CommonService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author shiroha
 */
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
    public Response<Map<String, JSONObject>> getEnumDesc(String name) throws PortableException {
        return Response.ofOk(commonService.getEnumDesc(name));
    }
}
