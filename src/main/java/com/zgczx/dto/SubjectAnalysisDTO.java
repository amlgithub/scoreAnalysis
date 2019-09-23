package com.zgczx.dto;

import com.zgczx.dataobject.score.ExamCoversionTotal;
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
    private Map<String, Integer> equilibriumDifference;
}
