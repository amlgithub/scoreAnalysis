package com.zgczx.config.datasoucreconfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManager;
import javax.sql.DataSource;
import java.util.Map;
import java.util.Properties;

/**
 * 第三数据源的具体配置
 * 这种配置是 1.多版本的
 * @author aml
 * @date 2019/10/10 10:45
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "entityManagerFactoryDb3",
        transactionManagerRef = "transactionManagerDb3",
        basePackages = {"com.zgczx.repository.mysql3.unifiedlogin.dao"}) // 指定该数据源操作的DAO接口包
public class Db3Config {

    @Autowired
    @Qualifier("db3DataSource")
    private DataSource db3DataSource;

    //@Primary//指定主数据源
    @Bean(name = "entityManagerDb3")
    public EntityManager entityManager(EntityManagerFactoryBuilder builder) {
        return entityManagerFactoryDb3(builder).getObject().createEntityManager();
    }

    //@Primary
    @Bean(name = "entityManagerFactoryDb3")
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryDb3(EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(db3DataSource)
                .properties(getVendorProperties(db3DataSource))
                .packages("com.zgczx.repository.mysql3.unifiedlogin.model") //设置实体类所在位置
                .persistenceUnit("db3PersistenceUnit")
                .build();
    }

    @Autowired
    private JpaProperties jpaProperties;

    private Map<String, String> getVendorProperties(DataSource dataSource) {
        return jpaProperties.getHibernateProperties(dataSource);
    }

    //@Primary
    @Bean(name = "transactionManagerDb3")
    public PlatformTransactionManager transactionManagerDb3(EntityManagerFactoryBuilder builder) {
        return new JpaTransactionManager(entityManagerFactoryDb3(builder).getObject());
    }

}