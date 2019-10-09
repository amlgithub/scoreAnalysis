package com.zgczx.dto;

import com.zgczx.dataobject.score.ExamCoversionTotal;
import lombok.Data;

import java.util.List;

/**
 * @author aml
 * @date 2019/9/16 14:08
 */
@Data
public class ExamCoversionTotalSingleDTO {
    private ExamCoversionTotal examCoversionTotal;
    //班级排名
    private int classRank;
    //年级排名
    private int gradeRank;
    // 年级排名进退名次
    private int waveGrade;
    // 班级排名进退名次
    private int waveClass;
    // 班级人数
    private int classNumber;
    //年级人数
    private int gradeNumber;
    //本此考试的总分标准
    private int sumScore;
    // 语文总分标准
    private int languageScore;
    // 数学满分标准
    private int mathScore;
    // 英语满分标准
    private int englishScore;
    // 物理满分标准
    private int physicalScore;
    // 化学满分标准
    private int chemistryScore;
    // 生物满分标准
    private int biologicalScore;
    // 政治满分标准
    private int politicalScore;
    // 历史满分标准
    private int historyScore;
    // 地理满分标准
    private int geographyScore;
    // subject此科目的对应分数
    private String score;
}
