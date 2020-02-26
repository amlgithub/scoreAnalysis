package com.zgczx.service.wechat;

import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpSession;

/**
 * @author aml
 * @date 2019/11/21 15:31
 */
public interface WeChatService {

    String getJsapiTicket(HttpSession session);

    JSONObject getSign(String url, HttpSession session);

    /**
     * 从微信公众号获取授权
     *
     * @param returnUrl
     * @param path
     * @return
     */
    String getAuthorizeFromWechat(String returnUrl, String path);

    /**
     * 从微信公众号获取用户信息
     *
     * @param code
     * @param returnUrl
     * @param path
     * @return
     */
    String getUserInfoFromWechat(String code,String returnUrl,String path);
}
