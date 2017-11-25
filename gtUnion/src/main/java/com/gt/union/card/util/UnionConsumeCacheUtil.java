package com.gt.union.card.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 消费核销 缓存键工具类
 *
 * @author linweicong
 * @version 2017-11-25 10:51:42
 */
public class UnionConsumeCacheUtil {
    public static final int TYPE_MEMBER_ID = 1;
    public static final int TYPE_UNION_ID = 2;
    public static final int TYPE_CARD_ID = 3;
    public static final int TYPE_ROOT_ID = 4;


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

    public static String getUnionIdKey(Integer unionId) {
        return "unionConsume:unionId:" + unionId;
    }

    public static String getCardIdKey(Integer cardId) {
        return "unionConsume:cardId:" + cardId;
    }

    public static String getRootIdKey(Integer rootId) {
        return "unionConsume:rootId:" + rootId;
    }
}