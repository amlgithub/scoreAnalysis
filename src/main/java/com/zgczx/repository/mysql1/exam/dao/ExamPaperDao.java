package com.zgczx.repository.mysql1.exam.dao;

import com.zgczx.repository.mysql1.exam.model.ExamPaper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.keyvalue.repository.config.QueryCreatorType;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 试卷表
 * @author aml
 * @date 2019/12/11 20:54
 */
@Repository
public interface ExamPaperDao extends JpaRepository<ExamPaper, Integer> {

    //1. 获取此试卷的所有信息，根据 试卷名称和科目
    ExamPaper findByExamNameAndSubjectAndValid(String examName, String subject,int deleted);

    @Query(value = "select * from e_exam_paper where exam_name=?1 and subject=?2 and valid=?3", nativeQuery = true)
    ExamPaper getBy(String examName, String subject,int deleted);

    //3. 获取所有的试卷信息，从而判断哪个属于章节练习，根据idList
    List<ExamPaper> findByIdIn(List idList);

    // 4. 获取所有考试名称(不包含专项练习或章节练习) lxj
    @Query(value = "select exam_name from e_exam_paper where exam_source='模拟考试' or exam_source='历年真题' ",nativeQuery = true)
    List<String> getExamName();

    // 5. 根据学科和年级查询考试名称和题目总数
    @Query(value = "select * from e_exam_paper where subject=?1 and grade_level=?2 ",nativeQuery = true)
    List<ExamPaper> getExamPaper(String subject,String levelName);
}
