package com.zgczx.config.wechatconfig;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author aml
 * @date 2019/10/12 20:05
 */
@Data
@ConfigurationProperties(prefix = "projectUrl")
@Component
public class ProjectUrlConfig {

    /**
     * 微信公众平台授权url
     */
    public String wechatMpAuthorize;

    /**
     * 微信开放平台授权url
     */
    public String wechatOpenAuthorize;

    /**
     * 成绩分析系统
     */
    public String score_analysis;

    /**
     * 获取公众号的AccessToken 的 URL
     */
    public String AccessToken;

    /**
     * 获取jsApi-ticket
     */
    public String getticket;
}
