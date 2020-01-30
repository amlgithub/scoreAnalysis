package com.zgczx.repository.mysql1.exam.dao;

import com.zgczx.repository.mysql1.exam.model.ExamContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author aml
 * @date 2019/12/19 15:10
 */
@Repository
public interface ExamContentDao extends JpaRepository<ExamContent, Integer> {

    ExamContent findByExamNameAndSubject(String examName, String subject);
}
