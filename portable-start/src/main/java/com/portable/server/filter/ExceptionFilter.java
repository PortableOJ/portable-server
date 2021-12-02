package com.portable.server.filter;

import com.portable.server.util.ExceptionConstant;
import org.springframework.core.annotation.Order;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author keqing
 */
@Order(1)
@WebFilter(urlPatterns = "/*", filterName = "exceptionFilter")
public class ExceptionFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) {}

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } catch (Exception e) {
            request.setAttribute("error", e);
            request.getRequestDispatcher(ExceptionConstant.RETHROW_URL).forward(request, response);
        }
    }

    @Override
    public void destroy() {}
}
