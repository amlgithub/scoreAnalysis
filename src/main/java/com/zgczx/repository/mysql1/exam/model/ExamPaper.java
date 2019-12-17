package com.zgczx.repository.mysql1.exam.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * 题库中的 试卷表，存放每一次试卷
 * @author aml
 * @date 2019/12/11 14:15
 */
@DynamicUpdate//生成动态的update语句,如果这个字段的值是null就不会被加入到update语句中
@DynamicInsert// 如果这个字段的值是null就不会加入到insert语句当中.
@Data
@Entity
@Table(name = "e_exam_paper")
public class ExamPaper {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "exam_name")
    private String examName;
    @Column(name = "exam_source")
    private String examSource;
    @Column(name = "exam_type")
    private String examType;
    @Column(name = "exam_location")
    private String examLocation;
    private String subject;
    @Column(name = "subject_id")
    private int subjectId;
    @Column(name = "chapter_id")
    private int chapterId;
    @Column(name = "paper_type")
    private String paperType;
    @Column(name = "exam_score")
    private String examScore;
    @Column(name = "question_count")
    private int questionCount;
    @Column(name = "suggest_time")
    private String suggestTime;
    @Column(name = "limit_star_time")
    private String limitStarTime;
    @Column(name = "limit_end_time")
    private String limitEndTime;
    @Column(name = "exam_content_id")
    private int examContentId;
    @Column(name = "exam_content")
    private String examContent;

    @Column(name = "question_list")
    private String questionList;
    @Column(name = "create_user")
    private String createUser;
    private int valid;
    private String rank;
    //解决返回给前端的是一个时间戳，改成为年月日时分秒格式
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Timestamp inserttime;
    //解决返回给前端的是一个时间戳，改成为年月日时分秒格式
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Timestamp updatetime;


}
