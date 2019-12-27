package com.zgczx.repository.mysql1.exam.dao;

import com.zgczx.repository.mysql1.exam.model.UserCollect;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * 用户收藏 表
 * @author aml
 * @date 2019/12/17 16:52
 */
@Repository
public interface UserCollectDao extends JpaRepository<UserCollect, Integer> {

    //1. 获取此用户 -> 此科目 -> 此试卷 -> 此题 是否收藏
    @Query(value = "SELECT * FROM e_user_collect WHERE student_number=?1 AND SUBJECT=?2 AND exam_paper_id=?3 AND question_id=?4 AND valid=?5", nativeQuery = true)
    UserCollect getByStudentNumberAndSubjectAndExamPaperIdAndQuestionId(String stuNumber,String subject,int paperId,int questionId,int valid);

    //2. 查询此题是否收藏
    UserCollect findByQuestionIdAndValid(int id, int valid);

}
