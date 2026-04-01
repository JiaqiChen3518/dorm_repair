package com.qg.dorm.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qg.dorm.common.Result;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

// @WebFilter(urlPatterns = "/api/*", filterName = "loginFilter")
public class LoginFilter implements Filter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession();

        // 获取请求路径
        String requestURI = httpRequest.getRequestURI();

        // 排除登录、注册和验证码接口
        if (requestURI.contains("/api/user/login") || requestURI.contains("/api/user/register") || requestURI.contains("/api/captcha")) {
            chain.doFilter(request, response);
            return;
        }

        // 检查是否登录
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            httpResponse.setContentType("application/json;charset=utf-8");
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            Result<?> result = Result.error(401, "未登录");
            httpResponse.getWriter().write(objectMapper.writeValueAsString(result));
            return;
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}