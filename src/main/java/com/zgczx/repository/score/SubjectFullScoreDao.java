package com.zgczx.repository.score;

import com.zgczx.dataobject.score.SubjectFullScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

/**
 * @author aml
 * @date 2019/9/19 16:19
 */
@Repository
public interface SubjectFullScoreDao extends JpaRepository<SubjectFullScore, Integer> {

    @Query(value = "SELECT * FROM subject_full_score sfs,exam_full_score_set sfss WHERE sfs.id=sfss.subject_schame_id AND sfss.examinfo_id=?1 ", nativeQuery = true)
    SubjectFullScore findById(int id);
}
