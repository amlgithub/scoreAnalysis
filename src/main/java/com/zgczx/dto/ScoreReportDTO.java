package com.zgczx.dto;

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
    // 具体科目的分数map，k: 科目名称，V：对应的分数
    private Map<String, String> subjectScoreMap;
    // 具体科目的年级排名，K:科目名称，V：对应的年级排名
    private Map<String, Integer> subjectGradeRankMap;
    // 具体科目的班级排名，K:科目名称，V：对应的班级排名
    private Map<String, Integer> subjectClassRankMap;
    // 总分满分标准、各科满分标准
    private Map<String, Integer> subjectStandardMap;

}
