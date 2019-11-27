package com.zgczx.repository.mysql2.scoretwo.dto;

import lombok.Data;

/**
 * 定位对比 1. 总分的 对比信息
 * @author aml
 * @date 2019/11/27 10:29
 */
@Data
public class TotalScoreInfoDTO {
    //我的排名
    private int myRank;
    //目标排名
    private int targetRank;
    //我的分数
    private String myScore;
    //目标分数
    private String targetScore;
    //分数差值
    private String scoreDifferentValue;
}
