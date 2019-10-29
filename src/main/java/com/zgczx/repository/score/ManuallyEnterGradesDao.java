package com.zgczx.repository.score;

import com.zgczx.dataobject.score.ManuallyEnterGrades;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author aml
 * @date 2019/10/29 19:15
 */
@Repository
public interface ManuallyEnterGradesDao extends JpaRepository<ManuallyEnterGrades, Integer> {


}
