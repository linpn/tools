package org.springframework.web.filter;

import org.springframework.web.support.SpringMvcExtRequest;
import org.springframework.web.support.SpringMvcExtResponse;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Spring MVC encoding support filter
 */
public class SpringMvcExtEncodingFilter extends CharacterEncodingFilter {

    private String encoding;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        SpringMvcExtRequest req = new SpringMvcExtRequest(request);
        SpringMvcExtResponse res = new SpringMvcExtResponse(response);
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
