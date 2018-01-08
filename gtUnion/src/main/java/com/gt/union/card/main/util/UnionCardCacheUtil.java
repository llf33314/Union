package com.gt.union.card.main.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 联盟卡 缓存键工具类
 *
 * @author linweicong
 * @version 2017-12-27 10:56:25
 */
public class UnionCardCacheUtil {
    public static final int TYPE_MEMBER_ID = 1;
    public static final int TYPE_UNION_ID = 2;
    public static final int TYPE_FAN_ID = 3;
    public static final int TYPE_ACTIVITY_ID = 4;

    public static String getIdKey(Integer id) {
        return "unionCard:" + id;
    }

    public static List<String> getIdKey(List<Integer> idList) {
        List<String> result = new ArrayList<>();
        for (Integer id : idList) {
            result.add(getIdKey(id));
        }
        return result;
    }


    public static String getMemberIdKey(Integer memberId) {
        return "unionCard:memberId:" + memberId;
    }

    public static String getValidMemberIdKey(Integer memberId) {
        return "unionCard:memberId:" + memberId + ":valid";
    }

    public static String getInvalidMemberIdKey(Integer memberId) {
        return "unionCard:memberId:" + memberId + ":invalid";
    }

    public static String getUnionIdKey(Integer unionId) {
        return "unionCard:unionId:" + unionId;
    }

    public static String getValidUnionIdKey(Integer unionId) {
        return "unionCard:unionId:" + unionId + ":valid";
    }

    public static String getInvalidUnionIdKey(Integer unionId) {
        return "unionCard:unionId:" + unionId + ":invalid";
    }

    public static String getFanIdKey(Integer fanId) {
        return "unionCard:fanId:" + fanId;
    }

    public static String getValidFanIdKey(Integer fanId) {
        return "unionCard:fanId:" + fanId + ":valid";
    }

    public static String getInvalidFanIdKey(Integer fanId) {
        return "unionCard:fanId:" + fanId + ":invalid";
    }

    public static String getActivityIdKey(Integer activityId) {
        return "unionCard:activityId:" + activityId;
    }

    public static String getValidActivityIdKey(Integer activityId) {
        return "unionCard:activityId:" + activityId + ":valid";
    }

    public static String getInvalidActivityIdKey(Integer activityId) {
        return "unionCard:activityId:" + activityId + ":invalid";
    }

}