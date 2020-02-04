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

    //5. 获取此用户-》此科目-》章节练习中 所有错的  试卷名称（每节的名称）
    @Query(value = "SELECT DISTINCT exam_paper_name FROM e_user_question_record WHERE student_number=?1 AND SUBJECT=?2 AND do_right=?3 AND exam_category IN(?4,?5) ", nativeQuery = true)
    List<String> getAllErrInfo(String stuNumber,String subject,int doRight,String examCategory,String examCategory2);

    //6. 获取此用户-》此科目-》章节练习-》所有试卷名称
    @Query(value = "SELECT DISTINCT exam_paper_name FROM e_user_question_record WHERE student_number=?1 AND SUBJECT=?2 AND exam_category=?3 ", nativeQuery = true)
    List<String> getAllExamPaperName(String stuNumber,String subject,String examCategory);

    //7. 获取此用户-》此科目-》章节练习中 所有错题的信息
    List<UserQuestionRecord> getByStudentNumberAndSubjectAndDoRightAndExamCategory(String stuNumber,String subject,int doRight,String examCategory);

    //8. 获取此节的 所有错题数量  lxj
    @Query(value = "SELECT COUNT(DISTINCT question_id) FROM e_user_question_record WHERE student_number=?1 AND SUBJECT=?2 AND exam_paper_name=?3",nativeQuery = true)
    int getByErrNumber(String stuNumber,String subject,String examPaperName);

    // 9. 查询用户某试卷最新一次做题记录 lxj
    @Query(value = "select * from e_user_question_record where student_number=?1 and exam_paper_name=?2 and exam_category=?3 and \n" +
            "times=(select max(times) from e_user_question_record where student_number=?1 and exam_paper_name=?2 and exam_category=?3) ",nativeQuery = true)
    List<UserQuestionRecord> getUserQuestionRecord(String stuNumber,String examName,String source);
    // 9.2 查询用户某试卷最新一次做题记录 lxj
    @Query(value = "select * from e_user_question_record where student_number=?1 and exam_paper_name=?2 and times=(select max(times) \n" +
            "from e_user_question_record where student_number=?1 and exam_paper_name=?2) AND inserttime >=?3 AND inserttime <=?4 ",nativeQuery = true)
    List<UserQuestionRecord> getUserQuestionRecord2(String stuNumber,String examName,String starTime,String endTime);
    // 9.3 用于十五接口 查询用户某试卷最新一次做题记录 lxj
    @Query(value = "select * from e_user_question_record where student_number=?1 and exam_paper_name=?2 and times=(select max(times) \n" +
            "from e_user_question_record where student_number=?1 and exam_paper_name=?2) AND exam_category=?3  ",nativeQuery = true)
    List<UserQuestionRecord> getUserQuestionRecord3(String stuNumber,String examName,String source);

    // 9(1). 查询用户专项练习最新一次做题记录 lxj
    @Query(value = "select * from e_user_question_record where student_number=?1 and knowledge_points=?2 and exam_category=?3 and \n" +
            "times=(select max(times) from e_user_question_record where student_number=?1 and exam_category=?3 \n" +
            "and knowledge_points=?2) ",nativeQuery = true)
    List<UserQuestionRecord> getUserQuestionRecordByKnowledgePoints(String stuNumber,String knowledgePoints,String source);

    // 10. 查询用户某份试卷最新一次做题时间 lxj
    @Query(value = "select max(updatetime) from e_user_question_record where student_number=?1 and exam_paper_name=?2 \n" +
            "and exam_category=?3 and times=(select max(times) from e_user_question_record where student_number=?1 \n" +
            "and exam_paper_name=?2 and exam_category=?3)",nativeQuery = true)
    String getDoTimeByChapter(String stuNumber,String examName,String category);
    // 10.2 查询用户某份试卷最新一次做题时间 lxj
    @Query(value = "select max(updatetime) from e_user_question_record where student_number=?1 and exam_paper_name=?2 \n" +
            "and exam_category=?3 and times=(select max(times) from e_user_question_record where student_number=?1 \n" +
            "and exam_paper_name=?2 and exam_category=?3) AND inserttime >=?4 AND inserttime <=?5 ",nativeQuery = true)
    String getDoTimeByChapter2(String stuNumber,String examName,String category,String starTime,String endTime);
    // 11. 根据用户学号查询用户做题时间  lxj
    @Query(value = "select distinct date_format(euqr.updatetime,'%Y-%m-%d') date FROM e_user_question_record as euqr,e_question as eq,\n" +
            "e_exam_paper as eep where euqr.student_number=?1 and euqr.`subject`=?2 and euqr.question_id=eq.id and eq.exam_id=eep.id \n" +
            "and eep.grade_level=?3 ",nativeQuery = true)
    List<String> getDoQuestionsDate(String stuNumber,String subject,String levelName);

    // 12. 根据用户时间统计用户当天做题数  lxj
    @Query(value = "select count(id) from e_user_question_record where student_number=?1 and DATE_FORMAT(inserttime,'%Y-%m-%d')=?2 ",nativeQuery = true)
    int getDoQUestionsNumsByDate(String stuNumber,String date);

    // 13. 根据做题时间和用户学号查询用户当天做对题数  lxj
    @Query(value = "select count(id) from e_user_question_record where student_number=?1 and DATE_FORMAT(inserttime,'%Y-%m-%d')=?2 and do_right=?3 ",nativeQuery = true)
    int getDoQuestionsRightNumsByDate(String stuNumber,String date, int doRight);

    // 14. 根据做题时间查询用户当天做题情况(用户获取用户做每道题的时间)  lxj
    @Query(value = "select do_time from e_user_question_record where student_number=?1 and DATE_FORMAT(inserttime,'%Y-%m-%d')=?2 ",nativeQuery = true)
    List<String> getDoQuestionsTimeList(String stuNumber,String date);

    // 15. 每个知识点最新一次做题时间

    // 16. 按照知识点统计用户最新一次做题记录  lxj
    @Query(value = "select * from e_user_question_record where student_number=?1 and knowledge_points=?2 and exam_category=?3 and \n" +
            "times=(select max(times) from e_user_question_record where student_number=?1 and knowledge_points=?2 and exam_category=?3) ",nativeQuery = true)
    List<UserQuestionRecord> getQuestionsRecordByAttribute(String stuNumber, String attribute, String questionCategory);
    // 16.2 按照知识点统计用户最新一次做题记录  lxj
    @Query(value = "select * from e_user_question_record where student_number=?1 and knowledge_points=?2 and exam_category=?3 and \n" +
            "times=(select max(times) from e_user_question_record where student_number=?1 and knowledge_points=?2 and exam_category=?3) AND inserttime >=?4 AND inserttime <=?5 ",nativeQuery = true)
    List<UserQuestionRecord> getQuestionsRecordByAttribute2(String stuNumber, String attribute, String questionCategory,String starTime,String endTime);

    // 17. 查询用户某个知识点最新一次做题时间
    @Query(value = "select max(updatetime) from e_user_question_record where student_number=?1 and knowledge_points=?2 \n" +
            "and exam_category=?3 and times=(select max(times) from e_user_question_record where student_number=?1 \n" +
            "and knowledge_points=?2 and exam_category=?3) ",nativeQuery = true)
    String getDoTimeByAttribute(String stuNumber,String attribute,String category);
    // 17.2 查询用户某个知识点最新一次做题时间
    @Query(value = "select max(updatetime) from e_user_question_record where student_number=?1 and knowledge_points=?2 \n" +
            "and exam_category=?3 and times=(select max(times) from e_user_question_record where student_number=?1 \n" +
            "and knowledge_points=?2 and exam_category=?3) AND inserttime >=?4 AND inserttime <=?5 ",nativeQuery = true)
    String getDoTimeByAttribute2(String stuNumber,String attribute,String category,String starTime,String endTime);

    // 18. 十九、 学习记录：上面三个数的统计：用户做题总数
    @Query(value = "select count(*) from e_user_question_record as euqr, e_question as eq where euqr.student_number=?1 and euqr.`subject`=?2 and euqr.question_id=eq.id \n" +
            "and eq.level_name=?3 ",nativeQuery = true)
    int getDoQuestionCount(String stuNumber,String  subject,String levelName);

    // 18(2). 十九、 学习记录：上面三个数的统计：查询用户每道题做题时长
    @Query(value = "select euqr.do_time from e_user_question_record as euqr, e_question as eq where euqr.student_number=?1 and euqr.`subject`=?2 \n" +
            "and euqr.question_id=eq.id and eq.level_name=?3 ",nativeQuery = true)
    List<String> getDoQUestionTime(String stuNumber,String subject, String levelName);

    // 18(3). 十九、 学习记录：上面三个数的统计：查询用户每道题做题时间
    @Query(value = "select euqr.* from e_user_question_record as euqr, e_question as eq where euqr.student_number=?1 and euqr.`subject`=?2 \n" +
            "and euqr.question_id=eq.id and eq.level_name=?3 ",nativeQuery = true)
    List<UserQuestionRecord> getDoQuestionUpdatetime(String stuNumber, String subject, String levelName);

    // 18(4). 十九、 学习记录：此用户、科目、年级 做题天数
    @Query(value = "select COUNT(DISTINCT SUBSTRING(euqr.inserttime,1,10)) from e_user_question_record as euqr, e_question as eq where euqr.student_number=?1 and euqr.`subject`=?2 \n" +
            "and euqr.question_id=eq.id and eq.level_name=?3 ",nativeQuery = true)
    int getDoQuestionDays(String stuNumber, String subject, String levelName);

    //19. 二十二、专项练习： 获取 专项练习 模块中，此用户是否做过,按做过次数的降序排列
    @Query(value = "SELECT * FROM e_user_question_record WHERE student_number=?1 AND exam_category=?2 AND question_id=?3 AND SUBJECT=?4 ORDER BY times DESC", nativeQuery = true)
    List<UserQuestionRecord> getSpecialRecord(String stuNumber,String examCategory,int questionid,String subject);

}
