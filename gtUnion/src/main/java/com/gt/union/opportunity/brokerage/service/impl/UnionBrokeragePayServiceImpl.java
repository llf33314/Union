package com.gt.union.opportunity.brokerage.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.api.client.pay.WxPayService;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.*;
import com.gt.union.opportunity.brokerage.constant.BrokerageConstant;
import com.gt.union.opportunity.brokerage.entity.UnionBrokerageIncome;
import com.gt.union.opportunity.brokerage.entity.UnionBrokeragePay;
import com.gt.union.opportunity.brokerage.mapper.UnionBrokeragePayMapper;
import com.gt.union.opportunity.brokerage.service.IUnionBrokerageIncomeService;
import com.gt.union.opportunity.brokerage.service.IUnionBrokeragePayService;
import com.gt.union.opportunity.brokerage.util.UnionBrokeragePayCacheUtil;
import com.gt.union.opportunity.brokerage.vo.BrokerageOpportunityVO;
import com.gt.union.opportunity.brokerage.vo.BrokeragePayVO;
import com.gt.union.opportunity.brokerage.vo.OpportunityBrokeragePayVO;
import com.gt.union.opportunity.main.constant.OpportunityConstant;
import com.gt.union.opportunity.main.entity.UnionOpportunity;
import com.gt.union.opportunity.main.service.IUnionOpportunityService;
import com.gt.union.opportunity.main.vo.OpportunityVO;
import com.gt.union.union.main.entity.UnionMain;
import com.gt.union.union.main.service.IUnionMainService;
import com.gt.union.union.member.entity.UnionMember;
import com.gt.union.union.member.service.IUnionMemberService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * 佣金支出 服务实现类
 *
 * @author linweicong
 * @version 2017-11-24 09:21:28
 */
@Service
public class UnionBrokeragePayServiceImpl extends ServiceImpl<UnionBrokeragePayMapper, UnionBrokeragePay> implements IUnionBrokeragePayService {

    private Logger logger = Logger.getLogger(UnionBrokeragePayServiceImpl.class);

    @Autowired
    private RedisCacheUtil redisCacheUtil;

    @Autowired
    private IUnionOpportunityService unionOpportunityService;

    @Autowired
    private IUnionBrokerageIncomeService unionBrokerageIncomeService;

    @Autowired
    private IUnionMainService unionMainService;

    @Autowired
    private IUnionMemberService unionMemberService;

    @Autowired
    private WxPayService wxPayService;

    //***************************************** Domain Driven Design - get *********************************************

    @Override
    public BrokeragePayVO getBrokeragePayVOByBusIdAndUnionIdAndMemberId(Integer busId, Integer unionId, Integer memberId) throws Exception {
        if (busId == null || unionId == null || memberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // （1）	判断union有效性和member读权限
        if (!unionMainService.isUnionValid(unionId)) {
            throw new BusinessException(CommonConstant.UNION_INVALID);
        }
        UnionMember member = unionMemberService.getReadByBusIdAndUnionId(busId, unionId);
        if (member == null) {
            throw new BusinessException(CommonConstant.UNION_READ_REJECT);
        }
        // （2）	判断memberId有效性
        UnionMember tgtMember = unionMemberService.getReadByIdAndUnionId(memberId, unionId);
        if (tgtMember == null) {
            throw new BusinessException("找不到盟员信息");
        }
        // （3）	获取所有支付成功的支付记录
        BrokeragePayVO result = new BrokeragePayVO();
        result.setUnion(unionMainService.getById(unionId));
        result.setMember(tgtMember);
        List<UnionOpportunity> opportunityList = new ArrayList<>();
        List<UnionBrokeragePay> incomePayList = listByFromBusIdAndToBusIdAndStatus(tgtMember.getBusId(), busId, BrokerageConstant.PAY_STATUS_SUCCESS);
        BigDecimal incomeBrokerage = BigDecimal.ZERO;
        if (ListUtil.isNotEmpty(incomePayList)) {
            for (UnionBrokeragePay pay : incomePayList) {
                incomeBrokerage = BigDecimalUtil.add(incomeBrokerage, pay.getMoney());

                UnionOpportunity opportunity = unionOpportunityService.getById(pay.getOpportunityId());
                if (opportunity != null) {
                    opportunityList.add(opportunity);
                }
            }
        }
        List<UnionBrokeragePay> expensePayList = listByFromBusIdAndToBusIdAndStatus(busId, tgtMember.getBusId(), BrokerageConstant.PAY_STATUS_SUCCESS);
        BigDecimal expenseBrokerage = BigDecimal.ZERO;
        if (ListUtil.isNotEmpty(expensePayList)) {
            for (UnionBrokeragePay pay : expensePayList) {
                expenseBrokerage = BigDecimalUtil.add(expenseBrokerage, pay.getMoney());

                UnionOpportunity opportunity = unionOpportunityService.getById(pay.getOpportunityId());
                if (opportunity != null) {
                    opportunity.setBrokerageMoney(BigDecimalUtil.subtract(Double.valueOf(0), opportunity.getBrokerageMoney()).doubleValue());
                    opportunityList.add(opportunity);
                }
            }
        }
        Collections.sort(opportunityList, new Comparator<UnionOpportunity>() {
            @Override
            public int compare(UnionOpportunity o1, UnionOpportunity o2) {
                return o2.getCreateTime().compareTo(o1.getCreateTime());
            }
        });
        result.setOpportunityList(opportunityList);
        result.setContactMoney(BigDecimalUtil.subtract(incomeBrokerage, expenseBrokerage).doubleValue());

        return result;
    }

    //***************************************** Domain Driven Design - list ********************************************

    @Override
    public List<BrokerageOpportunityVO> listBrokerageOpportunityVOByBusId(Integer busId, Integer optUnionId, Integer optFromMemberId, Integer optIsClose, String optClientName, String optClientPhone) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // （1）获取已被接受的商机推荐
        List<OpportunityVO> opportunityVOList = unionOpportunityService.listToMeByBusId(busId, optUnionId,
                String.valueOf(OpportunityConstant.OPPORTUNITY_ACCEPT_STATUS_CONFIRMED), optClientName, optClientPhone);
        // （2）	按已结算状态顺序(未>已)，时间倒序排序
        List<BrokerageOpportunityVO> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(opportunityVOList)) {
            for (OpportunityVO opportunityVO : opportunityVOList) {
                BrokerageOpportunityVO vo = new BrokerageOpportunityVO();
                vo.setOpportunity(opportunityVO.getOpportunity());
                vo.setFromMember(opportunityVO.getFromMember());
                vo.setToMember(opportunityVO.getToMember());
                vo.setUnion(opportunityVO.getUnion());

                boolean existIncome = unionBrokerageIncomeService.existByMemberIdAndUnionIdAndOpportunityId(vo.getFromMember().getId(), vo.getFromMember().getUnionId(), vo.getOpportunity().getId());
                if (optIsClose != null) {
                    boolean isOK = (CommonConstant.COMMON_YES == optIsClose && existIncome) || (CommonConstant.COMMON_NO == optIsClose && !existIncome);
                    if (!isOK) {
                        continue;
                    }
                }
                vo.setIsClose(existIncome ? CommonConstant.COMMON_YES : CommonConstant.COMMON_NO);
                result.add(vo);
            }
        }
        Collections.sort(result, new Comparator<BrokerageOpportunityVO>() {
            @Override
            public int compare(BrokerageOpportunityVO o1, BrokerageOpportunityVO o2) {
                if (o1.getIsClose().compareTo(o2.getIsClose()) < 0) {
                    return 1;
                } else if (o1.getIsClose().compareTo(o2.getIsClose()) > 0) {
                    return -1;
                }
                return o1.getOpportunity().getCreateTime().compareTo(o2.getOpportunity().getCreateTime());
            }
        });

        return result;
    }

    @Override
    public List<BrokeragePayVO> listBrokeragePayVOByBusId(Integer busId, Integer optUnionId) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // （1）	获取所有有效的member
        List<UnionMember> memberList = unionMemberService.listReadByBusId(busId);
        List<Integer> unionIdList = new ArrayList<>();
        if (ListUtil.isNotEmpty(memberList)) {
            for (UnionMember member : memberList) {
                Integer unionId = member.getUnionId();
                if (!unionMainService.isUnionValid(unionId)) {
                    continue;
                }
                if (optUnionId != null && !optUnionId.equals(unionId)) {
                    continue;
                }
                unionIdList.add(unionId);
            }
        }
        // （2）	获取所有与我有商机佣金往来的盟员id列表
        List<BrokeragePayVO> result = new ArrayList<>();
        List<Integer> memberIdList = listMemberIdByUnionIdListAndStatus(unionIdList, BrokerageConstant.PAY_STATUS_SUCCESS, busId);
        if (ListUtil.isNotEmpty(memberIdList)) {
            for (Integer memberId : memberIdList) {
                UnionMember member = unionMemberService.getById(memberId);
                if (member == null) {
                    throw new BusinessException("找不到盟员信息");
                }
                BrokeragePayVO vo = new BrokeragePayVO();
                vo.setMember(member);

                UnionMain union = unionMainService.getById(member.getUnionId());
                vo.setUnion(union);

                Double incomeMoney = sumMoneyByFromBusIdAndToBusIdAndStatus(member.getBusId(), busId, BrokerageConstant.PAY_STATUS_SUCCESS);
                Double expenseMoney = sumMoneyByFromBusIdAndToBusIdAndStatus(busId, member.getBusId(), BrokerageConstant.PAY_STATUS_SUCCESS);
                vo.setContactMoney(BigDecimalUtil.subtract(incomeMoney, expenseMoney).doubleValue());

                result.add(vo);
            }
        }
        // （3）	按联盟创建时间顺序、盟员创建时间顺序排序
        Collections.sort(result, new Comparator<BrokeragePayVO>() {
            @Override
            public int compare(BrokeragePayVO o1, BrokeragePayVO o2) {
                int unionCompare = o1.getUnion().getCreateTime().compareTo(o2.getUnion().getCreateTime());
                if (unionCompare > 0) {
                    return -1;
                } else if (unionCompare < 0) {
                    return 1;
                }

                return o2.getMember().getCreateTime().compareTo(o1.getMember().getCreateTime());
            }
        });

        return result;
    }

    @Override
    public List<Integer> listMemberIdByUnionIdListAndStatus(List<Integer> unionIdList, Integer status, Integer fromBusIdOrToBusId) throws Exception {
        if (unionIdList == null || status == null || fromBusIdOrToBusId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        Set<Integer> result = new HashSet<>();

        EntityWrapper<UnionBrokeragePay> fromBusIdEW = new EntityWrapper<>();
        fromBusIdEW.eq("del_status", CommonConstant.COMMON_NO)
                .eq("status", status)
                .eq("from_bus_id", fromBusIdOrToBusId)
                .in("union_id", unionIdList);
        List<UnionBrokeragePay> fromBusIdPayList = selectList(fromBusIdEW);
        if (ListUtil.isNotEmpty(fromBusIdPayList)) {
            for (UnionBrokeragePay pay : fromBusIdPayList) {
                result.add(pay.getToMemberId());
            }
        }

        EntityWrapper<UnionBrokeragePay> toBusIdEW = new EntityWrapper<>();
        toBusIdEW.eq("del_status", CommonConstant.COMMON_NO)
                .eq("status", status)
                .eq("to_bus_id", fromBusIdOrToBusId)
                .in("union_id", unionIdList);
        List<UnionBrokeragePay> toBusIdPayList = selectList(toBusIdEW);
        if (ListUtil.isNotEmpty(toBusIdPayList)) {
            for (UnionBrokeragePay pay : toBusIdPayList) {
                result.add(pay.getFromMemberId());
            }
        }

        return ListUtil.toList(result);
    }

    @Override
    public List<UnionBrokeragePay> listByFromBusIdAndToBusIdAndStatus(Integer fromBusId, Integer toBusId, Integer status) throws Exception {
        if (fromBusId == null || toBusId == null || status == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionBrokeragePay> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.COMMON_NO)
                .eq("from_bus_id", fromBusId)
                .eq("to_bus_id", toBusId)
                .eq("status", status);

        return selectList(entityWrapper);
    }


    //***************************************** Domain Driven Design - save ********************************************

    //***************************************** Domain Driven Design - remove ******************************************

    //***************************************** Domain Driven Design - update ******************************************

    @Transactional(rollbackFor = Exception.class)
    @Override
    public OpportunityBrokeragePayVO batchPayByBusId(Integer busId, List<Integer> opportunityIdList) throws Exception {
        if (busId == null || opportunityIdList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // （1）	校验参数有效性
        List<UnionBrokeragePay> savePayList = new ArrayList<>();
        BigDecimal brokerageSum = BigDecimal.ZERO;
        Date currentDate = DateUtil.getCurrentDate();
        String orderNo = "Brokerage_" + busId + "_" + DateUtil.getSerialNumber();
        if (ListUtil.isNotEmpty(opportunityIdList)) {
            for (Integer opportunityId : opportunityIdList) {
                UnionOpportunity opportunity = unionOpportunityService.getById(opportunityId);
                if (opportunity == null) {
                    throw new BusinessException("找不到商机信息");
                }
                // （2）	判断union有效性和member写权限
                if (!unionMainService.isUnionValid(opportunity.getUnionId())) {
                    throw new BusinessException(CommonConstant.UNION_INVALID);
                }
                UnionMember member = unionMemberService.getWriteByBusIdAndUnionId(busId, opportunity.getUnionId());
                if (member == null) {
                    throw new BusinessException(CommonConstant.UNION_WRITE_REJECT);
                }
                UnionMember toMember = unionMemberService.getWriteByBusIdAndUnionId(opportunity.getToMemberId(), opportunity.getUnionId());
                if (toMember == null || !toMember.getId().equals(member.getId())) {
                    throw new BusinessException("无法替人支付");
                }
                UnionMember fromMember = unionMemberService.getReadByIdAndUnionId(opportunity.getFromMemberId(), opportunity.getUnionId());
                if (fromMember == null) {
                    throw new BusinessException("找不到商机的推荐者");
                }
                // （3）	如果已付款过，则报错
                if (existByOpportunityIdAndUnionIdAndStatus(opportunityId, opportunity.getUnionId(), BrokerageConstant.PAY_STATUS_SUCCESS)) {
                    throw new BusinessException("重复支付");
                }

                brokerageSum = BigDecimalUtil.add(brokerageSum, opportunity.getBrokerageMoney());

                UnionBrokeragePay savePay = new UnionBrokeragePay();
                savePay.setDelStatus(CommonConstant.COMMON_NO);
                savePay.setCreateTime(currentDate);
                savePay.setStatus(BrokerageConstant.PAY_STATUS_PAYING);
                savePay.setFromMemberId(fromMember.getId());
                savePay.setToMemberId(toMember.getId());
                savePay.setUnionId(opportunity.getUnionId());
                savePay.setFromBusId(fromMember.getBusId());
                savePay.setToBusId(toMember.getBusId());
                savePay.setOrderNo("LM_" + orderNo);
                savePayList.add(savePay);
            }
        }
        if (ListUtil.isEmpty(savePayList)) {
            throw new BusinessException("没有有效的支付信息");
        }
        saveBatch(savePayList);

        // （3）	调用接口，返回支付链接
        StringBuilder sbPayIds = new StringBuilder();
        for (UnionBrokeragePay pay : savePayList) {
            sbPayIds.append(pay.getId()).append(",");
        }
        String payIds = sbPayIds.toString().substring(0, sbPayIds.toString().length() - 1);
        OpportunityBrokeragePayVO result = new OpportunityBrokeragePayVO();
        String socketKey = PropertiesUtil.getSocketKey() + orderNo;
        String notifyUrl = PropertiesUtil.getUnionUrl() + "/unionBrokeragePay/79B4DE7C/opportunity/callback?socketKey=" + socketKey + "&payIds=" + payIds;
        String payUrl = wxPayService.qrCodePay(brokerageSum.doubleValue(), null, "LM_" + orderNo,
                null, CommonConstant.COMMON_NO, null, notifyUrl, CommonConstant.COMMON_NO,
                null, 0, null);
        result.setPayUrl(payUrl);
        result.setSocketKey(socketKey);

        return result;
    }

    @Override
    public String updateCallbackByIds(String payIds, String socketKey, String payType, String orderNo, Integer isSuccess) {
        Map<String, Object> result = new HashMap<>(2);
        if (payIds == null || socketKey == null || payType == null || orderNo == null || isSuccess == null) {
            result.put("code", -1);
            result.put("msg", "参数缺少");
            return JSONObject.toJSONString(result);
        }

        // （1）	判断payIds有效性
        try {
            List<UnionBrokeragePay> updatePayList = new ArrayList<>();
            List<UnionBrokerageIncome> saveIncomeList = new ArrayList<>();
            boolean isRepeat = false;
            Date currentDate = DateUtil.getCurrentDate();
            String[] payIdArray = payIds.split(",");
            for (String payId : payIdArray) {
                UnionBrokeragePay pay = getById(Integer.valueOf(payId));
                if (pay == null) {
                    // TODO socket通知
                    result.put("code", -1);
                    result.put("msg", "参数permitId错误");
                    return JSONObject.toJSONString(result);
                }

                Integer payStatus = pay.getStatus();
                if (BrokerageConstant.PAY_STATUS_SUCCESS == payStatus || BrokerageConstant.PAY_STATUS_FAIL == payStatus) {
                    isRepeat = true;
                    break;
                }

                UnionBrokeragePay updatePay = new UnionBrokeragePay();
                updatePay.setId(pay.getId());
                updatePay.setStatus(isSuccess == CommonConstant.COMMON_YES ? BrokerageConstant.PAY_STATUS_SUCCESS : BrokerageConstant.PAY_STATUS_FAIL);
                if (payType.equals("0")) {
                    updatePay.setType(BrokerageConstant.PAY_TYPE_WX);
                    updatePay.setWxOrderNo(orderNo);
                } else {
                    updatePay.setType(BrokerageConstant.PAY_TYPE_ALIPAY);
                    updatePay.setAlipayOrderNo(orderNo);
                }

                UnionBrokerageIncome saveIncome = new UnionBrokerageIncome();
                saveIncome.setDelStatus(CommonConstant.DEL_STATUS_NO);
                saveIncome.setCreateTime(currentDate);
                saveIncome.setType(BrokerageConstant.INCOME_TYPE_OPPORTUNITY);
                saveIncome.setMoney(pay.getMoney());
                saveIncome.setBusId(pay.getToBusId());
                saveIncome.setMemberId(pay.getToMemberId());
                saveIncome.setUnionId(pay.getUnionId());
                saveIncome.setOpportunityId(pay.getOpportunityId());
                saveIncomeList.add(saveIncome);
            }

            // （2）	如果payIds的支付状态存在已处理，则直接返回成功
            if (isRepeat) {
                // TODO socket通知
                result.put("code", 0);
                result.put("msg", "重复处理");
                return JSONObject.toJSONString(result);
            }

            updateBatch(updatePayList);
            unionBrokerageIncomeService.saveBatch(saveIncomeList);

            // TODO socket通知
            result.put("code", 0);
            result.put("msg", "成功");
            return JSONObject.toJSONString(result);
        } catch (Exception e) {
            // TODO socket通知
            logger.error("", e);
            result.put("code", -1);
            result.put("msg", e.getMessage());
            return JSONObject.toJSONString(result);
        }

    }

    @Override
    public Double sumMoneyByFromBusIdAndToBusIdAndStatus(Integer fromBusId, Integer toBusId, Integer status) throws Exception {
        if (fromBusId == null || toBusId == null || status == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        BigDecimal result = BigDecimal.ZERO;
        List<UnionBrokeragePay> payList = listByFromBusIdAndToBusIdAndStatus(fromBusId, toBusId, status);
        if (ListUtil.isNotEmpty(payList)) {
            for (UnionBrokeragePay pay : payList) {
                result = BigDecimalUtil.add(result, pay.getMoney());
            }
        }

        return result.doubleValue();
    }

    //***************************************** Domain Driven Design - count *******************************************

    //***************************************** Domain Driven Design - boolean *****************************************

    @Override
    public boolean existByOpportunityIdAndUnionIdAndStatus(Integer opportunityId, Integer unionId, Integer status) throws Exception {
        if (opportunityId == null || unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionBrokeragePay> result = listByOpportunityId(opportunityId);
        result = filterByUnionId(result, unionId);

        return ListUtil.isNotEmpty(result) && status.equals(result.get(0).getStatus());
    }

    //***************************************** Domain Driven Design - filter ******************************************

    @Override
    public List<UnionBrokeragePay> filterByUnionId(List<UnionBrokeragePay> payList, Integer unionId) throws Exception {
        if (payList == null || unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionBrokeragePay> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(payList)) {
            for (UnionBrokeragePay pay : payList) {
                if (unionId.equals(pay.getUnionId())) {
                    result.add(pay);
                }
            }
        }

        return result;
    }

    //***************************************** Object As a Service - get **********************************************

    public UnionBrokeragePay getById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionBrokeragePay result;
        // (1)cache
        String idKey = UnionBrokeragePayCacheUtil.getIdKey(id);
        if (redisCacheUtil.exists(idKey)) {
            String tempStr = redisCacheUtil.get(idKey);
            result = JSONArray.parseObject(tempStr, UnionBrokeragePay.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionBrokeragePay> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("id", id)
                .eq("del_status", CommonConstant.DEL_STATUS_NO);
        result = selectOne(entityWrapper);
        setCache(result, id);
        return result;
    }

    //***************************************** Object As a Service - list *********************************************

    List<UnionBrokeragePay> listByFromBusId(Integer fromBusId) throws Exception {
        if (fromBusId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionBrokeragePay> result;
        // (1)cache
        String fromBusIdKey = UnionBrokeragePayCacheUtil.getFromBusIdKey(fromBusId);
        if (redisCacheUtil.exists(fromBusIdKey)) {
            String tempStr = redisCacheUtil.get(fromBusIdKey);
            result = JSONArray.parseArray(tempStr, UnionBrokeragePay.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionBrokeragePay> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("from_bus_id", fromBusId)
                .eq("del_status", CommonConstant.COMMON_NO);
        result = selectList(entityWrapper);
        setCache(result, fromBusId, UnionBrokeragePayCacheUtil.TYPE_FROM_BUS_ID);
        return result;
    }

    List<UnionBrokeragePay> listByToBusId(Integer toBusId) throws Exception {
        if (toBusId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionBrokeragePay> result;
        // (1)cache
        String toBusIdKey = UnionBrokeragePayCacheUtil.getToBusIdKey(toBusId);
        if (redisCacheUtil.exists(toBusIdKey)) {
            String tempStr = redisCacheUtil.get(toBusIdKey);
            result = JSONArray.parseArray(tempStr, UnionBrokeragePay.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionBrokeragePay> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("to_bus_id", toBusId)
                .eq("del_status", CommonConstant.COMMON_NO);
        result = selectList(entityWrapper);
        setCache(result, toBusId, UnionBrokeragePayCacheUtil.TYPE_TO_BUS_ID);
        return result;
    }

    List<UnionBrokeragePay> listByFromMemberId(Integer fromMemberId) throws Exception {
        if (fromMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionBrokeragePay> result;
        // (1)cache
        String fromMemberIdKey = UnionBrokeragePayCacheUtil.getFromMemberIdKey(fromMemberId);
        if (redisCacheUtil.exists(fromMemberIdKey)) {
            String tempStr = redisCacheUtil.get(fromMemberIdKey);
            result = JSONArray.parseArray(tempStr, UnionBrokeragePay.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionBrokeragePay> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("from_member_id", fromMemberId)
                .eq("del_status", CommonConstant.COMMON_NO);
        result = selectList(entityWrapper);
        setCache(result, fromMemberId, UnionBrokeragePayCacheUtil.TYPE_FROM_MEMBER_ID);
        return result;
    }

    List<UnionBrokeragePay> listByToMemberId(Integer toMemberId) throws Exception {
        if (toMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionBrokeragePay> result;
        // (1)cache
        String toMemberIdKey = UnionBrokeragePayCacheUtil.getToMemberIdKey(toMemberId);
        if (redisCacheUtil.exists(toMemberIdKey)) {
            String tempStr = redisCacheUtil.get(toMemberIdKey);
            result = JSONArray.parseArray(tempStr, UnionBrokeragePay.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionBrokeragePay> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("to_member_id", toMemberId)
                .eq("del_status", CommonConstant.COMMON_NO);
        result = selectList(entityWrapper);
        setCache(result, toMemberId, UnionBrokeragePayCacheUtil.TYPE_TO_MEMBER_ID);
        return result;
    }

    List<UnionBrokeragePay> listByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionBrokeragePay> result;
        // (1)cache
        String unionIdKey = UnionBrokeragePayCacheUtil.getUnionIdKey(unionId);
        if (redisCacheUtil.exists(unionIdKey)) {
            String tempStr = redisCacheUtil.get(unionIdKey);
            result = JSONArray.parseArray(tempStr, UnionBrokeragePay.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionBrokeragePay> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("union_id", unionId)
                .eq("del_status", CommonConstant.COMMON_NO);
        result = selectList(entityWrapper);
        setCache(result, unionId, UnionBrokeragePayCacheUtil.TYPE_UNION_ID);
        return result;
    }

    List<UnionBrokeragePay> listByOpportunityId(Integer opportunityId) throws Exception {
        if (opportunityId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionBrokeragePay> result;
        // (1)cache
        String opportunityIdKey = UnionBrokeragePayCacheUtil.getOpportunityIdKey(opportunityId);
        if (redisCacheUtil.exists(opportunityIdKey)) {
            String tempStr = redisCacheUtil.get(opportunityIdKey);
            result = JSONArray.parseArray(tempStr, UnionBrokeragePay.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionBrokeragePay> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("opportunity_id", opportunityId)
                .eq("del_status", CommonConstant.COMMON_NO);
        result = selectList(entityWrapper);
        setCache(result, opportunityId, UnionBrokeragePayCacheUtil.TYPE_OPPORTUNITY_ID);
        return result;
    }

    List<UnionBrokeragePay> listByVerifierId(Integer verifierId) throws Exception {
        if (verifierId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionBrokeragePay> result;
        // (1)cache
        String verifierIdKey = UnionBrokeragePayCacheUtil.getVerifierIdKey(verifierId);
        if (redisCacheUtil.exists(verifierIdKey)) {
            String tempStr = redisCacheUtil.get(verifierIdKey);
            result = JSONArray.parseArray(tempStr, UnionBrokeragePay.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionBrokeragePay> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("verifier_id", verifierId)
                .eq("del_status", CommonConstant.COMMON_NO);
        result = selectList(entityWrapper);
        setCache(result, verifierId, UnionBrokeragePayCacheUtil.TYPE_VERIFIER_ID);
        return result;
    }

    //***************************************** Object As a Service - save *********************************************

    @Transactional(rollbackFor = Exception.class)
    public void save(UnionBrokeragePay newUnionBrokeragePay) throws Exception {
        if (newUnionBrokeragePay == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        insert(newUnionBrokeragePay);
        removeCache(newUnionBrokeragePay);
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveBatch(List<UnionBrokeragePay> newUnionBrokeragePayList) throws Exception {
        if (newUnionBrokeragePayList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        insertBatch(newUnionBrokeragePayList);
        removeCache(newUnionBrokeragePayList);
    }

    //***************************************** Object As a Service - remove *******************************************

    @Transactional(rollbackFor = Exception.class)
    public void removeById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        UnionBrokeragePay unionBrokeragePay = getById(id);
        removeCache(unionBrokeragePay);
        // (2)remove in db logically
        UnionBrokeragePay removeUnionBrokeragePay = new UnionBrokeragePay();
        removeUnionBrokeragePay.setId(id);
        removeUnionBrokeragePay.setDelStatus(CommonConstant.DEL_STATUS_YES);
        updateById(removeUnionBrokeragePay);
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeBatchById(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<UnionBrokeragePay> unionBrokeragePayList = new ArrayList<>();
        for (Integer id : idList) {
            UnionBrokeragePay unionBrokeragePay = getById(id);
            unionBrokeragePayList.add(unionBrokeragePay);
        }
        removeCache(unionBrokeragePayList);
        // (2)remove in db logically
        List<UnionBrokeragePay> removeUnionBrokeragePayList = new ArrayList<>();
        for (Integer id : idList) {
            UnionBrokeragePay removeUnionBrokeragePay = new UnionBrokeragePay();
            removeUnionBrokeragePay.setId(id);
            removeUnionBrokeragePay.setDelStatus(CommonConstant.DEL_STATUS_YES);
            removeUnionBrokeragePayList.add(removeUnionBrokeragePay);
        }
        updateBatchById(removeUnionBrokeragePayList);
    }

    //***************************************** Object As a Service - update *******************************************

    @Transactional(rollbackFor = Exception.class)
    public void update(UnionBrokeragePay updateUnionBrokeragePay) throws Exception {
        if (updateUnionBrokeragePay == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        Integer id = updateUnionBrokeragePay.getId();
        UnionBrokeragePay unionBrokeragePay = getById(id);
        removeCache(unionBrokeragePay);
        // (2)update db
        updateById(updateUnionBrokeragePay);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateBatch(List<UnionBrokeragePay> updateUnionBrokeragePayList) throws Exception {
        if (updateUnionBrokeragePayList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<Integer> idList = new ArrayList<>();
        for (UnionBrokeragePay updateUnionBrokeragePay : updateUnionBrokeragePayList) {
            idList.add(updateUnionBrokeragePay.getId());
        }
        List<UnionBrokeragePay> unionBrokeragePayList = new ArrayList<>();
        for (Integer id : idList) {
            UnionBrokeragePay unionBrokeragePay = getById(id);
            unionBrokeragePayList.add(unionBrokeragePay);
        }
        removeCache(unionBrokeragePayList);
        // (2)update db
        updateBatchById(updateUnionBrokeragePayList);
    }

    //***************************************** Object As a Service - cache support ************************************

    private void setCache(UnionBrokeragePay newUnionBrokeragePay, Integer id) {
        if (id == null) {
            //do nothing,just in case
            return;
        }
        String idKey = UnionBrokeragePayCacheUtil.getIdKey(id);
        redisCacheUtil.set(idKey, newUnionBrokeragePay);
    }

    private void setCache(List<UnionBrokeragePay> newUnionBrokeragePayList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            //do nothing,just in case
            return;
        }
        String foreignIdKey = null;
        switch (foreignIdType) {
            case UnionBrokeragePayCacheUtil.TYPE_FROM_BUS_ID:
                foreignIdKey = UnionBrokeragePayCacheUtil.getFromBusIdKey(foreignId);
                break;
            case UnionBrokeragePayCacheUtil.TYPE_TO_BUS_ID:
                foreignIdKey = UnionBrokeragePayCacheUtil.getToBusIdKey(foreignId);
                break;
            case UnionBrokeragePayCacheUtil.TYPE_FROM_MEMBER_ID:
                foreignIdKey = UnionBrokeragePayCacheUtil.getFromMemberIdKey(foreignId);
                break;
            case UnionBrokeragePayCacheUtil.TYPE_TO_MEMBER_ID:
                foreignIdKey = UnionBrokeragePayCacheUtil.getToMemberIdKey(foreignId);
                break;
            case UnionBrokeragePayCacheUtil.TYPE_UNION_ID:
                foreignIdKey = UnionBrokeragePayCacheUtil.getUnionIdKey(foreignId);
                break;
            case UnionBrokeragePayCacheUtil.TYPE_OPPORTUNITY_ID:
                foreignIdKey = UnionBrokeragePayCacheUtil.getOpportunityIdKey(foreignId);
                break;
            case UnionBrokeragePayCacheUtil.TYPE_VERIFIER_ID:
                foreignIdKey = UnionBrokeragePayCacheUtil.getVerifierIdKey(foreignId);
                break;
            default:
                break;
        }
        if (foreignIdKey != null) {
            redisCacheUtil.set(foreignIdKey, newUnionBrokeragePayList);
        }
    }

    private void removeCache(UnionBrokeragePay unionBrokeragePay) {
        if (unionBrokeragePay == null) {
            return;
        }
        Integer id = unionBrokeragePay.getId();
        String idKey = UnionBrokeragePayCacheUtil.getIdKey(id);
        redisCacheUtil.remove(idKey);
    }

    private void removeCache(List<UnionBrokeragePay> unionBrokeragePayList) {
        if (ListUtil.isEmpty(unionBrokeragePayList)) {
            return;
        }
        List<Integer> idList = new ArrayList<>();
        for (UnionBrokeragePay unionBrokeragePay : unionBrokeragePayList) {
            idList.add(unionBrokeragePay.getId());
        }
        List<String> idKeyList = UnionBrokeragePayCacheUtil.getIdKey(idList);
        redisCacheUtil.remove(idKeyList);

        List<String> fromBusIdKeyList = getForeignIdKeyList(unionBrokeragePayList, UnionBrokeragePayCacheUtil.TYPE_FROM_BUS_ID);
        if (ListUtil.isNotEmpty(fromBusIdKeyList)) {
            redisCacheUtil.remove(fromBusIdKeyList);
        }

        List<String> toBusIdKeyList = getForeignIdKeyList(unionBrokeragePayList, UnionBrokeragePayCacheUtil.TYPE_TO_BUS_ID);
        if (ListUtil.isNotEmpty(toBusIdKeyList)) {
            redisCacheUtil.remove(toBusIdKeyList);
        }

        List<String> fromMemberIdKeyList = getForeignIdKeyList(unionBrokeragePayList, UnionBrokeragePayCacheUtil.TYPE_FROM_MEMBER_ID);
        if (ListUtil.isNotEmpty(fromMemberIdKeyList)) {
            redisCacheUtil.remove(fromMemberIdKeyList);
        }

        List<String> toMemberIdKeyList = getForeignIdKeyList(unionBrokeragePayList, UnionBrokeragePayCacheUtil.TYPE_TO_MEMBER_ID);
        if (ListUtil.isNotEmpty(toMemberIdKeyList)) {
            redisCacheUtil.remove(toMemberIdKeyList);
        }

        List<String> unionIdKeyList = getForeignIdKeyList(unionBrokeragePayList, UnionBrokeragePayCacheUtil.TYPE_UNION_ID);
        if (ListUtil.isNotEmpty(unionIdKeyList)) {
            redisCacheUtil.remove(unionIdKeyList);
        }

        List<String> opportunityIdKeyList = getForeignIdKeyList(unionBrokeragePayList, UnionBrokeragePayCacheUtil.TYPE_OPPORTUNITY_ID);
        if (ListUtil.isNotEmpty(opportunityIdKeyList)) {
            redisCacheUtil.remove(opportunityIdKeyList);
        }

        List<String> verifierIdKeyList = getForeignIdKeyList(unionBrokeragePayList, UnionBrokeragePayCacheUtil.TYPE_VERIFIER_ID);
        if (ListUtil.isNotEmpty(verifierIdKeyList)) {
            redisCacheUtil.remove(verifierIdKeyList);
        }
    }

    private List<String> getForeignIdKeyList(List<UnionBrokeragePay> unionBrokeragePayList, int foreignIdType) {
        List<String> result = new ArrayList<>();
        switch (foreignIdType) {
            case UnionBrokeragePayCacheUtil.TYPE_FROM_BUS_ID:
                for (UnionBrokeragePay unionBrokeragePay : unionBrokeragePayList) {
                    Integer fromBusId = unionBrokeragePay.getFromBusId();
                    if (fromBusId != null) {
                        String fromBusIdKey = UnionBrokeragePayCacheUtil.getFromBusIdKey(fromBusId);
                        result.add(fromBusIdKey);
                    }
                }
                break;
            case UnionBrokeragePayCacheUtil.TYPE_TO_BUS_ID:
                for (UnionBrokeragePay unionBrokeragePay : unionBrokeragePayList) {
                    Integer toBusId = unionBrokeragePay.getToBusId();
                    if (toBusId != null) {
                        String toBusIdKey = UnionBrokeragePayCacheUtil.getToBusIdKey(toBusId);
                        result.add(toBusIdKey);
                    }
                }
                break;
            case UnionBrokeragePayCacheUtil.TYPE_FROM_MEMBER_ID:
                for (UnionBrokeragePay unionBrokeragePay : unionBrokeragePayList) {
                    Integer fromMemberId = unionBrokeragePay.getFromMemberId();
                    if (fromMemberId != null) {
                        String fromMemberIdKey = UnionBrokeragePayCacheUtil.getFromMemberIdKey(fromMemberId);
                        result.add(fromMemberIdKey);
                    }
                }
                break;
            case UnionBrokeragePayCacheUtil.TYPE_TO_MEMBER_ID:
                for (UnionBrokeragePay unionBrokeragePay : unionBrokeragePayList) {
                    Integer toMemberId = unionBrokeragePay.getToMemberId();
                    if (toMemberId != null) {
                        String toMemberIdKey = UnionBrokeragePayCacheUtil.getToMemberIdKey(toMemberId);
                        result.add(toMemberIdKey);
                    }
                }
                break;
            case UnionBrokeragePayCacheUtil.TYPE_UNION_ID:
                for (UnionBrokeragePay unionBrokeragePay : unionBrokeragePayList) {
                    Integer unionId = unionBrokeragePay.getUnionId();
                    if (unionId != null) {
                        String unionIdKey = UnionBrokeragePayCacheUtil.getUnionIdKey(unionId);
                        result.add(unionIdKey);
                    }
                }
                break;
            case UnionBrokeragePayCacheUtil.TYPE_OPPORTUNITY_ID:
                for (UnionBrokeragePay unionBrokeragePay : unionBrokeragePayList) {
                    Integer opportunityId = unionBrokeragePay.getOpportunityId();
                    if (opportunityId != null) {
                        String opportunityIdKey = UnionBrokeragePayCacheUtil.getOpportunityIdKey(opportunityId);
                        result.add(opportunityIdKey);
                    }
                }
                break;
            case UnionBrokeragePayCacheUtil.TYPE_VERIFIER_ID:
                for (UnionBrokeragePay unionBrokeragePay : unionBrokeragePayList) {
                    Integer verifierId = unionBrokeragePay.getVerifierId();
                    if (verifierId != null) {
                        String verifierIdKey = UnionBrokeragePayCacheUtil.getVerifierIdKey(verifierId);
                        result.add(verifierIdKey);
                    }
                }
                break;
            default:
                break;
        }
        return result;
    }

}