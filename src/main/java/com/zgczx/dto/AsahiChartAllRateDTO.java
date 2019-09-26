package com.zgczx.dto;

import lombok.Data;

import java.util.Map;

/**
 * @author aml
 * @date 2019/9/26 10:50
 */
@Data
public class AsahiChartAllRateDTO {

    // 总分率值，即所得总分 / 总分满分标准
    private String totalScoreRate;

    // 三科分数之和率值，三科得分数和/ 三科总分
    private String threeSubjectsRate;

    // 6选3综合率值， 6选3综合得分 / 6选3综合满分标准分
    private String comprehensiveRate;

    // 所有真实科目的率值，k: 科目名称；v：所对应的率值
    private Map<String, String> allSubjectRateMap;

//
//    // 语文率值, 语文得分 / 语文总分
//    private int languageScoreRate;
//    // 数学率值, 数学得分 / 数学总分
//    private int mathScoreRate;
//    // 英语率值, 英语得分 / 英语总分
//    private int englishScoreRate;
//    // 物理率值, 物理得分 / 物理总分
//    private int physicalScoreRate;
//    // 化学率值, 化学得分 / 化学总分
//    private int chemistryScoreRate;
//    // 生物率值, 生物得分 / 生物总分
//    private int biologicalScoreRate;
//    // 政治率值, 政治得分 / 政治总分
//    private int politicalScoreRate;
//    // 历史率值, 历史得分 / 历史总分
//    private int historyScoreRate;
//    // 地理率值, 地理得分 / 地理总分
//    private int geographyScoreRate;
}
