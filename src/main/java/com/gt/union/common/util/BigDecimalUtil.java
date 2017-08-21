package com.gt.union.common.util;

import java.math.BigDecimal;

/**
 * Created by Administrator on 2017/8/2 0002.
 */
public class BigDecimalUtil {
    private static final int SCALE = 2;
    /**
     * 加法操作，默认四舍五入保留两位小数
     * @param b1
     * @param b2
     * @return
     */
    public static final BigDecimal add(BigDecimal b1, BigDecimal b2) {
        b1 = b1 == null ? BigDecimal.valueOf(0.0) : b1;
        b2 = b2 == null ? BigDecimal.valueOf(0.0) : b2;
        return b1.add(b2).setScale(SCALE, BigDecimal.ROUND_CEILING);
    }

    public static final BigDecimal add(BigDecimal b1, Double d1) {
        BigDecimal b2 = d1 == null ? BigDecimal.valueOf(0.0) : BigDecimal.valueOf(d1.doubleValue());
        return add(b1, b2);
    }

    public static final BigDecimal add(Double d1, BigDecimal b1) {
        return add(b1, d1);
    }

    public static final BigDecimal add(Double d1, Double d2) {
        BigDecimal b1 = d1 == null ? BigDecimal.valueOf(0.0) : BigDecimal.valueOf(d1.doubleValue());
        return add(b1, d2);
    }

    /**
     * 减法操作，默认四舍五入保留两位小数
     * @param b1
     * @param b2
     * @return
     */
    public static final BigDecimal subtract(BigDecimal b1, BigDecimal b2) {
        b1 = b1 == null ? BigDecimal.valueOf(0.0) : b1;
        b2 = b2 == null ? BigDecimal.valueOf(0.0) : b2;
        return b1.subtract(b2).setScale(SCALE, BigDecimal.ROUND_CEILING);
    }

    public static final BigDecimal subtract(BigDecimal b1, Double d1) {
        BigDecimal b2 = d1 == null ? BigDecimal.valueOf(0.0) : BigDecimal.valueOf(d1.doubleValue());
        return subtract(b1, b2);
    }

    public static final BigDecimal subtract(Double d1, BigDecimal b1) {
        BigDecimal b2 = d1 == null ? BigDecimal.valueOf(0.0) : BigDecimal.valueOf(d1.doubleValue());
        return subtract(b2, b1);
    }

    public static final BigDecimal subtract(Double d1, Double d2) {
        BigDecimal b1 = d1 == null ? BigDecimal.valueOf(0.0) : BigDecimal.valueOf(d1.doubleValue());
        return subtract(b1, d2);
    }

    /**
     * 乘法操作，默认四舍五入保留两位小数
     * @param b1
     * @param b2
     * @return
     */
    public static final BigDecimal multiply(BigDecimal b1, BigDecimal b2) {
        b1 = b1 == null ? BigDecimal.valueOf(0.0) : b1;
        b2 = b2 == null ? BigDecimal.valueOf(0.0) : b2;
        return b1.multiply(b2).setScale(SCALE, BigDecimal.ROUND_CEILING);
    }

    public static final BigDecimal multiply(BigDecimal b1, Double d1) {
        BigDecimal b2 = d1 == null ? BigDecimal.valueOf(0.0) : BigDecimal.valueOf(d1.doubleValue());
        return multiply(b1, b2);
    }

    public static final BigDecimal multiply(Double d1, BigDecimal b1) {
        return multiply(b1, d1);
    }

    public static final BigDecimal multiply(Double d1, Double d2) {
        BigDecimal b1 = d1 == null ? BigDecimal.valueOf(0.0) : BigDecimal.valueOf(d1.doubleValue());
        return multiply(b1, d2);
    }

    /**
     * 除法操作，默认四舍五入保留两位小数
     * @param b1
     * @param b2
     * @return
     */
    public static final BigDecimal divide(BigDecimal b1, BigDecimal b2) {
        return b1.divide(b2).setScale(SCALE, BigDecimal.ROUND_CEILING);
    }

    public static final BigDecimal divide(BigDecimal b1, Double d1) {
        BigDecimal b2 = d1 == null ? BigDecimal.valueOf(0.0) : BigDecimal.valueOf(d1.doubleValue());
        return divide(b1, b2);
    }

    public static final BigDecimal divide(Double d1, BigDecimal b1) {
        BigDecimal b2 = d1 == null ? BigDecimal.valueOf(0.0) : BigDecimal.valueOf(d1.doubleValue());
        return divide(b2, b1);
    }

    public static final BigDecimal divide(Double d1, Double d2) {
        BigDecimal b1 = d1 == null ? BigDecimal.valueOf(0.0) : BigDecimal.valueOf(d1.doubleValue());
        return divide(b1, d2);
    }
}
