package com.zgczx.repository.mysql1.score.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Objects;

/**
 * @author aml
 * @date 2019/9/22 16:28
 */
@Entity
@Data
@Table(name = "import_conversion_score", schema = "score_ananlysis_dev", catalog = "")
public class ImportConversionScore {
    private int id;
    private String studentMachineCard;
    private String username;
    private String yuwenScore;
    private String shuxueScore;
    private String yingyuScore;
    private String wuliConverscore;
    private String huaxueConverscore;
    private String shengwuConverscore;
    private String lishiConverscore;
    private String diliConverscore;
    private String zhengzhiConverscore;
    private String totalScore;
    private String classIndex;
    private String schoolIndex;
    private String examType;

    @Id
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
    @Column(name = "username")
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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
    @Column(name = "yingyu_score")
    public String getYingyuScore() {
        return yingyuScore;
    }

    public void setYingyuScore(String yingyuScore) {
        this.yingyuScore = yingyuScore;
    }

    @Basic
    @Column(name = "wuli_converscore")
    public String getWuliConverscore() {
        return wuliConverscore;
    }

    public void setWuliConverscore(String wuliConverscore) {
        this.wuliConverscore = wuliConverscore;
    }

    @Basic
    @Column(name = "huaxue_converscore")
    public String getHuaxueConverscore() {
        return huaxueConverscore;
    }

    public void setHuaxueConverscore(String huaxueConverscore) {
        this.huaxueConverscore = huaxueConverscore;
    }

    @Basic
    @Column(name = "shengwu_converscore")
    public String getShengwuConverscore() {
        return shengwuConverscore;
    }

    public void setShengwuConverscore(String shengwuConverscore) {
        this.shengwuConverscore = shengwuConverscore;
    }

    @Basic
    @Column(name = "lishi_converscore")
    public String getLishiConverscore() {
        return lishiConverscore;
    }

    public void setLishiConverscore(String lishiConverscore) {
        this.lishiConverscore = lishiConverscore;
    }

    @Basic
    @Column(name = "dili_converscore")
    public String getDiliConverscore() {
        return diliConverscore;
    }

    public void setDiliConverscore(String diliConverscore) {
        this.diliConverscore = diliConverscore;
    }

    @Basic
    @Column(name = "zhengzhi_converscore")
    public String getZhengzhiConverscore() {
        return zhengzhiConverscore;
    }

    public void setZhengzhiConverscore(String zhengzhiConverscore) {
        this.zhengzhiConverscore = zhengzhiConverscore;
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
    @Column(name = "class_index")
    public String getClassIndex() {
        return classIndex;
    }

    public void setClassIndex(String classIndex) {
        this.classIndex = classIndex;
    }

    @Basic
    @Column(name = "school_index")
    public String getSchoolIndex() {
        return schoolIndex;
    }

    public void setSchoolIndex(String schoolIndex) {
        this.schoolIndex = schoolIndex;
    }

    @Basic
    @Column(name = "exam_type")
    public String getExamType() {
        return examType;
    }

    public void setExamType(String examType) {
        this.examType = examType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImportConversionScore that = (ImportConversionScore) o;
        return id == that.id &&
                Objects.equals(studentMachineCard, that.studentMachineCard) &&
                Objects.equals(username, that.username) &&
                Objects.equals(yuwenScore, that.yuwenScore) &&
                Objects.equals(shuxueScore, that.shuxueScore) &&
                Objects.equals(yingyuScore, that.yingyuScore) &&
                Objects.equals(wuliConverscore, that.wuliConverscore) &&
                Objects.equals(huaxueConverscore, that.huaxueConverscore) &&
                Objects.equals(shengwuConverscore, that.shengwuConverscore) &&
                Objects.equals(lishiConverscore, that.lishiConverscore) &&
                Objects.equals(diliConverscore, that.diliConverscore) &&
                Objects.equals(zhengzhiConverscore, that.zhengzhiConverscore) &&
                Objects.equals(totalScore, that.totalScore) &&
                Objects.equals(classIndex, that.classIndex) &&
                Objects.equals(schoolIndex, that.schoolIndex) &&
                Objects.equals(examType, that.examType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, studentMachineCard, username, yuwenScore, shuxueScore, yingyuScore, wuliConverscore, huaxueConverscore, shengwuConverscore, lishiConverscore, diliConverscore, zhengzhiConverscore, totalScore, classIndex, schoolIndex, examType);
    }
}
