package com.zgczx.repository.mysql1.score.dao;

import com.zgczx.repository.mysql1.score.model.GoalSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author aml
 * @date 2019/11/29 15:45
 */
@Repository
public interface GoalSetDao extends JpaRepository<GoalSet, Integer> {

    @Query(value = "SELECT * FROM goal_set WHERE student_number=?1 AND exam_name=?2 ORDER BY id DESC", nativeQuery = true)
    List<GoalSet> findTargetValue(String studentNumber,String examName);
}
