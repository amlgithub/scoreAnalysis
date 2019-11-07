package com.zgczx.swagger2;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author aml
 * @date 2019/11/6 23:34
 */
@ComponentScan(basePackages = "com.zgczx.controller")
@EnableSwagger2
@Configuration
public class Swagger2 {

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                //.pathMapping("/score_analysis")  //本地将这里注销，线上需要将这里打开，线上的url通过nginx代理的
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.zgczx.controller")) //Controller所在包(必须新建包)
                .paths(PathSelectors.any())
                .build();
    }
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("java-demo构建api文档")  //标题
                .description("swagger description")  //描述
                .version("1.0")
                .build();
    }
}
