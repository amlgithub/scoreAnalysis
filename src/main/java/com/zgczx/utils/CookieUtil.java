package com.zgczx.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * EXPLAN: cookie工具类，
 *         cookie的set、get、clean（清除）
 * @author aml
 * @date 2019/11/8 21:23
 */
public class CookieUtil {

    /**
     * explain：清除cookie就是重新设置cookie
     *          将cookie设置为null，过期时间为：0
     * @param response
     * @param name
     */
    public static void del(HttpServletResponse response,
                           String name){

        set(response,name,null,0);
    }

    /**
     * 设置cookie
     * @param response
     * @param name
     * @param value
     * @param maxAge 过期时间
     */
    public static void set(HttpServletResponse response,
                           String name,
                           String value,
                           int maxAge){
        Cookie cookie = new Cookie(name,value);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);//设置过期时间
        response.addCookie(cookie);
    }

    /**
     * explain： 获取cookie
     * @param request
     * @param name
     * @return
     */
    public static Cookie get(HttpServletRequest request,
                             String name){
        Map<String, Cookie> cookieMap = readCookieMap(request);
        if (cookieMap.containsKey(name)){
            Cookie cookie = cookieMap.get(name);
            return cookie;
        }else {
            return null;
        }

    }

    /**
     * explain：将cookie数组封装成map，方便获取
     * @param request
     * @return
     */
    private static Map<String, Cookie> readCookieMap(HttpServletRequest request){
        Map<String,Cookie> cookieMap = new HashMap<>();
        Cookie[] cookies = request.getCookies();
        if (cookies != null){
            for (Cookie cookie:cookies){
                cookieMap.put(cookie.getName(),cookie);
            }
        }

        return cookieMap;
    }
}
