package com.zgczx.dataobject.score;

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
@Table(name = "manually_enter_grades", schema = "score_ananlysis_dev", catalog = "")
public class ManuallyEnterGrades {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String wechatOpenid;
    private String studentNumber;
    private String subjectName;
    private String score;
    private String classRank;
    private String gradeRank;
    private String examName;
    private Timestamp inserttime;
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
