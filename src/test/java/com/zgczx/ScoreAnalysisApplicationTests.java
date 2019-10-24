package com.zgczx;

import com.zgczx.dataobject.score.ExamInfo;
import com.zgczx.repository.score.ExamInfoDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ScoreAnalysisApplicationTests {
    @Autowired
    ExamInfoDao examInfoDao;
    @Test
    public void contextLoads() {
//        String dateString = "19年4月期中";
//        //1. 先用传来的参数，查库
////        ExamInfo examName = examInfoDao.getByExamName(dateString);
//        int byExamName = examInfoDao.findByExamName(dateString);
//        System.out.println(byExamName);
////        System.out.println(examName);

        List<ExamInfo> one = examInfoDao.findAll();
        System.out.println(one);
    }

}
