package com.zgczx.repository.mysql1.score.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

/**
 * @author aml
 * @date 2019/10/29 19:10
 */
@Entity
@Data
//@Table(name = "manually_enter_grades", schema = "score_ananlysis_dev", catalog = "")
@ApiModel(value = "ManuallyEnterGrades实体对象", description = "批量录入实体")
@Table(name = "manually_enter_grades")
public class ManuallyEnterGrades {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Basic
    @Column(name = "wechat_openid")
    private String wechatOpenid;

    @Basic
    @Column(name = "student_number")
    private String studentNumber;

    @Basic
    @Column(name = "subject_name")
    private String subjectName;

    @Basic
    @Column(name = "score")
    private String score;

    @Basic
    @Column(name = "class_rank")
    private String classRank;

    @Basic
    @Column(name = "grade_rank")
    private String gradeRank;
    @Basic
    @Column(name = "exam_name")
    private String examName;

    //解决返回给前端的是一个时间戳，改成为年月日时分秒格式
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Timestamp inserttime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Timestamp updatetime;

    @Column(name = "imgs")
    @Basic
    private String imgs;


}
