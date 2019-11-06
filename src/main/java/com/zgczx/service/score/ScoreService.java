package com.zgczx.service.score;

import com.zgczx.repository.mysql1.score.dto.*;
import com.zgczx.repository.mysql2.scoretwo.dto.ExamCoversionTotalDTO;
import com.zgczx.repository.mysql2.scoretwo.dto.ExamCoversionTotalSectionDTO;
import com.zgczx.repository.mysql2.scoretwo.dto.ExamCoversionTotalSingleDTO;
import com.zgczx.repository.mysql2.scoretwo.model.ExamCoversionTotal;
import com.zgczx.repository.mysql1.score.model.ExamInfo;

import java.util.List;

/**
 * @author aml
 * @date 2019/9/10 17:15
 */
public interface ScoreService {

    /**
     * 获取学生的所有成绩
     * @param userId 学号id
     * @param examType 具体哪次考试
     * @return ExamCoversionTotal对象
     */
    ExamCoversionTotal getExamCoversionTotal(Integer userId, String examType);

    /**
     *  获取所有考试名称
     * @return 多个ExamInfo对象
     */
    List<ExamInfo> getListExamInfols();

    List<ExamCoversionTotalDTO> getExamCoversionTotalInfo(String stuNumber, String examType);

    List<ExamCoversionTotalSingleDTO> getExamCoversionTotalSingleInfo(String stuNumber, String examType, String subject);

    List<ExamCoversionTotalSectionDTO> getExamCoversionTotalSectionInfo(String stuNumber, String examType);

    List<SixRateDTO> getSixRateInfo(String stuNumber, String examType);

    List<SubjectAnalysisDTO> getSubjectAnalysisInfo(String stuNumber, String examType);

    List<HistoricalAnalysisTotalDTO> getHistoricalAnalysisTotalInfo(String stuNumber, String examType,String openid);

    List<HistoricalAnalysisSingleDTO> getHistoricalAnalysisSingleInfo(String stuNumber, String examType, String subject,String openid);

    List<AsahiChartAllRateDTO> getAsahiChartAllRate(String stuNumber, String examType);

    List<ScoreReportDTO> getScoreReport(String stuNumber, String examType);

}
