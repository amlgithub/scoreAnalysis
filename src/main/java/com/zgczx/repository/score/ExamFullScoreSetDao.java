package com.zgczx.repository.score;

import com.zgczx.dataobject.score.ExamFullScoreSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

/**
 * @author aml
 * @date 2019/9/19 16:12
 */
@Repository
public interface ExamFullScoreSetDao extends JpaRepository<ExamFullScoreSet, Integer> {

    @Query(value = "select e from ExamFullScoreSet as e where e.examinfoId = ?1")
    ExamFullScoreSet findByExaminfoId(int examid);
}
