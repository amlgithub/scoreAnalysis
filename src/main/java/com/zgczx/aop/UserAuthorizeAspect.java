package com.zgczx.aop;

import com.zgczx.constans.CookieConstant;
import com.zgczx.constans.RedisConstans;
import com.zgczx.exception.UserAuthorizeException;
import com.zgczx.utils.CookieUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * explain：权限认证的切面
 * 不能直接访问某个接口的url，只有登录成功后才能访问接口
 * @author aml
 * @date 2019/11/9 13:35
 */
//TODO 暂时屏蔽 能够登录后需要放开，也就是有开放平台登录账号
// 2019年11.9号，先不放开，看需求再说
    // 微信端没必要使用权限认证，因为用户根本就不知道接口的url
    //但是PC端得需要，因为用户可以从 浏览器的窗口地址栏中看到具体的url
//@Aspect
//@Component
@Slf4j
public class UserAuthorizeAspect {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 定义切面方法
     * com.zgczx.controller.score:包名
     * Score*： 所有以Score开头的java文件
     * 例如：ScoreController
     */
    @Pointcut("execution(public * com.zgczx.controller.score.Score*.*(..))"+ //这些方法都要经过切面
    //"&& execution(public * com.zgczx.controller.user.UserController.login())" +
    "&& !execution(public * com.zgczx.controller.user.UserController.*(..))")//排除UserController中的方法，包含登录登出
    public void verify(){}

    @Before("verify()")
    public void doVerify(){
        //接收到请求， 记录请求内容
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();//获取HttpServletRequest
        //1.查询cookie
        Cookie cookie = CookieUtil.get(request, CookieConstant.TOKEN);
        if (cookie == null){
            log.warn("【登录校验】 Cookie中查不到token");
            //这里抛出异常，在handler中处理异常让其跳转到授权页面
            throw new UserAuthorizeException();
        }
        //2.去Redis中查询cookie
        String redisTokenValue = stringRedisTemplate.opsForValue().get(String.format(RedisConstans.TOKEN_PREFIX, cookie.getValue()));
        if (redisTokenValue == null){
            log.warn("【登录校验】 Redis中查不到token");
            //这里抛出异常，在handler中处理异常让其跳转到授权页面
            throw new UserAuthorizeException();
        }

    }


}
