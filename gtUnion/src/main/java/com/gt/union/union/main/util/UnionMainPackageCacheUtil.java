package com.gt.union.union.main.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 盟主服务套餐 缓存键工具类
 *
 * @author linweicong
 * @version 2017-11-23 15:26:16
 */
public class UnionMainPackageCacheUtil {

    public static String getIdKey(Integer id) {
        return "unionMainPackage:" + id;
    }

    public static List<String> getIdKey(List<Integer> idList) {
        List<String> result = new ArrayList<>();
        for (Integer id : idList) {
            result.add(getIdKey(id));
        }
        return result;
    }
}