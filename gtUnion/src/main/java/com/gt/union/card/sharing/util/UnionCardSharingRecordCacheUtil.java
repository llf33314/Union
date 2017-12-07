package com.gt.union.card.sharing.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 联盟卡售卡分成记录 缓存键工具类
 *
 * @author linweicong
 * @version 2017-11-23 17:39:04
 */
public class UnionCardSharingRecordCacheUtil {
    public static final int TYPE_SHARING_MEMBER_ID = 1;
    public static final int TYPE_FROM_MEMBER_ID = 2;
    public static final int TYPE_UNION_ID = 3;
    public static final int TYPE_ACTIVITY_ID = 4;
    public static final int TYPE_CARD_ID = 5;
    public static final int TYPE_FAN_ID = 6;

    public static String getIdKey(Integer id) {
        return "unionCardSharingRecord:" + id;
    }

    public static List<String> getIdKey(List<Integer> idList) {
        List<String> result = new ArrayList<>();
        for (Integer id : idList) {
            result.add(getIdKey(id));
        }
        return result;
    }

    public static String getSharingMemberIdKey(Integer sharingMemberId) {
        return "unionCardSharingRecord:sharingMemberId:" + sharingMemberId;
    }

    public static String getFromMemberIdKey(Integer fromMemberId) {
        return "unionCardSharingRecord:fromMemberId:" + fromMemberId;
    }

    public static String getUnionIdKey(Integer unionId) {
        return "unionCardSharingRecord:unionId:" + unionId;
    }

    public static String getActivityIdKey(Integer activityId) {
        return "unionCardSharingRecord:activityId:" + activityId;
    }

    public static String getCardIdKey(Integer cardId) {
        return "unionCardSharingRecord:cardId:" + cardId;
    }

    public static String getFanIdKey(Integer fanId) {
        return "unionCardSharingRecord:fanId:" + fanId;
    }
}