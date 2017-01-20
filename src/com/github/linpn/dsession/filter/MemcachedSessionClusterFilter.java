package com.github.linpn.dsession.filter;

import com.github.linpn.dsession.wrapper.MemcachedHttpServletRequestWrapper;
import net.rubyeye.xmemcached.MemcachedClient;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Memcached Session 拦截器。
 *
 * @author Linpn
 */
public class MemcachedSessionClusterFilter extends GenericFilterBean {

    private static MemcachedClient cache;
    private String memcached;   // MemcachedClient 的 bean id
    private String filterSuffix;    // 要过滤的扩展名

    @Override
    protected void initFilterBean() throws ServletException {
        try {
            //创建memcachedClient客户端
            if (cache == null) {
                cache = ContextLoader.getCurrentWebApplicationContext().getBean(memcached, MemcachedClient.class);
            }
        } catch (Exception e) {
            throw new ServletException("创建MemcachedClient出错", e);
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletRequest _request = (HttpServletRequest) request;
        String url = _request.getRequestURI();

        if (filterSuffix != null && !filterSuffix.equals("") && !url.matches(filterSuffix)) {
            chain.doFilter(new MemcachedHttpServletRequestWrapper(_request, cache), response);
        } else {
            chain.doFilter(request, response);
        }
    }

    /**
     * MemcachedClient 的 bean id
     */
    public void setMemcached(String memcached) {
        this.memcached = memcached;
    }

    /**
     * 要过滤的扩展名
     */
    public void setFilterSuffix(String filterSuffix) {
        this.filterSuffix = filterSuffix;
        this.filterSuffix = this.filterSuffix.replaceAll("\\s+", "");
        this.filterSuffix = this.filterSuffix.replaceAll("\\.", "\\\\.");
        this.filterSuffix = this.filterSuffix.replaceAll("\\*", ".*");
        this.filterSuffix = this.filterSuffix.replaceAll(",", "\\$|") + "$";
    }

}
