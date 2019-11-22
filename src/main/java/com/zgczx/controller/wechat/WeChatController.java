package com.zgczx.controller.wechat;

import com.alibaba.fastjson.JSONObject;
import com.zgczx.VO.ResultVO;
import com.zgczx.config.wechatconfig.ProjectUrlConfig;
import com.zgczx.enums.ResultEnum;
import com.zgczx.exception.ScoreException;
import com.zgczx.service.wechat.WeChatService;
import com.zgczx.utils.ResultVOUtil;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.util.UUID;

/**
 * @author aml
 * @date 2019/10/12 19:50
 */
@Api(description = "微信相关模块")
@Controller
//@RestController
@RequestMapping("/wechat")
@Slf4j
public class WeChatController {

    @Autowired
    private WxMpService wxMpService;

    @Autowired
    private WeChatService weChatService;

    @Autowired
    private ProjectUrlConfig projectUrlConfig;

    @ApiOperation(value = "授权")
    @GetMapping("/authorize")
    public String authorize(@RequestParam("returnUrl") String returnUrl) {//returnUrl：本项目的首页面访问路径
        //1. 配置
        //2. 调用
        //url：http://zhongkeruitong.top/score_analysis/wechat/userInfo   ： 直接跳转到这个接口获取其他信息
        //拼接这个访问url去调用这个“/userInfo”接口，去获取token，然后获取openid、头像等信息
//        String url = projectUrlConfig.getWechatMpAuthorize() + "/show/score_analysis/wechat/userInfo";
        String url = "http://zhongkeruitong.top/score_analysis/wechat/userInfo"; // 跳转请求下面接口的全路径url
        // 占位符使用，切记“，”，不是直接拼接“+”
        log.info("url= {}", url);
        String redirectUrl = wxMpService.oauth2buildAuthorizationUrl(url, WxConsts.OAUTH2_SCOPE_USER_INFO, URLEncoder.encode(returnUrl));
        log.info("【微信网页授权】 获取code，redirectUrl= {}", redirectUrl);
        //return "redirectUrl: " + redirectUrl; // redirectUrl：这个不是java的跳转关键字
        return "redirect:" + redirectUrl; //跳转的下面userInfo接口，请求这个接口获取信息数据
    }

    /**
     * 按照开发文档获取用户token，
     * 1 第一步：用户同意授权，获取code
     * 2 第二步：通过code换取网页授权access_token
     * 3 第三步：刷新access_token（如果需要）
     * 4 第四步：拉取用户信息(需scope为 snsapi_userinfo)
     * 5 附：检验授权凭证（access_token）是否有效
     * 获取code后，请求以下链接获取access_token：  https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code
     *
     * @param code      请求一个微信的url获取所需要的code
     * @param returnUrl state=STAT，可随意写
     * @return 获取用户的token，从token获取用户openid
     */
    @GetMapping("/userInfo")
    public String userInfo(@RequestParam("code") String code,
                           @RequestParam("state") String returnUrl) {
        WxMpOAuth2AccessToken wxMpOAuth2AccessToken = new WxMpOAuth2AccessToken();
        WxMpUser wxMpUser = new WxMpUser();
        try {
            wxMpOAuth2AccessToken = wxMpService.oauth2getAccessToken(code);
            wxMpUser = wxMpService.oauth2getUserInfo(wxMpOAuth2AccessToken, null);//从这里获取用户各种信息，例如昵称了等
        } catch (WxErrorException e) {
            log.error("【微信网页授权】{}", e);
            throw new ScoreException(ResultEnum.WECHAT_MP_ERROR.getCode(), e.getError().getErrorMsg());

        }
        //获取当前用户的openid，昵称、微信头像，直接存放到数据库中
        String openId = wxMpOAuth2AccessToken.getOpenId(); // 用户的openid
        String nickname = wxMpUser.getNickname(); // 用户昵称
        String headImgUrl = wxMpUser.getHeadImgUrl();//用户头像
        log.info("openid= {}", openId);
        log.info("【昵称】 {}", nickname);
        log.info("【头像】 {}", headImgUrl);
        return "redirect:" + returnUrl + "?openid=" + openId;
    }

    /**
     * 获取 appid, timestamp，nonceStr，signature，
     * 目的 获取微信的js通行证，来调用微信的一些方法，例如微信上传图片等
     *
     * @param url
     * @param session
     * @return
     */
    @ApiOperation(value = "获取js通行证的各种参数")
    @ResponseBody
    @GetMapping("/getSign")
    public ResultVO<?> getSign(@RequestParam("url") String url, HttpSession session) {
        log.info("【打印参数url：】{}", url);
        JSONObject sign = weChatService.getSign(url, session);
        return ResultVOUtil.success(sign);
    }

    /**
     * 从微信服务器上保存图片到自己指定位置
     * @param serverIds 微信返回给端的图片id
     * @param session
     * @param request
     * @param response
     * @return
     */
    @ApiOperation(value = "将微信服务器上的图片下载到指定的图片服务器上")
    @PostMapping("/savePicture")
    @ResponseBody
    public static String savePicture(
            @ApiParam(value = "serverIds", required = true)
            @RequestParam("serverIds")String serverIds,
            HttpSession session,
            HttpServletRequest request,
            HttpServletResponse response){
        String filename = null;
        log.info("【打印serversIds: 】{}",serverIds);
        if (StringUtils.isNoneBlank(serverIds)){
            filename = saveImageToDisk(serverIds, session , response, request);
        }
        String string = "http://zhongkeruitong.top/image/" + filename;
        log.info("【返回前端的url地址：】{}",string);
        return string;
    }


    // 将字节流中的文件上传到自己指定的 图片服务器上
    private static String saveImageToDisk(String serverIds, HttpSession session, HttpServletResponse response, HttpServletRequest request){
        String filename = null;
        //根据mediaId去微信服务器上下载图片到指定的文件夹中
        InputStream inputStream = getMedia(serverIds, response, session);
        byte[] data = new byte[1024];
        int len = 0;
        FileOutputStream fileOutputStream = null;

        //指定图片服务的地址
        String savePath = "/home/bigdata/application/score-img/";
        //图片命名,以UUID来给文件命名
        filename = UUID.randomUUID() + ".jpa";
        try {
            fileOutputStream = new FileOutputStream(savePath+filename);
            //当字节流读取不到最后字节，就 一直读取文件
            while ((len = inputStream.read(data)) != -1){
                //从data中，将0~len 长度的字节写到输出流中
                fileOutputStream.write(data,0,len);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null){
                try {
                    //关闭输入流
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fileOutputStream != null){
                try {
                    //关闭输出流
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        log.info("【输出上传到图片服务器上的文件名：】{}",filename);
        return filename;
    }

    //将从微信服务器上下载的文件转化为字节流
    private static InputStream getMedia(String serverIds, HttpServletResponse response, HttpSession session){
        //1. 微信的get请求链接
        String url = "https://api.weixin.qq.com/cgi-bin/media/get";
        //2. 从session中获取token
        String accessToken = (String) session.getAttribute("accessToken");
        log.info("【打印从session中获取的token：】{}",accessToken);
        //拼接请求微信get链接的 参数值
        String params = "access_token=" + accessToken + "&media_id=" + serverIds;
        InputStream inputStream = null;
        String urlNameString = url + "?" + params;
        log.info("【打印出完整的get链接： 】{}",urlNameString);

        try {
            //请求微信的get链接，获取所需的值
            URL urlGet = new URL(urlNameString);
            HttpURLConnection http = (HttpURLConnection) urlGet.openConnection();
            http.setRequestMethod("GET");//必须是get方式请求
            http.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            http.setDoOutput(true);
            http.setDoInput(true);
            http.connect();

            //判断文件是什么格式的，jpg还是MP4等
            String fileexpandedName = getFileexpandedName(http.getHeaderField("Content-Type"));
            log.info("【输出文件的类型格式: 】{}",fileexpandedName);

            //将获取的微信的文件转化为byte流
            inputStream = http.getInputStream();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return inputStream;
    }

    /**
     * 根据内容类型判断文件扩展名,
     * 文件头类型 content-type
     * @param contentType 内容类型
     * @return
     */
    public static String getFileexpandedName(String contentType) {

        String fileEndWitsh = "";
        if ("image/jpeg".equals(contentType)){
            fileEndWitsh = ".jpg";
        } else if ("audio/mpeg".equals(contentType)){
            fileEndWitsh = ".mp3";
        } else if ("audio/amr".equals(contentType)){
            fileEndWitsh = ".amr";
        } else if ("video/mp4".equals(contentType)){
            fileEndWitsh = ".mp4";
        } else if ("video/mpeg4".equals(contentType)){
            fileEndWitsh = ".mp4";
        }
        return fileEndWitsh;
    }
}
