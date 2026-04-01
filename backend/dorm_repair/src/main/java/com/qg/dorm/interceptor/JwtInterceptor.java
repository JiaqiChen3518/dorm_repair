package com.qg.dorm.interceptor;

import com.qg.dorm.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * JWT拦截器, 用于拦截请求, 检查token是否有效
 * 如果token有效, 则将用户信息设置到request中
 */
@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 从请求头中获取token
        String token = request.getHeader("Authorization");

        // 检查token是否为空或格式错误
        if (token == null || !token.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"msg\":\"未登录或token无效\"}");
            return false;
        }

        token = token.substring(7);

        if (!JwtUtil.validateToken(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"msg\":\"token已过期\"}");
            return false;
        }

        // 从token中提取用户信息
        Long userId = JwtUtil.getUserId(token);
        String account = JwtUtil.getAccount(token);
        Integer role = JwtUtil.getRole(token);

        // 将token中的用户信息设置到request中, 以便后续处理使用
        request.setAttribute("userId", userId);
        request.setAttribute("account", account);
        request.setAttribute("role", role);

        return true;
    }
}
