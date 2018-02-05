package com.gt.union.common.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * Double类型工具类
 *
 * @author linweicong
 * @version 2017-11-22 17:45:00
 */
public class DoubleUtil {
    public static final String PERCENT_PATTERN = "0.00%";

    /**
     * 判断num中的小数精度
     *
     * @param num     Double
     * @param decimal int
     * @return boolean
     */
    public static boolean checkDecimalPrecision(Double num, int decimal) {
        num = num == null ? Double.valueOf(0.0) : num;
        String strNum = BigDecimal.valueOf(num).toString();
        int index = strNum.indexOf(".");
        return !((index != -1 ? strNum.substring(index + 1).length() : 0) > decimal);
    }

    /**
     * 判断num中的小数精度
     *
     * @param num     double
     * @param decimal int
     * @return boolean
     */
    public static boolean checkDecimalPrecision(double num, int decimal) {
        return checkDecimalPrecision(Double.valueOf(num), decimal);
    }

    /**
     * 判断num中的整数精度
     *
     * @param num     Double
     * @param integer int
     * @return boolean
     */
    public static boolean checkIntegerPrecision(Double num, int integer) {
        num = num == null ? Double.valueOf(0) : num;
        String strNum = BigDecimal.valueOf(num).toString();
        int index = strNum.indexOf(".");
        return !(strNum.substring(index != -1 ? index : 0).length() > integer);
    }

    /**
     * 判断num中的整数精度
     *
     * @param num     double
     * @param integer int
     * @return boolean
     */
    public static boolean checkIntegerPrecision(double num, int integer) {
        return checkIntegerPrecision(Double.valueOf(num), integer);
    }

    /**
     * 判断整数和小数精度
     *
     * @param num     Double
     * @param integer int
     * @param decimal int
     * @return boolean
     */
    public static boolean checkPrecision(Double num, int integer, int decimal) {
        return checkIntegerPrecision(num, integer) && checkDecimalPrecision(num, decimal);
    }

    /**
     * 判断整数和小数精度
     *
     * @param num     double
     * @param integer int
     * @param decimal int
     * @return boolean
     */
    public static boolean checkPrecision(double num, int integer, int decimal) {
        return checkPrecision(Double.valueOf(num), integer, decimal);
    }

    /**
     * 将Double类型转成百分比形式
     *
     * @param d Double
     * @return String
     */
    public static String formatPercent(Double d) {
        return formatPercent(d, PERCENT_PATTERN);
    }

    /**
     * 将Double类型转成百分比形式
     *
     * @param d double
     * @return String
     */
    public static String formatPercent(double d) {
        return formatPercent(Double.valueOf(d));
    }

    /**
     * 将Double类型转成百分比形式
     *
     * @param d       double
     * @param pattern String
     * @return String
     */
    public static String formatPercent(double d, String pattern) {
        return formatPercent(Double.valueOf(d), pattern);
    }

    /**
     * 将Double类型转成百分比形式
     *
     * @param d       Double
     * @param pattern String
     * @return String
     */
    public static String formatPercent(Double d, String pattern) {
        DecimalFormat decimalFormat = new DecimalFormat(pattern);
        return decimalFormat.format(d);
    }
}
