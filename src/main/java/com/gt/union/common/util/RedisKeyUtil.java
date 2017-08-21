package com.gt.union.common.util;

/**
 * Created by Administrator on 2017/8/18 0018.
 */
public class RedisKeyUtil {
    /**
     * 获取封装的busUser key
     * @param busUserId
     * @return
     */
    public static final String getBusUserKey(Integer busUserId) {
        return getBusUserKey(String.valueOf(busUserId));
    }
    public static final String getBusUserKey(String busUserId) {
        return "busUser:" + busUserId;
    }

    /**
     * 获取封装的unionMain key
     * @param unionId
     * @return
     */
    public static final String getUnionMainKey(Integer unionId) {
        return getUnionMainKey(String.valueOf(unionId));
    }
    public static final String getUnionMainKey(String unionId) {
        return "unionMain:" + unionId;
    }

    /**
     * 获取封装的unionMainValid key
     * @param unionId
     * @return
     */
    public static final String getUnionMainValidKey(Integer unionId) {
        return getUnionMainValidKey(String.valueOf(unionId));
    }
    public static final String getUnionMainValidKey(String unionId) {
        return "unionMain:valid:" + unionId;
    }

    /**
     * 获取封装的unionMain & unionMember key
     * @param unionId
     * @param busId
     * @return
     */
    public static final String getUnionMemberBusIdKey(Integer unionId, Integer busId) {
        return getUnionMemberBusIdKey(String.valueOf(unionId), String.valueOf(busId));
    }
    public static final String getUnionMemberBusIdKey(String unionId, String busId) {
        return "unionMain:" + unionId + ":unionMember:busId:" + busId;
    }
}
