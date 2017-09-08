package com.gt.union.common.util;

import java.util.List;

/**
 * Created by Administrator on 2017/7/24 0024.
 */
public class ListUtil {
    /**
     * 判断列表是否为空
     * @param list
     * @return
     */
    public static final boolean isEmpty(List<?> list) {
        return list == null  || list.size() == 0;
    }

    /**
     * 判断列表是否不为空
     * @param list
     * @return
     */
    public static final boolean isNotEmpty(List<?> list) {
        return list != null && list.size() > 0;
    }
}
