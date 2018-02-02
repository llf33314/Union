package com.gt.union.refund.order.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 缓存键工具类
 *
 * @author hongjiye
 * @version 2018-02-02 16:58:00
 */
public class UnionRefundOrderCacheUtil {

    public static String getIdKey(Integer id) {
        return "unionRefundOrder:" + id;
    }

    public static List<String> getIdKey(List<Integer> idList) {
        List<String> result = new ArrayList<>();
        for (Integer id : idList) {
            result.add(getIdKey(id));
        }
        return result;
    }


}