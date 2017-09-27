package com.gt.union.common.util;


import java.util.*;

/**
 * Created by Administrator on 2017/7/24 0024.
 */
public class ListUtil {
    /**
     * 判断列表是否为空
     *
     * @param list
     * @return
     */
    public static final boolean isEmpty(List<?> list) {
        return list == null || list.size() == 0;
    }

    /**
     * 判断列表是否不为空
     *
     * @param list
     * @return
     */
    public static final boolean isNotEmpty(List<?> list) {
        return list != null && list.size() > 0;
    }

    /**
     * set转list
     *
     * @param set
     * @param <T>
     * @return
     */
    public static final <T> List<T> toList(Set<T> set) {
        List<T> result = new ArrayList<>();
        if (set != null) {
            Iterator<T> iterator = set.iterator();
            while (iterator.hasNext()) {
                result.add(iterator.next());
            }
        }
        return result;
    }

    /**
     * list转set
     *
     * @param list
     * @param <T>
     * @return
     */
    public static final <T> Set<T> toSet(List<T> list) {
        Set<T> result = new HashSet<>();
        if (isNotEmpty(list)) {
            for (T t : list) {
                result.add(t);
            }
        }
        return result;
    }
}
