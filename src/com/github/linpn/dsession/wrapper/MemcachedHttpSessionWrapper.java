package com.github.linpn.dsession.wrapper;

import net.rubyeye.xmemcached.MemcachedClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import java.net.InetSocketAddress;
import java.util.*;


/**
 * Memcached Session 装饰类
 *
 * @author Linpn
 */
@SuppressWarnings("deprecation")
public class MemcachedHttpSessionWrapper implements HttpSession {

    protected final static Log logger = LogFactory.getLog(MemcachedHttpSessionWrapper.class);
    protected MemcachedClient cache;
    protected ServletContext servletContext;
    protected String jsessionid;
    protected Integer interval;
    protected boolean isNew = false;
    private String SESSION_ID;


    /**
     * 创建session
     *
     * @param cache   MemcachedClient 缓存对象
     * @param session HttpSession 对象
     * @param ip      ip地址
     * @return 返回 Memcached HttpSession
     */
    public static HttpSession create(MemcachedClient cache, HttpSession session, String ip) {
        return new MemcachedHttpSessionWrapper(cache, session.getId(), ip, session.getServletContext(),
                session.getMaxInactiveInterval(), true);
    }

    /**
     * 获取session
     *
     * @param cache          MemcachedClient 缓存对象
     * @param jsessionid     SESSION的ID
     * @param ip             ip地址
     * @param servletContext ServletContext 对象
     * @param interval       超时时间, 单位：秒
     * @return 返回 Memcached HttpSession
     */
    public static HttpSession get(MemcachedClient cache, String jsessionid, String ip, ServletContext servletContext, int interval) {
        return new MemcachedHttpSessionWrapper(cache, jsessionid, ip, servletContext,
                interval, false);
    }

    /**
     * MemcachedHttpSessionWrapper 构造函数
     *
     * @param cache          MemcachedClient 缓存对象
     * @param servletContext ServletContext 对象
     * @param jsessionid     SESSION的ID
     * @param ip             ip地址
     * @param interval       超时时间, 单位：秒
     * @param isNew          是否是新创建, 新创建的会设置一些值
     */
    protected MemcachedHttpSessionWrapper(MemcachedClient cache, String jsessionid, String ip,
                                          ServletContext servletContext, int interval, boolean isNew) {
        try {
            this.cache = cache;
            this.jsessionid = jsessionid;
            this.servletContext = servletContext;
            this.interval = interval;
            this.isNew = isNew;
            this.SESSION_ID = "SESSION/" + this.getId() + "/";
            logger.info("Memcached session cluster filter, SESSION: " + jsessionid + ", IP: " + ip);

        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }


    @Override
    public String getId() {
        return jsessionid;
    }

    @Override
    public ServletContext getServletContext() {
        return servletContext;
    }

    @Override
    public boolean isNew() {
        return this.isNew;
    }


    @Override
    public Object getAttribute(String name) {
        try {
            return cache.get(SESSION_ID + name);
        } catch (Exception e) {
            logger.error("Memcached session error, SESSION: " + jsessionid + ", " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public void setAttribute(String name, Object value) {
        try {
            cache.set(SESSION_ID + name, this.getMaxInactiveInterval(), value);
        } catch (Exception e) {
            logger.error("Memcached session error, SESSION: " + jsessionid + ", " + e.getMessage(), e);
        }
    }

    @Override
    public void removeAttribute(String name) {
        try {
            cache.delete(SESSION_ID + name);
        } catch (Exception e) {
            logger.error("Memcached session error, SESSION: " + jsessionid + ", " + e.getMessage(), e);
        }
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        Set<String> keys = new HashSet<String>();

        try {
            Map<InetSocketAddress, Map<String, String>> map = cache.getStats();
            for (Iterator<InetSocketAddress> it = map.keySet().iterator(); it.hasNext(); ) {
                keys.addAll(map.get(it.next()).keySet());
            }
        } catch (Exception e) {
            logger.error("Memcached session error, SESSION: " + jsessionid + ", " + e.getMessage(), e);
        }

        return Collections.enumeration(keys);
    }

    @Override
    public int getMaxInactiveInterval() {
        return interval;
    }

    @Override
    public void setMaxInactiveInterval(int interval) {
        this.interval = interval;
    }

    @Override
    public void invalidate() {
    }

    @Override
    public long getCreationTime() {
        return System.currentTimeMillis();
    }

    @Override
    public long getLastAccessedTime() {
        return System.currentTimeMillis();
    }


    @Deprecated
    public Object getValue(String name) {
        return this.getAttribute(name);
    }

    @Deprecated
    public String[] getValueNames() {
        List<String> list = Collections.list(this.getAttributeNames());
        return list.toArray(new String[list.size()]);
    }

    @Deprecated
    public void putValue(String name, Object value) {
        this.setAttribute(name, value);
    }

    @Deprecated
    public void removeValue(String name) {
        this.removeAttribute(name);
    }

    @Deprecated
    public javax.servlet.http.HttpSessionContext getSessionContext() {
        return null;
    }

    public MemcachedClient getCache() {
        return cache;
    }
}
