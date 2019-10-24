package com.zgczx.test;

import com.zgczx.dataobject.score.ExamInfo;
import com.zgczx.repository.score.ExamInfoDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.*;

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
        System.out.println(examName);

    }

    @Test
    public void getByExamName() {
    }
}