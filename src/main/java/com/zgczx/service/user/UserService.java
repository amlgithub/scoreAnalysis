package com.zgczx.service.user;

import com.zgczx.repository.mysql1.user.model.UserFeedBack;
import com.zgczx.repository.mysql1.user.model.WechatStudent;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author aml
 * @date 2019/11/8 19:41
 */
public interface UserService {

    WechatStudent login(String openid, HttpServletRequest request,HttpServletResponse response);

    String logout(HttpServletRequest request, HttpServletResponse response);

    WechatStudent registerWechatStudent(String openid, String foreignKeId);

    /**
     * 新增 用户反馈问题表
     * @return
     */
    UserFeedBack addUserFeedBack(UserFeedBack userFeedBack);
}
