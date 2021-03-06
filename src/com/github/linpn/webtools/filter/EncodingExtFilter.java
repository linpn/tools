package com.github.linpn.webtools.filter;

import org.springframework.web.filter.CharacterEncodingFilter;
import com.github.linpn.webtools.http.HttpServletExtRequest;
import com.github.linpn.webtools.http.HttpServletExtResponse;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Spring MVC encoding http filter
 */
public class EncodingExtFilter extends CharacterEncodingFilter {

    private final static String[] urls = {".html", ".text", ".js", ".css", ".png", ".jpg", ".gif", ".ico", ".tff", ".woff", ".svg", ".eot"};

    private String encoding;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // 排除静态
        String path = request.getServletPath();
        for (String str : urls) {
            if (path.indexOf(str) > 0){
                filterChain.doFilter(request, response);
                return;
            }
        }

        HttpServletExtRequest req = new HttpServletExtRequest(request);
        HttpServletExtResponse res = new HttpServletExtResponse(response);
        req.setCharacterEncoding(this.encoding);
        res.setCharacterEncoding(this.encoding);
        res.setContentType("text/html");
        super.doFilterInternal(req, res, filterChain);
    }


    @Override
    public void setEncoding(String encoding) {
        this.encoding = encoding;
        super.setEncoding(encoding);
    }
}
