package com.gt.union.card.main.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 联盟卡购买记录 缓存键工具类
 *
 * @author linweicong
 * @version 2017-12-27 10:56:25
 */
public class UnionCardRecordCacheUtil {
    public static final int TYPE_ACTIVITY_ID = 1;
    public static final int TYPE_CARD_ID = 2;
    public static final int TYPE_FAN_ID = 3;
    public static final int TYPE_MEMBER_ID = 4;
    public static final int TYPE_UNION_ID = 5;

    public static String getIdKey(Integer id) {
        return "unionCardRecord:" + id;
    }

    public static List<String> getIdKey(List<Integer> idList) {
        List<String> result = new ArrayList<>();
        for (Integer id : idList) {
            result.add(getIdKey(id));
        }
        return result;
    }


    public static String getActivityIdKey(Integer activityId) {
        return "unionCardRecord:activityId:" + activityId;
    }

    public static String getValidActivityIdKey(Integer activityId) {
        return "unionCardRecord:activityId:" + activityId + ":valid";
    }

    public static String getInvalidActivityIdKey(Integer activityId) {
        return "unionCardRecord:activityId:" + activityId + ":invalid";
    }

    public static String getCardIdKey(Integer cardId) {
        return "unionCardRecord:cardId:" + cardId;
    }

    public static String getValidCardIdKey(Integer cardId) {
        return "unionCardRecord:cardId:" + cardId + ":valid";
    }

    public static String getInvalidCardIdKey(Integer cardId) {
        return "unionCardRecord:cardId:" + cardId + ":invalid";
    }

    public static String getFanIdKey(Integer fanId) {
        return "unionCardRecord:fanId:" + fanId;
    }

    public static String getValidFanIdKey(Integer fanId) {
        return "unionCardRecord:fanId:" + fanId + ":valid";
    }

    public static String getInvalidFanIdKey(Integer fanId) {
        return "unionCardRecord:fanId:" + fanId + ":invalid";
    }

    public static String getMemberIdKey(Integer memberId) {
        return "unionCardRecord:memberId:" + memberId;
    }

    public static String getValidMemberIdKey(Integer memberId) {
        return "unionCardRecord:memberId:" + memberId + ":valid";
    }

    public static String getInvalidMemberIdKey(Integer memberId) {
        return "unionCardRecord:memberId:" + memberId + ":invalid";
    }

    public static String getUnionIdKey(Integer unionId) {
        return "unionCardRecord:unionId:" + unionId;
    }

    public static String getValidUnionIdKey(Integer unionId) {
        return "unionCardRecord:unionId:" + unionId + ":valid";
    }

    public static String getInvalidUnionIdKey(Integer unionId) {
        return "unionCardRecord:unionId:" + unionId + ":invalid";
    }

}