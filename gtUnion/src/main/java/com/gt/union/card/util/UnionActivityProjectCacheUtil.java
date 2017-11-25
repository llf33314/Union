package com.gt.union.card.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 活动项目 缓存键工具类
 *
 * @author linweicong
 * @version 2017-11-24 16:48:44
 */
public class UnionActivityProjectCacheUtil {
    public static final int TYPE_ACTIVITY_ID = 1;
    public static final int TYPE_MEMBER_ID = 2;
    public static final int TYPE_UNION_ID = 3;


    public static String getIdKey(Integer id) {
        return "unionActivityProject:" + id;
    }

    public static List<String> getIdKey(List<Integer> idList) {
        List<String> result = new ArrayList<>();
        for (Integer id : idList) {
            result.add(getIdKey(id));
        }
        return result;
    }

    public static String getActivityIdKey(Integer activityId) {
        return "unionActivityProject:activityId:" + activityId;
    }

    public static String getMemberIdKey(Integer memberId) {
        return "unionActivityProject:memberId;" + memberId;
    }

    public static String getUnionIdKey(Integer unionId) {
        return "unionActivityProject:unionId:" + unionId;
    }
}