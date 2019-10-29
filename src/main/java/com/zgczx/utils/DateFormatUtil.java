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
 *
 * @author aml
 * @date 2019/10/24 10:56
 */
@Slf4j
public class DateFormatUtil {

    @Autowired
    ExamInfoDao examInfoDao;

    /**
     * 将库中不规范的考试名称封装为正确规范格式
     * @param dateString 数据库中的考试名称
     * @return
     * @throws Exception
     */
    public String dateFormat(String dateString) throws Exception {
        log.info("原始dataString字符串: {}", dateString);
        int year = dateString.indexOf("年");
        if (year == 4){
            return dateString;
        }
        if (year == 2) {
            dateString = "20" + dateString;
        } else if (year == 3) {
            dateString = "2" + dateString;
        }
        int c = dateString.indexOf("月");
        String substring = dateString.substring(c + 1, dateString.length());
        dateString = dateString.substring(0, c + 1);

        log.info("截取的前部分字符中： {}", dateString);
        log.info("截取的后部分字符中： {}", substring);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月");
        String format = null;
        if (dateString.indexOf("年") != -1) {
            Date parse = null;
            parse = dateFormat.parse(dateString);
            format = dateFormat.format(parse);
        }
        String standardFormat = format+substring;
        log.info("正确格式的字符串: {}",standardFormat );

        return standardFormat;
    }


    /**
     * 将前端传来的正确规范的考试名称，拆分为数据库中的考试名称，用于真正调用接口
     * @param dateString 规范的考试名称格式
     * @return
     */
    public String recoveryString(String dateString) {
        //1. 先用传来的参数，查库
        ExamInfo examName = examInfoDao.getByExamName(dateString);
        System.out.println(examName);

        if (examName != null) {
            //2. 查出
            if (examName.getExamName().equals(dateString)) {
                System.out.println("查出来了： " + dateString);
                return dateString;
            }
        }
        //3. 查不出
        dateString = dateString.substring(2, dateString.length());
        //3.2 用截取过后的字符串，再查库
        ExamInfo examName1 = examInfoDao.getByExamName(dateString);
        if (examName1 != null) {
            if (dateString.equals(examName1.getExamName())) {
                System.out.println(dateString);
                return dateString;
            }
        }
        //替换字符串中的第一个0,
        dateString = dateString.replaceFirst("0", "");
        return dateString;
    }

}
