package com.zgczx.dataobject.score;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

/**
 * @author aml
 * @date 2019/9/10 15:39
 */
@Entity
@Data
@Table(name = "exam_info", schema = "score_ananlysis_dev", catalog = "")
public class ExamInfo {
    private long id;
    private String examName;
    private String examGrade;
    private Timestamp examDate;
    private String paperId;

    @Id
    @Column(name = "id")
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
    @Column(name = "exam_grade")
    public String getExamGrade() {
        return examGrade;
    }

    public void setExamGrade(String examGrade) {
        this.examGrade = examGrade;
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
    @Column(name = "paper_id")
    public String getPaperId() {
        return paperId;
    }

    public void setPaperId(String paperId) {
        this.paperId = paperId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExamInfo examInfo = (ExamInfo) o;
        return id == examInfo.id &&
                Objects.equals(examName, examInfo.examName) &&
                Objects.equals(examGrade, examInfo.examGrade) &&
                Objects.equals(examDate, examInfo.examDate) &&
                Objects.equals(paperId, examInfo.paperId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, examName, examGrade, examDate, paperId);
    }
}
