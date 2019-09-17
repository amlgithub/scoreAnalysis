package com.zgczx.dto;

import com.zgczx.dataobject.score.ExamCoversionTotal;
import lombok.Data;

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
}
