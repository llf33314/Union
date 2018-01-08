package com.gt.union.common.util;

import java.math.BigDecimal;

/**
 * 高精度结算工具类
 *
 * @author linweicong
 * @version 2017-11-22 17:45:00
 */
public class BigDecimalUtil {
    private static final int DEFAULT_SCALE = 2;
    private static final int DEFAULT_ROUND = BigDecimal.ROUND_HALF_UP;

    public static BigDecimal add(BigDecimal b1, BigDecimal b2) {
        b1 = b1 == null ? BigDecimal.ZERO : b1;
        b2 = b2 == null ? BigDecimal.ZERO : b2;
        return b1.add(b2);
    }

    public static BigDecimal add(BigDecimal b1, Double d1) {
        BigDecimal b2 = d1 == null ? BigDecimal.ZERO : BigDecimal.valueOf(d1);
        return add(b1, b2);
    }

    public static BigDecimal add(Double d1, BigDecimal b1) {
        return add(b1, d1);
    }

    public static BigDecimal add(Double d1, Double d2) {
        BigDecimal b1 = d1 == null ? BigDecimal.ZERO : BigDecimal.valueOf(d1);
        return add(b1, d2);
    }

    public static BigDecimal subtract(BigDecimal b1, BigDecimal b2) {
        b1 = b1 == null ? BigDecimal.ZERO : b1;
        b2 = b2 == null ? BigDecimal.ZERO : b2;
        return b1.subtract(b2);
    }

    public static BigDecimal subtract(BigDecimal b1, Double d1) {
        BigDecimal b2 = d1 == null ? BigDecimal.ZERO : BigDecimal.valueOf(d1);
        return subtract(b1, b2);
    }

    public static BigDecimal subtract(Double d1, BigDecimal b1) {
        BigDecimal b2 = d1 == null ? BigDecimal.ZERO : BigDecimal.valueOf(d1);
        return subtract(b2, b1);
    }

    public static BigDecimal subtract(Double d1, Double d2) {
        BigDecimal b1 = d1 == null ? BigDecimal.ZERO : BigDecimal.valueOf(d1);
        return subtract(b1, d2);
    }

    public static BigDecimal multiply(BigDecimal b1, BigDecimal b2) {
        b1 = b1 == null ? BigDecimal.ZERO : b1;
        b2 = b2 == null ? BigDecimal.ZERO : b2;
        return b1.multiply(b2);
    }

    public static BigDecimal multiply(BigDecimal b1, Double d1) {
        BigDecimal b2 = d1 == null ? BigDecimal.ZERO : BigDecimal.valueOf(d1);
        return multiply(b1, b2);
    }

    public static BigDecimal multiply(Double d1, BigDecimal b1) {
        return multiply(b1, d1);
    }

    public static BigDecimal multiply(Double d1, Double d2) {
        BigDecimal b1 = d1 == null ? BigDecimal.ZERO : BigDecimal.valueOf(d1);
        return multiply(b1, d2);
    }

    public static BigDecimal divide(BigDecimal b1, BigDecimal b2) {
        b1 = b1 == null ? BigDecimal.ZERO : b1;
        b2 = b2 == null ? BigDecimal.ZERO : b2;
        return b1.divide(b2);
    }

    public static BigDecimal divide(BigDecimal b1, Double d1) {
        BigDecimal b2 = d1 == null ? BigDecimal.ZERO : BigDecimal.valueOf(d1);
        return divide(b1, b2);
    }

    public static BigDecimal divide(Double d1, BigDecimal b1) {
        BigDecimal b2 = d1 == null ? BigDecimal.ZERO : BigDecimal.valueOf(d1);
        return divide(b2, b1);
    }

    public static BigDecimal divide(Double d1, Double d2) {
        BigDecimal b1 = d1 == null ? BigDecimal.ZERO : BigDecimal.valueOf(d1);
        return divide(b1, d2);
    }

    public static Double toDouble(BigDecimal b) {
        return toDouble(b, DEFAULT_SCALE);
    }

    public static Double toDouble(BigDecimal b, Integer scale) {
        return toDouble(b, scale, DEFAULT_ROUND);
    }

    public static Double toDouble(BigDecimal b, Integer scale, Integer round) {
        b = b == null ? BigDecimal.ZERO : b;
        return b.setScale(scale, round).doubleValue();
    }
}
