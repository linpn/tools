package com.github.linpn.dsession.filter;

import com.github.linpn.dsession.wrapper.MemcachedHttpServletRequestWrapper;
import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.impl.KetamaMemcachedSessionLocator;
import net.rubyeye.xmemcached.transcoders.SerializingTranscoder;
import net.rubyeye.xmemcached.utils.XMemcachedClientFactoryBean;
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
    private String servers = "localhost:11211";
    private String filterSuffix = "*.js,*.css,*.png,*.jpg,*.gif,*.ico,*.tff,*.woff,*.svg,*.eot";

    @Override
    protected void initFilterBean() throws ServletException {
        try {
            //创建memcachedClient客户端
            if (cache == null) {
                if (memcached != null && !memcached.equals("")) {
                    cache = ContextLoader.getCurrentWebApplicationContext().getBean(memcached, MemcachedClient.class);
                } else {
                    XMemcachedClientFactoryBean builder = new XMemcachedClientFactoryBean();
                    builder.setServers(servers);
                    builder.setConnectionPoolSize(5);
                    builder.setSessionLocator(new KetamaMemcachedSessionLocator());
                    builder.setTranscoder(new SerializingTranscoder());
                    cache = (MemcachedClient) builder.getObject();
                }
                this.setFilterSuffix(filterSuffix);
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

    public void setServers(String servers) {
        this.servers = servers;
    }

    public void setFilterSuffix(String filterSuffix) {
        this.filterSuffix = filterSuffix;
        this.filterSuffix = this.filterSuffix.replaceAll("\\s+", "");
        this.filterSuffix = this.filterSuffix.replaceAll("\\.", "\\\\.");
        this.filterSuffix = this.filterSuffix.replaceAll("\\*", ".*");
        this.filterSuffix = this.filterSuffix.replaceAll(",", "\\$|") + "$";
    }
}
