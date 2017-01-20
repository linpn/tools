package com.github.linpn.dsession.wrapper;


import net.rubyeye.xmemcached.MemcachedClient;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


/**
 * Memcached Request 装饰类
 *
 * @author Linpn
 */
public class MemcachedHttpServletRequestWrapper extends BaseHttpServletRequestWrapper {

    private MemcachedClient cache;
    private final static String REQUEST_SESSION_CLUSTER_FILTER = "REQUEST_SESSION_CLUSTER_FILTER";

    public MemcachedHttpServletRequestWrapper(HttpServletRequest request, MemcachedClient cache) {
        super(request);
        this.cache = cache;
    }

    /**
     * The default behavior of this method is to return getSession()
     * on the wrapped request object.
     */
    public HttpSession getSession() {
        return this.getSession(true);
    }

    /**
     * The default behavior of this method is to return getSession(boolean create)
     * on the wrapped request object.
     */
    public HttpSession getSession(boolean create) {
        //从当前请求的request中获取分析式session
        HttpSession session = (HttpSession) this.getAttribute(REQUEST_SESSION_CLUSTER_FILTER);

        //按cookie中的jsessionid获取分布式session
        if (session == null) {
            String jsessionid = this.getSessionId(super.getCookies());
            String ip = this.getIpAddr((HttpServletRequest) super.getRequest());

            if (jsessionid != null) {
                session = MemcachedHttpSessionWrapper.get(cache, jsessionid, ip, this.getServletContext(),
                        super.getSession(true).getMaxInactiveInterval());
            }

            //如果获取不到分布式session，则创建
            if (session == null) {
                if (create) {
                    session = MemcachedHttpSessionWrapper.create(cache, super.getSession(true), ip);
                }
            }

            //获取到分布式session后，存到request中
            if (session != null) {
                this.setAttribute(REQUEST_SESSION_CLUSTER_FILTER, session);
            }
        }

        return session;
    }

}
