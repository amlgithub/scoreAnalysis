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


@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "entityManagerFactoryDb2",
        transactionManagerRef = "transactionManagerDb2",
        basePackages = {"com.zgczx.repository.mysql2.scoretwo.dao"}) // 指定该数据源操作的DAO接口包
public class Db2Config {

    @Autowired
    @Qualifier("db2DataSource")
    private DataSource db2DataSource;

    //@Primary//指定主数据源
    @Bean(name = "entityManagerDb2")
    public EntityManager entityManager(EntityManagerFactoryBuilder builder) {
        return entityManagerFactoryDb2(builder).getObject().createEntityManager();
    }

    //@Primary
    @Bean(name = "entityManagerFactoryDb2")
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryDb2(EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(db2DataSource)
                .properties(getVendorProperties(db2DataSource))
                .packages("com.zgczx.repository.mysql2.scoretwo.model") //设置实体类所在位置
                .persistenceUnit("db2PersistenceUnit")
                .build();

        //设置数据源的方言
//        LocalContainerEntityManagerFactoryBean entityManagerFactory = builder.dataSource(db2DataSource)
//                .packages("com.zgczx.repository.mysql2.scoretwo.model").build();
//        Properties jpaProperties = new Properties();
//        jpaProperties.put("hibernate.dialect", "org.hibernate.dialect.MySQL5Dialect");
//        jpaProperties.put("hibernate.physical_naming_strategy", "org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy");
//        jpaProperties.put("hibernate.connection.charSet", "utf-8");
//        jpaProperties.put("hibernate.show_sql", "false");
//        entityManagerFactory.setJpaProperties(jpaProperties);
//        return entityManagerFactory;

    }

    @Autowired
    private JpaProperties jpaProperties;

    private Map<String, String> getVendorProperties(DataSource dataSource) {
        return jpaProperties.getHibernateProperties(dataSource);
    }

    //@Primary
    @Bean(name = "transactionManagerDb2")
    public PlatformTransactionManager transactionManagerDb2(EntityManagerFactoryBuilder builder) {
        return new JpaTransactionManager(entityManagerFactoryDb2(builder).getObject());
    }

}