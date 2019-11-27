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

//    @Id
//    @Column(name = "id")
//    public int getId() {
//        return id;
//    }
//
//    public void setId(int id) {
//        this.id = id;
//    }
//
//    @Basic
//    @Column(name = "wechat_openid")
//    public String getWechatOpenid() {
//        return wechatOpenid;
//    }
//
//    public void setWechatOpenid(String wechatOpenid) {
//        this.wechatOpenid = wechatOpenid;
//    }
//
//    @Basic
//    @Column(name = "student_number")
//    public String getStudentNumber() {
//        return studentNumber;
//    }
//
//    public void setStudentNumber(String studentNumber) {
//        this.studentNumber = studentNumber;
//    }
//
//    @Basic
//    @Column(name = "subject_name")
//    public String getSubjectName() {
//        return subjectName;
//    }
//
//    public void setSubjectName(String subjectName) {
//        this.subjectName = subjectName;
//    }
//
//    @Basic
//    @Column(name = "score")
//    public String getScore() {
//        return score;
//    }
//
//    public void setScore(String score) {
//        this.score = score;
//    }
//
//    @Basic
//    @Column(name = "class_rank")
//    public String getClassRank() {
//        return classRank;
//    }
//
//    public void setClassRank(String classRank) {
//        this.classRank = classRank;
//    }
//
//    @Basic
//    @Column(name = "grade_rank")
//    public String getGradeRank() {
//        return gradeRank;
//    }
//
//    public void setGradeRank(String gradeRank) {
//        this.gradeRank = gradeRank;
//    }
//
//    @Basic
//    @Column(name = "exam_name")
//    public String getExamName() {
//        return examName;
//    }
//
//    public void setExamName(String examName) {
//        this.examName = examName;
//    }
//
//    @Basic
//    @Column(name = "inserttime")
//    public Timestamp getInserttime() {
//        return inserttime;
//    }
//
//    public void setInserttime(Timestamp inserttime) {
//        this.inserttime = inserttime;
//    }
//
//    @Basic
//    @Column(name = "updatetime")
//    public Timestamp getUpdatetime() {
//        return updatetime;
//    }
//
//    public void setUpdatetime(Timestamp updatetime) {
//        this.updatetime = updatetime;
//    }

}
