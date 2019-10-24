package com.zgczx.utils;

import com.zgczx.Application;
import com.zgczx.dataobject.score.ExamInfo;
import com.zgczx.repository.score.ExamInfoDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

/**
 * @author aml
 * @date 2019/10/24 13:33
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = DateFormatUtilTest.class)
public class DateFormatUtilTest {
    @Autowired
    static ExamInfoDao examInfoDao;
    @Test
    public void recoveryString() {
        String dateString = "19年4月期中";
        //1. 先用传来的参数，查库
        ExamInfo examName = examInfoDao.getByExamName(dateString);
        System.out.println(examName);
    }
}