package com.gt.union.main.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 联盟创建 缓存键工具类
 *
 * @author linweicong
 * @version 2017-11-23 15:26:25
 */
public class UnionMainCreateCacheUtil {
    public static final int TYPE_BUS_ID = 1;
    public static final int TYPE_UNION_ID = 2;
    public static final int TYPE_PERMIT_ID = 3;

    public static String getIdKey(Integer id) {
        return "unionMainCreate:" + id;
    }

    public static List<String> getIdKey(List<Integer> idList) {
        List<String> result = new ArrayList<>();
        for (Integer id : idList) {
            result.add(getIdKey(id));
        }
        return result;
    }

    public static String getBusIdKey(Integer busId) {
        return "unionMainCreate:busId:" + busId;
    }

    public static String getUnionIdKey(Integer unionId) {
        return "unionMainCreate:unionId:" + unionId;
    }

    public static String getPermitIdKey(Integer permitId) {
        return "unionMainCreate:permitId:" + permitId;
    }
}