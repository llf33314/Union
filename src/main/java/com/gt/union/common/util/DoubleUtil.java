package com.gt.union.common.util;

import java.text.DecimalFormat;

/**
 * Created by Administrator on 2017/7/28 0028.
 */
public class DoubleUtil {
    public static final String PERCENT_PATTERN = "0.00%";
    /**
     * 判断data中的小数精度是否超过了precision //TODO 感觉这个方法可以有更好的实现
     * @param data
     * @param precision
     * @return
     */
    public static final boolean checkDecimalPrecision(Double data, int precision) {
        data = data == null ? Double.valueOf(0.0) : data;
        String dataStr = String.valueOf(data.doubleValue());
        return dataStr.substring(dataStr.indexOf(".") + 1).length() > precision ? false : true;
    }

    /**
     * 判断data中的小数精度是否超过了precision //TODO 感觉这个方法可以有更好的实现
     * @param data
     * @param precision
     * @return
     */
    public static final boolean checkDecimalPrecision(double data, int precision) {
        return checkDecimalPrecision(Double.valueOf(data), precision);
    }

    /**
     * 将Double类型转成百分比形式
     * @param d
     * @return
     */
    public static final String formatPercent(Double d) {
        return formatPercent(d, PERCENT_PATTERN);
    }

    /**
     * 将Double类型转成百分比形式
     * @param d
     * @return
     */
    public static final String formatPercent(double d) {
        return formatPercent(Double.valueOf(d));
    }

    /**
     * 将Double类型转成百分比形式
     * @param d
     * @return
     */
    public static final String formatPercent(double d, String pattern) {
        return formatPercent(Double.valueOf(d), pattern);
    }

    /**
     * 将Double类型转成百分比形式
     * @param d
     * @return
     */
    public static final String formatPercent(Double d, String pattern) {
        DecimalFormat decimalFormat = new DecimalFormat(pattern);
        return decimalFormat.format(d);
    }
}
