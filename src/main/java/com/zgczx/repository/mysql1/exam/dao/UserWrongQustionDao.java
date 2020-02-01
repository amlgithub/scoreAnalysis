package com.zgczx.repository.mysql1.exam.dao;

import com.zgczx.repository.mysql1.exam.model.UserWrongQustion;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Update;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 用户错题的dao
 * @author aml
 * @date 2020/1/2 12:46
 */
@Repository
public interface UserWrongQustionDao extends JpaRepository<UserWrongQustion, Integer> {

    //1. 获取此来源的此题数据；由学号-科目-试卷来源（章节；模拟；历年真题）
    @Query(value = "SELECT * FROM e_user_wrong_qustion WHERE student_number=?1 AND exam_category=?2 AND question_id=?3 AND subject=?4 ", nativeQuery = true)
    UserWrongQustion getByStudentNumberAndExamCategoryAndQuestionId(String stuNumber, String examCategory, int questionid,String subject);

    // 2. 获取某个用户所有错题数  lxj
    @Query(value = "select count(distinct question_id) from e_user_wrong_qustion where `subject`=?2 and student_number=?1 ",nativeQuery = true)
    int getErrorProblemsNumber(String stuNumber,String subject);

//    // 3. 获取用户章节练习错题数(未掌握的)  lxj
//    @Query(value = "select count(distinct question_id) from e_user_wrong_qustion where `subject`=?2 and student_number=?1 and do_right=2 and exam_category='章节练习' ",nativeQuery = true)
//    int getChapterErrProblemsNum(String stuNumber,String subject);
//
//    // 4. 获取用户考试错题数(未掌握的)  lxj
//    @Query(value = "select count(distinct question_id) from e_user_wrong_qustion where `subject`=?2 and student_number=?1 and do_right=2 and (exam_category='模拟考试' or exam_category='历年真题') ",nativeQuery = true)
//    int getExamErrorProblemsNum(String stuNumber,String subject);

    // 5. 获取用户每一章的错题数(已掌握和未掌握，传参决定是否掌握) lxj
    @Query(value = "select count(distinct question_id) from e_user_wrong_qustion as euwq,e_exam_paper as eep,e_chapter as ec \n" +
            "where eep.chapter_id=ec.id and ec.chapter=?3 \n" +
            "and eep.exam_name=euwq.exam_paper_name and euwq.do_right=?4 and euwq.student_number=?1 and euwq.subject=?2 ",nativeQuery = true)
    int getErrorNumByChapter(String stuNumber, String subject,String chapter,int doRight);

    // 6. 统计用户每次考试错题数(已掌握和未掌握，传参决定是否掌握)  lxj
    @Query(value = "select count(distinct question_id) from e_exam_paper as eep,e_user_wrong_qustion as euwq where euwq.exam_paper_name=eep.exam_name \n" +
            "and exam_name=?3 and euwq.do_right=?4 and euwq.student_number=?1 and euwq.subject=?2 ",nativeQuery = true)
    int getErrorNumByExam(String stuNumber,String subject,String examName,int doRight);

    // 7. 获取此节的 所有错题数量(已掌握和未掌握，传参决定是否掌握) lxj
    @Query(value = "SELECT COUNT(DISTINCT question_id) FROM e_user_wrong_qustion WHERE student_number=?1 AND SUBJECT=?2 \n" +
            "AND exam_paper_name=?3 and do_right=?4 ",nativeQuery = true)
    int getByErrNumber(String stuNumber,String subject,String examPaperName,int doRight);

    // 8. 根据节的名称 获取错题的详细信息 lxj
    @Query(value = "select distinct question_id,euwq.id,euwq.student_number,euwq.openid,euwq.`subject`,euwq.do_right,euwq.user_answer,euwq.exam_paper_id,\n" +
            "euwq.exam_paper_name,euwq.exam_category,euwq.do_time,euwq.inserttime,euwq.updatetime from e_user_wrong_qustion as euwq,e_exam_paper as eep,e_chapter as ec \n" +
            "where ec.chapter=?3 and ec.section=?4 and ec.section=eep.exam_name and eep.exam_name=euwq.exam_paper_name and euwq.do_right=?5 and euwq.student_number=?1 \n" +
            "and euwq.subject=?2 ",nativeQuery = true)
    public List<UserWrongQustion> getErrorProblemsIdByChapterAndSection(String stuNumber,String subject,String chapter,String section,int doRight);

    // 9. 根据考试名称 获取本次考试错题id lxj
    @Query(value = "select distinct question_id,euwq.id,euwq.student_number,euwq.openid,euwq.`subject`,euwq.do_right,euwq.user_answer,euwq.exam_paper_id,\n" +
            "euwq.exam_paper_name,euwq.exam_category,euwq.do_time,euwq.inserttime,euwq.updatetime from e_user_wrong_qustion as euwq,e_exam_paper as eep  \n" +
            "where eep.exam_name=?3 and eep.exam_name=euwq.exam_paper_name and euwq.do_right=?4 \n" +
            "and euwq.student_number=?1 and euwq.subject=?2 ",nativeQuery = true)
    public List<UserWrongQustion> getErrorProblemsIdByExamName(String stuNumber,String subject,String examName,int doRight);

//    // 10. 统计用户练习错题数(已掌握的) lxj
//    @Query(value = "select count(distinct question_id) from e_user_wrong_qustion where `subject`=?2 and student_number=?1 and do_right=1 and exam_category='章节练习' ",nativeQuery = true)
//    int getChapterErrProblemsNum2(String stuNumber,String subject);
//
//    // 11. 统计用户考试错题数(已掌握的) lxj
//    @Query(value = "select count(distinct question_id) from e_user_wrong_qustion where `subject`=?2 and student_number=?1 and do_right=1 and (exam_category='模拟考试' or exam_category='历年真题') ",nativeQuery = true)
//    int getExamErrorProblemsNum2(String stuNumber,String subject);

    // 12. 统计用户已掌握错题数 lxj
    @Query(value = "select count(distinct question_id) from e_user_wrong_qustion where `subject`=?2 and student_number=?1 and do_right=1 ",nativeQuery = true)
    int getMasteredErrorProblemsNum(String stuNumber,String subject);

    // 12(1). 根据年级统计用户错题数(已掌握和未掌握，已掌握未掌握通过参数传) lxj
    @Query(value = "select count(distinct question_id) from e_user_wrong_qustion as euwq,e_exam_paper as eep where eep.grade_level=?2 and eep.id=euwq.exam_paper_id \n" +
            "and euwq.`subject`=?3 and euwq.student_number=?1 and euwq.do_right=?4 ",nativeQuery = true)
    int getMasteredErrorQuestionsCountByGradeLevel(String stuNumber,String levelName,String subject,int doRight);

    // 13. 统计用户未掌握错题数 lxj
    @Query(value = "select count(distinct question_id) from e_user_wrong_qustion where `subject`=?2 and student_number=?1 and do_right=2 ",nativeQuery = true)
    int getNotMasteredErrorProblemsNum(String stuNumber,String subject);

    // 14. 统计用户练习错题数  lxj
    @Query(value = "select count(distinct question_id) from e_user_wrong_qustion where `subject`=?2 and student_number=?1 and (exam_category='章节练习' or exam_category='专项练习') ",nativeQuery = true)
    int getPracticeErrorNum(String stuNumber,String subject);

    // 15. 统计用户考试错题数  lxj
    @Query(value = "select count(distinct question_id) from e_user_wrong_qustion where `subject`=?2 and student_number=?1 and (exam_category='模拟考试' or exam_category='历年真题') ",nativeQuery = true)
    int getExamErrorNum(String stuNumber,String subject);

    // 16. 用户删除已掌握错题中的某道题   lxj
    @Query(value="delete from e_user_wrong_qustion where id=?1 ",nativeQuery = true)
    @Modifying
    @Transactional
    void deleteById(int id);

    // 17. 根据学生学号、学科、题号、试题来源、已掌握查询已掌握错题的题号
    @Query(value = "select id from e_user_wrong_qustion where student_number=?1 and subject=?2 and do_right=1 and question_id=?3 and (exam_category=?4 or exam_category=?5) ",nativeQuery = true)
    String getIdBySubjectAndQuestionIdAndExamCategory(String stuNumber,String subject,int questionId,String examCategory1,String examCategory2);

    // 18. 错题表中查询同一用户、同一来源、同一道题做题记录  lxj
    @Query(value = "select * from e_user_wrong_qustion where student_number=?1 and `subject`=?2 and question_id=?3 and exam_paper_id=?4 \n" +
            "and exam_paper_name=?5 and (exam_category=?6 or exam_category=?7) ",nativeQuery = true)
    UserWrongQustion getUserWrongQustionByUserAndSubjectAndExamCategory(String stuNumber,String subject,int questionId,int examPaperId,String examPaperName,String examCategory1,String examCategory2);

    //19. 获取错题表中某用户-》某年级-》某科目 某类（章节练习） 未掌握（或已掌握）的所有题
    @Query(value = "SELECT * FROM e_user_wrong_qustion AS uwq\n" +
            "INNER JOIN e_question AS q \n" +
            "ON uwq.`question_id`=q.`id` \n" +
            "INNER JOIN e_exam_paper AS ep \n" +
            "ON uwq.`exam_paper_id`=ep.`id` \n" +
            "WHERE student_number=?1 AND ep.`grade_level`=?2 AND uwq.`subject`=?3 AND uwq.`do_right`=?4 AND ep.`exam_source`=?5 ", nativeQuery = true)
    List<UserWrongQustion> getAllInfo(String stuNumber,String gradeLevel,String subject,int doRight,String examCategory);

    //19.2 获取错题表中某用户-》某年级-》某科目 （专项练习） 未掌握（或已掌握）的所有题
    @Query(value = "SELECT * FROM e_user_wrong_qustion AS uwq\n" +
            "INNER JOIN e_question AS q \n" +
            "ON uwq.`question_id`=q.`id` \n" +
            "WHERE student_number=?1 AND q.`level_name`=?2 AND uwq.`subject`=?3 AND uwq.`do_right`=?4 AND uwq.exam_category=?5 ", nativeQuery = true)
    List<UserWrongQustion> getAllInfo2(String stuNumber,String gradeLevel,String subject,int doRight,String examCategory);


    //20. 获取错题表中 用户-》某年级-》某科目 各分类的 未掌握（或已掌握）的所有题
    @Query(value = "SELECT DISTINCT uwq.question_id,uwq.id,uwq.student_number,uwq.openid,uwq.`subject`,uwq.do_right,uwq.user_answer,uwq.exam_paper_id,uwq.exam_paper_name,uwq.exam_category,uwq.do_time,uwq.inserttime,uwq.updatetime FROM e_user_wrong_qustion AS uwq FROM e_user_wrong_qustion AS uwq\n" +
            "INNER JOIN e_exam_paper AS ep\n" +
            "ON uwq.`exam_paper_id`=ep.`id`\n" +
            "WHERE student_number=?1 AND ep.`grade_level`=?2 AND uwq.`subject`=?3 AND uwq.`do_right`=?4 ", nativeQuery = true)
    List<UserWrongQustion> getClassificationQuantity(String stuNumber,String gradeLevel,String subject,int doRight);

    //21. 获取错题表中 用户 -》 某年级 -》 某科目 全部的 未掌握和已掌握的 所有题
    @Query(value = "SELECT  uwq.*\n" +
            "FROM e_user_wrong_qustion AS uwq\n" +
            "INNER JOIN e_question AS q\n" +
            "ON uwq.`question_id`=q.`id`\n" +
            "INNER JOIN e_exam_paper AS ep\n" +
            "ON uwq.`exam_paper_id`=ep.`id`\n" +
            "WHERE student_number=?1 AND ep.`grade_level`=?2 AND uwq.`subject`=?3 AND uwq.`do_right`=?4 \n" +
            "GROUP BY uwq.`question_id` \n" +
            "ORDER BY  uwq.inserttime ASC", nativeQuery = true)
    List<UserWrongQustion> getAllByQuestion(String stuNumber,String gradeLevel,String subject,int doRight);

    //22. 获取获取错题表中 用户 -》 某年级 -》 某科目 -> 某道题 的 未掌握和已掌握的 此题的 来源
    @Query(value = "SELECT DISTINCT uwq.`exam_category` FROM e_user_wrong_qustion AS uwq\n" +
            "INNER JOIN e_question AS q\n" +
            "ON uwq.`question_id`=q.`id`\n" +
            "INNER JOIN e_exam_paper AS ep\n" +
            "ON uwq.`exam_paper_id`=ep.`id`\n" +
            "WHERE student_number=?1 AND ep.`grade_level`=?2 AND uwq.`subject`=?3 AND uwq.`do_right`=?4 AND uwq.`question_id`=?5", nativeQuery = true)
    List<String> getallQuestionSource(String stuNumber,String gradeLevel,String subject,int doRight, int questionId);


    //23. 根据“章节名称” 获取此学号、年级、科目、是否做对  获取 有哪些节有错题
    @Query(value = "SELECT DISTINCT uwq.`exam_paper_name` FROM e_user_wrong_qustion AS uwq\n" +
            "INNER JOIN e_exam_paper AS ep\n" +
            "ON uwq.`exam_paper_id`=ep.`id`\n" +
            "WHERE student_number=?1 AND ep.`grade_level`=?2 AND uwq.`subject`=?3 AND uwq.`do_right`=?4  AND ep.`exam_source`=?5 ", nativeQuery = true)
    List<String> getSectionName(String stuNumber,String gradeLevel,String subject,int doRight,String examCategory);

    //24. 获取错题中的章节对应关系，根据章的名称获取 去重后的 此节的名称
    @Query(value = "select DISTINCT euwq.exam_paper_name from e_user_wrong_qustion as euwq,e_exam_paper as eep,e_chapter as ec \n" +
            "where eep.chapter_id=ec.id and ec.chapter=?3 \n" +
            "and eep.exam_name=euwq.exam_paper_name and euwq.do_right=?4 and euwq.student_number=?1 and euwq.subject=?2 ",nativeQuery = true)
    List<String> getChapterSection(String stuNumber, String subject,String chapter,int doRight);

    //25. 获取专项练习中的 所有题的知识点属性，根据学号、年级、科目、是否作对
    @Query(value = "SELECT DISTINCT q.`question_attribute` FROM e_user_wrong_qustion AS uwq\n" +
            "INNER JOIN e_question AS q\n" +
            "ON uwq.`question_id`=q.`id`\n" +
            "WHERE  uwq.student_number=?1 AND uwq.SUBJECT=?3  AND uwq.do_right=?4 AND q.`level_name`=?2 AND uwq.`exam_category`=?5 ", nativeQuery = true)
    List<String> getQuestionAttribute(String stuNumber,String gradeLevel,String subject,int doRight,String examCategory);

    //26. 获取专项练习中 某个知识点属性的 数量，根据学号、年级、科目、是否作对、知识点属性
    @Query(value = "SELECT COUNT(*) FROM e_user_wrong_qustion AS uwq\n" +
            "INNER JOIN e_question AS q\n" +
            "ON uwq.`question_id`=q.`id`\n" +
            "WHERE  uwq.student_number=?1 AND uwq.SUBJECT=?3  AND uwq.do_right=?4 AND q.`level_name`=?2 AND uwq.`exam_category`=?5 AND FIND_IN_SET(?6,q.`question_attribute`) ", nativeQuery = true)
    int getQuestionAttributeNum(String stuNumber,String gradeLevel,String subject,int doRight,String examCategory,String attribute);

    //27. 获取错题表中某用户-》某年级-》某科目 章节练习 未掌握（或已掌握）的所有题
    @Query(value = "SELECT * FROM e_user_wrong_qustion AS uwq\n" +
            "INNER JOIN e_question AS q\n" +
            "ON uwq.`question_id`=q.`id`\n" +
            "WHERE  uwq.student_number=?1 AND uwq.SUBJECT=?3  AND uwq.do_right=?4 AND q.`level_name`=?2 AND uwq.`exam_category`=?5 ", nativeQuery = true)
    List<UserWrongQustion> getAllChapterInfo(String stuNumber,String gradeLevel,String subject,int doRight,String examCategory);

    //28. 获取历年真题获取模拟考试的 掌握或未掌握的 试卷名称，根据学号、年级、科目、是否作对，历年真题或模拟考试
    @Query(value = "SELECT DISTINCT uwq.`exam_paper_name` FROM e_user_wrong_qustion AS uwq \n" +
            "INNER JOIN e_exam_paper AS ep \n" +
            "ON uwq.`exam_paper_id`=ep.`id`\n" +
            "WHERE student_number=?1 AND ep.`grade_level`=?2 AND uwq.`subject`=?3 AND uwq.`do_right`=?4  AND ep.`exam_source`= uwq.`exam_category` AND uwq.`exam_category`=?5 ", nativeQuery = true)
    List<String> getExamPaperName(String stuNumber,String gradeLevel,String subject,int doRight,String examCategory);
    //29. 获取此考试名称的 未掌握或已掌握的 错题数量，根据学号、年级、科目、是否作对，历年真题或模拟考试的试卷名称
    @Query(value = "SELECT COUNT(DISTINCT uwq.`question_id`) FROM e_user_wrong_qustion AS uwq \n" +
            "INNER JOIN e_exam_paper AS ep \n" +
            "ON uwq.`exam_paper_id`=ep.`id`\n" +
            "WHERE student_number=?1 AND ep.`grade_level`=?2 AND uwq.`subject`=?3 AND uwq.`do_right`=?4  AND ep.`exam_source`= uwq.`exam_category` AND uwq.`exam_paper_name`=?5 ", nativeQuery = true)
    int getgetExamPaperNameNum(String stuNumber,String gradeLevel,String subject,int doRight,String examPaperName);

    // 30. 获取此 模块的总共未掌握或已掌握的错题数量，根据学号、年级、科目、是否作对，历年真题或模拟考试
    @Query(value = "SELECT COUNT(DISTINCT uwq.`question_id`) FROM e_user_wrong_qustion AS uwq \n" +
            "INNER JOIN e_exam_paper AS ep \n" +
            "ON uwq.`exam_paper_id`=ep.`id`\n" +
            "INNER JOIN e_question AS q ON uwq.`question_id`=q.id  \n" +
            "WHERE student_number=?1 AND ep.`grade_level`=?2 AND uwq.`subject`=?3 AND uwq.`do_right`=?4  AND ep.`exam_source`= uwq.`exam_category` AND uwq.`exam_category`=?5 AND q.`level_name`=?2 ", nativeQuery = true)
    int getTotalExamPaperNum(String stuNumber,String gradeLevel,String subject,int doRight,String examCategory);

    //31. 传来“全部”时，获取所有 用户的、科目、年级、是否掌握 所有数量
    @Query(value = "SELECT * FROM e_user_wrong_qustion AS uwq \n" +
            "INNER JOIN e_question AS q\n" +
            "ON uwq.`question_id`=q.id\n" +
            "WHERE student_number=?1 AND uwq.`subject`=?2 AND q.level_name=?3 AND uwq.`do_right`=?4 ", nativeQuery = true)
    List<UserWrongQustion> totalNum(String stuNumber,String subject,String gradeLevel,int doRight);

}
