package com.gt.union.card.project.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 项目 缓存键工具类
 *
 * @author linweicong
 * @version 2017-12-27 10:56:25
 */
public class UnionCardProjectCacheUtil {
    public static final int TYPE_ACTIVITY_ID = 1;
    public static final int TYPE_MEMBER_ID = 2;
    public static final int TYPE_UNION_ID = 3;

    public static String getIdKey(Integer id) {
        return "unionCardProject:" + id;
    }

    public static List<String> getIdKey(List<Integer> idList) {
        List<String> result = new ArrayList<>();
        for (Integer id : idList) {
            result.add(getIdKey(id));
        }
        return result;
    }


    public static String getActivityIdKey(Integer activityId) {
        return "unionCardProject:activityId:" + activityId;
    }

    public static String getValidActivityIdKey(Integer activityId) {
        return "unionCardProject:activityId:" + activityId + ":valid";
    }

    public static String getInvalidActivityIdKey(Integer activityId) {
        return "unionCardProject:activityId:" + activityId + ":invalid";
    }

    public static String getMemberIdKey(Integer memberId) {
        return "unionCardProject:memberId:" + memberId;
    }

    public static String getValidMemberIdKey(Integer memberId) {
        return "unionCardProject:memberId:" + memberId + ":valid";
    }

    public static String getInvalidMemberIdKey(Integer memberId) {
        return "unionCardProject:memberId:" + memberId + ":invalid";
    }

    public static String getUnionIdKey(Integer unionId) {
        return "unionCardProject:unionId:" + unionId;
    }

    public static String getValidUnionIdKey(Integer unionId) {
        return "unionCardProject:unionId:" + unionId + ":valid";
    }

    public static String getInvalidUnionIdKey(Integer unionId) {
        return "unionCardProject:unionId:" + unionId + ":invalid";
    }

}