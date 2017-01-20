package com.github.linpn.dsession.filter;

import com.github.linpn.dsession.wrapper.RedisHttpServletRequestWrapper;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.web.filter.GenericFilterBean;
import redis.clients.jedis.JedisPoolConfig;

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

    private String hostName = "localhost";
    private int port = 6379;
    private int maxTotal = 8;
    private String filterSuffix = "*.js,*.css,*.png,*.jpg,*.gif,*.ico,*.tff,*.woff,*.svg,*.eot";

    @Override
    protected void initFilterBean() throws ServletException {
        try {
            //创建redisTemplate客户端
            if (cache == null) {
                JedisPoolConfig poolConfig = new JedisPoolConfig();
                poolConfig.setMaxTotal(maxTotal);
                JedisConnectionFactory connectionFactory = new JedisConnectionFactory();
                connectionFactory.setHostName(hostName);
                connectionFactory.setPort(port);
                connectionFactory.setPoolConfig(poolConfig);
                cache = new RedisTemplate();
                cache.setConnectionFactory(connectionFactory);
                cache.setKeySerializer(new StringRedisSerializer());
                cache.setHashKeySerializer(new StringRedisSerializer());
                this.setFilterSuffix(filterSuffix);
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

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setMaxTotal(int maxTotal) {
        this.maxTotal = maxTotal;
    }

    public void setFilterSuffix(String filterSuffix) {
        this.filterSuffix = filterSuffix;
        this.filterSuffix = this.filterSuffix.replaceAll("\\s+", "");
        this.filterSuffix = this.filterSuffix.replaceAll("\\.", "\\\\.");
        this.filterSuffix = this.filterSuffix.replaceAll("\\*", ".*");
        this.filterSuffix = this.filterSuffix.replaceAll(",", "\\$|") + "$";
    }
}
