package com.zgczx.controller.wechat;

import com.zgczx.config.wechatconfig.ProjectUrlConfig;
import com.zgczx.enums.ResultEnum;
import com.zgczx.exception.ScoreException;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URLEncoder;

/**
 * @author aml
 * @date 2019/10/12 19:50
 */
@Controller
@RequestMapping("/wechat")
@Slf4j
public class WeChatController {

    @Autowired
    private WxMpService wxMpService;

    @Autowired
    private ProjectUrlConfig projectUrlConfig;

    @GetMapping("/authorize")
    public String authorize(@RequestParam("returnUrl") String returnUrl){
        //1. 配置
        //2. 调用
        //拼接这个访问url去调用这个“/userInfo”接口，去获取token，然后获取openid、头像等信息
        String url = projectUrlConfig.getWechatMpAuthorize() + "/show/score_analysis/wechat/userInfo";
        // 占位符使用，切记“，”，不是直接拼接“+”
        log.info("url= {}" ,url);
        String redirectUrl = wxMpService.oauth2buildAuthorizationUrl(url, WxConsts.OAUTH2_SCOPE_USER_INFO, URLEncoder.encode(returnUrl));
        log.info("redirectUrl= {}" , redirectUrl);
        return "redirectUrl: " + redirectUrl;
    }

    /**
     * 按照开发文档获取用户token，
     * 1 第一步：用户同意授权，获取code
     * 2 第二步：通过code换取网页授权access_token
     * 3 第三步：刷新access_token（如果需要）
     * 4 第四步：拉取用户信息(需scope为 snsapi_userinfo)
     * 5 附：检验授权凭证（access_token）是否有效
     * 获取code后，请求以下链接获取access_token：  https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code
     * @param code 请求一个微信的url获取所需要的code
     * @param returnUrl state=STAT，可随意写
     * @return 获取用户的token，从token获取用户openid
     */
    @GetMapping("/userInfo")
    public String userInfo(@RequestParam("code") String code,
                           @RequestParam("state") String returnUrl){
        WxMpOAuth2AccessToken wxMpOAuth2AccessToken = new WxMpOAuth2AccessToken();
        try{
            wxMpOAuth2AccessToken = wxMpService.oauth2getAccessToken(code);
        } catch (WxErrorException e){
            log.error("【微信网页授权】{}" , e);
            throw new ScoreException(ResultEnum.WECHAT_MP_ERROR.getCode(),e.getError().getErrorMsg());

        }
        String openId = wxMpOAuth2AccessToken.getOpenId();
        log.info("openid= {}" , openId);
        return "redirect:" + returnUrl + "?openid=" + openId;
    }


}
