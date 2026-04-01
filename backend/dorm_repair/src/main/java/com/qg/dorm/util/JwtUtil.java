package com.qg.dorm.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtUtil {

    // JWT密钥
    private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    // JWT过期时间, 7天
    private static final long EXPIRATION_TIME = 7 * 24 * 60 * 60 * 1000;

    /**
     * 生成JWT
     * @param userId
     * @param account
     * @param role
     * @return
     */
    public static String generateToken(Long userId, String account, Integer role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("account", account);
        claims.put("role", role);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(String.valueOf(userId)) // 主题, 用于存储用户ID
                .setIssuedAt(new Date()) // 签发时间
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // 过期时间
                .signWith(SECRET_KEY)
                .compact();
    }

    /**
     * 解析JWT，获取Claims
     * @param token
     * @return
     */
    public static Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 从JWT中获取用户ID
     * @param token
     * @return
     */
    public static Long getUserId(String token) {
        Claims claims = parseToken(token);
        return Long.valueOf(claims.get("userId").toString());
    }

    /**
     * 从JWT中获取用户账号
     * @param token
     * @return
     */
    public static String getAccount(String token) {
        Claims claims = parseToken(token);
        return claims.get("account").toString();
    }

    /**
     * 从JWT中获取用户角色
     * @param token
     * @return
     */
    public static Integer getRole(String token) {
        Claims claims = parseToken(token);
        return Integer.valueOf(claims.get("role").toString());
    }

    /**
     * 检查JWT是否过期
     * @param token
     * @return
     */
    public static boolean isTokenExpired(String token) {
        try {
            Claims claims = parseToken(token);
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * 验证JWT是否有效
     * @param token
     * @return
     */
    public static boolean validateToken(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }
}
