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

    /**
     * 获取封装的unionApplyInfo
     * @param unionId
     * @param busId
     * @return
     */
    public static final String getUnionApplyInfoKey(Integer unionId, Integer busId) {
        return getUnionApplyInfoKey(String.valueOf(unionId), String.valueOf(busId));
    }
    public static final String getUnionApplyInfoKey(String unionId, String busId) {
        return "unionMain:" + unionId + ":unionApplyInfo:busId:" + busId;
    }

    /**
     * 字典类型
     * @param itemKey
     * @return
     */
    public static final String getDictTypeKey(String itemKey){
        return "dictType:" + itemKey;
    }

    /**
     * 获取封装的unionInfoDict的缓存key
     * @param unionId
     * @return
     */
    public static final String getUnionInfoDictKey(Integer unionId){
        return getUnionInfoDictKey(String.valueOf(unionId));
    }

    public static final String getUnionInfoDictKey(String unionId){
        return "dict:unionId:" + unionId;
    }


    /**
     * 获取封装unionMemberCard的缓存key
     * @param memberId  用户id
     * @param busId     商家id
     * @return
     */
    public static final String getUnionMemberCardKey(Integer memberId, Integer busId){
        return getUnionMemberCardKey(String.valueOf(memberId), String.valueOf(busId));
    }

    public static final String getUnionMemberCardKey(String memberId, String busId){
        return "memberCard:" + memberId + "busId:" + busId;
    }

    /**
     * 获取创建联盟支付参数key
     * @param time
     * @return
     */
    public static final String getCreateUnionPayParamKey(String time){
        return "createUnion:" + time;
    }

    /**
     * 获取创建联盟支付状态key
     * @param time
     * @return
     */
    public static final String getCreateUnionPayStatusKey(String time){
        return "createUnion:status:" + time;
    }

    /**
     * 获取升级联盟卡支付参数key
     * @param time
     * @return
     */
    public static final String getUpdateCardPayParamKey(String time){
        return "updateCard:" + time;
    }

    /**
     * 获取升级联盟卡支付状态key
     * @param status
     * @return
     */
    public static final String getUpdateCardPayStatusKey(String status){
        return "updateCard:status:" + status;
    }

    /**
     * 根据商家id获取公众号信息key
     * @param busId
     * @return
     */
    public static final String getWxPublicUserBusIdKey(Integer busId){
        return getWxPublicUserBusIdKey(String.valueOf(busId));
    }

    public static final String getWxPublicUserBusIdKey(String busId){
        return "wxPublicUser:" + busId;
    }

}
