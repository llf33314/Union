package com.gt.union.opportunity.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.api.bean.session.WxPublicUsers;
import com.gt.union.api.client.user.IBusUserService;
import com.gt.union.brokerage.constant.BrokerageConstant;
import com.gt.union.brokerage.entity.UnionBrokerageIncome;
import com.gt.union.brokerage.entity.UnionBrokeragePay;
import com.gt.union.brokerage.service.IUnionBrokerageIncomeService;
import com.gt.union.brokerage.service.IUnionBrokeragePayService;
import com.gt.union.common.amqp.entity.PhoneMessage;
import com.gt.union.common.amqp.sender.PhoneMessageSender;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.constant.ConfigConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.*;
import com.gt.union.main.entity.UnionMain;
import com.gt.union.main.service.IUnionMainService;
import com.gt.union.member.constant.MemberConstant;
import com.gt.union.member.entity.UnionMember;
import com.gt.union.member.service.IUnionMemberService;
import com.gt.union.opportunity.constant.OpportunityConstant;
import com.gt.union.opportunity.entity.UnionOpportunity;
import com.gt.union.opportunity.entity.UnionOpportunityRatio;
import com.gt.union.opportunity.mapper.UnionOpportunityMapper;
import com.gt.union.opportunity.service.IUnionOpportunityRatioService;
import com.gt.union.opportunity.service.IUnionOpportunityService;
import com.gt.union.opportunity.vo.UnionOpportunityVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * <p>
 * 商机推荐 服务实现类
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
@Service
public class UnionOpportunityServiceImpl extends ServiceImpl<UnionOpportunityMapper, UnionOpportunity> implements IUnionOpportunityService {

    @Autowired
    private IUnionMemberService unionMemberService;

    @Autowired
    private IUnionMainService unionMainService;

    @Autowired
    private PhoneMessageSender phoneMessageSender;

    @Autowired
    private IUnionOpportunityRatioService unionOpportunityBrokerageRatioService;

    @Autowired
    private IBusUserService busUserService;

    @Autowired
    private IUnionBrokerageIncomeService unionBrokerageIncomeService;

    @Autowired
    private IUnionBrokeragePayService unionBrokeragePayService;

    @Autowired
    private RedisCacheUtil redisCacheUtil;

    /*******************************************************************************************************************
     ****************************************** Domain Driven Design - get *********************************************
     ******************************************************************************************************************/

    @Override
    public Map<String, Object> getContactDetailByBusIdAndTgtMemberId(Integer busId, Integer tgtMemberId, Integer userMemberId) throws Exception {
        if (busId == null || tgtMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        Map<String, Object> result = new HashMap<>();
        //(1)目标盟员姓名
        UnionMember tgtMember = this.unionMemberService.getById(tgtMemberId);
        if (tgtMember == null) {
            throw new BusinessException("找不到目标盟员信息");
        }
        result.put("tgtMemberEnterpriseName", tgtMember.getEnterpriseName()); //目标盟员姓名
        //(2)目标盟员所在联盟名称
        UnionMain unionMain = this.unionMainService.getById(tgtMember.getUnionId());
        if (unionMain == null) {
            throw new BusinessException("找不到目标盟员所在的联盟信息");
        }
        result.put("tgtUnionName", unionMain.getName()); //目标盟员所在联盟名称
        //(3)商机推荐支付往来信息
        List<Map<String, Object>> contactList = new ArrayList<>();
        BigDecimal contactMoneySum = null;
        if (userMemberId == null) {
            UnionMember userMember = this.unionMemberService.getByBusIdAndUnionId(busId, unionMain.getId());
            if (userMember == null) {
                throw new BusinessException(CommonConstant.UNION_MEMBER_INVALID);
            }
            userMemberId = userMember.getId();
        }
        //(4-1)我推荐的商机
        List<UnionOpportunity> opportunityFromMeList = this.listByFromMemberIdAndToMemberIdAndIsPaid(userMemberId, tgtMemberId, OpportunityConstant.IS_PAID_YES);
        if (ListUtil.isNotEmpty(opportunityFromMeList)) {
            for (UnionOpportunity opportunity : opportunityFromMeList) {
                Map<String, Object> contactMap = new HashMap<>();
                contactMap.put("lastModifyTime", DateUtil.getDateString(opportunity.getModifytime(), DateUtil.DATETIME_PATTERN)); //最后修改时间
                contactMap.put("clientName", opportunity.getClientName()); //客户姓名
                contactMap.put("clientPhone", opportunity.getClientPhone()); //客户电话
                contactMap.put("brokeragePrice", opportunity.getBrokeragePrice()); //佣金收入：正数
                contactList.add(contactMap);
                contactMoneySum = BigDecimalUtil.add(contactMoneySum, opportunity.getBrokeragePrice()); //统计佣金往来总额
            }
        }
        //(4-2)我接受的商机
        List<UnionOpportunity> opportunityToMeList = this.listByFromMemberIdAndToMemberIdAndIsPaid(tgtMemberId, userMemberId, OpportunityConstant.IS_PAID_YES);
        if (ListUtil.isNotEmpty(opportunityToMeList)) {
            for (UnionOpportunity opportunity : opportunityToMeList) {
                Map<String, Object> contactMap = new HashMap<>();
                contactMap.put("lastModifyTime", DateUtil.getDateString(opportunity.getModifytime(), DateUtil.DATETIME_PATTERN)); //最后修改时间
                contactMap.put("clientName", opportunity.getClientName()); //客户姓名
                contactMap.put("clientPhone", opportunity.getClientPhone()); //客户电话
                contactMap.put("brokeragePrice", BigDecimalUtil.subtract(0D, opportunity.getBrokeragePrice()).doubleValue()); //佣金支出：负数
                contactList.add(contactMap);
                contactMoneySum = BigDecimalUtil.subtract(contactMoneySum, opportunity.getBrokeragePrice()); //统计佣金往来总额
            }
        }
        result.put("contactList", contactList);
        result.put("contactMoneySum", contactMoneySum);
        return result;
    }

    @Override
    public Map<String, Object> getStatisticsByBusIdAndMemberId(Integer busId, Integer memberId) throws Exception {
        if (busId == null || memberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        //(1)判断是否具有盟员权限
        UnionMember member = this.unionMemberService.getByIdAndBusId(memberId, busId);
        if (member == null) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_INVALID);
        }
        //(2)检查联盟有效期
        this.unionMainService.checkUnionValid(member.getUnionId());
        //(3)判断是否具有读权限
        if (!this.unionMemberService.hasReadAuthority(member)) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_READ_REJECT);
        }
        Map<String, Object> resultMap = new HashMap<>();
        //未结算佣金收入，即已被接受但未支付的推荐佣金收入
        Double unPaidBrokerageIncome = this.sumBrokeragePriceByFromMemberIdAndIsPaid(memberId, OpportunityConstant.IS_PAID_NO);
        resultMap.put("unPaidBrokerageIncome", unPaidBrokerageIncome);
        //已结算佣金收入，即已被接受且已支付的推荐佣金收入
        Double paidBrokerageIncome = this.sumBrokeragePriceByFromMemberIdAndIsPaid(memberId, OpportunityConstant.IS_PAID_YES);
        resultMap.put("paidBrokerageIncome", paidBrokerageIncome);
        //总佣金收入
        BigDecimal brokerageIncomeSum = BigDecimalUtil.add(unPaidBrokerageIncome, paidBrokerageIncome);
        resultMap.put("brokerageIncomeSum", brokerageIncomeSum);
        //收入百分比
        if (brokerageIncomeSum.doubleValue() != 0.0D) {
            //未结算佣金收入占总佣金收入的百分比
            BigDecimal bdUnPaidBrokerageIncomePercent = BigDecimalUtil.divide(unPaidBrokerageIncome, brokerageIncomeSum, 4);
            String unPaidBrokerageIncomePercent = DoubleUtil.formatPercent(bdUnPaidBrokerageIncomePercent.doubleValue());
            resultMap.put("unPaidBrokerageIncomePercent", unPaidBrokerageIncomePercent);
            //已结算佣金收入占总佣金收入的百分比
            BigDecimal bdPaidBrokerageIncomePercent = BigDecimalUtil.subtract(Double.valueOf(1.00), bdUnPaidBrokerageIncomePercent, 4);
            String paidBrokerageIncomePercent = DoubleUtil.formatPercent(bdPaidBrokerageIncomePercent.doubleValue());
            resultMap.put("paidBrokerageIncomePercent", paidBrokerageIncomePercent);
        }

        //未结算支出佣金，即已接受但未支付的商机推荐佣金支出
        Double unPaidBrokerageExpense = this.sumBrokeragePriceByToMemberIdAndIsPaid(memberId, OpportunityConstant.IS_PAID_NO);
        resultMap.put("unPaidBrokerageExpense", unPaidBrokerageExpense);
        //已结算支出佣金，即已接受且已支付的商机推荐佣金支出
        Double paidBrokerageExpense = this.sumBrokeragePriceByToMemberIdAndIsPaid(memberId, OpportunityConstant.IS_PAID_YES);
        resultMap.put("paidBrokerageExpense", paidBrokerageExpense);
        //总支出佣金
        BigDecimal brokerageExpenseSum = BigDecimalUtil.add(unPaidBrokerageExpense, paidBrokerageExpense);
        resultMap.put("brokerageExpenseSum", brokerageExpenseSum);
        //支出百分比
        if (brokerageExpenseSum.doubleValue() != 0.0D) {
            //未结算支出佣金占总支出佣金的百分比
            BigDecimal bdUnPaidBrokerageExpensePercent = BigDecimalUtil.divide(unPaidBrokerageExpense, brokerageExpenseSum, 4);
            String unPaidBrokerageExpensePercent = DoubleUtil.formatPercent(bdUnPaidBrokerageExpensePercent.doubleValue());
            resultMap.put("unPaidBrokerageExpensePercent", unPaidBrokerageExpensePercent);
            //已结算支出佣金占总支出佣金的百分比
            BigDecimal bdPaidBrokerageExpensePercent = BigDecimalUtil.subtract(Double.valueOf(1.00), bdUnPaidBrokerageExpensePercent, 4);
            String paidBrokerageExpensePercent = DoubleUtil.formatPercent(bdPaidBrokerageExpensePercent.doubleValue());
            resultMap.put("paidBrokerageExpensePercent", paidBrokerageExpensePercent);
        }

        //获取一周统计数据
        List<Map<String, Map<String, Double>>> brokerageInWeek = new ArrayList<>();
        int mondayOffset = (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 2 + 7) % 7;//例如今天是星期四，则mondayOffSet=3
        Date mondayInThisWeek = DateUtil.addDays(DateUtil.getCurrentDate(), 0 - mondayOffset);
        Date tempDate;
        for (int i = 0; i <= mondayOffset; i++) { //获取周一到今天的统计数据
            tempDate = DateUtil.addDays(mondayInThisWeek, i);
            String day = DateUtil.getDateString(tempDate, DateUtil.DATE_PATTERN);
            String dayBegin = day + " 00:00:00";
            String dayEnd = day + " 23:59:59";
            Map<String, Double> brokerageInDayMap = getPaidBrokerageBetweenDay(memberId, dayBegin, dayEnd);
            String enDayInWeek = DateUtil.getEnDayInWeek(tempDate);
            Map<String, Map<String, Double>> dayMap = new HashMap<>();
            dayMap.put(enDayInWeek, brokerageInDayMap);
            brokerageInWeek.add(dayMap);
        }
        for (int j = mondayOffset + 1; j < 7; j++) { //补充今天到周日的统计数据
            tempDate = DateUtil.addDays(mondayInThisWeek, j);
            String day = DateUtil.getDateString(tempDate, DateUtil.DATE_PATTERN);
            String enDayInWeek = DateUtil.getEnDayInWeek(tempDate);
            Map<String, Double> brokerageInDayMap = new HashMap<>();
            brokerageInDayMap.put("paidBrokerageIncome", Double.valueOf(0D));
            brokerageInDayMap.put("paidBrokerageExpense", Double.valueOf(0D));
            Map<String, Map<String, Double>> dayMap = new HashMap<>();
            dayMap.put(enDayInWeek, brokerageInDayMap);
            brokerageInWeek.add(dayMap);
        }
        resultMap.put("brokerageInWeek", brokerageInWeek);
        return resultMap;
    }

    //根据盟员身份id、开始时间和结束时间，获取已支付的商机佣金收入和支出信息
    private Map<String, Double> getPaidBrokerageBetweenDay(Integer memberId, String strDateBegin, String strDateEnd) throws Exception {
        if (memberId != null || StringUtil.isNotEmpty(strDateBegin) || StringUtil.isNotEmpty(strDateEnd)) {
            Date beginDate = DateUtil.parseDate(strDateBegin);
            Date endDate = DateUtil.parseDate(strDateEnd);
            Map<String, Double> result = new HashMap<>();

            BigDecimal incomeBrokerageSum = BigDecimal.valueOf(0D);
            List<UnionOpportunity> opportunityFromMeList = this.listByFromMemberId(memberId);
            if (ListUtil.isNotEmpty(opportunityFromMeList)) {
                incomeBrokerageSum = sumBrokeragePriceWithFilter(opportunityFromMeList, beginDate, endDate);
            }
            result.put("paidBrokerageIncome", incomeBrokerageSum.doubleValue());

            BigDecimal expenseBrokerageSum = BigDecimal.valueOf(0D);
            List<UnionOpportunity> opportunityToMeList = this.listByToMemberId(memberId);
            if (ListUtil.isNotEmpty(opportunityToMeList)) {
                expenseBrokerageSum = sumBrokeragePriceWithFilter(opportunityToMeList, beginDate, endDate);
            }
            result.put("paidBrokerageExpense", expenseBrokerageSum.doubleValue());
            return result;
        }
        return null;
    }

    private BigDecimal sumBrokeragePriceWithFilter(List<UnionOpportunity> opportunityList, Date beginDate, Date endDate) {
        BigDecimal result = BigDecimal.valueOf(0D);
        if (ListUtil.isNotEmpty(opportunityList)) {
            for (UnionOpportunity opportunity : opportunityList) {
                if (!opportunity.getIsAccept().equals(OpportunityConstant.ACCEPT_YES)) {
                    continue;
                }
                UnionBrokerageIncome income = this.unionBrokerageIncomeService.getByUnionOpportunityId(opportunity.getId());
                if (income == null || income.getCreatetime().compareTo(beginDate) < 0 || income.getCreatetime().compareTo(endDate) > 0) {
                    continue;
                }
                result = BigDecimalUtil.add(result, opportunity.getBrokeragePrice());
            }
        }
        return result;
    }

    /*******************************************************************************************************************
     ****************************************** Domain Driven Design - list ********************************************
     ******************************************************************************************************************/

    @Override
    public Page pageFromMeMapByBusId(Page page, final Integer busId, final Integer unionId, final String isAccept
            , final String clientName, final String clientPhone) throws Exception {
        if (page == null || busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        Wrapper wrapper = new Wrapper() {
            @Override
            public String getSqlSegment() {
                StringBuilder sbSqlSegment = new StringBuilder(" o")
                        .append(" LEFT JOIN t_union_member m ON m.id = o.to_member_id")
                        .append(" LEFT JOIN t_union_main m2 ON m2.id = m.union_id")
                        .append(" WHERE o.del_status = ").append(CommonConstant.DEL_STATUS_NO)
                        .append("  AND exists(")
                        .append("    SELECT m3.id FROM t_union_member m3")
                        .append("    WHERE m3.del_status = ").append(CommonConstant.DEL_STATUS_NO)
                        .append("      AND m3.status != ").append(MemberConstant.STATUS_APPLY_IN)
                        .append("      AND m3.bus_id = ").append(busId)
                        .append("      AND m3.id = o.from_member_id")
                        .append("      )");
                if (unionId != null) {
                    sbSqlSegment.append(" AND m2.id = ").append(unionId);
                }
                if (StringUtil.isNotEmpty(isAccept)) {
                    sbSqlSegment.append(" AND o.is_accept IN (").append(isAccept).append(")");
                }
                if (StringUtil.isNotEmpty(clientName)) {
                    sbSqlSegment.append(" AND o.client_name LIKE '%").append(clientName).append("%'");
                }
                if (StringUtil.isNotEmpty(clientPhone)) {
                    sbSqlSegment.append(" AND o.client_phone LIKE '%").append(clientPhone).append("%'");
                }
                sbSqlSegment.append(" ORDER BY o.is_accept ASC, o.modifytime DESC");
                return sbSqlSegment.toString();
            }
        };
        StringBuilder sbSqlSelect = new StringBuilder(" o.id opportunityId") //推荐商机id
                .append(", o.client_name clientName") //客户姓名
                .append(", o.client_phone clientPhone") //客户电话
                .append(", o.business_msg businessMsg") //业务备注
                .append(", m.enterprise_name toEnterpriseName") //接受商机推荐的盟员名称
                .append(", m2.name unionName") //联盟名称
                .append(", o.is_accept isAccept"); //受理状态
        wrapper.setSqlSelect(sbSqlSelect.toString());
        return this.selectMapsPage(page, wrapper);
    }

    @Override
    public Page pageToMeMapByBusId(Page page, final Integer busId, final Integer unionId, final String isAccept
            , final String clientName, final String clientPhone) throws Exception {
        if (page == null || busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        Wrapper wrapper = new Wrapper() {
            @Override
            public String getSqlSegment() {
                StringBuilder sbSqlSegment = new StringBuilder(" o")
                        .append(" LEFT JOIN t_union_member m ON m.id = o.from_member_id")
                        .append(" LEFT JOIN t_union_main m2 ON m2.id = m.union_id")
                        .append(" WHERE o.del_status = ").append(CommonConstant.DEL_STATUS_NO)
                        .append("  AND exists(")
                        .append("    SELECT m3.id FROM t_union_member m3")
                        .append("    WHERE m3.del_status =  ").append(CommonConstant.DEL_STATUS_NO)
                        .append("      AND m3.status != ").append(MemberConstant.STATUS_APPLY_IN)
                        .append("      AND m3.bus_id =").append(busId)
                        .append("      AND m3.id = o.to_member_id")
                        .append("      )");
                if (unionId != null) {
                    sbSqlSegment.append(" AND m2.id = ").append(unionId);
                }
                if (StringUtil.isNotEmpty(isAccept)) {
                    sbSqlSegment.append(" AND o.is_accept IN (").append(isAccept).append(")");
                }
                if (StringUtil.isNotEmpty(clientName)) {
                    sbSqlSegment.append(" AND o.client_name LIKE '%").append(clientName).append("%'");
                }
                if (StringUtil.isNotEmpty(clientPhone)) {
                    sbSqlSegment.append(" AND o.client_phone LIKE '%").append(clientPhone).append("%'");
                }
                sbSqlSegment.append("ORDER BY o.is_accept ASC, o.modifytime DESC");
                return sbSqlSegment.toString();
            }
        };
        StringBuilder sbSqlSelect = new StringBuilder(" o.id opportunityId") //推荐商机id
                .append(", o.client_name clientName") //客户姓名
                .append(", o.client_phone clientPhone") //客户电话
                .append(", o.business_msg businessMsg") //业务备注
                .append(", m.enterprise_name fromEnterpriseName") //推荐商机的盟员名称
                .append(", m2.name unionName") //联盟名称
                .append(", o.is_accept isAccept"); //受理状态
        wrapper.setSqlSelect(sbSqlSelect.toString());
        return this.selectMapsPage(page, wrapper);
    }

    @Override
    public Page pageIncomeByBusId(Page page, final Integer busId, final Integer unionId, final Integer toMemberId
            , final Integer isClose, final String clientName, final String clientPhone) throws Exception {
        if (page == null || busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        Wrapper wrapper = new Wrapper() {
            @Override
            public String getSqlSegment() {
                StringBuilder sbSqlSegment = new StringBuilder(" o")
                        .append(" LEFT JOIN t_union_member m ON m.id = o.to_member_id")
                        .append(" LEFT JOIN t_union_main m2 ON m2.id = m.union_id")
                        .append(" LEFT JOIN t_union_brokerage_income bi ON bi.opportunity_id = o.id")
                        .append(" WHERE o.del_status = ").append(CommonConstant.DEL_STATUS_NO)
                        .append("  AND o.is_accept = ").append(OpportunityConstant.ACCEPT_YES)
                        .append("  AND exists(")
                        .append("    SELECT m3.id FROM t_union_member m3")
                        .append("    WHERE m3.del_status = ").append(CommonConstant.DEL_STATUS_NO)
                        .append("      AND m3.status != ").append(MemberConstant.STATUS_APPLY_IN)
                        .append("      AND m3.bus_id = ").append(busId)
                        .append("      AND m3.id = o.from_member_id")
                        .append("  )");
                if (unionId != null) {
                    sbSqlSegment.append(" AND m2.id = ").append(unionId);
                }
                if (toMemberId != null) {
                    sbSqlSegment.append(" AND m.id = ").append(toMemberId);
                }
                if (isClose != null) {
                    if (isClose.equals(CommonConstant.COMMON_YES)) {
                        sbSqlSegment.append(" AND bi.opportunity_id IS NOT NULL");
                    } else {
                        sbSqlSegment.append(" AND bi.opportunity_id IS NULL");
                    }
                }
                if (StringUtil.isNotEmpty(clientName)) {
                    sbSqlSegment.append(" AND o.client_name LIKE '%").append(clientName).append("%'");
                }
                if (StringUtil.isNotEmpty(clientPhone)) {
                    sbSqlSegment.append(" AND o.client_phone LIKE '%").append(clientPhone).append("%'");
                }
                sbSqlSegment.append(" ORDER BY bi.opportunity_id ASC");
                return sbSqlSegment.toString();
            }
        };
        StringBuilder sbSqlSelect = new StringBuilder(" o.id opportunityId") //推荐商机id
                .append(", o.client_name clientName") //客户姓名
                .append(", o.client_phone clientPhone") //客户电话
                .append(", o.business_msg businessMsg") //业务被指
                .append(", m.enterprise_name toEnterpriseName") //商机接受方盟员名称
                .append(", m2.name unionName") //联盟名称
                .append(", o.accept_price acceptPrice") //受理金额
                .append(", o.brokerage_price brokeragePrice") //佣金金额
                .append(", o.type opportunityType") //商机推荐类型
                .append(", CASE IFNULL(bi.opportunity_id, 0) WHEN 0 THEN ").append(CommonConstant.COMMON_NO)
                .append(" ELSE ").append(CommonConstant.COMMON_YES).append(" END isClose") //是否已结算
                .append(", o.modifytime lastModifyTime"); //最后更新的时间
        wrapper.setSqlSelect(sbSqlSelect.toString());
        return this.selectMapsPage(page, wrapper);
    }

    @Override
    public Page pageExpenseByBusId(Page page, final Integer busId, final Integer unionId, final Integer fromMemberId
            , final Integer isClose, final String clientName, final String clientPhone) throws Exception {
        if (page == null || busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        Wrapper wrapper = new Wrapper() {
            @Override
            public String getSqlSegment() {
                StringBuilder sbSqlSegment = new StringBuilder(" o")
                        .append(" LEFT JOIN t_union_member m ON m.id = o.from_member_id")
                        .append(" LEFT JOIN t_union_main m2 ON m2.id = m.union_id")
                        .append(" LEFT JOIN t_union_brokerage_income bi ON bi.opportunity_id = o.id")
                        .append(" WHERE o.del_status = ").append(CommonConstant.DEL_STATUS_NO)
                        .append("  AND o.is_accept = ").append(OpportunityConstant.ACCEPT_YES)
                        .append("  AND exists(")
                        .append("    SELECT m3.id FROM t_union_member m3")
                        .append("    WHERE m3.del_status = ").append(CommonConstant.DEL_STATUS_NO)
                        .append("      AND m3.status != ").append(MemberConstant.STATUS_APPLY_IN)
                        .append("      AND m3.bus_id = ").append(busId)
                        .append("      AND m3.id = o.to_member_id")
                        .append("  )");
                if (unionId != null) {
                    sbSqlSegment.append(" AND m2.id = ").append(unionId);
                }
                if (fromMemberId != null) {
                    sbSqlSegment.append(" AND m.id = ").append(fromMemberId);
                }
                if (isClose != null) {
                    if (isClose.equals(CommonConstant.COMMON_YES)) {
                        sbSqlSegment.append(" AND bi.opportunity_id IS NOT NULL");
                    } else {
                        sbSqlSegment.append(" AND bi.opportunity_id IS NULL");
                    }
                }
                if (StringUtil.isNotEmpty(clientName)) {
                    sbSqlSegment.append(" AND o.client_name LIKE '%").append(clientName).append("%'");
                }
                if (StringUtil.isNotEmpty(clientPhone)) {
                    sbSqlSegment.append(" AND o.client_phone LIKE '%").append(clientPhone).append("%'");
                }
                sbSqlSegment.append(" ORDER BY bi.opportunity_id ASC");
                return sbSqlSegment.toString();
            }
        };
        StringBuilder sbSqlSelect = new StringBuilder(" o.id opportunityId") //推荐商机id
                .append(", o.client_name clientName") //客户姓名
                .append(", o.client_phone clientPhone") //客户电话
                .append(", o.business_msg businessMsg") //业务被指
                .append(", m.enterprise_name fromEnterpriseName") //商机接受方盟员名称
                .append(", m2.name unionName") //联盟名称
                .append(", o.accept_price acceptPrice") //受理金额
                .append(", o.brokerage_price brokeragePrice") //佣金金额
                .append(", o.type opportunityType") //商机推荐类型
                .append(", CASE IFNULL(bi.opportunity_id, 0) WHEN 0 THEN ").append(CommonConstant.COMMON_NO)
                .append(" ELSE ").append(CommonConstant.COMMON_YES).append(" END isClose") //是否已结算
                .append(", o.modifytime lastModifyTime"); //最后更新的时间
        wrapper.setSqlSelect(sbSqlSelect.toString());
        return this.selectMapsPage(page, wrapper);
    }

    @Override
    public Page pageContactByBusId(Page page, Integer busId, Integer userMemberId) throws Exception {
        if (page == null || busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        //(1)获取所有与商家有过商机推荐支付往来的盟员身份id列表
        Set<Integer> tgtMemberIdSet = getContactMemberIdSet(busId, userMemberId);
        //(2)根据目标盟员身份id列表，分页获取目标盟员身份列表信息
        Page result = this.unionMemberService.pageByIds(page, ListUtil.toList(tgtMemberIdSet));
        //(3)根据分页后的目标盟员列表信息，统计已支付的商机推荐佣金往来信息
        List<UnionMember> tgtMemberList = result.getRecords();
        if (ListUtil.isNotEmpty(tgtMemberList)) {
            List<Map<String, Object>> records = getContactMapList(busId, userMemberId, tgtMemberList);
            result.setRecords(records);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> listContactByBusId(Integer busId, Integer userMemberId) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<Map<String, Object>> result = new ArrayList<>();
        //(1)获取所有与商家有过商机推荐支付往来的盟员身份id列表
        Set<Integer> tgtMemberIdSet = getContactMemberIdSet(busId, userMemberId);
        //(2)根据目标盟员身份id列表，获取所有目标盟员身份列表信息
        List<UnionMember> tgtMemberList = this.unionMemberService.listByIds(ListUtil.toList(tgtMemberIdSet));
        if (ListUtil.isNotEmpty(tgtMemberList)) {
            //(3)获取往来信息
            result = getContactMapList(busId, userMemberId, tgtMemberList);
        }
        return result;
    }

    //私有方法：获取商家的与其他盟员有佣金往来的盟员身份id列表
    private Set<Integer> getContactMemberIdSet(Integer busId, Integer userMemberId) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<Integer> userMemberIdList = new ArrayList<>();
        if (userMemberId != null) {
            //(1-1)判断是否具有盟员权限
            UnionMember userMember = this.unionMemberService.getByIdAndBusId(userMemberId, busId);
            if (userMember == null) {
                throw new BusinessException(CommonConstant.UNION_MEMBER_INVALID);
            }
            //(1-2)检查联盟有效期
            this.unionMainService.checkUnionValid(userMember.getUnionId());
            //(1-3)判断是否具有读权限
            if (!this.unionMemberService.hasReadAuthority(userMember)) {
                throw new BusinessException(CommonConstant.UNION_MEMBER_READ_REJECT);
            }
            //(1-4)添加盟员身份id
            userMemberIdList.add(userMemberId);
        } else {
            //(1-1)获取商家具有读权限的盟员身份列表
            List<UnionMember> readMemberList = this.unionMemberService.listReadByBusId(busId);
            if (ListUtil.isNotEmpty(readMemberList)) {
                for (UnionMember readMember : readMemberList) {
                    //(1-2)添加盟员身份id
                    userMemberIdList.add(readMember.getId());
                }
            }
        }
        //(2)获取所有与商家有过商机推荐支付往来的盟员身份id列表
        Set<Integer> tgtMemberIdSet = new HashSet<>();
        if (ListUtil.isNotEmpty(userMemberIdList)) {
            for (Integer tempUserMemberId : userMemberIdList) {
                //(4-1)我推荐的已支付的商机列表信息
                List<UnionOpportunity> opportunityFromMeList = this.listByFromMemberIdAndIsPaid(tempUserMemberId, OpportunityConstant.IS_PAID_YES);
                if (ListUtil.isNotEmpty(opportunityFromMeList)) {
                    for (UnionOpportunity opportunityFromMe : opportunityFromMeList) {
                        tgtMemberIdSet.add(opportunityFromMe.getToMemberId());
                    }
                }
                //(4-2)我接受的已支付的商机列表信息
                List<UnionOpportunity> opportunityToMeList = this.listByToMemberIdAndIsPaid(tempUserMemberId, OpportunityConstant.IS_PAID_YES);
                if (ListUtil.isNotEmpty(opportunityToMeList)) {
                    for (UnionOpportunity opportunityToMe : opportunityToMeList) {
                        tgtMemberIdSet.add(opportunityToMe.getFromMemberId());
                    }
                }
            }
        }
        return tgtMemberIdSet;
    }

    //私有方法：获取商家的与其他盟员的佣金往来统计列表信息
    private List<Map<String, Object>> getContactMapList(Integer busId, Integer userMemberId, List<UnionMember> tgtMemberList) throws Exception {
        List<Map<String, Object>> resultList = new ArrayList<>();
        if (ListUtil.isNotEmpty(tgtMemberList)) {
            for (UnionMember tgtMember : tgtMemberList) {
                Map<String, Object> map = new HashMap<>();
                map.put("tgtMemberId", tgtMember.getId()); //目标盟员id
                map.put("tgtMemberEnterpriseName", tgtMember.getEnterpriseName()); //目标盟员名称
                UnionMain tgtUnion = this.unionMainService.getById(tgtMember.getUnionId());
                map.put("tgtUnionName", tgtUnion != null ? tgtUnion.getName() : ""); //目标盟员所在联盟名称
                Double brokerageIncome;
                if (userMemberId != null) {
                    brokerageIncome = this.sumBrokeragePriceByFromMemberIdAndToMemberIdAndIsPaid(userMemberId, tgtMember.getId(), OpportunityConstant.IS_PAID_YES);
                } else {
                    brokerageIncome = this.sumBrokeragePriceByBusIdAndToMemberIdAndIsPaid(busId, tgtMember.getId(), OpportunityConstant.IS_PAID_YES);
                }
                Double brokerageExpense;
                if (userMemberId != null) {
                    brokerageExpense = this.sumBrokeragePriceByFromMemberIdAndToMemberIdAndIsPaid(tgtMember.getId(), userMemberId, OpportunityConstant.IS_PAID_YES);
                } else {
                    brokerageExpense = this.sumBrokeragePriceByBusIdAndFromMemberIdAndIsPaid(busId, tgtMember.getId(), OpportunityConstant.IS_PAID_YES);
                }
                BigDecimal contactMoney = BigDecimalUtil.subtract(brokerageIncome, brokerageExpense);
                map.put("contactMoney", contactMoney.doubleValue()); //往来金额
                resultList.add(map);
            }
        }
        return resultList;
    }

    @Override
    public List<UnionOpportunity> listByFromMemberIdAndIsPaid(Integer fromMemberId, Integer isPaid) throws Exception {
        if (fromMemberId == null || isPaid == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionOpportunity> result = new ArrayList<>();
        List<UnionOpportunity> opportunityList = this.listByFromMemberId(fromMemberId);
        if (ListUtil.isNotEmpty(opportunityList)) {
            result = filterWithIsPaid(opportunityList, isPaid);
        }
        return result;
    }

    @Override
    public List<UnionOpportunity> listByBusIdAndFromMemberIdAndIsPaid(Integer busId, Integer fromMemberId, Integer isPaid) throws Exception {
        if (busId == null || fromMemberId == null || isPaid == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionOpportunity> result = new ArrayList<>();
        List<UnionMember> readMemberList = this.unionMemberService.listReadByBusId(busId);
        if (ListUtil.isNotEmpty(readMemberList)) {
            Set<Integer> toMemberIdSet = new HashSet<>();
            for (UnionMember readMember : readMemberList) {
                toMemberIdSet.add(readMember.getId());
            }
            List<UnionOpportunity> opportunityList = this.listByFromMemberIdAndIsPaid(fromMemberId, isPaid);
            if (ListUtil.isNotEmpty(opportunityList)) {
                for (UnionOpportunity opportunity : opportunityList) {
                    if (toMemberIdSet.contains(opportunity.getToMemberId())) {
                        result.add(opportunity);
                    }
                }
            }
        }
        return result;
    }

    @Override
    public List<UnionOpportunity> listByToMemberIdAndIsPaid(Integer toMemberId, Integer isPaid) throws Exception {
        if (toMemberId == null || isPaid == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionOpportunity> result = new ArrayList<>();
        List<UnionOpportunity> opportunityList = this.listByToMemberId(toMemberId);
        if (ListUtil.isNotEmpty(opportunityList)) {
            result = filterWithIsPaid(opportunityList, isPaid);
        }
        return result;
    }

    private List<UnionOpportunity> filterWithIsPaid(List<UnionOpportunity> opportunityList, Integer isPaid) {
        List<UnionOpportunity> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(opportunityList) || isPaid != null) {
            for (UnionOpportunity opportunity : opportunityList) {
                if (!opportunity.getIsAccept().equals(OpportunityConstant.ACCEPT_YES)) {
                    continue;
                }
                UnionBrokerageIncome income = this.unionBrokerageIncomeService.getByUnionOpportunityId(opportunity.getId());
                switch (isPaid) {
                    case OpportunityConstant.IS_PAID_YES:
                        if (income != null) {
                            result.add(opportunity);
                        }
                        break;
                    case OpportunityConstant.IS_PAID_NO:
                        if (income == null) {
                            result.add(opportunity);
                        }
                        break;
                    default:
                        break;
                }
            }
        }
        return result;
    }

    @Override
    public List<UnionOpportunity> listByBusIdAndToMemberIdAndIsPaid(Integer busId, Integer toMemberId, Integer isPaid) throws Exception {
        if (busId == null || toMemberId == null || isPaid == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionOpportunity> result = new ArrayList<>();
        List<UnionMember> readMemberList = this.unionMemberService.listReadByBusId(busId);
        if (ListUtil.isNotEmpty(readMemberList)) {
            Set<Integer> fromMemberIdSet = new HashSet<>();
            for (UnionMember readMember : readMemberList) {
                fromMemberIdSet.add(readMember.getId());
            }
            List<UnionOpportunity> opportunityList = this.listByToMemberIdAndIsPaid(toMemberId, isPaid);
            if (ListUtil.isNotEmpty(opportunityList)) {
                for (UnionOpportunity opportunity : opportunityList) {
                    if (fromMemberIdSet.contains(opportunity.getFromMemberId())) {
                        result.add(opportunity);
                    }
                }
            }
        }
        return result;
    }

    @Override
    public List<UnionOpportunity> listByFromMemberIdAndToMemberIdAndIsPaid(Integer fromMemberId, Integer toMemberId, Integer isPaid) throws Exception {
        if (fromMemberId == null || toMemberId == null || isPaid == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionOpportunity> result = new ArrayList<>();
        List<UnionOpportunity> opportunityList = this.listByFromMemberIdAndIsPaid(fromMemberId, isPaid);
        if (ListUtil.isNotEmpty(opportunityList)) {
            for (UnionOpportunity opportunity : opportunityList) {
                if (toMemberId.equals(opportunity.getToMemberId())) {
                    result.add(opportunity);
                }
            }
        }
        return result;
    }

    /*******************************************************************************************************************
     ****************************************** Domain Driven Design - save ********************************************
     ******************************************************************************************************************/

    @Override
    @Transactional
    public void saveByBusIdAndMemberId(Integer busId, Integer memberId, UnionOpportunityVO vo) throws Exception {
        if (busId == null || memberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        //(1)判断是否具有盟员权限
        UnionMember fromMember = this.unionMemberService.getByIdAndBusId(memberId, busId);
        if (fromMember == null) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_INVALID);
        }
        //(2)检查联盟有效期
        this.unionMainService.checkUnionValid(fromMember.getUnionId());
        //(3)判断是否具有写权限
        if (!this.unionMemberService.hasWriteAuthority(fromMember)) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_WRITE_REJECT);
        }
        //(4)判断接收商家状态
        UnionMember toMember = this.unionMemberService.getById(vo.getToMemberId());
        if (toMember == null) {
            throw new BusinessException("您推荐的盟员不存在或已过期");
        }
        if (!fromMember.getUnionId().equals(toMember.getUnionId())) {
            throw new BusinessException("无法向其他联盟的人推荐商机");
        }
        if (!this.unionMemberService.hasWriteAuthority(toMember)) {
            throw new BusinessException("您推荐的盟员不是正式盟员或正处在退盟过渡期");
        }
        //(5)商家保存信息
        UnionOpportunity saveOpportunity = new UnionOpportunity();
        saveOpportunity.setCreatetime(DateUtil.getCurrentDate()); //创建时间
        saveOpportunity.setDelStatus(CommonConstant.DEL_STATUS_NO); //删除状态
        saveOpportunity.setType(OpportunityConstant.TYPE_OFFLINE); //推荐类型：线下
        saveOpportunity.setFromMemberId(memberId); //推荐盟员身份id
        saveOpportunity.setToMemberId(vo.getToMemberId()); //接收盟员身份id
        saveOpportunity.setIsAccept(OpportunityConstant.ACCEPT_NON); //是否受理
        saveOpportunity.setClientName(vo.getClientName()); //客户姓名
        saveOpportunity.setClientPhone(vo.getClientPhone()); //客户电话
        saveOpportunity.setBusinessMsg(vo.getBusinessMsg()); //业务备注
        //(6)短信通知信息
        UnionMain unionMain = this.unionMainService.getById(fromMember.getUnionId());
        String phone = StringUtil.isNotEmpty(toMember.getNotifyPhone()) ? toMember.getNotifyPhone() : toMember.getDirectorPhone();
        StringBuilder sbContent = new StringBuilder("\"").append(unionMain.getName()).append("\"的盟员\"")
                .append(fromMember.getEnterpriseName()).append("\"为你推荐了客户，请到商机消息处查看。客户信息：")
                .append(vo.getClientName()).append("，").append(vo.getClientPhone()).append("，").append(vo.getBusinessMsg());
        PhoneMessage phoneMessage = new PhoneMessage(busId, phone, sbContent.toString());
        //(7)执行保存操作
        this.save(saveOpportunity);
        this.phoneMessageSender.sendMsg(phoneMessage);
    }

    /*******************************************************************************************************************
     ****************************************** Domain Driven Design - remove ******************************************
     ******************************************************************************************************************/

    /*******************************************************************************************************************
     ****************************************** Domain Driven Design - update ******************************************
     ******************************************************************************************************************/

    @Override
    @Transactional
    public void updateAcceptYesByIdAndBusId(Integer opportunityId, Integer busId, Double acceptPrice) throws Exception {
        if (opportunityId == null || busId == null || acceptPrice == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        //(1)检查推荐商机的状态
        UnionOpportunity opportunity = this.getById(opportunityId);
        if (opportunity == null) {
            throw new BusinessException("推荐商机不存在或已处理");
        }
        if (!opportunity.getIsAccept().equals(OpportunityConstant.ACCEPT_NON)) {
            throw new BusinessException("推荐商机已处理");
        }
        //(2)判断是否是商机推荐的接受者
        UnionMember toMember = this.unionMemberService.getByIdAndBusId(opportunity.getToMemberId(), busId);
        if (toMember == null) {
            throw new BusinessException("不是该推荐商机的接受者");
        }
        //(3)检查联盟有效期
        this.unionMainService.checkUnionValid(toMember.getUnionId());
        //(4)判断是否具有写权限
        if (!this.unionMemberService.hasWriteAuthority(toMember)) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_WRITE_REJECT);
        }
        //(5)判断是否设置了商机佣金比例
        UnionOpportunityRatio ratio = this.unionOpportunityBrokerageRatioService.getByFromMemberIdAndToMemberId(
                opportunity.getFromMemberId(), opportunity.getToMemberId());
        if (ratio == null) {
            throw new BusinessException("您还没设置给推荐方的商机佣金比例，请设置后接受商机");
        }
        //(6)更新商机推荐信息
        UnionOpportunity updateOpportunity = new UnionOpportunity();
        updateOpportunity.setId(opportunityId); //推荐商机id
        updateOpportunity.setIsAccept(OpportunityConstant.ACCEPT_YES); //受理状态
        updateOpportunity.setAcceptPrice(acceptPrice); //受理金额
        updateOpportunity.setBrokeragePrice(BigDecimalUtil.multiply(acceptPrice, BigDecimalUtil.divide(ratio.getRatio(), 100D)).doubleValue()); //佣金金额
        updateOpportunity.setModifytime(DateUtil.getCurrentDate()); //修改时间
        //(7)短信通知信息
        UnionMain unionMain = this.unionMainService.getById(toMember.getUnionId());
        UnionMember fromMember = this.unionMemberService.getById(opportunity.getFromMemberId());
        String phone = StringUtil.isNotEmpty(fromMember.getNotifyPhone()) ? fromMember.getNotifyPhone() : fromMember.getDirectorPhone();
        StringBuilder sbContent = new StringBuilder("\"").append(unionMain.getName()).append("\"的盟员\"")
                .append(toMember.getEnterpriseName()).append("\"已接受了您推荐的商机消息。客户信息：")
                .append(opportunity.getClientName()).append("，")
                .append(opportunity.getClientPhone()).append("，")
                .append(opportunity.getBusinessMsg());
        PhoneMessage phoneMessage = new PhoneMessage(busId, phone, sbContent.toString());
        //(8)执行保存操作
        this.update(updateOpportunity);
        this.phoneMessageSender.sendMsg(phoneMessage);
    }

    @Override
    @Transactional
    public void updateAcceptNoByIdAndBusId(Integer opportunityId, Integer busId) throws Exception {
        if (opportunityId == null || busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        //(1)检查推荐商机的状态
        UnionOpportunity opportunity = this.getById(opportunityId);
        if (opportunity == null) {
            throw new BusinessException("推荐商机不存在或已处理");
        }
        if (!opportunity.getIsAccept().equals(OpportunityConstant.ACCEPT_NON)) {
            throw new BusinessException("推荐商机已处理");
        }
        //(2)判断是否是商机推荐的接受者
        UnionMember toMember = this.unionMemberService.getByIdAndBusId(opportunity.getToMemberId(), busId);
        if (toMember == null) {
            throw new BusinessException("不是该推荐商机的接受者");
        }
        //(3)检查联盟有效期
        this.unionMainService.checkUnionValid(toMember.getUnionId());
        //(4)判断是否具有读权限
        if (!this.unionMemberService.hasReadAuthority(toMember)) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_READ_REJECT);
        }
        //(6)更新商机推荐信息
        UnionOpportunity updateOpportunity = new UnionOpportunity();
        updateOpportunity.setId(opportunityId); //推荐商机id
        updateOpportunity.setIsAccept(OpportunityConstant.ACCEPT_NO); //受理状态
        updateOpportunity.setModifytime(DateUtil.getCurrentDate()); //修改时间
        //(7)短信通知信息
        UnionMain unionMain = this.unionMainService.getById(toMember.getUnionId());
        UnionMember fromMember = this.unionMemberService.getById(opportunity.getFromMemberId());
        String phone = StringUtil.isNotEmpty(fromMember.getNotifyPhone()) ? fromMember.getNotifyPhone() : fromMember.getDirectorPhone();
        StringBuilder sbContent = new StringBuilder("\"").append(unionMain.getName()).append("\"的盟员\"")
                .append(toMember.getEnterpriseName()).append("\"已拒绝了您推荐的商机消息。客户信息：")
                .append(opportunity.getClientName()).append("，")
                .append(opportunity.getClientPhone()).append("，")
                .append(opportunity.getBusinessMsg());
        PhoneMessage phoneMessage = new PhoneMessage(busId, phone, sbContent.toString());
        //(8)执行保存操作
        this.update(updateOpportunity);
        this.phoneMessageSender.sendMsg(phoneMessage);
    }

    /*******************************************************************************************************************
     ****************************************** Domain Driven Design - count *******************************************
     ******************************************************************************************************************/

    @Override
    public Double sumBrokeragePriceByBusIdAndToMemberIdAndIsPaid(Integer busId, Integer toMemberId, Integer isPaid) throws Exception {
        if (busId == null || toMemberId == null || isPaid == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        BigDecimal result = BigDecimal.valueOf(0D);
        List<UnionOpportunity> opportunityList = this.listByBusIdAndToMemberIdAndIsPaid(busId, toMemberId, isPaid);
        if (ListUtil.isNotEmpty(opportunityList)) {
            for (UnionOpportunity opportunity : opportunityList) {
                result = BigDecimalUtil.add(result, opportunity.getBrokeragePrice());
            }
        }
        return result.doubleValue();
    }

    @Override
    public Double sumBrokeragePriceByBusIdAndFromMemberIdAndIsPaid(Integer busId, Integer fromMemberId, Integer isPaid) throws Exception {
        if (busId == null || fromMemberId == null || isPaid == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        BigDecimal result = BigDecimal.valueOf(0D);
        List<UnionOpportunity> opportunityList = this.listByBusIdAndFromMemberIdAndIsPaid(busId, fromMemberId, isPaid);
        if (ListUtil.isNotEmpty(opportunityList)) {
            for (UnionOpportunity opportunity : opportunityList) {
                result = BigDecimalUtil.add(result, opportunity.getBrokeragePrice());
            }
        }
        return result.doubleValue();
    }

    @Override
    public Double sumBrokeragePriceByFromMemberIdAndToMemberIdAndIsPaid(Integer fromMemberId, Integer toMemberId, Integer isPaid) throws Exception {
        if (fromMemberId == null || toMemberId == null || isPaid == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        BigDecimal result = BigDecimal.valueOf(0D);
        List<UnionOpportunity> opportunityList = this.listByFromMemberIdAndToMemberIdAndIsPaid(fromMemberId, toMemberId, isPaid);
        if (ListUtil.isNotEmpty(opportunityList)) {
            for (UnionOpportunity opportunity : opportunityList) {
                result = BigDecimalUtil.add(result, opportunity.getBrokeragePrice());
            }
        }
        return result.doubleValue();
    }

    @Override
    public Double sumBrokeragePriceByFromMemberIdAndIsPaid(Integer fromMemberId, Integer isPaid) throws Exception {
        if (fromMemberId == null || isPaid == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        BigDecimal result = BigDecimal.valueOf(0D);
        List<UnionOpportunity> opportunityList = this.listByFromMemberIdAndIsPaid(fromMemberId, isPaid);
        if (ListUtil.isNotEmpty(opportunityList)) {
            for (UnionOpportunity opportunity : opportunityList) {
                result = BigDecimalUtil.add(result, opportunity.getBrokeragePrice());
            }
        }
        return result.doubleValue();
    }


    @Override
    public Double sumBrokeragePriceByToMemberIdAndIsPaid(Integer toMemberId, Integer isPaid) throws Exception {
        if (toMemberId == null || isPaid == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        BigDecimal result = BigDecimal.valueOf(0D);
        List<UnionOpportunity> opportunityList = this.listByToMemberIdAndIsPaid(toMemberId, isPaid);
        if (ListUtil.isNotEmpty(opportunityList)) {
            for (UnionOpportunity opportunity : opportunityList) {
                result = BigDecimalUtil.add(result, opportunity.getBrokeragePrice());
            }
        }
        return result.doubleValue();
    }

    /*******************************************************************************************************************
     ****************************************** Domain Driven Design - boolean *****************************************
     ******************************************************************************************************************/

    /*******************************************************************************************************************
     ****************************************** Domain Driven Design - other *******************************************
     ******************************************************************************************************************/

    @Override
    public Map<String, Object> payOpportunityQRCode(Integer busId, String ids) throws Exception {
        Map<String, Object> data = new HashMap<String, Object>();
        if (StringUtil.isEmpty(ids)) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        String[] arrs = ids.split(",");
        if (arrs.length == 0) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        EntityWrapper wrapper = new EntityWrapper<>();
        wrapper.eq("del_status", CommonConstant.DEL_STATUS_NO);
        wrapper.in("id", arrs);
        List<UnionOpportunity> list = this.selectList(wrapper);
        if (ListUtil.isEmpty(list)) {
            throw new BusinessException(CommonConstant.PARAM_ERROR);
        }
        double totalFee = 0;
        WxPublicUsers publicUser = busUserService.getWxPublicUserByBusId(ConfigConstant.WXMP_DUOFEN_BUSID);
        for (UnionOpportunity opportunity : list) {
            if (CommonUtil.isEmpty(opportunity.getIsAccept()) || opportunity.getIsAccept() != OpportunityConstant.ACCEPT_YES) {
                throw new BusinessException("不可支付未接受的商机");
            }
            UnionBrokerageIncome brokerageIncome = unionBrokerageIncomeService.getByUnionOpportunityId(opportunity.getId());
            if (brokerageIncome != null) {
                throw new BusinessException("该商机已支付");
            }
            totalFee = new BigDecimal(opportunity.getBrokeragePrice()).add(new BigDecimal(totalFee)).doubleValue();
        }
        if (totalFee <= 0) {
            throw new BusinessException("支付金额有误");
        }
        String orderNo = OpportunityConstant.ORDER_PREFIX + System.currentTimeMillis();
        String only = DateTimeKit.getDateTime(new Date(), DateTimeKit.yyyyMMddHHmmss);
        data.put("totalFee", totalFee);
        data.put("busId", ConfigConstant.WXMP_DUOFEN_BUSID);
        data.put("sourceType", 1);//是否墨盒支付
        data.put("payWay", 1);//系统判断支付方式
        data.put("isreturn", 0);//0：不需要同步跳转
        data.put("model", ConfigConstant.PAY_MODEL);
        data.put("notifyUrl", ConfigConstant.UNION_ROOT_URL + "/unionOpportunity/79B4DE7C/paymentSuccess/" + only);
        data.put("orderNum", orderNo);//订单号
        data.put("payBusId", busId);//支付的商家id
        data.put("isSendMessage", 0);//不推送
        data.put("appid", publicUser.getAppid());//appid
        data.put("desc", "联盟商机佣金");
        data.put("appidType", 0);//公众号
        data.put("only", only);
        data.put("data", ids);
        String paramKey = RedisKeyUtil.getRecommendPayParamKey(only);
        String statusKey = RedisKeyUtil.getRecommendPayStatusKey(only);
        redisCacheUtil.set(paramKey, data, 360l);//5分钟
        redisCacheUtil.set(statusKey, ConfigConstant.USER_ORDER_STATUS_001, 300l);//等待扫码状态
        return data;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void payOpportunitySuccess(String orderNo, String only) throws Exception {
        //解密参数
        String paramKey = RedisKeyUtil.getRecommendPayParamKey(only);
        String obj = redisCacheUtil.get(paramKey);
        if (CommonUtil.isEmpty(obj)) {
            throw new BusinessException("支付失败");
        }
        Map<String, Object> result = JSONObject.parseObject(obj, Map.class);
        String statusKey = RedisKeyUtil.getRecommendPayStatusKey(only);
        String[] arrs = result.get("data").toString().split(",");
        List<Integer> ids = new ArrayList<Integer>();
        for (String id : arrs) {
            ids.add(Integer.valueOf(id));
        }
        int count = unionBrokerageIncomeService.countByOpportunityIds(ids);
        if (count > 0) {
            throw new BusinessException("商机已支付");
        }
        EntityWrapper wrapper = new EntityWrapper<>();
        wrapper.eq("del_status", CommonConstant.DEL_STATUS_NO);
        wrapper.in("id", ids.toArray());
        List<UnionOpportunity> list = this.selectList(wrapper);
        //插入佣金收入列表
        this.insertBatchByList(list, orderNo, null);
        redisCacheUtil.remove(paramKey);
        redisCacheUtil.set(statusKey, ConfigConstant.USER_ORDER_STATUS_003, 60l);//支付成功
    }

    /**
     * 插入佣金收入列表
     *
     * @param list
     */
    @Override
    public void insertBatchByList(List<UnionOpportunity> list, String orderNo, Integer verifierId) throws Exception {
        List<UnionBrokerageIncome> incomes = new ArrayList<UnionBrokerageIncome>();
        List<UnionBrokeragePay> pays = new ArrayList<UnionBrokeragePay>();

        for (UnionOpportunity opportunity : list) {
            UnionMember fromMember = unionMemberService.getById(opportunity.getFromMemberId());
            UnionMember toMember = unionMemberService.getById(opportunity.getToMemberId());
            UnionBrokerageIncome brokerageIncome = new UnionBrokerageIncome();
            brokerageIncome.setCreatetime(new Date());
            brokerageIncome.setDelStatus(CommonConstant.DEL_STATUS_NO);
            brokerageIncome.setBusId(fromMember.getBusId());
            brokerageIncome.setOpportunityId(opportunity.getId());
            brokerageIncome.setMoney(opportunity.getBrokeragePrice());
            brokerageIncome.setType(BrokerageConstant.TYPE_INCOME);
            incomes.add(brokerageIncome);


            UnionBrokeragePay pay = new UnionBrokeragePay();
            pay.setCreatetime(new Date());
            pay.setDelStatus(CommonConstant.DEL_STATUS_NO);
            pay.setFromBusId(toMember.getBusId());
            pay.setToBusId(fromMember.getBusId());
            pay.setMoney(opportunity.getBrokeragePrice());
            pay.setOrderNo(orderNo);
            pay.setOpportunityId(opportunity.getId());
            pay.setStatus(BrokerageConstant.BROKERAGE_PAY_STATUS_YES);
            pay.setType(BrokerageConstant.BROKERAGE_PAY_TYPE_WX);
            pay.setVerifierId(verifierId);
            pays.add(pay);
        }
        unionBrokerageIncomeService.insertBatch(incomes);
        unionBrokeragePayService.insertBatch(pays);
    }

    /*******************************************************************************************************************
     ****************************************** Object As a Service - get **********************************************
     ******************************************************************************************************************/

    @Override
    public UnionOpportunity getById(Integer opportunityId) throws Exception {
        if (opportunityId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionOpportunity result;
        //(1)cache
        String opportunityIdKey = RedisKeyUtil.getOpportunityIdKey(opportunityId);
        if (this.redisCacheUtil.exists(opportunityIdKey)) {
            String tempStr = this.redisCacheUtil.get(opportunityIdKey);
            result = JSONArray.parseObject(tempStr, UnionOpportunity.class);
            return result;
        }
        //(2)db
        EntityWrapper<UnionOpportunity> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("id", opportunityId);
        result = this.selectOne(entityWrapper);
        setCache(result, opportunityId);
        return result;
    }

    /*******************************************************************************************************************
     ****************************************** Object As a Service - list *********************************************
     ******************************************************************************************************************/

    @Override
    public List<UnionOpportunity> listByFromMemberId(Integer fromMemberId) throws Exception {
        if (fromMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionOpportunity> result;
        //(1)get in cache
        String fromMemberIdKey = RedisKeyUtil.getOpportunityFromMemberIdKey(fromMemberId);
        if (this.redisCacheUtil.exists(fromMemberIdKey)) {
            String tempStr = this.redisCacheUtil.get(fromMemberIdKey);
            result = JSONArray.parseArray(tempStr, UnionOpportunity.class);
            return result;
        }
        //(2)get in db
        EntityWrapper<UnionOpportunity> entityWrapper = new EntityWrapper();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("from_member_id", fromMemberId);
        result = this.selectList(entityWrapper);
        setCache(result, fromMemberId, OpportunityConstant.REDIS_KEY_OPPORTUNITY_FROM_MEMBER_ID);
        return result;
    }

    @Override
    public List<UnionOpportunity> listByToMemberId(Integer toMemberId) throws Exception {
        if (toMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionOpportunity> result;
        //(1)get in cache
        String toMemberIdKey = RedisKeyUtil.getOpportunityToMemberIdKey(toMemberId);
        if (this.redisCacheUtil.exists(toMemberIdKey)) {
            String tempStr = this.redisCacheUtil.get(toMemberIdKey);
            result = JSONArray.parseArray(tempStr, UnionOpportunity.class);
            return result;
        }
        //(2)get in db
        EntityWrapper<UnionOpportunity> entityWrapper = new EntityWrapper();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("to_member_id", toMemberId);
        result = this.selectList(entityWrapper);
        setCache(result, toMemberId, OpportunityConstant.REDIS_KEY_OPPORTUNITY_TO_MEMBER_ID);
        return result;
    }

    /*******************************************************************************************************************
     ****************************************** Object As a Service - save *********************************************
     ******************************************************************************************************************/

    @Override
    @Transactional
    public void save(UnionOpportunity newOpportunity) throws Exception {
        if (newOpportunity == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        this.insert(newOpportunity);
        this.removeCache(newOpportunity);
    }

    @Override
    @Transactional
    public void saveBatch(List<UnionOpportunity> newOpportunityList) throws Exception {
        if (newOpportunityList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        this.insertBatch(newOpportunityList);
        this.removeCache(newOpportunityList);
    }

    /*******************************************************************************************************************
     ****************************************** Object As a Service - remove *******************************************
     ******************************************************************************************************************/

    @Override
    @Transactional
    public void removeById(Integer opportunityId) throws Exception {
        if (opportunityId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        //(1)remove cache
        UnionOpportunity opportunity = this.getById(opportunityId);
        removeCache(opportunity);
        //(2)remove in db logically
        UnionOpportunity removeOpportunity = new UnionOpportunity();
        removeOpportunity.setId(opportunityId);
        removeOpportunity.setDelStatus(CommonConstant.DEL_STATUS_YES);
        this.updateById(removeOpportunity);
    }

    @Override
    @Transactional
    public void removeBatchById(List<Integer> opportunityIdList) throws Exception {
        if (opportunityIdList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        //(1)remove cache
        List<UnionOpportunity> opportunityList = new ArrayList<>();
        for (Integer opportunityId : opportunityIdList) {
            UnionOpportunity opportunity = this.getById(opportunityId);
            opportunityList.add(opportunity);
        }
        removeCache(opportunityList);
        //(2)remove in db logically
        List<UnionOpportunity> removeOpportunityList = new ArrayList<>();
        for (Integer opportunityId : opportunityIdList) {
            UnionOpportunity removeOpportunity = new UnionOpportunity();
            removeOpportunity.setId(opportunityId);
            removeOpportunity.setDelStatus(CommonConstant.DEL_STATUS_YES);
            removeOpportunityList.add(removeOpportunity);
        }
        this.updateBatchById(removeOpportunityList);
    }

    /*******************************************************************************************************************
     ****************************************** Object As a Service - update *******************************************
     ******************************************************************************************************************/

    @Override
    @Transactional
    public void update(UnionOpportunity updateOpportunity) throws Exception {
        if (updateOpportunity == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        //(1)remove cache
        Integer opportunityId = updateOpportunity.getId();
        UnionOpportunity opportunity = this.getById(opportunityId);
        removeCache(opportunity);
        //(2)update db
        this.updateById(updateOpportunity);
    }

    @Override
    @Transactional
    public void updateBatch(List<UnionOpportunity> updateOpportunityList) throws Exception {
        if (updateOpportunityList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        //(1)remove cache
        List<Integer> opportunityIdList = new ArrayList<>();
        for (UnionOpportunity updateOpportunity : updateOpportunityList) {
            opportunityIdList.add(updateOpportunity.getId());
        }
        List<UnionOpportunity> opportunityList = new ArrayList<>();
        for (Integer opportunityId : opportunityIdList) {
            UnionOpportunity opportunity = this.getById(opportunityId);
            opportunityList.add(opportunity);
        }
        removeCache(opportunityList);
        //(2)update db
        this.updateBatchById(updateOpportunityList);
    }

    /*******************************************************************************************************************
     ****************************************** Object As a Service - cache support ************************************
     ******************************************************************************************************************/

    private void setCache(UnionOpportunity newOpportunity, Integer opportunityId) {
        if (opportunityId == null) {
            return; //do nothing,just in case
        }
        String opportunityIdKey = RedisKeyUtil.getOpportunityIdKey(opportunityId);
        this.redisCacheUtil.set(opportunityIdKey, newOpportunity);
    }

    private void setCache(List<UnionOpportunity> newOpportunityList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            return; //do nothing,just in case
        }
        String foreignIdKey;
        switch (foreignIdType) {
            case OpportunityConstant.REDIS_KEY_OPPORTUNITY_FROM_MEMBER_ID:
                foreignIdKey = RedisKeyUtil.getOpportunityFromMemberIdKey(foreignId);
                this.redisCacheUtil.set(foreignIdKey, newOpportunityList);
                break;
            case OpportunityConstant.REDIS_KEY_OPPORTUNITY_TO_MEMBER_ID:
                foreignIdKey = RedisKeyUtil.getOpportunityToMemberIdKey(foreignId);
                this.redisCacheUtil.set(foreignIdKey, newOpportunityList);
                break;
            default:
                break;
        }
    }

    private void removeCache(UnionOpportunity opportunity) {
        if (opportunity == null) {
            return;
        }
        Integer opportunityId = opportunity.getId();
        String opportunityIdKey = RedisKeyUtil.getOpportunityIdKey(opportunityId);
        this.redisCacheUtil.remove(opportunityIdKey);
        Integer fromMemberId = opportunity.getFromMemberId();
        if (fromMemberId != null) {
            String fromMemberIdKey = RedisKeyUtil.getOpportunityFromMemberIdKey(fromMemberId);
            this.redisCacheUtil.remove(fromMemberIdKey);
        }
        Integer toMemberId = opportunity.getToMemberId();
        if (toMemberId != null) {
            String toMemberIdKey = RedisKeyUtil.getOpportunityToMemberIdKey(toMemberId);
            this.redisCacheUtil.remove(toMemberIdKey);
        }
    }

    private void removeCache(List<UnionOpportunity> opportunityList) {
        if (ListUtil.isEmpty(opportunityList)) {
            return;
        }
        List<Integer> opportunityIdList = new ArrayList<>();
        for (UnionOpportunity opportunity : opportunityList) {
            opportunityIdList.add(opportunity.getId());
        }
        List<String> opportunityIdKeyList = RedisKeyUtil.getOpportunityIdKey(opportunityIdList);
        this.redisCacheUtil.remove(opportunityIdKeyList);
        List<String> fromMemberIdKeyList = getForeignIdKeyList(opportunityList, OpportunityConstant.REDIS_KEY_OPPORTUNITY_FROM_MEMBER_ID);
        if (ListUtil.isNotEmpty(fromMemberIdKeyList)) {
            this.redisCacheUtil.remove(fromMemberIdKeyList);
        }
        List<String> toMemberIdKeyList = getForeignIdKeyList(opportunityList, OpportunityConstant.REDIS_KEY_OPPORTUNITY_TO_MEMBER_ID);
        if (ListUtil.isNotEmpty(toMemberIdKeyList)) {
            this.redisCacheUtil.remove(toMemberIdKeyList);
        }
    }

    private List<String> getForeignIdKeyList(List<UnionOpportunity> opportunityList, int foreignIdType) {
        List<String> result = new ArrayList<>();
        switch (foreignIdType) {
            case OpportunityConstant.REDIS_KEY_OPPORTUNITY_FROM_MEMBER_ID:
                for (UnionOpportunity opportunity : opportunityList) {
                    Integer fromMemberId = opportunity.getFromMemberId();
                    if (fromMemberId != null) {
                        String fromMemberIdKey = RedisKeyUtil.getOpportunityFromMemberIdKey(fromMemberId);
                        result.add(fromMemberIdKey);
                    }
                }
                break;
            case OpportunityConstant.REDIS_KEY_OPPORTUNITY_TO_MEMBER_ID:
                for (UnionOpportunity opportunity : opportunityList) {
                    Integer toMemberId = opportunity.getToMemberId();
                    if (toMemberId != null) {
                        String toMemberIdKey = RedisKeyUtil.getOpportunityToMemberIdKey(toMemberId);
                        result.add(toMemberIdKey);
                    }
                }
                break;
            default:
                break;
        }
        return result;
    }

}
