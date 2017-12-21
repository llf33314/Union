package com.gt.union.card.main.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 联盟卡购买记录 缓存键工具类
 *
 * @author linweicong
 * @version 2017-11-23 17:39:04
 */
public class UnionCardRecordCacheUtil {
    public static final int TYPE_CARD_ID = 1;
    public static final int TYPE_FAN_ID = 2;

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

    public static String getCardIdKey(Integer cardId) {
        return "unionCardRecord:cardId:" + cardId;
    }

    public static String getFanIdKey(Integer fanId) {
        return "unionCardRecord:fanId:" + fanId;
    }
}