package com.gt.union.opportunity.main.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.*;
import com.gt.union.opportunity.brokerage.constant.BrokerageConstant;
import com.gt.union.opportunity.main.constant.OpportunityConstant;
import com.gt.union.opportunity.main.entity.UnionOpportunity;
import com.gt.union.opportunity.main.entity.UnionOpportunityRatio;
import com.gt.union.opportunity.main.mapper.UnionOpportunityMapper;
import com.gt.union.opportunity.main.service.IUnionOpportunityRatioService;
import com.gt.union.opportunity.main.service.IUnionOpportunityService;
import com.gt.union.opportunity.main.util.UnionOpportunityCacheUtil;
import com.gt.union.opportunity.main.vo.OpportunityStatisticsDay;
import com.gt.union.opportunity.main.vo.OpportunityStatisticsVO;
import com.gt.union.opportunity.main.vo.OpportunityVO;
import com.gt.union.union.main.entity.UnionMain;
import com.gt.union.union.main.service.IUnionMainService;
import com.gt.union.union.member.entity.UnionMember;
import com.gt.union.union.member.service.IUnionMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * 商机 服务实现类
 *
 * @author linweicong
 * @version 2017-11-23 16:56:17
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
    private IUnionOpportunityRatioService opportunityRatioService;

    //***************************************** Domain Driven Design - get *********************************************

    @Override
    public UnionOpportunity getByIdAndUnionIdAndToMemberId(Integer opportunityId, Integer unionId, Integer toMemberId) throws Exception {
        if (opportunityId == null || unionId == null || toMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        UnionOpportunity result = getById(opportunityId);

        return result != null && unionId.equals(result.getUnionId()) && toMemberId.equals(result.getToMemberId()) ? result : null;
    }

    @Override
    public OpportunityStatisticsVO getOpportunityStatisticsVOByBusIdAndUnionId(Integer busId, Integer unionId) throws Exception {
        if (busId == null || unionId == null) {
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
        // （2）	获取已接受的给我的商机，区分是否已支付
        OpportunityStatisticsVO result = new OpportunityStatisticsVO();

        BigDecimal paidIncome = BigDecimal.ZERO;
        List<UnionOpportunity> paidToMeOpportunityList = listAcceptConfirmedByToMemberIdAndUnionId(member.getId(), unionId, CommonConstant.COMMON_YES);
        if (ListUtil.isNotEmpty(paidToMeOpportunityList)) {
            for (UnionOpportunity opportunity : paidToMeOpportunityList) {
                paidIncome = BigDecimalUtil.add(paidIncome, opportunity.getBrokerageMoney());
            }
        }
        result.setPaidIncome(paidIncome.doubleValue());

        BigDecimal unPaidIncome = BigDecimal.ZERO;
        List<UnionOpportunity> unPaidToMeOpportunityList = listAcceptConfirmedByToMemberIdAndUnionId(member.getId(), unionId, CommonConstant.COMMON_NO);
        if (ListUtil.isNotEmpty(unPaidToMeOpportunityList)) {
            for (UnionOpportunity opportunity : paidToMeOpportunityList) {
                unPaidIncome = BigDecimalUtil.add(unPaidIncome, opportunity.getBrokerageMoney());
            }
        }
        result.setUnPaidIncome(unPaidIncome.doubleValue());

        BigDecimal incomeSum = BigDecimalUtil.add(paidIncome, unPaidIncome);
        result.setPaidIncomePercent(BigDecimalUtil.divide(paidIncome, incomeSum, 4).doubleValue());
        result.setUnPaidIncomePercent(BigDecimalUtil.divide(unPaidIncome, incomeSum, 4).doubleValue());
        // （3）	获取已接受的我给的商机，区分是否已支付
        BigDecimal paidExpense = BigDecimal.ZERO;
        List<UnionOpportunity> paidFromMeOpportunityList = listAcceptConfirmedByFromMemberIdAndUnionId(member.getId(), unionId, CommonConstant.COMMON_YES);
        if (ListUtil.isNotEmpty(paidFromMeOpportunityList)) {
            for (UnionOpportunity opportunity : paidFromMeOpportunityList) {
                paidExpense = BigDecimalUtil.add(paidExpense, opportunity.getBrokerageMoney());
            }
        }
        result.setPaidExpense(paidExpense.doubleValue());

        BigDecimal unPaidExpense = BigDecimal.ZERO;
        List<UnionOpportunity> unPaidFromMeOpportunityList = listAcceptConfirmedByFromMemberIdAndUnionId(member.getId(), unionId, CommonConstant.COMMON_NO);
        if (ListUtil.isNotEmpty(unPaidFromMeOpportunityList)) {
            for (UnionOpportunity opportunity : unPaidFromMeOpportunityList) {
                unPaidExpense = BigDecimalUtil.add(unPaidExpense, opportunity.getBrokerageMoney());
            }
        }
        result.setUnPaidExpense(unPaidExpense.doubleValue());

        BigDecimal expenseSum = BigDecimalUtil.add(paidExpense, unPaidExpense);
        result.setPaidExpensePercent(BigDecimalUtil.divide(unPaidExpense, expenseSum, 4).doubleValue());
        result.setUnPaidExpensePercent(BigDecimalUtil.divide(unPaidExpense, expenseSum, 4).doubleValue());
        // （4）	获取一周内商机收支信息
        // （4-1）周一
        OpportunityStatisticsDay mondayStatistic = new OpportunityStatisticsDay();
        Date monday = DateUtil.getMondayInWeek();
        String strMonday = DateUtil.getDateString(monday, DateUtil.DATE_PATTERN);
        Date beginDate = DateUtil.parseDate(strMonday + " 00:00:00", DateUtil.DATETIME_PATTERN);
        Date endDate = DateUtil.parseDate(strMonday + " 23:59:59", DateUtil.DATETIME_PATTERN);
        List<UnionOpportunity> mondayIncomeOpportunityList = filterBetweenTime(paidFromMeOpportunityList, beginDate, endDate);
        BigDecimal mondayIncome = BigDecimal.ZERO;
        if (ListUtil.isNotEmpty(mondayIncomeOpportunityList)) {
            for (UnionOpportunity opportunity : mondayIncomeOpportunityList) {
                mondayIncome = BigDecimalUtil.add(mondayIncome, opportunity.getBrokerageMoney());
            }
        }
        mondayStatistic.setPaidIncome(mondayIncome.doubleValue());
        List<UnionOpportunity> mondayExpenseOpportunityList = filterBetweenTime(paidToMeOpportunityList, beginDate, endDate);
        BigDecimal mondayExpense = BigDecimal.ZERO;
        if (ListUtil.isNotEmpty(mondayExpenseOpportunityList)) {
            for (UnionOpportunity opportunity : mondayExpenseOpportunityList) {
                mondayExpense = BigDecimalUtil.add(mondayExpense, opportunity.getBrokerageMoney());
            }
        }
        mondayStatistic.setPaidExpense(mondayExpense.doubleValue());
        result.setMonday(mondayStatistic);

        // （4-1）周二
        OpportunityStatisticsDay tuesdayStatistic = new OpportunityStatisticsDay();
        Date tuesday = DateUtil.addDays(monday, 1);
        strMonday = DateUtil.getDateString(tuesday, DateUtil.DATE_PATTERN);
        beginDate = DateUtil.parseDate(strMonday + " 00:00:00", DateUtil.DATETIME_PATTERN);
        endDate = DateUtil.parseDate(strMonday + " 23:59:59", DateUtil.DATETIME_PATTERN);
        List<UnionOpportunity> tuesdayIncomeOpportunityList = filterBetweenTime(paidFromMeOpportunityList, beginDate, endDate);
        BigDecimal tuesdayIncome = BigDecimal.ZERO;
        if (ListUtil.isNotEmpty(tuesdayIncomeOpportunityList)) {
            for (UnionOpportunity opportunity : tuesdayIncomeOpportunityList) {
                tuesdayIncome = BigDecimalUtil.add(tuesdayIncome, opportunity.getBrokerageMoney());
            }
        }
        tuesdayStatistic.setPaidIncome(tuesdayIncome.doubleValue());
        List<UnionOpportunity> tuesdayExpenseOpportunityList = filterBetweenTime(paidToMeOpportunityList, beginDate, endDate);
        BigDecimal tuesdayExpense = BigDecimal.ZERO;
        if (ListUtil.isNotEmpty(tuesdayExpenseOpportunityList)) {
            for (UnionOpportunity opportunity : tuesdayExpenseOpportunityList) {
                tuesdayExpense = BigDecimalUtil.add(tuesdayExpense, opportunity.getBrokerageMoney());
            }
        }
        tuesdayStatistic.setPaidExpense(tuesdayExpense.doubleValue());
        result.setTuesday(tuesdayStatistic);

        // （4-1）周三
        OpportunityStatisticsDay wednesdayStatistic = new OpportunityStatisticsDay();
        Date wednesday = DateUtil.addDays(tuesday, 1);
        strMonday = DateUtil.getDateString(wednesday, DateUtil.DATE_PATTERN);
        beginDate = DateUtil.parseDate(strMonday + " 00:00:00", DateUtil.DATETIME_PATTERN);
        endDate = DateUtil.parseDate(strMonday + " 23:59:59", DateUtil.DATETIME_PATTERN);
        List<UnionOpportunity> wednesdayIncomeOpportunityList = filterBetweenTime(paidFromMeOpportunityList, beginDate, endDate);
        BigDecimal wednesdayIncome = BigDecimal.ZERO;
        if (ListUtil.isNotEmpty(wednesdayIncomeOpportunityList)) {
            for (UnionOpportunity opportunity : wednesdayIncomeOpportunityList) {
                wednesdayIncome = BigDecimalUtil.add(wednesdayIncome, opportunity.getBrokerageMoney());
            }
        }
        wednesdayStatistic.setPaidIncome(wednesdayIncome.doubleValue());
        List<UnionOpportunity> wednesdayExpenseOpportunityList = filterBetweenTime(paidToMeOpportunityList, beginDate, endDate);
        BigDecimal wednesdayExpense = BigDecimal.ZERO;
        if (ListUtil.isNotEmpty(wednesdayExpenseOpportunityList)) {
            for (UnionOpportunity opportunity : wednesdayExpenseOpportunityList) {
                wednesdayExpense = BigDecimalUtil.add(wednesdayExpense, opportunity.getBrokerageMoney());
            }
        }
        wednesdayStatistic.setPaidExpense(wednesdayExpense.doubleValue());
        result.setWednesday(wednesdayStatistic);

        // （4-1）周四
        OpportunityStatisticsDay thursdayStatistic = new OpportunityStatisticsDay();
        Date thursday = DateUtil.addDays(wednesday, 1);
        strMonday = DateUtil.getDateString(thursday, DateUtil.DATE_PATTERN);
        beginDate = DateUtil.parseDate(strMonday + " 00:00:00", DateUtil.DATETIME_PATTERN);
        endDate = DateUtil.parseDate(strMonday + " 23:59:59", DateUtil.DATETIME_PATTERN);
        List<UnionOpportunity> thursdayIncomeOpportunityList = filterBetweenTime(paidFromMeOpportunityList, beginDate, endDate);
        BigDecimal thursdayIncome = BigDecimal.ZERO;
        if (ListUtil.isNotEmpty(thursdayIncomeOpportunityList)) {
            for (UnionOpportunity opportunity : thursdayIncomeOpportunityList) {
                thursdayIncome = BigDecimalUtil.add(thursdayIncome, opportunity.getBrokerageMoney());
            }
        }
        thursdayStatistic.setPaidIncome(thursdayIncome.doubleValue());
        List<UnionOpportunity> thursdayExpenseOpportunityList = filterBetweenTime(paidToMeOpportunityList, beginDate, endDate);
        BigDecimal thursdayExpense = BigDecimal.ZERO;
        if (ListUtil.isNotEmpty(thursdayExpenseOpportunityList)) {
            for (UnionOpportunity opportunity : thursdayExpenseOpportunityList) {
                thursdayExpense = BigDecimalUtil.add(thursdayExpense, opportunity.getBrokerageMoney());
            }
        }
        thursdayStatistic.setPaidExpense(thursdayExpense.doubleValue());
        result.setThursday(thursdayStatistic);

        // （4-1）周五
        OpportunityStatisticsDay fridayStatistic = new OpportunityStatisticsDay();
        Date friday = DateUtil.addDays(thursday, 1);
        strMonday = DateUtil.getDateString(friday, DateUtil.DATE_PATTERN);
        beginDate = DateUtil.parseDate(strMonday + " 00:00:00", DateUtil.DATETIME_PATTERN);
        endDate = DateUtil.parseDate(strMonday + " 23:59:59", DateUtil.DATETIME_PATTERN);
        List<UnionOpportunity> fridayIncomeOpportunityList = filterBetweenTime(paidFromMeOpportunityList, beginDate, endDate);
        BigDecimal fridayIncome = BigDecimal.ZERO;
        if (ListUtil.isNotEmpty(fridayIncomeOpportunityList)) {
            for (UnionOpportunity opportunity : fridayIncomeOpportunityList) {
                fridayIncome = BigDecimalUtil.add(fridayIncome, opportunity.getBrokerageMoney());
            }
        }
        fridayStatistic.setPaidIncome(fridayIncome.doubleValue());
        List<UnionOpportunity> fridayExpenseOpportunityList = filterBetweenTime(paidToMeOpportunityList, beginDate, endDate);
        BigDecimal fridayExpense = BigDecimal.ZERO;
        if (ListUtil.isNotEmpty(fridayExpenseOpportunityList)) {
            for (UnionOpportunity opportunity : fridayExpenseOpportunityList) {
                fridayExpense = BigDecimalUtil.add(fridayExpense, opportunity.getBrokerageMoney());
            }
        }
        fridayStatistic.setPaidExpense(fridayExpense.doubleValue());
        result.setFriday(fridayStatistic);

        // （4-1）周六
        OpportunityStatisticsDay saturdayStatistic = new OpportunityStatisticsDay();
        Date saturday = DateUtil.addDays(friday, 1);
        strMonday = DateUtil.getDateString(saturday, DateUtil.DATE_PATTERN);
        beginDate = DateUtil.parseDate(strMonday + " 00:00:00", DateUtil.DATETIME_PATTERN);
        endDate = DateUtil.parseDate(strMonday + " 23:59:59", DateUtil.DATETIME_PATTERN);
        List<UnionOpportunity> saturdayIncomeOpportunityList = filterBetweenTime(paidFromMeOpportunityList, beginDate, endDate);
        BigDecimal saturdayIncome = BigDecimal.ZERO;
        if (ListUtil.isNotEmpty(saturdayIncomeOpportunityList)) {
            for (UnionOpportunity opportunity : saturdayIncomeOpportunityList) {
                saturdayIncome = BigDecimalUtil.add(saturdayIncome, opportunity.getBrokerageMoney());
            }
        }
        saturdayStatistic.setPaidIncome(saturdayIncome.doubleValue());
        List<UnionOpportunity> saturdayExpenseOpportunityList = filterBetweenTime(paidToMeOpportunityList, beginDate, endDate);
        BigDecimal saturdayExpense = BigDecimal.ZERO;
        if (ListUtil.isNotEmpty(saturdayExpenseOpportunityList)) {
            for (UnionOpportunity opportunity : saturdayExpenseOpportunityList) {
                saturdayExpense = BigDecimalUtil.add(saturdayExpense, opportunity.getBrokerageMoney());
            }
        }
        saturdayStatistic.setPaidExpense(saturdayExpense.doubleValue());
        result.setSaturday(saturdayStatistic);

        // （4-1）周二
        OpportunityStatisticsDay sundayStatistic = new OpportunityStatisticsDay();
        Date sunday = DateUtil.addDays(saturday, 1);
        strMonday = DateUtil.getDateString(sunday, DateUtil.DATE_PATTERN);
        beginDate = DateUtil.parseDate(strMonday + " 00:00:00", DateUtil.DATETIME_PATTERN);
        endDate = DateUtil.parseDate(strMonday + " 23:59:59", DateUtil.DATETIME_PATTERN);
        List<UnionOpportunity> sundayIncomeOpportunityList = filterBetweenTime(paidFromMeOpportunityList, beginDate, endDate);
        BigDecimal sundayIncome = BigDecimal.ZERO;
        if (ListUtil.isNotEmpty(sundayIncomeOpportunityList)) {
            for (UnionOpportunity opportunity : sundayIncomeOpportunityList) {
                sundayIncome = BigDecimalUtil.add(sundayIncome, opportunity.getBrokerageMoney());
            }
        }
        sundayStatistic.setPaidIncome(sundayIncome.doubleValue());
        List<UnionOpportunity> sundayExpenseOpportunityList = filterBetweenTime(paidToMeOpportunityList, beginDate, endDate);
        BigDecimal sundayExpense = BigDecimal.ZERO;
        if (ListUtil.isNotEmpty(sundayExpenseOpportunityList)) {
            for (UnionOpportunity opportunity : sundayExpenseOpportunityList) {
                sundayExpense = BigDecimalUtil.add(sundayExpense, opportunity.getBrokerageMoney());
            }
        }
        sundayStatistic.setPaidExpense(sundayExpense.doubleValue());
        result.setSunday(sundayStatistic);

        return result;
    }

    //***************************************** Domain Driven Design - list ********************************************

    @Override
    public List<OpportunityVO> listToMeByBusId(Integer busId, Integer optUnionId, String optAcceptStatus, String optClientName, String optClientPhone) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // （1）	获取商家所有有效的member
        List<UnionMember> memberList = unionMemberService.listReadByBusId(busId);
        // （2）	根据unionId过滤掉一些member
        List<UnionMember> toMemberList = new ArrayList<>();
        if (optUnionId == null) {
            toMemberList = memberList;
        } else {
            for (UnionMember member : memberList) {
                if (optUnionId.equals(member.getUnionId())) {
                    toMemberList.add(member);
                }
            }
        }
        List<Integer> toMemberIdList = new ArrayList<>();
        if (ListUtil.isNotEmpty(toMemberList)) {
            for (UnionMember member : toMemberList) {
                toMemberIdList.add(member.getId());
            }
        }
        // （3）	按受理状态顺序、创建时间倒序排序
        List<OpportunityVO> result = new ArrayList<>();
        List<Integer> acceptStatusList = null;
        if (StringUtil.isNotEmpty(optAcceptStatus)) {
            acceptStatusList = new ArrayList<>();
            String[] acceptStatusArray = optAcceptStatus.trim().split(";");
            for (String acceptStatus : acceptStatusArray) {
                acceptStatusList.add(Integer.valueOf(acceptStatus.trim()));
            }
        }
        List<UnionOpportunity> opportunityList = listByToMemberIdList(toMemberIdList, acceptStatusList, optClientName, optClientPhone);
        if (ListUtil.isNotEmpty(opportunityList)) {
            for (UnionOpportunity opportunity : opportunityList) {
                OpportunityVO vo = new OpportunityVO();
                vo.setOpportunity(opportunity);

                UnionMain union = unionMainService.getById(opportunity.getUnionId());
                if (!unionMainService.isUnionValid(union)) {
                    continue;
                }
                vo.setUnion(union);

                UnionMember fromMember = unionMemberService.getReadByIdAndUnionId(opportunity.getFromMemberId(), union.getId());
                vo.setFromMember(fromMember);

                UnionMember toMember = unionMemberService.getReadByIdAndUnionId(opportunity.getToMemberId(), union.getId());
                vo.setToMember(toMember);

                result.add(vo);
            }
        }
        Collections.sort(result, new Comparator<OpportunityVO>() {
            @Override
            public int compare(OpportunityVO o1, OpportunityVO o2) {
                int acceptStatusOrder = o1.getOpportunity().getAcceptStatus().compareTo(o2.getOpportunity().getAcceptStatus());
                if (acceptStatusOrder < 0) {
                    return 1;
                }
                if (acceptStatusOrder > 0) {
                    return -1;
                }

                return o1.getOpportunity().getCreateTime().compareTo(o2.getOpportunity().getCreateTime());
            }
        });

        return result;
    }

    @Override
    public List<UnionOpportunity> listByToMemberIdList(List<Integer> toMemberIdList, List<Integer> optAcceptStatusList,
                                                       String optClientName, String optClientPhone) throws Exception {
        if (toMemberIdList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionOpportunity> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.COMMON_NO)
                .in("to_member_id", toMemberIdList);
        if (ListUtil.isNotEmpty(optAcceptStatusList)) {
            entityWrapper.in("accept_status", optAcceptStatusList);
        }
        if (StringUtil.isNotEmpty(optClientName)) {
            entityWrapper.like("client_name", optClientName);
        }
        if (StringUtil.isNotEmpty(optClientPhone)) {
            entityWrapper.like("client_phone", optClientPhone);
        }

        return selectList(entityWrapper);
    }

    @Override
    public List<OpportunityVO> listFromMeByBusId(Integer busId, Integer optUnionId, String optAcceptStatus, String optClientName, String optClientPhone) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // （1）	获取商家所有有效的member
        List<UnionMember> memberList = unionMemberService.listReadByBusId(busId);
        // （2）	根据unionId过滤掉一些member
        List<UnionMember> fromMemberList = new ArrayList<>();
        if (optUnionId == null) {
            fromMemberList = memberList;
        } else {
            for (UnionMember member : memberList) {
                if (optUnionId.equals(member.getUnionId())) {
                    fromMemberList.add(member);
                }
            }
        }
        List<Integer> fromMemberIdList = new ArrayList<>();
        if (ListUtil.isNotEmpty(fromMemberList)) {
            for (UnionMember member : fromMemberList) {
                fromMemberIdList.add(member.getId());
            }
        }
        // （3）	按受理状态顺序、创建时间倒序排序
        List<OpportunityVO> result = new ArrayList<>();
        List<Integer> acceptStatusList = null;
        if (StringUtil.isNotEmpty(optAcceptStatus)) {
            acceptStatusList = new ArrayList<>();
            String[] acceptStatusArray = optAcceptStatus.trim().split(";");
            for (String acceptStatus : acceptStatusArray) {
                acceptStatusList.add(Integer.valueOf(acceptStatus.trim()));
            }
        }
        List<UnionOpportunity> opportunityList = listByFromMemberIdList(fromMemberIdList, acceptStatusList, optClientName, optClientPhone);
        if (ListUtil.isNotEmpty(opportunityList)) {
            for (UnionOpportunity opportunity : opportunityList) {
                OpportunityVO vo = new OpportunityVO();
                vo.setOpportunity(opportunity);

                UnionMain union = unionMainService.getById(opportunity.getUnionId());
                if (!unionMainService.isUnionValid(union)) {
                    continue;
                }
                vo.setUnion(union);

                UnionMember fromMember = unionMemberService.getReadByIdAndUnionId(opportunity.getFromMemberId(), union.getId());
                vo.setFromMember(fromMember);

                UnionMember toMember = unionMemberService.getReadByIdAndUnionId(opportunity.getToMemberId(), union.getId());
                vo.setToMember(toMember);

                result.add(vo);
            }
        }
        Collections.sort(result, new Comparator<OpportunityVO>() {
            @Override
            public int compare(OpportunityVO o1, OpportunityVO o2) {
                int acceptStatusOrder = o1.getOpportunity().getAcceptStatus().compareTo(o2.getOpportunity().getAcceptStatus());
                if (acceptStatusOrder < 0) {
                    return 1;
                }
                if (acceptStatusOrder > 0) {
                    return -1;
                }

                return o1.getOpportunity().getCreateTime().compareTo(o2.getOpportunity().getCreateTime());
            }
        });

        return result;
    }

    @Override
    public List<UnionOpportunity> listByFromMemberIdList(List<Integer> fromMemberIdList, List<Integer> optAcceptStatusList, String optClientName, String optClientPhone) throws Exception {
        if (fromMemberIdList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionOpportunity> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.COMMON_NO)
                .in("from_member_id", fromMemberIdList);
        if (ListUtil.isNotEmpty(optAcceptStatusList)) {
            entityWrapper.in("accept_status", optAcceptStatusList);
        }
        if (StringUtil.isNotEmpty(optClientName)) {
            entityWrapper.like("client_name", optClientName);
        }
        if (StringUtil.isNotEmpty(optClientPhone)) {
            entityWrapper.like("client_phone", optClientPhone);
        }

        return selectList(entityWrapper);
    }

    @Override
    public List<UnionOpportunity> listAcceptConfirmedByToMemberIdAndUnionId(Integer toMemberId, Integer unionId, Integer optIsPay) throws Exception {
        if (toMemberId == null || unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionOpportunity> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.COMMON_NO)
                .eq("accept_status", OpportunityConstant.OPPORTUNITY_ACCEPT_STATUS_CONFIRMED)
                .eq("to_member_id", toMemberId)
                .eq("union_id", unionId);
        if (optIsPay != null) {
            if (CommonConstant.COMMON_YES == optIsPay) {
                entityWrapper.exists(" SELECT p.id FROM t_union_brokerage_pay p "
                        + " WHERE p.del_status=" + CommonConstant.COMMON_NO
                        + " AND p.opportunity_id = t_union_opportunity.id"
                        + " AND p.status=" + BrokerageConstant.PAY_STATUS_SUCCESS);
            } else {
                entityWrapper.notExists(" SELECT p.id FROM t_union_brokerage_pay p "
                        + " WHERE p.del_status=" + CommonConstant.COMMON_NO
                        + " AND p.opportunity_id = t_union_opportunity.id"
                        + " AND p.status=" + BrokerageConstant.PAY_STATUS_SUCCESS);
            }
        }

        return selectList(entityWrapper);
    }

    @Override
    public List<UnionOpportunity> listAcceptConfirmedByFromMemberIdAndUnionId(Integer fromMemberId, Integer unionId, Integer optIsPay) throws Exception {
        if (fromMemberId == null || unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionOpportunity> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.COMMON_NO)
                .eq("accept_status", OpportunityConstant.OPPORTUNITY_ACCEPT_STATUS_CONFIRMED)
                .eq("from_member_id", fromMemberId)
                .eq("union_id", unionId);
        if (optIsPay != null) {
            if (CommonConstant.COMMON_YES == optIsPay) {
                entityWrapper.exists(" SELECT p.id FROM t_union_brokerage_pay p "
                        + " WHERE p.del_status=" + CommonConstant.COMMON_NO
                        + " AND p.opportunity_id = t_union_opportunity.id"
                        + " AND p.status=" + BrokerageConstant.PAY_STATUS_SUCCESS);
            } else {
                entityWrapper.notExists(" SELECT p.id FROM t_union_brokerage_pay p "
                        + " WHERE p.del_status=" + CommonConstant.COMMON_NO
                        + " AND p.opportunity_id = t_union_opportunity.id"
                        + " AND p.status=" + BrokerageConstant.PAY_STATUS_SUCCESS);
            }
        }

        return selectList(entityWrapper);
    }

    //***************************************** Domain Driven Design - save ********************************************

    @Override
    public void saveOpportunityVOByBusIdAndUnionId(Integer busId, Integer unionId, OpportunityVO vo) throws Exception {
        if (busId == null || unionId == null || vo == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // （1）	判断union有效性和member写权限
        if (!unionMainService.isUnionValid(unionId)) {
            throw new BusinessException(CommonConstant.UNION_INVALID);
        }
        UnionMember member = unionMemberService.getWriteByBusIdAndUnionId(busId, unionId);
        if (member == null) {
            throw new BusinessException(CommonConstant.UNION_WRITE_REJECT);
        }
        // （2）	校验表单数据
        UnionOpportunity saveOpportunity = new UnionOpportunity();
        saveOpportunity.setDelStatus(CommonConstant.DEL_STATUS_NO);
        saveOpportunity.setCreateTime(DateUtil.getCurrentDate());
        saveOpportunity.setAcceptStatus(OpportunityConstant.OPPORTUNITY_ACCEPT_STATUS_CONFIRMING);

        Integer toMemberId = vo.getToMember() != null ? vo.getToMember().getId() : null;
        if (toMemberId == null) {
            throw new BusinessException("商机接受者不能为空");
        }
        UnionMember toMember = unionMemberService.getWriteByIdAndUnionId(toMemberId, unionId);
        if (toMember == null) {
            throw new BusinessException("找不到商机接受者信息");
        }
        saveOpportunity.setToMemberId(toMemberId);

        String clientName = vo.getOpportunity().getClientName();
        if (StringUtil.isEmpty(clientName)) {
            throw new BusinessException("客户名称不能为空");
        }
        saveOpportunity.setClientName(clientName);

        String clientPhone = vo.getOpportunity().getClientPhone();
        if (StringUtil.isEmpty(clientPhone)) {
            throw new BusinessException("客户电话不能为空");
        }
        if (!StringUtil.isPhone(clientPhone)) {
            throw new BusinessException("客户电话格式有误");
        }
        saveOpportunity.setClientPhone(clientPhone);

        String businessMsg = vo.getOpportunity().getBusinessMsg();
        if (StringUtil.isEmpty(businessMsg)) {
            throw new BusinessException("业务备注不能为空");
        }
        saveOpportunity.setBusinessMsg(businessMsg);

        save(saveOpportunity);
    }

    //***************************************** Domain Driven Design - remove ******************************************

    //***************************************** Domain Driven Design - update ******************************************

    @Override
    public void updateStatusByIdAndUnionIdAndBusId(Integer opportunityId, Integer unionId, Integer busId, Integer isAccept, Double acceptPrice) throws Exception {
        if (opportunityId == null || unionId == null || busId == null || isAccept == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // （1）	判断union有效性和member写权限
        if (!unionMainService.isUnionValid(unionId)) {
            throw new BusinessException(CommonConstant.UNION_INVALID);
        }
        UnionMember member = unionMemberService.getWriteByBusIdAndUnionId(busId, unionId);
        if (member == null) {
            throw new BusinessException(CommonConstant.UNION_WRITE_REJECT);
        }
        // （2）	判断opportunityId有效性
        UnionOpportunity opportunity = getByIdAndUnionIdAndToMemberId(opportunityId, unionId, member.getId());
        if (opportunity == null) {
            throw new BusinessException("找不到商机信息");
        }
        // （3）	要求opportunity为未处理状态，否则，报错
        if (OpportunityConstant.OPPORTUNITY_ACCEPT_STATUS_CONFIRMING != opportunity.getAcceptStatus()) {
            throw new BusinessException("商机已处理");
        }
        // （4）	接受时受理金额不能为空，且需大于0
        UnionOpportunity updateOpportunity = new UnionOpportunity();
        if (CommonConstant.COMMON_YES == isAccept) {
            if (acceptPrice == null || acceptPrice <= 0) {
                throw new BusinessException("接受商机时受理金额不能为空，且必须大于0");
            }
            UnionOpportunityRatio ratio = opportunityRatioService.getByFromMemberIdAndToMemberIdAndUnionId(member.getId(), opportunity.getFromMemberId(), unionId);
            if (ratio == null) {
                throw new BusinessException("没有对商机推荐者设置佣金比例");
            }
            updateOpportunity.setId(opportunityId);
            updateOpportunity.setAcceptStatus(OpportunityConstant.OPPORTUNITY_ACCEPT_STATUS_CONFIRMED);
            updateOpportunity.setAcceptPrice(acceptPrice);
            BigDecimal brokerageRatio = BigDecimalUtil.divide(ratio.getRatio(), Double.valueOf(100));
            updateOpportunity.setBrokerageMoney(BigDecimalUtil.multiply(acceptPrice, brokerageRatio).doubleValue());
        } else {
            updateOpportunity.setId(opportunityId);
            updateOpportunity.setAcceptStatus(OpportunityConstant.OPPORTUNITY_ACCEPT_STATUS_REJECT);
        }

        update(updateOpportunity);
    }

    //***************************************** Domain Driven Design - count *******************************************

    //***************************************** Domain Driven Design - boolean *****************************************

    //***************************************** Domain Driven Design - filter ******************************************

    @Override
    public List<UnionOpportunity> filterBetweenTime(List<UnionOpportunity> opportunityList, Date optBeginTime, Date optEndTime) throws Exception {
        if (opportunityList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionOpportunity> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(opportunityList)) {
            for (UnionOpportunity opportunity : opportunityList) {
                if (optBeginTime != null && optBeginTime.compareTo(opportunity.getCreateTime()) > 0) {
                    continue;
                }
                if (optEndTime != null && optEndTime.compareTo(opportunity.getCreateTime()) < 0) {
                    continue;
                }
                result.add(opportunity);
            }
        }

        return result;
    }

    //***************************************** Object As a Service - get **********************************************

    @Override
    public UnionOpportunity getById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionOpportunity result;
        // (1)cache
        String idKey = UnionOpportunityCacheUtil.getIdKey(id);
        if (redisCacheUtil.exists(idKey)) {
            String tempStr = redisCacheUtil.get(idKey);
            result = JSONArray.parseObject(tempStr, UnionOpportunity.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionOpportunity> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("id", id)
                .eq("del_status", CommonConstant.DEL_STATUS_NO);
        result = selectOne(entityWrapper);
        setCache(result, id);
        return result;
    }

    //***************************************** Object As a Service - list *********************************************

    public List<UnionOpportunity> listByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionOpportunity> result;
        // (1)cache
        String unionIdKey = UnionOpportunityCacheUtil.getUnionIdKey(unionId);
        if (redisCacheUtil.exists(unionIdKey)) {
            String tempStr = redisCacheUtil.get(unionIdKey);
            result = JSONArray.parseArray(tempStr, UnionOpportunity.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionOpportunity> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("union_id", unionId)
                .eq("del_status", CommonConstant.COMMON_NO);
        result = selectList(entityWrapper);
        setCache(result, unionId, UnionOpportunityCacheUtil.TYPE_UNION_ID);
        return result;
    }

    public List<UnionOpportunity> listByFromMemberId(Integer fromMemberId) throws Exception {
        if (fromMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionOpportunity> result;
        // (1)cache
        String fromMemberIdKey = UnionOpportunityCacheUtil.getFromMemberIdKey(fromMemberId);
        if (redisCacheUtil.exists(fromMemberIdKey)) {
            String tempStr = redisCacheUtil.get(fromMemberIdKey);
            result = JSONArray.parseArray(tempStr, UnionOpportunity.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionOpportunity> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("from_member_id", fromMemberId)
                .eq("del_status", CommonConstant.COMMON_NO);
        result = selectList(entityWrapper);
        setCache(result, fromMemberId, UnionOpportunityCacheUtil.TYPE_FROM_MEMBER_ID);
        return result;
    }

    public List<UnionOpportunity> listByToMemberId(Integer toMemberId) throws Exception {
        if (toMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionOpportunity> result;
        // (1)cache
        String toMemberIdKey = UnionOpportunityCacheUtil.getToMemberIdKey(toMemberId);
        if (redisCacheUtil.exists(toMemberIdKey)) {
            String tempStr = redisCacheUtil.get(toMemberIdKey);
            result = JSONArray.parseArray(tempStr, UnionOpportunity.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionOpportunity> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("to_member_id", toMemberId)
                .eq("del_status", CommonConstant.COMMON_NO);
        result = selectList(entityWrapper);
        setCache(result, toMemberId, UnionOpportunityCacheUtil.TYPE_TO_MEMBER_ID);
        return result;
    }

    //***************************************** Object As a Service - save *********************************************

    @Transactional(rollbackFor = Exception.class)
    public void save(UnionOpportunity newUnionOpportunity) throws Exception {
        if (newUnionOpportunity == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        insert(newUnionOpportunity);
        removeCache(newUnionOpportunity);
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveBatch(List<UnionOpportunity> newUnionOpportunityList) throws Exception {
        if (newUnionOpportunityList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        insertBatch(newUnionOpportunityList);
        removeCache(newUnionOpportunityList);
    }

    //***************************************** Object As a Service - remove *******************************************

    @Transactional(rollbackFor = Exception.class)
    public void removeById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        UnionOpportunity unionOpportunity = getById(id);
        removeCache(unionOpportunity);
        // (2)remove in db logically
        UnionOpportunity removeUnionOpportunity = new UnionOpportunity();
        removeUnionOpportunity.setId(id);
        removeUnionOpportunity.setDelStatus(CommonConstant.DEL_STATUS_YES);
        updateById(removeUnionOpportunity);
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeBatchById(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<UnionOpportunity> unionOpportunityList = new ArrayList<>();
        for (Integer id : idList) {
            UnionOpportunity unionOpportunity = getById(id);
            unionOpportunityList.add(unionOpportunity);
        }
        removeCache(unionOpportunityList);
        // (2)remove in db logically
        List<UnionOpportunity> removeUnionOpportunityList = new ArrayList<>();
        for (Integer id : idList) {
            UnionOpportunity removeUnionOpportunity = new UnionOpportunity();
            removeUnionOpportunity.setId(id);
            removeUnionOpportunity.setDelStatus(CommonConstant.DEL_STATUS_YES);
            removeUnionOpportunityList.add(removeUnionOpportunity);
        }
        updateBatchById(removeUnionOpportunityList);
    }

    //***************************************** Object As a Service - update *******************************************

    @Transactional(rollbackFor = Exception.class)
    public void update(UnionOpportunity updateUnionOpportunity) throws Exception {
        if (updateUnionOpportunity == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        Integer id = updateUnionOpportunity.getId();
        UnionOpportunity unionOpportunity = getById(id);
        removeCache(unionOpportunity);
        // (2)update db
        updateById(updateUnionOpportunity);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateBatch(List<UnionOpportunity> updateUnionOpportunityList) throws Exception {
        if (updateUnionOpportunityList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<Integer> idList = new ArrayList<>();
        for (UnionOpportunity updateUnionOpportunity : updateUnionOpportunityList) {
            idList.add(updateUnionOpportunity.getId());
        }
        List<UnionOpportunity> unionOpportunityList = new ArrayList<>();
        for (Integer id : idList) {
            UnionOpportunity unionOpportunity = getById(id);
            unionOpportunityList.add(unionOpportunity);
        }
        removeCache(unionOpportunityList);
        // (2)update db
        updateBatchById(updateUnionOpportunityList);
    }

    //***************************************** Object As a Service - cache support ************************************

    private void setCache(UnionOpportunity newUnionOpportunity, Integer id) {
        if (id == null) {
            //do nothing,just in case
            return;
        }
        String idKey = UnionOpportunityCacheUtil.getIdKey(id);
        redisCacheUtil.set(idKey, newUnionOpportunity);
    }

    private void setCache(List<UnionOpportunity> newUnionOpportunityList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            //do nothing,just in case
            return;
        }
        String foreignIdKey = null;
        switch (foreignIdType) {
            case UnionOpportunityCacheUtil.TYPE_UNION_ID:
                foreignIdKey = UnionOpportunityCacheUtil.getUnionIdKey(foreignId);
                break;
            case UnionOpportunityCacheUtil.TYPE_FROM_MEMBER_ID:
                foreignIdKey = UnionOpportunityCacheUtil.getFromMemberIdKey(foreignId);
                break;
            case UnionOpportunityCacheUtil.TYPE_TO_MEMBER_ID:
                foreignIdKey = UnionOpportunityCacheUtil.getToMemberIdKey(foreignId);
                break;
            default:
                break;
        }
        if (foreignIdKey != null) {
            redisCacheUtil.set(foreignIdKey, newUnionOpportunityList);
        }
    }

    private void removeCache(UnionOpportunity unionOpportunity) {
        if (unionOpportunity == null) {
            return;
        }
        Integer id = unionOpportunity.getId();
        String idKey = UnionOpportunityCacheUtil.getIdKey(id);
        redisCacheUtil.remove(idKey);

        Integer unionId = unionOpportunity.getUnionId();
        if (unionId != null) {
            String unionIdKey = UnionOpportunityCacheUtil.getUnionIdKey(unionId);
            redisCacheUtil.remove(unionIdKey);
        }

        Integer fromMemberId = unionOpportunity.getFromMemberId();
        if (fromMemberId != null) {
            String fromMemberIdKey = UnionOpportunityCacheUtil.getFromMemberIdKey(fromMemberId);
            redisCacheUtil.remove(fromMemberIdKey);
        }

        Integer toMemberId = unionOpportunity.getToMemberId();
        if (toMemberId != null) {
            String toMemberIdKey = UnionOpportunityCacheUtil.getToMemberIdKey(toMemberId);
            redisCacheUtil.remove(toMemberIdKey);
        }
    }

    private void removeCache(List<UnionOpportunity> unionOpportunityList) {
        if (ListUtil.isEmpty(unionOpportunityList)) {
            return;
        }
        List<Integer> idList = new ArrayList<>();
        for (UnionOpportunity unionOpportunity : unionOpportunityList) {
            idList.add(unionOpportunity.getId());
        }
        List<String> idKeyList = UnionOpportunityCacheUtil.getIdKey(idList);
        redisCacheUtil.remove(idKeyList);

        List<String> unionIdKeyList = getForeignIdKeyList(unionOpportunityList, UnionOpportunityCacheUtil.TYPE_UNION_ID);
        if (ListUtil.isNotEmpty(unionIdKeyList)) {
            redisCacheUtil.remove(unionIdKeyList);
        }

        List<String> fromMemberIdKeyList = getForeignIdKeyList(unionOpportunityList, UnionOpportunityCacheUtil.TYPE_FROM_MEMBER_ID);
        if (ListUtil.isNotEmpty(fromMemberIdKeyList)) {
            redisCacheUtil.remove(fromMemberIdKeyList);
        }

        List<String> toMemberIdKeyList = getForeignIdKeyList(unionOpportunityList, UnionOpportunityCacheUtil.TYPE_TO_MEMBER_ID);
        if (ListUtil.isNotEmpty(toMemberIdKeyList)) {
            redisCacheUtil.remove(toMemberIdKeyList);
        }
    }

    private List<String> getForeignIdKeyList(List<UnionOpportunity> unionOpportunityList, int foreignIdType) {
        List<String> result = new ArrayList<>();
        switch (foreignIdType) {
            case UnionOpportunityCacheUtil.TYPE_UNION_ID:
                for (UnionOpportunity unionOpportunity : unionOpportunityList) {
                    Integer unionId = unionOpportunity.getUnionId();
                    if (unionId != null) {
                        String unionIdKey = UnionOpportunityCacheUtil.getUnionIdKey(unionId);
                        result.add(unionIdKey);
                    }
                }
                break;
            case UnionOpportunityCacheUtil.TYPE_FROM_MEMBER_ID:
                for (UnionOpportunity unionOpportunity : unionOpportunityList) {
                    Integer fromMemberId = unionOpportunity.getFromMemberId();
                    if (fromMemberId != null) {
                        String fromMemberIdKey = UnionOpportunityCacheUtil.getFromMemberIdKey(fromMemberId);
                        result.add(fromMemberIdKey);
                    }
                }
                break;
            case UnionOpportunityCacheUtil.TYPE_TO_MEMBER_ID:
                for (UnionOpportunity unionOpportunity : unionOpportunityList) {
                    Integer toMemberId = unionOpportunity.getToMemberId();
                    if (toMemberId != null) {
                        String toMemberIdKey = UnionOpportunityCacheUtil.getToMemberIdKey(toMemberId);
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