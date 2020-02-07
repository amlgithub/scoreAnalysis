package com.zgczx.repository.mysql1.exam.dto;

import lombok.Data;

import java.util.List;

/**
 * @author aml
 * @date 2019/12/24 14:19
 */
@Data
public class EchoPaperTotalDTO {

    private EchoPaperDTO question;

    private List<String> randomOption;//选项列表

    private int complete; //是否做完这个卷子

    private String userOption;//用户的每道题的选项

    private int collect; //这道题是否已经收藏过，1为收藏，2为未收藏

    private String rightOption;// 正确选项

    private String sourcePaperId;// 来源试卷id

    private List<String> imgList; //= new LinkedList<>();//2.4 新修改
}
