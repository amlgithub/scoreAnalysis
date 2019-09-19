package com.zgczx.repository.score;

import com.zgczx.dataobject.score.SubjectFullScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

/**
 * @author aml
 * @date 2019/9/19 16:19
 */
@Repository
public interface SubjectFullScoreDao extends JpaRepository<SubjectFullScore, Integer> {
}
