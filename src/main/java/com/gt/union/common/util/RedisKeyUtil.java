package com.gt.union.common.util;

import java.util.ArrayList;
import java.util.List;

/**
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

    /*******************************************************************************************************************
     ********************************************* member module *******************************************************
     ******************************************************************************************************************/
    public static final String getMemberIdKey(Integer memberId) {
        return new StringBuilder("member:").append(memberId).toString();
    }

    public static final List<String> getMemberIdKey(List<Integer> memberIdList) {
        List<String> result = new ArrayList<>();
        for (Integer memberId : memberIdList) {
            result.add(getMemberIdKey(memberId));
        }
        return result;
    }

    public static final String getMemberBusIdKey(Integer busId) {
        return new StringBuilder("member:busId:").append(busId).toString();
    }

    public static final String getMemberUnionIdKey(Integer unionId) {
        return new StringBuilder("member:unionId:").append(unionId).toString();
    }

    /*******************************************************************************************************************
     ********************************************* member out module ***************************************************
     ******************************************************************************************************************/
    public static final String getMemberOutIdKey(Integer outId) {
        return new StringBuilder("memberOut:").append(outId).toString();
    }

    public static final List<String> getMemberOutIdKey(List<Integer> outIdList) {
        List<String> result = new ArrayList<>();
        for (Integer outId : outIdList) {
            result.add(getMemberOutIdKey(outId));
        }
        return result;
    }

    public static final String getMemberOutApplyMemberIdKey(Integer applyMemberId) {
        return new StringBuilder("memberOut:applyMemberId:").append(applyMemberId).toString();
    }

    /*******************************************************************************************************************
     ********************************************* member join module **************************************************
     ******************************************************************************************************************/
    public static final String getMemberJoinIdKey(Integer joinId) {
        return new StringBuilder("memberJoin:").append(joinId).toString();
    }

    public static final List<String> getMemberJoinIdKey(List<Integer> joinIdList) {
        List<String> result = new ArrayList<>();
        for (Integer joinId : joinIdList) {
            result.add(getMemberJoinIdKey(joinId));
        }
        return result;
    }

    public static final String getMemberJoinApplyMemberIdKey(Integer applyMemberId) {
        return new StringBuilder("memberJoin:applyMemberId:").append(applyMemberId).toString();
    }

    public static final String getMemberJoinRecommendMemberIdKey(Integer recommendMemberId) {
        return new StringBuilder("memberJoin:recommendMemberId:").append(recommendMemberId).toString();
    }

    /*******************************************************************************************************************
     ********************************************* member discount module **************************************************
     ******************************************************************************************************************/
    public static final String getMemberDiscountIdKey(Integer discountId) {
        return new StringBuilder("memberDiscount:").append(discountId).toString();
    }

    public static final List<String> getMemberDiscountIdKey(List<Integer> discountIdList) {
        List<String> result = new ArrayList<>();
        for (Integer discountId : discountIdList) {
            result.add(getMemberDiscountIdKey(discountId));
        }
        return result;
    }

    public static final String getMemberDiscountFromMemberIdKey(Integer fromMemberId) {
        return new StringBuilder("memberDiscount:fromMemberId:").append(fromMemberId).toString();
    }

    public static final String getMemberDiscountToMemberIdKey(Integer toMemberId) {
        return new StringBuilder("memberDiscount:toMemberId:").append(toMemberId).toString();
    }

    /**
     * 获取封装的unionMainValid key
     *
     * @param unionId
     * @return
     */
    public static final String getMainValidKey(Integer unionId) {
        return getMainValidKey(String.valueOf(unionId));
    }

    public static final String getMainValidKey(String unionId) {
        return "main:" + unionId + ":valid";
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
}
