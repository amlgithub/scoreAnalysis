package com.zgczx.repository.mysql3.unifiedlogin.dao;

import com.zgczx.repository.mysql3.unifiedlogin.model.WechatLogin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * @author aml
 * @date 2019/11/17 19:24
 */
public interface WechatLoginDao extends JpaRepository<WechatLogin, Integer> {

    WechatLogin findAllByOpenid(String openid);

    @Modifying
//    @Transactional
    @Query(value = "UPDATE wechat_login SET diyid=?1 WHERE openid=?2 ", nativeQuery = true)
    int updateDiyidByOpenid(String diyid, String openid);
}
