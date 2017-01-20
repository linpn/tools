package com.github.linpn.spring.handler;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Spring mvc 异常映射处理
 *
 * @author Linpn
 */
public class MappingExceptionResolver extends SimpleMappingExceptionResolver {

    /**
     * 输出Exception StackTrace 字符串
     */
    @Override
    protected ModelAndView getModelAndView(String viewName, Exception ex) {
        ModelAndView mv = super.getModelAndView(viewName, ex);
        StringWriter sw = new StringWriter();
        ex.printStackTrace(new PrintWriter(sw));
        mv.addObject("exceptionStackTrace", sw.toString());
        return mv;
    }

}
