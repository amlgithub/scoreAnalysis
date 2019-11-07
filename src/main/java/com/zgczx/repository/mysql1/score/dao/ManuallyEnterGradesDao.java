package com.zgczx.repository.mysql1.score.dao;

import com.zgczx.repository.mysql1.score.model.ManuallyEnterGrades;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author aml
 * @date 2019/10/29 19:15
 */
@Repository
public interface ManuallyEnterGradesDao extends JpaRepository<ManuallyEnterGrades, Integer> {

    @Query(value = "SELECT exam_name FROM manually_enter_grades WHERE wechat_openid =?1 AND exam_name LIKE ?2 ", nativeQuery = true)
    List<String> getExamNameByWechatOpenidAndYear(String openid, String year);

    @Query(value = "SELECT exam_name FROM manually_enter_grades WHERE wechat_openid =?1 AND exam_name LIKE ?2", nativeQuery = true)
    List<String> getExamNameByYearMonthAndWechatOpenid(String openid, String yearMonth);

    List<ManuallyEnterGrades> findAllByWechatOpenidAndExamName(String openid,String examName);
}
