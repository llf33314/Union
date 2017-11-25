package com.gt.union.card.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 消费核销活动项目 缓存键工具类
 *
 * @author linweicong
 * @version 2017-11-25 10:51:42
 */
public class UnionConsumeProjectCacheUtil {
    public static final int TYPE_CONSUME_ID = 1;

    public static String getIdKey(Integer id) {
        return "unionConsumeProject:" + id;
    }

    public static List<String> getIdKey(List<Integer> idList) {
        List<String> result = new ArrayList<>();
        for (Integer id : idList) {
            result.add(getIdKey(id));
        }
        return result;
    }

    public static String getConsumeIdKey(Integer consumeId) {
        return "unionConsumeProject:consumeId:" + consumeId;
    }
}