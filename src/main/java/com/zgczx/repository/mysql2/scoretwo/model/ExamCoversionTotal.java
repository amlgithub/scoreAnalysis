package com.zgczx.repository.mysql2.scoretwo.model;

import lombok.Data;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;
import javax.persistence.Entity;

/**
 * @author aml
 * @date 2019/9/10 15:38
 */
@Component
@Entity
@Data
//@Table(name = "exam_coversion_total", schema = "score_ananlysis_wechat", catalog = "")
@Table(name = "exam_coversion_total", schema = "score_ananlysis_wechat", catalog = "")
public class ExamCoversionTotal {
    private long id;
    private long examId;
    private String studentMachineCard;
    private String studentNumber;
    private String studentName;
    private String classId;
    private String gradeName;
    private Timestamp examDate;
    private String examType;
    private Double yuwenScore;
    private Double shuxueScore;
    private Double yingyuScore;
    private Double wuliCoversion;
    private Double huaxueCoversion;
    private Double shengwuCoversion;
    private Double lishiCoversion;
    private Double diliCoversion;
    private Double zhengzhiCoversion;
    private Double coversionTotal;
    private Double coversionAvg;
    private Integer schoolIndex;
    private Integer classIndex;
    //    @Basic
//    @Column(name = "openid")
//    private String openid;
//    @Basic
//    @Column(name = "data_source")
//    private Integer dataSource;
//    @Basic
//    @Column(name = "valid")
//    private Integer valid;
    private String openid;
    private Integer dataSource;
    private Integer valid;
    private String schoolName;

    @Id
    @Column(name = "id")
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "exam_id")
    public long getExamId() {
        return examId;
    }

    public void setExamId(long examId) {
        this.examId = examId;
    }

    @Basic
    @Column(name = "student_machine_card")
    public String getStudentMachineCard() {
        return studentMachineCard;
    }

    public void setStudentMachineCard(String studentMachineCard) {
        this.studentMachineCard = studentMachineCard;
    }

    @Basic
    @Column(name = "student_number")
    public String getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }

    @Basic
    @Column(name = "student_name")
    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    @Basic
    @Column(name = "class_id")
    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    @Basic
    @Column(name = "grade_name")
    public String getGradeName() {
        return gradeName;
    }

    public void setGradeName(String gradeName) {
        this.gradeName = gradeName;
    }

    @Basic
    @Column(name = "exam_date")
    public Timestamp getExamDate() {
        return examDate;
    }

    public void setExamDate(Timestamp examDate) {
        this.examDate = examDate;
    }

    @Basic
    @Column(name = "exam_type")
    public String getExamType() {
        return examType;
    }

    public void setExamType(String examType) {
        this.examType = examType;
    }

    @Basic
    @Column(name = "yuwen_score")
    public Double getYuwenScore() {
        return yuwenScore;
    }

    public void setYuwenScore(Double yuwenScore) {
        this.yuwenScore = yuwenScore;
    }

    @Basic
    @Column(name = "shuxue_score")
    public Double getShuxueScore() {
        return shuxueScore;
    }

    public void setShuxueScore(Double shuxueScore) {
        this.shuxueScore = shuxueScore;
    }

    @Basic
    @Column(name = "yingyu_score")
    public Double getYingyuScore() {
        return yingyuScore;
    }

    public void setYingyuScore(Double yingyuScore) {
        this.yingyuScore = yingyuScore;
    }

    @Basic
    @Column(name = "wuli_coversion")
    public Double getWuliCoversion() {
        return wuliCoversion;
    }

    public void setWuliCoversion(Double wuliCoversion) {
        this.wuliCoversion = wuliCoversion;
    }

    @Basic
    @Column(name = "huaxue_coversion")
    public Double getHuaxueCoversion() {
        return huaxueCoversion;
    }

    public void setHuaxueCoversion(Double huaxueCoversion) {
        this.huaxueCoversion = huaxueCoversion;
    }

    @Basic
    @Column(name = "shengwu_coversion")
    public Double getShengwuCoversion() {
        return shengwuCoversion;
    }

    public void setShengwuCoversion(Double shengwuCoversion) {
        this.shengwuCoversion = shengwuCoversion;
    }

    @Basic
    @Column(name = "lishi_coversion")
    public Double getLishiCoversion() {
        return lishiCoversion;
    }

    public void setLishiCoversion(Double lishiCoversion) {
        this.lishiCoversion = lishiCoversion;
    }

    @Basic
    @Column(name = "dili_coversion")
    public Double getDiliCoversion() {
        return diliCoversion;
    }

    public void setDiliCoversion(Double diliCoversion) {
        this.diliCoversion = diliCoversion;
    }

    @Basic
    @Column(name = "zhengzhi_coversion")
    public Double getZhengzhiCoversion() {
        return zhengzhiCoversion;
    }

    public void setZhengzhiCoversion(Double zhengzhiCoversion) {
        this.zhengzhiCoversion = zhengzhiCoversion;
    }

    @Basic
    @Column(name = "coversion_total")
    public Double getCoversionTotal() {
        return coversionTotal;
    }

    public void setCoversionTotal(Double coversionTotal) {
        this.coversionTotal = coversionTotal;
    }

    @Basic
    @Column(name = "coversion_avg")
    public Double getCoversionAvg() {
        return coversionAvg;
    }

    public void setCoversionAvg(Double coversionAvg) {
        this.coversionAvg = coversionAvg;
    }

//    @Basic
//    @Column(name = "data_source")
//    public Integer getDataSource() {
//        return dataSource;
//    }
//
//    public void setDataSource(Integer dataSource) {
//        this.dataSource = dataSource;
//    }

    @Basic
    @Column(name = "school_index")
    public Integer getSchoolIndex() {
        return schoolIndex;
    }

    public void setSchoolIndex(Integer schoolIndex) {
        this.schoolIndex = schoolIndex;
    }

    @Basic
    @Column(name = "class_index")
    public Integer getClassIndex() {
        return classIndex;
    }

    public void setClassIndex(Integer classIndex) {
        this.classIndex = classIndex;
    }

    @Basic
    @Column(name = "openid")
    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    @Basic
    @Column(name = "data_source")
    public Integer getDataSource() {
        return dataSource;
    }

    public void setDataSource(Integer dataSource) {
        this.dataSource = dataSource;
    }

    @Basic
    @Column(name = "valid")
    public Integer getValid() {
        return valid;
    }

    public void setValid(Integer valid) {
        this.valid = valid;
    }

    @Basic
    @Column(name = "school_name")
    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExamCoversionTotal that = (ExamCoversionTotal) o;
        return Objects.equals(schoolName, that.schoolName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(schoolName);
    }
}
