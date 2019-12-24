package com.zgczx.repository.mysql1.exam.dao;

import com.zgczx.repository.mysql1.exam.model.UserPaperRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author aml
 * @date 2019/12/22 19:15
 */
@Repository
public interface UserPaperRecordDao extends JpaRepository<UserPaperRecord, Integer> {

    //1. 获取此用户是否 存在 此套试卷的 记录 按记录的次数 降序返回
    @Query(value = "SELECT * FROM e_user_paper_record WHERE student_number=?1 AND subject=?2 AND exam_paper_id=?3 ORDER BY times DESC", nativeQuery = true)
    List<UserPaperRecord> getByStudentNumberAndSubjectAndExamPaperId(String stuNumber,String subjectName,int examPaperId);

    //2. 获取此用户的 本次试卷的保存信息，按照times降序获取
  //  List<UserPaperRecord> getByStudentNumberAndSubjectAndExamPaperId(String stu)




}
