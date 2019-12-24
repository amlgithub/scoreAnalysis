package com.zgczx.repository.mysql1.exam.dao;

import com.zgczx.repository.mysql1.exam.model.UserQuestionRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 用户做题记录表
 * @author aml
 * @date 2019/12/16 15:11
 */
@Repository
public interface UserQuestionRecordDao extends JpaRepository<UserQuestionRecord, Integer> {

    //1. 获取某学生->某科目 -> 某试卷的所有做题记录； 可以统计做对数量和做错数量
    List<UserQuestionRecord> getByStudentNumberAndSubjectAndExamPaperIdAndTimes(String studentNumber,String subject,int paperId,int times);

    //2. 获取这份试卷中这道题，此用户是否做过,按做过次数的降序排列
    @Query(value = "SELECT * FROM e_user_question_record WHERE student_number=?1 AND exam_paper_id=?2 AND question_id=?3 ORDER BY times DESC", nativeQuery = true)
    List<UserQuestionRecord> getByStudentNumberAndExamPaperIdAndQuestionId(String stuNumber,int sourcePaperId,int questionid);

    // 3. 获取 此用户回显的 做题记录
    @Query(value = "SELECT * FROM e_user_question_record  WHERE student_number=?1 AND SUBJECT=?2 AND exam_paper_id=?3 ORDER BY times DESC\n", nativeQuery = true)
    List<UserQuestionRecord> getByStudentNumberAndSubjectAndExamPaperId(String studentNumber,String subject,int sourcePaperId);

    //4. 获取此学生-》此科目-》所有错题的试卷id： exam_paper_id
    @Query(value = "SELECT DISTINCT exam_paper_id FROM e_user_question_record WHERE student_number=?1 AND SUBJECT=?2 ", nativeQuery = true)
    List<Integer> getAllExamPaperId(String stuNumber, String subject);

    //5. 获取此用户-》此科目-》章节练习中 所有错的 题号 和 试卷名称（每节的名称）
    @Query(value = "SELECT DISTINCT question_id,exam_paper_name FROM e_user_question_record WHERE student_number=?1 AND SUBJECT=?2 AND do_right=?3 AND exam_category=?3 ", nativeQuery = true)
    List<String> getAllErrInfo(String stuNumber,String subject,int doRight,String examCategory);

    //6. 获取此用户-》此科目-》章节练习-》所有试卷名称
    @Query(value = "SELECT DISTINCT exam_paper_name FROM e_user_question_record WHERE student_number=?1 AND SUBJECT=?2 AND exam_category=?3 ", nativeQuery = true)
    List<String> getAllExamPaperName(String stuNumber,String subject,String examCategory);

    //7. 获取此用户-》此科目-》章节练习中 所有错题的信息
    List<UserQuestionRecord> getByStudentNumberAndSubjectAndDoRightAndExamCategory(String stuNumber,String subject,int doRight,String examCategory);
}
