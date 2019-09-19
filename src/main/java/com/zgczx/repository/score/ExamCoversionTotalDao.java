package com.zgczx.repository.score;

import com.zgczx.dataobject.score.ExamCoversionTotal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import java.math.BigInteger;
import java.util.List;

/**
 * @author aml
 * @date 2019/9/10 15:52
 */
@Repository
public interface ExamCoversionTotalDao extends JpaRepository<ExamCoversionTotal, Integer> {

    /**
     * 根据学号获取此学生所有成绩
     *
     * @param stuNumber 学号
     * @return ExamCoversionTotal实体对象
     */
    ExamCoversionTotal findByStudentNumberAndExamType(String stuNumber,String examType);

    /**
     *  获取单科的班级排名
     * @param  单科具体科目
     * @return 学号、单科分数、单科班级排名
     */
//    @Query(value = "SELECT t.student_number, t.yuwen_score,t.rank" +
//            "FROM " +
//            "  (select u.student_number, u.yuwen_score, @rank:= @rank + 1," +
//            "    @last_rank:= CASE " +
//            "      WHEN @last_score = u.yuwen_score\n" +
//            "        THEN @last_rank" +
//            "      WHEN @last_score:= u.yuwen_score \n" +
//            "        THEN @rank " +
//            "    END AS rank " +
//            "  FROM " +
//            "    (SELECT * FROM exam_coversion_total WHERE class_id=?1 AND exam_type=?2 ORDER BY yuwen_score DESC) u, " +
//            "    (SELECT @rank:= 0, @last_score:= NULL, @last_rank:= 0) r" +
//            ")t ", nativeQuery = true)
    @Query(value = "SELECT t.student_number, t.shuxue_score,t.rank FROM(SELECT u.student_number,u.shuxue_score,@rank \\:= @rank + 1,@last_rank \\:= CASE WHEN @last_score = u.shuxue_score THEN @last_rank WHEN @last_score \\:= u.shuxue_score THEN @rank END AS rank FROM (SELECT * FROM exam_coversion_total WHERE class_id=?1 AND exam_type=?2 ORDER BY shuxue_score DESC)u,(SELECT @rank \\:= 0,@last_score \\:= NULL,@last_rank \\:= 0)r)t", nativeQuery = true)
    List<String> getSingleClassRank(String classid, String examType);

    /**
     *  获取某科目的成绩排序，降序
     * @param classid 班级id
     * @param examType 哪次考试
     * @param subject 哪个科目
     * @return 实体对象
     */
    //@Query("select examCoversionTotal from ExamCoversionTotal examCoversionTotal where examCoversionTotal.classId = ?1 and examCoversionTotal.examType = ?2 order by ?3 desc ")
    //@Query(value = "SELECT * FROM exam_coversion_total WHERE class_id=?1 AND exam_type=?2 ORDER BY  yingyu_score=?3  DESC", nativeQuery = true)  必须得指定第三个参数是数据库中哪个列的
    List<ExamCoversionTotal> findAllByClassIdAndExamType(String classid, String examType,String subject);

    // 获取三科的班排
    @Query(value = "SELECT student_number,yuwen_score+shuxue_score+yingyu_score AS s FROM exam_coversion_total WHERE class_id=?1 AND exam_type=?2 ORDER BY s DESC", nativeQuery = true)
    List<String[]> findByClassIdAndExamType(String classid, String examType);

    //获取三科的年排
    @Query(value = "SELECT student_number,yuwen_score+shuxue_score+yingyu_score AS s FROM exam_coversion_total WHERE  exam_type=?1 ORDER BY s DESC", nativeQuery = true)
    List<String[]> findByClassIdAndExamTypeGrade(String examType);

    // 获取综合的班排
    @Query(value = "SELECT student_number,wuli_coversion+huaxue_coversion+shengwu_coversion+lishi_coversion+dili_coversion+zhengzhi_coversion  AS s FROM exam_coversion_total WHERE class_id=?1 AND exam_type=?2 ORDER BY s DESC", nativeQuery = true)
    List<String[]> findByClassIdAndExamTypeComplex(String classid, String examType);

    // 获取综合的年排
    @Query(value = "SELECT student_number,wuli_coversion+huaxue_coversion+shengwu_coversion+lishi_coversion+dili_coversion+zhengzhi_coversion  AS s FROM exam_coversion_total WHERE  exam_type=?1 ORDER BY s DESC", nativeQuery = true)
    List<String[]> findByClassIdAndExamTypeComplexGrade(String examType);

    @Query(value = "select e.coversionTotal from ExamCoversionTotal as e where e.classId = ?1 and e.examType = ?2")
    List<Double> getCoversionTotalByClassIdAndExamType(String classid, String examType);

    @Query(value = "select sum(sfs.yuwen+sfs.shuxue+sfs.yingyu+sfs.wuli+sfs.huaxue+sfs.shengwu+sfs.zhengzhi+sfs.lishi+sfs.dili)as total from subject_full_score sfs,exam_full_score_set sfss where sfs.id=sfss.subject_schame_id and sfss.examinfo_id=?1  ",nativeQuery = true)
    @Transactional
    BigInteger findSchametotal(int examid);
}
