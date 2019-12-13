package com.zgczx.repository.mysql1.exam.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * 题库中的 用户做题记录表，存放用户所有做题记录
 * @author aml
 * @date 2019/12/11 14:34
 */
@Data
@Entity
@Table(name = "e_user_question_record")
public class UserQuestionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String studentNumber;
    private String subject;
    private String questionTextContent;
    private String userAnswer;
    private int doRight;
    private int questionId;
    private int examPaperId;
    private String customerScore;
    private String questionScore;
    private String doTime;
    private int status;
    //解决返回给前端的是一个时间戳，改成为年月日时分秒格式
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Timestamp inserttime;
    //解决返回给前端的是一个时间戳，改成为年月日时分秒格式
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Timestamp updatetime;

}
