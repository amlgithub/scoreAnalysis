package com.zgczx.utils;

import com.github.binarywang.java.emoji.EmojiConverter;

/**
 * @author aml
 * @date 2019/7/25 17:10
 */
public class EmojiUtil {

    private static EmojiConverter emojiConverter = EmojiConverter.getInstance();

    /**
     * 将emojiStr 转为 带有表情的字符，将数据库中的字符还原(带有表情)并展示给前端
     * @param emojiStr  原始数据（带有表情）
     * @return
     */
    public static String emojiConverterUnicodeStr(String emojiStr){
        return emojiConverter.toUnicode(emojiStr);
    }

    /**
     * 将带有表情的字符串转换为编码存入数据库中
     * @param str 带有表情的字符
     * @return
     */
    public static String emojiConverterToAlias(String str){
        return emojiConverter.toAlias(str);
    }

}
