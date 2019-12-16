package com.zgczx.repository.mysql3.unifiedlogin.dao;

import com.zgczx.repository.mysql3.unifiedlogin.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @ProjectName: springboot-demo
 * @Package: com.zkrt.springboot.repository.mysql.user.dao
 * @ClassName: UserDao
 * @Author: zyh
 * @Description: ${description}
 * @Date: 2019/6/18 14:46
 * @Version: 1.0
 **/
@Repository
public interface UserDao extends JpaRepository<User,Integer> {
    /**
     * 根据openid验证此用户是否存在
     * @param openId
     * @return
     */
    User findByWechatid(String openId);

}
