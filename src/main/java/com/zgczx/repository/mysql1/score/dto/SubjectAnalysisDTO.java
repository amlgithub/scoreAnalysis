package com.zgczx.repository.mysql1.score.dto;

import com.zgczx.repository.mysql2.scoretwo.model.ExamCoversionTotal;
import lombok.Data;

import java.util.Map;

/**
 * @author aml
 * @date 2019/9/23 11:17
 */
@Data
public class SubjectAnalysisDTO {

    private ExamCoversionTotal examCoversionTotal;

    // 学科贡献率，各单科贡献率，各科分数和总分的比值
    private Map<String, String> contributionRate;

    // 学科均衡差值，即各科年排排名和总分年级排名的差值
    private Map<String, String> equilibriumDifference;

    // 年级的标准率值，即年级排名 / 年级总人数
    private String gradeRate;

//    // 上次考试的 学科贡献率，各单科贡献率，各科分数和总分的比值
//    private Map<String, String> oldcontributionRate;
//
//    // 前三次 学科贡献率的平均值
//    private Map<String, String> avgcontributionRate;

    // 此map中九门科目的map，每一个map中最多能放
    //  本次、上次、平均、差值（本次 - 平均）   率值
    private Map<String, Map<String, String>> map;
}
