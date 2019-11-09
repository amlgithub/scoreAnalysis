package com.zgczx.utils;

import com.zgczx.repository.mysql1.score.model.ExamInfo;
import com.zgczx.repository.mysql1.score.dao.ExamInfoDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author aml
 * @date 2019/10/24 13:33
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class DateFormatUtilTest {
    @Autowired
    ExamInfoDao examInfoDao;

    @Test
    public void recoveryString() {
        String dateString = "2019年010月月考";
        //1. 先用传来的参数，查库
        ExamInfo examName = examInfoDao.getByExamName(dateString);
        System.out.println(examName);
        if (examName != null) {
            if (examName.getExamName().equals(dateString)) {
                System.out.println("查出来了： " + dateString);
//            return dateString;
            }
        }
        //3. 查不出
        dateString = dateString.substring(2, dateString.length());
        ExamInfo examName1 = examInfoDao.getByExamName(dateString);
        if (examName1 != null) {
            if (dateString.equals(examName1.getExamName())) {
                System.out.println(dateString);

            }
        }

        dateString = dateString.replaceFirst("0", "");

        System.out.println("替换0的个数： " + dateString);

    }
}