package com.zgczx.constans;

/**
 * Explain（解释）：Redis常量配置
 * 过期时间等
 * @author aml
 * @date 2019/11/8 21:02
 */
public interface RedisConstans {

    /**
     * token 前缀
     */
    String TOKEN_PREFIX = "token_%s";

    /**
     * 过期时间
     */
    Integer EXPIPE = 7200;// 单位：s
}
