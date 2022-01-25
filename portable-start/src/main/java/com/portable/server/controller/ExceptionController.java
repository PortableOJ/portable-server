package com.portable.server.controller;

import com.portable.server.exception.PortableException;
import com.portable.server.util.ExceptionConstant;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author shiroha
 */
@RestController
public class ExceptionController {

    @RequestMapping(value = ExceptionConstant.RETHROW_URL, method = {
            RequestMethod.GET,
            RequestMethod.HEAD,
            RequestMethod.POST,
            RequestMethod.PUT,
            RequestMethod.PATCH,
            RequestMethod.DELETE,
            RequestMethod.OPTIONS,
            RequestMethod.TRACE
    })
    public void rethrow(HttpServletRequest request) throws Exception {
        throw ((Exception) request.getAttribute("error"));
    }

    @RequestMapping(value = ExceptionConstant.NOT_FOUND, method = {
            RequestMethod.GET,
            RequestMethod.HEAD,
            RequestMethod.POST,
            RequestMethod.PUT,
            RequestMethod.PATCH,
            RequestMethod.DELETE,
            RequestMethod.OPTIONS,
            RequestMethod.TRACE
    })
    public void notFound(HttpServletRequest request) throws Exception {
        throw PortableException.of("S-01-001", request);
    }
}
