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

    @Query(value = "SELECT DISTINCT LEFT(exam_name, 8) FROM manually_enter_grades WHERE wechat_openid =?1 AND exam_name LIKE ?2 ", nativeQuery = true)
    List<String> getExamNameByWechatOpenidAndYear(String openid, String year);

    @Query(value = "SELECT COUNT(exam_name) FROM manually_enter_grades WHERE wechat_openid =?1 AND exam_name LIKE ?2", nativeQuery = true)
    int countByWechatOpenidAndExamName(String openid, String examName);

    @Query(value = "SELECT exam_name FROM manually_enter_grades WHERE wechat_openid =?1 AND exam_name LIKE ?2", nativeQuery = true)
    List<String> getExamNameByYearMonthAndWechatOpenid(String openid, String yearMonth);

    List<ManuallyEnterGrades> findAllByWechatOpenidAndExamName(String openid,String examName);
    // 使用in来代替 openid or openid 。。。
    List<ManuallyEnterGrades> findByWechatOpenidIn(List<String> openids);

    //动态太拼接openid 和 其他的条件
    @Query(value = "SELECT * FROM manually_enter_grades WHERE student_number=?2 AND wechat_openid IN (?1)", nativeQuery = true)
    List<ManuallyEnterGrades> findByWechatOpenidInAndStudentNumber(List<String> openids,String studengNumber);
}
