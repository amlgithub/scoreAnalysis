package com.zgczx.utils;

import java.util.HashMap;
import java.util.Map;

import static com.zgczx.utils.FilterStringUtil.braces;

/**
 * 将 json（k-v）格式的string，转为map
 *
 * @author aml
 * @date 2019/12/24 14:28
 */
public class StringToMapUtil {

    public Map<String, String> stringToMap(String mapText) {
        if (mapText == null || mapText.equals("")) {
            return null;
        }
        Map<String, String> map = new HashMap<String, String>();
        //1. 首先 先去掉 大括号
        String braces = braces(mapText);
        String[] split = braces.split(",");
        for (String s : split){
            int i = s.indexOf(":");
            String key = s.substring(0, i);
            String value = s.substring(i + 1, s.length());
            map.put(key,value);
        }
        return map;
    }
}
