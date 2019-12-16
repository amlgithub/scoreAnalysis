package com.zgczx.utils;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 *  map操作的一些公共方法
 * @author aml
 * @date 2019/9/18 16:04
 */
public class MapUtil {
    
    
    public static Map<String, String> relationOption(String[] strings){

        Map<String, String> map = new HashMap<>();
        map.put("a","A");
        map.put("b","B");
        map.put("c","C");
        map.put("d","D");
        return map;
    }
    //静态方法
    public static  Map<String, String> meal(){
        Map<String, String> mealid = new HashMap<>();
        //对应的k-v
        mealid.put("1", "早餐");
        mealid.put("2", "午餐");
        mealid.put("3", "晚餐");
        return mealid;
    }

    public static Map<String, String> dishclass(){
        Map<String, String> dishclassid = new HashMap<>();
        dishclassid.put("1", "套餐");
        dishclassid.put("2", "小炒");
        dishclassid.put("3", "盖饭");
        dishclassid.put("4", "面点");
        dishclassid.put("5", "小吃");
        dishclassid.put("6", "清真");
        dishclassid.put("7", "其他");
        return dishclassid;
    }

    public static Map<String, String> campus(){
        Map<String, String> campusid = new HashMap<>();
        //对应的k-v
        campusid.put("1", "校本部");
        campusid.put("2", "清华园校区");
        campusid.put("3", "双榆树校区");
        return campusid;
    }

    public static Map<Integer, String> role(){
        Map<Integer, String> roleid = new HashMap<>();
        //对应的k-v
        roleid.put(1, "老师");
        roleid.put(2, "学生");
        return roleid;
    }

}
