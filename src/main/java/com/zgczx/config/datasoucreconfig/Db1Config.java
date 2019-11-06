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



@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "entityManagerFactoryDb1",
        transactionManagerRef = "transactionManagerDb1",
        basePackages = {"com.zgczx.repository.mysql1"}) //设置Repository所在位置
public class Db1Config {

    @Autowired
    @Qualifier("db1DataSource")
    private DataSource db1DataSource;

    @Primary
    @Bean(name = "entityManagerDb1")
    public EntityManager entityManager(EntityManagerFactoryBuilder builder) {
        return entityManagerFactoryDb1(builder).getObject().createEntityManager();
    }

    @Primary
    @Bean(name = "entityManagerFactoryDb1")
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryDb1(EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(db1DataSource)
                .properties(getVendorProperties(db1DataSource))
                .packages("com.zgczx.repository.mysql1") //设置实体类所在位置
                .persistenceUnit("db1PersistenceUnit")
                .build();
    }

    @Autowired
    private JpaProperties jpaProperties;

    private Map<String, String> getVendorProperties(DataSource dataSource) {
        return jpaProperties.getHibernateProperties(dataSource);
    }

    @Primary
    @Bean(name = "transactionManagerDb1")
    public PlatformTransactionManager transactionManagerDb1(EntityManagerFactoryBuilder builder) {
        return new JpaTransactionManager(entityManagerFactoryDb1(builder).getObject());
    }

}
