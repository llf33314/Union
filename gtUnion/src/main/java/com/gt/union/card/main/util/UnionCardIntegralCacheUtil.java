package com.gt.union.card.main.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 联盟积分 缓存键工具类
 *
 * @author linweicong
 * @version 2017-09-01 11:34:16
 */
public class UnionCardIntegralCacheUtil {
    public static final int TYPE_FAN_ID = 1;
    public static final int TYPE_UNION_ID = 2;

    public static String getIdKey(Integer id) {
        return "unionCardIntegral:" + id;
    }

    public static List<String> getIdKey(List<Integer> idList) {
        List<String> result = new ArrayList<>();
        for (Integer id : idList) {
            result.add(getIdKey(id));
        }
        return result;
    }

    public static String getFanIdKey(Integer fanId) {
        return "unionCardIntegral:fanId:" + fanId;
    }

    public static String getUnionIdKey(Integer unionId) {
        return "unionCardIntegral:unionId:" + unionId;
    }
}