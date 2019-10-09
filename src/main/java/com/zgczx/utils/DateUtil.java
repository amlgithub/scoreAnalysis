package com.zgczx.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * @author aml
 * @date 2019/9/27 19:37
 */
public class DateUtil {


    public static String getNowTime(){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(calendar.getTime());
    }
}
