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
}
