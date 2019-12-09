package com.zgczx.repository.mysql1.score.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * 对比分析中目标设定表
 * @author aml
 * @date 2019/11/29 15:35
 */
@Data
@Entity
@Table(name="goal_set")
public class GoalSet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "student_number")
    private String studentNumber;
    @Column(name = "exam_name")
    private String examName;
    private String openid;
    @Column(name = "total_score")
    private String totalScore;
    private String yuwen;
    private String shuxue;
    private String yingyu;
    private String wuli;
    private String huaxue;
    private String shengwu;
    private String zhengzhi;
    private String lishi;
    private String dili;
    //解决返回给前端的是一个时间戳，改成为年月日时分秒格式
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Timestamp inserttime;
    //解决返回给前端的是一个时间戳，改成为年月日时分秒格式
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Timestamp updatetime;


}
