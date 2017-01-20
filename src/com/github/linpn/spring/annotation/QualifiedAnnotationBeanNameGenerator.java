package com.github.linpn.spring.annotation;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;

/**
 * 自定义@Controller注解生成bean的命名策略，重写buildDefaultBeanName方法，使用完全限定类名策略
 */
public class QualifiedAnnotationBeanNameGenerator extends AnnotationBeanNameGenerator {

    @Override
    protected String buildDefaultBeanName(BeanDefinition definition) {
        return definition.getBeanClassName();
    }
}
