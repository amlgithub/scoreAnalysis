package com.zgczx.repository.mysql1.exam.dao;

import com.zgczx.repository.mysql1.exam.model.UserCollect;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Map;

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
    //1.2 获取此用户、此科目、此题是否收藏
    UserCollect getByStudentNumberAndSubjectAndQuestionId(String stuNumber,String subject,int questionId);
    //2. 查询此题是否收藏
    UserCollect findByQuestionIdAndValid(int id, int valid);

    // 3. 统计用户收藏总题数    lxj
    @Query(value="select count(distinct question_id) from e_user_collect where student_number=?1 and subject=?2 and valid=1 ",nativeQuery = true)
    int getCollectProblemsNum(String stuNumber,String subject);

    // 3. 按年级统计用户收藏总题数    lxj
    @Query(value = "select count(distinct question_id) from e_user_collect as euc,e_exam_paper as eep where eep.grade_level=?2 and eep.id=euc.exam_paper_id \n" +
            "and euc.student_number=?1 and euc.subject=?3 and euc.valid=1 ",nativeQuery = true)
    int getCollectCountByGradeLevel(String stuNumber,String levelName,String subject);

    // 4. 统计用户收藏练习题数  lxj
    @Query(value = "select count(distinct euc.question_id) from e_user_collect euc,e_question eq where euc.student_number=?1 and euc.`subject`=?2 and euc.valid=1 and euc.question_id=eq.id and eq.question_source='章节练习' ",nativeQuery = true)
    int getCollectChapterProblemsNum(String stuNumber,String subject);

    // 5. 统计用户收藏考试题数  lxj
    @Query(value="select count(distinct euc.question_id) from e_user_collect euc,e_question eq where euc.student_number=?1 and euc.`subject`=?2 and euc.valid=1 and euc.question_id=eq.id and (eq.question_source='模拟考试' or eq.question_source='历年真题') ",nativeQuery = true)
    int getCollectExamProblemsNum(String stuNumber,String subject);

    // 6. 统计用户每一章收藏的题数  lxj
    @Query(value = "select count(distinct euc.question_id) from e_exam_paper as eep,e_user_collect as euc,e_chapter as ec where eep.chapter_id=ec.id and ec.chapter=?3 and eep.id=euc.exam_paper_id and euc.valid=1 and euc.student_number=?1 and euc.subject=?2 ",nativeQuery = true)
    int getCollectProblemsByChapter(String stuNumber, String subject,String chapter);

    // 7. 统计用户每次考试收藏的题数  lxj
    @Query(value = "select count(distinct euc.question_id) from e_exam_paper as eep,e_user_collect as euc where euc.exam_paper_id=eep.id \n" +
            "and exam_name=?3 and euc.valid=1 and euc.student_number=?1 and euc.subject=?2 ",nativeQuery = true)
    int getCollectProblemsByExam(String stuNumber,String subject,String examName);

	// 8. 根据节的名称获取对应的收藏题数  lxj
    @Query(value = "select count(distinct euc.question_id) from e_exam_paper as eep,e_user_collect as euc,e_chapter as ec \n" +
            "where eep.chapter_id=ec.id and ec.section=?3 \n" +
            "and eep.id=euc.exam_paper_id and euc.valid=1 and euc.student_number=?1 and euc.subject=?2 ",nativeQuery = true)
    int getCollectProblemsBySection(String stuNumber,String subject,String section);
	// 9. 查询此用户此题是否插入收藏库表中 aml
    UserCollect findByStudentNumberAndQuestionId(String sutNumber,int questionId);

    // 10. 查看某道题某个用户是否收藏了 lxj
    @Query(value = "select count(*) from e_user_collect where student_number=?1 and `subject`=?2 and valid=1 and question_id=?3 ",nativeQuery = true)
    int getIfCollectByStuNumAndQuestionId(String stuNumber,String subject,int questionId);

}
