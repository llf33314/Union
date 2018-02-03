package com.gt.union.opportunity.brokerage.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.gt.union.api.client.pay.WxPayService;
import com.gt.union.api.client.socket.SocketService;
import com.gt.union.card.consume.constant.ConsumeConstant;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.constant.ConfigConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.response.GtJsonResult;
import com.gt.union.common.util.*;
import com.gt.union.opportunity.brokerage.constant.BrokerageConstant;
import com.gt.union.opportunity.brokerage.dao.IUnionBrokeragePayDao;
import com.gt.union.opportunity.brokerage.entity.UnionBrokerageIncome;
import com.gt.union.opportunity.brokerage.entity.UnionBrokeragePay;
import com.gt.union.opportunity.brokerage.service.IUnionBrokerageIncomeService;
import com.gt.union.opportunity.brokerage.service.IUnionBrokeragePayService;
import com.gt.union.opportunity.brokerage.service.IUnionBrokeragePayStrategyService;
import com.gt.union.opportunity.brokerage.vo.BrokeragePayVO;
import com.gt.union.opportunity.main.constant.OpportunityConstant;
import com.gt.union.opportunity.main.entity.UnionOpportunity;
import com.gt.union.opportunity.main.service.IUnionOpportunityService;
import com.gt.union.refund.opportunity.entity.UnionRefundOpportunity;
import com.gt.union.refund.opportunity.service.IUnionRefundOpportunityService;
import com.gt.union.refund.order.constant.RefundOrderConstant;
import com.gt.union.refund.order.entity.UnionRefundOrder;
import com.gt.union.refund.order.service.IUnionRefundOrderService;
import com.gt.union.union.main.entity.UnionMain;
import com.gt.union.union.main.service.IUnionMainService;
import com.gt.union.union.main.vo.UnionPayVO;
import com.gt.union.union.member.entity.UnionMember;
import com.gt.union.union.member.service.IUnionMemberService;
import org.apache.log4j.Logger;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;

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
    private IUnionOpportunityService unionOpportunityService;

    @Autowired
    private IUnionBrokerageIncomeService unionBrokerageIncomeService;

    @Autowired
    private IUnionMainService unionMainService;

    @Autowired
    private IUnionMemberService unionMemberService;

    @Autowired
    private IUnionRefundOrderService unionRefundOrderService;

    @Autowired
    private IUnionRefundOpportunityService unionRefundOpportunityService;

    @Autowired
    private WxPayService wxPayService;

    @Autowired
    private SocketService socketService;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private TransactionTemplate transactionTemplate;

    //********************************************* Base On Business - get *********************************************

    @Override
    public BrokeragePayVO getBrokeragePayVOByBusIdAndUnionIdAndMemberId(Integer busId, Integer unionId, Integer memberId) throws Exception {
        if (busId == null || unionId == null || memberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // 判断union有效性
        List<UnionMember> memberList = unionMemberService.listByBusIdAndUnionId(busId, unionId);
        if (ListUtil.isEmpty(memberList)) {
            throw new BusinessException(CommonConstant.MEMBER_NOT_FOUND);
        }
        // 判断member读权限
        List<Integer> busIdList = unionMemberService.getBusIdList(memberList);
        // 判断memberId有效性
        UnionMember tgtMember = unionMemberService.getById(memberId);
        if (tgtMember == null) {
            throw new BusinessException(CommonConstant.MEMBER_NOT_FOUND);
        }
        // 获取所有支付成功的支付记录
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
                opportunity.setBrokerageMoney(BigDecimalUtil.toDouble(BigDecimalUtil.subtract(0.0, opportunity.getBrokerageMoney())));
                opportunityList.add(opportunity);
            }
        }
        // 按时间倒序排序
        Collections.sort(opportunityList, new Comparator<UnionOpportunity>() {
            @Override
            public int compare(UnionOpportunity o1, UnionOpportunity o2) {
                return o2.getCreateTime().compareTo(o1.getCreateTime());
            }
        });
        result.setOpportunityList(opportunityList);
        result.setContactMoney(BigDecimalUtil.toDouble(BigDecimalUtil.subtract(incomeBrokerage, expenseBrokerage)));

        return result;
    }

    //********************************************* Base On Business - list ********************************************

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
                .eq(ListUtil.isEmpty(toBusIdList), "to_bus_id", null)
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
                .eq(ListUtil.isEmpty(fromBusIdList), "from_bus_id", null)
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
                .eq("sys_order_no", orderNo);

        return unionBrokeragePayDao.selectList(entityWrapper);
    }

    @Override
    public Page pageBrokerageOpportunityVOByBusId(
            Page page, Integer busId, Integer optUnionId, Integer optFromMemberId, Integer optIsClose, String optClientName, String optClientPhone) throws Exception {
        if (page == null || busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // 获取商家所有的member
        List<UnionMember> memberList = unionMemberService.listByBusId(busId);
        List<Integer> toMemberIdList = unionMemberService.getIdList(memberList);
        // 获取已被接受的商机推荐
        EntityWrapper<UnionOpportunity> opportunityEntityWrapper = new EntityWrapper<>();
        opportunityEntityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("accept_status", OpportunityConstant.ACCEPT_STATUS_CONFIRMED)
                .in("to_member_id", toMemberIdList)
                .eq(ListUtil.isEmpty(toMemberIdList), "to_member_id", null)
                .eq(optUnionId != null, "union_id", optUnionId)
                .eq(optFromMemberId != null, "from_member_id", optFromMemberId)
                .eq(optIsClose != null, "is_close", optIsClose)
                .like(StringUtil.isNotEmpty(optClientName), "client_name", optClientName)
                .like(StringUtil.isNotEmpty(optClientPhone), "client_phone", optClientPhone)
                .orderBy("is_close ASC, create_time", true);

        Page result = unionOpportunityService.pageSupport(page, opportunityEntityWrapper);
        // toVO
        List<UnionOpportunity> resultListDate = result.getRecords();
        result.setRecords(unionOpportunityService.listBrokerageOpportunityVO(resultListDate));

        return result;
    }

    @Override
    public Page pageBrokeragePayVOByBusId(Page page, Integer busId, Integer optUnionId) throws Exception {
        if (page == null || busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        // 获取所有有效的member
        List<UnionMember> memberList = unionMemberService.listByBusId(busId);
        if (optUnionId != null) {
            memberList = unionMemberService.filterByUnionId(memberList, optUnionId);
        }
        List<Integer> memberIdList = unionMemberService.getIdList(memberList);
        List<Integer> busIdList = unionMemberService.getBusIdList(memberList);
        // 获取所有与我有商机佣金往来的盟员id列表
        Set<Integer> memberIdSet = new HashSet<>();
        List<UnionOpportunity> fromMeOpportunityList = unionOpportunityService.listValidByFromMemberIdListAndAcceptStatus(memberIdList, OpportunityConstant.ACCEPT_STATUS_CONFIRMED);
        if (ListUtil.isNotEmpty(fromMeOpportunityList)) {
            memberIdSet.addAll(unionOpportunityService.getToMemberIdList(fromMeOpportunityList));
        }
        List<UnionOpportunity> toMeOpportunityList = unionOpportunityService.listValidByToMemberIdListAndAcceptStatus(memberIdList, OpportunityConstant.ACCEPT_STATUS_CONFIRMED);
        if (ListUtil.isNotEmpty(toMeOpportunityList)) {
            memberIdSet.addAll(unionOpportunityService.getFromMemberIdList(toMeOpportunityList));
        }
        List<Integer> contactMemberIdList = ListUtil.toList(memberIdSet);
        // 按联盟创建时间顺序、盟员创建时间顺序排序
        EntityWrapper<UnionMember> entityWrapper = new EntityWrapper<>();
        entityWrapper.in("id", contactMemberIdList)
                .eq(ListUtil.isEmpty(contactMemberIdList), "id", null)
                .orderBy("union_id ASC, create_time", true);
        Page result = unionMemberService.pageSupport(page, entityWrapper);
        // toVO
        List<UnionMember> dataList = result.getRecords();
        result.setRecords(listBrokeragePayVO(busIdList, dataList));

        return result;
    }

    @Override
    public List<BrokeragePayVO> listBrokeragePayVOByBusId(Integer busId, Integer optUnionId) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // 获取所有有效的member
        List<UnionMember> memberList = unionMemberService.listByBusId(busId);
        if (optUnionId != null) {
            memberList = unionMemberService.filterByUnionId(memberList, optUnionId);
        }
        List<Integer> memberIdList = unionMemberService.getIdList(memberList);
        List<Integer> busIdList = unionMemberService.getBusIdList(memberList);
        // 获取所有与我有商机佣金往来的盟员id列表
        Set<Integer> memberIdSet = new HashSet<>();
        List<UnionOpportunity> fromMeOpportunityList = unionOpportunityService.listValidByFromMemberIdListAndAcceptStatus(memberIdList, OpportunityConstant.ACCEPT_STATUS_CONFIRMED);
        if (ListUtil.isNotEmpty(fromMeOpportunityList)) {
            memberIdSet.addAll(unionOpportunityService.getToMemberIdList(fromMeOpportunityList));
        }
        List<UnionOpportunity> toMeOpportunityList = unionOpportunityService.listValidByToMemberIdListAndAcceptStatus(memberIdList, OpportunityConstant.ACCEPT_STATUS_CONFIRMED);
        if (ListUtil.isNotEmpty(toMeOpportunityList)) {
            memberIdSet.addAll(unionOpportunityService.getFromMemberIdList(toMeOpportunityList));
        }
        List<Integer> contactMemberIdList = ListUtil.toList(memberIdSet);
        // 按联盟创建时间顺序、盟员创建时间顺序排序
        EntityWrapper<UnionMember> entityWrapper = new EntityWrapper<>();
        entityWrapper.in("id", contactMemberIdList)
                .eq(ListUtil.isEmpty(contactMemberIdList), "id", null)
                .orderBy("union_id ASC, create_time", true);
        List<UnionMember> dataList = unionMemberService.listSupport(entityWrapper);
        // toVO
        return listBrokeragePayVO(busIdList, dataList);
    }

    private List<BrokeragePayVO> listBrokeragePayVO(List<Integer> busIdList, List<UnionMember> resultDataList) throws Exception {
        List<BrokeragePayVO> result = new ArrayList<>();

        if (ListUtil.isNotEmpty(resultDataList)) {
            for (UnionMember contactMember : resultDataList) {
                BrokeragePayVO vo = new BrokeragePayVO();
                vo.setMember(contactMember);

                UnionMain union = unionMainService.getById(contactMember.getUnionId());
                vo.setUnion(union);

                Double incomeMoney = sumValidMoneyByFromBusIdAndToBusIdListAndStatus(contactMember.getBusId(), busIdList, BrokerageConstant.PAY_STATUS_SUCCESS);
                Double expenseMoney = sumValidMoneyByFromBusIdListAndToBusIdAndStatus(busIdList, contactMember.getBusId(), BrokerageConstant.PAY_STATUS_SUCCESS);
                vo.setContactMoney(BigDecimalUtil.toDouble(BigDecimalUtil.subtract(incomeMoney, expenseMoney)));

                result.add(vo);
            }
        }

        return result;
    }

    //********************************************* Base On Business - save ********************************************

    //********************************************* Base On Business - remove ******************************************

    //********************************************* Base On Business - update ******************************************

    @Override
    public UnionPayVO batchPayByBusId(Integer busId, List<Integer> opportunityIdList, Integer verifierId, IUnionBrokeragePayStrategyService unionBrokeragePayStrategyService, Integer memberId) throws Exception {
        if (busId == null || opportunityIdList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        logger.info("商机支付请求参数信息：busId == " + busId + ", verifierId == " + verifierId + ", opportunityIdList : " +JSONObject.toJSONString(opportunityIdList) + ", memberId == " + memberId);
        logger.info("商机支付模块：=====>" + unionBrokeragePayStrategyService.getClass().getName());
        // 校验参数有效性
        List<UnionBrokeragePay> savePayList = new ArrayList<>();
        BigDecimal brokerageSum = BigDecimal.ZERO;
        Date currentDate = DateUtil.getCurrentDate();
        String orderNo = "LM" + ConfigConstant.PAY_MODEL_OPPORTUNITY + DateUtil.getSerialNumber();
        if (ListUtil.isNotEmpty(opportunityIdList)) {
            for (Integer opportunityId : opportunityIdList) {
                UnionOpportunity opportunity = unionOpportunityService.getValidById(opportunityId);
                if (opportunity == null) {
                    throw new BusinessException("找不到商机信息");
                }
                // 判断union有效性和member写权限
                UnionMember member = unionMemberService.getValidReadByBusIdAndId(busId, opportunity.getToMemberId());
                if (member == null) {
                    throw new BusinessException("不支持代付商机佣金");
                }
                UnionMember fromMember = unionMemberService.getById(opportunity.getFromMemberId());
                if (fromMember == null) {
                    throw new BusinessException("找不到商机推荐者信息");
                }
                // 如果已付款过，则报错
                if (existValidByOpportunityIdAndStatus(opportunityId, BrokerageConstant.PAY_STATUS_SUCCESS)) {
                    throw new BusinessException("请勿重复支付");
                }

                brokerageSum = BigDecimalUtil.add(brokerageSum, opportunity.getBrokerageMoney());

                UnionBrokeragePay savePay = new UnionBrokeragePay();
                savePay.setDelStatus(CommonConstant.COMMON_NO);
                savePay.setCreateTime(currentDate);
                savePay.setStatus(BrokerageConstant.PAY_STATUS_PAYING);
                savePay.setFromMemberId(opportunity.getToMemberId());
                savePay.setToMemberId(opportunity.getFromMemberId());
                savePay.setUnionId(opportunity.getUnionId());
                savePay.setFromBusId(busId);
                savePay.setToBusId(fromMember.getBusId());
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

        // 调用接口，返回支付链接
        UnionPayVO result = unionBrokeragePayStrategyService.unionBrokerageApply(orderNo, BigDecimalUtil.toDouble(brokerageSum), memberId, busId);

        saveBatch(savePayList);
        return result;
    }

    @Override
    public String updateCallbackByOrderNo(final String orderNo, String socketKey, final String payType, final String payOrderNo, final Integer isSuccess) {
        Map<String, Object> result = new HashMap<>(2);
        if (orderNo == null || socketKey == null || payType == null || payOrderNo == null || isSuccess == null) {
            result.put("code", -1);
            result.put("msg", "参数缺少");
            return JSONObject.toJSONString(result);
        }
        RLock rLock = null;
        RLock rLock1 = null;
        try{
            rLock1 = redissonClient.getLock(orderNo);
            rLock1.lock(10, TimeUnit.SECONDS);
            List<UnionBrokeragePay> payList = listValidByOrderNo(orderNo);
            if (ListUtil.isEmpty(payList)) {
                result.put("code", -1);
                result.put("msg", "不存在商机支付信息");
                return JSONObject.toJSONString(result);
            }
            Integer busId = payList.get(0).getFromBusId();
            //锁
            String key = busId.toString();
            rLock = redissonClient.getLock(key);
            rLock.lock(10, TimeUnit.SECONDS);
            List<Integer> opportunityIds = new ArrayList<Integer>();
            for (UnionBrokeragePay pay : payList) {
                Integer payStatus = pay.getStatus();
                if (BrokerageConstant.PAY_STATUS_SUCCESS == payStatus || BrokerageConstant.PAY_STATUS_FAIL == payStatus) {
                    result.put("code", -1);
                    result.put("msg", "重复处理");
                    return JSONObject.toJSONString(result);
                }
                opportunityIds.add(pay.getOpportunityId());
            }
            List<UnionOpportunity> opportunities = unionOpportunityService.listByIdList(opportunityIds);
            final List<UnionOpportunity> refundOpportunityList = new ArrayList<>();
            if(ListUtil.isNotEmpty(opportunities)){
                //订单总额
                double totalMoney = 0d;
                //退款金额
                double refundMoney = 0d;
                final Date currentDate = DateUtil.getCurrentDate();
                final List<UnionOpportunity> updateOpportunityList = new ArrayList<>();
                final List<UnionBrokerageIncome> saveIncomeList = new ArrayList<>();
                final List<Integer> opportunityIdList = new ArrayList<Integer>();
                for(UnionOpportunity opportunity : opportunities){
                    totalMoney = BigDecimalUtil.add(totalMoney, opportunity.getBrokerageMoney()).doubleValue();
                    if(CommonConstant.COMMON_YES == opportunity.getIsClose()){
                        //已结算  需退款
                        refundMoney = BigDecimalUtil.add(refundMoney, opportunity.getBrokerageMoney()).doubleValue();
                        refundOpportunityList.add(opportunity);
                    }else {
                        //未结算
                        opportunityIdList.add(opportunity.getId());

                        //商机结算状态
                        UnionOpportunity updateOpportunity = new UnionOpportunity();
                        updateOpportunity.setId(opportunity.getId());
                        updateOpportunity.setIsClose(CommonConstant.COMMON_YES);
                        updateOpportunityList.add(updateOpportunity);

                        //支付成功 添加商机收入
                        if (CommonConstant.COMMON_YES == isSuccess) {
                            UnionMember member = unionMemberService.getById(opportunity.getFromMemberId());
                            UnionBrokerageIncome saveIncome = new UnionBrokerageIncome();
                            saveIncome.setDelStatus(CommonConstant.DEL_STATUS_NO);
                            saveIncome.setCreateTime(currentDate);
                            saveIncome.setType(BrokerageConstant.INCOME_TYPE_OPPORTUNITY);
                            saveIncome.setMoney(opportunity.getBrokerageMoney());
                            saveIncome.setBusId(member.getBusId());
                            saveIncome.setMemberId(opportunity.getFromMemberId());
                            saveIncome.setUnionId(opportunity.getUnionId());
                            saveIncome.setOpportunityId(opportunity.getId());
                            saveIncomeList.add(saveIncome);
                        }
                    }
                }
                final double refundFee = refundMoney;
                final double totalFee = totalMoney;
                //事务
                GtJsonResult gtJsonResult = transactionTemplate.execute(new TransactionCallback<GtJsonResult>(){

                    @Override
                    public GtJsonResult doInTransaction(TransactionStatus status) {

                        try{
                            //更新订单状态
                            if (ListUtil.isNotEmpty(opportunityIdList)) {
                                EntityWrapper wrapper = new EntityWrapper<>();
                                wrapper.eq("sys_order_no", orderNo);
                                wrapper.eq("del_status", CommonConstant.DEL_STATUS_NO);
                                wrapper.eq("status", ConsumeConstant.PAY_STATUS_PAYING);
                                wrapper.in("opportunity_id", opportunityIdList);

                                UnionBrokeragePay pay = new UnionBrokeragePay();
                                pay.setStatus(CommonConstant.COMMON_YES == isSuccess ? BrokerageConstant.PAY_STATUS_SUCCESS : BrokerageConstant.PAY_STATUS_FAIL);
                                if ("0".equals(payType)) {
                                    pay.setType(BrokerageConstant.PAY_TYPE_WX);
                                    pay.setWxOrderNo(payOrderNo);
                                } else {
                                    pay.setType(BrokerageConstant.PAY_TYPE_ALIPAY);
                                    pay.setAlipayOrderNo(payOrderNo);
                                }
                                unionBrokeragePayDao.update(pay, wrapper);
                            }
                            //添加商机收入
                            if (ListUtil.isNotEmpty(saveIncomeList)) {
                                unionBrokerageIncomeService.saveBatch(saveIncomeList);
                            }
                            //更新商机结算状态
                            if (ListUtil.isNotEmpty(updateOpportunityList)) {
                                unionOpportunityService.updateBatch(updateOpportunityList);
                            }
                            //退款列表
                            if(ListUtil.isNotEmpty(refundOpportunityList)){
                                //去退款
                                refundOpportunity(orderNo, refundFee, totalFee, refundOpportunityList);
                            }
                        }catch (Exception e){
                            logger.error("操作失败",e);
                            status.setRollbackOnly();
                            return GtJsonResult.instanceErrorMsg(e.getMessage());
                        }
                        return GtJsonResult.instanceSuccessMsg();
                    }
                });
                if(gtJsonResult.isSuccess()){
                    //成功
                    // socket通知
                    if (StringUtil.isNotEmpty(socketKey)) {
                        socketService.socketPaySendMessage(socketKey, isSuccess, null, orderNo);
                    }
                    result.put("code", 0);
                    result.put("msg", "成功");
                }else {
                    //异常
                    result.put("code", -1);
                    result.put("msg", gtJsonResult.getErrorMsg());
                    return JSONObject.toJSONString(result);
                }
            }else {
                result.put("code", -1);
                result.put("msg", "不存在商机信息");
                return JSONObject.toJSONString(result);
            }
        }catch (Exception e) {
            logger.error("", e);
            result.put("code", -1);
            result.put("msg", e.getMessage());
            return JSONObject.toJSONString(result);
        }finally {
            if(rLock != null){
                rLock.unlock();
            }
            if(rLock1 != null){
                rLock1.unlock();
            }
        }

        return JSONObject.toJSONString(result);
    }

    @Async
    private void refundOpportunity(final String orderNo, final double refundFee, double totalFee, final List<UnionOpportunity> refundOpportunityList) {
        try{
            UnionRefundOrder successRefundOrder = unionRefundOrderService.getValidByOrderNoAndStatusAndType(orderNo, RefundOrderConstant.REFUND_STATUS_SUCCESS, RefundOrderConstant.TYPE_OPPORTUNITY);
            //没有退款
            if(successRefundOrder == null){
                final GtJsonResult gtJsonResult = wxPayService.refundOrder(orderNo,refundFee, totalFee);
                final Map data = (Map)gtJsonResult.getData();

                Boolean result = transactionTemplate.execute(new TransactionCallback<Boolean>(){

                    @Override
                    public Boolean doInTransaction(TransactionStatus transactionStatus) {
                        try{
                            Date currentDate = new Date();
                            UnionRefundOrder applyRefundOrder = unionRefundOrderService.getValidByOrderNoAndStatusAndType(orderNo, RefundOrderConstant.REFUND_STATUS_APPLYING, RefundOrderConstant.TYPE_OPPORTUNITY);
                            if(applyRefundOrder == null){
                                applyRefundOrder = new UnionRefundOrder();
                                applyRefundOrder.setDelStatus(CommonConstant.DEL_STATUS_NO);
                                applyRefundOrder.setCreateTime(currentDate);
                                applyRefundOrder.setRefundMoney(refundFee);
                                applyRefundOrder.setSysOrderNo(orderNo);
                                applyRefundOrder.setType(RefundOrderConstant.TYPE_OPPORTUNITY);
                                applyRefundOrder.setStatus(RefundOrderConstant.REFUND_STATUS_SUCCESS);
                                unionRefundOrderService.save(applyRefundOrder);

                                if(ListUtil.isNotEmpty(refundOpportunityList)){
                                    List<UnionRefundOpportunity> refundOpporList = new ArrayList<UnionRefundOpportunity>();
                                    for(UnionOpportunity opportunity : refundOpportunityList){
                                        UnionRefundOpportunity refundOpportunity = new UnionRefundOpportunity();
                                        refundOpportunity.setCreateTime(new Date());
                                        refundOpportunity.setOpportunityId(opportunity.getId());
                                        refundOpportunity.setDelStatus(CommonConstant.DEL_STATUS_NO);
                                        refundOpportunity.setRefundOrderId(applyRefundOrder.getId());
                                        refundOpporList.add(refundOpportunity);
                                    }
                                    unionRefundOpportunityService.saveBatch(refundOpporList);
                                }
                            }

                            if(gtJsonResult.isSuccess()){
                                UnionRefundOrder successRefundOrder = new UnionRefundOrder();
                                successRefundOrder.setId(applyRefundOrder.getId());
                                successRefundOrder.setModifyTime(new Date());
                                successRefundOrder.setDesc("退款成功");
                                successRefundOrder.setStatus(RefundOrderConstant.REFUND_STATUS_SUCCESS);
                                successRefundOrder.setRefundOrderNo(CommonUtil.isNotEmpty(data.get("data")) ? data.get("data").toString() : "");
                                unionRefundOrderService.update(successRefundOrder);
                            }

                        }catch (Exception e){
                            logger.error("商机退款失败", e);
                            transactionStatus.setRollbackOnly();
                            return false;
                        }
                        return true;
                    }
                });
            }
        }catch (Exception e){
            logger.error("商机退款失败", e);
        }
    }



    //********************************************* Base On Business - other *******************************************

    @Override
    public Double sumValidMoneyByFromBusIdAndToBusIdListAndStatus(Integer fromBusId, List<Integer> toBusIdList, Integer status) throws Exception {
        if (fromBusId == null || toBusIdList == null || status == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionBrokeragePay> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("from_bus_id", fromBusId)
                .eq("status", status)
                .in("to_bus_id", toBusIdList)
                .eq(ListUtil.isEmpty(toBusIdList), "to_bus_id", null);

        entityWrapper.setSqlSelect("IfNull(SUM(money),0) moneySum");
        Map<String, Object> resultMap = unionBrokeragePayDao.selectMap(entityWrapper);

        return Double.valueOf(resultMap.get("moneySum").toString());
    }

    @Override
    public Double sumValidMoneyByFromBusIdListAndToBusIdAndStatus(List<Integer> fromBusIdList, Integer toBusId, Integer status) throws Exception {
        if (fromBusIdList == null || toBusId == null || status == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionBrokeragePay> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .in("from_bus_id", fromBusIdList)
                .eq(ListUtil.isEmpty(fromBusIdList), "from_bus_id", null)
                .eq("status", status)
                .eq("to_bus_id", toBusId);

        entityWrapper.setSqlSelect("IfNull(SUM(money),0) moneySum");
        Map<String, Object> resultMap = unionBrokeragePayDao.selectMap(entityWrapper);

        return Double.valueOf(resultMap.get("moneySum").toString());
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

        return unionBrokeragePayDao.selectCount(entityWrapper) > 0;
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

        return unionBrokeragePayDao.selectById(id);
    }

    @Override
    public UnionBrokeragePay getValidById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionBrokeragePay> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("id", id);

        return unionBrokeragePayDao.selectOne(entityWrapper);
    }

    @Override
    public UnionBrokeragePay getInvalidById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionBrokeragePay> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("id", id);

        return unionBrokeragePayDao.selectOne(entityWrapper);
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

        EntityWrapper<UnionBrokeragePay> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("from_bus_id", fromBusId);

        return unionBrokeragePayDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionBrokeragePay> listValidByFromBusId(Integer fromBusId) throws Exception {
        if (fromBusId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionBrokeragePay> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("from_bus_id", fromBusId);

        return unionBrokeragePayDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionBrokeragePay> listInvalidByFromBusId(Integer fromBusId) throws Exception {
        if (fromBusId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionBrokeragePay> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("from_bus_id", fromBusId);

        return unionBrokeragePayDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionBrokeragePay> listByToBusId(Integer toBusId) throws Exception {
        if (toBusId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionBrokeragePay> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("to_bus_id", toBusId);

        return unionBrokeragePayDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionBrokeragePay> listValidByToBusId(Integer toBusId) throws Exception {
        if (toBusId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionBrokeragePay> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("to_bus_id", toBusId);

        return unionBrokeragePayDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionBrokeragePay> listInvalidByToBusId(Integer toBusId) throws Exception {
        if (toBusId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionBrokeragePay> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("to_bus_id", toBusId);

        return unionBrokeragePayDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionBrokeragePay> listByFromMemberId(Integer fromMemberId) throws Exception {
        if (fromMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionBrokeragePay> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("from_member_id", fromMemberId);

        return unionBrokeragePayDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionBrokeragePay> listValidByFromMemberId(Integer fromMemberId) throws Exception {
        if (fromMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionBrokeragePay> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("from_member_id", fromMemberId);

        return unionBrokeragePayDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionBrokeragePay> listInvalidByFromMemberId(Integer fromMemberId) throws Exception {
        if (fromMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionBrokeragePay> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("from_member_id", fromMemberId);

        return unionBrokeragePayDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionBrokeragePay> listByToMemberId(Integer toMemberId) throws Exception {
        if (toMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionBrokeragePay> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("to_member_id", toMemberId);

        return unionBrokeragePayDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionBrokeragePay> listValidByToMemberId(Integer toMemberId) throws Exception {
        if (toMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionBrokeragePay> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("to_member_id", toMemberId);

        return unionBrokeragePayDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionBrokeragePay> listInvalidByToMemberId(Integer toMemberId) throws Exception {
        if (toMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionBrokeragePay> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("to_member_id", toMemberId);

        return unionBrokeragePayDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionBrokeragePay> listByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionBrokeragePay> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("union_id", unionId);

        return unionBrokeragePayDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionBrokeragePay> listValidByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionBrokeragePay> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("union_id", unionId);

        return unionBrokeragePayDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionBrokeragePay> listInvalidByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionBrokeragePay> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("union_id", unionId);

        return unionBrokeragePayDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionBrokeragePay> listByOpportunityId(Integer opportunityId) throws Exception {
        if (opportunityId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionBrokeragePay> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("opportunity_id", opportunityId);

        return unionBrokeragePayDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionBrokeragePay> listValidByOpportunityId(Integer opportunityId) throws Exception {
        if (opportunityId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionBrokeragePay> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("opportunity_id", opportunityId);

        return unionBrokeragePayDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionBrokeragePay> listInvalidByOpportunityId(Integer opportunityId) throws Exception {
        if (opportunityId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionBrokeragePay> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("opportunity_id", opportunityId);

        return unionBrokeragePayDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionBrokeragePay> listByVerifierId(Integer verifierId) throws Exception {
        if (verifierId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionBrokeragePay> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("verifier_id", verifierId);

        return unionBrokeragePayDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionBrokeragePay> listValidByVerifierId(Integer verifierId) throws Exception {
        if (verifierId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionBrokeragePay> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("verifier_id", verifierId);

        return unionBrokeragePayDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionBrokeragePay> listInvalidByVerifierId(Integer verifierId) throws Exception {
        if (verifierId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionBrokeragePay> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("verifier_id", verifierId);

        return unionBrokeragePayDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionBrokeragePay> listByIdList(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionBrokeragePay> entityWrapper = new EntityWrapper<>();
        entityWrapper.in("id", idList).eq(ListUtil.isEmpty(idList), "id", null);

        return unionBrokeragePayDao.selectList(entityWrapper);
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
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveBatch(List<UnionBrokeragePay> newUnionBrokeragePayList) throws Exception {
        if (newUnionBrokeragePayList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        unionBrokeragePayDao.insertBatch(newUnionBrokeragePayList);
    }

    //********************************************* Object As a Service - remove ***************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

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

        unionBrokeragePayDao.updateById(updateUnionBrokeragePay);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBatch(List<UnionBrokeragePay> updateUnionBrokeragePayList) throws Exception {
        if (updateUnionBrokeragePayList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        unionBrokeragePayDao.updateBatchById(updateUnionBrokeragePayList);
    }

}