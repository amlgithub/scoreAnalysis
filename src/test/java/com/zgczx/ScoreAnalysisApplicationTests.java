package com.zgczx;

import com.zgczx.mapper.ManuallyEnterGradesMapper;
import com.zgczx.repository.mysql1.score.dao.ManuallyEnterGradesDao;
import com.zgczx.repository.mysql1.score.model.ExamInfo;
import com.zgczx.repository.mysql1.score.dao.ExamInfoDao;
import com.zgczx.repository.mysql1.score.model.ManuallyEnterGrades;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

//使用MapperScan批量扫描所有的Mapper接口；
@MapperScan(value = "com.zgczx.mapper")
@RunWith(SpringRunner.class)
@SpringBootTest
public class ScoreAnalysisApplicationTests {
    @Autowired
    ExamInfoDao examInfoDao;

    @Autowired
    DataSource dataSource;

    // 用mybatis写的接口方法，查询方法
    @Autowired
    ManuallyEnterGradesMapper manuallyEnterGradesMapper;

    @Autowired
    ManuallyEnterGradesDao manuallyEnterGradesDao;

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

    @Test
    public void contextLoads1()throws SQLException {
        System.out.println(dataSource.getClass());

        Connection connection = dataSource.getConnection();
        System.out.println(connection);
        connection.close();

    }

    @Test
    public void getManuallyEnterGrades(){

        ManuallyEnterGrades manuallyEnterGradesById = manuallyEnterGradesMapper.getManuallyEnterGradesById(16);
        System.out.println(manuallyEnterGradesById);
        List<ManuallyEnterGrades> manuallyEnterGrades = manuallyEnterGradesMapper.getManuallyEnterGrades();
        System.out.println(manuallyEnterGrades);

    }
    @Test
    public void getAllByOpenid(){
        List<String> stringList = new ArrayList<>();
        stringList.add("111");
        stringList.add("121");
        List<ManuallyEnterGrades> byWechatOpenidIn = manuallyEnterGradesDao.findByWechatOpenidIn(stringList);
        System.out.println(byWechatOpenidIn);

        List<ManuallyEnterGrades> byWechatOpenidInAndStudentNumber = manuallyEnterGradesDao.findByWechatOpenidInAndStudentNumber(stringList, "111");
        System.out.println(byWechatOpenidInAndStudentNumber);

    }

}
