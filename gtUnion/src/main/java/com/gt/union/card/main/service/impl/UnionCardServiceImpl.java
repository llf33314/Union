package com.gt.union.card.main.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.api.client.pay.WxPayService;
import com.gt.union.api.client.pay.entity.PayParam;
import com.gt.union.api.client.sms.SmsService;
import com.gt.union.api.client.socket.SocketService;
import com.gt.union.card.activity.constant.ActivityConstant;
import com.gt.union.card.activity.entity.UnionCardActivity;
import com.gt.union.card.activity.service.IUnionCardActivityService;
import com.gt.union.card.main.constant.CardConstant;
import com.gt.union.card.main.entity.UnionCard;
import com.gt.union.card.main.entity.UnionCardFan;
import com.gt.union.card.main.entity.UnionCardRecord;
import com.gt.union.card.main.mapper.UnionCardMapper;
import com.gt.union.card.main.service.IUnionCardApplyService;
import com.gt.union.card.main.service.IUnionCardFanService;
import com.gt.union.card.main.service.IUnionCardRecordService;
import com.gt.union.card.main.service.IUnionCardService;
import com.gt.union.card.main.util.UnionCardCacheUtil;
import com.gt.union.card.main.vo.CardApplyVO;
import com.gt.union.card.main.vo.CardPhoneVO;
import com.gt.union.card.project.constant.ProjectConstant;
import com.gt.union.card.project.entity.UnionCardProject;
import com.gt.union.card.project.service.IUnionCardProjectService;
import com.gt.union.card.sharing.entity.UnionCardSharingRatio;
import com.gt.union.card.sharing.entity.UnionCardSharingRecord;
import com.gt.union.card.sharing.service.IUnionCardSharingRatioService;
import com.gt.union.card.sharing.service.IUnionCardSharingRecordService;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.constant.ConfigConstant;
import com.gt.union.common.constant.SmsCodeConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.*;
import com.gt.union.opportunity.brokerage.constant.BrokerageConstant;
import com.gt.union.opportunity.brokerage.entity.UnionBrokerageIncome;
import com.gt.union.opportunity.brokerage.service.IUnionBrokerageIncomeService;
import com.gt.union.union.main.entity.UnionMain;
import com.gt.union.union.main.service.IUnionMainService;
import com.gt.union.union.main.vo.UnionPayVO;
import com.gt.union.union.member.constant.MemberConstant;
import com.gt.union.union.member.entity.UnionMember;
import com.gt.union.union.member.service.IUnionMemberService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * 联盟卡 服务实现类
 *
 * @author linweicong
 * @version 2017-11-23 17:39:04
 */
@Service
public class UnionCardServiceImpl extends ServiceImpl<UnionCardMapper, UnionCard> implements IUnionCardService {
    private Logger logger = Logger.getLogger(UnionCardServiceImpl.class);

    @Autowired
    private RedisCacheUtil redisCacheUtil;

    @Autowired
    private IUnionCardFanService unionCardFanService;

    @Autowired
    private IUnionMainService unionMainService;

    @Autowired
    private IUnionMemberService unionMemberService;

    @Autowired
    private IUnionCardActivityService unionCardActivityService;

    @Autowired
    private IUnionCardProjectService unionCardProjectService;

    @Autowired
    private WxPayService wxPayService;

    @Autowired
    private SmsService smsService;

    @Autowired
    private IUnionCardRecordService unionCardRecordService;

    @Autowired
    private IUnionCardSharingRatioService unionCardSharingRatioService;

    @Autowired
    private IUnionCardSharingRecordService unionCardSharingRecordService;

    @Autowired
    private IUnionBrokerageIncomeService unionBrokerageIncomeService;

    @Autowired
    private SocketService socketService;

    //***************************************** Domain Driven Design - get *********************************************

    @Override
    public CardApplyVO getCardApplyVOByBusIdAndFanId(Integer busId, Integer fanId, Integer optUnionId) throws Exception {
        if (fanId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // （1）	判断fanId有效性
        UnionCardFan fan = unionCardFanService.getById(fanId);
        if (fan == null) {
            throw new BusinessException("找不到粉丝信息");
        }
        // （2）	获取商家有效的union
        List<UnionMain> busUnionList = unionMainService.listMyValidWriteByBusId(busId);
        // （3）	过滤掉粉丝已办卡，且商家没有新活动卡的union
        List<UnionMain> optionUnionList = new ArrayList<>();
        if (ListUtil.isNotEmpty(busUnionList)) {
            for (UnionMain union : busUnionList) {
                Integer unionId = union.getId();
                UnionMember member = unionMemberService.getReadByBusIdAndUnionId(busId, unionId);
                if (member == null) {
                    throw new BusinessException("找不到盟员信息");
                }

                UnionCard discountCard = getValidDiscountCardByUnionIdAndFanId(unionId, fanId);
                if (discountCard == null) {
                    optionUnionList.add(union);
                    continue;
                }

                List<UnionCardActivity> sellingActivityList = unionCardActivityService.listByUnionIdAndStatus(unionId, ActivityConstant.STATUS_SELLING);
                if (ListUtil.isNotEmpty(sellingActivityList)) {
                    for (UnionCardActivity activity : sellingActivityList) {
                        UnionCardProject project = unionCardProjectService.getByUnionIdAndMemberIdAndActivityId(unionId, member.getId(), activity.getId());
                        if (project != null && ProjectConstant.STATUS_ACCEPT == project.getStatus()) {
                            UnionCard activityCard = getValidActivityCardByUnionIdAndFanIdAndActivityId(unionId, fanId, activity.getId());
                            if (activityCard == null) {
                                optionUnionList.add(union);
                                break;
                            }
                        }
                    }
                }
            }
        }
        if (ListUtil.isEmpty(optionUnionList)) {
            return null;
        }
        // （4）	如果unionId存在且有效，则当前联盟为对应union；否则，取第一个union
        CardApplyVO result = new CardApplyVO();
        result.setUnionList(optionUnionList);
        UnionMain currentUnion = optionUnionList.get(0);
        if (optUnionId != null) {
            for (UnionMain optionUnion : optionUnionList) {
                if (optUnionId.equals(optionUnion.getId())) {
                    currentUnion = optionUnion;
                    break;
                }
            }
        }
        if (currentUnion == null) {
            return result;
        }
        result.setCurrentUnion(currentUnion);

        UnionMember currentMember = unionMemberService.getReadByBusIdAndUnionId(busId, currentUnion.getId());
        result.setCurrentMember(currentMember);

        // （5）	获取当前联盟粉丝的折扣卡和活动卡情况，若已办理，则不显示；否则，要求活动卡在售卖中状态
        UnionCard discountCard = getValidDiscountCardByUnionIdAndFanId(currentUnion.getId(), fanId);
        result.setIsDiscountCard(discountCard == null ? CommonConstant.COMMON_YES : CommonConstant.COMMON_NO);

        List<UnionCardActivity> sellingActivityList = unionCardActivityService.listByUnionIdAndStatus(currentUnion.getId(), ActivityConstant.STATUS_SELLING);
        if (ListUtil.isNotEmpty(sellingActivityList)) {
            List<UnionCardActivity> activityList = new ArrayList<>();
            for (UnionCardActivity activity : sellingActivityList) {
                UnionCardProject project = unionCardProjectService.getByUnionIdAndMemberIdAndActivityId(currentUnion.getId(), currentMember.getId(), activity.getId());
                if (project != null && ProjectConstant.STATUS_ACCEPT == project.getStatus()) {
                    UnionCard activityCard = getValidActivityCardByUnionIdAndFanIdAndActivityId(currentUnion.getId(), fanId, activity.getId());
                    if (activityCard != null) {
                        continue;
                    }
                    Integer activityCardCount = countByUnionIdAndActivityId(currentUnion.getId(), activity.getId());
                    if (activityCardCount < activity.getAmount()) {
                        activityList.add(activity);
                    }
                }
            }
            result.setActivityList(activityList);
        }

        return result;
    }

    @Override
    public UnionCard getValidDiscountCardByUnionIdAndFanId(Integer unionId, Integer fanId) throws Exception {
        if (unionId == null || fanId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionCard> result = listValidByUnionIdAndFanIdAndType(unionId, fanId, CardConstant.TYPE_DISCOUNT);

        return ListUtil.isNotEmpty(result) ? result.get(0) : null;
    }

    @Override
    public UnionCard getValidActivityCardByUnionIdAndFanIdAndActivityId(Integer unionId, Integer fanId, Integer activityId) throws Exception {
        if (unionId == null || fanId == null || activityId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionCard> result = listValidByUnionIdAndFanIdAndType(unionId, activityId, CardConstant.TYPE_ACTIVITY);

        return ListUtil.isNotEmpty(result) ? result.get(0) : null;
    }

    //***************************************** Domain Driven Design - list ********************************************

    @Override
    public List<UnionCard> listValidByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCard> entityWrapper = new EntityWrapper<>();
        entityWrapper.ge("validity", DateUtil.getCurrentDate())
                .eq("union_id", unionId)
                .eq("del_status", CommonConstant.DEL_STATUS_NO);

        return selectList(entityWrapper);
    }

    @Override
    public List<UnionCard> listValidByUnionIdAndFanId(Integer unionId, Integer fanId) throws Exception {
        if (unionId == null || fanId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionCard> result = listByFanId(fanId);
        result = filterByUnionId(result, unionId);
        result = filterByValidity(result);

        return result;
    }

    @Override
    public List<UnionCard> listValidByUnionIdAndFanIdAndType(Integer unionId, Integer fanId, Integer type) throws Exception {
        if (unionId == null || fanId == null || type == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionCard> result = listValidByUnionIdAndFanId(unionId, fanId);
        result = filterByType(result, type);

        return result;
    }

    @Override
    public List<UnionCard> listValidByUnionIdAndActivityId(Integer unionId, Integer activityId) throws Exception {
        if (unionId == null || activityId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionCard> result = listByActivityId(activityId);
        result = filterByUnionId(result, unionId);
        result = filterByType(result, CardConstant.TYPE_ACTIVITY);
        result = filterByValidity(result);

        return result;
    }

    @Override
    public List<UnionCard> listValidByFanId(Integer fanId) throws Exception {
        if (fanId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionCard> result = listByFanId(fanId);
        result = filterByValidity(result);

        return result;
    }

    //***************************************** Domain Driven Design - save ********************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UnionPayVO saveApplyByBusIdAndUnionIdAndFanId(Integer busId, Integer unionId, Integer fanId, List<Integer> activityIdList, IUnionCardApplyService unionCardApplyService) throws Exception {
        if (busId == null || unionId == null || fanId == null || activityIdList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // （1）	判断union有效性和member写权限
        UnionMain union = unionMainService.getById(unionId);
        if (!unionMainService.isUnionValid(union)) {
            throw new BusinessException(CommonConstant.UNION_INVALID);
        }
        UnionMember member = unionMemberService.getWriteByBusIdAndUnionId(busId, unionId);
        if (member == null) {
            throw new BusinessException(CommonConstant.UNION_WRITE_REJECT);
        }
        // （2）	判断fanId有效性
        UnionCardFan fan = unionCardFanService.getById(fanId);
        if (fan == null) {
            throw new BusinessException("找不到粉丝信息");
        }
        // （3）	判断是否已办理过折扣卡，如果没有，则自动办理
        Date currentDate = DateUtil.getCurrentDate();
        UnionCard discountCard = getValidDiscountCardByUnionIdAndFanId(unionId, fanId);
        UnionCard saveDiscountCard = null;
        if (discountCard == null) {
            saveDiscountCard = new UnionCard();
            saveDiscountCard.setDelStatus(CommonConstant.DEL_STATUS_NO);
            saveDiscountCard.setCreateTime(currentDate);
            saveDiscountCard.setType(CardConstant.TYPE_DISCOUNT);
            saveDiscountCard.setFanId(fanId);
            saveDiscountCard.setMemberId(member.getId());
            saveDiscountCard.setUnionId(unionId);
            saveDiscountCard.setName(union.getName() + "折扣卡");
            saveDiscountCard.setValidity(DateUtil.addYears(currentDate, 10));
        }
        if (ListUtil.isEmpty(activityIdList)) {
            if (saveDiscountCard != null) {
                save(saveDiscountCard);
                return null;
            }
            throw new BusinessException("请选择活动卡信息");
        }
        // （4）	判断activity有效性
        List<UnionCard> saveCardList = new ArrayList<>();
        if (saveDiscountCard != null) {
            saveCardList.add(saveDiscountCard);
        }
        BigDecimal payMoneySum = BigDecimal.ZERO;
        for (Integer activityId : activityIdList) {
            UnionCardActivity activity = unionCardActivityService.getByIdAndUnionId(activityId, unionId);
            if (activity == null) {
                throw new BusinessException("找不到活动卡信息");
            }
            if (ActivityConstant.STATUS_SELLING != unionCardActivityService.getStatus(activity)) {
                throw new BusinessException("活动卡不在售卡状态");
            }
            Integer activityCardCount = countByUnionIdAndActivityId(unionId, activityId);
            if (activityCardCount >= activity.getAmount()) {
                throw new BusinessException("售卡量已达活动卡发行量");
            }
            UnionCard saveActivityCard = new UnionCard();
            saveActivityCard.setDelStatus(CommonConstant.DEL_STATUS_NO);
            saveActivityCard.setCreateTime(currentDate);
            saveActivityCard.setType(CardConstant.TYPE_ACTIVITY);
            saveActivityCard.setFanId(fanId);
            saveActivityCard.setMemberId(member.getId());
            saveActivityCard.setUnionId(unionId);
            saveActivityCard.setActivityId(activityId);
            saveActivityCard.setName(activity.getName());
            saveActivityCard.setValidity(DateUtil.addDays(currentDate, activity.getValidityDay()));
            saveCardList.add(saveActivityCard);

            payMoneySum = BigDecimalUtil.add(payMoneySum, activity.getPrice());
        }
        saveBatch(saveCardList);
        // （6）	新增未付款的联盟卡购买记录，并返回支付链接
        List<UnionCardRecord> saveCardRecordList = new ArrayList<>();
        String orderNo = "LM" + ConfigConstant.PAY_MODEL_CARD + DateUtil.getSerialNumber();
        for (UnionCard saveCard : saveCardList) {
            if (CardConstant.TYPE_ACTIVITY != saveCard.getType()) {
                continue;
            }
            UnionCardActivity activity = unionCardActivityService.getByIdAndUnionId(saveCard.getActivityId(), unionId);

            UnionCardRecord saveCardRecord = new UnionCardRecord();
            saveCardRecord.setDelStatus(CommonConstant.COMMON_NO);
            saveCardRecord.setCreateTime(currentDate);
            saveCardRecord.setFanId(fanId);
            saveCardRecord.setMemberId(member.getId());
            saveCardRecord.setUnionId(unionId);
            saveCardRecord.setCardId(saveCard.getId());
            saveCardRecord.setActivityId(saveCard.getActivityId());
            saveCardRecord.setSysOrderNo(orderNo);
            saveCardRecord.setPayMoney(activity.getPrice());
            saveCardRecord.setPayStatus(CardConstant.PAY_STATUS_PAYING);

            saveCardRecordList.add(saveCardRecord);
        }

        UnionPayVO result = unionCardApplyService.unionCardApply(orderNo, payMoneySum.doubleValue(), busId, unionId, activityIdList);

        unionCardRecordService.saveBatch(saveCardRecordList);
        return result;
    }

    @Override
    public UnionCardFan checkCardPhoneVO(CardPhoneVO vo) throws Exception {
        if (vo == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // （1）	判断phone和code有效性
        String phone = vo.getPhone();
        if (StringUtil.isEmpty(phone)) {
            throw new BusinessException("手机号不能为空");
        }
        if (!StringUtil.isPhone(phone)) {
            throw new BusinessException("手机号错误，请输入正确的手机号");
        }
        String code = vo.getCode();
        if (StringUtil.isEmpty(code)) {
            throw new BusinessException("验证码不能为空");
        }
        if (!smsService.checkPhoneCode(SmsCodeConstant.APPLY_UNION_CARD_TYPE, code, phone)) {
            throw new BusinessException("验证码错误");
        }
        // （2）	根据phone判断fan是否存在，如果已存在，则返回；否则，新增并返回
        UnionCardFan result = unionCardFanService.getOrSaveByPhone(phone);
        return result;
    }

    //***************************************** Domain Driven Design - remove ******************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String updateCallbackByOrderNo(String orderNo, String socketKey, String payType, String payOrderNo, Integer isSuccess) {
        Map<String, Object> result = new HashMap<>(2);
        if (orderNo == null || payType == null || payOrderNo == null || isSuccess == null) {
            result.put("code", -1);
            result.put("msg", "参数缺少");
            return JSONObject.toJSONString(result);
        }

        // （1）	判断recordIds有效性
        try {
            List<UnionCardRecord> updateRecordList = new ArrayList<>();
            List<UnionCardSharingRecord> saveSharingRecordList = new ArrayList<>();
            List<UnionBrokerageIncome> saveIncomeList = new ArrayList<>();
            boolean isRepeat = false;
            Date currentDate = DateUtil.getCurrentDate();
            List<UnionCardRecord> recordList = unionCardRecordService.listByOrderNo(orderNo);
            if (ListUtil.isEmpty(recordList)) {
                throw new BusinessException("没有支付信息");
            }
            for (UnionCardRecord record : recordList) {
                if (record == null) {
                    result.put("code", -1);
                    result.put("msg", "找不到联盟卡购买记录信息");
                    return JSONObject.toJSONString(result);
                }

                Integer payStatus = record.getPayStatus();
                if (CardConstant.PAY_STATUS_SUCCESS == payStatus || CardConstant.PAY_STATUS_FAIL == payStatus || CardConstant.PAY_STATUS_RETURN == payStatus) {
                    isRepeat = true;
                    break;
                }

                UnionCardRecord updateRecord = new UnionCardRecord();
                updateRecord.setId(record.getId());
                updateRecord.setPayStatus(CommonConstant.COMMON_YES == isSuccess ? BrokerageConstant.PAY_STATUS_SUCCESS : BrokerageConstant.PAY_STATUS_FAIL);
                if (payType.equals("0")) {
                    updateRecord.setPayType(CardConstant.PAY_TYPE_WX);
                    updateRecord.setWxOrderNo(payOrderNo);
                } else {
                    updateRecord.setPayType(BrokerageConstant.PAY_TYPE_ALIPAY);
                    updateRecord.setAlipayOrderNo(payOrderNo);
                }
                updateRecordList.add(updateRecord);

                if (CommonConstant.COMMON_YES == isSuccess) {
                    UnionCard card = getById(record.getCardId());
                    UnionMember ownerMember = unionMemberService.getOwnerByUnionId(card.getUnionId());
                    List<UnionCardSharingRatio> sharingRatioList = unionCardSharingRatioService.listByUnionIdAndActivityId(record.getUnionId(), record.getActivityId());
                    if (ListUtil.isEmpty(sharingRatioList)) {
                        // 没有售卡分成比例
                        List<UnionCardProject> projectList = unionCardProjectService.listByUnionIdAndActivityIdAndStatus(record.getUnionId(), record.getActivityId(), ProjectConstant.STATUS_ACCEPT);
                        if (ListUtil.isEmpty(projectList)) {
                            // 没有售卡分成比例，并且没有审核通过的优惠项目
                            UnionCardSharingRecord saveSharingRecord = new UnionCardSharingRecord();
                            saveSharingRecord.setDelStatus(CommonConstant.DEL_STATUS_NO);
                            saveSharingRecord.setCreateTime(currentDate);
                            saveSharingRecord.setSellPrice(record.getPayMoney());
                            saveSharingRecord.setSharingRatio(1.0);
                            saveSharingRecord.setSharingMoney(record.getPayMoney());
                            saveSharingRecord.setSharingMemberId(ownerMember.getId());
                            saveSharingRecord.setFromMemberId(record.getMemberId());
                            saveSharingRecord.setActivityId(record.getActivityId());
                            saveSharingRecord.setCardId(record.getCardId());
                            saveSharingRecord.setFanId(record.getFanId());
                            saveSharingRecord.setUnionId(record.getUnionId());
                            saveSharingRecordList.add(saveSharingRecord);

                            UnionBrokerageIncome saveIncome = new UnionBrokerageIncome();
                            saveIncome.setDelStatus(CommonConstant.DEL_STATUS_NO);
                            saveIncome.setCreateTime(currentDate);
                            saveIncome.setType(BrokerageConstant.INCOME_TYPE_CARD);
                            saveIncome.setMoney(saveSharingRecord.getSharingMoney());
                            saveIncome.setBusId(ownerMember.getBusId());
                            saveIncome.setMemberId(ownerMember.getId());
                            saveIncome.setUnionId(saveSharingRecord.getUnionId());
                            saveIncome.setCardId(saveSharingRecord.getCardId());
                            saveIncomeList.add(saveIncome);
                        } else {
                            // 没有售卡分成比例，并且存在审核通过的优惠项目
                            int sharingMemberCount = 1;
                            List<UnionMember> sharingMemberList = new ArrayList<>();
                            for (UnionCardProject project : projectList) {
                                UnionMember sharingMember = unionMemberService.getWriteByIdAndUnionId(project.getMemberId(), project.getUnionId());
                                if (sharingMember != null) {
                                    sharingMemberList.add(sharingMember);
                                    sharingMemberCount++;
                                }
                            }
                            BigDecimal sharingRatio = BigDecimalUtil.divide(Double.valueOf(1), Double.valueOf(sharingMemberCount));
                            BigDecimal sharingMoney = BigDecimalUtil.multiply(record.getPayMoney(), sharingRatio);
                            BigDecimal sharedMoney = BigDecimal.ZERO;
                            for (UnionMember sharingMember : sharingMemberList) {
                                UnionCardSharingRecord saveSharingRecord = new UnionCardSharingRecord();
                                saveSharingRecord.setDelStatus(CommonConstant.DEL_STATUS_NO);
                                saveSharingRecord.setCreateTime(currentDate);
                                saveSharingRecord.setSellPrice(record.getPayMoney());
                                saveSharingRecord.setSharingRatio(sharingRatio.doubleValue());
                                saveSharingRecord.setSharingMoney(sharingMoney.doubleValue());
                                saveSharingRecord.setSharingMemberId(sharingMember.getId());
                                saveSharingRecord.setFromMemberId(record.getMemberId());
                                saveSharingRecord.setActivityId(record.getActivityId());
                                saveSharingRecord.setCardId(record.getCardId());
                                saveSharingRecord.setFanId(record.getFanId());
                                saveSharingRecord.setUnionId(record.getUnionId());
                                saveSharingRecordList.add(saveSharingRecord);

                                UnionBrokerageIncome saveIncome = new UnionBrokerageIncome();
                                saveIncome.setDelStatus(CommonConstant.DEL_STATUS_NO);
                                saveIncome.setCreateTime(currentDate);
                                saveIncome.setType(BrokerageConstant.INCOME_TYPE_CARD);
                                saveIncome.setMoney(sharingMoney.doubleValue());
                                saveIncome.setBusId(sharingMember.getBusId());
                                saveIncome.setMemberId(sharingMember.getId());
                                saveIncome.setUnionId(saveSharingRecord.getUnionId());
                                saveIncome.setCardId(saveSharingRecord.getCardId());
                                saveIncomeList.add(saveIncome);

                                sharedMoney = BigDecimalUtil.add(sharedMoney, sharingMoney);
                            }
                            BigDecimal surplusSharingMoney = BigDecimalUtil.subtract(record.getPayMoney(), sharedMoney);
                            UnionCardSharingRecord saveSharingRecord = new UnionCardSharingRecord();
                            saveSharingRecord.setDelStatus(CommonConstant.DEL_STATUS_NO);
                            saveSharingRecord.setCreateTime(currentDate);
                            saveSharingRecord.setSellPrice(record.getPayMoney());
                            saveSharingRecord.setSharingRatio(sharingRatio.doubleValue());
                            saveSharingRecord.setSharingMoney(surplusSharingMoney.doubleValue());
                            saveSharingRecord.setSharingMemberId(ownerMember.getId());
                            saveSharingRecord.setFromMemberId(record.getMemberId());
                            saveSharingRecord.setActivityId(record.getActivityId());
                            saveSharingRecord.setCardId(record.getCardId());
                            saveSharingRecord.setFanId(record.getFanId());
                            saveSharingRecord.setUnionId(record.getUnionId());
                            saveSharingRecordList.add(saveSharingRecord);

                            UnionBrokerageIncome saveIncome = new UnionBrokerageIncome();
                            saveIncome.setDelStatus(CommonConstant.DEL_STATUS_NO);
                            saveIncome.setCreateTime(currentDate);
                            saveIncome.setType(BrokerageConstant.INCOME_TYPE_CARD);
                            saveIncome.setMoney(surplusSharingMoney.doubleValue());
                            saveIncome.setBusId(ownerMember.getBusId());
                            saveIncome.setMemberId(ownerMember.getId());
                            saveIncome.setUnionId(saveSharingRecord.getUnionId());
                            saveIncome.setCardId(saveSharingRecord.getCardId());
                            saveIncomeList.add(saveIncome);
                        }
                    } else {
                        // 存在售卡分成比例
                        Double payMoney = record.getPayMoney();
                        BigDecimal sharedMoney = BigDecimal.ZERO;
                        BigDecimal sharedRatio = BigDecimal.ZERO;
                        for (UnionCardSharingRatio sharingRatio : sharingRatioList) {
                            UnionMember sharingMember = unionMemberService.getWriteByIdAndUnionId(sharingRatio.getMemberId(), sharingRatio.getUnionId());
                            if (MemberConstant.IS_UNION_OWNER_YES == sharingMember.getIsUnionOwner()) {
                                continue;
                            }
                            Double ratio = sharingRatio.getRatio();
                            BigDecimal sharingMoney = BigDecimalUtil.multiply(payMoney, ratio);

                            UnionCardSharingRecord saveSharingRecord = new UnionCardSharingRecord();
                            saveSharingRecord.setDelStatus(CommonConstant.DEL_STATUS_NO);
                            saveSharingRecord.setCreateTime(currentDate);
                            saveSharingRecord.setSellPrice(record.getPayMoney());
                            saveSharingRecord.setSharingRatio(ratio);
                            saveSharingRecord.setSharingMoney(sharingMoney.doubleValue());
                            saveSharingRecord.setSharingMemberId(sharingMember.getId());
                            saveSharingRecord.setFromMemberId(record.getMemberId());
                            saveSharingRecord.setActivityId(record.getActivityId());
                            saveSharingRecord.setCardId(record.getCardId());
                            saveSharingRecord.setFanId(record.getFanId());
                            saveSharingRecord.setUnionId(record.getUnionId());
                            saveSharingRecordList.add(saveSharingRecord);

                            UnionBrokerageIncome saveIncome = new UnionBrokerageIncome();
                            saveIncome.setDelStatus(CommonConstant.DEL_STATUS_NO);
                            saveIncome.setCreateTime(currentDate);
                            saveIncome.setType(BrokerageConstant.INCOME_TYPE_CARD);
                            saveIncome.setMoney(sharingMoney.doubleValue());
                            saveIncome.setBusId(sharingMember.getBusId());
                            saveIncome.setMemberId(sharingMember.getId());
                            saveIncome.setUnionId(saveSharingRecord.getUnionId());
                            saveIncome.setCardId(saveSharingRecord.getCardId());
                            saveIncomeList.add(saveIncome);

                            sharedMoney = BigDecimalUtil.add(sharedMoney, sharingMoney);
                            sharedRatio = BigDecimalUtil.add(sharedRatio, ratio);
                        }
                        BigDecimal surplusSharingMoney = BigDecimalUtil.subtract(payMoney, sharedMoney);
                        BigDecimal surplusSharingRatio = BigDecimalUtil.subtract(1.0, sharedRatio);
                        UnionCardSharingRecord saveSharingRecord = new UnionCardSharingRecord();
                        saveSharingRecord.setDelStatus(CommonConstant.DEL_STATUS_NO);
                        saveSharingRecord.setCreateTime(currentDate);
                        saveSharingRecord.setSellPrice(record.getPayMoney());
                        saveSharingRecord.setSharingRatio(surplusSharingRatio.doubleValue());
                        saveSharingRecord.setSharingMoney(surplusSharingMoney.doubleValue());
                        saveSharingRecord.setSharingMemberId(ownerMember.getId());
                        saveSharingRecord.setFromMemberId(record.getMemberId());
                        saveSharingRecord.setActivityId(record.getActivityId());
                        saveSharingRecord.setCardId(record.getCardId());
                        saveSharingRecord.setFanId(record.getFanId());
                        saveSharingRecord.setUnionId(record.getUnionId());
                        saveSharingRecordList.add(saveSharingRecord);

                        UnionBrokerageIncome saveIncome = new UnionBrokerageIncome();
                        saveIncome.setDelStatus(CommonConstant.DEL_STATUS_NO);
                        saveIncome.setCreateTime(currentDate);
                        saveIncome.setType(BrokerageConstant.INCOME_TYPE_CARD);
                        saveIncome.setMoney(surplusSharingMoney.doubleValue());
                        saveIncome.setBusId(ownerMember.getBusId());
                        saveIncome.setMemberId(ownerMember.getId());
                        saveIncome.setUnionId(saveSharingRecord.getUnionId());
                        saveIncome.setCardId(saveSharingRecord.getCardId());
                        saveIncomeList.add(saveIncome);
                    }
                }
            }

            // （2）	如果recordIds的支付状态存在已处理，则直接返回成功
            if (isRepeat) {
                result.put("code", 0);
                result.put("msg", "重复处理");
                return JSONObject.toJSONString(result);
            }

            if (ListUtil.isNotEmpty(updateRecordList)) {
                unionCardRecordService.updateBatch(updateRecordList);
            }
            if (ListUtil.isNotEmpty(saveSharingRecordList)) {
                unionCardSharingRecordService.saveBatch(saveSharingRecordList);
            }
            if (ListUtil.isNotEmpty(saveIncomeList)) {
                unionBrokerageIncomeService.saveBatch(saveIncomeList);
            }

            // socket通知
            if(StringUtil.isNotEmpty(socketKey)){
                socketService.socketPaySendMessage(socketKey, isSuccess, null);
            }
            result.put("code", 0);
            result.put("msg", "成功");
            return JSONObject.toJSONString(result);
        } catch (Exception e) {
            logger.error("", e);
            result.put("code", -1);
            result.put("msg", e.getMessage());
            return JSONObject.toJSONString(result);
        }
    }

    //***************************************** Domain Driven Design - update ******************************************

    //***************************************** Domain Driven Design - count *******************************************

    @Override
    public Integer countByUnionIdAndActivityId(Integer unionId, Integer activityId) throws Exception {
        if (unionId == null || activityId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionCard> result = listByActivityId(activityId);
        result = filterByUnionId(result, unionId);

        return ListUtil.isNotEmpty(result) ? result.size() : 0;
    }

    //***************************************** Domain Driven Design - boolean *****************************************

    @Override
    public boolean existValidByUnionIdAndFanIdAndType(Integer unionId, Integer fanId, Integer type) throws Exception {
        if (unionId == null || fanId == null || type == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionCard> result = listValidByUnionIdAndFanIdAndType(unionId, fanId, type);

        return ListUtil.isNotEmpty(result);
    }

    //***************************************** Domain Driven Design - filter ******************************************

    @Override
    public List<UnionCard> filterByType(List<UnionCard> cardList, Integer type) throws Exception {
        if (cardList == null || type == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionCard> result = new ArrayList<>();
        for (UnionCard card : cardList) {
            if (type.equals(card.getType())) {
                result.add(card);
            }
        }

        return result;
    }

    @Override
    public List<UnionCard> filterByUnionId(List<UnionCard> cardList, Integer unionId) throws Exception {
        if (cardList == null || unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionCard> result = new ArrayList<>();
        for (UnionCard card : cardList) {
            if (unionId.equals(card.getUnionId())) {
                result.add(card);
            }
        }

        return result;
    }

    @Override
    public List<UnionCard> filterByValidity(List<UnionCard> cardList) throws Exception {
        if (cardList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionCard> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(cardList)) {
            Date currentDate = DateUtil.getCurrentDate();
            for (UnionCard card : cardList) {
                if (currentDate.compareTo(card.getValidity()) < 0) {
                    result.add(card);
                }
            }
        }

        return result;
    }

    //***************************************** Object As a Service - get **********************************************
    @Override
    public UnionCard getById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionCard result;
        // (1)cache
        String idKey = UnionCardCacheUtil.getIdKey(id);
        if (redisCacheUtil.exists(idKey)) {
            String tempStr = redisCacheUtil.get(idKey);
            result = JSONArray.parseObject(tempStr, UnionCard.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionCard> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("id", id)
                .eq("del_status", CommonConstant.DEL_STATUS_NO);
        result = selectOne(entityWrapper);
        setCache(result, id);
        return result;
    }

    //***************************************** Object As a Service - list *********************************************

    public List<UnionCard> listByMemberId(Integer memberId) throws Exception {
        if (memberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionCard> result;
        // (1)cache
        String memberIdKey = UnionCardCacheUtil.getMemberIdKey(memberId);
        if (redisCacheUtil.exists(memberIdKey)) {
            String tempStr = redisCacheUtil.get(memberIdKey);
            result = JSONArray.parseArray(tempStr, UnionCard.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionCard> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("member_id", memberId)
                .eq("del_status", CommonConstant.COMMON_NO);
        result = selectList(entityWrapper);
        setCache(result, memberId, UnionCardCacheUtil.TYPE_MEMBER_ID);
        return result;
    }

    public List<UnionCard> listByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionCard> result;
        // (1)cache
        String unionIdKey = UnionCardCacheUtil.getUnionIdKey(unionId);
        if (redisCacheUtil.exists(unionIdKey)) {
            String tempStr = redisCacheUtil.get(unionIdKey);
            result = JSONArray.parseArray(tempStr, UnionCard.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionCard> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("union_id", unionId)
                .eq("del_status", CommonConstant.COMMON_NO);
        result = selectList(entityWrapper);
        setCache(result, unionId, UnionCardCacheUtil.TYPE_UNION_ID);
        return result;
    }

    public List<UnionCard> listByFanId(Integer fanId) throws Exception {
        if (fanId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionCard> result;
        // (1)cache
        String fanIdKey = UnionCardCacheUtil.getFanIdKey(fanId);
        if (redisCacheUtil.exists(fanIdKey)) {
            String tempStr = redisCacheUtil.get(fanIdKey);
            result = JSONArray.parseArray(tempStr, UnionCard.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionCard> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("fan_id", fanId)
                .eq("del_status", CommonConstant.COMMON_NO);
        result = selectList(entityWrapper);
        setCache(result, fanId, UnionCardCacheUtil.TYPE_FAN_ID);
        return result;
    }

    public List<UnionCard> listByActivityId(Integer activityId) throws Exception {
        if (activityId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionCard> result;
        // (1)cache
        String activityIdKey = UnionCardCacheUtil.getActivityIdKey(activityId);
        if (redisCacheUtil.exists(activityIdKey)) {
            String tempStr = redisCacheUtil.get(activityIdKey);
            result = JSONArray.parseArray(tempStr, UnionCard.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionCard> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("activity_id", activityId)
                .eq("del_status", CommonConstant.COMMON_NO);
        result = selectList(entityWrapper);
        setCache(result, activityId, UnionCardCacheUtil.TYPE_ACTIVITY_ID);
        return result;
    }

    //***************************************** Object As a Service - save *********************************************

    @Transactional(rollbackFor = Exception.class)
    public void save(UnionCard newUnionCard) throws Exception {
        if (newUnionCard == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        insert(newUnionCard);
        removeCache(newUnionCard);
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveBatch(List<UnionCard> newUnionCardList) throws Exception {
        if (newUnionCardList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        insertBatch(newUnionCardList);
        removeCache(newUnionCardList);
    }

    //***************************************** Object As a Service - remove *******************************************

    @Transactional(rollbackFor = Exception.class)
    public void removeById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        UnionCard unionCard = getById(id);
        removeCache(unionCard);
        // (2)remove in db logically
        UnionCard removeUnionCard = new UnionCard();
        removeUnionCard.setId(id);
        removeUnionCard.setDelStatus(CommonConstant.DEL_STATUS_YES);
        updateById(removeUnionCard);
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeBatchById(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<UnionCard> unionCardList = new ArrayList<>();
        for (Integer id : idList) {
            UnionCard unionCard = getById(id);
            unionCardList.add(unionCard);
        }
        removeCache(unionCardList);
        // (2)remove in db logically
        List<UnionCard> removeUnionCardList = new ArrayList<>();
        for (Integer id : idList) {
            UnionCard removeUnionCard = new UnionCard();
            removeUnionCard.setId(id);
            removeUnionCard.setDelStatus(CommonConstant.DEL_STATUS_YES);
            removeUnionCardList.add(removeUnionCard);
        }
        updateBatchById(removeUnionCardList);
    }

    //***************************************** Object As a Service - update *******************************************

    @Transactional(rollbackFor = Exception.class)
    public void update(UnionCard updateUnionCard) throws Exception {
        if (updateUnionCard == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        Integer id = updateUnionCard.getId();
        UnionCard unionCard = getById(id);
        removeCache(unionCard);
        // (2)update db
        updateById(updateUnionCard);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateBatch(List<UnionCard> updateUnionCardList) throws Exception {
        if (updateUnionCardList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<Integer> idList = new ArrayList<>();
        for (UnionCard updateUnionCard : updateUnionCardList) {
            idList.add(updateUnionCard.getId());
        }
        List<UnionCard> unionCardList = new ArrayList<>();
        for (Integer id : idList) {
            UnionCard unionCard = getById(id);
            unionCardList.add(unionCard);
        }
        removeCache(unionCardList);
        // (2)update db
        updateBatchById(updateUnionCardList);
    }

    //***************************************** Object As a Service - cache support ************************************

    private void setCache(UnionCard newUnionCard, Integer id) {
        if (id == null) {
            //do nothing,just in case
            return;
        }
        String idKey = UnionCardCacheUtil.getIdKey(id);
        redisCacheUtil.set(idKey, newUnionCard);
    }

    private void setCache(List<UnionCard> newUnionCardList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            //do nothing,just in case
            return;
        }
        String foreignIdKey = null;
        switch (foreignIdType) {
            case UnionCardCacheUtil.TYPE_MEMBER_ID:
                foreignIdKey = UnionCardCacheUtil.getMemberIdKey(foreignId);
                break;
            case UnionCardCacheUtil.TYPE_UNION_ID:
                foreignIdKey = UnionCardCacheUtil.getUnionIdKey(foreignId);
                break;
            case UnionCardCacheUtil.TYPE_FAN_ID:
                foreignIdKey = UnionCardCacheUtil.getFanIdKey(foreignId);
                break;
            case UnionCardCacheUtil.TYPE_ACTIVITY_ID:
                foreignIdKey = UnionCardCacheUtil.getActivityIdKey(foreignId);
                break;
            default:
                break;
        }
        if (foreignIdKey != null) {
            redisCacheUtil.set(foreignIdKey, newUnionCardList);
        }
    }

    private void removeCache(UnionCard unionCard) {
        if (unionCard == null) {
            return;
        }
        Integer id = unionCard.getId();
        String idKey = UnionCardCacheUtil.getIdKey(id);
        redisCacheUtil.remove(idKey);

        Integer memberId = unionCard.getMemberId();
        if (memberId != null) {
            String memberIdKey = UnionCardCacheUtil.getMemberIdKey(memberId);
            redisCacheUtil.remove(memberIdKey);
        }

        Integer unionId = unionCard.getUnionId();
        if (unionId != null) {
            String unionIdKey = UnionCardCacheUtil.getUnionIdKey(unionId);
            redisCacheUtil.remove(unionIdKey);
        }

        Integer fanId = unionCard.getFanId();
        if (fanId != null) {
            String fanIdKey = UnionCardCacheUtil.getFanIdKey(fanId);
            redisCacheUtil.remove(fanIdKey);
        }

        Integer activityId = unionCard.getActivityId();
        if (activityId != null) {
            String activityIdKey = UnionCardCacheUtil.getActivityIdKey(activityId);
            redisCacheUtil.remove(activityIdKey);
        }
    }

    private void removeCache(List<UnionCard> unionCardList) {
        if (ListUtil.isEmpty(unionCardList)) {
            return;
        }
        List<Integer> idList = new ArrayList<>();
        for (UnionCard unionCard : unionCardList) {
            idList.add(unionCard.getId());
        }
        List<String> idKeyList = UnionCardCacheUtil.getIdKey(idList);
        redisCacheUtil.remove(idKeyList);

        List<String> memberIdKeyList = getForeignIdKeyList(unionCardList, UnionCardCacheUtil.TYPE_MEMBER_ID);
        if (ListUtil.isNotEmpty(memberIdKeyList)) {
            redisCacheUtil.remove(memberIdKeyList);
        }

        List<String> unionIdKeyList = getForeignIdKeyList(unionCardList, UnionCardCacheUtil.TYPE_UNION_ID);
        if (ListUtil.isNotEmpty(unionIdKeyList)) {
            redisCacheUtil.remove(unionIdKeyList);
        }

        List<String> fanIdKeyList = getForeignIdKeyList(unionCardList, UnionCardCacheUtil.TYPE_FAN_ID);
        if (ListUtil.isNotEmpty(fanIdKeyList)) {
            redisCacheUtil.remove(fanIdKeyList);
        }

        List<String> activityIdKeyList = getForeignIdKeyList(unionCardList, UnionCardCacheUtil.TYPE_ACTIVITY_ID);
        if (ListUtil.isNotEmpty(activityIdKeyList)) {
            redisCacheUtil.remove(activityIdKeyList);
        }
    }

    private List<String> getForeignIdKeyList(List<UnionCard> unionCardList, int foreignIdType) {
        List<String> result = new ArrayList<>();
        switch (foreignIdType) {
            case UnionCardCacheUtil.TYPE_MEMBER_ID:
                for (UnionCard unionCard : unionCardList) {
                    Integer memberId = unionCard.getMemberId();
                    if (memberId != null) {
                        String memberIdKey = UnionCardCacheUtil.getMemberIdKey(memberId);
                        result.add(memberIdKey);
                    }
                }
                break;
            case UnionCardCacheUtil.TYPE_UNION_ID:
                for (UnionCard unionCard : unionCardList) {
                    Integer unionId = unionCard.getUnionId();
                    if (unionId != null) {
                        String unionIdKey = UnionCardCacheUtil.getUnionIdKey(unionId);
                        result.add(unionIdKey);
                    }
                }
                break;
            case UnionCardCacheUtil.TYPE_FAN_ID:
                for (UnionCard unionCard : unionCardList) {
                    Integer fanId = unionCard.getFanId();
                    if (fanId != null) {
                        String fanIdKey = UnionCardCacheUtil.getFanIdKey(fanId);
                        result.add(fanIdKey);
                    }
                }
                break;
            case UnionCardCacheUtil.TYPE_ACTIVITY_ID:
                for (UnionCard unionCard : unionCardList) {
                    Integer activityId = unionCard.getActivityId();
                    if (activityId != null) {
                        String activityIdKey = UnionCardCacheUtil.getActivityIdKey(activityId);
                        result.add(activityIdKey);
                    }
                }
                break;
            default:
                break;
        }
        return result;
    }
}