package com.zgczx.dto;

import com.zgczx.dataobject.score.ExamCoversionTotal;
import lombok.Data;

/**
 * @author aml
 * @date 2019/9/24 20:30
 */
@Data
public class HistoricalAnalysisSingleDTO {

    private ExamCoversionTotal examCoversionTotal;
    //单科班级排名
    private int classRank;
    //单科年级排名
    private int gradeRank;
    // 班级平均分
    private String classAverage;
    // 年级平均分
    private String gradeAverage;
    // 年级排名的百分率
    private String gradePercentage;
    // 班级排名的百分率
    private String classPercentage;
    // 班级平均分百分率
    private String classAveragePercentage;
    // 年级平均分百分率
    private String gradeAveragePercentage;
    // 单科分数的百分率
    private String singleScorePercentage;
}
