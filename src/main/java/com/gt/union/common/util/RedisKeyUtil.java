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

    /*******************************************************************************************************************
     ********************************************* main module **************************************************
     ******************************************************************************************************************/
    public static final String getUnionIdKey(Integer unionId) {
        return new StringBuilder("union:").append(unionId).toString();
    }

    public static final List<String> getUnionIdKey(List<Integer> unionIdList) {
        List<String> result = new ArrayList<>();
        for (Integer unionId : unionIdList) {
            result.add(getUnionIdKey(unionId));
        }
        return result;
    }

    public static final String getUnionValidKey(Integer unionId) {
        return new StringBuilder("union:").append(unionId).append("valid").toString();
    }

    /*******************************************************************************************************************
     ********************************************* main dict module **************************************************
     ******************************************************************************************************************/
    public static final String getDictIdKey(Integer dictId) {
        return new StringBuilder("unionDict:").append(dictId).toString();
    }

    public static final List<String> getDictIdKey(List<Integer> dictIdList) {
        List<String> result = new ArrayList<>();
        for (Integer dictId : dictIdList) {
            result.add(getDictIdKey(dictId));
        }
        return result;
    }

    public static final String getDictUnionIdKey(Integer unionId) {
        return new StringBuilder("unionDict:unionId").append(unionId).toString();
    }

    /*******************************************************************************************************************
     ********************************************* main charge module **************************************************
     ******************************************************************************************************************/
    public static final String getChargeIdKey(Integer chargeId) {
        return new StringBuilder("unionCharge:").append(chargeId).toString();
    }

    public static final List<String> getChargeIdKey(List<Integer> chargeIdList) {
        List<String> result = new ArrayList<>();
        for (Integer chargeId : chargeIdList) {
            result.add(getChargeIdKey(chargeId));
        }
        return result;
    }

    public static final String getChargeUnionIdKey(Integer unionId) {
        return new StringBuilder("unionCharge:unionId").append(unionId).toString();
    }

    /*******************************************************************************************************************
     ********************************************* main transfer module ************************************************
     ******************************************************************************************************************/
    public static final String getTransferIdKey(Integer transferId) {
        return new StringBuilder("unionTransfer:").append(transferId).toString();
    }

    public static final List<String> getTransferIdKey(List<Integer> transferIdList) {
        List<String> result = new ArrayList<>();
        for (Integer transferId : transferIdList) {
            result.add(getTransferIdKey(transferId));
        }
        return result;
    }

    public static final String getTransferUnionIdKey(Integer unionId) {
        return new StringBuilder("unionTransfer:unionId").append(unionId).toString();
    }

    public static final String getTransferFromMemberIdKey(Integer fromMemberId) {
        return new StringBuilder("unionTransfer:fromMemberId").append(fromMemberId).toString();
    }

    public static final String getTransferToMemberIdKey(Integer toMemberId) {
        return new StringBuilder("unionTransfer:toMemberId").append(toMemberId).toString();
    }

    /*******************************************************************************************************************
     ********************************************* main notice module **************************************************
     ******************************************************************************************************************/
    public static final String getNoticeIdKey(Integer noticeId) {
        return new StringBuilder("unionNotice:").append(noticeId).toString();
    }

    public static final List<String> getNoticeIdKey(List<Integer> noticeIdList) {
        List<String> result = new ArrayList<>();
        for (Integer noticeId : noticeIdList) {
            result.add(getNoticeIdKey(noticeId));
        }
        return result;
    }

    public static final String getNoticeUnionIdKey(Integer unionId) {
        return new StringBuilder("unionNotice:unionId").append(unionId).toString();
    }

    /*******************************************************************************************************************
     ********************************************* opportunity module **************************************************
     ******************************************************************************************************************/
    public static final String getOpportunityIdKey(Integer opportunityId) {
        return new StringBuilder("opportunity:").append(opportunityId).toString();
    }

    public static final List<String> getOpportunityIdKey(List<Integer> opportunityIdList) {
        List<String> result = new ArrayList<>();
        for (Integer opportunityId : opportunityIdList) {
            result.add(getOpportunityIdKey(opportunityId));
        }
        return result;
    }

    public static final String getOpportunityFromMemberIdKey(Integer fromMemberId) {
        return new StringBuilder("opportunity:fromMemberId").append(fromMemberId).toString();
    }

    public static final String getOpportunityToMemberIdKey(Integer toMemberId) {
        return new StringBuilder("opportunity:toMemberId").append(toMemberId).toString();
    }

    /*******************************************************************************************************************
     ********************************************* opportunity brokerage ratio module **************************************************
     ******************************************************************************************************************/
    public static final String getRatioIdKey(Integer ratioId) {
        return new StringBuilder("opportunityRatio:").append(ratioId).toString();
    }

    public static final List<String> getRatioIdKey(List<Integer> ratioIdList) {
        List<String> result = new ArrayList<>();
        for (Integer ratioId : ratioIdList) {
            result.add(getRatioIdKey(ratioId));
        }
        return result;
    }

    public static final String getRatioFromMemberIdKey(Integer fromMemberId) {
        return new StringBuilder("opportunityRatio:fromMemberId").append(fromMemberId).toString();
    }

    public static final String getRatioToMemberIdKey(Integer toMemberId) {
        return new StringBuilder("opportunityRatio:toMemberId").append(toMemberId).toString();
    }

    /*******************************************************************************************************************
     ********************************************* preferential project module **************************************************
     ******************************************************************************************************************/
    public static final String getProjectIdKey(Integer projectId) {
        return new StringBuilder("preferentialProject:").append(projectId).toString();
    }

    public static final List<String> getProjectIdKey(List<Integer> projectIdList) {
        List<String> result = new ArrayList<>();
        for (Integer projectId : projectIdList) {
            result.add(getProjectIdKey(projectId));
        }
        return result;
    }

    public static final String getProjectMemberIdKey(Integer memberId) {
        return new StringBuilder("preferentialProject:memberId").append(memberId).toString();
    }

    /*******************************************************************************************************************
     ********************************************* preferential item module **************************************************
     ******************************************************************************************************************/
    public static final String getItemIdKey(Integer itemId) {
        return new StringBuilder("preferentialItem:").append(itemId).toString();
    }

    public static final List<String> getItemIdKey(List<Integer> itemIdList) {
        List<String> result = new ArrayList<>();
        for (Integer itemId : itemIdList) {
            result.add(getItemIdKey(itemId));
        }
        return result;
    }

    public static final String getItemProjectIdKey(Integer projectId) {
        return new StringBuilder("preferentialItem:projectId").append(projectId).toString();
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
