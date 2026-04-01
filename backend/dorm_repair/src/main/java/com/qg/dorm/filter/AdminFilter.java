package com.qg.dorm.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qg.dorm.common.Result;
import com.qg.dorm.constant.RoleConstant;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

// @WebFilter(urlPatterns = "/api/order/*", filterName = "adminFilter")
public class AdminFilter implements Filter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final List<String> ADMIN_PATHS = Arrays.asList(
            "/api/order/all",
            "/api/order/updateStatus",
            "/api/order/complete",
            "/api/order/delete",
            "/api/order/statistics"
    );

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession();

        String requestURI = httpRequest.getRequestURI();
        boolean isAdminPath = false;
        for (String adminPath : ADMIN_PATHS) {
            if (requestURI.contains(adminPath)) {
                isAdminPath = true;
                break;
            }
        }

        if (isAdminPath) {
            Integer role = (Integer) session.getAttribute("role");
            if (role == null || role != RoleConstant.ROLE_ADMIN) {
                httpResponse.setContentType("application/json;charset=utf-8");
                httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
                Result<?> result = Result.error(403, "无权限");
                httpResponse.getWriter().write(objectMapper.writeValueAsString(result));
                return;
            }
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}