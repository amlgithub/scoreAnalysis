package com.zgczx.handler;

import com.zgczx.config.wechatconfig.ProjectUrlConfig;
import com.zgczx.exception.UserAuthorizeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

/**
 * explain：user授权异常捕获处理
 *
 * @author aml
 * @date 2019/11/9 13:58
 */
@ControllerAdvice
public class UserAuthorizeExceptionHandler {

    @Autowired
    private ProjectUrlConfig projectUrlConfig;

    /**
     * 处理UserAuthorizeException.class这个类抛出的异常，
     * 并且跳转到指定的url。
     * 拦截登录异常
     * 例如：重定向至登录页面 - 也就是微信扫码登录
     *
     * @return
     */
    @ExceptionHandler(value = UserAuthorizeException.class)//处理此类抛出的异常
    public ModelAndView handlerUserAuthorizeException() {
        return new ModelAndView("redirect:"
                .concat(projectUrlConfig.wechatOpenAuthorize)//微信开放平台登录授权地址
                .concat("/wechat/qrAuthorize")
                .concat("?returnUrl=")
                // .concat(projectUrlConfig.score_analysis())//服务器访问的地址
                .concat("/seller/login"));

    }


}
