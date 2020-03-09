package com.zgczx.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则匹配string中所需要的内容
 *
 * @author aml
 * @date 2019/12/12 11:00
 */
public class FilterStringUtil {


    /**
     * @param str
     * @return
     * @Title : filterNumber
     * @Type : FilterStr
     * @date : 2014年3月12日 下午7:23:03
     * @Description : 过滤出数字
     */
    public static String filterNumber(String number) {
        number = number.replaceAll("[^(0-9)]", "");
        return number;
    }

    /**
     * @param alph
     * @return
     * @Title : filterAlphabet
     * @Type : FilterStr
     * @date : 2014年3月12日 下午7:28:54
     * @Description : 过滤出字母
     */
    public static String filterAlphabet(String alph) {
        alph = alph.replaceAll("[^(A-Za-z)]", "");
        return alph;
    }

    public static String filterAlphabetCapital(String alph) {
        alph = alph.replaceAll("[^(A-Z)]", "");
        return alph;
    }

    // 去除 []
    public static String filterMiddleBrackets(String alph) {
        alph = alph.replaceAll("\\[|\\]|\\s", "");
        return alph;
    }

    // 1)去除 指定的字符串,切记 要加上()；里面写要匹配的 字符串内容
    public static String filter1(String alph) {
        alph = alph.replaceAll("(\\},\"randomOption\":\\[)", ",\"randomOption\":");
        return alph;
    }

    // 2)去除 指定的字符串,切记 要加上()；里面写要匹配的 字符串内容
    public static String filter2(String alph) {
        alph = alph.replaceAll("(\\],\"rightOption\":)", ",\"rightOption\":");
        return alph;
    }
    //3) 去除 指定的字符串,切记 要加上()；里面写要匹配的 字符串内容
    public static String filter3(String alph) {
        String b = alph.replaceAll("(\",\"B)", "B");
        String c = b.replaceAll("(\",\"C)", "C");
        String d = c.replaceAll("(\",\"D)", "D");
        return d;
    }

    // 去掉 大括号
    public static String braces(String alph) {
        alph = alph.replaceAll("(\\{|\\}|\")", "");
        return alph;
    }

    /**
     * 专门用于 获取题的接口，筛选选项中的 特殊字符
     *  // 去除\s,\t,\n 和 t ,n 等符号
     * @param alph
     * @return
     */
    public static String filterspecial(String alph) {
//        Pattern p = Pattern.compile("\\s*|\\t|\\r|\\n");
        Pattern p = Pattern.compile("\\\\s*|\\t|\\r|\\n|t|n");
        Matcher m = p.matcher(alph);
        boolean b = m.find();
        String s = null;
        if (b == true){
            s = m.replaceAll("");
        }else {
            s = alph;
        }


       // alph = alph.replaceAll("\\s*|\\t|\\r|\\n", "");
        return s;
    }
    public static String filterspecial2(String alph) {
//        Pattern p = Pattern.compile("\\s*|\\t|\\r|\\n");
        Pattern p = Pattern.compile("\\\\s*|\\t|\\r|\\n");
        Matcher m = p.matcher(alph);
        boolean b = m.find();
        String s = null;
        if (b == true){
            s = m.replaceAll("");
        }else {
            s = alph;
        }


        // alph = alph.replaceAll("\\s*|\\t|\\r|\\n", "");
        return s;
    }
    /**
     * @param chin
     * @return
     * @Title : filterChinese
     * @Type : FilterStr
     * @date : 2014年3月12日 下午9:12:37
     * @Description : 过滤出中文
     */
    public static String filterChinese(String chin) {
        chin = chin.replaceAll("[^(\\u4e00-\\u9fa5)]", "");
        return chin;
    }

    /**
     * @param character
     * @return
     * @Title : filter
     * @Type : FilterStr
     * @date : 2014年3月12日 下午9:17:22
     * @Description : 过滤出字母、数字和中文
     */
    public static String filter(String character) {
        character = character.replaceAll("[^(a-zA-Z0-9\\u4e00-\\u9fa5)]", "");
        return character;
    }

    public static String optionLetter(String character) {
        character = character.replaceAll("A．|B．|C．|D．|E．|A.|B.|C.|D.|E.", "");
        return character;
    }

    // 只留中文和数字 还有 逗号,
    public static String filterChineseAndMath(String character) {
        character = character.replaceAll("[^(0-9\\u4e00-\\u9fa5)|\\,]", "");
        return character;
    }


    // 去除题的 题号和点，只留题的内容; 最大题号为 10000
    public static String filterTitleNumber(String string){
        //以数字开头并且包含.表示一个新的题目开始
        String regex = "^\\d{1,10000}\\．";
        Pattern pattern = Pattern.compile(regex);
        Matcher m = pattern.matcher(string);
        if (m.find()){
            String string1 = m.replaceAll("");
            return string1;
        }else {
         /*   //以数字开头并且包含.表示一个新的题目开始
            String regex1 = "^\\d{1,100}\\.";
            Pattern pattern1 = Pattern.compile(regex1);
            Matcher m1 = pattern.matcher(string);
            if (m1.find()){
                String string1 = m1.replaceAll("");
                return string1;
            }*/
            string = string.replaceAll("^\\d{1,10000}\\.", "");
            return string;
        }
    }
    /**
     * 去除字符串中头部和尾部所包含的空格（包括:空格(全角，半角)、制表符、换页符等）
     * @param s
     * @return
     */
    public static String trim(String s){
        String result = "";
        if(null!=s && !"".equals(s)){
            result = s.replaceAll("^[　*| *| *|//s*]*", "").replaceAll("[　*| *| *|//s*]*$", "");
        }
        return result;
    }
    /**
     * @param args
     * @Title : main
     * @Type : FilterStr
     * @date : 2014年3月12日 下午7:18:22
     * @Description :
     */
    public static void main(String[] args) {
//        /**
//         * 声明字符串you
//         */
//        String you = "^&^&^you123$%$%你好";
//        /**
//         * 调用过滤出数字的方法
//         */
//        you = filterNumber(you);
//        /**
//         * 打印结果
//         */
//        System.out.println("过滤出数字：" + you);
//
//        /**
//         * 声明字符串hai
//         */
//        String hai = "￥%……4556ahihdjsadhj$%$%你好吗wewewe";
//        /**
//         * 调用过滤出字母的方法
//         */
//        hai = filterAlphabet(hai);
//        /**
//         * 打印结果
//         */
//        System.out.println("过滤出字母：" + hai);
//
//        /**
//         * 声明字符串dong
//         */
//        String dong = "$%$%$张三34584yuojk李四@#￥#%%￥……%&";
//        /**
//         * 调用过滤出中文的方法
//         */
//        dong = filterChinese(dong);
//        /**
//         * 打印结果
//         */
//        System.out.println("过滤出中文：" + dong);
//
//        /**
//         * 声明字符串str
//         */
//        String str = "$%$%$张三34584yuojk李四@#￥#%%￥……%&";
//        /**
//         * 调用过滤出字母、数字和中文的方法
//         */
//        str = filter(str);
//        /**
//         * 打印结果
//         */
//        System.out.println("过滤出字母、数字和中文：" + str);
//
//
//        String s = filterMiddleBrackets("[1, 2, 3, 4, 5, 6, 7, 8, 9, 10]");
//        System.out.println("sssssssssssssssssssss:   " + s);
//
//        String s1 = "A．一定的流动性\\t\\t\\t\\t\\t\\t\\t\\tB．选择透性\\nC．较大的稳定性\\t\\t\\t\\t\\t\\t\\t\\tD．运输物质的功能\\n";
//        String filterspecial = filterspecial(s1);
//        System.out.println(filterspecial);
//
//        String s2 = optionLetter("A．C．B．D．E.一定的流动性");
//        System.out.println("s2:   "+ s2);

//        String s3 = filter1("},\"randomOption\":[");
//        System.out.println("ddd: "+s3);
//
//        String s4 = filter2("],\"rightOption\":");
//        System.out.println("444: "+s4);

     /*   String braces = braces("{\"0\":\"\",\"1\":\"\",\"2\":\"\",\"3\":\"\",\"4\":\"\",\"5\":\"\",\"6\":\"\",\"7\":\"\",\"8\":\"\",\"9\":\"\"}");
        System.out.println(braces);

        String string = filterChineseAndMath("第3章细胞的基本结构,第1节细胞膜的结构和功能");
        System.out.println(string);*/

//        String string1 = filterTitleNumber("1．科学家研究发现，当温度升高到一定程度时，细胞膜的厚度变薄且面积增大。说明（　　）");
//        System.out.println(string1);

        String string2 = filterTitleNumber("2.以下关于细胞膜结构的叙述中，不正确的是（　　）");
        System.out.println(string2);


    }


}
