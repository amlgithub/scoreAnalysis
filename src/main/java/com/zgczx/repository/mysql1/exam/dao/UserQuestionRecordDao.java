package com.zgczx.repository.mysql1.exam.dao;

import com.zgczx.repository.mysql1.exam.model.UserQuestionRecord;
import org.springframework.data.jpa.repository.JpaRepository;
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
    List<UserQuestionRecord> getByStudentNumberAndSubjectAndExamPaperId(String studentNumber,String subject,int paperId);
}
