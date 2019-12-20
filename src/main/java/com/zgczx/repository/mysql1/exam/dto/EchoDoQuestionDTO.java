package com.zgczx.repository.mysql1.exam.dto;

import lombok.Data;

/**
 * 回显 用户 的做题情况
 * @author aml
 * @date 2019/12/20 12:51
 */
@Data
public class EchoDoQuestionDTO {
    // 题号
    private int questionNo;

    // 对应的当时填写的文本
    private String questionNoText;

}
