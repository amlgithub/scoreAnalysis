package com.zgczx.repository.mysql2.scoretwo.dto;

import lombok.Data;

/**
 * 定位对比图一上的 评语中的四个值
 * 班级最高分、年级最高分、班级平均分、年级平均分
 * @author aml
 * @date 2019/11/25 10:49
 */
@Data
public class CommentValueDTO {
    //班级最高分
    private String classHighScore;
    //年级最高分
    private String gradeHighScore;
    //班级平均分
    private String classAvgScore;
    //年级平均分
    private String gradeAvgScore;
    // 自己的总分分数
    private String totalScore;
}
