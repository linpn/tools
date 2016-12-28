package org.springframework.web.support;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.PropertyValue;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.ServletRequestParameterPropertyValues;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 对HttpServletRequest进行扩展。
 *
 * @author Linpn
 */
public class SpringMvcExtRequest extends HttpServletRequestWrapper implements HttpServletRequest, MultipartRequest {

    protected final static Log logger = LogFactory.getLog(SpringMvcExtRequest.class);

    private HttpServletRequest request;

    public SpringMvcExtRequest(HttpServletRequest request) {
        super(request);
        this.request = (HttpServletRequest) super.getRequest();
    }

    public HttpServletRequest getRequest() {
        return request;
    }


    public String getParameter(String name) {
        return this.getParameter(name, null, null);
    }

    public String getParameter(String name, String defaultValue) {
        return this.getParameter(name, defaultValue, null);
    }

    public String getParameter(String name, String defaultValue, String charsetName) {
        try {
            String value = request.getParameter(name);

            if (value == null || value.equals(""))
                return defaultValue;

            if (charsetName == null || charsetName.equals("")) {
                return value.trim();
            } else {
                return new String(value.getBytes("ISO8859-1"), charsetName);
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public Boolean getBooleanParameter(String name) {
        return this.getBooleanParameter(name, null);
    }

    public Boolean getBooleanParameter(String name, Boolean defaultValue) {
        try {
            String value = this.getParameter(name);

            if (value == null || value.equals(""))
                return defaultValue;

            return Boolean.valueOf(value);

        } catch (Exception e) {
            return defaultValue;
        }
    }

    public Character getCharParameter(String name) {
        return this.getCharParameter(name, null);
    }

    public Character getCharParameter(String name, Character defaultValue) {
        try {
            String value = this.getParameter(name);

            if (value == null || value.equals(""))
                return defaultValue;

            return value.charAt(0);

        } catch (Exception e) {
            return defaultValue;
        }
    }

    public Byte getByteParameter(String name) {
        return this.getByteParameter(name, null);
    }

    public Byte getByteParameter(String name, Byte defaultValue) {
        try {
            String value = this.getParameter(name);

            if (value == null || value.equals(""))
                return defaultValue;

            return Byte.valueOf(value);

        } catch (Exception e) {
            return defaultValue;
        }
    }

    public Short getShortParameter(String name) {
        return this.getShortParameter(name, null);
    }

    public Short getShortParameter(String name, Short defaultValue) {
        try {
            String value = this.getParameter(name);

            if (value == null || value.equals(""))
                return defaultValue;

            return Short.valueOf(value);

        } catch (Exception e) {
            return defaultValue;
        }
    }

    public Integer getIntParameter(String name) {
        return this.getIntParameter(name, null);
    }

    public Integer getIntParameter(String name, Integer defaultValue) {
        try {
            String value = this.getParameter(name);

            if (value == null || value.equals(""))
                return defaultValue;

            return Integer.valueOf(value);

        } catch (Exception e) {
            return defaultValue;
        }
    }

    public Long getLongParameter(String name) {
        return this.getLongParameter(name, null);
    }

    public Long getLongParameter(String name, Long defaultValue) {
        try {
            String value = this.getParameter(name);

            if (value == null || value.equals(""))
                return defaultValue;

            return Long.valueOf(value);

        } catch (Exception e) {
            return defaultValue;
        }
    }

    public Float getFloatParameter(String name) {
        return this.getFloatParameter(name, null);
    }

    public Float getFloatParameter(String name, Float defaultValue) {
        try {
            String value = this.getParameter(name);

            if (value == null || value.equals(""))
                return defaultValue;

            return Float.valueOf(value);

        } catch (Exception e) {
            return defaultValue;
        }
    }

    public Double getDoubleParameter(String name) {
        return this.getDoubleParameter(name, null);
    }

    public Double getDoubleParameter(String name, Double defaultValue) {
        try {
            String value = this.getParameter(name);

            if (value == null || value.equals(""))
                return defaultValue;

            return Double.valueOf(value);

        } catch (Exception e) {
            return defaultValue;
        }
    }

    public BigDecimal getBigDecimalParameter(String name) {
        return this.getBigDecimalParameter(name, null);
    }

    public BigDecimal getBigDecimalParameter(String name, BigDecimal defaultValue) {
        try {
            String value = this.getParameter(name);

            if (value == null || value.equals(""))
                return defaultValue;

            return new BigDecimal(value);

        } catch (Exception e) {
            return defaultValue;
        }
    }

    public Date getDateParameter(String name) {
        return this.getDateParameter(name, null);
    }

    public Date getDateParameter(String name, String pattern) {
        return this.getDateParameter(name, pattern, null);
    }

    public Date getDateParameter(String name, String pattern, Date defaultValue) {
        try {
            String value = this.getParameter(name);

            if (value == null || value.equals(""))
                return defaultValue;

            SimpleDateFormat sdf;
            if (pattern == null || pattern.equals(""))
                sdf = new SimpleDateFormat("yyyy-MM-dd");
            else
                sdf = new SimpleDateFormat(pattern);

            return sdf.parse(value);

        } catch (Exception e) {
            return defaultValue;
        }
    }

    public <T> T getBindObject(Class<T> clazz) {
        return this.getBindObject(clazz, null);
    }

    public <T> T getBindObject(Class<T> clazz, String objectName) {
        try {
            PropertyValue[] propertyValues = new ServletRequestParameterPropertyValues(request).getPropertyValues();
            T obj = clazz.newInstance();
            objectName = objectName == null || objectName.equals("") ? "" : objectName + ".";

            for (PropertyValue propertyValue : propertyValues) {
                try {
                    String name = propertyValue.getName();

                    //反射赋值
                    if (name.startsWith(objectName)) {
                        Field field = clazz.getDeclaredField(name.substring(objectName.length()));
                        Method method = clazz.getMethod("set" + field.getName().substring(0, 1).toUpperCase(Locale.ENGLISH) + field.getName().substring(1), field.getType());

                        //String
                        if (field.getType() == String.class) {
                            method.invoke(obj, this.getParameter(name));
                        } else {
                            //Boolean
                            if (field.getType() == Boolean.class) {
                                method.invoke(obj, this.getBooleanParameter(name));
                            } else {
                                //Character
                                if (field.getType() == Character.class) {
                                    method.invoke(obj, this.getCharParameter(name));
                                } else {
                                    //Byte
                                    if (field.getType() == Byte.class) {
                                        method.invoke(obj, this.getByteParameter(name));
                                    } else {
                                        //Short
                                        if (field.getType() == Short.class) {
                                            method.invoke(obj, this.getShortParameter(name));
                                        } else {
                                            //Integer
                                            if (field.getType() == Integer.class) {
                                                method.invoke(obj, this.getIntParameter(name));
                                            } else {
                                                //Long
                                                if (field.getType() == Long.class) {
                                                    method.invoke(obj, this.getLongParameter(name));
                                                } else {
                                                    //Float
                                                    if (field.getType() == Float.class) {
                                                        method.invoke(obj, this.getFloatParameter(name));
                                                    } else {
                                                        //Double
                                                        if (field.getType() == Double.class) {
                                                            method.invoke(obj, this.getDoubleParameter(name));
                                                        } else {
                                                            //BigDecimal
                                                            if (field.getType() == BigDecimal.class) {
                                                                method.invoke(obj, this.getBigDecimalParameter(name));
                                                            } else {
                                                                //Date
                                                                if (field.getType() == Date.class) {
                                                                    method.invoke(obj, this.getDateParameter(name));
                                                                }
                                                                //其它
                                                                else {
                                                                    method.invoke(obj, propertyValue.getValue());
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    logger.warn(e);
                }
            }

            return obj;

        } catch (Exception e) {
            logger.error("绑定对象出错", e);
        }

        return null;
    }

    public MultipartFile getFile(String name) {
        if (request instanceof MultipartRequest) {
            MultipartRequest multipartRequest = (MultipartRequest) request;
            return multipartRequest.getFile(name);
        }
        return null;
    }

    public List<MultipartFile> getFiles(String name) {
        if (request instanceof MultipartRequest) {
            MultipartRequest multipartRequest = (MultipartRequest) request;
            return multipartRequest.getFiles(name);
        }
        return null;
    }

    public Iterator<String> getFileNames() {
        if (request instanceof MultipartRequest) {
            MultipartRequest multipartRequest = (MultipartRequest) request;
            return multipartRequest.getFileNames();
        }
        return null;
    }

    public Map<String, MultipartFile> getFileMap() {
        if (request instanceof MultipartRequest) {
            MultipartRequest multipartRequest = (MultipartRequest) request;
            return multipartRequest.getFileMap();
        }
        return null;
    }

    public MultiValueMap<String, MultipartFile> getMultiFileMap() {
        if (request instanceof MultipartRequest) {
            MultipartRequest multipartRequest = (MultipartRequest) request;
            return multipartRequest.getMultiFileMap();
        }
        return null;
    }

    public String getMultipartContentType(String paramOrFileName) {
        if (request instanceof MultipartRequest) {
            MultipartRequest multipartRequest = (MultipartRequest) request;
            return multipartRequest.getMultipartContentType(paramOrFileName);
        }
        return null;
    }

    public String getAuthType() {
        return request.getAuthType();
    }

    public Cookie[] getCookies() {
        return request.getCookies();
    }

    public long getDateHeader(String name) {
        return request.getDateHeader(name);
    }

    public String getHeader(String name) {
        return request.getHeader(name);
    }

    public Enumeration<String> getHeaders(String name) {
        return request.getHeaders(name);
    }

    public Enumeration<String> getHeaderNames() {
        return request.getHeaderNames();
    }

    public int getIntHeader(String name) {
        return request.getIntHeader(name);
    }

    public String getMethod() {
        return request.getMethod();
    }

    public String getPathInfo() {
        return request.getPathInfo();
    }

    public String getPathTranslated() {
        return request.getPathTranslated();
    }

    public String getContextPath() {
        return request.getContextPath();
    }

    public String getQueryString() {
        return request.getQueryString();
    }

    public String getRemoteUser() {
        return request.getRemoteUser();
    }

    public boolean isUserInRole(String role) {
        return request.isUserInRole(role);
    }

    public Principal getUserPrincipal() {
        return request.getUserPrincipal();
    }

    public String getRequestedSessionId() {
        return request.getRequestedSessionId();
    }

    public String getRequestURI() {
        return request.getRequestURI();
    }

    public StringBuffer getRequestURL() {
        return request.getRequestURL();
    }

    public String getServletPath() {
        return request.getServletPath();
    }

    public HttpSession getSession(boolean create) {
        return request.getSession(create);
    }

    public HttpSession getSession() {
        return request.getSession();
    }

    public String changeSessionId() {
        return request.changeSessionId();
    }

    public boolean isRequestedSessionIdValid() {
        return request.isRequestedSessionIdValid();
    }

    public boolean isRequestedSessionIdFromCookie() {
        return request.isRequestedSessionIdFromCookie();
    }

    public boolean isRequestedSessionIdFromURL() {
        return request.isRequestedSessionIdFromURL();
    }

    public boolean authenticate(HttpServletResponse response)
            throws IOException, ServletException {
        return request.authenticate(response);
    }

    public void login(String username, String password) throws ServletException {
        request.login(username, password);
    }

    public void logout() throws ServletException {
        request.logout();
    }

    public Collection<Part> getParts() throws IOException,
            IllegalStateException, ServletException {
        return request.getParts();
    }

    public Part getPart(String name) throws IOException, IllegalStateException,
            ServletException {
        return request.getPart(name);
    }

    public <T extends HttpUpgradeHandler> T upgrade(Class<T> handlerClass) throws IOException, ServletException {
        return request.upgrade(handlerClass);
    }

    public Object getAttribute(String name) {
        return request.getAttribute(name);
    }

    public Enumeration<String> getAttributeNames() {
        return request.getAttributeNames();
    }

    public String getCharacterEncoding() {
        return request.getCharacterEncoding();
    }

    public void setCharacterEncoding(String env)
            throws UnsupportedEncodingException {
        request.setCharacterEncoding(env);
    }

    public int getContentLength() {
        return request.getContentLength();
    }

    public long getContentLengthLong() {
        return request.getContentLengthLong();
    }

    public String getContentType() {
        return request.getContentType();
    }

    public ServletInputStream getInputStream() throws IOException {
        return request.getInputStream();
    }

    public Enumeration<String> getParameterNames() {
        return request.getParameterNames();
    }

    public String[] getParameterValues(String name) {
        return request.getParameterValues(name);
    }

    public Map<String, String[]> getParameterMap() {
        return request.getParameterMap();
    }

    public String getProtocol() {
        return request.getProtocol();
    }

    public String getScheme() {
        return request.getScheme();
    }

    public String getServerName() {
        return request.getServerName();
    }

    public int getServerPort() {
        return request.getServerPort();
    }

    public BufferedReader getReader() throws IOException {
        return request.getReader();
    }

    public String getRemoteAddr() {
        //获取访问者真实的IP地址(避免反向代理的影响)
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

    public String getRemoteHost() {
        return request.getRemoteHost();
    }

    public void setAttribute(String name, Object o) {
        request.setAttribute(name, o);
    }

    public void removeAttribute(String name) {
        request.removeAttribute(name);
    }

    public Locale getLocale() {
        return request.getLocale();
    }

    public Enumeration<Locale> getLocales() {
        return request.getLocales();
    }

    public boolean isSecure() {
        return request.isSecure();
    }

    public RequestDispatcher getRequestDispatcher(String path) {
        return request.getRequestDispatcher(path);
    }

    public int getRemotePort() {
        return request.getRemotePort();
    }

    public String getLocalName() {
        return request.getLocalName();
    }

    public String getLocalAddr() {
        return request.getLocalAddr();
    }

    public int getLocalPort() {
        return request.getLocalPort();
    }

    public ServletContext getServletContext() {
        return request.getServletContext();
    }

    public AsyncContext startAsync() {
        return request.startAsync();
    }

    public AsyncContext startAsync(ServletRequest servletRequest,
                                   ServletResponse servletResponse) {
        return request.startAsync(servletRequest, servletResponse);
    }

    public boolean isAsyncStarted() {
        return request.isAsyncStarted();
    }

    public boolean isAsyncSupported() {
        return request.isAsyncSupported();
    }

    public AsyncContext getAsyncContext() {
        return request.getAsyncContext();
    }

    public DispatcherType getDispatcherType() {
        return request.getDispatcherType();
    }


    @Deprecated
    public String getRealPath(String path) {
        return request.getRealPath(path);
    }

    @Deprecated
    public boolean isRequestedSessionIdFromUrl() {
        return request.isRequestedSessionIdFromUrl();
    }

}

