package com.zgczx.repository.mysql1.score.dto;

import lombok.Data;

import java.util.Map;

/**
 * @author aml
 * @date 2019/9/27 12:12
 */
@Data
public class ScoreReportDTO {
    // 总分分数
    private String totalScore;
    // 总分的年级排名
    private int totalScoreGradeRank;
    // 总分的班级排名
    private int totalScoreClassRank;

    //总分的年级平均分
    private String totalAverage;

    private Map<String, Map<String, String>> map;

    //总分满分数值
    private String totalScoreStandard;
    //班级总人数
    private int totalClassNumber;
    // 年级总人数
    private int totalGradeNumber;

//    // 具体科目的分数map，k: 科目名称，V：对应的分数
//    private Map<String, String> subjectScoreMap;
//    // 具体科目的年级排名，K:科目名称，V：对应的年级排名
//    private Map<String, Integer> subjectGradeRankMap;
//    // 具体科目的班级排名，K:科目名称，V：对应的班级排名
//    private Map<String, Integer> subjectClassRankMap;
//    // 总分满分标准、各科满分标准
//    private Map<String, Integer> subjectStandardMap;
//


//    //语文map，一个map中存放，分数、年排、班排、满分标准
//    private Map<String, String> yuwenMap;
//    private Map<String, String> shuxueMap;
//    private Map<String, String> yingyuMap;
//    private Map<String, String> wuliMap;
//    private Map<String, String> huaxueMap;
//    private Map<String, String> shengwuMap;
//    private Map<String, String> diliMap;
//    private Map<String, String> lishiMap;
//    private Map<String, String> zhengzhiMap;
}
