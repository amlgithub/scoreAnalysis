package com.zgczx.service.wechat.impl;

import com.alibaba.fastjson.JSONObject;
import com.zgczx.config.wechatconfig.ProjectUrlConfig;
import com.zgczx.config.wechatconfig.WeChatAccountConfig;
import com.zgczx.service.wechat.WeChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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

    @Autowired
    private WeChatService weChatService;

    @Autowired
    private WeChatAccountConfig weChatAccountConfig;

    @Autowired
    private ProjectUrlConfig projectUrlConfig;

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
        ResponseEntity<JSONObject> forEntity = restTemplate().getForEntity(projectUrlConfig.getticket, JSONObject.class, params);
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
