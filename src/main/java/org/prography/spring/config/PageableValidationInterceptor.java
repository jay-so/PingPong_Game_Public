package org.prography.spring.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.prography.spring.common.BussinessException;
import org.springframework.web.servlet.HandlerInterceptor;

import static org.prography.spring.common.ApiResponseCode.BAD_REQUEST;

public class PageableValidationInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String page = request.getParameter("page");
        String size = request.getParameter("size");

        if (page != null && Integer.parseInt(page) < 0) {
            throw new BussinessException(BAD_REQUEST);
        }

        if (size != null && Integer.parseInt(size) < 1) {
            throw new BussinessException(BAD_REQUEST);
        }

        return true;
    }
}
