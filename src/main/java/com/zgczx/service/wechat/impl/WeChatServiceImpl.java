package com.zgczx.service.wechat.impl;

import com.alibaba.fastjson.JSONObject;
import com.zgczx.config.wechatconfig.ProjectUrlConfig;
import com.zgczx.config.wechatconfig.WeChatAccountConfig;
import com.zgczx.enums.ResultEnum;
import com.zgczx.exception.WechatException;
import com.zgczx.repository.mysql3.unifiedlogin.dao.WechatLoginDao;
import com.zgczx.repository.mysql3.unifiedlogin.model.WechatLogin;
import com.zgczx.service.wechat.WeChatService;
import com.zgczx.utils.CharacterVerifiyUtil;
import com.zgczx.utils.EmojiUtil;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author aml
 * @date 2019/11/21 15:41
 */
@Service
@Slf4j
public class WeChatServiceImpl implements WeChatService {

    private final static Logger logger = LoggerFactory.getLogger(WeChatServiceImpl.class);

    @Autowired
    private WeChatService weChatService;

    @Autowired
    private WeChatAccountConfig weChatAccountConfig;

    @Autowired
    private ProjectUrlConfig projectUrlConfig;

    @Autowired
    private WechatLoginDao wechatLoginDao;

    /**
     * 注入微信第三方工具类
     */
    @Autowired
    WxMpService wxMpService;

    /**
     * RestTemplate:是spring用于同步客户端HTTP访问的中心类
     * 1. 它简化了与HTTP服务器的通信，并实施RESTful原则。
     * 2. 它处理HTTP连接，留下应用程序代码来提供url
     * （使用可能的模板变量）并提取结果。
     * @return
     */
    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }


    /**
     * 从微信公众号拿授权
     *
     * @param returnUrl
     * @param path
     * @return
     */
    @Override
    public String getAuthorizeFromWechat(String returnUrl, String path) {

        /*
         * url：用户授权完成后的重定向链接，即用户自己定义的回调地址
         *
         * String url = "http://zhongkeruitong.top/wechat/userInfo?path="+path;
         */
        String url = projectUrlConfig.getWechatMpAuthorize()+path;

        logger.info("returnUrl:  "+ returnUrl);
        logger.info("Path: " + path);

        /*
         * 用户在调用authorize方法时，需传入一个回调地址; 微信服务器会请求这个地址，并把参数附在这个地址后面
         *
         * 调用微信第三方SDK的oauth2buildAuthorizationUrl方法,封装了直接请求微信API的方法
         * 其中 redirectUrl 中，包含获取用户头像，姓名等信息所需要的code等参数
         *
         * oauth2授权的url连接
         * String CONNECT_OAUTH2_AUTHORIZE_URL = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=code&scope=%s&state=%s#wechat_redirect";
         *
         * 前端请求此接口的一个例子，其中returnUrl为
         *     returnUrl:  /index.html
         *     Path: menu
         *
         * 请求微信的授权接口，微信执行完后，会请求我们传给它的回调地址（回调地址此处我们定义为请求/userInfo接口）
         * 微信会在此回调地址后附上一些参数（比如code）
         *
         *     redirectUrl: https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx0b6356c5690adc27&redirect_uri=http%3A%2F%2Fzhongkeruitong.top%2Fwechat%2FuserInfo%3Fpath%3Dmenu&response_type=code&scope=snsapi_userinfo&state=%2Findex.html#wechat_redirect
         */

        String redirectUrl = wxMpService.oauth2buildAuthorizationUrl(url, WxConsts.OAUTH2_SCOPE_USER_INFO, URLEncoder.encode(returnUrl));

        logger.info("redirectUrl ---- "+redirectUrl);

        /*请求 /userInfo 接口*/

        return redirectUrl;
    }

    /**
     * 获取用户信息
     *
     * @param code
     * @param returnUrl
     * @param path
     * @return
     */
    @Override
    public String getUserInfoFromWechat(String code, String returnUrl, String path) {

        WxMpOAuth2AccessToken wxMpOAuth2AccessToken = new WxMpOAuth2AccessToken();
        try {
            /*
             * 用code换取oauth2的access token
             *
             * 详情请见: http://mp.weixin.qq.com/wiki/index.php?title=网页授权获取用户基本信息
             */
            wxMpOAuth2AccessToken = wxMpService.oauth2getAccessToken(code);
        } catch (WxErrorException e) {
            logger.error("【微信网页授权】{}", e);
            throw new WechatException(ResultEnum.WECHAT_MP_ERROR.getCode(), e.getError().getErrorMsg());
        }

        /*
         * useropenid： 用户openid（每个用户，对于一个公众号都有一个微信的id号）
         * access_token： 授权凭证
         */
        String useropenid = wxMpOAuth2AccessToken.getOpenId();
        String access_token = wxMpOAuth2AccessToken.getAccessToken();

        WxMpUser wxMpUser = new WxMpUser();
        try {
            /*
             * 用oauth2获取用户信息, 当前面引导授权时的scope是snsapi_userinfo的时候才可以
             *
             * @param lang zh_CN, zh_TW, en
             */
            wxMpUser = wxMpService.oauth2getUserInfo(wxMpOAuth2AccessToken, null);
        } catch (WxErrorException e) {
            logger.error("【微信网页授权】{}", e);
            throw new WechatException(ResultEnum.WECHAT_MP_ERROR.getCode(), e.getError().getErrorMsg());
        }
        /*
         * nickname： 用户微信名
         * headimgurl： 用户微信头像
         */
        String nickname = wxMpUser.getNickname();
        String headimgurl = wxMpUser.getHeadImgUrl();


        logger.info("nickname:   "+ nickname);
        logger.info("headimgurl:   "+ headimgurl);
        logger.info("useropenid:   "+ useropenid);
        logger.info("access_token:   "+ access_token);


        //UserLogin userLogin = userDao.findAllByWechatId(useropenid);
        WechatLogin userLogin = wechatLoginDao.findAllByOpenid(useropenid);

        if (userLogin != null){
            logger.info("path的参数：{}",path);
            logger.info("跳转url-1：{}","http://zhongkeruitong.top/score_analysis/index.html#/home" +"?useropenid="+useropenid+
                    "&access_token="+access_token+"&headimgurl="+headimgurl+"&path="+path);

            if (path.equals("menu3")){
                // 试试跳转到 此项目的首页面url
                return "http://zhongkeruitong.top/score_analysis/index.html#/bbs/hot" +"?useropenid="+useropenid+
                        "&access_token="+access_token+"&headimgurl="+headimgurl+"&path="+path;
            }
            if (path.equals("menu2")){
                // 试试跳转到 此项目的首页面url
                return "http://zhongkeruitong.top/score_analysis/index.html#/lineCourse" +"?useropenid="+useropenid+
                        "&access_token="+access_token+"&headimgurl="+headimgurl+"&path="+path;
            }
            // 试试跳转到 此项目的首页面url
            return "http://zhongkeruitong.top/score_analysis/index.html#/home" +"?useropenid="+useropenid+
                    "&access_token="+access_token+"&headimgurl="+headimgurl+"&path="+path;
        }
        //微信端首次授权获取信息，并存入到wechat_login（微信-学号关联表）表中去
        WechatLogin wechatLogin = new WechatLogin();
        wechatLogin.setOpenid(useropenid);

        // 验证用户昵称上是否有特殊字符
        boolean emoji = CharacterVerifiyUtil.findEmoji(nickname);
        if (emoji){
            nickname = EmojiUtil.emojiConverterToAlias(nickname);
        }

        wechatLogin.setNickName(nickname);
        wechatLogin.setHeadimgurl(headimgurl);
        Timestamp date = new Timestamp(System.currentTimeMillis());
        wechatLogin.setInserttime(date);
        wechatLogin.setUpdatetime(date);
        wechatLoginDao.save(wechatLogin);


        logger.info(projectUrlConfig.getUserWebURL()+"?useropenid="+useropenid+
                "&access_token="+access_token+"&path="+path+"&headimgurl="+headimgurl);
        logger.info("跳转url-2：{}","http://zhongkeruitong.top/score_analysis/index.html#/home" +"?useropenid="+useropenid+
                "&access_token="+access_token+"&headimgurl="+headimgurl+"&path="+path);

        if (path.equals("menu3")){
            // 试试跳转到 此项目的首页面url
            return "http://zhongkeruitong.top/score_analysis/index.html#/bbs/hot" +"?useropenid="+useropenid+
                    "&access_token="+access_token+"&headimgurl="+headimgurl+"&path="+path;
        }
        if (path.equals("menu2")){
            // 试试跳转到 此项目的首页面url
            return "http://zhongkeruitong.top/score_analysis/index.html#/lineCourse" +"?useropenid="+useropenid+
                    "&access_token="+access_token+"&headimgurl="+headimgurl+"&path="+path;
        }
        // 试试跳转到 此项目的首页面url
        return "http://zhongkeruitong.top/score_analysis/index.html#/home" +"?useropenid="+useropenid+
                "&access_token="+access_token+"&headimgurl="+headimgurl+"&path="+path;


//        return projectUrlConfig.getUserWebURL()+"?useropenid="+useropenid+
//                "&access_token="+access_token+"&path="+path+"&headimgurl="+headimgurl;

    }




    @Override
    public String getJsapiTicket(HttpSession session) {
        //获取AccessToken
        Map<String,String> params = new HashMap<String,String>();
        params.put("grant_type","client_credential");
        params.put("appid", weChatAccountConfig.getMpAppId());
        params.put("secret",weChatAccountConfig.getMpAppSecret());
        //输出access_token的参数等
        log.info("token参数：{}", params);
        log.info("获取token的网址: {}", projectUrlConfig.getAccessToken() );
        log.info("JSONObject.class: {}", JSONObject.class );

        //ResponseEntity标识整个http相应：状态码、头部信息以及相应体内容。因此我们可以使用其对http响应实现完整配置。
        ResponseEntity<JSONObject> responseEntity = restTemplate().getForEntity(projectUrlConfig.getAccessToken(), JSONObject.class,params);
        log.info("【输出responseEntity访问链接：】{}",responseEntity);
        //从微信的返回链接 获取 access_token的值
        String accessToken = responseEntity.getBody().getString("access_token");
        //将token存放到session中
        session.setAttribute("accessToken",accessToken);
        log.info("【打印access_token: 】{}",accessToken);
        //访问微信获取js通行证的链接，获取返回code
        ResponseEntity<JSONObject> forEntity = restTemplate().getForEntity(projectUrlConfig.getticket, JSONObject.class, accessToken);
        String ticket = forEntity.getBody().getString("ticket");
        log.info("【打印ticket： 】{}",ticket);

        return ticket;
    }

    @Override
    public JSONObject getSign(String url, HttpSession session) {
        //1. 获取jsapi的许可证
        String jsapiTicket = weChatService.getJsapiTicket(session);
        //2. 生成签名的随机串
        String nonceStr = createNonceStr();
        //3. 生成签名的时间戳
        String timestamp = createTimestamp();
        //4. 加密后的签名
        String signature = "";
        String finalUrl = url;
        //5. 当前网页的URL，不包含“#”和其后面
        if (url.indexOf("#") > 0){
            finalUrl = url.substring(0,url.indexOf("#"));
        }
        //6. 注意参数名称必须小写，并且有序
        String params = "jsapi_ticket="+jsapiTicket +
                "&noncestr="+nonceStr +
                "&timestamp="+timestamp +
                "&url="+finalUrl;
        log.info("【打印params： 】,{}",params);
        log.info("【打印jsapi_ticket: 】,{}",jsapiTicket);
        log.info("【打印出url: 】,{}",url);

        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
            messageDigest.reset();
            messageDigest.update(params.getBytes("UTF-8"));
            //获取加密后的签名, 最终完成hash值的计算
            signature = byteToHex(messageDigest.digest());

        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //封装数据，严格按照微信要求
        JSONObject json = new JSONObject();
        json.put("appId", weChatAccountConfig.getMpAppId());
        json.put("url", finalUrl);
        json.put("jsapi_ticket", jsapiTicket);
        json.put("nonceStr", nonceStr);
        json.put("timestamp", timestamp);
        json.put("signature", signature);
        log.info("json-->" + json.toJSONString());
        return json;
    }

    //2. 生成签名的随机串
    private static String createNonceStr(){
        return UUID.randomUUID().toString();
    }
    //3. 生成签名的时间戳
    private static String createTimestamp(){
        return Long.toString(System.currentTimeMillis() / 1000);
    }

    //获取加密后的签名
    private static String byteToHex(byte[] hash){
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            // “%02”: 以十六进制输出， “2”: 为指定的输出字段的宽度，如果位数小于2，则左端补0
            formatter.format("%02x", b);
        }
        String s = formatter.toString();
        formatter.close();
        return s;
    }
}
