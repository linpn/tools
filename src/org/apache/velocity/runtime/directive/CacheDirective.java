package org.apache.velocity.runtime.directive;

import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.*;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.parser.node.Node;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.context.ContextLoader;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;


/**
 * Velocity页面缓存宏。
 * #cache('key', 分钟)
 * html_body
 * #end
 * 其中key为指定的cache key，如果为空，则以宏的使用位置(行号和列号)为key
 *
 * @author Linpn
 */
public class CacheDirective extends Directive {

    private static final String VELOCITY_CACHED_KEY = "VELOCITY_CACHED_KEY";

    private static final String CACHE_REDIS_TEMPLATE = "direcitive.cache.redistemplate";
    private static final String CACHE_EXPIRE = "direcitive.cache.expire";

    private static RedisTemplate<String, Object> cache;
    private String redisTemplate = "redisTemplate";
    private long expire = 5; //默认时间为5分钟

    /**
     * 指令的名称
     */
    @Override
    public String getName() {
        return "cache";
    }

    /**
     * 指令类型为块指令
     */
    @Override
    public int getType() {
        return BLOCK;
    }

    @Override
    public void init(RuntimeServices rs, InternalContextAdapter context,
                     Node node) throws TemplateInitException {
        super.init(rs, context, node);

        try {
            //获取redisTemplate对象
            Object _redisTemplate = rs.getProperty(CACHE_REDIS_TEMPLATE);
            if (_redisTemplate != null && !_redisTemplate.equals("")) {
                redisTemplate = _redisTemplate.toString();
            }

            //获取默认缓存超时时间
            Object _expire = rs.getProperty(CACHE_EXPIRE);
            if (_expire != null && isNumeric(_expire.toString())) {
                expire = Long.valueOf(_expire.toString());
            }

            //创建redisTemplate客户端
            if (cache == null) {
                cache = ContextLoader.getCurrentWebApplicationContext().getBean(redisTemplate, RedisTemplate.class);
            }
        } catch (Exception e) {
            throw new VelocityException("创建RedisTemplate出错", e);
        }
    }

    @Override
    public boolean render(InternalContextAdapter context, Writer writer, Node node)
            throws IOException, ResourceNotFoundException, ParseErrorException, MethodInvocationException {
        //获取参数
        String[] params = this.getParams(context, node);
        String key = new StringBuilder(VELOCITY_CACHED_KEY + "|" + params[0]).append(context.getCurrentTemplateName()).append(':').append(node.getLine()).append(':').append(node.getColumn()).toString();
        long expire = isNumeric(params[1]) ? Long.valueOf(params[1]) : this.expire;

        //获取缓存数据
        try {
            String value = this.getCachedValue(key);
            if (value == null) {
                Node bodyNode = getBodyNode(node);
                StringWriter sw = new StringWriter();
                bodyNode.render(context, sw);
                value = sw.toString();
                this.setCacheValue(key, value, expire);
            }
            writer.write(value);
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    protected String getCachedValue(String key) {
        Object value = cache.boundValueOps(key).get();
        if (value != null) {
            return value.toString();
        } else {
            return "";
        }
    }

    protected void setCacheValue(String key, String value, long expire) {
        cache.boundValueOps(key).set(value, expire, TimeUnit.MILLISECONDS);
    }

    private String[] getParams(InternalContextAdapter context, Node node) {
        //参数个数
        String[] params = new String[2];
        int count = node.jjtGetNumChildren();
        if (count == 2) {
            params[0] = node.jjtGetChild(0).value(context).toString();
            params[1] = node.jjtGetChild(1).value(context).toString();
            return params;
        } else if (count == 1) {
            params[0] = node.jjtGetChild(0).value(context).toString();
            params[1] = "";
            return params;
        } else {
            throw new ParseErrorException("参数个数不正确!");
        }
    }

    private Node getBodyNode(Node node) {
        return node.jjtGetChild(node.jjtGetNumChildren() - 1);
    }

    private boolean isNumeric(String str) {
        if (str != null && !str.equals("")) {
            Pattern pattern = Pattern.compile("[0-9]*");
            return pattern.matcher(str).matches();
        } else {
            return false;
        }
    }
}
