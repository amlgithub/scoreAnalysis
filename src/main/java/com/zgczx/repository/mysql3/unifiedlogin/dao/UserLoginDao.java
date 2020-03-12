package com.zgczx.repository.mysql3.unifiedlogin.dao;


import com.zgczx.repository.mysql3.unifiedlogin.model.UserLogin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface UserLoginDao extends JpaRepository<UserLogin, Integer> {

    List<UserLogin> getUserByAccountOrDiyid(String account, int diyid);

    List<UserLogin>  getUserByAccount(String account);

    List<UserLogin>  getUserByDiyidAndMessagecode(int diyid, String messagecode);

    @Query(value = "update user_login set headimage=?2 where  account=?1 or diyid=?1 ", nativeQuery = true)
    @Modifying
    int setHeadimage(String account, String headimage);

    /**
     * 根据wechat_openid注册账号
     * @param wechatId
     * @param userName
     * @param headimg
     * @return
     */
    @Modifying
    @Transactional
    @Query(value = "insert into user_login(wechat_id, user_name,headimg) values (?1,?2,?3)", nativeQuery = true)
    int insertUserInfo(String wechatId, String userName, String headimg);

    // 根据WeChatId获取此用户数据
    @Transactional
    @Query(value = "SELECT * FROM user_login WHERE wechat_id=?1", nativeQuery = true)
    UserLogin getUserLoginByWechatId(String wechatId);

    UserLogin findAllByWechatId(String wechatId);

    // 根据学号查询是否 学校提供有此 用户的基本信息
    UserLogin findByDiyid(String diyid);

    //根据姓名查询
    @Query(value = "SELECT * FROM user_login WHERE real_name=?1 " ,nativeQuery = true)
    List<UserLogin> findByRealName(String name);

}
