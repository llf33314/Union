package com.gt.union.card.consume.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 消费核销 缓存键工具类
 *
 * @author linweicong
 * @version 2017-12-27 10:56:25
 */
public class UnionConsumeCacheUtil {
    public static final int TYPE_MEMBER_ID = 1;
    public static final int TYPE_UNION_ID = 2;
    public static final int TYPE_CARD_ID = 3;
    public static final int TYPE_FAN_ID = 4;

    public static String getIdKey(Integer id) {
        return "unionConsume:" + id;
    }

    public static List<String> getIdKey(List<Integer> idList) {
        List<String> result = new ArrayList<>();
        for (Integer id : idList) {
            result.add(getIdKey(id));
        }
        return result;
    }


    public static String getMemberIdKey(Integer memberId) {
        return "unionConsume:memberId:" + memberId;
    }

    public static String getValidMemberIdKey(Integer memberId) {
        return "unionConsume:memberId:" + memberId + ":valid";
    }

    public static String getInvalidMemberIdKey(Integer memberId) {
        return "unionConsume:memberId:" + memberId + ":invalid";
    }

    public static String getUnionIdKey(Integer unionId) {
        return "unionConsume:unionId:" + unionId;
    }

    public static String getValidUnionIdKey(Integer unionId) {
        return "unionConsume:unionId:" + unionId + ":valid";
    }

    public static String getInvalidUnionIdKey(Integer unionId) {
        return "unionConsume:unionId:" + unionId + ":invalid";
    }

    public static String getCardIdKey(Integer cardId) {
        return "unionConsume:cardId:" + cardId;
    }

    public static String getValidCardIdKey(Integer cardId) {
        return "unionConsume:cardId:" + cardId + ":valid";
    }

    public static String getInvalidCardIdKey(Integer cardId) {
        return "unionConsume:cardId:" + cardId + ":invalid";
    }

    public static String getFanIdKey(Integer fanId) {
        return "unionConsume:fanId:" + fanId;
    }

    public static String getValidFanIdKey(Integer fanId) {
        return "unionConsume:fanId:" + fanId + ":valid";
    }

    public static String getInvalidFanIdKey(Integer fanId) {
        return "unionConsume:fanId:" + fanId + ":invalid";
    }

}