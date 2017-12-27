package com.gt.union.union.main.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 联盟许可 缓存键工具类
 *
 * @author linweicong
 * @version 2017-12-27 10:56:25
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


    public static String getBusIdKey(Integer busId) {
        return "unionMainPermit:busId:" + busId;
    }

    public static String getValidBusIdKey(Integer busId) {
        return "unionMainPermit:busId:" + busId + ":valid";
    }

    public static String getInvalidBusIdKey(Integer busId) {
        return "unionMainPermit:busId:" + busId + ":invalid";
    }

    public static String getPackageIdKey(Integer packageId) {
        return "unionMainPermit:packageId:" + packageId;
    }

    public static String getValidPackageIdKey(Integer packageId) {
        return "unionMainPermit:packageId:" + packageId + ":valid";
    }

    public static String getInvalidPackageIdKey(Integer packageId) {
        return "unionMainPermit:packageId:" + packageId + ":invalid";
    }

}