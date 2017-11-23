package com.gt.union.card.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 联盟卡售卡分成比例 缓存键工具类
 *
 * @author linweicong
 * @version 2017-11-23 17:39:04
 */
public class UnionCardSharingRatioCacheUtil {
    public static final int TYPE_ACTIVITY_ID = 1;
    public static final int TYPE_MEMBER_ID = 2;
    public static final int TYPE_UNION_ID = 3;

    public static String getIdKey(Integer id) {
        return "unionCardSharingRatio:" + id;
    }

    public static List<String> getIdKey(List<Integer> idList) {
        List<String> result = new ArrayList<>();
        for (Integer id : idList) {
            result.add(getIdKey(id));
        }
        return result;
    }

    public static String getActivityIdKey(Integer activityId) {
        return "unionCardSharingRatio:activityId:" + activityId;
    }

    public static String getMemberIdKey(Integer memberId) {
        return "unionCardSharingRatio:memberId:" + memberId;
    }

    public static String getUnionIdKey(Integer unionId) {
        return "unionCardSharingRatio:unionId:" + unionId;
    }
}