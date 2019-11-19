package com.zgczx.controller.wechat;

import com.zgczx.config.wechatconfig.ProjectUrlConfig;
import com.zgczx.enums.ResultEnum;
import com.zgczx.exception.ScoreException;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
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
    public String authorize(@RequestParam("returnUrl") String returnUrl){//returnUrl：本项目的首页面访问路径
        //1. 配置
        //2. 调用
        //url：http://zhongkeruitong.top/score_analysis/wechat/userInfo   ： 直接跳转到这个接口获取其他信息
        //拼接这个访问url去调用这个“/userInfo”接口，去获取token，然后获取openid、头像等信息
//        String url = projectUrlConfig.getWechatMpAuthorize() + "/show/score_analysis/wechat/userInfo";
        String url =  "http://zhongkeruitong.top/score_analysis/wechat/userInfo"; // 跳转请求下面接口的全路径url
        // 占位符使用，切记“，”，不是直接拼接“+”
        log.info("url= {}" ,url);
        String redirectUrl = wxMpService.oauth2buildAuthorizationUrl(url, WxConsts.OAUTH2_SCOPE_USER_INFO, URLEncoder.encode(returnUrl));
        log.info("【微信网页授权】 获取code，redirectUrl= {}" , redirectUrl);
        //return "redirectUrl: " + redirectUrl; // redirectUrl：这个不是java的跳转关键字
        return  "redirect:" + redirectUrl; //跳转的下面userInfo接口，请求这个接口获取信息数据
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
        WxMpUser wxMpUser = new WxMpUser();
        try{
            wxMpOAuth2AccessToken = wxMpService.oauth2getAccessToken(code);
            wxMpUser = wxMpService.oauth2getUserInfo(wxMpOAuth2AccessToken,null);//从这里获取用户各种信息，例如昵称了等
        } catch (WxErrorException e){
            log.error("【微信网页授权】{}" , e);
            throw new ScoreException(ResultEnum.WECHAT_MP_ERROR.getCode(),e.getError().getErrorMsg());

        }
        //获取当前用户的openid，昵称、微信头像，直接存放到数据库中
        String openId = wxMpOAuth2AccessToken.getOpenId(); // 用户的openid
        String nickname= wxMpUser.getNickname(); // 用户昵称
        String headImgUrl = wxMpUser.getHeadImgUrl();//用户头像
        log.info("openid= {}" , openId);
        log.info("【昵称】 {}", nickname);
        log.info("【头像】 {}", headImgUrl);
        return "redirect:" + returnUrl + "?openid=" + openId;
    }


}
