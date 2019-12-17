package com.zgczx.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * int[] 的全排列， int[]可以改为 char[]
 * 并且使用了 一个 全局变量 接收 递归结果
 * 可以在其他类中使用
 * @author aml
 * @date 2019/12/17 10:17
 */
public class FullPermutationUtil {

    // 定义一个 全局变量，来存放递归打印的 结果
//    public static List<String> list = new ArrayList<>(); 定义为list的变量名的话，有问题，多放进去了两个 int[]的值
    public static List<String> l = new ArrayList<>();

    public static void permute2(int[] array, int start) {
        //System.out.println(l.size());

        if (start == array.length) {  // 输出
            System.out.println(Arrays.toString(array));
            l.add(Arrays.toString(array));
        } else {
            for (int i = start; i < array.length; ++i) {
                swap(array, start, i);  //  交换元素
                permute2(array, start + 1);  //交换后，再进行全排列算法
                swap(array, start, i);  //还原成原来的数组，便于下一次的全排列
            }
        }
    }
    private static void swap(int[] array, int s, int i) {
        int t = array[s];
        array[s] = array[i];
        array[i] = t;
    }
    public static void main(String[] args) {
        int[] arrays = new int[]{1, 2, 3, 4};
        permute2(arrays, 0);
        System.out.println(l.size());
        System.out.println(l);
    }
}
