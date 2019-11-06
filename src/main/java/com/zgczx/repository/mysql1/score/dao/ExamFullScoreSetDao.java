package com.zgczx.repository.mysql1.score.dao;

import com.zgczx.repository.mysql1.score.model.ExamFullScoreSet;
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

    @Query(value = "SELECT SUM(sfs.yuwen+sfs.shuxue+sfs.yingyu+sfs.wuli+sfs.huaxue+sfs.shengwu+sfs.zhengzhi+sfs.lishi+sfs.dili) FROM subject_full_score AS sfs WHERE id=(SELECT subject_schame_id FROM exam_full_score_set WHERE examinfo_id=?1)",nativeQuery = true)
    BigInteger getSchameTotal(int examId);
}
