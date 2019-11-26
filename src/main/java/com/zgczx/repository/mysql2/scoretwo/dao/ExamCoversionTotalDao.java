package com.zgczx.repository.mysql2.scoretwo.dao;

import com.zgczx.repository.mysql2.scoretwo.model.ExamCoversionTotal;
import org.apache.ibatis.annotations.Param;
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
     * 11.22，修改查询条件，加上@query，主要加上valid=1
     * 根据学号获取此学生所有成绩
     *
     * @param stuNumber 学号
     * @return ExamCoversionTotal实体对象
     */
    @Query(value = "select * from exam_coversion_total where student_number=?1 and exam_type=?2 and valid='1'", nativeQuery = true)
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

    //11.22 获取单科班级排名
    //ORDER BY ?4： 他是直接填充为 'yuwen_score', 查询就变成 order by 'yuwen_score'  我要的是 order by yuwen_score 这种的
    //目前这样查询的结果不对
    @Query(value = "SELECT * FROM exam_coversion_total WHERE school_name=?1 AND class_id=?2 AND exam_type=?3 AND valid=1 ORDER BY ?4 DESC", nativeQuery = true)
    List<ExamCoversionTotal> getAllSingleClassRank(String schoolName,String classid,String examName,@Param(value = "subject") String subject);

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
    @Query(value = "SELECT student_number,yuwen_score+shuxue_score+yingyu_score AS s FROM exam_coversion_total WHERE class_id=?1 AND exam_type=?2 AND school_name=?3 AND grade_name=?4 AND valid=1 ORDER BY s DESC", nativeQuery = true)
    List<String[]> findByClassIdAndExamType(String classid, String examType,String schoolName,String gradeName);

    //获取三科的年排
    @Query(value = "SELECT student_number,yuwen_score+shuxue_score+yingyu_score AS s FROM exam_coversion_total WHERE  exam_type=?1 AND school_name=?2 AND grade_name=?3 AND valid=1 ORDER BY s DESC", nativeQuery = true)
    List<String[]> findByClassIdAndExamTypeGrade(String examType,String schoolName,String gradeName);

    // 获取综合的班排
    @Query(value = "SELECT student_number,wuli_coversion+huaxue_coversion+shengwu_coversion+lishi_coversion+dili_coversion+zhengzhi_coversion  AS s FROM exam_coversion_total WHERE class_id=?1 AND exam_type=?2 AND school_name=?3 AND grade_name=?4 AND valid=1 ORDER BY s DESC", nativeQuery = true)
    List<String[]> findByClassIdAndExamTypeComplex(String classid, String examType,String schoolName,String gradeName);

    // 获取综合的年排
    @Query(value = "SELECT student_number,wuli_coversion+huaxue_coversion+shengwu_coversion+lishi_coversion+dili_coversion+zhengzhi_coversion  AS s FROM exam_coversion_total WHERE  exam_type=?1 AND school_name=?2 AND grade_name=?3 AND valid=1 ORDER BY s DESC", nativeQuery = true)
    List<String[]> findByClassIdAndExamTypeComplexGrade(String examType,String schoolName,String gradeName);

    @Query(value = "select e.coversionTotal from ExamCoversionTotal as e where e.classId = ?1 and e.examType = ?2 and e.schoolName=?3 and e.gradeName=?4")
    List<Double> getCoversionTotalByClassIdAndExamType(String classid, String examType,String schoolName,String gradeName);

    @Query(value = "select sum(sfs.yuwen+sfs.shuxue+sfs.yingyu+sfs.wuli+sfs.huaxue+sfs.shengwu+sfs.zhengzhi+sfs.lishi+sfs.dili)as total from subject_full_score sfs,exam_full_score_set sfss where sfs.id=sfss.subject_schame_id and sfss.examinfo_id=?1  ",nativeQuery = true)
    @Transactional
    BigInteger findSchametotal(int examid);



    /**
     *  语数英、物化生、政史地，九门科目的降序排列,年级排名
     *  年级排名：by schoolName、gradename
     * @param examType 具体考试名称
     * @return 返回单个数据库字段的所有值
     */
    @Query(value = "select yuwen_score from exam_coversion_total where exam_type=?1 AND school_name=?2 AND grade_name=?3 AND valid=1 order by yuwen_score desc", nativeQuery = true)
    List<String> findByYuwenScore(String examType,String schoolName,String gradeName);
    @Query(value = "select yuwen_score from exam_coversion_total where exam_type=?1 AND school_name=?2 AND valid=?3 AND grade_name=?4 order by yuwen_score desc", nativeQuery = true)
    List<String> findByYuwenScoreAndSchoolNameAndValid(String examType,String schoolName, int valid,String gradeName);
    // 求语文的班级排名list
    @Query(value = "select yuwen_score from exam_coversion_total where exam_type=?1 AND school_name=?2 AND valid=?3 AND class_id=?4 AND grade_name=?5 order by yuwen_score desc", nativeQuery = true)
    List<String> findByYuwenScoreAndSchoolNameAndValidAndClassId(String examType,String schoolName, int valid,String classid,String gradeName);

    @Query(value = "select shuxue_score from exam_coversion_total where exam_type=?1 AND school_name=?2 AND grade_name=?3 AND valid=1 order by shuxue_score desc", nativeQuery = true)
    List<String> findByShuxueScore(String examType,String schoolName,String gradeName);
    @Query(value = "select shuxue_score from exam_coversion_total where exam_type=?1 AND school_name=?2 AND valid=?3 AND grade_name=?4 order by shuxue_score desc", nativeQuery = true)
    List<String> findByShuxueScoreAndSchoolNameAndValid(String examType,String schoolName, int valid,String gradeName);
    // 求数学的班级排名list
    @Query(value = "select shuxue_score from exam_coversion_total where exam_type=?1 AND school_name=?2 AND valid=?3 AND class_id=?4 AND grade_name=?5 order by shuxue_score desc", nativeQuery = true)
    List<String> findByShuxueScoreAndSchoolNameAndValidAndClassId(String examType,String schoolName, int valid,String classid,String gradeName);

    @Query(value = "select yingyu_score from exam_coversion_total where exam_type=?1 AND school_name=?2 AND grade_name=?3 AND valid=1 order by yingyu_score desc", nativeQuery = true)
    List<String> findByYingyuScore(String examType,String schoolName,String gradeName);
    @Query(value = "select yingyu_score from exam_coversion_total where exam_type=?1 AND school_name=?2 AND valid=?3 AND grade_name=?4 order by yingyu_score desc", nativeQuery = true)
    List<String> findByYingyuScoreAndSchoolNameAndValid(String examType,String schoolName, int valid,String gradeName);
    // 求英语的班级排名list
    @Query(value = "select yingyu_score from exam_coversion_total where exam_type=?1 AND school_name=?2 AND valid=?3 AND class_id=?4 AND grade_name=?5 order by yingyu_score desc", nativeQuery = true)
    List<String> findByYingyuScoreAndSchoolNameAndValidAndClassId(String examType,String schoolName, int valid,String classid,String gradeName);


    @Query(value = "select wuli_coversion from exam_coversion_total where exam_type=?1 AND school_name=?2 AND grade_name=?3 AND valid=1 order by wuli_coversion desc", nativeQuery = true)
    List<String> findByWuliCoversion(String examType,String schoolName,String gradeName);
    @Query(value = "select wuli_coversion from exam_coversion_total where exam_type=?1 AND school_name=?2 AND valid=?3 AND grade_name=?4 order by wuli_coversion desc", nativeQuery = true)
    List<String> findByWuliCoversionAndSchoolNameAndValid(String examType,String schoolName, int valid,String gradeName);
    // 求物理的班级排名list
    @Query(value = "select wuli_coversion from exam_coversion_total where exam_type=?1 AND school_name=?2 AND valid=?3 AND class_id=?4 AND grade_name=?5 order by wuli_coversion desc", nativeQuery = true)
    List<String> findByWuliCoversionAndSchoolNameAndValidAndClassId(String examType,String schoolName, int valid,String classid,String gradeName);



    @Query(value = "select huaxue_coversion from exam_coversion_total where exam_type=?1 AND school_name=?2 AND grade_name=?3 AND valid=1 order by huaxue_coversion desc", nativeQuery = true)
    List<String> findByHuaxueCoversion(String examType,String schoolName,String gradeName);
    @Query(value = "select huaxue_coversion from exam_coversion_total where exam_type=?1 AND school_name=?2 AND valid=?3 AND grade_name=?4 order by huaxue_coversion desc", nativeQuery = true)
    List<String> findByHuaxueCoversionAndSchoolNameAndValid(String examType,String schoolName, int valid,String gradeName);
    // 求化学的班级排名list
    @Query(value = "select huaxue_coversion from exam_coversion_total where exam_type=?1 AND school_name=?2 AND valid=?3 AND class_id=?4 AND grade_name=?5 order by huaxue_coversion desc", nativeQuery = true)
    List<String> findByHuaxueCoversionAndSchoolNameAndValidAndClassId(String examType,String schoolName, int valid,String classid,String gradeName);


    @Query(value = "select shengwu_coversion from exam_coversion_total where exam_type=?1 AND school_name=?2 AND grade_name=?3 AND valid=1 order by shengwu_coversion desc", nativeQuery = true)
    List<String> findByShengwuCoversion(String examType,String schoolName,String gradeName);
    @Query(value = "select shengwu_coversion from exam_coversion_total where exam_type=?1 AND school_name=?2 AND valid=?3 AND grade_name=?4 order by shengwu_coversion desc", nativeQuery = true)
    List<String> findByShengwuCoversionAndSchoolNameAndValid(String examType,String schoolName, int valid,String gradeName);
    // 求生物的班级排名list
    @Query(value = "select shengwu_coversion from exam_coversion_total where exam_type=?1 AND school_name=?2 AND valid=?3 AND class_id=?4 AND grade_name=?5 order by shengwu_coversion desc", nativeQuery = true)
    List<String> findByShengwuCoversionAndSchoolNameAndValidAndClassId(String examType,String schoolName, int valid,String classid,String gradeName);


    @Query(value = "select lishi_coversion from exam_coversion_total where exam_type=?1 AND school_name=?2 AND grade_name=?3 AND valid=1 order by lishi_coversion desc", nativeQuery = true)
    List<String> findByLishiCoversion(String examType,String schoolName,String gradeName);
    @Query(value = "select lishi_coversion from exam_coversion_total where exam_type=?1 AND school_name=?2 AND valid=?3 AND grade_name=?4 order by lishi_coversion desc", nativeQuery = true)
    List<String> findByLishiCoversionAndSchoolNameAndValid(String examType,String schoolName, int valid,String gradeName);
    // 求历史的班级排名list
    @Query(value = "select lishi_coversion from exam_coversion_total where exam_type=?1 AND school_name=?2 AND valid=?3 AND class_id=?4 AND grade_name=?5 order by lishi_coversion desc", nativeQuery = true)
    List<String> findByLishiCoversionAndSchoolNameAndValidAndClassId(String examType,String schoolName, int valid,String classid,String gradeName);


    @Query(value = "select dili_coversion from exam_coversion_total where exam_type=?1 AND school_name=?2 AND grade_name=?3 AND valid=1 order by dili_coversion desc", nativeQuery = true)
    List<String> findByDiliCoversion(String examType,String schoolName,String gradeName);
    @Query(value = "select dili_coversion from exam_coversion_total where exam_type=?1 AND school_name=?2 AND valid=?3 AND grade_name=?4 order by dili_coversion desc", nativeQuery = true)
    List<String> findByDiliCoversionAndSchoolNameAndValid(String examType,String schoolName, int valid,String gradeName);
    // 求历史的班级排名list
    @Query(value = "select dili_coversion from exam_coversion_total where exam_type=?1 AND school_name=?2 AND valid=?3 AND class_id=?4 AND grade_name=?5 order by dili_coversion desc", nativeQuery = true)
    List<String> findByDiliCoversionAndSchoolNameAndValidAndClassId(String examType,String schoolName, int valid,String classid,String gradeName);


    @Query(value = "select zhengzhi_coversion from exam_coversion_total where exam_type=?1 AND school_name=?2 AND grade_name=?3 AND valid=1 order by zhengzhi_coversion desc", nativeQuery = true)
    List<String> findByZhengzhiCoversion(String examType,String schoolName,String gradeName);
    @Query(value = "select zhengzhi_coversion from exam_coversion_total where exam_type=?1 AND school_name=?2 AND valid=?3 AND grade_name=?4 order by zhengzhi_coversion desc", nativeQuery = true)
    List<String> findByZhengzhiCoversionAndSchoolNameAndValid(String examType,String schoolName, int valid,String gradeName);
    // 求历史的班级排名list
    @Query(value = "select zhengzhi_coversion from exam_coversion_total where exam_type=?1 AND school_name=?2 AND valid=?3 AND class_id=?4 AND grade_name=?5 order by zhengzhi_coversion desc", nativeQuery = true)
    List<String> findByZhengzhiCoversionAndSchoolNameAndValidAndClassId(String examType,String schoolName, int valid,String classid,String gradeName);

    // 年级总分排名； 获取总分的数组降序; 年级排名
    @Query(value = "SELECT coversion_total FROM exam_coversion_total WHERE exam_type=?1 AND school_name=?2 AND grade_name=?3 AND valid='1' ORDER BY coversion_total DESC", nativeQuery = true)
    List<String> findByTotalScore(String examType,String schoolName,String gradeName);
    //11.19年级总分排名； 此用户在某学校-某年级-某考试下的年级总分降序，用来获取年级排名
    @Query(value = "SELECT coversion_total FROM exam_coversion_total WHERE school_name=?1 AND grade_name=?2 AND exam_type=?3 AND valid='1' ORDER BY coversion_total DESC", nativeQuery = true)
    List<String> findAllBySchoolNameAndGradeNameAndExamType(String schoolName,String gradeName,String examName);

    //11.25 班级总分排名； 获取总分的数组降序; 班级排名
    @Query(value = "SELECT coversion_total AS s FROM exam_coversion_total WHERE class_id=?1 AND exam_type=?2 AND school_name=?3 AND grade_name=?4 AND valid=1 ORDER BY s DESC", nativeQuery = true)
    List<String[]> findClassTotalByClassIdAndExamType(String classid, String examType,String schoolName,String gradeName);

    /**
     *  语数英、物化生、政史地，九门科目的降序排列,班级排名
     * @param examType 具体考试名称
     * @return 返回单个数据库字段的所有值
     */
    @Query(value = "select yuwen_score from exam_coversion_total where class_id = ?1 and exam_type=?2 AND school_name=?3 AND grade_name=?4 AND valid='1' order by yuwen_score desc", nativeQuery = true)
    List<String> findByClassIdAndYuwenScore(String classid, String examType,String schoolName,String gradeName);
    @Query(value = "select shuxue_score from exam_coversion_total where class_id = ?1 and exam_type=?2 AND school_name=?3 AND grade_name=?4 AND valid='1' order by shuxue_score desc", nativeQuery = true)
    List<String> findByClassIdAndShuxueScore(String classid, String examType,String schoolName,String gradeName);
    @Query(value = "select yingyu_score from exam_coversion_total where class_id = ?1 and exam_type=?2 AND school_name=?3 AND grade_name=?4 AND valid='1' order by yingyu_score desc", nativeQuery = true)
    List<String> findByClassIdAndYingyuScore(String classid, String examType,String schoolName,String gradeName);
    @Query(value = "select wuli_coversion from exam_coversion_total where class_id = ?1 and exam_type=?2 AND school_name=?3 AND grade_name=?4 AND valid='1' order by wuli_coversion desc", nativeQuery = true)
    List<String> findByClassIdAndWuliCoversion(String classid, String examType,String schoolName,String gradeName);
    @Query(value = "select huaxue_coversion from exam_coversion_total where class_id = ?1 and exam_type=?2 AND school_name=?3 AND grade_name=?4 AND valid='1' order by huaxue_coversion desc", nativeQuery = true)
    List<String> findByClassIdAndHuaxueCoversion(String classid, String examType,String schoolName,String gradeName);
    @Query(value = "select shengwu_coversion from exam_coversion_total where class_id = ?1 and exam_type=?2 AND school_name=?3 AND grade_name=?4 AND valid='1' order by shengwu_coversion desc", nativeQuery = true)
    List<String> findByClassIdAndShengwuCoversion(String classid, String examType,String schoolName,String gradeName);
    @Query(value = "select lishi_coversion from exam_coversion_total where class_id = ?1 and exam_type=?2 AND school_name=?3 AND grade_name=?4 AND valid='1' order by lishi_coversion desc", nativeQuery = true)
    List<String> findByClassIdAndLishiCoversion(String classid, String examType,String schoolName,String gradeName);
    @Query(value = "select dili_coversion from exam_coversion_total where class_id = ?1 and exam_type=?2 AND school_name=?3 AND grade_name=?4 AND valid='1' order by dili_coversion desc", nativeQuery = true)
    List<String> findByClassIdAndDiliCoversion(String classid, String examType,String schoolName,String gradeName);
    @Query(value = "select zhengzhi_coversion from exam_coversion_total where class_id = ?1 and exam_type=?2 AND school_name=?3 AND grade_name=?4 AND valid='1' order by zhengzhi_coversion desc", nativeQuery = true)
    List<String> findByClassIdAndZhengzhiCoversion(String classid, String examType,String schoolName,String gradeName);

    // 获取此次考试的年级人数
    int countByExamTypeAndSchoolNameAndGradeNameAndValid(String examType,String schoolName,String gradeName,int valid);
    // 获取此次考试的年级人数,以valid = 1 的所有数据来统计
    int countByExamTypeAndValidAndSchoolNameAndGradeName(String examType,int valid,String schoolName,String gradeName);

    // 获取此次考试的班级人数
    int countByClassIdAndExamType(String classid, String examType);
    int countByClassIdAndExamTypeAndValidAndSchoolNameAndGradeName(String classid, String examType,int valid,String schoolName,String gradeName);

    // 获取选取物理的年级总人数，按照考试分数 > 0来计算，可能会有些误差
    @Query(value = "SELECT COUNT(*) FROM exam_coversion_total WHERE exam_type=?1 AND school_name=?2 AND grade_name=?3 AND valid=1 AND wuli_coversion > '0'", nativeQuery = true)
    int countByExamTypeAndWuli(String examType,String schoolName,String gradeName);
    // 获取选取化学的年级总人数，按照考试分数 > 0来计算，可能会有些误差
    @Query(value = "SELECT COUNT(*) FROM exam_coversion_total WHERE exam_type=?1 AND school_name=?2 AND grade_name=?3 AND valid=1 AND huaxue_coversion > '0'", nativeQuery = true)
    int countByExamTypeAndHuaxue(String examType,String schoolName,String gradeName);
    // 获取选取生物的年级总人数，按照考试分数 > 0来计算，可能会有些误差
    @Query(value = "SELECT COUNT(*) FROM exam_coversion_total WHERE exam_type=?1 AND school_name=?2 AND grade_name=?3 AND valid=1 AND shengwu_coversion > '0'", nativeQuery = true)
    int countByExamTypeAndShengwu(String examType,String schoolName,String gradeName);
    // 获取选取政治的年级总人数，按照考试分数 > 0来计算，可能会有些误差
    @Query(value = "SELECT COUNT(*) FROM exam_coversion_total WHERE exam_type=?1 AND school_name=?2 AND grade_name=?3 AND valid=1 AND zhengzhi_coversion > '0'", nativeQuery = true)
    int countByExamTypeAndZhegnzhi(String examType,String schoolName,String gradeName);
    // 获取选取历史的年级总人数，按照考试分数 > 0来计算，可能会有些误差
    @Query(value = "SELECT COUNT(*) FROM exam_coversion_total WHERE exam_type=?1 AND school_name=?2 AND grade_name=?3 AND valid=1 AND lishi_coversion > '0'", nativeQuery = true)
    int countByExamTypeAndLishi(String examType,String schoolName,String gradeName);
    // 获取选取地理的年级总人数，按照考试分数 > 0来计算，可能会有些误差
    @Query(value = "SELECT COUNT(*) FROM exam_coversion_total WHERE exam_type=?1 AND school_name=?2 AND grade_name=?3 AND valid=1 AND dili_coversion > '0'", nativeQuery = true)
    int countByExamTypeAndDili(String examType,String schoolName,String gradeName);





    // 班级总分累加和
//    @Query(value = "SELECT SUM(coversion_total) FROM exam_coversion_total WHERE class_id=?1 AND exam_type=?2 ", nativeQuery = true)
//    float sumCoversionTotalByClassIdAndExamType(String classid, String examType);
    @Query(value = "SELECT SUM(coversion_total) FROM exam_coversion_total WHERE class_id=?1 AND exam_type=?2 AND valid=?3 AND school_name=?4 AND grade_name=?5", nativeQuery = true)
    float sumCoversionTotalByClassIdAndExamTypeAndValidAndSchoolName(String classid, String examType,int valid,String schoolName,String gradeName);


    //年级总分累积和
    @Query(value = "SELECT SUM(coversion_total) FROM exam_coversion_total WHERE exam_type=?1 ", nativeQuery = true)
    float sumCoversionTotalByExamType(String examType);
    @Query(value = "SELECT SUM(coversion_total) FROM exam_coversion_total WHERE exam_type=?1 AND valid=?2 AND school_name=?3 AND grade_name=?4", nativeQuery = true)
    float sumCoversionTotalByExamTypeAndValidAndSchoolName(String examType,int valid,String schoolName,String gradeName);

    //总分的年级平均分
    @Query(value = "SELECT AVG(coversion_total) FROM exam_coversion_total WHERE exam_type=?1 AND school_name=?2 AND grade_name=?3 AND valid='1' ", nativeQuery = true)
    String totalAverageByExamType(String examType,String schoolName,String gradeName);

    @Query(value = "SELECT exam_type FROM exam_coversion_total WHERE school_name = ?1 GROUP BY exam_type", nativeQuery = true)
    List<String> getAllExamTypeBySchoolName(String schoolName);


    // 获取此用户的班级
    @Query(value = "SELECT class_id FROM exam_coversion_total WHERE (username=?1 OR student_number=?2) AND school_name=?3 AND exam_type=?4 AND valid=?5 AND grade_name=?6", nativeQuery = true)
    String getClassIdByStudentNameAndSchoolNameAndExamTypeAndValid(String username,String studentNumber,String schoolName, String examType,String valid,String gradeName);

    //学科贡献率时，获取此学生的所有考试名称
    @Query(value = "SELECT exam_type FROM exam_coversion_total WHERE student_number=?1 AND school_name=?2 AND grade_name=?3 AND class_id=?4 AND valid=?5", nativeQuery = true)
    List<String> getAllExamNameByStudentNumberAndSchoolNameAndGradeNameAndClassIdAndValid(String studentNumber,String schoolName,String gradeName,String classid,int valid);
}
