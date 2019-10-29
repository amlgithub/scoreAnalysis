package com.zgczx.repository.score;

import com.zgczx.dataobject.score.ExamInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author aml
 * @date 2019/9/10 15:53
 */
@Repository
public interface ExamInfoDao extends JpaRepository<ExamInfo, Integer> {

    /**
     * 根据具体的 哪次考试获取 此id
     * @param examType 具体考试名称
     * @return id
     */
    @Query(value = "select e.id from ExamInfo e where e.examName = ?1")
    int findByExamName(String examType);
   // @Query(value = "select * from exam_info where exam_name=?1",nativeQuery = true)
    ExamInfo getByExamName(String examType);

    @Query(value = "SELECT exam_name FROM exam_info ", nativeQuery = true)
    List<String>getAllExamName();
}
