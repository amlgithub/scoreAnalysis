package com.zgczx.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 字符串的全排列
 *
 * @author aml
 * @date 2019/12/15 18:22
 */
@Slf4j
public class RecursionTreeUtil {

    // 定义一个 全局变量，来存放递归打印的 结果
    public static List<String> list = new ArrayList<>();

    /**
     * @param s    字符串
     * @param from 开始下标
     * @param to   结束下标
     */
    public static void permutation(char[] s, int from, int to) {
        List<char[]> list = new ArrayList<>();
        if (to <= 1)
            return;
        if (from == to) {
            System.out.println(s);
        } else {
            for (int i = from; i <= to; i++) {
                swap(s, i, from); //交换前缀，使其产生下一个前缀
                permutation(s, from + 1, to);
                swap(s, from, i); //将前缀换回，继续做上一个前缀的排列
            }
        }

    }

    public static void swap(char[] s, int i, int j) {
        char tmp = s[i];
        s[i] = s[j];
        s[j] = tmp;
    }


    public static void permute(int[] array, int start) {

        if (start == array.length) {  // 输出
            System.out.println(Arrays.toString(array));

//            list.add(Arrays.toString(array));// 全局变量接收 递归结果
        } else {
            for (int i = start; i < array.length; ++i) {
                swap(array, start, i);  //  交换元素
                permute(array, start + 1);  //交换后，再进行全排列算法
                swap(array, start, i);  //还原成原来的数组，便于下一次的全排列

            }
        }

    }

    /**
     * #19.12.16
     * 让每道题选项随机的 函数，但这个方法不太好
     *
     * @param array 有几个选项
     * @param start 从 0 开始
     * @return
     */
    public static int[] randomSort(int[] array, int start) {

        int random = (int) (Math.random() * (array.length - 1) + 1);
        swap(array, start, random);  //  交换元素
        //  System.out.println(Arrays.toString(array));
        log.info("【输出此次选项的顺序：】{}", Arrays.toString(array));
        return array;
    }

    private static void swap(int[] array, int s, int i) {
        int t = array[s];
        array[s] = array[i];
        array[i] = t;
    }


    public static void main(String[] args) {
//            char[] s = {'a','b','c','d'};
//            permutation(s, 0, 3);

        int[] array = new int[]{1, 2, 3, 4};
//        randomSort(array, 0);

        permute(array, 0);


    }


}