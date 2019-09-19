package com.zgczx.dataobject.score;

import javax.persistence.*;
import java.util.Objects;

/**
 * @author aml
 * @date 2019/9/19 15:21
 */
@Entity
@Table(name = "exam_full_score_set", schema = "score_ananlysis_dev", catalog = "")
public class ExamFullScoreSet {
    private long id;
    private Long examinfoId;
    private Long subjectSchameId;

    @Id
    @Column(name = "id")
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "examinfo_id")
    public Long getExaminfoId() {
        return examinfoId;
    }

    public void setExaminfoId(Long examinfoId) {
        this.examinfoId = examinfoId;
    }

    @Basic
    @Column(name = "subject_schame_id")
    public Long getSubjectSchameId() {
        return subjectSchameId;
    }

    public void setSubjectSchameId(Long subjectSchameId) {
        this.subjectSchameId = subjectSchameId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExamFullScoreSet that = (ExamFullScoreSet) o;
        return id == that.id &&
                Objects.equals(examinfoId, that.examinfoId) &&
                Objects.equals(subjectSchameId, that.subjectSchameId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, examinfoId, subjectSchameId);
    }
}
