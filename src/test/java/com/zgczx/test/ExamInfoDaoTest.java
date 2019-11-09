package com.zgczx.test;

import com.zgczx.repository.mysql1.score.model.ExamInfo;
import com.zgczx.repository.mysql1.score.dao.ExamInfoDao;
import com.zgczx.repository.mysql2.scoretwo.dao.ExamCoversionTotalDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * 单元测试Junit步骤
 * @author aml
 * @date 2019/10/24 13:54
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ExamInfoDaoTest {
    @Autowired
    ExamInfoDao examInfoDao;

    @Autowired
    ExamCoversionTotalDao examCoversionTotalDao;
    @Test
    public void findByExamName() {
        String dateString = "19年4月期中";
        //1. 先用传来的参数，查库
//        int byExamName = examInfoDao.findByExamName(dateString);
//        System.out.println(byExamName);
//        List<ExamInfo> one = examInfoDao.findAll();
//        System.out.println(one);
//        int byExamName = examInfoDao.findByExamName(dateString);
//        System.out.println("查询出来的id为： "+byExamName);

        ExamInfo examName = examInfoDao.getByExamName(dateString);
        ExampleMatcher matching = ExampleMatcher.matching();
        examCoversionTotalDao.findAll((Iterable<Integer>) matching);
        String s = "exam_type";
        String s1 = "2019年3月考试";
////        List<String> byYuwenScore = examCoversionTotalDao.findByYuwenScore(s, s1);
//
//        System.out.println(byYuwenScore);

    }

    @Test
    public void getByExamName() {
    }
}