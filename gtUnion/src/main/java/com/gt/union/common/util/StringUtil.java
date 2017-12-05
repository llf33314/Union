package com.gt.union.common.util;

import java.util.regex.Pattern;

/**
 * 字符串工具类
 *
 * @author linweicong
 * @version 2017-11-22 17:45:00
 */
public class StringUtil {
    private static String PHONE_PATTERN = "^1[3|4|5|6|7|8][0-9][0-9]{8}$";
    private static String EMAIL_PATTERN = "^[A-Za-z0-9\\u4e00-\\u9fa5]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";

    /**
     * 判断字符串是否为空，如为空，则返回true，否则返回false
     *
     * @param str String
     * @return boolean
     */
    public static boolean isEmpty(String str) {
        return str == null || "".equals(str.trim());
    }

    /**
     * 判断字符串是否不为空，如不为空，则返回true，否则返回false
     *
     * @param str String
     * @return boolean
     */
    public static boolean isNotEmpty(String str) {
        return str != null && !"".equals(str.trim());
    }


    /**
     * 判断字符串中英文的长度
     *
     * @param str String
     * @return double
     */
    public static double getStringLength(String str) {
        if (CommonUtil.isEmpty(str)) {
            return 0;
        }
        double valueLength = 0;
        // 获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1
        String chinese = "[\u4e00-\u9fa5]";
        for (int i = 0; i < str.length(); i++) {
            // 获取一个字符
            String temp = str.substring(i, i + 1);
            // 判断是否为中文字符
            if (temp.matches(chinese)) {
                // 中文字符长度为1
                valueLength += 1;
            } else {
                // 其他字符长度为0.5
                valueLength += 0.5;
            }
        }       //进位取整
        return Math.ceil(valueLength);
    }

    /**
     * 正则判断是否是手机号
     *
     * @param str 要判断的字符串
     * @return boolean
     */
    public static boolean isPhone(String str) {
        return Pattern.matches(PHONE_PATTERN, str);
    }

    /**
     * 正则判断是否是邮箱
     *
     * @param str 要判断的字符串
     * @return boolean
     */
    public static boolean isEmail(String str) {
        return Pattern.matches(EMAIL_PATTERN, str);
    }
}
