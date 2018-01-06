package com.gt.union.card.main.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.gt.union.api.client.sms.SmsService;
import com.gt.union.api.client.socket.SocketService;
import com.gt.union.card.activity.constant.ActivityConstant;
import com.gt.union.card.activity.entity.UnionCardActivity;
import com.gt.union.card.activity.service.IUnionCardActivityService;
import com.gt.union.card.activity.vo.CardActivityApplyVO;
import com.gt.union.card.main.constant.CardConstant;
import com.gt.union.card.main.dao.IUnionCardDao;
import com.gt.union.card.main.entity.UnionCard;
import com.gt.union.card.main.entity.UnionCardFan;
import com.gt.union.card.main.entity.UnionCardRecord;
import com.gt.union.card.main.service.IUnionCardApplyService;
import com.gt.union.card.main.service.IUnionCardFanService;
import com.gt.union.card.main.service.IUnionCardRecordService;
import com.gt.union.card.main.service.IUnionCardService;
import com.gt.union.card.main.vo.CardApplyVO;
import com.gt.union.card.main.vo.CardPhoneVO;
import com.gt.union.card.project.constant.ProjectConstant;
import com.gt.union.card.project.entity.UnionCardProject;
import com.gt.union.card.project.service.IUnionCardProjectItemService;
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
import com.gt.union.common.util.BigDecimalUtil;
import com.gt.union.common.util.DateUtil;
import com.gt.union.common.util.ListUtil;
import com.gt.union.common.util.StringUtil;
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
public class UnionCardServiceImpl implements IUnionCardService {
    private Logger logger = Logger.getLogger(UnionCardServiceImpl.class);

    @Autowired
    private IUnionCardDao unionCardDao;

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

    @Autowired
    private IUnionCardProjectItemService unionCardProjectItemService;

    //********************************************* Base On Business - get *********************************************

    @Override
    public CardApplyVO getCardApplyVOByBusIdAndFanId(Integer busId, Integer fanId, Integer optUnionId) throws Exception {
        if (fanId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // （1）	判断fanId有效性
        UnionCardFan fan = unionCardFanService.getValidById(fanId);
        if (fan == null) {
            throw new BusinessException("找不到粉丝信息");
        }

        List<UnionMember> memberList = unionMemberService.listValidReadByBusId(busId);
        if (optUnionId != null) {
            memberList = unionMemberService.filterByUnionId(memberList, optUnionId);
        }
        memberList = unionMemberService.filterInvalidUnionId(memberList);

        List<UnionMember> optionMemberList = new ArrayList<>();
        if (ListUtil.isNotEmpty(memberList)) {
            for (UnionMember member : memberList) {
                Integer unionId = member.getUnionId();
                if (!existValidUnexpiredByUnionIdAndFanIdAndType(unionId, fanId, CardConstant.TYPE_DISCOUNT)) {
                    optionMemberList.add(member);
                    continue;
                }

                List<UnionCardActivity> sellingActivityList = unionCardActivityService.listValidByUnionIdAndStatus(unionId, ActivityConstant.STATUS_SELLING);
                if (ListUtil.isNotEmpty(sellingActivityList)) {
                    for (UnionCardActivity activity : sellingActivityList) {
                        if (!unionCardProjectService.existValidByUnionIdAndMemberIdAndActivityIdAndStatus(unionId, member.getId(), activity.getId(), ProjectConstant.STATUS_ACCEPT)) {
                            continue;
                        }
                        if (!existValidUnexpiredByUnionIdAndFanIdAndType(unionId, fanId, CardConstant.TYPE_ACTIVITY)) {
                            optionMemberList.add(member);
                            break;
                        }
                    }
                }
            }
        }
        if (ListUtil.isEmpty(optionMemberList)) {
            return null;
        }
        // 按我创建的联盟>我加入的联盟(按时间顺序)
        Collections.sort(optionMemberList, new Comparator<UnionMember>() {
            @Override
            public int compare(UnionMember o1, UnionMember o2) {
                if (MemberConstant.IS_UNION_OWNER_YES == o1.getIsUnionOwner()) {
                    return -1;
                }
                if (MemberConstant.IS_UNION_OWNER_YES == o2.getIsUnionOwner()) {
                    return 1;
                }

                return o1.getCreateTime().compareTo(o2.getCreateTime());
            }
        });
        List<Integer> optUnionIdList = unionMemberService.getUnionIdList(optionMemberList);

        CardApplyVO result = new CardApplyVO();
        result.setUnionList(unionMainService.listByIdList(optUnionIdList));
        UnionMember currentMember = optionMemberList.get(0);
        Integer currentMemberId = currentMember.getId();
        Integer currentUnionId = currentMember.getUnionId();
        result.setCurrentMember(currentMember);

        UnionMain currentUnion = unionMainService.getById(currentUnionId);
        result.setCurrentUnion(currentUnion);

        // 	获取当前联盟粉丝的折扣卡和活动卡情况，若已办理，则不显示；否则，要求活动卡在售卖中状态
        result.setIsDiscountCard(existValidUnexpiredByUnionIdAndFanIdAndType(currentUnionId, fanId, CardConstant.TYPE_DISCOUNT) ? CommonConstant.COMMON_NO : CommonConstant.COMMON_YES);

        List<UnionCardActivity> sellingActivityList = unionCardActivityService.listValidByUnionIdAndStatus(currentUnionId, ActivityConstant.STATUS_SELLING);
        if (ListUtil.isNotEmpty(sellingActivityList)) {
            List<CardActivityApplyVO> cardActivityApplyVOList = new ArrayList<>();
            for (UnionCardActivity activity : sellingActivityList) {
                if (!unionCardProjectService.existValidByUnionIdAndMemberIdAndActivityIdAndStatus(currentUnionId, currentMemberId, activity.getId(), ProjectConstant.STATUS_ACCEPT)) {
                    continue;
                }
                Integer activityCardCount = countValidByUnionIdAndActivityId(currentUnionId, activity.getId());
                if (activityCardCount < activity.getAmount()) {
                    CardActivityApplyVO cardActivityApplyVO = new CardActivityApplyVO();
                    cardActivityApplyVO.setActivity(activity);
                    cardActivityApplyVO.setItemCount(unionCardProjectItemService.countValidCommittedByUnionIdAndActivityId(currentUnionId, activity.getId()));
                    cardActivityApplyVOList.add(cardActivityApplyVO);
                }
            }
            result.setCardActivityApplyVOList(cardActivityApplyVOList);
        }

        if (CommonConstant.COMMON_NO == result.getIsDiscountCard() && ListUtil.isEmpty(result.getCardActivityApplyVOList())) {
            return null;
        }

        return result;
    }

    @Override
    public UnionCard getValidUnexpiredByUnionIdAndFanIdAndType(Integer unionId, Integer fanId, Integer type) throws Exception {
        if (unionId == null || fanId == null || type == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCard> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .gt("validity", DateUtil.getCurrentDate())
                .eq("union_id", unionId)
                .eq("fan_id", fanId)
                .eq("type", type);

        return unionCardDao.selectOne(entityWrapper);
    }

    @Override
    public UnionCard getValidUnexpiredByUnionIdAndFanIdAndActivityId(Integer unionId, Integer fanId, Integer activityId) throws Exception {
        if (unionId == null || fanId == null || activityId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCard> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .gt("validity", DateUtil.getCurrentDate())
                .eq("union_id", unionId)
                .eq("fan_id", fanId)
                .eq("activity_id", activityId);

        return unionCardDao.selectOne(entityWrapper);
    }

    //********************************************* Base On Business - list ********************************************

    @Override
    public List<UnionCard> listValidUnexpiredByUnionIdAndFanId(Integer unionId, Integer fanId) throws Exception {
        if (unionId == null || fanId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCard> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .gt("validity", DateUtil.getCurrentDate())
                .eq("union_id", unionId)
                .eq("fan_id", fanId);

        return unionCardDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionCard> listValidUnexpiredByUnionIdAndFanIdAndType(Integer unionId, Integer fanId, Integer type) throws Exception {
        if (unionId == null || fanId == null || type == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCard> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .gt("validity", DateUtil.getCurrentDate())
                .eq("union_id", unionId)
                .eq("fan_id", fanId)
                .eq("type", type);

        return unionCardDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionCard> listValidUnexpiredByUnionIdAndActivityId(Integer unionId, Integer activityId) throws Exception {
        if (unionId == null || activityId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCard> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .gt("validity", DateUtil.getCurrentDate())
                .eq("union_id", unionId)
                .eq("activity_id", activityId);

        return unionCardDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionCard> listValidUnexpiredByFanIdAndType(Integer fanId, Integer type) throws Exception {
        if (fanId == null || type == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCard> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .gt("validity", DateUtil.getCurrentDate())
                .eq("fan_id", fanId)
                .eq("type", type);

        return unionCardDao.selectList(entityWrapper);
    }

    //********************************************* Base On Business - save ********************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UnionPayVO saveApplyByBusIdAndUnionIdAndFanId(Integer busId, Integer unionId, Integer fanId, List<Integer> activityIdList, IUnionCardApplyService unionCardApplyService) throws Exception {
        if (busId == null || unionId == null || fanId == null || activityIdList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // 判断union有效性和member写权限
        UnionMain union = unionMainService.getValidById(unionId);
        if (!unionMainService.isUnionValid(union)) {
            throw new BusinessException(CommonConstant.UNION_INVALID);
        }
        UnionMember member = unionMemberService.getValidReadByBusIdAndUnionId(busId, unionId);
        if (member == null) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_ERROR);
        }
        // 判断fanId有效性
        UnionCardFan fan = unionCardFanService.getValidById(fanId);
        if (fan == null) {
            throw new BusinessException("找不到粉丝信息");
        }
        // 	判断是否已办理过折扣卡，如果没有，且是只办折扣卡，则自动办理
        Date currentDate = DateUtil.getCurrentDate();
        UnionCard discountCard = getValidUnexpiredByUnionIdAndFanIdAndType(unionId, fanId, CardConstant.TYPE_DISCOUNT);
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
        // 新增未付款的联盟卡购买记录，并返回支付链接
        List<UnionCardRecord> saveCardRecordList = new ArrayList<>();
        String orderNo = "LM" + ConfigConstant.PAY_MODEL_CARD + DateUtil.getSerialNumber();
        BigDecimal payMoneySum = BigDecimal.ZERO;
        if (ListUtil.isNotEmpty(activityIdList)) {
            for (Integer activityId : activityIdList) {
                UnionCardActivity activity = unionCardActivityService.getValidByIdAndUnionId(activityId, unionId);
                if (activity == null) {
                    throw new BusinessException("找不到活动卡信息");
                }
                if (ActivityConstant.STATUS_SELLING != unionCardActivityService.getStatus(activity)) {
                    throw new BusinessException("活动卡不在售卡状态");
                }
                Integer activityCardCount = countValidByUnionIdAndActivityId(unionId, activityId);
                if (activityCardCount >= activity.getAmount()) {
                    throw new BusinessException("售卡量已达活动卡发行量");
                }
                if (existValidUnexpiredByUnionIdAndFanIdAndActivityId(unionId, fan.getId(), activity.getId())) {
                    throw new BusinessException("已办理活动卡");
                }

                UnionCardRecord saveCardRecord = new UnionCardRecord();
                saveCardRecord.setDelStatus(CommonConstant.COMMON_NO);
                saveCardRecord.setCreateTime(currentDate);
                saveCardRecord.setFanId(fanId);
                saveCardRecord.setMemberId(member.getId());
                saveCardRecord.setUnionId(unionId);
                saveCardRecord.setActivityId(activityId);
                saveCardRecord.setSysOrderNo(orderNo);
                saveCardRecord.setPayMoney(activity.getPrice());
                saveCardRecord.setPayStatus(CardConstant.PAY_STATUS_PAYING);

                saveCardRecordList.add(saveCardRecord);

                payMoneySum = BigDecimalUtil.add(payMoneySum, activity.getPrice());
            }
        }

        UnionPayVO result = unionCardApplyService.unionCardApply(orderNo, BigDecimalUtil.toDouble(payMoneySum), busId, unionId, activityIdList);

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
        return unionCardFanService.getOrSaveByPhone(phone);
    }

    //********************************************* Base On Business - remove ******************************************

    //********************************************* Base On Business - update ******************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String updateCallbackByOrderNo(String orderNo, String socketKey, String payType, String payOrderNo, Integer isSuccess) {
        Map<String, Object> result = new HashMap<>(2);
        if (orderNo == null || payType == null || payOrderNo == null || isSuccess == null) {
            result.put("code", -1);
            result.put("msg", "参数缺少");
            return JSONObject.toJSONString(result);
        }

        try {
            // 判断recordIds有效性
            List<UnionCardRecord> recordList = unionCardRecordService.listValidByOrderNo(orderNo);
            if (ListUtil.isEmpty(recordList)) {
                throw new BusinessException("没有支付信息");
            }

            List<UnionCardRecord> updateRecordList = new ArrayList<>();
            Date currentDate = DateUtil.getCurrentDate();
            for (UnionCardRecord record : recordList) {
                // 如果recordIds的支付状态存在已处理，则直接返回成功
                Integer payStatus = record.getPayStatus();
                if (CardConstant.PAY_STATUS_SUCCESS == payStatus || CardConstant.PAY_STATUS_FAIL == payStatus || CardConstant.PAY_STATUS_RETURN == payStatus) {
                    result.put("code", 0);
                    result.put("msg", "重复处理");
                    return JSONObject.toJSONString(result);
                }

                // 根据订单信息生成活动卡
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

                UnionCard saveActivityCard = new UnionCard();
                saveActivityCard.setDelStatus(CommonConstant.DEL_STATUS_NO);
                saveActivityCard.setCreateTime(currentDate);
                saveActivityCard.setType(CardConstant.TYPE_ACTIVITY);
                saveActivityCard.setFanId(record.getFanId());
                saveActivityCard.setMemberId(record.getMemberId());
                saveActivityCard.setUnionId(record.getUnionId());
                UnionCardActivity activity = unionCardActivityService.getById(record.getActivityId());
                if (activity != null) {
                    saveActivityCard.setActivityId(activity.getId());
                    saveActivityCard.setName(activity.getName());
                    saveActivityCard.setValidity(DateUtil.addDays(currentDate, activity.getValidityDay()));
                }
                save(saveActivityCard);

                updateRecord.setCardId(saveActivityCard.getId());
                updateRecordList.add(updateRecord);
            }

            // 自动办理折扣卡
            UnionCardRecord tempRecord = recordList.get(0);
            Integer unionId = tempRecord.getUnionId();
            Integer fanId = tempRecord.getFanId();
            if (!existValidUnexpiredByUnionIdAndFanIdAndType(unionId, fanId, CardConstant.TYPE_DISCOUNT)) {
                UnionCard saveDiscountCard = new UnionCard();
                saveDiscountCard.setDelStatus(CommonConstant.DEL_STATUS_NO);
                saveDiscountCard.setCreateTime(currentDate);
                saveDiscountCard.setType(CardConstant.TYPE_DISCOUNT);
                saveDiscountCard.setFanId(fanId);
                saveDiscountCard.setMemberId(tempRecord.getMemberId());
                saveDiscountCard.setUnionId(unionId);
                UnionMain union = unionMainService.getById(unionId);
                saveDiscountCard.setName((union != null ? union.getName() : "") + "折扣卡");
                saveDiscountCard.setValidity(DateUtil.addYears(currentDate, 10));

                save(saveDiscountCard);
            }

            List<UnionCardSharingRecord> saveSharingRecordList = new ArrayList<>();
            List<UnionBrokerageIncome> saveIncomeList = new ArrayList<>();
            if (CommonConstant.COMMON_YES == isSuccess) {
                for (UnionCardRecord record : recordList) {
                    if (unionCardSharingRatioService.existValidByUnionIdAndActivityId(record.getUnionId(), record.getActivityId())) {
                        saveSharingRecordList.addAll(shareByRatio(record));
                    } else {
                        saveSharingRecordList.addAll(shareByAverage(record));
                    }
                }
                saveIncomeList = getSaveIncomeList(saveSharingRecordList);
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
            if (StringUtil.isNotEmpty(socketKey)) {
                socketService.socketPaySendMessage(socketKey, isSuccess, null);
            }
            result.put("code", 0);
            result.put("msg", "成功");
            return JSONObject.toJSONString(result);
        } catch (Exception e) {
            logger.error("升级联盟卡支付成功回调错误", e);
            result.put("code", -1);
            result.put("msg", e.getMessage());
            return JSONObject.toJSONString(result);
        }
    }

    private List<UnionCardSharingRecord> shareByRatio(UnionCardRecord record) throws Exception {
        List<UnionCardSharingRatio> ratioList = unionCardSharingRatioService.listValidByUnionIdAndActivityId(record.getUnionId(), record.getActivityId(), "create_time", true);
        boolean isIncludeUnionOwnerId = unionCardSharingRatioService.existUnionOwnerId(ratioList);
        boolean isIncludeInvalidMemberId = unionCardSharingRatioService.existInvalidMemberId(ratioList);
        ratioList = unionCardSharingRatioService.filterInvalidMemberId(ratioList);

        List<UnionCardSharingRecord> result = new ArrayList<>();
        Double payMoney = record.getPayMoney();
        BigDecimal sharedRatioSum = BigDecimal.ZERO;
        BigDecimal sharedMoneySum = BigDecimal.ZERO;
        Date currentDate = DateUtil.getCurrentDate();
        for (UnionCardSharingRatio ratio : ratioList) {
            Double sharingRatio = ratio.getRatio();
            BigDecimal sharingMoney = BigDecimalUtil.multiply(payMoney, sharingRatio);

            UnionCardSharingRecord saveSharingRecord = new UnionCardSharingRecord();
            saveSharingRecord.setDelStatus(CommonConstant.DEL_STATUS_NO);
            saveSharingRecord.setCreateTime(currentDate);
            saveSharingRecord.setSellPrice(payMoney);
            saveSharingRecord.setSharingRatio(sharingRatio);
            saveSharingRecord.setSharingMoney(BigDecimalUtil.toDouble(sharingMoney));
            saveSharingRecord.setSharingMemberId(ratio.getMemberId());
            saveSharingRecord.setFromMemberId(record.getMemberId());
            saveSharingRecord.setActivityId(record.getActivityId());
            saveSharingRecord.setCardId(record.getCardId());
            saveSharingRecord.setFanId(record.getFanId());
            saveSharingRecord.setUnionId(record.getUnionId());
            result.add(saveSharingRecord);

            sharedRatioSum = BigDecimalUtil.add(sharedRatioSum, sharingRatio);
            sharedMoneySum = BigDecimalUtil.add(sharedMoneySum, sharingMoney);
        }
        BigDecimal surplusSharingRatio = BigDecimalUtil.subtract(1.0, sharedRatioSum);
        BigDecimal surplusSharingMoney = BigDecimalUtil.subtract(payMoney, sharedMoneySum);
        if (surplusSharingRatio.doubleValue() > 0 && surplusSharingMoney.doubleValue() > 0) {
            Integer surplusSharingRatioMemberId = (isIncludeUnionOwnerId || isIncludeInvalidMemberId || ListUtil.isEmpty(ratioList))
                    ? unionMemberService.getValidOwnerByUnionId(record.getUnionId()).getId() : ratioList.get(0).getMemberId();

            UnionCardSharingRecord ownerSaveSharingRecord = new UnionCardSharingRecord();
            ownerSaveSharingRecord.setDelStatus(CommonConstant.DEL_STATUS_NO);
            ownerSaveSharingRecord.setCreateTime(currentDate);
            ownerSaveSharingRecord.setSellPrice(payMoney);
            ownerSaveSharingRecord.setSharingRatio(BigDecimalUtil.toDouble(surplusSharingRatio, 4));
            ownerSaveSharingRecord.setSharingMoney(BigDecimalUtil.toDouble(surplusSharingMoney));
            ownerSaveSharingRecord.setSharingMemberId(surplusSharingRatioMemberId);
            ownerSaveSharingRecord.setFromMemberId(record.getMemberId());
            ownerSaveSharingRecord.setActivityId(record.getActivityId());
            ownerSaveSharingRecord.setCardId(record.getCardId());
            ownerSaveSharingRecord.setFanId(record.getFanId());
            ownerSaveSharingRecord.setUnionId(record.getUnionId());
            result.add(ownerSaveSharingRecord);
        }

        return result;
    }

    private List<UnionCardSharingRecord> shareByAverage(UnionCardRecord record) throws Exception {
        List<UnionCardProject> projectList = unionCardProjectService.listValidByUnionIdAndActivityIdAndStatus(record.getUnionId(), record.getActivityId(), ProjectConstant.STATUS_ACCEPT);
        if (ListUtil.isNotEmpty(projectList)) {
            if (!unionCardSharingRatioService.existValidByUnionIdAndActivityId(record.getUnionId(), record.getActivityId())) {
                unionCardSharingRatioService.autoEqualDivisionRatio(projectList);
            }
            return shareByRatio(record);
        } else {
            List<UnionCardSharingRecord> result = new ArrayList<>();

            UnionCardSharingRecord ownerSaveSharingRecord = new UnionCardSharingRecord();
            ownerSaveSharingRecord.setDelStatus(CommonConstant.DEL_STATUS_NO);
            ownerSaveSharingRecord.setCreateTime(DateUtil.getCurrentDate());
            ownerSaveSharingRecord.setSellPrice(record.getPayMoney());
            ownerSaveSharingRecord.setSharingRatio(1.0);
            ownerSaveSharingRecord.setSharingMoney(record.getPayMoney());
            ownerSaveSharingRecord.setSharingMemberId(unionMemberService.getValidOwnerByUnionId(record.getUnionId()).getId());
            ownerSaveSharingRecord.setFromMemberId(record.getMemberId());
            ownerSaveSharingRecord.setActivityId(record.getActivityId());
            ownerSaveSharingRecord.setCardId(record.getCardId());
            ownerSaveSharingRecord.setFanId(record.getFanId());
            ownerSaveSharingRecord.setUnionId(record.getUnionId());
            result.add(ownerSaveSharingRecord);

            return result;
        }
    }

    private List<UnionBrokerageIncome> getSaveIncomeList(List<UnionCardSharingRecord> saveSharingRecordList) throws Exception {
        List<UnionBrokerageIncome> result = new ArrayList<>();

        if (ListUtil.isNotEmpty(saveSharingRecordList)) {
            Date currentDate = DateUtil.getCurrentDate();
            for (UnionCardSharingRecord saveSharingRecord : saveSharingRecordList) {
                Integer sharingMemberId = saveSharingRecord.getSharingMemberId();
                UnionMember sharingMember = unionMemberService.getById(sharingMemberId);

                UnionBrokerageIncome saveIncome = new UnionBrokerageIncome();
                saveIncome.setDelStatus(CommonConstant.DEL_STATUS_NO);
                saveIncome.setCreateTime(currentDate);
                saveIncome.setType(BrokerageConstant.INCOME_TYPE_CARD);
                saveIncome.setMoney(saveSharingRecord.getSharingMoney());
                saveIncome.setBusId(sharingMember.getBusId());
                saveIncome.setMemberId(sharingMemberId);
                saveIncome.setUnionId(saveSharingRecord.getUnionId());
                saveIncome.setCardId(saveSharingRecord.getCardId());
                result.add(saveIncome);
            }
        }

        return result;
    }

    //********************************************* Base On Business - other *******************************************

    @Override
    public Integer countValidByUnionIdAndActivityId(Integer unionId, Integer activityId) throws Exception {
        if (unionId == null || activityId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCard> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("union_id", unionId)
                .eq("activity_id", activityId);

        return unionCardDao.selectCount(entityWrapper);
    }

    @Override
    public boolean existValidUnexpiredByUnionIdAndFanIdAndType(Integer unionId, Integer fanId, Integer type) throws Exception {
        if (unionId == null || fanId == null || type == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCard> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .gt("validity", DateUtil.getCurrentDate())
                .eq("union_id", unionId)
                .eq("fan_id", fanId)
                .eq("type", type);

        return unionCardDao.selectCount(entityWrapper) > 0;
    }

    @Override
    public boolean existValidUnexpiredByUnionIdAndFanIdAndActivityId(Integer unionId, Integer fanId, Integer activityId) throws Exception {
        if (unionId == null || fanId == null || activityId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCard> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .gt("validity", DateUtil.getCurrentDate())
                .eq("union_id", unionId)
                .eq("fan_id", fanId)
                .eq("activity_id", activityId);

        return unionCardDao.selectCount(entityWrapper) > 0;
    }

    //********************************************* Base On Business - filter ******************************************

    @Override
    public List<UnionCard> filterByDelStatus(List<UnionCard> unionCardList, Integer delStatus) throws Exception {
        if (delStatus == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionCard> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(unionCardList)) {
            for (UnionCard unionCard : unionCardList) {
                if (delStatus.equals(unionCard.getDelStatus())) {
                    result.add(unionCard);
                }
            }
        }

        return result;
    }

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
    public List<UnionCard> filterByActivityId(List<UnionCard> cardList, Integer activityId) throws Exception {
        if (cardList == null || activityId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionCard> result = new ArrayList<>();
        for (UnionCard card : cardList) {
            if (activityId.equals(card.getActivityId())) {
                result.add(card);
            }
        }

        return result;
    }

    @Override
    public List<UnionCard> filterExpired(List<UnionCard> cardList) throws Exception {
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

    //********************************************* Object As a Service - get ******************************************

    @Override
    public UnionCard getById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        return unionCardDao.selectById(id);
    }

    @Override
    public UnionCard getValidById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCard> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("id", id);

        return unionCardDao.selectOne(entityWrapper);
    }

    @Override
    public UnionCard getInvalidById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCard> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("id", id);

        return unionCardDao.selectOne(entityWrapper);
    }

    //********************************************* Object As a Service - list *****************************************

    @Override
    public List<Integer> getIdList(List<UnionCard> unionCardList) throws Exception {
        if (unionCardList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<Integer> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(unionCardList)) {
            for (UnionCard unionCard : unionCardList) {
                result.add(unionCard.getId());
            }
        }

        return result;
    }

    @Override
    public List<Integer> getUnionIdList(List<UnionCard> unionCardList) throws Exception {
        if (unionCardList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<Integer> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(unionCardList)) {
            for (UnionCard unionCard : unionCardList) {
                result.add(unionCard.getUnionId());
            }
        }

        return result;
    }

    @Override
    public List<UnionCard> listByMemberId(Integer memberId) throws Exception {
        if (memberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCard> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("member_id", memberId);

        return unionCardDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionCard> listValidByMemberId(Integer memberId) throws Exception {
        if (memberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCard> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("member_id", memberId);

        return unionCardDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionCard> listInvalidByMemberId(Integer memberId) throws Exception {
        if (memberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCard> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("member_id", memberId);

        return unionCardDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionCard> listByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCard> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("union_id", unionId);

        return unionCardDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionCard> listValidByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCard> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("union_id", unionId);

        return unionCardDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionCard> listInvalidByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCard> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("union_id", unionId);

        return unionCardDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionCard> listByFanId(Integer fanId) throws Exception {
        if (fanId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCard> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("fan_id", fanId);

        return unionCardDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionCard> listValidByFanId(Integer fanId) throws Exception {
        if (fanId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCard> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("fan_id", fanId);

        return unionCardDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionCard> listInvalidByFanId(Integer fanId) throws Exception {
        if (fanId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCard> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("fan_id", fanId);

        return unionCardDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionCard> listByActivityId(Integer activityId) throws Exception {
        if (activityId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCard> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("activity_id", activityId);

        return unionCardDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionCard> listValidByActivityId(Integer activityId) throws Exception {
        if (activityId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCard> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("activity_id", activityId);

        return unionCardDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionCard> listInvalidByActivityId(Integer activityId) throws Exception {
        if (activityId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCard> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("activity_id", activityId);

        return unionCardDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionCard> listByIdList(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCard> entityWrapper = new EntityWrapper<>();
        entityWrapper.in("id", idList);

        return unionCardDao.selectList(entityWrapper);
    }

    @Override
    public Page pageSupport(Page page, EntityWrapper<UnionCard> entityWrapper) throws Exception {
        if (page == null || entityWrapper == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        return unionCardDao.selectPage(page, entityWrapper);
    }
    //********************************************* Object As a Service - save *****************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(UnionCard newUnionCard) throws Exception {
        if (newUnionCard == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        unionCardDao.insert(newUnionCard);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveBatch(List<UnionCard> newUnionCardList) throws Exception {
        if (newUnionCardList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        unionCardDao.insertBatch(newUnionCardList);
    }

    //********************************************* Object As a Service - remove ***************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        UnionCard removeUnionCard = new UnionCard();
        removeUnionCard.setId(id);
        removeUnionCard.setDelStatus(CommonConstant.DEL_STATUS_YES);
        unionCardDao.updateById(removeUnionCard);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeBatchById(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionCard> removeUnionCardList = new ArrayList<>();
        for (Integer id : idList) {
            UnionCard removeUnionCard = new UnionCard();
            removeUnionCard.setId(id);
            removeUnionCard.setDelStatus(CommonConstant.DEL_STATUS_YES);
            removeUnionCardList.add(removeUnionCard);
        }
        unionCardDao.updateBatchById(removeUnionCardList);
    }

    //********************************************* Object As a Service - update ***************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(UnionCard updateUnionCard) throws Exception {
        if (updateUnionCard == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        unionCardDao.updateById(updateUnionCard);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBatch(List<UnionCard> updateUnionCardList) throws Exception {
        if (updateUnionCardList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        unionCardDao.updateBatchById(updateUnionCardList);
    }

}