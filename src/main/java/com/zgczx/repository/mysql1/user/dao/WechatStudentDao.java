package com.zgczx.repository.mysql1.user.dao;

import com.zgczx.repository.mysql1.user.model.WechatStudent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author aml
 * @date 2019/11/8 19:39
 */
@Repository
public interface WechatStudentDao extends JpaRepository<WechatStudent, Integer> {

    WechatStudent findByOpenid(String openid);
}
