package com.gt.union.common.util;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 列表工具类
 *
 * @author linweicong
 * @version 2017-11-22 17:45:00
 */
public class ListUtil {
    /**
     * 判断列表是否为空
     *
     * @param list List<?>
     * @return boolean
     */
    public static boolean isEmpty(List<?> list) {
        return list == null || list.size() == 0;
    }

    /**
     * 判断列表是否不为空
     *
     * @param list List<?>
     * @return boolean
     */
    public static boolean isNotEmpty(List<?> list) {
        return list != null && list.size() > 0;
    }

    /**
     * set转list
     *
     * @param set Set<T>
     * @return List<T>
     */
    public static <T> List<T> toList(Set<T> set) {
        List<T> result = new ArrayList<>();
        if (set != null) {
            result.addAll(set);
        }
        return result;
    }

    /**
     * list转set
     *
     * @param list List<T>
     * @return Set<T>
     */
    public static <T> Set<T> toSet(List<T> list) {
        Set<T> result = new HashSet<>();
        if (isNotEmpty(list)) {
            result.addAll(list);
        }
        return result;
    }
}
