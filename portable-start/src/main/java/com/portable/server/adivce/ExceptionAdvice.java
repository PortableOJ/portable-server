package com.portable.server.adivce;

import com.portable.server.exception.ExceptionTextType;
import com.portable.server.exception.PortableException;
import com.portable.server.model.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

/**
 * @author shiroha
 */
@Slf4j
@RestControllerAdvice
public class ExceptionAdvice {

    @Resource
    private MessageSource messageSource;

    private String getMessage(String code, Object... objects) {
        String msg = messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
        Object[] textObjects = Arrays.stream(objects).map(o -> {
            if (o instanceof ExceptionTextType) {
                return ((ExceptionTextType) o).getText();
            }
            return o;
        }).toArray();
        return String.format(msg, textObjects);
    }

    private Response<Void> getResponse(PortableException e) {
        return Response.ofFail(e.getCode(), getMessage(e.getCode(), e.getObjects()));
    }

    @Order(1)
    @ResponseBody
    @ExceptionHandler(value = PortableException.class)
    public Response<Void> exceptionPortableHandler(HttpServletRequest httpServletRequest, PortableException e) {
        Response<Void> response = getResponse(e);
        log.error(response.getMsg() + "[URI]: " + httpServletRequest.getRequestURI());
        return response;
    }

    @Order(2)
    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public Response<Void> exceptionSupperHandler(HttpServletRequest httpServletRequest, Exception e) {
        if (e instanceof PortableException) {
            return exceptionPortableHandler(httpServletRequest, (PortableException) e);
        }

        log.error(e.getMessage() + "\n\t[URI]: " + httpServletRequest.getRequestURI());
        return getResponse(PortableException.systemDefaultException());
    }
}
