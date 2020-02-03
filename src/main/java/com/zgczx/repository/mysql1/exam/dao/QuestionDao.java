package com.zgczx.repository.mysql1.exam.dao;

import com.zgczx.repository.mysql1.exam.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

/**
 * 题库表
 * @author aml
 * @date 2019/12/11 20:47
 */
@Repository
public interface QuestionDao extends JpaRepository<Question, Integer> {

    //1. 获取相同考试名的所有题
    List<Question> findByExamName(String examName);

    //2. 获取此试卷中的所有题的信息，根据idList：题库的主键id列表
    List<Question> findByIdIn(List<Integer> idList);

    // 3. 获取此道题的 科目名称 ： 根据 question中的主键id
    @Query(value = "SELECT SUBJECT FROM e_exam_paper WHERE id=(SELECT exam_id FROM e_question WHERE id =?1 )", nativeQuery = true)
    String getSubjectName(int id);

    // 4. 根据章节名称查询收藏的题的详细信息 lxj
    @Query(value = "select * from e_question where id in (select distinct question_id from e_user_collect as euc,e_exam_paper as eep,e_chapter as ec \n" +
            "where ec.chapter=?3 and ec.section=?4 \n" +
            "and ec.section=eep.exam_name and eep.id=euc.exam_paper_id and euc.valid=1 and euc.student_number=?1 and euc.subject=?2) ",nativeQuery = true)
    List<Question> getSectionCollectProblems(String stuNumber,String subject,String chapter,String section);

    // 5. 根据考试名称查询收藏的题的详细信息 lxj
    @Query(value = "select * from e_question where id in (select distinct question_id from e_user_collect as euc,e_exam_paper as eep\n" +
            "where eep.exam_name=?3 \n" +
            "and eep.id=euc.exam_paper_id and euc.valid=1 and euc.student_number=?1 and euc.subject=?2) ",nativeQuery = true)
    public List<Question> getExamCollectProblems(String stuNumber,String subject,String examName);

    // 6. 根据question_id查询题的详细信息 lxj
    @Query(value = "select * from e_question where id=?1 and subject=?2 ",nativeQuery = true)
    public List<Question> getQuestionInfoById(int id,String subject);

    // 7. 根据章名称获取本章对应的题数  lxj
    @Query(value = "select count(distinct eq.id) from e_chapter as ec,e_question as eq,e_exam_paper as eep where ec.`subject`=?1 and ec.level_name=?2 and \n" +
            "ec.chapter=?3 and ec.section=eep.exam_name and eep.id=eq.exam_id ",nativeQuery = true)
    public int getQuestionsNumByChapter(String subject,String levelName,String chapter);

    // 8. 根据科目、章的名称、知识点得到对应的题的详细信息(已涉及学科和年级)  lxj
    @Query(value = "select ec.chapter,eq.* from e_question as eq,e_chapter as ec,e_exam_paper as eep where ec.`subject`=?1 and ec.level_name=?2 and \n" +
            "ec.chapter=?3 and ec.section=eep.exam_name and eep.id=eq.exam_id and eq.question_attribute=?4 ",nativeQuery = true)
    public List<Question> getQuestionsBySubjectAndChapterAndAttribute(String subject,String levelName,String chapter,String attribute);

    // 9. 根据章节  获取知识点和对应的题数(已涉及学科和年级)  lxj
    @Query(value = "select distinct eq.question_attribute from e_question as eq,e_chapter as ec,e_exam_paper as eep where ec.`subject`=?1 and ec.level_name=?2 and \n" +
            "ec.chapter=?3 and ec.section=eep.exam_name and eep.id=eq.exam_id ",nativeQuery = true)
    public List<String> getAttributesByChapter(String subject,String levelName,String chapter);

    // 10. 根据知识点获取对应的题数(已涉及学科和年级)  lxj
    @Query(value = "select count(distinct id) from e_question as eq where find_in_set(?1,question_attribute) " ,nativeQuery = true)
    int getQuestionsNumsByAttribute(String attribute);

    // 11. 根据年级和学科查询对应的知识点
    @Query(value = "select distinct question_attribute from e_question where `subject`=?1 and level_name=?2 and valid=1",nativeQuery = true)
    List<String> getQUestionAttribute(String subject,String levelName);




    // 批量修改，用于解析Word并存库
    @Modifying
    @Transactional
    @Query(value = "update e_question as a set a.exam_id=?2 where a.id in (?1) ", nativeQuery = true)
    int updateByIds(List<Integer> idList,int examId);

    // 获取此题 有效的所有数据
    Question getByIdAndValid(int id, int valid);

    //14. 专项练习： 根据知识点、年级、科目 获取相关的所有题
    @Query(value = "SELECT * FROM e_question WHERE SUBJECT=?1 AND level_name=?2 AND FIND_IN_SET(?3,question_attribute) and valid=1    ", nativeQuery = true)
    List<Question> getAllSubjectAndLevelNameByQuestionAndAttribute(String subject,String levelName,String point);


}
