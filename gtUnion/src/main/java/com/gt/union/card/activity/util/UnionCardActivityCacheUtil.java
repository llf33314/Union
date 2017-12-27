package com.gt.union.card.activity.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 活动 缓存键工具类
 *
 * @author linweicong
 * @version 2017-12-27 10:56:25
 */
public class UnionCardActivityCacheUtil {
    public static final int TYPE_UNION_ID = 1;

    public static String getIdKey(Integer id) {
        return "unionCardActivity:" + id;
    }

    public static List<String> getIdKey(List<Integer> idList) {
        List<String> result = new ArrayList<>();
        for (Integer id : idList) {
            result.add(getIdKey(id));
        }
        return result;
    }


    public static String getUnionIdKey(Integer unionId) {
        return "unionCardActivity:unionId:" + unionId;
    }

    public static String getValidUnionIdKey(Integer unionId) {
        return "unionCardActivity:unionId:" + unionId + ":valid";
    }

    public static String getInvalidUnionIdKey(Integer unionId) {
        return "unionCardActivity:unionId:" + unionId + ":invalid";
    }

}