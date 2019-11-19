package com.zgczx.service.user.impl;

import com.zgczx.constans.CookieConstant;
import com.zgczx.constans.RedisConstans;
import com.zgczx.enums.ResultEnum;
import com.zgczx.exception.ScoreException;
import com.zgczx.repository.mysql1.user.dao.WechatStudentDao;
import com.zgczx.repository.mysql1.user.model.WechatStudent;
import com.zgczx.service.user.UserService;
import com.zgczx.utils.CookieUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


/**
 * @author aml
 * @date 2019/11/8 19:41
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    WechatStudentDao wechatStudentDao;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    private String info;
    @Override
    public WechatStudent login(String openid,
                               HttpServletRequest request,
                               HttpServletResponse response) {
        // 1. openid去和数据库里的数据匹配
        WechatStudent wechatStudent = wechatStudentDao.findByOpenid(openid);
        if (wechatStudent == null){
            info = "您还没有授权，暂无法登录";
            log.error("WechatStudent实体未找到，{} ", info);
            throw new ScoreException(ResultEnum.RESULE_DATA_NONE,info);
        }
        // 设置Redis切换db
        JedisConnectionFactory jedisConnectionFactory = (JedisConnectionFactory) stringRedisTemplate.getConnectionFactory();
        log.info("默认的当前所在的db= {} ", jedisConnectionFactory.getDatabase());
        jedisConnectionFactory.setDatabase(1);// 设置切换到指定的db上
        stringRedisTemplate.setConnectionFactory(jedisConnectionFactory);// 执行切换操作
        log.info("指定的db: {}", jedisConnectionFactory.getDatabase());

        // 2. 设置token至Redis
        String token = UUID.randomUUID().toString();
        Integer expire = RedisConstans.EXPIPE;// 过期时间
//        stringRedisTemplate.opsForValue().set("abc", "dsdsfdfsf");
        //设置：key,value,过期时间,时间单位 s
        //String类的format()方法用于创建格式化的字符串以及连接多个字符串对象
        //String.format(RedisConstans.TOKEN_PREFIX,token):将TOKEN_PREFIX和token（UUID）拼接起来组合成为Redis的key
        stringRedisTemplate.opsForValue().set(String.format(RedisConstans.TOKEN_PREFIX,token),openid,expire, TimeUnit.SECONDS);
        // 3. 设置token至cookie
        //设置cookie的name为：CookieConstant.TOKEN即为token； value为：token（UUID）
        CookieUtil.set(response, CookieConstant.TOKEN,token,CookieConstant.EXPIPE);

        return wechatStudent;
    }

    @Override
    public String logout(HttpServletRequest request, HttpServletResponse response) {

        // 1. 从cookie查询
        Cookie cookie = CookieUtil.get(request, CookieConstant.TOKEN);
        if (cookie != null){
            //2. 清除Redis
            // cookie中的：（name + value == Redis的key）
            //String类的format()方法用于创建格式化的字符串以及连接多个字符串对象
            stringRedisTemplate.opsForValue().getOperations().delete(String.format(RedisConstans.TOKEN_PREFIX,cookie.getValue()));

            // 3. 清除cookie
            CookieUtil.del(response,CookieConstant.TOKEN);

        }
        info = "清除cookie成功!";

        return info;
    }

//    @Override
//    public WechatStudent registerWechatStudent(WechatStudent wechatStudent) {
//        if (wechatStudent == null){
//            info = "【wechatStudent】实体为空";
//            log.error(info);
//            throw new ScoreException(ResultEnum.PARAM_IS_INVALID.getCode(),info);
//        }
//        if (StringUtils.isEmpty(wechatStudent.getOpenid())){
//            log.error("【学生注册】openid 为空,openid={}",wechatStudent.getOpenid());
//            throw new ScoreException(ResultEnum.PARAM_EXCEPTION.getCode(),ResultEnum.PARAM_EXCEPTION.getMessage());
//        }
//
//        WechatStudent save = wechatStudentDao.save(wechatStudent);
//
//        return save;
//    }


    @Override
    public WechatStudent registerWechatStudent(String openid, String foreignKeId) {
        WechatStudent wechatStudent = new WechatStudent();
        wechatStudent.setOpenid(openid);
        wechatStudent.setForeignKeId(Integer.parseInt(foreignKeId));
        WechatStudent save = wechatStudentDao.save(wechatStudent);
        return save;
    }
}
