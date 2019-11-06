package com.zgczx.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * 获取注入spring中的bean的各种方式
 * @author aml
 * @date 2019/11/1 16:13
 */

@Component
public class SpringUtil implements ApplicationContextAware {

    private static Logger logger = LoggerFactory.getLogger(SpringUtil.class);

    private static ApplicationContext applicationContext;

    // 1. 获取applicationContext
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

        if(SpringUtil.applicationContext == null) {

            SpringUtil.applicationContext = applicationContext;

        }

        logger.info("ApplicationContext配置成功,applicationContext对象："+SpringUtil.applicationContext);

    }

    public static ApplicationContext getApplicationContext() {

        return applicationContext;

    }

    // 2. 根据注入bean中的别名获取，例如： DataSource dataSource= (DataSource)SpringUtil.getBean("db2DataSource");
    public static Object getBean(String name) {

        return getApplicationContext().getBean(name);

    }

    // 2。根据类类型获取对应的bean，这里好像没法获取相同类型中指定的的bean，
    public static <T> T getBean(Class<T> clazz) {

        return getApplicationContext().getBean(clazz);

    }

    // 2. 根据类型和bean的别名获取指定的bean
    public static <T> T getBean(String name,Class<T> clazz) {

        return getApplicationContext().getBean(name,clazz);

    }

}