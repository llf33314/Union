package com.gt.union.opportunity.service.impl;

import com.alibaba.fastjson.JSON;
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
import com.gt.union.opportunity.entity.UnionOpportunityBrokerageRatio;
import com.gt.union.opportunity.mapper.UnionOpportunityMapper;
import com.gt.union.opportunity.service.IUnionOpportunityBrokerageRatioService;
import com.gt.union.opportunity.service.IUnionOpportunityService;
import com.gt.union.opportunity.vo.UnionOpportunityVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.net.URLEncoder;
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
    private RedisCacheUtil redisCacheUtil;

    @Autowired
    private IUnionMemberService unionMemberService;

    @Autowired
    private IUnionMainService unionMainService;

    @Autowired
    private PhoneMessageSender phoneMessageSender;

    @Autowired
    private IUnionOpportunityBrokerageRatioService unionOpportunityBrokerageRatioService;

    @Autowired
    private IBusUserService busUserService;

    @Autowired
    private IUnionBrokerageIncomeService unionBrokerageIncomeService;

    @Autowired
    private IUnionBrokeragePayService unionBrokeragePayService;

    @Value("${union.url}")
    private String unionUrl;

    @Value("${wx.duofen.busId}")
    private Integer duofenBusId;

    @Value("${union.encryptKey}")
    private String encryptKey;

    //-------------------------------------------------- get ----------------------------------------------------------

    /**
     * 根据商机推荐id，获取商机推荐信息
     *
     * @param opportunityId {not null} 商家推荐id
     * @return
     * @throws Exception
     */
    @Override
    public UnionOpportunity getById(Integer opportunityId) throws Exception {
        if (opportunityId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        EntityWrapper entityWrapper = new EntityWrapper();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("id", opportunityId);
        return this.selectOne(entityWrapper);
    }

    /**
     * 根据商家id和目标盟员身份id，获取所有商家与目标盟员之间的商机推荐支付往来列表记录
     *
     * @param busId        {not null} 商家id
     * @param tgtMemberId  {not null} 目标盟员身份id
     * @param userMemberId 可选项 商家的盟员身份id
     * @return
     * @throws Exception
     */
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
        if (userMemberId != null) {
            //(4-1)我推荐的商机
            List<UnionOpportunity> opportunityFromMeList = this.listPaidByFromMemberIdAndToMemberId(userMemberId, tgtMemberId);
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
            List<UnionOpportunity> opportunityToMeList = this.listPaidByFromMemberIdAndToMemberId(tgtMemberId, userMemberId);
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
        }
        result.put("contactList", contactList);
        result.put("contactMoneySum", contactMoneySum);
        return result;
    }

    /**
     * 根据商家id和盟员身份id，获取商机佣金统计数据
     *
     * @param busId    {not null} 商家id
     * @param memberId {not null} 盟员身份id
     * @return
     * @throws Exception
     */
    @Override
    public Map<String, Object> getStatisticsByBusIdAndMemberId(Integer busId, Integer memberId) throws Exception {
        if (busId == null || memberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        //(1)判断是否具有盟员权限
        UnionMember unionMember = this.unionMemberService.getByIdAndBusId(memberId, busId);
        if (unionMember == null) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_INVALID);
        }
        //(2)检查联盟有效期
        this.unionMainService.checkUnionMainValid(unionMember.getUnionId());
        //(3)判断是否具有读权限
        if (!this.unionMemberService.hasReadAuthority(unionMember)) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_READ_REJECT);
        }
        Map<String, Object> resultMap = new HashMap<>();
        //未结算佣金收入，即已被接受但未支付的推荐佣金收入
        Double unPaidBrokerageIncome = this.sumBrokeragePriceByFromMemberIdAndIsPaid(memberId, CommonConstant.COMMON_NO);
        resultMap.put("unPaidBrokerageIncome", unPaidBrokerageIncome);
        //已结算佣金收入，即已被接受且已支付的推荐佣金收入
        Double paidBrokerageIncome = this.sumBrokeragePriceByFromMemberIdAndIsPaid(memberId, CommonConstant.COMMON_YES);
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
        Double unPaidBrokerageExpense = this.sumBrokeragePriceByToMemberIdAndIsPaid(memberId, CommonConstant.COMMON_NO);
        resultMap.put("unPaidBrokerageExpense", unPaidBrokerageExpense);
        //已结算支出佣金，即已接受且已支付的商机推荐佣金支出
        Double paidBrokerageExpense = this.sumBrokeragePriceByToMemberIdAndIsPaid(memberId, CommonConstant.COMMON_YES);
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
        int mondayOffset = 0 - ((Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 2 + 7) % 7);//例如今天是星期四，则mondayOffSet=-3
        Date mondayInThisWeek = DateUtil.addDays(DateUtil.getCurrentDate(), mondayOffset);
        for (; mondayOffset <= 0; mondayOffset++) {
            String strDate = DateUtil.getDateString(mondayInThisWeek, DateUtil.DATE_PATTERN);
            String strDateBegin = strDate + " 00:00:00";
            String strDateEnd = strDate + " 23:59:59";
            Map<String, Double> brokerageInDayMap = getPaidBrokerageBetweenDay(memberId, strDateBegin, strDateEnd);
            String strWeek = DateUtil.getWeek(mondayInThisWeek);
            Map<String, Map<String, Double>> weekMap = new HashMap<>();
            weekMap.put(strWeek, brokerageInDayMap);
            brokerageInWeek.add(weekMap);
            mondayInThisWeek = DateUtil.addDays(mondayInThisWeek, 1);
        }
        resultMap.put("brokerageInWeek", brokerageInWeek);
        return resultMap;
    }

    /**
     * 根据盟员身份id、开始时间和结束时间，获取已支付的商机佣金收入和支出信息
     *
     * @param memberId     {not null} 盟员身份id
     * @param strDateBegin {not null} 开始时间
     * @param strDateEnd   {not null} 结束时间
     * @return
     */
    private Map<String, Double> getPaidBrokerageBetweenDay(final Integer memberId, String strDateBegin, String strDateEnd) {
        if (memberId != null || StringUtil.isNotEmpty(strDateBegin) || StringUtil.isNotEmpty(strDateEnd)) {
            Map<String, Double> resultMap = new HashMap<>();
            //已结算的收入佣金
            Wrapper incomeWrapper = new Wrapper() {
                @Override
                public String getSqlSegment() {
                    StringBuilder sbSqlSegment = new StringBuilder(" o")
                            .append(" WHERE o.del_status = ").append(CommonConstant.DEL_STATUS_NO)
                            .append("  AND o.is_accept = ").append(OpportunityConstant.ACCEPT_YES)
                            .append("  AND o.from_member_id = ").append(memberId)
                            .append("  AND exists(")
                            .append("    SELECT bi.id FROM t_union_brokerage_income bi")
                            .append("    WHERE bi.del_status = ").append(CommonConstant.DEL_STATUS_NO)
                            .append("      AND bi.type = ").append(BrokerageConstant.SOURCE_TYPE_OPPORTUNITY)
                            .append("      AND bi.opportunity_id = t_union_opportunity.id")
                            .append("  )");
                    return sbSqlSegment.toString();
                }
            };
            incomeWrapper.setSqlSelect(" IFNULL(SUM(o.brokerage_price), 0) brokeragePriceSum");
            Map<String, Object> incomeMap = this.selectMap(incomeWrapper);
            Object objIncomeBrokerageSum = incomeMap != null ? incomeMap.get("brokeragePriceSum") : null;
            Double incomeBrokerageSum = Double.valueOf(objIncomeBrokerageSum != null ? objIncomeBrokerageSum.toString() : "0");
            resultMap.put("paidBrokerageIncome", incomeBrokerageSum);
            //已结算的支出佣金
            Wrapper expenseWrapper = new Wrapper() {
                @Override
                public String getSqlSegment() {
                    StringBuilder sbSqlSegment = new StringBuilder(" o")
                            .append(" WHERE o.del_status = ").append(CommonConstant.DEL_STATUS_NO)
                            .append("  AND o.is_accept = ").append(OpportunityConstant.ACCEPT_YES)
                            .append("  AND o.to_member_id = ").append(memberId)
                            .append("  AND exists(")
                            .append("    SELECT bi.id FROM t_union_brokerage_income bi")
                            .append("    WHERE bi.del_status = ").append(CommonConstant.DEL_STATUS_NO)
                            .append("      AND bi.type = ").append(BrokerageConstant.SOURCE_TYPE_OPPORTUNITY)
                            .append("      AND bi.opportunity_id = t_union_opportunity.id")
                            .append("  )");
                    return sbSqlSegment.toString();
                }
            };
            expenseWrapper.setSqlSelect(" IFNULL(SUM(o.brokerage_price), 0) brokeragePriceSum");
            Map<String, Object> expenseMap = this.selectMap(expenseWrapper);
            Object objExpenseBrokerageSum = expenseMap != null ? expenseMap.get("brokeragePriceSum") : null;
            Double expenseBrokerageSum = Double.valueOf(objExpenseBrokerageSum != null ? objExpenseBrokerageSum.toString() : "0");
            resultMap.put("paidBrokerageExpense", expenseBrokerageSum);
            return resultMap;
        }
        return null;
    }

    //------------------------------------------ list(include page) ---------------------------------------------------

    /**
     * 根据商家id，分页查询商家推荐的商机列表信息
     *
     * @param page        {not null} 分页对象
     * @param busId       {not null} 商家id
     * @param unionId     可选项，联盟id
     * @param isAccept    可选项，受理状态，当勾选多个时用英文字符的逗号拼接，如=1,2
     * @param clientName  可选项，顾客姓名，模糊查询
     * @param clientPhone 可选项，顾客电话，模糊查询
     * @return
     * @throws Exception
     */
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
                    sbSqlSegment.append(" AND o.client_name LIKE %").append(clientName).append("%");
                }
                if (StringUtil.isNotEmpty(clientPhone)) {
                    sbSqlSegment.append(" AND o.client_phone LIKE %").append(clientPhone).append("%");
                }
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

    /**
     * 根据商家id，分页查询推荐给商家的商机列表信息
     *
     * @param page        {not null} 分页对象
     * @param busId       {not null} 商家id
     * @param unionId     可选项，联盟id
     * @param isAccept    可选项，受理状态，当勾选多个时用英文字符的逗号拼接，如=1,2
     * @param clientName  可选项，顾客姓名，模糊查询
     * @param clientPhone 可选项，顾客电话，模糊查询
     * @return
     * @throws Exception
     */
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
                    sbSqlSegment.append(" AND o.client_name LIKE %").append(clientName).append("%");
                }
                if (StringUtil.isNotEmpty(clientPhone)) {
                    sbSqlSegment.append(" AND o.client_phone LIKE %").append(clientPhone).append("%");
                }
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

    /**
     * 根据商家id，获取商家因商机推荐而得到的佣金收入列表信息
     *
     * @param page        {not null} 分页对象
     * @param busId       {not null} 商家id
     * @param unionId     可选项 联盟id
     * @param toMemberId  可选项 接受商机的盟员身份id
     * @param isClose     可选项 是否已结算，0为否，1为是
     * @param clientName  可选项 客户姓名
     * @param clientPhone 可选项 客户电话
     * @return
     * @throws Exception
     */
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
                        .append("      AND m3.id = o.from_bus_id")
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
                    sbSqlSegment.append(" AND o.client_name LIKE %").append(clientName).append("%");
                }
                if (StringUtil.isNotEmpty(clientPhone)) {
                    sbSqlSegment.append(" AND o.client_phone LIKE %").append(clientPhone).append("%");
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

    /**
     * 根据商家id，获取商家因接受商机推荐而支付的佣金列表信息
     *
     * @param page         {not null} 分页对象
     * @param busId        {not null} 商家id
     * @param unionId      可选项 联盟id
     * @param fromMemberId 可选项 推荐商机的盟员身份id
     * @param isClose      可选项 是否已结算，0为否，1为是
     * @param clientName   可选项 客户姓名
     * @param clientPhone  可选项 客户电话
     * @return
     * @throws Exception
     */
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
                        .append("      AND m3.id = o.to_member_id");
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
                    sbSqlSegment.append(" AND o.client_name LIKE %").append(clientName).append("%");
                }
                if (StringUtil.isNotEmpty(clientPhone)) {
                    sbSqlSegment.append(" AND o.client_phone LIKE %").append(clientPhone).append("%");
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

    /**
     * 根据商家id，分页获取商机佣金支付往来列表信息
     *
     * @param page         {not null} 分页对象
     * @param busId        {not null} 商家id
     * @param userMemberId 可选项 商家盟员身份id
     * @return
     * @throws Exception
     */
    @Override
    public Page pageContactByBusId(Page page, Integer busId, Integer userMemberId) throws Exception {
        if (page == null || busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        //(1)获取商家具有读权限的盟员身份id列表
        List<Integer> userMemberIdList = new ArrayList<>();
        if (userMemberId != null) {
            //(2-1)判断是否具有盟员权限
            UnionMember userMember = this.unionMemberService.getByIdAndBusId(userMemberId, busId);
            if (userMember == null) {
                throw new BusinessException(CommonConstant.UNION_MEMBER_INVALID);
            }
            //(2-2)检查联盟有效期
            this.unionMainService.checkUnionMainValid(userMember.getUnionId());
            //(2-3)判断是否具有读权限
            if (!this.unionMemberService.hasReadAuthority(userMember)) {
                throw new BusinessException(CommonConstant.UNION_MEMBER_READ_REJECT);
            }
            //(2-4)添加盟员身份id
            userMemberIdList.add(userMemberId);
        } else {
            //(2-1)获取商家具有读权限的盟员身份列表
            List<UnionMember> readMemberList = this.unionMemberService.listReadByBusId(busId);
            if (ListUtil.isNotEmpty(readMemberList)) {
                for (UnionMember readMember : readMemberList) {
                    //(2-2)添加盟员身份id
                    userMemberIdList.add(readMember.getBusId());
                }
            }
        }
        //(3)获取所有与商家有过商机推荐支付往来的盟员身份id列表
        Set<Integer> tgtMemberIdSet = new HashSet<>();
        if (ListUtil.isNotEmpty(userMemberIdList)) {
            for (Integer tempUserMemberId : userMemberIdList) {
                //(4-1)我推荐的已支付的商机列表信息
                List<UnionOpportunity> opportunityListFromMe = this.listPaidByFromMemberId(tempUserMemberId);
                if (ListUtil.isNotEmpty(opportunityListFromMe)) {
                    for (UnionOpportunity opportunityFromMe : opportunityListFromMe) {
                        tgtMemberIdSet.add(opportunityFromMe.getToMemberId());
                    }
                }
                //(4-2)我接受的已支付的商机列表信息
                List<UnionOpportunity> opportunityListToMe = this.listPaidByToMemberId(tempUserMemberId);
                if (ListUtil.isNotEmpty(opportunityListToMe)) {
                    for (UnionOpportunity opportunityToMe : opportunityListToMe) {
                        tgtMemberIdSet.add(opportunityToMe.getFromMemberId());
                    }
                }
            }
        }
        //(5)根据目标盟员身份id列表，分页获取目标盟员身份列表信息
        Page result = this.unionMemberService.pageByMemberIdList(page, ListUtil.toList(tgtMemberIdSet));
        //(6)根据分页后的目标盟员列表信息，统计已支付的商机推荐佣金往来信息
        List<UnionMember> tgtMemberList = result.getRecords();
        if (ListUtil.isNotEmpty(tgtMemberList)) {
            List<Map<String, Object>> records = new ArrayList<>();
            for (UnionMember tgtMember : tgtMemberList) {
                Map<String, Object> map = new HashMap<>();
                map.put("tgtMemberId", tgtMember.getId()); //目标盟员id
                map.put("tgtMemberEnterpriseName", tgtMember.getEnterpriseName()); //目标盟员名称
                UnionMain tgtUnion = this.unionMainService.getById(tgtMember.getUnionId());
                map.put("tgtUnionName", tgtUnion != null ? tgtUnion.getName() : ""); //目标盟员所在联盟名称
                Double brokerageIncome;
                if (userMemberId != null) {
                    brokerageIncome = this.sumPaidBrokeragePriceByFromMemberIdAndToMemberId(userMemberId, tgtMember.getId());
                } else {
                    brokerageIncome = this.sumPaidBrokeragePriceByBusIdAndToMemberId(busId, tgtMember.getId());
                }
                Double brokerageExpense;
                if (userMemberId != null) {
                    brokerageExpense = this.sumPaidBrokeragePriceByFromMemberIdAndToMemberId(tgtMember.getId(), userMemberId);
                } else {
                    brokerageExpense = this.sumPaidBrokeragePriceByBusIdAndFromMemberId(busId, tgtMember.getId());
                }
                BigDecimal contactMoney = BigDecimalUtil.subtract(brokerageIncome, brokerageExpense);
                map.put("contactMoney", contactMoney.doubleValue()); //往来金额
                records.add(map);
            }
            result.setRecords(records);
        }
        return result;
    }

    /**
     * 根据推荐方的盟员身份id，获取所有已推荐的，且已支付的商机推荐列表信息
     *
     * @param fromMemberId {not null} 推荐方的盟员身份id
     * @return
     * @throws Exception
     */
    @Override
    public List<UnionOpportunity> listPaidByFromMemberId(Integer fromMemberId) throws Exception {
        if (fromMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        EntityWrapper entityWrapper = new EntityWrapper();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("is_accept", OpportunityConstant.ACCEPT_YES)
                .eq("from_member_id", fromMemberId)
                .exists(new StringBuilder(" SELECT bi.id FROM t_union_brokerage_income bi")
                        .append(" WHERE bi.del_status = ").append(CommonConstant.DEL_STATUS_NO)
                        .append("  AND bi.type = ").append(BrokerageConstant.SOURCE_TYPE_OPPORTUNITY)
                        .append("  AND bi.opportunity_id = t_union_opportunity.id")
                        .toString());
        return this.selectList(entityWrapper);
    }

    /**
     * 根据商家id和推荐方的盟员身份id，获取所有推荐给商家的，已接受状态的，且已支付的商机推荐列表信息
     *
     * @param busId        {not null} 商家id
     * @param fromMemberId {not null} 推荐方的盟员身份id
     * @return
     * @throws Exception
     */
    @Override
    public List<UnionOpportunity> listPaidByBusIdAndFromMemberId(Integer busId, Integer fromMemberId) throws Exception {
        if (busId == null || fromMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        EntityWrapper entityWrapper = new EntityWrapper();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("is_accept", OpportunityConstant.ACCEPT_YES)
                .eq("from_member_id", fromMemberId)
                .exists(new StringBuilder(" SELECT bi.id FROM t_union_brokerage_income bi")
                        .append(" WHERE bi.del_status = ").append(CommonConstant.DEL_STATUS_NO)
                        .append("  AND bi.type = ").append(BrokerageConstant.SOURCE_TYPE_OPPORTUNITY)
                        .append("  AND bi.opportunity_id = t_union_opportunity.id")
                        .toString())
                .exists(new StringBuilder(" SELECT * FROM t_union_member m")
                        .append(" WHERE m.del_status = ").append(CommonConstant.DEL_STATUS_NO)
                        .append("  AND m.status != ").append(MemberConstant.STATUS_APPLY_IN)
                        .append("  AND m.bus_id = ").append(busId)
                        .append("  AND m.id = t_union_opportunity.to_member_id")
                        .toString());
        return this.selectList(entityWrapper);
    }

    /**
     * 根据接收方的盟员身份id，获取所有已接受，且已支付的商机推荐列表信息
     *
     * @param toMemberId {not null} 接收方的盟员身份id
     * @return
     * @throws Exception
     */
    @Override
    public List<UnionOpportunity> listPaidByToMemberId(Integer toMemberId) throws Exception {
        if (toMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        EntityWrapper entityWrapper = new EntityWrapper();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("is_accept", OpportunityConstant.ACCEPT_YES)
                .eq("to_member_id", toMemberId)
                .exists(new StringBuilder(" SELECT bi.id FROM t_union_brokerage_income bi")
                        .append(" WHERE bi.del_status = ").append(CommonConstant.DEL_STATUS_NO)
                        .append("  AND bi.type = ").append(BrokerageConstant.SOURCE_TYPE_OPPORTUNITY)
                        .append("  AND bi.opportunity_id = t_union_opportunity.id")
                        .toString());
        return this.selectList(entityWrapper);
    }

    /**
     * 根据商家id和接收方的盟员身份id，获取所有商家推荐的，已接受状态，且已支付的商机推荐列表信息
     *
     * @param busId      {not null} 商家id
     * @param toMemberId {not null} 接收方的盟员身份id
     * @return
     * @throws Exception
     */
    @Override
    public List<UnionOpportunity> listPaidByBusIdAndToMemberId(Integer busId, Integer toMemberId) throws Exception {
        if (busId == null || toMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        EntityWrapper entityWrapper = new EntityWrapper();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("is_accept", OpportunityConstant.ACCEPT_YES)
                .eq("to_member_id", toMemberId)
                .exists(new StringBuilder(" SELECT bi.id FROM t_union_brokerage_income bi")
                        .append(" WHERE bi.del_status = ").append(CommonConstant.DEL_STATUS_NO)
                        .append("  AND bi.type = ").append(BrokerageConstant.SOURCE_TYPE_OPPORTUNITY)
                        .append("  AND bi.opportunity_id = t_union_opportunity.id")
                        .toString())
                .exists(new StringBuilder(" SELECT m.id FROM t_union_member m")
                        .append(" WHERE m.del_status = ").append(CommonConstant.DEL_STATUS_NO)
                        .append("  AND m.status != ").append(MemberConstant.STATUS_APPLY_IN)
                        .append("  AND m.bus_id = ").append(busId)
                        .append("  AND m.id = t_union_opportunity.from_member_id")
                        .toString());
        return this.selectList(entityWrapper);
    }

    /**
     * 根据推荐方的盟员身份id和接收方的盟员身份id，获取所有已接受的，且已支付的商机推荐列表信息
     *
     * @param fromMemberId {not null} 推荐方的盟员身份id
     * @param toMemberId   {not null} 接收方的盟员身份id
     * @return
     * @throws Exception
     */
    @Override
    public List<UnionOpportunity> listPaidByFromMemberIdAndToMemberId(Integer fromMemberId, Integer toMemberId) throws Exception {
        if (fromMemberId == null || toMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        EntityWrapper entityWrapper = new EntityWrapper();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("is_accept", OpportunityConstant.ACCEPT_YES)
                .eq("from_member_id", fromMemberId)
                .eq("to_member_id", toMemberId)
                .exists(new StringBuilder(" SELECT bi.id FROM t_union_brokerage_income bi")
                        .append(" WHERE bi.del_status = ").append(CommonConstant.DEL_STATUS_NO)
                        .append("  AND bi.type = ").append(BrokerageConstant.SOURCE_TYPE_OPPORTUNITY)
                        .append("  AND bi.opportunity_id = t_union_opportunity.id")
                        .toString());
        return this.selectList(entityWrapper);
    }

    //------------------------------------------------- update --------------------------------------------------------

    /**
     * 接受商机：根据商机推荐id、商家id和受理金额，更新商机推荐信息
     *
     * @param opportunityId {not null} 商机推荐id
     * @param busId         {not null} 商家id
     * @param acceptPrice   {not null} 受理金额
     * @throws Exception
     */
    @Override
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
        this.unionMainService.checkUnionMainValid(toMember.getUnionId());
        //(4)判断是否具有写权限
        if (!this.unionMemberService.hasWriteAuthority(toMember)) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_WRITE_REJECT);
        }
        //(5)判断是否设置了商机佣金比例
        UnionOpportunityBrokerageRatio ratio = this.unionOpportunityBrokerageRatioService.getByFromMemberIdAndToMemberId(
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
        this.updateById(updateOpportunity);
        this.phoneMessageSender.sendMsg(phoneMessage);
    }

    /**
     * 拒绝商机：根据商机推荐id和商家id，更新商机推荐信息
     *
     * @param opportunityId
     * @param busId
     * @throws Exception
     */
    @Override
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
        this.unionMainService.checkUnionMainValid(toMember.getUnionId());
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
        this.updateById(updateOpportunity);
        this.phoneMessageSender.sendMsg(phoneMessage);
    }

    //------------------------------------------------- save ----------------------------------------------------------

    /**
     * 根据商家id和盟员身份id，保存商机推荐信息
     *
     * @param busId    {not null} 商家id
     * @param memberId {not null} 盟员身份id
     * @param vo
     * @throws Exception
     */
    @Override
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
        this.unionMainService.checkUnionMainValid(fromMember.getUnionId());
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
        if (this.unionMemberService.hasWriteAuthority(toMember)) {
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
        this.insert(saveOpportunity);
        this.phoneMessageSender.sendMsg(phoneMessage);
    }

    //------------------------------------------------- count ---------------------------------------------------------

    /**
     * 根据商家id和接收方盟员身份id，统计商家推荐的、已接受状态的、且已支付的商机佣金总和
     *
     * @param busId      {not null} 商家id
     * @param toMemberId {not null} 接收方盟员身份id
     * @return
     * @throws Exception
     */
    @Override
    public Double sumPaidBrokeragePriceByBusIdAndToMemberId(final Integer busId, final Integer toMemberId) throws Exception {
        if (busId == null || toMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        Wrapper wrapper = new Wrapper() {
            @Override
            public String getSqlSegment() {
                StringBuilder sbSqlSegment = new StringBuilder(" o")
                        .append(" WHERE o.del_status = ").append(CommonConstant.DEL_STATUS_NO)
                        .append("  AND o.is_accept = ").append(OpportunityConstant.ACCEPT_YES)
                        .append("  AND o.to_member_id = ").append(toMemberId)
                        .append("  AND exists(")
                        .append("    SELECT bi.id FROM t_union_brokerage_income bi")
                        .append("    WHERE bi.del_status = ").append(CommonConstant.DEL_STATUS_NO)
                        .append("      AND bi.type = ").append(BrokerageConstant.SOURCE_TYPE_OPPORTUNITY)
                        .append("      AND bi.opportunity_id = o.id")
                        .append("  )")
                        .append("  AND exists(")
                        .append("    SELECT m.id FROM t_union_member m")
                        .append("    WHERE m.del_status = ").append(CommonConstant.DEL_STATUS_NO)
                        .append("      AND m.status !=").append(MemberConstant.STATUS_APPLY_IN)
                        .append("      AND m.id = o.from_member_id")
                        .append("      AND m.bus_id = ").append(busId)
                        .append("  )");
                return sbSqlSegment.toString();
            }
        };
        wrapper.setSqlSelect(" IFNULL(SUM(o.brokerage_price), 0) brokeragePriceSum");
        Map<String, Object> map = this.selectMap(wrapper);
        Object objBrokeragePriceSum = map != null ? map.get("brokeragePriceSum") : null;
        Double brokeragePriceSum = Double.valueOf(objBrokeragePriceSum != null ? objBrokeragePriceSum.toString() : "0");
        return brokeragePriceSum;
    }

    /**
     * 根据商家id和推荐方盟员身份id，统计商家接收的、已接受状态的、且已支付的商机佣金总和
     *
     * @param busId        {not null} 商家id
     * @param fromMemberId {not null} 推荐方盟员身份id
     * @return
     * @throws Exception
     */
    @Override
    public Double sumPaidBrokeragePriceByBusIdAndFromMemberId(final Integer busId, final Integer fromMemberId) throws Exception {
        if (busId == null || fromMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        Wrapper wrapper = new Wrapper() {
            @Override
            public String getSqlSegment() {
                StringBuilder sbSqlSegment = new StringBuilder(" o")
                        .append(" WHERE o.del_status = ").append(CommonConstant.DEL_STATUS_NO)
                        .append("  AND o.is_accept = ").append(OpportunityConstant.ACCEPT_YES)
                        .append("  AND o.from_member_id = ").append(fromMemberId)
                        .append("  AND exists(")
                        .append("    SELECT bi.id FROM t_union_brokerage_income bi")
                        .append("    WHERE bi.del_status = ").append(CommonConstant.DEL_STATUS_NO)
                        .append("      AND bi.type = ").append(BrokerageConstant.SOURCE_TYPE_OPPORTUNITY)
                        .append("      AND bi.opportunity_id = o.id")
                        .append("  )")
                        .append("  AND exists(")
                        .append("    SELECT m.id FROM t_union_member m")
                        .append("    WHERE m.del_status = ").append(CommonConstant.DEL_STATUS_NO)
                        .append("      AND m.status !=").append(MemberConstant.STATUS_APPLY_IN)
                        .append("      AND m.id = o.to_member_id")
                        .append("      AND m.bus_id = ").append(busId)
                        .append("  )");
                return sbSqlSegment.toString();
            }
        };
        wrapper.setSqlSelect(" IFNULL(SUM(o.brokerage_price), 0) brokeragePriceSum");
        Map<String, Object> map = this.selectMap(wrapper);
        Object objBrokeragePriceSum = map != null ? map.get("brokeragePriceSum") : null;
        Double brokeragePriceSum = Double.valueOf(objBrokeragePriceSum != null ? objBrokeragePriceSum.toString() : "0");
        return brokeragePriceSum;
    }

    /**
     * 根据推荐方盟员身份id和接收方盟员身份id，统计推荐方推荐的、已接受状态的、且已支付的商机佣金总和
     *
     * @param fromMemberId {not null} 推荐方盟员身份id
     * @param toMemberId   {not null} 接收方盟员身份id
     * @return
     * @throws Exception
     */
    @Override
    public Double sumPaidBrokeragePriceByFromMemberIdAndToMemberId(final Integer fromMemberId, final Integer toMemberId) throws Exception {
        if (fromMemberId == null || toMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        Wrapper wrapper = new Wrapper() {
            @Override
            public String getSqlSegment() {
                StringBuilder sbSqlSegment = new StringBuilder(" o")
                        .append(" WHERE o.del_status = ").append(CommonConstant.DEL_STATUS_NO)
                        .append("  AND o.is_accept = ").append(OpportunityConstant.ACCEPT_YES)
                        .append("  AND o.from_member_id = ").append(fromMemberId)
                        .append("  AND o.to_member_id = ").append(toMemberId)
                        .append("  AND exists(")
                        .append("    SELECT bi.id FROM t_union_brokerage_income bi")
                        .append("    WHERE bi.del_status = ").append(CommonConstant.DEL_STATUS_NO)
                        .append("      AND bi.type = ").append(BrokerageConstant.SOURCE_TYPE_OPPORTUNITY)
                        .append("      AND bi.opportunity_id = o.id")
                        .append("  )");
                return sbSqlSegment.toString();
            }
        };
        wrapper.setSqlSelect(" IFNULL(SUM(o.brokerage_price), 0) brokeragePriceSum");
        Map<String, Object> map = this.selectMap(wrapper);
        Object objBrokeragePriceSum = map != null ? map.get("brokeragePriceSum") : null;
        Double brokeragePriceSum = Double.valueOf(objBrokeragePriceSum != null ? objBrokeragePriceSum.toString() : "0");
        return brokeragePriceSum;
    }

    /**
     * 根据商机推荐方的盟员身份id，统计已被接受的、已支付或未支付的佣金之和，即佣金收入
     *
     * @param fromMemberId {not null} 商机推荐方的盟员身份id
     * @param isPaid       {not null} 是否已支付，1为是，0为否
     * @return
     * @throws Exception
     */
    @Override
    public Double sumBrokeragePriceByFromMemberIdAndIsPaid(final Integer fromMemberId, final Integer isPaid) throws Exception {
        if (fromMemberId == null || isPaid == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        Wrapper wrapper = new Wrapper() {
            @Override
            public String getSqlSegment() {
                StringBuilder sbSqlSegment = new StringBuilder(" o")
                        .append(" WHERE o.del_status = ").append(CommonConstant.DEL_STATUS_NO)
                        .append("  AND o.is_accept = ").append(OpportunityConstant.ACCEPT_YES)
                        .append("  AND o.from_member_id = ").append(fromMemberId)
                        .append("  AND ").append(isPaid == CommonConstant.COMMON_YES ? "exists(" : "not exists(")
                        .append("    SELECT * FROM t_union_brokerage_income bi")
                        .append("    WHERE bi.del_status = ").append(CommonConstant.DEL_STATUS_NO)
                        .append("      AND bi.type = ").append(BrokerageConstant.SOURCE_TYPE_OPPORTUNITY)
                        .append("      AND bi.opportunity_id = o.id")
                        .append("  )");
                return sbSqlSegment.toString();
            }
        };
        wrapper.setSqlSelect("IFNULL(SUM(o.brokerage_price), 0) brokeragePriceSum");
        Map<String, Object> map = this.selectMap(wrapper);
        Object objBrokeragePriceSum = map != null ? map.get("brokeragePriceSum") : null;
        Double brokeragePriceSum = Double.valueOf(objBrokeragePriceSum != null ? objBrokeragePriceSum.toString() : "0");
        return brokeragePriceSum;
    }

    /**
     * 根据商机接受方的盟员身份id，统计已接受的、已支付或未支付的佣金之和，即佣金支出
     *
     * @param toMemberId {not null} 商机接受方的盟员身份id
     * @param isPaid     {not null} 是否已支付，1为是，0为否
     * @return
     * @throws Exception
     */
    @Override
    public Double sumBrokeragePriceByToMemberIdAndIsPaid(final Integer toMemberId, final Integer isPaid) throws Exception {
        if (toMemberId == null || isPaid == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        Wrapper wrapper = new Wrapper() {
            @Override
            public String getSqlSegment() {
                StringBuilder sbSqlSegment = new StringBuilder(" o")
                        .append(" WHERE o.del_status = ").append(CommonConstant.DEL_STATUS_NO)
                        .append("  AND o.is_accept = ").append(OpportunityConstant.ACCEPT_YES)
                        .append("  AND o.to_member_id = ").append(toMemberId)
                        .append("  AND ").append(isPaid == CommonConstant.COMMON_YES ? "exists(" : "not exists(")
                        .append("    SELECT * FROM t_union_brokerage_income bi")
                        .append("    WHERE bi.del_status = ").append(CommonConstant.DEL_STATUS_NO)
                        .append("      AND bi.type = ").append(BrokerageConstant.SOURCE_TYPE_OPPORTUNITY)
                        .append("      AND bi.opportunity_id = o.id")
                        .append("  )");
                return sbSqlSegment.toString();
            }
        };
        wrapper.setSqlSelect("IFNULL(SUM(o.brokerage_price), 0) brokeragePriceSum");
        Map<String, Object> map = this.selectMap(wrapper);
        Object objBrokeragePriceSum = map != null ? map.get("brokeragePriceSum") : null;
        Double brokeragePriceSum = Double.valueOf(objBrokeragePriceSum != null ? objBrokeragePriceSum.toString() : "0");
        return brokeragePriceSum;
    }

    //------------------------------------------------ boolean --------------------------------------------------------


    //------------------------------------------------ money --------------------------------------------------------
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
        WxPublicUsers publicUser = busUserService.getWxPublicUserByBusId(duofenBusId);
        for (UnionOpportunity opportunity : list) {
            if (CommonUtil.isEmpty(opportunity.getIsAccept()) || opportunity.getIsAccept() != OpportunityConstant.ACCEPT_NON) {
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
        data.put("busId", duofenBusId);
        data.put("sourceType", 1);//是否墨盒支付
        data.put("payWay", 1);//系统判断支付方式
        data.put("isreturn", 0);//0：不需要同步跳转
        data.put("model", ConfigConstant.PAY_MODEL);
        String encrypt = EncryptUtil.encrypt(encryptKey, ids);
        encrypt = URLEncoder.encode(encrypt, "UTF-8");
        data.put("notifyUrl", unionUrl + "/unionOpportunity/79B4DE7C/paymentSuccess/" + encrypt + "/" + only);
        data.put("orderNum", orderNo);//订单号
        data.put("payBusId", busId);//支付的商家id
        data.put("isSendMessage", 0);//不推送
        data.put("appid", publicUser.getAppid());//appid
        data.put("desc", "联盟商机推荐");
        data.put("appidType", 0);//公众号
        data.put("only", only);
        String paramKey = RedisKeyUtil.getRecommendPayParamKey(only);
        String statusKey = RedisKeyUtil.getRecommendPayStatusKey(only);
        redisCacheUtil.set(paramKey, JSON.toJSONString(data), 360l);//5分钟
        redisCacheUtil.set(statusKey, ConfigConstant.USER_ORDER_STATUS_001, 300l);//等待扫码状态
        return data;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void payOpportunitySuccess(String encrypt, String only) throws Exception {
        //解密参数
        String ids = EncryptUtil.decrypt(encryptKey, encrypt);
        String paramKey = RedisKeyUtil.getRecommendPayParamKey(only);
        Object obj = redisCacheUtil.get(paramKey);
        Map<String, Object> result = JSONObject.parseObject(obj.toString(), Map.class);
        String statusKey = RedisKeyUtil.getRecommendPayStatusKey(only);
        String[] arrs = ids.split(",");
        EntityWrapper wrapper = new EntityWrapper<>();
        wrapper.eq("del_status", CommonConstant.DEL_STATUS_NO);
        wrapper.in("id", arrs);
        List<UnionOpportunity> list = this.selectList(wrapper);
        String orderNo = result.get("orderNum").toString();
        //插入佣金收入列表
        insertBatchByList(list, orderNo);
        redisCacheUtil.remove(paramKey);
        redisCacheUtil.set(statusKey, ConfigConstant.USER_ORDER_STATUS_003, 60l);//支付成功
    }

    /**
     * 插入佣金收入列表
     *
     * @param list
     */
    private void insertBatchByList(List<UnionOpportunity> list, String orderNo) throws Exception{
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
            pays.add(pay);
        }
        unionBrokerageIncomeService.insertBatch(incomes);
        unionBrokeragePayService.insertBatch(pays);
    }
}
