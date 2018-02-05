package com.gt.union.common.util;

/**
 * redis缓存键工具
 * @author hongjiye
 * Created by Administrator on 2017/8/18 0018.
 */
public class RedisKeyUtil {
    /**
     * 获取封装的busUser key
     *
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
     * 获取进销存权限缓存key
     * @return
     */
    public static final String getJxcAuthorityKey(){
        return "jxc:authority";
    }

    /**
     * 字典类型
     *
     * @param itemKey
     * @return
     */
    public static final String getDictTypeKey(String itemKey) {
        return "dictType:" + itemKey;
    }

    /**
     * 获取创建联盟支付参数key
     *
     * @param time
     * @return
     */
    public static final String getCreateUnionPayParamKey(String time) {
        return "createUnion:" + time;
    }

    /**
     * 获取创建联盟支付状态key
     *
     * @param time
     * @return
     */
    public static final String getCreateUnionPayStatusKey(String time) {
        return "createUnion:status:" + time;
    }

    /**
     * 根据商家id获取公众号信息key
     *
     * @param busId
     * @return
     */
    public static final String getWxPublicUserBusIdKey(Integer busId) {
        return getWxPublicUserBusIdKey(String.valueOf(busId));
    }

    /**
     * 根据商机id获取公众号信箱key
     *
     * @param busId
     * @return
     */
    public static final String getWxPublicUserBusIdKey(String busId) {
        return "wxPublicUser:" + busId;
    }


    /**
     * 根据粉丝id获取手机验证码key
     *
     * @param memberId
     * @return
     */
    public static final String getMemberPhoneCodeKey(Integer memberId) {
        return getMemberPhoneCodeKey(String.valueOf(memberId));
    }

    public static final String getMemberPhoneCodeKey(String memberId) {
        return "member:phone:" + memberId;
    }

    /**
     * 获取支付商机推荐佣金参数key
     *
     * @param only
     * @return
     */
    public static final String getRecommendPayParamKey(String only) {
        return "recommend:" + only;
    }

    /**
     * 获取支付商机推荐佣金状态key
     *
     * @param only
     * @return
     */
    public static final String getRecommendPayStatusKey(String only) {
        return "recommend:status:" + only;
    }


    /**
     * 获取办理联盟卡验证码key
     *
     * @param phone
     * @return
     */
    public static final String getBindCardPhoneKey(String phone) {
        return "card:" + phone;
    }

    /**
     * 获取佣金平台管理员验证码key
     *
     * @param phone
     * @return
     */
    public static final String getVerifyPhoneKey(String phone) {
        return "verifier:" + phone;
    }

    /**
     * 获取公众号二维码联盟key
     *
     * @param publicId
     * @param busId
     * @return
     */
    public static String getWxPublicUserQRCodeKey(Integer publicId, Integer busId) {
        return getWxPublicUserQRCodeKey(String.valueOf(publicId), String.valueOf(busId));
    }

    /**
     * 获取公众号二维码联盟key
     *
     * @param publicId
     * @param busId
     * @return
     */
    public static String getWxPublicUserQRCodeKey(String publicId, String busId) {
        return "qrcode:" + publicId + ":" + busId;
    }

    /**
     * 佣金平台手机号验证码key
     *
     * @param phone
     * @return
     */
    public static String getBrokeragePhoneKey(String phone) {
        return "h5brokerage:" + phone;
    }

    /**
     * 获取手机端手机登录验证码key
     *
     * @param phone
     * @return
     */
    public static String getCardH5LoginPhoneKey(String phone) {
        return "cardh5:login:" + phone;
    }

    /**
     * 获取手机端绑定手机号key
     *
     * @param phone
     * @return
     */
    public static String getCardH5BindPhoneKey(String phone) {
        return "cardh5:bind:" + phone;
    }

    /**
     * 获取支付办理联盟卡状态key
     *
     * @param only
     * @return
     */
    public static String getBindCardPayStatusKey(String only) {
        return "card:bind:status:" + only;
    }

    /**
     * 获取支付办理联盟卡参数key
     *
     * @param only
     * @return
     */
    public static String getBindCardPayParamKey(String only) {
        return "card:bind:param:" + only;
    }

    /**
     * 获取消费核销状态key
     *
     * @param only
     * @return
     */
    public static String getConsumePayStatusKey(String only) {
        return "consume:status:" + only;
    }

    /**
     * 获取消费核销参数key
     *
     * @param only
     * @return
     */
    public static String getConsumePayParamKey(String only) {
        return "consume:param:" + only;
    }

    /**
     * 根据手机号获取主联盟卡信息
     *
     * @param phone
     * @return
     */
    public static String getUnionCardRootByPhoneKey(String phone) {
        return "cardRoot:phone:" + phone;
    }

    /**
     * 根据卡号获取主联盟卡信息
     *
     * @param cardNo
     * @return
     */
    public static String getUnionCardRootByCardNoKey(String cardNo) {
        return "cardRoot:cardNo:" + cardNo;
    }
}
