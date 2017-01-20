package com.github.linpn.dsession.wrapper;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;


/**
 * 基类
 *
 * @author Linpn
 */
public class BaseHttpServletRequestWrapper extends HttpServletRequestWrapper {

    /**
     * Constructs a request object wrapping the given request.
     *
     * @param request request
     * @throws IllegalArgumentException if the request is null
     */
    public BaseHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
    }

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
        if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

}
