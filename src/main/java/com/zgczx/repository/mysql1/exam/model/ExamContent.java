package com.zgczx.repository.mysql1.exam.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * 题库中的 试卷内容表，存放学校提供的每一次的考试数据
 * @author aml
 * @date 2019/12/11 12:59
 */
@DynamicUpdate//生成动态的update语句,如果这个字段的值是null就不会被加入到update语句中
@DynamicInsert// 如果这个字段的值是null就不会加入到insert语句当中.
@Data
@Entity
@Table(name = "e_exam_content")
public class ExamContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String subject;

    @Column(name = "exam_name")
    private String examName;

    private String content;

    private String answer;

    //解决返回给前端的是一个时间戳，改成为年月日时分秒格式
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Timestamp inserttime;
    //解决返回给前端的是一个时间戳，改成为年月日时分秒格式
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Timestamp updatetime;

}
