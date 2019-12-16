package com.zgczx.enums;

import lombok.Getter;

/**
 * A-Z 的26字母对应表
 * @author aml
 * @date 2019/12/15 16:22
 */
@Getter
public enum LetterEnum {

    A(0,"A"),
    B(1,"B"),
    C(2,"C"),
    D(3,"D"),
    E(4,"E")
    ;


    private Integer code;

    private String message;

    LetterEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
