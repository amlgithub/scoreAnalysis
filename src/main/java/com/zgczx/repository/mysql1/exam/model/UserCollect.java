package com.zgczx.repository.mysql1.exam.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * 用户 收藏表
 * @author aml
 * @date 2019/12/17 16:47
 */
@DynamicInsert
@Data
@Entity
@Table(name = "e_user_collect")
public class UserCollect {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "student_number")
    private String studentNumber;

    private String openid;

    private String subject;

    @Column(name = "question_id")
    private int questionId;

    @Column(name = "exam_paper_id")
    private int examPaperId;

    @Column(name = "user_answer")
    private String userAnswer;

    private String classification;

    private int valid;
    //解决返回给前端的是一个时间戳，改成为年月日时分秒格式
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Timestamp inserttime;
    //解决返回给前端的是一个时间戳，改成为年月日时分秒格式
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Timestamp updatetime;
}
