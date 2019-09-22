package com.zgczx.dto;

import com.zgczx.dataobject.score.ExamCoversionTotal;
import lombok.Data;

import java.util.List;

/**
 * @author aml
 * @date 2019/9/17 21:11
 */
@Data
public class ExamCoversionTotalSectionDTO {

    private ExamCoversionTotal examCoversionTotal;
    // 三科：语数英的总分分值
    private Float threeSubject;
    //剩下的6选3的总分分值
    private Float comprehensive;
    //三科班级排名
    private int classRank;
    //三科年级排名
    private int gradeRank;
    //综合班级排名
    private int complexClassRank;
    //综合年级排名
    private int complexGradeRank;

    // 年级排名进退名次
    private int waveGrade;
    // 班级排名进退名次
    private int waveClass;
    //存放某学生的6选3的具体科目
    private List<String> list;
}
