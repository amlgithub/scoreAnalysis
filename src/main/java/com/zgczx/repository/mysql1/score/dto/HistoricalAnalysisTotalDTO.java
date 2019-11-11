package com.zgczx.repository.mysql1.score.dto;

import com.zgczx.repository.mysql2.scoretwo.model.ExamCoversionTotal;
import lombok.Data;

import java.util.Map;

/**
 * @author aml
 * @date 2019/9/24 16:08
 */
@Data
public class HistoricalAnalysisTotalDTO {

//    private ExamCoversionTotal examCoversionTotal;
////    //班级排名
////    private int classRank;
////    //年级排名
////    private int gradeRank;
//    // 班级平均分
//    private String classAverage;
//    // 年级平均分
//    private String gradeAverage;
//    // 年级排名的百分率
//    private String gradePercentage;
//    // 班级排名的百分率
//    private String classPercentage;
//    // 班级平均分百分率
//    private String classAveragePercentage;
//    // 年级平均分百分率
//    private String gradeAveragePercentage;
//    // 总分的百分率
//    private String totalScorePercentage;

    // 里面存放多个子map，例如班排map、年排map、平均分map等
   // private Map<String, Map<String, String>> map;

    private Map<Integer, Map<String, String>> mapTotal;

}
