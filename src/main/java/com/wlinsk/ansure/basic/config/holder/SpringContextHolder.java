package com.wlinsk.ansure.basic.config.holder;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * @Author: wlinsk
 * @Date: 2025/6/4
 */
@Component
public class SpringContextHolder implements ApplicationContextAware {
    private static ApplicationContext context;
    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        context = ctx;
    }
    public static Environment getEnvironment() {
        return context.getEnvironment();
    }

    public static <T> T getBean(Class<T> clazz) {
        return context.getBean(clazz);
    }
}
