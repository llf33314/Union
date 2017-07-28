package com.gt.union.common.util;

/**
 * Created by Administrator on 2017/7/28 0028.
 */
public class DoubleUtil {
    /**
     * 判断data中的小数精度是否超过了precision
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
     * 判断data中的小数精度是否超过了precision
     * @param data
     * @param precision
     * @return
     */
    public static final boolean checkDecimalPrecision(double data, int precision) {
        System.out.println("111:" + data);
        return checkDecimalPrecision(Double.valueOf(data), precision);
    }
}
