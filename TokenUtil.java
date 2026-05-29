package com.easy.util;

import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import cn.hutool.jwt.signers.JWTSigner;
import cn.hutool.jwt.signers.JWTSignerUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 基于 Hutool 的 JWT Token 工具类
 */
public class TokenUtil {
    // 密钥（生产环境建议配置在配置文件中，且密钥长度不低于 16 位）
    private static final String SECRET_KEY = "easy";

    // Token 过期时间（单位：秒），此处设置为 2 小时
    private static final long EXPIRE_SECONDS = 7200L;

    // 获取签名器（HS256 算法）
    private static JWTSigner getSigner() {
        return JWTSignerUtil.hs256(SECRET_KEY.getBytes());
    }

    /**
     * 从请求中获取 Token（支持多种方式）
     */
    public static String getTokenFromRequest(HttpServletRequest request) {
        // 1. 从 Authorization 请求头获取
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && !authHeader.isEmpty()) {
            // 支持多种格式：Bearer token、Token token、直接 token
            if (authHeader.startsWith("Bearer ")) {
                return authHeader.substring(7);
            } else if (authHeader.startsWith("Token ")) {
                return authHeader.substring(6);
            }
            return authHeader;
        }

        // 2. 从 query 参数获取
        String tokenParam = request.getParameter("token");
        if (tokenParam != null && !tokenParam.isEmpty()) {
            return tokenParam;
        }

        // 3. 从 cookie 获取
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("token".equals(cookie.getName()) ||
                        "userToken".equals(cookie.getName()) ||
                        "authToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        // 4. 从其他常见请求头获取
        String[] headerNames = {"X-Auth-Token", "X-Token", "Token"};
        for (String headerName : headerNames) {
            String headerValue = request.getHeader(headerName);
            if (headerValue != null && !headerValue.isEmpty()) {
                return headerValue;
            }
        }

        return null;
    }

    /**
     * 生成 Token
     * @param userId 用户ID（自定义载荷数据）
     * @return JWT Token 字符串
     */
    public static String generateUserToken(String userId) {
        // 1. 构建载荷数据
        Map<String, Object> payload = new HashMap<>();
        payload.put("userId", userId);
        payload.put("exp", System.currentTimeMillis() / 1000 + EXPIRE_SECONDS); // 添加过期时间
        payload.put("iat", System.currentTimeMillis() / 1000); // 签发时间

        // 2. 生成 Token
        return JWTUtil.createToken(payload, getSigner());
    }

    /**
     * 生成 Token
     * @param adminId 用户ID（自定义载荷数据）
     * @return JWT Token 字符串
     */
    public static String generateAdminToken(String adminId) {
        // 1. 构建载荷数据
        Map<String, Object> payload = new HashMap<>();
        payload.put("adminId", adminId);
        payload.put("exp", System.currentTimeMillis() / 1000 + EXPIRE_SECONDS);
        payload.put("iat", System.currentTimeMillis() / 1000);

        // 2. 生成 Token
        return JWTUtil.createToken(payload, getSigner());
    }

    /**
     * 校验 Token 有效性
     * @param token Token 字符串
     * @return true=有效，false=无效/过期
     */
    public static boolean verifyToken(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }

        try {
            token = cleanToken(token); // 清理 token
            JWT jwt = JWTUtil.parseToken(token).setSigner(getSigner());

            // 校验签名 + 过期时间
            boolean isValid = jwt.verify();
            if (!isValid) {
                return false;
            }

            // 额外检查过期时间
            Object expObj = jwt.getPayload("exp");
            if (expObj != null) {
                long exp = Long.parseLong(expObj.toString());
                long currentTime = System.currentTimeMillis() / 1000;
                if (currentTime > exp) {
                    return false; // Token 已过期
                }
            }

            return true;
        } catch (Exception e) {
            // 签名错误、Token 格式错误、过期等异常均视为无效
            return false;
        }
    }

    /**
     * 清理 Token（移除多余空格和引号）
     */
    private static String cleanToken(String token) {
        if (token == null) return null;

        token = token.trim();

        // 移除可能的多余引号
        if (token.startsWith("\"") && token.endsWith("\"")) {
            token = token.substring(1, token.length() - 1);
        }
        if (token.startsWith("'") && token.endsWith("'")) {
            token = token.substring(1, token.length() - 1);
        }

        return token;
    }

    /**
     * 从 Token 中解析载荷内容
     * @param token Token 字符串
     * @return 载荷数据 Map
     */
    public static Map<String, Object> parseTokenPayload(String token) {
        if (!verifyToken(token)) {
            throw new RuntimeException("Token 无效或已过期");
        }

        token = cleanToken(token);
        return JWTUtil.parseToken(token).getPayload().getClaimsJson();
    }

    /**
     * 从 Token 中获取指定字段值
     * @param token Token 字符串
     * @param field 字段名
     * @return 字段值
     */
    public static Object getFieldFromToken(String token, String field) {
        return parseTokenPayload(token).get(field);
    }

    /**
     * 通过request对象获取token中的登录用户的id
     * @param request
     * @return
     */
    public static int getLoginUserID(HttpServletRequest request) {
        String token = getTokenFromRequest(request);
        if (token == null) {
            return -1;
        }

        try {
            Object obj = getFieldFromToken(token, "userId");
            if (obj == null) {
                return -1;
            }
            return Integer.parseInt(obj.toString());
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * 通过request对象获取token中的登录用户的id
     * @param request
     * @return
     */
    public static int getLoginAdminID(HttpServletRequest request) {
        String token = getTokenFromRequest(request);
        if (token == null) {
            return -1;
        }

        try {
            Object obj = getFieldFromToken(token, "adminId");
            if (obj == null) {
                return -1;
            }
            return Integer.parseInt(obj.toString());
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * 检查请求中是否有有效的 Token
     */
    public static boolean hasValidToken(HttpServletRequest request) {
        String token = getTokenFromRequest(request);
        return verifyToken(token);
    }

    /**
     * 获取用户ID（增强版）
     */
    public static Integer getUserIdFromToken(String token) {
        try {
            if (!verifyToken(token)) {
                return null;
            }
            Object userId = getFieldFromToken(token, "userId");
            if (userId != null) {
                return Integer.parseInt(userId.toString());
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}