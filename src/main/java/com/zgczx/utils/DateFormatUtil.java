package com.zgczx.utils;

import com.zgczx.dataobject.score.ExamInfo;
import com.zgczx.repository.score.ExamInfoDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日期格式化工具
 * 将考试名称中的日期格式化为: xxxx年xx月
 * @author aml
 * @date 2019/10/24 10:56
 */
@Slf4j
public class DateFormatUtil {

    @Autowired
    static ExamInfoDao examInfoDao;

    public static String dateFormat(String dateString) throws Exception{

        int year = dateString.indexOf("年");
        if (year == 2){
            dateString = "20"+dateString;
        }else if (year == 3){
            dateString = "2" +dateString;
        }
        int c = dateString.indexOf("月");
        String substring = dateString.substring(c+1,dateString.length());
        dateString = dateString.substring(0, c + 1);
        System.out.println("输出1："+substring);
        System.out.println("输出2："+dateString);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月");
        String format = null;
        if (dateString.indexOf("年") != -1) {
            Date parse = null;

            parse = dateFormat.parse(dateString);
            format = dateFormat.format(parse);
        }

            System.out.println("s:" + format);

        System.out.println("完整:" + format+substring);
        return format+substring;
    }


    public static String recoveryString(String dateString){
       //1. 先用传来的参数，查库
        ExamInfo examName = examInfoDao.getByExamName(dateString);
        System.out.println(examName);
        //2. 查出
        if (examName.equals(dateString)){
            return dateString;
        }else {
            //3. 查不出
            dateString = dateString.substring(2,dateString.length());


            return null;
        }
    }



    public static void main(String[] args)throws Exception{
       String s = "2019年4月月考期末";
        recoveryString("19年4月期中");
//       dateFormat(s);
    }

}
