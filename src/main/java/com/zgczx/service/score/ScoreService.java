package com.zgczx.service.score;

import com.zgczx.dataobject.score.ExamCoversionTotal;
import com.zgczx.dataobject.score.ExamInfo;
import com.zgczx.dto.ExamCoversionTotalDTO;
import com.zgczx.dto.ExamCoversionTotalSectionDTO;
import com.zgczx.dto.ExamCoversionTotalSingleDTO;
import com.zgczx.dto.SixRateDTO;

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
}
