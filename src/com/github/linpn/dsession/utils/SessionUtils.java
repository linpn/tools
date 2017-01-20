package com.github.linpn.dsession.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * 工具类
 */
public class SessionUtils {

    /**
     * 从Cookie或url中获取JSESSIONID
     *
     * @return 返回 JSESSIONID
     */
    public static String getSessionId(Cookie[] cookies) {
        String jsessionid = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("JSESSIONID")) {
                    jsessionid = cookie.getValue();
                    break;
                }
            }
        }
        return jsessionid;
    }


    /**
     * 获取客户端真实IP
     *
     * @return 返回客户端真实IP
     */
    public static String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
