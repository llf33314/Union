package com.gt.union.opportunity.main.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.api.amqp.entity.PhoneMessage;
import com.gt.union.api.amqp.sender.PhoneMessageSender;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.*;
import com.gt.union.opportunity.brokerage.vo.BrokerageOpportunityVO;
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

    @Autowired
    private PhoneMessageSender phoneMessageSender;

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
        // （2）	获取已接受的我推荐的商机，区分是否已支付
        OpportunityStatisticsVO result = new OpportunityStatisticsVO();

        List<UnionOpportunity> incomeOpportunityList = listByUnionIdAndFromMemberIdAndAcceptStatus(unionId, member.getId(), OpportunityConstant.ACCEPT_STATUS_CONFIRMED);
        List<UnionOpportunity> paidIncomeOpportunityList = filterByIsClose(incomeOpportunityList, OpportunityConstant.IS_CLOSE_YES);
        BigDecimal paidIncome = BigDecimal.ZERO;
        if (ListUtil.isNotEmpty(paidIncomeOpportunityList)) {
            for (UnionOpportunity opportunity : paidIncomeOpportunityList) {
                paidIncome = BigDecimalUtil.add(paidIncome, opportunity.getBrokerageMoney());
            }
        }
        result.setPaidIncome(paidIncome.doubleValue());

        List<UnionOpportunity> unPaidIncomeOpportunityList = filterByIsClose(incomeOpportunityList, OpportunityConstant.IS_CLOSE_NO);
        BigDecimal unPaidIncome = BigDecimal.ZERO;
        if (ListUtil.isNotEmpty(unPaidIncomeOpportunityList)) {
            for (UnionOpportunity opportunity : unPaidIncomeOpportunityList) {
                unPaidIncome = BigDecimalUtil.add(unPaidIncome, opportunity.getBrokerageMoney());
            }
        }
        result.setUnPaidIncome(unPaidIncome.doubleValue());

        BigDecimal incomeSum = BigDecimalUtil.add(paidIncome, unPaidIncome);
        result.setIncomeSum(incomeSum.doubleValue());
        // （3）	获取已接受的推荐给我的商机，区分是否已支付
        List<UnionOpportunity> expenseOpportunityList = listByUnionIdAndToMemberIdAndAcceptStatus(unionId, member.getId(), OpportunityConstant.ACCEPT_STATUS_CONFIRMED);
        List<UnionOpportunity> paidExpenseOpportunityList = filterByIsClose(expenseOpportunityList, OpportunityConstant.IS_CLOSE_YES);
        BigDecimal paidExpense = BigDecimal.ZERO;
        if (ListUtil.isNotEmpty(paidExpenseOpportunityList)) {
            for (UnionOpportunity opportunity : paidExpenseOpportunityList) {
                paidExpense = BigDecimalUtil.add(paidExpense, opportunity.getBrokerageMoney());
            }
        }
        result.setPaidExpense(paidExpense.doubleValue());

        List<UnionOpportunity> unPaidExpenseOpportunityList = filterByIsClose(expenseOpportunityList, OpportunityConstant.IS_CLOSE_NO);
        BigDecimal unPaidExpense = BigDecimal.ZERO;
        if (ListUtil.isNotEmpty(unPaidExpenseOpportunityList)) {
            for (UnionOpportunity opportunity : unPaidExpenseOpportunityList) {
                unPaidExpense = BigDecimalUtil.add(unPaidExpense, opportunity.getBrokerageMoney());
            }
        }
        result.setUnPaidExpense(unPaidExpense.doubleValue());

        BigDecimal expenseSum = BigDecimalUtil.add(paidExpense, unPaidExpense);
        result.setExpenseSum(expenseSum.doubleValue());
        // （4）	获取一周内商机收支信息
        Date indexDay = DateUtil.getMondayInWeek();
        String strIndexDay = DateUtil.getDateString(indexDay, DateUtil.DATE_PATTERN);
        for (int i = 0; i < 7; i++) {
            OpportunityStatisticsDay dayStatistic = new OpportunityStatisticsDay();
            String strDay = DateUtil.getDateString(indexDay, DateUtil.DATE_PATTERN);
            Date beginDate = DateUtil.parseDate(strIndexDay + " 00:00:00", DateUtil.DATETIME_PATTERN);
            Date endDate = DateUtil.parseDate(strIndexDay + " 23:59:59", DateUtil.DATETIME_PATTERN);
            List<UnionOpportunity> dayIncomeOpportunityList = filterBetweenTime(paidIncomeOpportunityList, beginDate, endDate);
            BigDecimal dayIncome = BigDecimal.ZERO;
            if (ListUtil.isNotEmpty(dayIncomeOpportunityList)) {
                for (UnionOpportunity opportunity : dayIncomeOpportunityList) {
                    dayIncome = BigDecimalUtil.add(dayIncome, opportunity.getBrokerageMoney());
                }
            }
            dayStatistic.setPaidIncome(dayIncome.doubleValue());
            List<UnionOpportunity> dayExpenseOpportunityList = filterBetweenTime(paidExpenseOpportunityList, beginDate, endDate);
            BigDecimal dayExpense = BigDecimal.ZERO;
            if (ListUtil.isNotEmpty(dayExpenseOpportunityList)) {
                for (UnionOpportunity opportunity : dayExpenseOpportunityList) {
                    dayExpense = BigDecimalUtil.add(dayExpense, opportunity.getBrokerageMoney());
                }
            }
            dayStatistic.setPaidExpense(dayExpense.doubleValue());
            switch (i) {
                case 0:
                    result.setMonday(dayStatistic);
                    break;
                case 1:
                    result.setTuesday(dayStatistic);
                    break;
                case 2:
                    result.setWednesday(dayStatistic);
                    break;
                case 3:
                    result.setThursday(dayStatistic);
                    break;
                case 4:
                    result.setFriday(dayStatistic);
                    break;
                case 5:
                    result.setSaturday(dayStatistic);
                    break;
                case 6:
                    result.setSunday(dayStatistic);
                    break;
                default:
                    break;
            }
            indexDay = DateUtil.addDays(indexDay, 1);
            strIndexDay = DateUtil.getDateString(indexDay, DateUtil.DATE_PATTERN);
        }

        return result;
    }

    @Override
    public List<Integer> getIdList(List<UnionOpportunity> opportunityList) throws Exception {
        if (opportunityList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<Integer> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(opportunityList)) {
            for (UnionOpportunity opportunity : opportunityList) {
                result.add(opportunity.getId());
            }
        }

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
        List<Integer> toMemberIdList = unionMemberService.getIdList(toMemberList);
        // （3）根据查询条件进行过滤
        List<UnionOpportunity> opportunityList = listByToMemberIdList(toMemberIdList);
        List<Integer> acceptStatusList = getAcceptStatusList(optAcceptStatus);
        if (ListUtil.isNotEmpty(acceptStatusList)) {
            opportunityList = filterByAcceptStatusList(opportunityList, acceptStatusList);
        }
        if (StringUtil.isNotEmpty(optClientName)) {
            opportunityList = filterByLikeClientName(opportunityList, optClientName);
        }
        if (StringUtil.isNotEmpty(optClientPhone)) {
            opportunityList = filterByLikeClientPhone(opportunityList, optClientPhone);
        }
        // （3）	按受理状态顺序、创建时间倒序排序
        List<OpportunityVO> result = getOpportunityVOList(opportunityList);
        sortByAcceptStatus(result);

        return result;
    }

    private List<OpportunityVO> getOpportunityVOList(List<UnionOpportunity> opportunityList) throws Exception {
        List<OpportunityVO> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(opportunityList)) {
            for (UnionOpportunity opportunity : opportunityList) {
                OpportunityVO vo = new OpportunityVO();
                vo.setOpportunity(opportunity);

                UnionMain union = unionMainService.getById(opportunity.getUnionId());
                if (!unionMainService.isUnionValid(union)) {
                    continue;
                }
                vo.setUnion(union);

                UnionMember fromMember = unionMemberService.getByIdAndUnionId(opportunity.getFromMemberId(), union.getId());
                vo.setFromMember(fromMember);

                UnionMember toMember = unionMemberService.getByIdAndUnionId(opportunity.getToMemberId(), union.getId());
                vo.setToMember(toMember);

                result.add(vo);
            }
        }
        return result;
    }

    private void sortByAcceptStatus(List<OpportunityVO> result) {
        Collections.sort(result, new Comparator<OpportunityVO>() {
            @Override
            public int compare(OpportunityVO o1, OpportunityVO o2) {
                int acceptStatusOrder = o1.getOpportunity().getAcceptStatus().compareTo(o2.getOpportunity().getAcceptStatus());
                if (acceptStatusOrder < 0) {
                    return -1;
                }
                if (acceptStatusOrder > 0) {
                    return 1;
                }

                return o2.getOpportunity().getCreateTime().compareTo(o1.getOpportunity().getCreateTime());
            }
        });
    }

    @Override
    public List<UnionOpportunity> listByToMemberIdList(List<Integer> toMemberIdList) throws Exception {
        if (toMemberIdList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionOpportunity> result = new ArrayList<>();
        for (Integer toMemberId : toMemberIdList) {
            result.addAll(listByToMemberId(toMemberId));
        }

        return result;
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
        List<Integer> fromMemberIdList = unionMemberService.getIdList(fromMemberList);
        // （3）根据查询条件进行过滤
        List<UnionOpportunity> opportunityList = listByFromMemberIdList(fromMemberIdList);
        List<Integer> acceptStatusList = getAcceptStatusList(optAcceptStatus);
        if (ListUtil.isNotEmpty(acceptStatusList)) {
            opportunityList = filterByAcceptStatusList(opportunityList, acceptStatusList);
        }
        if (StringUtil.isNotEmpty(optClientName)) {
            opportunityList = filterByLikeClientName(opportunityList, optClientName);
        }
        if (StringUtil.isNotEmpty(optClientPhone)) {
            opportunityList = filterByLikeClientPhone(opportunityList, optClientPhone);
        }
        // （4）	按受理状态顺序、创建时间倒序排序
        List<OpportunityVO> result = getOpportunityVOList(opportunityList);
        sortByAcceptStatus(result);

        return result;
    }

    private List<Integer> getAcceptStatusList(String optAcceptStatus) {
        List<Integer> result = null;
        if (StringUtil.isNotEmpty(optAcceptStatus)) {
            result = new ArrayList<>();
            String[] acceptStatusArray = optAcceptStatus.trim().split(";");
            for (String acceptStatus : acceptStatusArray) {
                result.add(Integer.valueOf(acceptStatus.trim()));
            }
        }
        return result;
    }

    @Override
    public List<UnionOpportunity> listByFromMemberIdList(List<Integer> fromMemberIdList) throws Exception {
        if (fromMemberIdList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionOpportunity> result = new ArrayList<>();
        for (Integer fromMemberId : fromMemberIdList) {
            result.addAll(listByFromMemberId(fromMemberId));
        }

        return result;
    }

    @Override
    public List<UnionOpportunity> listByUnionIdAndToMemberIdAndAcceptStatus(Integer unionId, Integer toMemberId, Integer acceptStatus) throws Exception {
        if (unionId == null || toMemberId == null || acceptStatus == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionOpportunity> result = listByToMemberId(toMemberId);
        result = filterByUnionId(result, unionId);
        result = filterByAcceptStatus(result, acceptStatus);

        return result;
    }

    @Override
    public List<UnionOpportunity> listByUnionIdAndToMemberIdAndAcceptStatusAndIsClose(Integer unionId, Integer toMemberId, Integer acceptStatus, Integer isClose) throws Exception {
        if (unionId == null || toMemberId == null || acceptStatus == null || isClose == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionOpportunity> result = listByUnionIdAndToMemberIdAndAcceptStatus(unionId, toMemberId, acceptStatus);
        result = filterByIsClose(result, isClose);

        return result;
    }

    @Override
    public List<UnionOpportunity> listByUnionIdAndFromMemberIdAndAcceptStatus(Integer unionId, Integer fromMemberId, Integer acceptStatus) throws Exception {
        if (unionId == null || fromMemberId == null || acceptStatus == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionOpportunity> result = listByFromMemberId(fromMemberId);
        result = filterByUnionId(result, unionId);
        result = filterByAcceptStatus(result, acceptStatus);

        return result;
    }

    @Override
    public List<BrokerageOpportunityVO> listBrokerageOpportunityVO(List<UnionOpportunity> opportunityList) throws Exception {
        if (opportunityList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<BrokerageOpportunityVO> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(opportunityList)) {
            for (UnionOpportunity opportunity : opportunityList) {
                BrokerageOpportunityVO vo = new BrokerageOpportunityVO();
                vo.setOpportunity(opportunity);

                vo.setFromMember(unionMemberService.getReadByIdAndUnionId(opportunity.getFromMemberId(), opportunity.getUnionId()));

                vo.setToMember(unionMemberService.getReadByIdAndUnionId(opportunity.getToMemberId(), opportunity.getUnionId()));

                vo.setUnion(unionMainService.getById(opportunity.getUnionId()));

                result.add(vo);
            }
        }
        Collections.sort(result, new Comparator<BrokerageOpportunityVO>() {
            @Override
            public int compare(BrokerageOpportunityVO o1, BrokerageOpportunityVO o2) {
                int c = o1.getOpportunity().getIsClose().compareTo(o2.getOpportunity().getIsClose());
                if (c < 0) {
                    return -1;
                } else if (c > 0) {
                    return 1;
                }
                return o2.getOpportunity().getCreateTime().compareTo(o1.getOpportunity().getCreateTime());
            }
        });

        return result;
    }

    @Override
    public List<UnionOpportunity> listByFromMemberIdListAndAcceptStatusAndIsClose(List<Integer> fromMemberIdList, Integer acceptStatus, Integer isClose) throws Exception {
        if (fromMemberIdList == null || acceptStatus == null || isClose == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionOpportunity> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(fromMemberIdList)) {
            result = listByFromMemberIdList(fromMemberIdList);
            result = filterByAcceptStatus(result, acceptStatus);
            result = filterByIsClose(result, isClose);
        }

        return result;
    }

    @Override
    public List<UnionOpportunity> listByToMemberIdListAndAcceptStatusAndIsClose(List<Integer> toMemberIdList, Integer acceptStatus, Integer isClose) throws Exception {
        if (toMemberIdList == null || acceptStatus == null || isClose == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionOpportunity> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(toMemberIdList)) {
            result = listByToMemberIdList(toMemberIdList);
            result = filterByAcceptStatus(result, acceptStatus);
            result = filterByIsClose(result, isClose);
        }

        return result;
    }


    //***************************************** Domain Driven Design - save ********************************************

    @Override
    public void saveOpportunityVOByBusIdAndUnionId(Integer busId, Integer unionId, OpportunityVO vo) throws Exception {
        if (busId == null || unionId == null || vo == null) {
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
        // （2）	校验表单数据
        UnionOpportunity saveOpportunity = new UnionOpportunity();
        saveOpportunity.setDelStatus(CommonConstant.DEL_STATUS_NO);
        saveOpportunity.setCreateTime(DateUtil.getCurrentDate());
        saveOpportunity.setAcceptStatus(OpportunityConstant.ACCEPT_STATUS_CONFIRMING);
        saveOpportunity.setIsClose(OpportunityConstant.IS_CLOSE_NO);
        saveOpportunity.setFromMemberId(member.getId());
        saveOpportunity.setType(OpportunityConstant.TYPE_OFFLINE);
        saveOpportunity.setUnionId(unionId);

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
        if (StringUtil.getStringLength(clientName) > 10) {
            throw new BusinessException("客户名称字段长度不能超过10");
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

        // （3）发送短信通知
        String phone = StringUtil.isNotEmpty(toMember.getNotifyPhone()) ? toMember.getNotifyPhone() : toMember.getDirectorPhone();
        String content = "\"" + union.getName() + "\"的盟员\""
                + member.getEnterpriseName() + "\"为你推荐了客户，请到商机消息处查看。客户信息："
                + clientName + "，" + clientPhone + "，" + businessMsg;
        phoneMessageSender.sendMsg(new PhoneMessage(toMember.getBusId(), phone, content));
    }

    //***************************************** Domain Driven Design - remove ******************************************

    //***************************************** Domain Driven Design - update ******************************************

    @Override
    public void updateStatusByBusIdAndIdAndUnionId(Integer busId, Integer opportunityId, Integer unionId, Integer isAccept, Double acceptPrice) throws Exception {
        if (opportunityId == null || unionId == null || busId == null || isAccept == null) {
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
        // （2）	判断opportunityId有效性
        UnionOpportunity opportunity = getByIdAndUnionIdAndToMemberId(opportunityId, unionId, member.getId());
        if (opportunity == null) {
            throw new BusinessException("找不到商机信息");
        }
        // （3）	要求opportunity为未处理状态
        if (OpportunityConstant.ACCEPT_STATUS_CONFIRMING != opportunity.getAcceptStatus()) {
            throw new BusinessException("商机已处理");
        }
        // （4）	接受时受理金额不能为空，且需大于0
        UnionOpportunity updateOpportunity = new UnionOpportunity();
        if (CommonConstant.COMMON_YES == isAccept) {
            if (acceptPrice == null || acceptPrice <= 0) {
                throw new BusinessException("接受商机时受理金额不能为空，且必须大于0");
            }
            UnionOpportunityRatio ratio = opportunityRatioService.getByUnionIdAndFromMemberIdAndToMemberId(unionId, member.getId(), opportunity.getFromMemberId());
            if (ratio == null) {
                throw new BusinessException("没有对商机推荐者设置佣金比例");
            }
            updateOpportunity.setId(opportunityId);
            updateOpportunity.setAcceptStatus(OpportunityConstant.ACCEPT_STATUS_CONFIRMED);
            updateOpportunity.setAcceptPrice(acceptPrice);
            updateOpportunity.setBrokerageMoney(BigDecimalUtil.multiply(acceptPrice, ratio.getRatio()).doubleValue());
        } else {
            updateOpportunity.setId(opportunityId);
            updateOpportunity.setAcceptStatus(OpportunityConstant.ACCEPT_STATUS_REJECT);
        }

        update(updateOpportunity);
        // （5）发送短信通知
        UnionMember fromMember = unionMemberService.getReadByIdAndUnionId(opportunity.getFromMemberId(), unionId);
        if (fromMember != null) {
            String phone = StringUtil.isNotEmpty(fromMember.getNotifyPhone()) ? fromMember.getNotifyPhone() : fromMember.getDirectorPhone();
            String content = "\"" + union.getName() + "\"的盟员\""
                    + member.getEnterpriseName() + "\""
                    + (CommonConstant.COMMON_YES == isAccept ? "已接受" : "已拒绝")
                    + "了您推荐的商机消息。客户信息："
                    + opportunity.getClientName() + "，"
                    + opportunity.getClientPhone() + "，"
                    + opportunity.getBusinessMsg();
            phoneMessageSender.sendMsg(new PhoneMessage(fromMember.getBusId(), phone, content));
        }
    }

    //***************************************** Domain Driven Design - count *******************************************

    @Override
    public Double sumBrokerageMoneyByFromMemberIdListAndAcceptStatusAndIsClose(List<Integer> fromMemberIdList, Integer acceptStatus, Integer isClose) throws Exception {
        if (fromMemberIdList == null || acceptStatus == null || isClose == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        BigDecimal result = BigDecimal.ZERO;
        List<UnionOpportunity> opportunityList = listByFromMemberIdListAndAcceptStatusAndIsClose(fromMemberIdList, acceptStatus, isClose);
        if (ListUtil.isNotEmpty(opportunityList)) {
            for (UnionOpportunity opportunity : opportunityList) {
                result = BigDecimalUtil.add(result, opportunity.getBrokerageMoney());
            }
        }

        return result.doubleValue();
    }

    @Override
    public Double sumBrokerageMoneyByToMemberIdListAndAcceptStatusAndIsClose(List<Integer> toMemberIdList, Integer acceptStatus, Integer isClose) throws Exception {
        if (toMemberIdList == null || acceptStatus == null || isClose == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        BigDecimal result = BigDecimal.ZERO;
        List<UnionOpportunity> opportunityList = listByToMemberIdListAndAcceptStatusAndIsClose(toMemberIdList, acceptStatus, isClose);
        if (ListUtil.isNotEmpty(opportunityList)) {
            for (UnionOpportunity opportunity : opportunityList) {
                result = BigDecimalUtil.add(result, opportunity.getBrokerageMoney());
            }
        }

        return result.doubleValue();
    }

    //***************************************** Domain Driven Design - boolean *****************************************

    //***************************************** Domain Driven Design - filter ******************************************

    @Override
    public List<UnionOpportunity> filterByUnionId(List<UnionOpportunity> opportunityList, Integer unionId) throws Exception {
        if (opportunityList == null || unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionOpportunity> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(opportunityList)) {
            for (UnionOpportunity opportunity : opportunityList) {
                if (unionId.equals(opportunity.getUnionId())) {
                    result.add(opportunity);
                }
            }
        }

        return result;
    }

    @Override
    public List<UnionOpportunity> filterByAcceptStatus(List<UnionOpportunity> opportunityList, Integer acceptStatus) throws Exception {
        if (opportunityList == null || acceptStatus == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionOpportunity> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(opportunityList)) {
            for (UnionOpportunity opportunity : opportunityList) {
                if (acceptStatus.equals(opportunity.getAcceptStatus())) {
                    result.add(opportunity);
                }
            }
        }

        return result;
    }

    @Override
    public List<UnionOpportunity> filterByIsClose(List<UnionOpportunity> opportunityList, Integer isClose) throws Exception {
        if (opportunityList == null || isClose == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionOpportunity> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(opportunityList)) {
            for (UnionOpportunity opportunity : opportunityList) {
                if (isClose.equals(opportunity.getIsClose())) {
                    result.add(opportunity);
                }
            }
        }

        return result;
    }

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

    @Override
    public List<UnionOpportunity> filterByAcceptStatusList(List<UnionOpportunity> opportunityList, List<Integer> optAcceptStatusList) throws Exception {
        if (opportunityList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionOpportunity> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(opportunityList)) {
            for (UnionOpportunity opportunity : opportunityList) {
                if (optAcceptStatusList.contains(opportunity.getAcceptStatus())) {
                    result.add(opportunity);
                }
            }
        }

        return result;
    }

    @Override
    public List<UnionOpportunity> filterByLikeClientName(List<UnionOpportunity> opportunityList, String likeClientName) throws Exception {
        if (opportunityList == null || likeClientName == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionOpportunity> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(opportunityList)) {
            for (UnionOpportunity opportunity : opportunityList) {
                if (StringUtil.isNotEmpty(opportunity.getClientName()) && opportunity.getClientName().contains(likeClientName)) {
                    result.add(opportunity);
                }
            }
        }

        return result;
    }

    @Override
    public List<UnionOpportunity> filterByLikeClientPhone(List<UnionOpportunity> opportunityList, String likeClientPhone) throws Exception {
        if (opportunityList == null || likeClientPhone == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionOpportunity> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(opportunityList)) {
            for (UnionOpportunity opportunity : opportunityList) {
                if (StringUtil.isNotEmpty(opportunity.getClientPhone()) && opportunity.getClientPhone().contains(likeClientPhone)) {
                    result.add(opportunity);
                }
            }
        }

        return result;
    }

    @Override
    public List<UnionOpportunity> filterByToMemberId(List<UnionOpportunity> opportunityList, Integer toMemberId) throws Exception {
        if (opportunityList == null || toMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionOpportunity> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(opportunityList)) {
            for (UnionOpportunity opportunity : opportunityList) {
                if (toMemberId.equals(opportunity.getToMemberId())) {
                    result.add(opportunity);
                }
            }
        }

        return result;
    }

    @Override
    public List<UnionOpportunity> filterByFromMemberId(List<UnionOpportunity> opportunityList, Integer fromMemberId) throws Exception {
        if (opportunityList == null || fromMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionOpportunity> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(opportunityList)) {
            for (UnionOpportunity opportunity : opportunityList) {
                if (fromMemberId.equals(opportunity.getFromMemberId())) {
                    result.add(opportunity);
                }
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

    @Override
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