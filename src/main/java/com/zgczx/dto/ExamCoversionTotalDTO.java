package com.zgczx.dto;

import com.zgczx.dataobject.score.ExamCoversionTotal;
import lombok.Data;

/**
 * @author aml
 * @date 2019/9/11 21:00
 * DTO（Data Transfer Object） ：数据传输对象， Service 或 Manager 向外传输的对象
 *  controller中真正返回给前端的数据对象
 */
@Data
public class ExamCoversionTotalDTO {

    private ExamCoversionTotal examCoversionTotal;
    // 年级排名进退名次
    private int waveGrade;
    // 班级排名进退名次
    private int waveClass;

}
