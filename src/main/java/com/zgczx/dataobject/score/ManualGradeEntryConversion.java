package com.zgczx.dataobject.score;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

/**
 * 这个表是手动录入成绩转换为等级分后的总表，所有手动录入成绩的数据都从此表中获取
 * @author aml
 * @date 2019/10/14 13:59
 */
@Entity
@Table(name = "manual_grade_entry_conversion", schema = "score_ananlysis_dev", catalog = "")
public class ManualGradeEntryConversion {
    private int id;
    private String wechatOpenid;
    private String studentNumber;
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
    private Integer classRank;
    private Integer gradeRank;
    private String examName;
    private String schoolName;
    private String gradeName;
    private String className;
    private String examFullName;
    private Timestamp insettime;
    private Timestamp updatetime;

    @Id
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "wechat_openid")
    public String getWechatOpenid() {
        return wechatOpenid;
    }

    public void setWechatOpenid(String wechatOpenid) {
        this.wechatOpenid = wechatOpenid;
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
    @Column(name = "class_rank")
    public Integer getClassRank() {
        return classRank;
    }

    public void setClassRank(Integer classRank) {
        this.classRank = classRank;
    }

    @Basic
    @Column(name = "grade_rank")
    public Integer getGradeRank() {
        return gradeRank;
    }

    public void setGradeRank(Integer gradeRank) {
        this.gradeRank = gradeRank;
    }

    @Basic
    @Column(name = "exam_name")
    public String getExamName() {
        return examName;
    }

    public void setExamName(String examName) {
        this.examName = examName;
    }

    @Basic
    @Column(name = "school_name")
    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
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
    @Column(name = "class_name")
    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    @Basic
    @Column(name = "exam_full_name")
    public String getExamFullName() {
        return examFullName;
    }

    public void setExamFullName(String examFullName) {
        this.examFullName = examFullName;
    }

    @Basic
    @Column(name = "insettime")
    public Timestamp getInsettime() {
        return insettime;
    }

    public void setInsettime(Timestamp insettime) {
        this.insettime = insettime;
    }

    @Basic
    @Column(name = "updatetime")
    public Timestamp getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(Timestamp updatetime) {
        this.updatetime = updatetime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ManualGradeEntryConversion that = (ManualGradeEntryConversion) o;
        return id == that.id &&
                Objects.equals(wechatOpenid, that.wechatOpenid) &&
                Objects.equals(studentNumber, that.studentNumber) &&
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
                Objects.equals(classRank, that.classRank) &&
                Objects.equals(gradeRank, that.gradeRank) &&
                Objects.equals(examName, that.examName) &&
                Objects.equals(schoolName, that.schoolName) &&
                Objects.equals(gradeName, that.gradeName) &&
                Objects.equals(className, that.className) &&
                Objects.equals(examFullName, that.examFullName) &&
                Objects.equals(insettime, that.insettime) &&
                Objects.equals(updatetime, that.updatetime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, wechatOpenid, studentNumber, yuwenScore, shuxueScore, yingyuScore, wuliCoversion, huaxueCoversion, shengwuCoversion, lishiCoversion, diliCoversion, zhengzhiCoversion, coversionTotal, coversionAvg, classRank, gradeRank, examName, schoolName, gradeName, className, examFullName, insettime, updatetime);
    }
}
