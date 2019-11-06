package com.zgczx.repository.basefactory;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;

/**
 * 获取多数据源中的实体管理工厂
 * @author aml
 * @date 2019/11/1 11:07
 */
@Repository
public class BaseFactory {

    /**
     * 数据源1
     */
    public EntityManager entityManagerDb1;

    /**
     * 数据源2
     */
    public EntityManager entityManagerDb2;

    @PersistenceContext(unitName = "entityManagerFactoryDb1")
    public void setEntityManagerDb1(EntityManager entityManagerDb1){
        this.entityManagerDb1 = entityManagerDb1;
    }

    // 注入指定数据源的实体管理工厂
    @PersistenceContext(unitName = "entityManagerFactoryDb2")
    public void setEntityManagerDb2(EntityManager entityManagerDb2){
        this.entityManagerDb2 = entityManagerDb2;
    }

}
