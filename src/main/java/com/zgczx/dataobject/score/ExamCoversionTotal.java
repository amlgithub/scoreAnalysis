package com.zgczx.dataobject.score;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

/**
 * @author aml
 * @date 2019/9/10 15:38
 */
@Entity
@Data
@Table(name = "exam_coversion_total", schema = "score_ananlysis_dev", catalog = "")
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExamCoversionTotal that = (ExamCoversionTotal) o;
        return id == that.id &&
                examId == that.examId &&
                Objects.equals(studentMachineCard, that.studentMachineCard) &&
                Objects.equals(studentNumber, that.studentNumber) &&
                Objects.equals(studentName, that.studentName) &&
                Objects.equals(classId, that.classId) &&
                Objects.equals(gradeName, that.gradeName) &&
                Objects.equals(examDate, that.examDate) &&
                Objects.equals(examType, that.examType) &&
                Objects.equals(yuwenScore, that.yuwenScore) &&
                Objects.equals(shuxueScore, that.shuxueScore) &&
                Objects.equals(yingyuScore, that.yingyuScore) &&
                Objects.equals(wuliCoversion, that.wuliCoversion) &&
                Objects.equals(huaxueCoversion, that.huaxueCoversion) &&
                Objects.equals(shengwuCoversion, that.shengwuCoversion) &&
                Objects.equals(lishiCoversion, that.lishiCoversion) &&
                Objects.equals(diliCoversion, that.diliCoversion) &&
                Objects.equals(zhengzhiCoversion, that.zhengzhiCoversion) &&
                Objects.equals(coversionTotal, that.coversionTotal) &&
                Objects.equals(coversionAvg, that.coversionAvg) &&
                Objects.equals(schoolIndex, that.schoolIndex) &&
                Objects.equals(classIndex, that.classIndex);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, examId, studentMachineCard, studentNumber, studentName, classId, gradeName, examDate, examType, yuwenScore, shuxueScore, yingyuScore, wuliCoversion, huaxueCoversion, shengwuCoversion, lishiCoversion, diliCoversion, zhengzhiCoversion, coversionTotal, coversionAvg, schoolIndex, classIndex);
    }
}
