package com.zgczx.config.datasoucreconfig;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;


@Configuration // 显式配置了多个数据源
public class DataSourceConfig {

    @Bean(name = "db1DataSource")
    @Qualifier("db1DataSource")
    @Primary // 设置 主数据源
    @ConfigurationProperties(prefix = "spring.datasource.db1")// 指定配置文件中的数据源前缀
    public DataSource db1DataSource(){
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }

    @Bean(name = "db2DataSource")
    @Qualifier("db2DataSource")
    @ConfigurationProperties(prefix = "spring.datasource.db2")// 指定配置文件中的数据源前缀
    public DataSource db2DataSource(){
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }

    @Bean(name = "db3DataSource")
    @Qualifier("db3DataSource")
    @ConfigurationProperties(prefix = "spring.datasource.db3")// 指定配置文件中的数据源前缀
    public DataSource db3DataSource(){
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }
}
