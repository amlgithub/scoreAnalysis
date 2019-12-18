package com.zgczx.repository.mysql1.exam.dao;

import com.zgczx.repository.mysql1.exam.model.ExamPaper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

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

}
