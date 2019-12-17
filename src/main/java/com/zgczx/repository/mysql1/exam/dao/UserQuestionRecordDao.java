package com.zgczx.repository.mysql1.exam.dao;

import com.zgczx.repository.mysql1.exam.model.UserQuestionRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 用户做题记录表
 * @author aml
 * @date 2019/12/16 15:11
 */
@Repository
public interface UserQuestionRecordDao extends JpaRepository<UserQuestionRecord, Integer> {
}
