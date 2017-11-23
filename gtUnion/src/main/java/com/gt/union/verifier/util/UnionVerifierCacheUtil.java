package com.gt.union.verifier.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 平台管理者 缓存键工具类
 *
 * @author linweicong
 * @version 2017-11-23 14:54:27
 */
public class UnionVerifierCacheUtil {
    public static final int TYPE_BUS_ID = 1;

    public static String getIdKey(Integer id) {
        return "unionVerifier:" + id;
    }

    public static List<String> getIdKey(List<Integer> idList) {
        List<String> result = new ArrayList<>();
        for (Integer id : idList) {
            result.add(getIdKey(id));
        }
        return result;
    }

    public static String getBusIdKey(Integer busId) {
        return "unionVerifier:busId:" + busId;
    }
}