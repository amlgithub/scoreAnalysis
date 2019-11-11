package com.zgczx.repository.mysql2.scoretwo.dao;

import com.zgczx.repository.mysql2.scoretwo.model.ExamCoversionTotal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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

    @Query(value = "SELECT * FROM exam_coversion_total WHERE (student_number=?1 OR openid=?2) AND exam_type=?3 AND valid='1'", nativeQuery = true)
    ExamCoversionTotal findByStudentNumberOrOpenidAndExamType(String stuNumber,String openid,String examType);
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



    /**
     *  语数英、物化生、政史地，九门科目的降序排列,年级排名
     * @param examType 具体考试名称
     * @return 返回单个数据库字段的所有值
     */
    @Query(value = "select yuwen_score from exam_coversion_total where exam_type=?1 order by yuwen_score desc", nativeQuery = true)
    List<String> findByYuwenScore(String examType);
    @Query(value = "select yuwen_score from exam_coversion_total where exam_type=?1 AND school_name=?2 AND valid=?3 order by yuwen_score desc", nativeQuery = true)
    List<String> findByYuwenScoreAndSchoolNameAndValid(String examType,String schoolName, int valid);
    // 求语文的班级排名list
    @Query(value = "select yuwen_score from exam_coversion_total where exam_type=?1 AND school_name=?2 AND valid=?3 AND class_id=?4 order by yuwen_score desc", nativeQuery = true)
    List<String> findByYuwenScoreAndSchoolNameAndValidAndClassId(String examType,String schoolName, int valid,String classid);

    @Query(value = "select shuxue_score from exam_coversion_total where exam_type=?1 order by shuxue_score desc", nativeQuery = true)
    List<String> findByShuxueScore(String examType);
    @Query(value = "select shuxue_score from exam_coversion_total where exam_type=?1 AND school_name=?2 AND valid=?3 order by shuxue_score desc", nativeQuery = true)
    List<String> findByShuxueScoreAndSchoolNameAndValid(String examType,String schoolName, int valid);
    // 求数学的班级排名list
    @Query(value = "select shuxue_score from exam_coversion_total where exam_type=?1 AND school_name=?2 AND valid=?3 AND class_id=?4 order by shuxue_score desc", nativeQuery = true)
    List<String> findByShuxueScoreAndSchoolNameAndValidAndClassId(String examType,String schoolName, int valid,String classid);

    @Query(value = "select yingyu_score from exam_coversion_total where exam_type=?1 order by yingyu_score desc", nativeQuery = true)
    List<String> findByYingyuScore(String examType);
    @Query(value = "select yingyu_score from exam_coversion_total where exam_type=?1 AND school_name=?2 AND valid=?3 order by yingyu_score desc", nativeQuery = true)
    List<String> findByYingyuScoreAndSchoolNameAndValid(String examType,String schoolName, int valid);
    // 求英语的班级排名list
    @Query(value = "select yingyu_score from exam_coversion_total where exam_type=?1 AND school_name=?2 AND valid=?3 AND class_id=?4 order by yingyu_score desc", nativeQuery = true)
    List<String> findByYingyuScoreAndSchoolNameAndValidAndClassId(String examType,String schoolName, int valid,String classid);


    @Query(value = "select wuli_coversion from exam_coversion_total where exam_type=?1 order by wuli_coversion desc", nativeQuery = true)
    List<String> findByWuliCoversion(String examType);
    @Query(value = "select wuli_coversion from exam_coversion_total where exam_type=?1 AND school_name=?2 AND valid=?3 order by wuli_coversion desc", nativeQuery = true)
    List<String> findByWuliCoversionAndSchoolNameAndValid(String examType,String schoolName, int valid);
    // 求物理的班级排名list
    @Query(value = "select wuli_coversion from exam_coversion_total where exam_type=?1 AND school_name=?2 AND valid=?3 AND class_id=?4 order by wuli_coversion desc", nativeQuery = true)
    List<String> findByWuliCoversionAndSchoolNameAndValidAndClassId(String examType,String schoolName, int valid,String classid);



    @Query(value = "select huaxue_coversion from exam_coversion_total where exam_type=?1 order by huaxue_coversion desc", nativeQuery = true)
    List<String> findByHuaxueCoversion(String examType);
    @Query(value = "select huaxue_coversion from exam_coversion_total where exam_type=?1 AND school_name=?2 AND valid=?3 order by huaxue_coversion desc", nativeQuery = true)
    List<String> findByHuaxueCoversionAndSchoolNameAndValid(String examType,String schoolName, int valid);
    // 求化学的班级排名list
    @Query(value = "select huaxue_coversion from exam_coversion_total where exam_type=?1 AND school_name=?2 AND valid=?3 AND class_id=?4 order by huaxue_coversion desc", nativeQuery = true)
    List<String> findByHuaxueCoversionAndSchoolNameAndValidAndClassId(String examType,String schoolName, int valid,String classid);


    @Query(value = "select shengwu_coversion from exam_coversion_total where exam_type=?1 order by shengwu_coversion desc", nativeQuery = true)
    List<String> findByShengwuCoversion(String examType);
    @Query(value = "select shengwu_coversion from exam_coversion_total where exam_type=?1 AND school_name=?2 AND valid=?3 order by shengwu_coversion desc", nativeQuery = true)
    List<String> findByShengwuCoversionAndSchoolNameAndValid(String examType,String schoolName, int valid);
    // 求生物的班级排名list
    @Query(value = "select shengwu_coversion from exam_coversion_total where exam_type=?1 AND school_name=?2 AND valid=?3 AND class_id=?4 order by shengwu_coversion desc", nativeQuery = true)
    List<String> findByShengwuCoversionAndSchoolNameAndValidAndClassId(String examType,String schoolName, int valid,String classid);


    @Query(value = "select lishi_coversion from exam_coversion_total where exam_type=?1 order by lishi_coversion desc", nativeQuery = true)
    List<String> findByLishiCoversion(String examType);
    @Query(value = "select lishi_coversion from exam_coversion_total where exam_type=?1 AND school_name=?2 AND valid=?3 order by lishi_coversion desc", nativeQuery = true)
    List<String> findByLishiCoversionAndSchoolNameAndValid(String examType,String schoolName, int valid);
    // 求历史的班级排名list
    @Query(value = "select lishi_coversion from exam_coversion_total where exam_type=?1 AND school_name=?2 AND valid=?3 AND class_id=?4 order by lishi_coversion desc", nativeQuery = true)
    List<String> findByLishiCoversionAndSchoolNameAndValidAndClassId(String examType,String schoolName, int valid,String classid);


    @Query(value = "select dili_coversion from exam_coversion_total where exam_type=?1 order by dili_coversion desc", nativeQuery = true)
    List<String> findByDiliCoversion(String examType);
    @Query(value = "select dili_coversion from exam_coversion_total where exam_type=?1 AND school_name=?2 AND valid=?3 order by dili_coversion desc", nativeQuery = true)
    List<String> findByDiliCoversionAndSchoolNameAndValid(String examType,String schoolName, int valid);
    // 求历史的班级排名list
    @Query(value = "select dili_coversion from exam_coversion_total where exam_type=?1 AND school_name=?2 AND valid=?3 AND class_id=?4 order by dili_coversion desc", nativeQuery = true)
    List<String> findByDiliCoversionAndSchoolNameAndValidAndClassId(String examType,String schoolName, int valid,String classid);


    @Query(value = "select zhengzhi_coversion from exam_coversion_total where exam_type=?1 order by zhengzhi_coversion desc", nativeQuery = true)
    List<String> findByZhengzhiCoversion(String examType);
    @Query(value = "select zhengzhi_coversion from exam_coversion_total where exam_type=?1 AND school_name=?2 AND valid=?3 order by zhengzhi_coversion desc", nativeQuery = true)
    List<String> findByZhengzhiCoversionAndSchoolNameAndValid(String examType,String schoolName, int valid);
    // 求历史的班级排名list
    @Query(value = "select zhengzhi_coversion from exam_coversion_total where exam_type=?1 AND school_name=?2 AND valid=?3 AND class_id=?4 order by zhengzhi_coversion desc", nativeQuery = true)
    List<String> findByZhengzhiCoversionAndSchoolNameAndValidAndClassId(String examType,String schoolName, int valid,String classid);

    // 获取总分的数组降序
    @Query(value = "SELECT coversion_total FROM exam_coversion_total WHERE exam_type=?1 ORDER BY coversion_total DESC", nativeQuery = true)
    List<String> findByTotalScore(String examType);

    /**
     *  语数英、物化生、政史地，九门科目的降序排列,班级排名
     * @param examType 具体考试名称
     * @return 返回单个数据库字段的所有值
     */
    @Query(value = "select yuwen_score from exam_coversion_total where class_id = ?1 and exam_type=?2 order by yuwen_score desc", nativeQuery = true)
    List<String> findByClassIdAndYuwenScore(String classid, String examType);
    @Query(value = "select shuxue_score from exam_coversion_total where class_id = ?1 and exam_type=?2 order by shuxue_score desc", nativeQuery = true)
    List<String> findByClassIdAndShuxueScore(String classid, String examType);
    @Query(value = "select yingyu_score from exam_coversion_total where class_id = ?1 and exam_type=?2 order by yingyu_score desc", nativeQuery = true)
    List<String> findByClassIdAndYingyuScore(String classid, String examType);
    @Query(value = "select wuli_coversion from exam_coversion_total where class_id = ?1 and exam_type=?2 order by wuli_coversion desc", nativeQuery = true)
    List<String> findByClassIdAndWuliCoversion(String classid, String examType);
    @Query(value = "select huaxue_coversion from exam_coversion_total where class_id = ?1 and exam_type=?2 order by huaxue_coversion desc", nativeQuery = true)
    List<String> findByClassIdAndHuaxueCoversion(String classid, String examType);
    @Query(value = "select shengwu_coversion from exam_coversion_total where class_id = ?1 and exam_type=?2 order by shengwu_coversion desc", nativeQuery = true)
    List<String> findByClassIdAndShengwuCoversion(String classid, String examType);
    @Query(value = "select lishi_coversion from exam_coversion_total where class_id = ?1 and exam_type=?2 order by lishi_coversion desc", nativeQuery = true)
    List<String> findByClassIdAndLishiCoversion(String classid, String examType);
    @Query(value = "select dili_coversion from exam_coversion_total where class_id = ?1 and exam_type=?2 order by dili_coversion desc", nativeQuery = true)
    List<String> findByClassIdAndDiliCoversion(String classid, String examType);
    @Query(value = "select zhengzhi_coversion from exam_coversion_total where class_id = ?1 and exam_type=?2 order by zhengzhi_coversion desc", nativeQuery = true)
    List<String> findByClassIdAndZhengzhiCoversion(String classid, String examType);

    // 获取此次考试的年级人数
    int countByExamType(String examType);
    // 获取此次考试的年级人数,以valid = 1 的所有数据来统计
    int countByExamTypeAndValidAndSchoolName(String examType,int valid,String schoolName);

    // 获取此次考试的班级人数
    int countByClassIdAndExamType(String classid, String examType);
    int countByClassIdAndExamTypeAndValidAndSchoolName(String classid, String examType,int valid,String schoolName);

    // 获取选取物理的年级总人数，按照考试分数 > 0来计算，可能会有些误差
    @Query(value = "SELECT COUNT(*) FROM exam_coversion_total WHERE exam_type=?1 AND wuli_coversion > '0'", nativeQuery = true)
    int countByExamTypeAndWuli(String examType);
    // 获取选取化学的年级总人数，按照考试分数 > 0来计算，可能会有些误差
    @Query(value = "SELECT COUNT(*) FROM exam_coversion_total WHERE exam_type=?1 AND huaxue_coversion > '0'", nativeQuery = true)
    int countByExamTypeAndHuaxue(String examType);
    // 获取选取生物的年级总人数，按照考试分数 > 0来计算，可能会有些误差
    @Query(value = "SELECT COUNT(*) FROM exam_coversion_total WHERE exam_type=?1 AND shengwu_coversion > '0'", nativeQuery = true)
    int countByExamTypeAndShengwu(String examType);
    // 获取选取政治的年级总人数，按照考试分数 > 0来计算，可能会有些误差
    @Query(value = "SELECT COUNT(*) FROM exam_coversion_total WHERE exam_type=?1 AND zhengzhi_coversion > '0'", nativeQuery = true)
    int countByExamTypeAndZhegnzhi(String examType);
    // 获取选取历史的年级总人数，按照考试分数 > 0来计算，可能会有些误差
    @Query(value = "SELECT COUNT(*) FROM exam_coversion_total WHERE exam_type=?1 AND lishi_coversion > '0'", nativeQuery = true)
    int countByExamTypeAndLishi(String examType);
    // 获取选取地理的年级总人数，按照考试分数 > 0来计算，可能会有些误差
    @Query(value = "SELECT COUNT(*) FROM exam_coversion_total WHERE exam_type=?1 AND dili_coversion > '0'", nativeQuery = true)
    int countByExamTypeAndDili(String examType);





    // 班级总分累加和
//    @Query(value = "SELECT SUM(coversion_total) FROM exam_coversion_total WHERE class_id=?1 AND exam_type=?2 ", nativeQuery = true)
//    float sumCoversionTotalByClassIdAndExamType(String classid, String examType);
    @Query(value = "SELECT SUM(coversion_total) FROM exam_coversion_total WHERE class_id=?1 AND exam_type=?2 AND valid=?3 AND school_name=?4", nativeQuery = true)
    float sumCoversionTotalByClassIdAndExamTypeAndValidAndSchoolName(String classid, String examType,int valid,String schoolName);


    //年级总分累积和
    @Query(value = "SELECT SUM(coversion_total) FROM exam_coversion_total WHERE exam_type=?1 ", nativeQuery = true)
    float sumCoversionTotalByExamType(String examType);
    @Query(value = "SELECT SUM(coversion_total) FROM exam_coversion_total WHERE exam_type=?1 AND valid=?2 AND school_name=?3", nativeQuery = true)
    float sumCoversionTotalByExamTypeAndValidAndSchoolName(String examType,int valid,String schoolName);

    //总分的年级平均分
    @Query(value = "SELECT AVG(coversion_total) FROM exam_coversion_total WHERE exam_type=?1", nativeQuery = true)
    String totalAverageByExamType(String examType);

    @Query(value = "SELECT exam_type FROM exam_coversion_total WHERE school_name = ?1 GROUP BY exam_type", nativeQuery = true)
    List<String> getAllExamTypeBySchoolName(String schoolName);


    // 获取此用户的班级
    @Query(value = "SELECT class_id FROM exam_coversion_total WHERE (username=?1 OR student_number=?2) AND school_name=?3 AND exam_type=?4 AND valid=?5", nativeQuery = true)
    String getClassIdByStudentNameAndSchoolNameAndExamTypeAndValid(String username,String studentNumber,String schoolName, String examType,String valid);
}
