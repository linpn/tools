package com.github.linpn.dsession.filter;

import com.github.linpn.dsession.wrapper.RedisHttpServletRequestWrapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Redis Session 拦截器。
 *
 * @author Linpn
 */
public class RedisSessionClusterFilter extends GenericFilterBean {

    private static RedisTemplate<String, Object> cache;
    private String redisTemplate;   // RedisTemplate 的 bean id
    private String filterSuffix;    // 要过滤的扩展名

    @Override
    protected void initFilterBean() throws ServletException {
        try {
            //创建redisTemplate客户端
            if (cache == null) {
                cache = ContextLoader.getCurrentWebApplicationContext().getBean(redisTemplate, RedisTemplate.class);
            }
        } catch (Exception e) {
            throw new ServletException("创建RedisTemplate出错", e);
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletRequest _request = (HttpServletRequest) request;
        String url = _request.getRequestURI();

        if (filterSuffix != null && !filterSuffix.equals("") && !url.matches(filterSuffix)) {
            chain.doFilter(new RedisHttpServletRequestWrapper(_request, cache), response);
        } else {
            chain.doFilter(request, response);
        }
    }

    /**
     * RedisTemplate 的 bean id
     */
    public void setRedisTemplate(String redisTemplate) {
        this.redisTemplate = redisTemplate;
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
