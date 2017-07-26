package com.gt.union.common.util;

/**
 * Created by Administrator on 2017/7/26 0026.
 */
public class StringUtil {
    /**
     * 判断字符串是否为空，如为空，则返回true，否则返回false
     * @param str
     * @return
     */
    public static final boolean isEmpty(String str) {
        return str == null || "".equals(str.trim());
    }

    /**
     * 判断字符串是否不为空，如不为空，则返回true，否则返回false
     * @param str
     * @return
     */
    public static final boolean isNotEmpty(String str) {
        return str != null && !"".equals(str.trim());
    }
}
