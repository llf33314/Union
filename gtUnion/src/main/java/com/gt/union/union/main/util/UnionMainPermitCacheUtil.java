package com.gt.union.union.main.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 联盟许可 缓存键工具类
 *
 * @author linweicong
 * @version 2017-11-23 15:26:19
 */
public class UnionMainPermitCacheUtil {
    public static final int TYPE_BUS_ID = 1;
    public static final int TYPE_PACKAGE_ID = 2;

    public static String getIdKey(Integer id) {
        return "unionMainPermit:" + id;
    }

    public static List<String> getIdKey(List<Integer> idList) {
        List<String> result = new ArrayList<>();
        for (Integer id : idList) {
            result.add(getIdKey(id));
        }
        return result;
    }

    public static String getBusId(Integer busId) {
        return "unionMainPermit:busId:" + busId;
    }

    public static String getPackageId(Integer packageId) {
        return "unionMainPermit:packageId:" + packageId;
    }

}