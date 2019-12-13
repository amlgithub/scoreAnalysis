package com.zgczx.repository.mysql1.exam.dao;

import com.zgczx.repository.mysql1.exam.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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
}