package com.zgczx.repository.mysql1.exam.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

/**
 * @author aml
 * @date 2019/12/24 11:11
 */
@Data
@Entity
public class EchoPaperDTO {

    @Id
    private int id;
    @Column(name = "exam_id")
    private int examId;
    @Column(name = "question_source")
    private String questionSource;
    @Column(name = "exam_name")
    private String examName;
    @Column(name = "exam_type")
    private String examType;
    @Column(name = "exam_location")
    private String examLocation;
    @Column(name = "question_id")
    private int questionId;
    @Column(name = "question_type")
    private String questionType;
    @Column(name = "question_difficult")
    private String questionDifficult;
    @Column(name = "question_context")
    private String questionContext;
    @Column(name = "question_option")
    private String questionOption;
    @Column(name = "question_score")
    private String questionScore;
    @Column(name = "question_attribute")
    private String questionAttribute;
    @Column(name = "correct_option")
    private String correctOption;
    @Column(name = "correct_text")
    private String correctText;
    @Column(name = "correct_analysis")
    private String correctAnalysis;
    @Column(name = "chapter_id")
    private int chapterId;

    private String level;
    @Column(name = "level_name")
    private String levelName;
    @Column(name = "create_user")
    private String createUser;
    @Column(name = "knowledge_module")
    private String knowledgeModule;
    @Column(name = "cognitive_level")
    private String cognitiveLevel;
    @Column(name = "core_literacy")
    private String coreLiteracy;

    private int valid;
    @Column(name = "question_imgs")
    private String questionImgs;

    //解决返回给前端的是一个时间戳，改成为年月日时分秒格式
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Timestamp inserttime;
    //解决返回给前端的是一个时间戳，改成为年月日时分秒格式
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Timestamp updatetime;

    private String randomOption;

    private String rightOption;
    private String sourcePaperId;
    private int collect;


}
