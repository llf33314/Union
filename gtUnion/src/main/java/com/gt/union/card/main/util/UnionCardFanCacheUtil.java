package com.gt.union.card.main.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 联盟卡粉丝信息 缓存键工具类
 *
 * @author linweicong
 * @version 2017-12-27 10:56:25
 */
public class UnionCardFanCacheUtil {

    public static String getIdKey(Integer id) {
        return "unionCardFan:" + id;
    }

    public static List<String> getIdKey(List<Integer> idList) {
        List<String> result = new ArrayList<>();
        for (Integer id : idList) {
            result.add(getIdKey(id));
        }
        return result;
    }


}