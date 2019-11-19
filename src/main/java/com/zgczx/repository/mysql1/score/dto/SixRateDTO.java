package com.zgczx.repository.mysql1.score.dto;

import lombok.Data;

/**
 * @author aml
 * @date 2019/9/19 15:30
 */
@Data
public class SixRateDTO {
//    // 高分率
//    private  double highNumRate;
//    // 优秀率
//    private double excellentRate;
//    //良好率
//    private double goodRate;
//    // 及格率
//    private double passRate;
//    // 低分率
//    private double failRate;
//    // 超均率
//    private double beyondRate;
//    // 所处率值
//    private String locationRate;

    // 高分人数
    private  int highNumRate;
    // 优秀人数
    private int excellentRate;
    //良好人数
    private int goodRate;
    // 及格人数
    private int passRate;
    // 低分人数
    private int failRate;
    // 超均人数
    private int beyondRate;
    // 所处率值
    private String locationRate;
}
