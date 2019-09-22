package com.zgczx.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * 实现跨域访问的配置任务
 * @author jason
 *
 */
@Configuration
public class CorsOriginConfig extends WebMvcConfigurerAdapter{

//	@Override
//	public void addCorsMappings(CorsRegistry registry){
//		registry.addMapping("/**")
//		.allowedOrigins("*")
//		.allowedMethods("GET","POST")
//		.allowCredentials(false).maxAge(3600);
//	}
}
