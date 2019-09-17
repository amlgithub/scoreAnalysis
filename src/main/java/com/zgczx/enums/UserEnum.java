package com.zgczx.enums;

import lombok.Getter;

/**
 * 用户状态枚举类
 *
 * @author jason
 */
@Getter
public enum UserEnum {

    /**
     * 数据库执行错误，请检查数据库操作
     */
    DB_ERROR(600,"数据库执行错误，请检查数据库操作"),

    /**
     * 该stuOpenid没有被注册
     */
    stuOpenid_not_registered(601,"该stuOpenid没有被注册"),

    /**
     * 该teaOpenid没有被注册
     */
    teaOpenid_not_registered(602,"该teaOpenid没有被注册"),

    /**
     * 该stuOpenid已经被创建
     */
    stuOpenid_is_created(603,"该stuOpenid已经被创建"),

    /**
     * 该teaOpenid已经被创建
     */
    teaOpenid_is_created(603,"该teaOpenid已经被创建"),



    ;

    private Integer code;

    private String message;

    UserEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
