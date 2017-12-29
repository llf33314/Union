package com.gt.union.opportunity.brokerage.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.gt.union.api.client.socket.SocketService;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.constant.ConfigConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.*;
import com.gt.union.opportunity.brokerage.constant.BrokerageConstant;
import com.gt.union.opportunity.brokerage.dao.IUnionBrokeragePayDao;
import com.gt.union.opportunity.brokerage.entity.UnionBrokerageIncome;
import com.gt.union.opportunity.brokerage.entity.UnionBrokeragePay;
import com.gt.union.opportunity.brokerage.service.IUnionBrokerageIncomeService;
import com.gt.union.opportunity.brokerage.service.IUnionBrokeragePayService;
import com.gt.union.opportunity.brokerage.service.IUnionBrokeragePayStrategyService;
import com.gt.union.opportunity.brokerage.util.UnionBrokeragePayCacheUtil;
import com.gt.union.opportunity.brokerage.vo.BrokeragePayVO;
import com.gt.union.opportunity.main.constant.OpportunityConstant;
import com.gt.union.opportunity.main.entity.UnionOpportunity;
import com.gt.union.opportunity.main.service.IUnionOpportunityService;
import com.gt.union.union.main.entity.UnionMain;
import com.gt.union.union.main.service.IUnionMainService;
import com.gt.union.union.main.vo.UnionPayVO;
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
public class UnionBrokeragePayServiceImpl implements IUnionBrokeragePayService {
    private Logger logger = Logger.getLogger(UnionBrokeragePayServiceImpl.class);

    @Autowired
    private IUnionBrokeragePayDao unionBrokeragePayDao;

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
    private SocketService socketService;

    //********************************************* Base On Business - get *********************************************

    @Override
    public BrokeragePayVO getBrokeragePayVOByBusIdAndUnionIdAndMemberId(Integer busId, Integer unionId, Integer memberId) throws Exception {
        if (busId == null || unionId == null || memberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // （1）	判断union有效性和member读权限
        List<UnionMember> memberList = unionMemberService.listByBusIdAndUnionId(busId, unionId);
        if (ListUtil.isEmpty(memberList)) {
            throw new BusinessException(CommonConstant.MEMBER_NOT_FOUND);
        }
        List<Integer> busIdList = unionMemberService.getBusIdList(memberList);
        // （2）	判断memberId有效性
        UnionMember tgtMember = unionMemberService.getById(memberId);
        if (tgtMember == null) {
            throw new BusinessException(CommonConstant.MEMBER_NOT_FOUND);
        }
        // （3）	获取所有支付成功的支付记录
        BrokeragePayVO result = new BrokeragePayVO();
        result.setUnion(unionMainService.getById(unionId));
        result.setMember(tgtMember);
        List<UnionOpportunity> opportunityList = new ArrayList<>();
        List<UnionBrokeragePay> incomePayList = listValidByFromBusIdAndToBusIdListAndStatus(tgtMember.getBusId(), busIdList, BrokerageConstant.PAY_STATUS_SUCCESS);
        BigDecimal incomeBrokerage = BigDecimal.ZERO;
        if (ListUtil.isNotEmpty(incomePayList)) {
            for (UnionBrokeragePay pay : incomePayList) {
                incomeBrokerage = BigDecimalUtil.add(incomeBrokerage, pay.getMoney());

                UnionOpportunity opportunity = unionOpportunityService.getById(pay.getOpportunityId());
                opportunityList.add(opportunity);
            }
        }
        List<UnionBrokeragePay> expensePayList = listValidByFromBusIdListAndToBusIdAndStatus(busIdList, tgtMember.getBusId(), BrokerageConstant.PAY_STATUS_SUCCESS);
        BigDecimal expenseBrokerage = BigDecimal.ZERO;
        if (ListUtil.isNotEmpty(expensePayList)) {
            for (UnionBrokeragePay pay : expensePayList) {
                expenseBrokerage = BigDecimalUtil.add(expenseBrokerage, pay.getMoney());

                UnionOpportunity opportunity = unionOpportunityService.getById(pay.getOpportunityId());
                opportunity.setBrokerageMoney(BigDecimalUtil.subtract(Double.valueOf(0), opportunity.getBrokerageMoney()).doubleValue());
                opportunityList.add(opportunity);
            }
        }
        // （4）按时间倒序排序
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

    //********************************************* Base On Business - list ********************************************

    @Override
    public List<UnionBrokeragePay> listValidByFromBusIdAndToBusIdAndStatus(Integer fromBusId, Integer toBusId, Integer status) throws Exception {
        if (fromBusId == null || toBusId == null || status == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionBrokeragePay> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("from_bus_id", fromBusId)
                .eq("to_bus_id", toBusId)
                .eq("status", status)
                .orderBy("create_time", false);

        return unionBrokeragePayDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionBrokeragePay> listValidByFromBusIdAndToBusIdListAndStatus(Integer fromBusId, List<Integer> toBusIdList, Integer status) throws Exception {
        if (fromBusId == null || toBusIdList == null || status == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionBrokeragePay> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("from_bus_id", fromBusId)
                .eq("status", status)
                .in("to_bus_id", toBusIdList)
                .orderBy("create_time", false);

        return unionBrokeragePayDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionBrokeragePay> listValidByFromBusIdListAndToBusIdAndStatus(List<Integer> fromBusIdList, Integer toBusId, Integer status) throws Exception {
        if (fromBusIdList == null || toBusId == null || status == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionBrokeragePay> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("to_bus_id", toBusId)
                .eq("status", status)
                .in("from_bus_id", fromBusIdList)
                .orderBy("create_time", false);

        return unionBrokeragePayDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionBrokeragePay> listValidByOrderNo(String orderNo) throws Exception {
        if (orderNo == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionBrokeragePay> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("order_no", orderNo);

        return unionBrokeragePayDao.selectList(entityWrapper);
    }

    @Override
    public Page pageBrokerageOpportunityVOByBusId(
            Page page, Integer busId, Integer optUnionId, Integer optFromMemberId, Integer optIsClose, String optClientName, String optClientPhone) throws Exception {
        if (page == null || busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // （1）	获取商家所有的member
        List<UnionMember> memberList = unionMemberService.listByBusId(busId);
        List<Integer> toMemberIdList = unionMemberService.getIdList(memberList);
        // （3）获取已被接受的商机推荐
        EntityWrapper<UnionOpportunity> opportunityEntityWrapper = new EntityWrapper<>();
        opportunityEntityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .in("to_member_id", toMemberIdList)
                .eq(optUnionId != null, "union_id", optUnionId)
                .eq(optFromMemberId != null, "from_member_id", optFromMemberId)
                .eq(optIsClose != null, "is_close", optIsClose)
                .like(StringUtil.isNotEmpty(optClientName), "client_name", optClientName)
                .like(StringUtil.isNotEmpty(optClientPhone), "client_phone", optClientPhone)
                .orderBy("is_close ASC, create_time", true);

        Page result = unionOpportunityService.pageSupport(page, opportunityEntityWrapper);
        List<UnionOpportunity> resultListDate = result.getRecords();
        result.setRecords(unionOpportunityService.listBrokerageOpportunityVO(resultListDate));
        
        return result;
    }

    @Override
    public List<BrokeragePayVO> listBrokeragePayVOByBusId(Integer busId, Integer optUnionId) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // （1）	获取所有有效的member
        List<UnionMember> memberList = unionMemberService.listByBusId(busId);
        if (optUnionId != null) {
            memberList = unionMemberService.filterByUnionId(memberList, optUnionId);
        }
        List<Integer> memberIdList = unionMemberService.getIdList(memberList);
        List<Integer> busIdList = unionMemberService.getBusIdList(memberList);
        // （2）	获取所有与我有商机佣金往来的盟员id列表
        Set<Integer> memberIdSet = new HashSet<>();
        List<UnionOpportunity> fromMeOpportunityList = unionOpportunityService.listValidByFromMemberIdListAndAcceptStatus(memberIdList, OpportunityConstant.ACCEPT_STATUS_CONFIRMED);
        if (ListUtil.isNotEmpty(fromMeOpportunityList)) {
            for (UnionOpportunity opportunity : fromMeOpportunityList) {
                memberIdSet.add(opportunity.getToMemberId());
            }
        }
        List<UnionOpportunity> toMeOpportunityList = unionOpportunityService.listValidByToMemberIdListAndAcceptStatus(memberIdList, OpportunityConstant.ACCEPT_STATUS_CONFIRMED);
        if (ListUtil.isNotEmpty(toMeOpportunityList)) {
            for (UnionOpportunity opportunity : toMeOpportunityList) {
                memberIdSet.add(opportunity.getFromMemberId());
            }
        }
        List<Integer> contactMemberIdList = ListUtil.toList(memberIdSet);
        // （3）	按联盟创建时间顺序、盟员创建时间顺序排序
        List<BrokeragePayVO> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(contactMemberIdList)) {
            for (Integer contactMemberId : contactMemberIdList) {
                UnionMember contactMember = unionMemberService.getById(contactMemberId);
                BrokeragePayVO vo = new BrokeragePayVO();
                vo.setMember(contactMember);

                UnionMain union = unionMainService.getById(contactMember.getUnionId());
                vo.setUnion(union);

                Double incomeMoney = sumValidMoneyByFromBusIdAndToBusIdListAndStatus(contactMember.getBusId(), busIdList, BrokerageConstant.PAY_STATUS_SUCCESS);
                Double expenseMoney = sumValidMoneyByFromBusIdListAndToBusIdAndStatus(busIdList, contactMember.getBusId(), BrokerageConstant.PAY_STATUS_SUCCESS);
                vo.setContactMoney(BigDecimalUtil.subtract(incomeMoney, expenseMoney).doubleValue());

                result.add(vo);
            }
        }

        Collections.sort(result, new Comparator<BrokeragePayVO>() {
            @Override
            public int compare(BrokeragePayVO o1, BrokeragePayVO o2) {
                int c = o1.getUnion().getCreateTime().compareTo(o2.getUnion().getCreateTime());
                if (c > 0) {
                    return 1;
                } else if (c < 0) {
                    return 2;
                }

                return o1.getMember().getCreateTime().compareTo(o2.getMember().getCreateTime());
            }
        });

        return result;
    }

    //********************************************* Base On Business - save ********************************************

    //********************************************* Base On Business - remove ******************************************

    //********************************************* Base On Business - update ******************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UnionPayVO batchPayByBusId(Integer busId, List<Integer> opportunityIdList, Integer verifierId, IUnionBrokeragePayStrategyService unionBrokeragePayStrategyService) throws Exception {
        if (busId == null || opportunityIdList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // （1）	校验参数有效性
        List<UnionBrokeragePay> savePayList = new ArrayList<>();
        BigDecimal brokerageSum = BigDecimal.ZERO;
        Date currentDate = DateUtil.getCurrentDate();
        String orderNo = "LM" + ConfigConstant.PAY_MODEL_OPPORTUNITY + DateUtil.getSerialNumber();
        if (ListUtil.isNotEmpty(opportunityIdList)) {
            for (Integer opportunityId : opportunityIdList) {
                UnionOpportunity opportunity = unionOpportunityService.getById(opportunityId);
                if (opportunity == null) {
                    throw new BusinessException("找不到商机信息");
                }
                // （2）	判断union有效性和member写权限
                UnionMember member = unionMemberService.getByBusIdAndId(busId, opportunity.getToMemberId());
                if (member == null) {
                    throw new BusinessException("不支持代付商机佣金");
                }
                UnionMember fromMember = unionMemberService.getById(opportunity.getFromMemberId());
                if (fromMember == null) {
                    throw new BusinessException("找不到商机推荐者信息");
                }
                // （3）	如果已付款过，则报错
                if (existValidByOpportunityIdAndStatus(opportunityId, BrokerageConstant.PAY_STATUS_SUCCESS)) {
                    throw new BusinessException("请勿重复支付");
                }

                brokerageSum = BigDecimalUtil.add(brokerageSum, opportunity.getBrokerageMoney());

                UnionBrokeragePay savePay = new UnionBrokeragePay();
                savePay.setDelStatus(CommonConstant.COMMON_NO);
                savePay.setCreateTime(currentDate);
                savePay.setStatus(BrokerageConstant.PAY_STATUS_PAYING);
                savePay.setFromMemberId(opportunity.getFromMemberId());
                savePay.setToMemberId(opportunity.getToMemberId());
                savePay.setUnionId(opportunity.getUnionId());
                savePay.setFromBusId(fromMember.getBusId());
                savePay.setToBusId(busId);
                savePay.setSysOrderNo(orderNo);
                savePay.setOpportunityId(opportunityId);
                savePay.setMoney(opportunity.getBrokerageMoney());
                savePay.setVerifierId(verifierId);
                savePayList.add(savePay);
            }
        }
        if (ListUtil.isEmpty(savePayList)) {
            throw new BusinessException("没有有效的支付信息");
        }

        // （4）	调用接口，返回支付链接
        UnionPayVO result = unionBrokeragePayStrategyService.unionBrokerageApply(orderNo, brokerageSum.doubleValue());

        saveBatch(savePayList);
        return result;
    }

    @Override
    public String updateCallbackByOrderNo(String orderNo, String socketKey, String payType, String payOrderNo, Integer isSuccess) {
        Map<String, Object> result = new HashMap<>(2);
        if (orderNo == null || socketKey == null || payType == null || payOrderNo == null || isSuccess == null) {
            result.put("code", -1);
            result.put("msg", "参数缺少");
            return JSONObject.toJSONString(result);
        }

        // （1）	判断payIds有效性
        try {
            List<UnionBrokeragePay> updatePayList = new ArrayList<>();
            List<UnionBrokerageIncome> saveIncomeList = new ArrayList<>();
            List<UnionOpportunity> updateOpportunityList = new ArrayList<>();
            boolean isRepeat = false;
            Date currentDate = DateUtil.getCurrentDate();
            List<UnionBrokeragePay> payList = listValidByOrderNo(orderNo);
            if (ListUtil.isEmpty(payList)) {
                throw new BusinessException("没有支付信息");
            }
            for (UnionBrokeragePay pay : payList) {
                Integer payStatus = pay.getStatus();
                if (BrokerageConstant.PAY_STATUS_SUCCESS == payStatus || BrokerageConstant.PAY_STATUS_FAIL == payStatus) {
                    isRepeat = true;
                    break;
                }

                UnionBrokeragePay updatePay = new UnionBrokeragePay();
                updatePay.setId(pay.getId());
                updatePay.setStatus(CommonConstant.COMMON_YES == isSuccess ? BrokerageConstant.PAY_STATUS_SUCCESS : BrokerageConstant.PAY_STATUS_FAIL);
                if (payType.equals("0")) {
                    updatePay.setType(BrokerageConstant.PAY_TYPE_WX);
                    updatePay.setWxOrderNo(payOrderNo);
                } else {
                    updatePay.setType(BrokerageConstant.PAY_TYPE_ALIPAY);
                    updatePay.setAlipayOrderNo(payOrderNo);
                }
                updatePayList.add(updatePay);

                if (CommonConstant.COMMON_YES == isSuccess) {
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

                    UnionOpportunity updateOpportunity = new UnionOpportunity();
                    updateOpportunity.setId(pay.getOpportunityId());
                    updateOpportunity.setIsClose(CommonConstant.COMMON_YES);
                    updateOpportunityList.add(updateOpportunity);
                }
            }

            // （2）	如果payIds的支付状态存在已处理，则直接返回成功
            if (isRepeat) {
                result.put("code", 0);
                result.put("msg", "重复处理");
                return JSONObject.toJSONString(result);
            }

            if (ListUtil.isNotEmpty(updatePayList)) {
                updateBatch(updatePayList);
            }
            if (ListUtil.isNotEmpty(saveIncomeList)) {
                unionBrokerageIncomeService.saveBatch(saveIncomeList);
            }
            if (ListUtil.isNotEmpty(updateOpportunityList)) {
                unionOpportunityService.updateBatch(updateOpportunityList);
            }

            // socket通知
            if (StringUtil.isNotEmpty(socketKey)) {
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

    //********************************************* Base On Business - other *******************************************

    @Override
    public Double sumValidMoneyByFromBusIdAndToBusIdListAndStatus(Integer fromBusId, List<Integer> toBusIdList, Integer status) throws Exception {
        if (fromBusId == null || toBusIdList == null || status == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        BigDecimal result = BigDecimal.ZERO;
        List<UnionBrokeragePay> payList = listValidByFromBusIdAndToBusIdListAndStatus(fromBusId, toBusIdList, status);
        if (ListUtil.isNotEmpty(payList)) {
            for (UnionBrokeragePay pay : payList) {
                result = BigDecimalUtil.add(result, pay.getMoney());
            }
        }

        return result.doubleValue();
    }

    @Override
    public Double sumValidMoneyByFromBusIdListAndToBusIdAndStatus(List<Integer> fromBusIdList, Integer toBusId, Integer status) throws Exception {
        if (fromBusIdList == null || toBusId == null || status == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        BigDecimal result = BigDecimal.ZERO;
        List<UnionBrokeragePay> payList = listValidByFromBusIdListAndToBusIdAndStatus(fromBusIdList, toBusId, status);
        if (ListUtil.isNotEmpty(payList)) {
            for (UnionBrokeragePay pay : payList) {
                result = BigDecimalUtil.add(result, pay.getMoney());
            }
        }

        return result.doubleValue();
    }

    @Override
    public boolean existValidByOpportunityIdAndStatus(Integer opportunityId, Integer status) throws Exception {
        if (opportunityId == null || status == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionBrokeragePay> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("opportunity_id", opportunityId)
                .eq("status", status);

        return unionBrokeragePayDao.selectOne(entityWrapper) != null;
    }

    //********************************************* Base On Business - filter ******************************************

    @Override
    public List<UnionBrokeragePay> filterByDelStatus(List<UnionBrokeragePay> unionBrokeragePayList, Integer delStatus) throws Exception {
        if (delStatus == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionBrokeragePay> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(unionBrokeragePayList)) {
            for (UnionBrokeragePay unionBrokeragePay : unionBrokeragePayList) {
                if (delStatus.equals(unionBrokeragePay.getDelStatus())) {
                    result.add(unionBrokeragePay);
                }
            }
        }

        return result;
    }

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

    @Override
    public List<UnionBrokeragePay> filterByOpportunityId(List<UnionBrokeragePay> payList, Integer opportunityId) throws Exception {
        if (payList == null || opportunityId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionBrokeragePay> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(payList)) {
            for (UnionBrokeragePay pay : payList) {
                if (opportunityId.equals(pay.getOpportunityId())) {
                    result.add(pay);
                }
            }
        }

        return result;
    }

    @Override
    public List<UnionBrokeragePay> filterByStatus(List<UnionBrokeragePay> payList, Integer status) throws Exception {
        if (payList == null || status == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionBrokeragePay> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(payList)) {
            for (UnionBrokeragePay pay : payList) {
                if (status.equals(pay.getStatus())) {
                    result.add(pay);
                }
            }
        }

        return result;
    }

    @Override
    public List<UnionBrokeragePay> filterByToBusId(List<UnionBrokeragePay> payList, Integer toBusId) throws Exception {
        if (payList == null || toBusId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionBrokeragePay> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(payList)) {
            for (UnionBrokeragePay pay : payList) {
                if (toBusId.equals(pay.getToBusId())) {
                    result.add(pay);
                }
            }
        }

        return result;
    }

    //********************************************* Object As a Service - get ******************************************

    @Override
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
        result = unionBrokeragePayDao.selectById(id);
        setCache(result, id);
        return result;
    }

    @Override
    public UnionBrokeragePay getValidById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionBrokeragePay result = getById(id);

        return result != null && CommonConstant.DEL_STATUS_NO == result.getDelStatus() ? result : null;
    }

    @Override
    public UnionBrokeragePay getInvalidById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionBrokeragePay result = getById(id);

        return result != null && CommonConstant.DEL_STATUS_YES == result.getDelStatus() ? result : null;
    }

    //********************************************* Object As a Service - list *****************************************

    @Override
    public List<Integer> getIdList(List<UnionBrokeragePay> unionBrokeragePayList) throws Exception {
        if (unionBrokeragePayList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<Integer> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(unionBrokeragePayList)) {
            for (UnionBrokeragePay unionBrokeragePay : unionBrokeragePayList) {
                result.add(unionBrokeragePay.getId());
            }
        }

        return result;
    }

    @Override
    public List<UnionBrokeragePay> listByFromBusId(Integer fromBusId) throws Exception {
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
        entityWrapper.eq("from_bus_id", fromBusId);
        result = unionBrokeragePayDao.selectList(entityWrapper);
        setCache(result, fromBusId, UnionBrokeragePayCacheUtil.TYPE_FROM_BUS_ID);
        return result;
    }

    @Override
    public List<UnionBrokeragePay> listValidByFromBusId(Integer fromBusId) throws Exception {
        if (fromBusId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionBrokeragePay> result;
        // (1)cache
        String validFromBusIdKey = UnionBrokeragePayCacheUtil.getValidFromBusIdKey(fromBusId);
        if (redisCacheUtil.exists(validFromBusIdKey)) {
            String tempStr = redisCacheUtil.get(validFromBusIdKey);
            result = JSONArray.parseArray(tempStr, UnionBrokeragePay.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionBrokeragePay> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("from_bus_id", fromBusId);
        result = unionBrokeragePayDao.selectList(entityWrapper);
        setValidCache(result, fromBusId, UnionBrokeragePayCacheUtil.TYPE_FROM_BUS_ID);
        return result;
    }

    @Override
    public List<UnionBrokeragePay> listInvalidByFromBusId(Integer fromBusId) throws Exception {
        if (fromBusId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionBrokeragePay> result;
        // (1)cache
        String invalidFromBusIdKey = UnionBrokeragePayCacheUtil.getInvalidFromBusIdKey(fromBusId);
        if (redisCacheUtil.exists(invalidFromBusIdKey)) {
            String tempStr = redisCacheUtil.get(invalidFromBusIdKey);
            result = JSONArray.parseArray(tempStr, UnionBrokeragePay.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionBrokeragePay> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("from_bus_id", fromBusId);
        result = unionBrokeragePayDao.selectList(entityWrapper);
        setInvalidCache(result, fromBusId, UnionBrokeragePayCacheUtil.TYPE_FROM_BUS_ID);
        return result;
    }

    @Override
    public List<UnionBrokeragePay> listByToBusId(Integer toBusId) throws Exception {
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
        entityWrapper.eq("to_bus_id", toBusId);
        result = unionBrokeragePayDao.selectList(entityWrapper);
        setCache(result, toBusId, UnionBrokeragePayCacheUtil.TYPE_TO_BUS_ID);
        return result;
    }

    @Override
    public List<UnionBrokeragePay> listValidByToBusId(Integer toBusId) throws Exception {
        if (toBusId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionBrokeragePay> result;
        // (1)cache
        String validToBusIdKey = UnionBrokeragePayCacheUtil.getValidToBusIdKey(toBusId);
        if (redisCacheUtil.exists(validToBusIdKey)) {
            String tempStr = redisCacheUtil.get(validToBusIdKey);
            result = JSONArray.parseArray(tempStr, UnionBrokeragePay.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionBrokeragePay> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("to_bus_id", toBusId);
        result = unionBrokeragePayDao.selectList(entityWrapper);
        setValidCache(result, toBusId, UnionBrokeragePayCacheUtil.TYPE_TO_BUS_ID);
        return result;
    }

    @Override
    public List<UnionBrokeragePay> listInvalidByToBusId(Integer toBusId) throws Exception {
        if (toBusId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionBrokeragePay> result;
        // (1)cache
        String invalidToBusIdKey = UnionBrokeragePayCacheUtil.getInvalidToBusIdKey(toBusId);
        if (redisCacheUtil.exists(invalidToBusIdKey)) {
            String tempStr = redisCacheUtil.get(invalidToBusIdKey);
            result = JSONArray.parseArray(tempStr, UnionBrokeragePay.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionBrokeragePay> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("to_bus_id", toBusId);
        result = unionBrokeragePayDao.selectList(entityWrapper);
        setInvalidCache(result, toBusId, UnionBrokeragePayCacheUtil.TYPE_TO_BUS_ID);
        return result;
    }

    @Override
    public List<UnionBrokeragePay> listByFromMemberId(Integer fromMemberId) throws Exception {
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
        entityWrapper.eq("from_member_id", fromMemberId);
        result = unionBrokeragePayDao.selectList(entityWrapper);
        setCache(result, fromMemberId, UnionBrokeragePayCacheUtil.TYPE_FROM_MEMBER_ID);
        return result;
    }

    @Override
    public List<UnionBrokeragePay> listValidByFromMemberId(Integer fromMemberId) throws Exception {
        if (fromMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionBrokeragePay> result;
        // (1)cache
        String validFromMemberIdKey = UnionBrokeragePayCacheUtil.getValidFromMemberIdKey(fromMemberId);
        if (redisCacheUtil.exists(validFromMemberIdKey)) {
            String tempStr = redisCacheUtil.get(validFromMemberIdKey);
            result = JSONArray.parseArray(tempStr, UnionBrokeragePay.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionBrokeragePay> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("from_member_id", fromMemberId);
        result = unionBrokeragePayDao.selectList(entityWrapper);
        setValidCache(result, fromMemberId, UnionBrokeragePayCacheUtil.TYPE_FROM_MEMBER_ID);
        return result;
    }

    @Override
    public List<UnionBrokeragePay> listInvalidByFromMemberId(Integer fromMemberId) throws Exception {
        if (fromMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionBrokeragePay> result;
        // (1)cache
        String invalidFromMemberIdKey = UnionBrokeragePayCacheUtil.getInvalidFromMemberIdKey(fromMemberId);
        if (redisCacheUtil.exists(invalidFromMemberIdKey)) {
            String tempStr = redisCacheUtil.get(invalidFromMemberIdKey);
            result = JSONArray.parseArray(tempStr, UnionBrokeragePay.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionBrokeragePay> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("from_member_id", fromMemberId);
        result = unionBrokeragePayDao.selectList(entityWrapper);
        setInvalidCache(result, fromMemberId, UnionBrokeragePayCacheUtil.TYPE_FROM_MEMBER_ID);
        return result;
    }

    @Override
    public List<UnionBrokeragePay> listByToMemberId(Integer toMemberId) throws Exception {
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
        entityWrapper.eq("to_member_id", toMemberId);
        result = unionBrokeragePayDao.selectList(entityWrapper);
        setCache(result, toMemberId, UnionBrokeragePayCacheUtil.TYPE_TO_MEMBER_ID);
        return result;
    }

    @Override
    public List<UnionBrokeragePay> listValidByToMemberId(Integer toMemberId) throws Exception {
        if (toMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionBrokeragePay> result;
        // (1)cache
        String validToMemberIdKey = UnionBrokeragePayCacheUtil.getValidToMemberIdKey(toMemberId);
        if (redisCacheUtil.exists(validToMemberIdKey)) {
            String tempStr = redisCacheUtil.get(validToMemberIdKey);
            result = JSONArray.parseArray(tempStr, UnionBrokeragePay.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionBrokeragePay> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("to_member_id", toMemberId);
        result = unionBrokeragePayDao.selectList(entityWrapper);
        setValidCache(result, toMemberId, UnionBrokeragePayCacheUtil.TYPE_TO_MEMBER_ID);
        return result;
    }

    @Override
    public List<UnionBrokeragePay> listInvalidByToMemberId(Integer toMemberId) throws Exception {
        if (toMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionBrokeragePay> result;
        // (1)cache
        String invalidToMemberIdKey = UnionBrokeragePayCacheUtil.getInvalidToMemberIdKey(toMemberId);
        if (redisCacheUtil.exists(invalidToMemberIdKey)) {
            String tempStr = redisCacheUtil.get(invalidToMemberIdKey);
            result = JSONArray.parseArray(tempStr, UnionBrokeragePay.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionBrokeragePay> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("to_member_id", toMemberId);
        result = unionBrokeragePayDao.selectList(entityWrapper);
        setInvalidCache(result, toMemberId, UnionBrokeragePayCacheUtil.TYPE_TO_MEMBER_ID);
        return result;
    }

    @Override
    public List<UnionBrokeragePay> listByUnionId(Integer unionId) throws Exception {
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
        entityWrapper.eq("union_id", unionId);
        result = unionBrokeragePayDao.selectList(entityWrapper);
        setCache(result, unionId, UnionBrokeragePayCacheUtil.TYPE_UNION_ID);
        return result;
    }

    @Override
    public List<UnionBrokeragePay> listValidByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionBrokeragePay> result;
        // (1)cache
        String validUnionIdKey = UnionBrokeragePayCacheUtil.getValidUnionIdKey(unionId);
        if (redisCacheUtil.exists(validUnionIdKey)) {
            String tempStr = redisCacheUtil.get(validUnionIdKey);
            result = JSONArray.parseArray(tempStr, UnionBrokeragePay.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionBrokeragePay> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("union_id", unionId);
        result = unionBrokeragePayDao.selectList(entityWrapper);
        setValidCache(result, unionId, UnionBrokeragePayCacheUtil.TYPE_UNION_ID);
        return result;
    }

    @Override
    public List<UnionBrokeragePay> listInvalidByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionBrokeragePay> result;
        // (1)cache
        String invalidUnionIdKey = UnionBrokeragePayCacheUtil.getInvalidUnionIdKey(unionId);
        if (redisCacheUtil.exists(invalidUnionIdKey)) {
            String tempStr = redisCacheUtil.get(invalidUnionIdKey);
            result = JSONArray.parseArray(tempStr, UnionBrokeragePay.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionBrokeragePay> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("union_id", unionId);
        result = unionBrokeragePayDao.selectList(entityWrapper);
        setInvalidCache(result, unionId, UnionBrokeragePayCacheUtil.TYPE_UNION_ID);
        return result;
    }

    @Override
    public List<UnionBrokeragePay> listByOpportunityId(Integer opportunityId) throws Exception {
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
        entityWrapper.eq("opportunity_id", opportunityId);
        result = unionBrokeragePayDao.selectList(entityWrapper);
        setCache(result, opportunityId, UnionBrokeragePayCacheUtil.TYPE_OPPORTUNITY_ID);
        return result;
    }

    @Override
    public List<UnionBrokeragePay> listValidByOpportunityId(Integer opportunityId) throws Exception {
        if (opportunityId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionBrokeragePay> result;
        // (1)cache
        String validOpportunityIdKey = UnionBrokeragePayCacheUtil.getValidOpportunityIdKey(opportunityId);
        if (redisCacheUtil.exists(validOpportunityIdKey)) {
            String tempStr = redisCacheUtil.get(validOpportunityIdKey);
            result = JSONArray.parseArray(tempStr, UnionBrokeragePay.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionBrokeragePay> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("opportunity_id", opportunityId);
        result = unionBrokeragePayDao.selectList(entityWrapper);
        setValidCache(result, opportunityId, UnionBrokeragePayCacheUtil.TYPE_OPPORTUNITY_ID);
        return result;
    }

    @Override
    public List<UnionBrokeragePay> listInvalidByOpportunityId(Integer opportunityId) throws Exception {
        if (opportunityId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionBrokeragePay> result;
        // (1)cache
        String invalidOpportunityIdKey = UnionBrokeragePayCacheUtil.getInvalidOpportunityIdKey(opportunityId);
        if (redisCacheUtil.exists(invalidOpportunityIdKey)) {
            String tempStr = redisCacheUtil.get(invalidOpportunityIdKey);
            result = JSONArray.parseArray(tempStr, UnionBrokeragePay.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionBrokeragePay> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("opportunity_id", opportunityId);
        result = unionBrokeragePayDao.selectList(entityWrapper);
        setInvalidCache(result, opportunityId, UnionBrokeragePayCacheUtil.TYPE_OPPORTUNITY_ID);
        return result;
    }

    @Override
    public List<UnionBrokeragePay> listByVerifierId(Integer verifierId) throws Exception {
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
        entityWrapper.eq("verifier_id", verifierId);
        result = unionBrokeragePayDao.selectList(entityWrapper);
        setCache(result, verifierId, UnionBrokeragePayCacheUtil.TYPE_VERIFIER_ID);
        return result;
    }

    @Override
    public List<UnionBrokeragePay> listValidByVerifierId(Integer verifierId) throws Exception {
        if (verifierId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionBrokeragePay> result;
        // (1)cache
        String validVerifierIdKey = UnionBrokeragePayCacheUtil.getValidVerifierIdKey(verifierId);
        if (redisCacheUtil.exists(validVerifierIdKey)) {
            String tempStr = redisCacheUtil.get(validVerifierIdKey);
            result = JSONArray.parseArray(tempStr, UnionBrokeragePay.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionBrokeragePay> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("verifier_id", verifierId);
        result = unionBrokeragePayDao.selectList(entityWrapper);
        setValidCache(result, verifierId, UnionBrokeragePayCacheUtil.TYPE_VERIFIER_ID);
        return result;
    }

    @Override
    public List<UnionBrokeragePay> listInvalidByVerifierId(Integer verifierId) throws Exception {
        if (verifierId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionBrokeragePay> result;
        // (1)cache
        String invalidVerifierIdKey = UnionBrokeragePayCacheUtil.getInvalidVerifierIdKey(verifierId);
        if (redisCacheUtil.exists(invalidVerifierIdKey)) {
            String tempStr = redisCacheUtil.get(invalidVerifierIdKey);
            result = JSONArray.parseArray(tempStr, UnionBrokeragePay.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionBrokeragePay> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("verifier_id", verifierId);
        result = unionBrokeragePayDao.selectList(entityWrapper);
        setInvalidCache(result, verifierId, UnionBrokeragePayCacheUtil.TYPE_VERIFIER_ID);
        return result;
    }

    @Override
    public List<UnionBrokeragePay> listByIdList(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionBrokeragePay> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(idList)) {
            for (Integer id : idList) {
                result.add(getById(id));
            }
        }

        return result;
    }

    @Override
    public Page pageSupport(Page page, EntityWrapper<UnionBrokeragePay> entityWrapper) throws Exception {
        if (page == null || entityWrapper == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        return unionBrokeragePayDao.selectPage(page, entityWrapper);
    }
    //********************************************* Object As a Service - save *****************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(UnionBrokeragePay newUnionBrokeragePay) throws Exception {
        if (newUnionBrokeragePay == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        unionBrokeragePayDao.insert(newUnionBrokeragePay);
        removeCache(newUnionBrokeragePay);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveBatch(List<UnionBrokeragePay> newUnionBrokeragePayList) throws Exception {
        if (newUnionBrokeragePayList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        unionBrokeragePayDao.insertBatch(newUnionBrokeragePayList);
        removeCache(newUnionBrokeragePayList);
    }

    //********************************************* Object As a Service - remove ***************************************

    @Override
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
        unionBrokeragePayDao.updateById(removeUnionBrokeragePay);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeBatchById(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<UnionBrokeragePay> unionBrokeragePayList = listByIdList(idList);
        removeCache(unionBrokeragePayList);
        // (2)remove in db logically
        List<UnionBrokeragePay> removeUnionBrokeragePayList = new ArrayList<>();
        for (Integer id : idList) {
            UnionBrokeragePay removeUnionBrokeragePay = new UnionBrokeragePay();
            removeUnionBrokeragePay.setId(id);
            removeUnionBrokeragePay.setDelStatus(CommonConstant.DEL_STATUS_YES);
            removeUnionBrokeragePayList.add(removeUnionBrokeragePay);
        }
        unionBrokeragePayDao.updateBatchById(removeUnionBrokeragePayList);
    }

    //********************************************* Object As a Service - update ***************************************

    @Override
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
        unionBrokeragePayDao.updateById(updateUnionBrokeragePay);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBatch(List<UnionBrokeragePay> updateUnionBrokeragePayList) throws Exception {
        if (updateUnionBrokeragePayList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<Integer> idList = getIdList(updateUnionBrokeragePayList);
        List<UnionBrokeragePay> unionBrokeragePayList = listByIdList(idList);
        removeCache(unionBrokeragePayList);
        // (2)update db
        unionBrokeragePayDao.updateBatchById(updateUnionBrokeragePayList);
    }

    //********************************************* Object As a Service - cache support ********************************

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

    private void setValidCache(List<UnionBrokeragePay> newUnionBrokeragePayList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            //do nothing,just in case
            return;
        }
        String validForeignIdKey = null;
        switch (foreignIdType) {
            case UnionBrokeragePayCacheUtil.TYPE_FROM_BUS_ID:
                validForeignIdKey = UnionBrokeragePayCacheUtil.getValidFromBusIdKey(foreignId);
                break;
            case UnionBrokeragePayCacheUtil.TYPE_TO_BUS_ID:
                validForeignIdKey = UnionBrokeragePayCacheUtil.getValidToBusIdKey(foreignId);
                break;
            case UnionBrokeragePayCacheUtil.TYPE_FROM_MEMBER_ID:
                validForeignIdKey = UnionBrokeragePayCacheUtil.getValidFromMemberIdKey(foreignId);
                break;
            case UnionBrokeragePayCacheUtil.TYPE_TO_MEMBER_ID:
                validForeignIdKey = UnionBrokeragePayCacheUtil.getValidToMemberIdKey(foreignId);
                break;
            case UnionBrokeragePayCacheUtil.TYPE_UNION_ID:
                validForeignIdKey = UnionBrokeragePayCacheUtil.getValidUnionIdKey(foreignId);
                break;
            case UnionBrokeragePayCacheUtil.TYPE_OPPORTUNITY_ID:
                validForeignIdKey = UnionBrokeragePayCacheUtil.getValidOpportunityIdKey(foreignId);
                break;
            case UnionBrokeragePayCacheUtil.TYPE_VERIFIER_ID:
                validForeignIdKey = UnionBrokeragePayCacheUtil.getValidVerifierIdKey(foreignId);
                break;

            default:
                break;
        }
        if (validForeignIdKey != null) {
            redisCacheUtil.set(validForeignIdKey, newUnionBrokeragePayList);
        }
    }

    private void setInvalidCache(List<UnionBrokeragePay> newUnionBrokeragePayList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            //do nothing,just in case
            return;
        }
        String invalidForeignIdKey = null;
        switch (foreignIdType) {
            case UnionBrokeragePayCacheUtil.TYPE_FROM_BUS_ID:
                invalidForeignIdKey = UnionBrokeragePayCacheUtil.getInvalidFromBusIdKey(foreignId);
                break;
            case UnionBrokeragePayCacheUtil.TYPE_TO_BUS_ID:
                invalidForeignIdKey = UnionBrokeragePayCacheUtil.getInvalidToBusIdKey(foreignId);
                break;
            case UnionBrokeragePayCacheUtil.TYPE_FROM_MEMBER_ID:
                invalidForeignIdKey = UnionBrokeragePayCacheUtil.getInvalidFromMemberIdKey(foreignId);
                break;
            case UnionBrokeragePayCacheUtil.TYPE_TO_MEMBER_ID:
                invalidForeignIdKey = UnionBrokeragePayCacheUtil.getInvalidToMemberIdKey(foreignId);
                break;
            case UnionBrokeragePayCacheUtil.TYPE_UNION_ID:
                invalidForeignIdKey = UnionBrokeragePayCacheUtil.getInvalidUnionIdKey(foreignId);
                break;
            case UnionBrokeragePayCacheUtil.TYPE_OPPORTUNITY_ID:
                invalidForeignIdKey = UnionBrokeragePayCacheUtil.getInvalidOpportunityIdKey(foreignId);
                break;
            case UnionBrokeragePayCacheUtil.TYPE_VERIFIER_ID:
                invalidForeignIdKey = UnionBrokeragePayCacheUtil.getInvalidVerifierIdKey(foreignId);
                break;

            default:
                break;
        }
        if (invalidForeignIdKey != null) {
            redisCacheUtil.set(invalidForeignIdKey, newUnionBrokeragePayList);
        }
    }

    private void removeCache(UnionBrokeragePay unionBrokeragePay) {
        if (unionBrokeragePay == null) {
            return;
        }
        Integer id = unionBrokeragePay.getId();
        String idKey = UnionBrokeragePayCacheUtil.getIdKey(id);
        redisCacheUtil.remove(idKey);

        Integer fromBusId = unionBrokeragePay.getFromBusId();
        if (fromBusId != null) {
            String fromBusIdKey = UnionBrokeragePayCacheUtil.getFromBusIdKey(fromBusId);
            redisCacheUtil.remove(fromBusIdKey);

            String validFromBusIdKey = UnionBrokeragePayCacheUtil.getValidFromBusIdKey(fromBusId);
            redisCacheUtil.remove(validFromBusIdKey);

            String invalidFromBusIdKey = UnionBrokeragePayCacheUtil.getInvalidFromBusIdKey(fromBusId);
            redisCacheUtil.remove(invalidFromBusIdKey);
        }

        Integer toBusId = unionBrokeragePay.getToBusId();
        if (toBusId != null) {
            String toBusIdKey = UnionBrokeragePayCacheUtil.getToBusIdKey(toBusId);
            redisCacheUtil.remove(toBusIdKey);

            String validToBusIdKey = UnionBrokeragePayCacheUtil.getValidToBusIdKey(toBusId);
            redisCacheUtil.remove(validToBusIdKey);

            String invalidToBusIdKey = UnionBrokeragePayCacheUtil.getInvalidToBusIdKey(toBusId);
            redisCacheUtil.remove(invalidToBusIdKey);
        }

        Integer fromMemberId = unionBrokeragePay.getFromMemberId();
        if (fromMemberId != null) {
            String fromMemberIdKey = UnionBrokeragePayCacheUtil.getFromMemberIdKey(fromMemberId);
            redisCacheUtil.remove(fromMemberIdKey);

            String validFromMemberIdKey = UnionBrokeragePayCacheUtil.getValidFromMemberIdKey(fromMemberId);
            redisCacheUtil.remove(validFromMemberIdKey);

            String invalidFromMemberIdKey = UnionBrokeragePayCacheUtil.getInvalidFromMemberIdKey(fromMemberId);
            redisCacheUtil.remove(invalidFromMemberIdKey);
        }

        Integer toMemberId = unionBrokeragePay.getToMemberId();
        if (toMemberId != null) {
            String toMemberIdKey = UnionBrokeragePayCacheUtil.getToMemberIdKey(toMemberId);
            redisCacheUtil.remove(toMemberIdKey);

            String validToMemberIdKey = UnionBrokeragePayCacheUtil.getValidToMemberIdKey(toMemberId);
            redisCacheUtil.remove(validToMemberIdKey);

            String invalidToMemberIdKey = UnionBrokeragePayCacheUtil.getInvalidToMemberIdKey(toMemberId);
            redisCacheUtil.remove(invalidToMemberIdKey);
        }

        Integer unionId = unionBrokeragePay.getUnionId();
        if (unionId != null) {
            String unionIdKey = UnionBrokeragePayCacheUtil.getUnionIdKey(unionId);
            redisCacheUtil.remove(unionIdKey);

            String validUnionIdKey = UnionBrokeragePayCacheUtil.getValidUnionIdKey(unionId);
            redisCacheUtil.remove(validUnionIdKey);

            String invalidUnionIdKey = UnionBrokeragePayCacheUtil.getInvalidUnionIdKey(unionId);
            redisCacheUtil.remove(invalidUnionIdKey);
        }

        Integer opportunityId = unionBrokeragePay.getOpportunityId();
        if (opportunityId != null) {
            String opportunityIdKey = UnionBrokeragePayCacheUtil.getOpportunityIdKey(opportunityId);
            redisCacheUtil.remove(opportunityIdKey);

            String validOpportunityIdKey = UnionBrokeragePayCacheUtil.getValidOpportunityIdKey(opportunityId);
            redisCacheUtil.remove(validOpportunityIdKey);

            String invalidOpportunityIdKey = UnionBrokeragePayCacheUtil.getInvalidOpportunityIdKey(opportunityId);
            redisCacheUtil.remove(invalidOpportunityIdKey);
        }

        Integer verifierId = unionBrokeragePay.getVerifierId();
        if (verifierId != null) {
            String verifierIdKey = UnionBrokeragePayCacheUtil.getVerifierIdKey(verifierId);
            redisCacheUtil.remove(verifierIdKey);

            String validVerifierIdKey = UnionBrokeragePayCacheUtil.getValidVerifierIdKey(verifierId);
            redisCacheUtil.remove(validVerifierIdKey);

            String invalidVerifierIdKey = UnionBrokeragePayCacheUtil.getInvalidVerifierIdKey(verifierId);
            redisCacheUtil.remove(invalidVerifierIdKey);
        }

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

                        String validFromBusIdKey = UnionBrokeragePayCacheUtil.getValidFromBusIdKey(fromBusId);
                        result.add(validFromBusIdKey);

                        String invalidFromBusIdKey = UnionBrokeragePayCacheUtil.getInvalidFromBusIdKey(fromBusId);
                        result.add(invalidFromBusIdKey);
                    }
                }
                break;
            case UnionBrokeragePayCacheUtil.TYPE_TO_BUS_ID:
                for (UnionBrokeragePay unionBrokeragePay : unionBrokeragePayList) {
                    Integer toBusId = unionBrokeragePay.getToBusId();
                    if (toBusId != null) {
                        String toBusIdKey = UnionBrokeragePayCacheUtil.getToBusIdKey(toBusId);
                        result.add(toBusIdKey);

                        String validToBusIdKey = UnionBrokeragePayCacheUtil.getValidToBusIdKey(toBusId);
                        result.add(validToBusIdKey);

                        String invalidToBusIdKey = UnionBrokeragePayCacheUtil.getInvalidToBusIdKey(toBusId);
                        result.add(invalidToBusIdKey);
                    }
                }
                break;
            case UnionBrokeragePayCacheUtil.TYPE_FROM_MEMBER_ID:
                for (UnionBrokeragePay unionBrokeragePay : unionBrokeragePayList) {
                    Integer fromMemberId = unionBrokeragePay.getFromMemberId();
                    if (fromMemberId != null) {
                        String fromMemberIdKey = UnionBrokeragePayCacheUtil.getFromMemberIdKey(fromMemberId);
                        result.add(fromMemberIdKey);

                        String validFromMemberIdKey = UnionBrokeragePayCacheUtil.getValidFromMemberIdKey(fromMemberId);
                        result.add(validFromMemberIdKey);

                        String invalidFromMemberIdKey = UnionBrokeragePayCacheUtil.getInvalidFromMemberIdKey(fromMemberId);
                        result.add(invalidFromMemberIdKey);
                    }
                }
                break;
            case UnionBrokeragePayCacheUtil.TYPE_TO_MEMBER_ID:
                for (UnionBrokeragePay unionBrokeragePay : unionBrokeragePayList) {
                    Integer toMemberId = unionBrokeragePay.getToMemberId();
                    if (toMemberId != null) {
                        String toMemberIdKey = UnionBrokeragePayCacheUtil.getToMemberIdKey(toMemberId);
                        result.add(toMemberIdKey);

                        String validToMemberIdKey = UnionBrokeragePayCacheUtil.getValidToMemberIdKey(toMemberId);
                        result.add(validToMemberIdKey);

                        String invalidToMemberIdKey = UnionBrokeragePayCacheUtil.getInvalidToMemberIdKey(toMemberId);
                        result.add(invalidToMemberIdKey);
                    }
                }
                break;
            case UnionBrokeragePayCacheUtil.TYPE_UNION_ID:
                for (UnionBrokeragePay unionBrokeragePay : unionBrokeragePayList) {
                    Integer unionId = unionBrokeragePay.getUnionId();
                    if (unionId != null) {
                        String unionIdKey = UnionBrokeragePayCacheUtil.getUnionIdKey(unionId);
                        result.add(unionIdKey);

                        String validUnionIdKey = UnionBrokeragePayCacheUtil.getValidUnionIdKey(unionId);
                        result.add(validUnionIdKey);

                        String invalidUnionIdKey = UnionBrokeragePayCacheUtil.getInvalidUnionIdKey(unionId);
                        result.add(invalidUnionIdKey);
                    }
                }
                break;
            case UnionBrokeragePayCacheUtil.TYPE_OPPORTUNITY_ID:
                for (UnionBrokeragePay unionBrokeragePay : unionBrokeragePayList) {
                    Integer opportunityId = unionBrokeragePay.getOpportunityId();
                    if (opportunityId != null) {
                        String opportunityIdKey = UnionBrokeragePayCacheUtil.getOpportunityIdKey(opportunityId);
                        result.add(opportunityIdKey);

                        String validOpportunityIdKey = UnionBrokeragePayCacheUtil.getValidOpportunityIdKey(opportunityId);
                        result.add(validOpportunityIdKey);

                        String invalidOpportunityIdKey = UnionBrokeragePayCacheUtil.getInvalidOpportunityIdKey(opportunityId);
                        result.add(invalidOpportunityIdKey);
                    }
                }
                break;
            case UnionBrokeragePayCacheUtil.TYPE_VERIFIER_ID:
                for (UnionBrokeragePay unionBrokeragePay : unionBrokeragePayList) {
                    Integer verifierId = unionBrokeragePay.getVerifierId();
                    if (verifierId != null) {
                        String verifierIdKey = UnionBrokeragePayCacheUtil.getVerifierIdKey(verifierId);
                        result.add(verifierIdKey);

                        String validVerifierIdKey = UnionBrokeragePayCacheUtil.getValidVerifierIdKey(verifierId);
                        result.add(validVerifierIdKey);

                        String invalidVerifierIdKey = UnionBrokeragePayCacheUtil.getInvalidVerifierIdKey(verifierId);
                        result.add(invalidVerifierIdKey);
                    }
                }
                break;

            default:
                break;
        }
        return result;
    }

}