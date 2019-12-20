package com.zgczx.repository.mysql1.exam.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * 题库中的 用户做题记录表，存放用户所有做题记录
 * @author aml
 * @date 2019/12/11 14:34
 */
@DynamicUpdate//生成动态的update语句,如果这个字段的值是null就不会被加入到update语句中
@DynamicInsert// 如果这个字段的值是null就不会加入到insert语句当中.
@Data
@Entity
@Table(name = "e_user_question_record")
public class UserQuestionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "student_number")
    private String studentNumber;
    private String openid;
    private String subject;
    @Column(name = "question_text_content")
    private String questionTextContent;
    @Column(name = "user_answer")
    private String userAnswer;
    @Column(name = "do_right")
    private int doRight;
    @Column(name = "question_id")
    private int questionId;

    private int times;
    @Column(name = "exam_paper_id")
    private int examPaperId;
    @Column(name = "customer_score")
    private String customerScore;
    @Column(name = "question_score")
    private String questionScore;
    @Column(name = "do_time")
    private String doTime;
    private int status;
    //解决返回给前端的是一个时间戳，改成为年月日时分秒格式
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Timestamp inserttime;
    //解决返回给前端的是一个时间戳，改成为年月日时分秒格式
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Timestamp updatetime;

}
