package com.zgczx.repository.mysql1.exam.dto;

import com.zgczx.repository.mysql1.exam.model.Question;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author aml
 * @date 2019/12/13 16:38
 */
@Data
public class QuestionDTO {

    private Question question;

//    // 选项列表ABCD
//    private List<String> option;

    // 选项列表ABCD
    private List<String> randomOption;

    // 此题的正确选项，随着答案文本，选项跟着变化
    private String rightOption;

}