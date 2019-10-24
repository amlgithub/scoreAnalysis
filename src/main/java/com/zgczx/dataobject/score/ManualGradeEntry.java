package com.zgczx.dataobject.score;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

/**
 * @author aml
 * @date 2019/10/14 13:59
 */
@Entity
@Table(name = "manual_grade_entry", schema = "score_ananlysis_dev", catalog = "")
public class ManualGradeEntry {
    private int id;
    private String wechatOpenid;
    private String studentNumber;
    private String yuwenScore;
    private String shuxueScore;
    private String yingyuSocre;
    private String wuliScore;
    private String huaxueScore;
    private String shengwuScore;
    private String zhengzhiScore;
    private String lishiScore;
    private String diliScore;
    private String totalScore;
    private String classRank;
    private String gradeRank;
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
    public String getYuwenScore() {
        return yuwenScore;
    }

    public void setYuwenScore(String yuwenScore) {
        this.yuwenScore = yuwenScore;
    }

    @Basic
    @Column(name = "shuxue_score")
    public String getShuxueScore() {
        return shuxueScore;
    }

    public void setShuxueScore(String shuxueScore) {
        this.shuxueScore = shuxueScore;
    }

    @Basic
    @Column(name = "yingyu_socre")
    public String getYingyuSocre() {
        return yingyuSocre;
    }

    public void setYingyuSocre(String yingyuSocre) {
        this.yingyuSocre = yingyuSocre;
    }

    @Basic
    @Column(name = "wuli_score")
    public String getWuliScore() {
        return wuliScore;
    }

    public void setWuliScore(String wuliScore) {
        this.wuliScore = wuliScore;
    }

    @Basic
    @Column(name = "huaxue_score")
    public String getHuaxueScore() {
        return huaxueScore;
    }

    public void setHuaxueScore(String huaxueScore) {
        this.huaxueScore = huaxueScore;
    }

    @Basic
    @Column(name = "shengwu_score")
    public String getShengwuScore() {
        return shengwuScore;
    }

    public void setShengwuScore(String shengwuScore) {
        this.shengwuScore = shengwuScore;
    }

    @Basic
    @Column(name = "zhengzhi_score")
    public String getZhengzhiScore() {
        return zhengzhiScore;
    }

    public void setZhengzhiScore(String zhengzhiScore) {
        this.zhengzhiScore = zhengzhiScore;
    }

    @Basic
    @Column(name = "lishi_score")
    public String getLishiScore() {
        return lishiScore;
    }

    public void setLishiScore(String lishiScore) {
        this.lishiScore = lishiScore;
    }

    @Basic
    @Column(name = "dili_score")
    public String getDiliScore() {
        return diliScore;
    }

    public void setDiliScore(String diliScore) {
        this.diliScore = diliScore;
    }

    @Basic
    @Column(name = "total_score")
    public String getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(String totalScore) {
        this.totalScore = totalScore;
    }

    @Basic
    @Column(name = "class_rank")
    public String getClassRank() {
        return classRank;
    }

    public void setClassRank(String classRank) {
        this.classRank = classRank;
    }

    @Basic
    @Column(name = "grade_rank")
    public String getGradeRank() {
        return gradeRank;
    }

    public void setGradeRank(String gradeRank) {
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
        ManualGradeEntry that = (ManualGradeEntry) o;
        return id == that.id &&
                Objects.equals(wechatOpenid, that.wechatOpenid) &&
                Objects.equals(studentNumber, that.studentNumber) &&
                Objects.equals(yuwenScore, that.yuwenScore) &&
                Objects.equals(shuxueScore, that.shuxueScore) &&
                Objects.equals(yingyuSocre, that.yingyuSocre) &&
                Objects.equals(wuliScore, that.wuliScore) &&
                Objects.equals(huaxueScore, that.huaxueScore) &&
                Objects.equals(shengwuScore, that.shengwuScore) &&
                Objects.equals(zhengzhiScore, that.zhengzhiScore) &&
                Objects.equals(lishiScore, that.lishiScore) &&
                Objects.equals(diliScore, that.diliScore) &&
                Objects.equals(totalScore, that.totalScore) &&
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
        return Objects.hash(id, wechatOpenid, studentNumber, yuwenScore, shuxueScore, yingyuSocre, wuliScore, huaxueScore, shengwuScore, zhengzhiScore, lishiScore, diliScore, totalScore, classRank, gradeRank, examName, schoolName, gradeName, className, examFullName, insettime, updatetime);
    }
}
