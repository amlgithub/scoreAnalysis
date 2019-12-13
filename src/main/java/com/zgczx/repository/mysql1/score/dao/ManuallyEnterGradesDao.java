package com.zgczx.repository.mysql1.score.dao;

import com.zgczx.repository.mysql1.score.model.ManuallyEnterGrades;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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

    @Query(value = "SELECT COUNT(DISTINCT exam_name) FROM manually_enter_grades WHERE wechat_openid =?1 AND exam_name LIKE ?2", nativeQuery = true)
    int countByWechatOpenidAndExamName(String openid, String examName);

    @Query(value = "SELECT DISTINCT exam_name FROM manually_enter_grades WHERE wechat_openid =?1 AND exam_name LIKE ?2", nativeQuery = true)
    List<String> getExamNameByYearMonthAndWechatOpenid(String openid, String yearMonth);

    List<ManuallyEnterGrades> findAllByWechatOpenidAndExamName(String openid,String examName);
    // 使用in来代替 openid or openid 。。。
    List<ManuallyEnterGrades> findByWechatOpenidIn(List<String> openids);

    //动态太拼接openid 和 其他的条件
    @Query(value = "SELECT * FROM manually_enter_grades WHERE student_number=?2 AND wechat_openid IN (?1)", nativeQuery = true)
    List<ManuallyEnterGrades> findByWechatOpenidInAndStudentNumber(List<String> openids,String studengNumber);

    // 手动录入成绩，删除 某条 数据功能，根据openid和examName
    int deleteByStudentNumberAndExamNameAndSubjectName(String studentNumber, String examname,String subject);

//    @Modifying :只能用于返回值为 void int
//    @Query(name = "", nativeQuery = true)
//    ManuallyEnterGrades updateByWechatOpenidAndExamName(String openid, String examname);

    // 获取录入的某条数据，根据openid、studentnumber 、examName
    @Query(value = "SELECT * FROM manually_enter_grades WHERE wechat_openid=?1 AND student_number=?2 AND exam_name=?3 ", nativeQuery = true)
    ManuallyEnterGrades findByWechatOpenidAndStudentNumberAndExamName(String openid,String studentNumber,String examName);

    // 批量录入手动录入成绩实体时，判断如果其中有某条数据 已经录入过，就不允许此次录入操作
    ManuallyEnterGrades findAllByStudentNumberAndExamNameAndSubjectName(String studentNumber, String examname, String subjectName);


}
