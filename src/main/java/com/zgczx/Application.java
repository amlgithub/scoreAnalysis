package com.zgczx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import tk.mybatis.spring.annotation.MapperScan;


@MapperScan(basePackages = "com.zgczx.mapper")
@SpringBootApplication
//@EnableCaching
public class Application extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
