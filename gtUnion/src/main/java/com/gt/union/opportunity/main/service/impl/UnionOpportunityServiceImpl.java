package com.gt.union.opportunity.main.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.gt.union.api.amqp.entity.PhoneMessage;
import com.gt.union.api.amqp.sender.PhoneMessageSender;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.BigDecimalUtil;
import com.gt.union.common.util.DateUtil;
import com.gt.union.common.util.ListUtil;
import com.gt.union.common.util.StringUtil;
import com.gt.union.opportunity.brokerage.vo.BrokerageOpportunityVO;
import com.gt.union.opportunity.main.constant.OpportunityConstant;
import com.gt.union.opportunity.main.dao.IUnionOpportunityDao;
import com.gt.union.opportunity.main.entity.UnionOpportunity;
import com.gt.union.opportunity.main.entity.UnionOpportunityRatio;
import com.gt.union.opportunity.main.service.IUnionOpportunityRatioService;
import com.gt.union.opportunity.main.service.IUnionOpportunityService;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 商机 服务实现类
 *
 * @author linweicong
 * @version 2017-11-23 16:56:17
 */
@Service
public class UnionOpportunityServiceImpl implements IUnionOpportunityService {
    @Autowired
    private IUnionOpportunityDao unionOpportunityDao;

    @Autowired
    private IUnionMemberService unionMemberService;

    @Autowired
    private IUnionMainService unionMainService;

    @Autowired
    private IUnionOpportunityRatioService opportunityRatioService;

    @Autowired
    private PhoneMessageSender phoneMessageSender;

    //********************************************* Base On Business - get *********************************************

    @Override
    public UnionOpportunity getValidByIdAndUnionIdAndToMemberId(Integer opportunityId, Integer unionId, Integer toMemberId) throws Exception {
        if (opportunityId == null || unionId == null || toMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionOpportunity> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("id", opportunityId)
                .eq("union_id", unionId)
                .eq("to_member_id", toMemberId);

        return unionOpportunityDao.selectOne(entityWrapper);
    }

    @Override
    public UnionOpportunity getValidByIdAndUnionIdAndToMemberIdAndAcceptStatus(Integer opportunityId, Integer unionId, Integer toMemberId, Integer acceptStatus) throws Exception {
        if (opportunityId == null || unionId == null || toMemberId == null || acceptStatus == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionOpportunity> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("id", opportunityId)
                .eq("union_id", unionId)
                .eq("to_member_id", toMemberId)
                .eq("accept_status", acceptStatus);

        return unionOpportunityDao.selectOne(entityWrapper);
    }

    @Override
    public OpportunityStatisticsVO getOpportunityStatisticsVOByBusIdAndUnionId(Integer busId, Integer unionId) throws Exception {
        if (busId == null || unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // 获取我的盟员信息
        List<UnionMember> memberList = unionMemberService.listByBusIdAndUnionId(busId, unionId);
        if (ListUtil.isEmpty(memberList)) {
            throw new BusinessException(CommonConstant.MEMBER_NOT_FOUND);
        }
        List<Integer> memberIdList = unionMemberService.getIdList(memberList);
        // 获取已接受的我推荐的商机，区分是否已支付
        OpportunityStatisticsVO result = new OpportunityStatisticsVO();
        List<UnionOpportunity> incomeOpportunityList = listValidByUnionIdAndFromMemberIdListAndAcceptStatus(unionId, memberIdList, OpportunityConstant.ACCEPT_STATUS_CONFIRMED);
        List<UnionOpportunity> paidIncomeOpportunityList = filterByIsClose(incomeOpportunityList, OpportunityConstant.IS_CLOSE_YES);
        BigDecimal paidIncome = BigDecimal.ZERO;
        if (ListUtil.isNotEmpty(paidIncomeOpportunityList)) {
            for (UnionOpportunity opportunity : paidIncomeOpportunityList) {
                paidIncome = BigDecimalUtil.add(paidIncome, opportunity.getBrokerageMoney());
            }
        }
        result.setPaidIncome(BigDecimalUtil.toDouble(paidIncome));

        List<UnionOpportunity> unPaidIncomeOpportunityList = filterByIsClose(incomeOpportunityList, OpportunityConstant.IS_CLOSE_NO);
        BigDecimal unPaidIncome = BigDecimal.ZERO;
        if (ListUtil.isNotEmpty(unPaidIncomeOpportunityList)) {
            for (UnionOpportunity opportunity : unPaidIncomeOpportunityList) {
                unPaidIncome = BigDecimalUtil.add(unPaidIncome, opportunity.getBrokerageMoney());
            }
        }
        result.setUnPaidIncome(BigDecimalUtil.toDouble(unPaidIncome));

        BigDecimal incomeSum = BigDecimalUtil.add(paidIncome, unPaidIncome);
        result.setIncomeSum(BigDecimalUtil.toDouble(incomeSum));
        // 获取已接受的推荐给我的商机，区分是否已支付
        List<UnionOpportunity> expenseOpportunityList = listValidByUnionIdAndToMemberIdListAndAcceptStatus(unionId, memberIdList, OpportunityConstant.ACCEPT_STATUS_CONFIRMED);
        List<UnionOpportunity> paidExpenseOpportunityList = filterByIsClose(expenseOpportunityList, OpportunityConstant.IS_CLOSE_YES);
        BigDecimal paidExpense = BigDecimal.ZERO;
        if (ListUtil.isNotEmpty(paidExpenseOpportunityList)) {
            for (UnionOpportunity opportunity : paidExpenseOpportunityList) {
                paidExpense = BigDecimalUtil.add(paidExpense, opportunity.getBrokerageMoney());
            }
        }
        result.setPaidExpense(BigDecimalUtil.toDouble(paidExpense));

        List<UnionOpportunity> unPaidExpenseOpportunityList = filterByIsClose(expenseOpportunityList, OpportunityConstant.IS_CLOSE_NO);
        BigDecimal unPaidExpense = BigDecimal.ZERO;
        if (ListUtil.isNotEmpty(unPaidExpenseOpportunityList)) {
            for (UnionOpportunity opportunity : unPaidExpenseOpportunityList) {
                unPaidExpense = BigDecimalUtil.add(unPaidExpense, opportunity.getBrokerageMoney());
            }
        }
        result.setUnPaidExpense(BigDecimalUtil.toDouble(unPaidExpense));

        BigDecimal expenseSum = BigDecimalUtil.add(paidExpense, unPaidExpense);
        result.setExpenseSum(BigDecimalUtil.toDouble(expenseSum));
        // 获取一周内商机收支信息
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
            dayStatistic.setPaidIncome(BigDecimalUtil.toDouble(dayIncome));
            List<UnionOpportunity> dayExpenseOpportunityList = filterBetweenTime(paidExpenseOpportunityList, beginDate, endDate);
            BigDecimal dayExpense = BigDecimal.ZERO;
            if (ListUtil.isNotEmpty(dayExpenseOpportunityList)) {
                for (UnionOpportunity opportunity : dayExpenseOpportunityList) {
                    dayExpense = BigDecimalUtil.add(dayExpense, opportunity.getBrokerageMoney());
                }
            }
            dayStatistic.setPaidExpense(BigDecimalUtil.toDouble(dayExpense));
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

    //********************************************* Base On Business - list ********************************************

    @Override
    public List<UnionOpportunity> listValidByFromMemberIdListAndAcceptStatus(List<Integer> fromMemberIdList, Integer acceptStatus) throws Exception {
        if (fromMemberIdList == null || acceptStatus == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionOpportunity> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .in("from_member_id", fromMemberIdList)
                .eq("accept_status", acceptStatus);

        return unionOpportunityDao.selectList(entityWrapper);
    }


    @Override
    public List<UnionOpportunity> listValidByFromMemberIdListAndAcceptStatusAndIsClose(List<Integer> fromMemberIdList, Integer acceptStatus, Integer isClose) throws Exception {
        if (fromMemberIdList == null || acceptStatus == null || isClose == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionOpportunity> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .in("from_member_id", fromMemberIdList)
                .eq("accept_status", acceptStatus)
                .eq("is_close", isClose);

        return unionOpportunityDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionOpportunity> listValidByToMemberIdListAndAcceptStatus(List<Integer> toMemberIdList, Integer acceptStatus) throws Exception {
        if (toMemberIdList == null || acceptStatus == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionOpportunity> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .in("to_member_id", toMemberIdList)
                .eq("accept_status", acceptStatus);

        return unionOpportunityDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionOpportunity> listValidByToMemberIdListAndAcceptStatusAndIsClose(List<Integer> toMemberIdList, Integer acceptStatus, Integer isClose) throws Exception {
        if (toMemberIdList == null || acceptStatus == null || isClose == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionOpportunity> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .in("to_member_id", toMemberIdList)
                .eq("accept_status", acceptStatus)
                .eq("is_close", isClose);

        return unionOpportunityDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionOpportunity> listValidByUnionIdAndFromMemberIdListAndAcceptStatus(Integer unionId, List<Integer> fromMemberIdList, Integer acceptStatus) throws Exception {
        if (unionId == null || fromMemberIdList == null || acceptStatus == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionOpportunity> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("union_id", unionId)
                .in("from_member_id", fromMemberIdList)
                .eq("accept_status", acceptStatus);

        return unionOpportunityDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionOpportunity> listValidByUnionIdAndToMemberIdListAndAcceptStatus(Integer unionId, List<Integer> toMemberIdList, Integer acceptStatus) throws Exception {
        if (unionId == null || toMemberIdList == null || acceptStatus == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionOpportunity> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("union_id", unionId)
                .in("to_member_id", toMemberIdList)
                .eq("accept_status", acceptStatus);

        return unionOpportunityDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionMember> listFromMemberByBusIdAndUnionId(Integer busId, Integer unionId) throws Exception {
        if (busId == null || unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionMember> memberList = unionMemberService.listByBusIdAndUnionId(busId, unionId);
        List<Integer> memberIdList = unionMemberService.getIdList(memberList);

        List<UnionOpportunity> opportunityList = listValidByUnionIdAndToMemberIdListAndAcceptStatus(unionId, memberIdList, OpportunityConstant.ACCEPT_STATUS_CONFIRMED);
        List<Integer> fromMemberIdList = getFromMemberIdList(opportunityList);
        fromMemberIdList = ListUtil.unique(fromMemberIdList);

        return unionMemberService.listByIdList(fromMemberIdList);
    }

    @Override
    public List<UnionMember> listToMemberByBusIdAndUnionId(Integer busId, Integer unionId) throws Exception {
        if (busId == null || unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionMember> memberList = unionMemberService.listByBusIdAndUnionId(busId, unionId);
        List<Integer> memberIdList = unionMemberService.getIdList(memberList);

        List<UnionOpportunity> opportunityList = listValidByUnionIdAndFromMemberIdListAndAcceptStatus(unionId, memberIdList, OpportunityConstant.ACCEPT_STATUS_CONFIRMED);
        List<Integer> toMemberIdList = getToMemberIdList(opportunityList);
        toMemberIdList = ListUtil.unique(toMemberIdList);

        return unionMemberService.listByIdList(toMemberIdList);
    }

    @Override
    public Page pageFromMeByBusId(Page page, Integer busId, Integer optUnionId, String optAcceptStatus, String optClientName, String optClientPhone) throws Exception {
        if (page == null || busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // 获取商家所有有效的member
        List<UnionMember> memberList = unionMemberService.listValidReadByBusId(busId);
        // 根据unionId过滤掉一些member
        if (optUnionId != null) {
            memberList = unionMemberService.filterByUnionId(memberList, optUnionId);
        }
        List<Integer> fromMemberIdList = unionMemberService.getIdList(memberList);
        // 数据源
        List<Integer> acceptStatusList = getAcceptStatusList(optAcceptStatus);
        EntityWrapper<UnionOpportunity> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .in("from_member_id", fromMemberIdList)
                .in(ListUtil.isNotEmpty(acceptStatusList), "accept_status", acceptStatusList)
                .like(StringUtil.isNotEmpty(optClientName), "client_name", optClientName)
                .like(StringUtil.isNotEmpty(optClientPhone), "client_phone", optClientPhone)
                .exists(" SELECT * FROM t_union_member m" +
                        " WHERE m.id=t_union_opportunity.to_member_id" +
                        " AND m.del_status=" + CommonConstant.DEL_STATUS_NO)
                .orderBy("accept_status ASC,create_time", false);
        Page result = unionOpportunityDao.selectPage(page, entityWrapper);
        // toVO
        List<UnionOpportunity> resultDataList = result.getRecords();
        result.setRecords(getOpportunityVOList(resultDataList));

        return result;
    }

    @Override
    public Page pageToMeByBusId(Page page, Integer busId, Integer optUnionId, String optAcceptStatus, String optClientName, String optClientPhone) throws Exception {
        if (page == null || busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // 获取商家所有有效的member
        List<UnionMember> memberList = unionMemberService.listValidReadByBusId(busId);
        // 根据unionId过滤掉一些member
        if (optUnionId != null) {
            memberList = unionMemberService.filterByUnionId(memberList, optUnionId);
        }
        List<Integer> toMemberIdList = unionMemberService.getIdList(memberList);
        // 数据源
        List<Integer> acceptStatusList = getAcceptStatusList(optAcceptStatus);
        EntityWrapper<UnionOpportunity> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .in("to_member_id", toMemberIdList)
                .in(ListUtil.isNotEmpty(acceptStatusList), "accept_status", acceptStatusList)
                .like(StringUtil.isNotEmpty(optClientName), "client_name", optClientName)
                .like(StringUtil.isNotEmpty(optClientPhone), "client_phone", optClientPhone)
                .exists(" SELECT * FROM t_union_member m" +
                        " WHERE m.id=t_union_opportunity.from_member_id" +
                        " AND m.del_status=" + CommonConstant.DEL_STATUS_NO)
                .orderBy("accept_status ASC,create_time", false);
        Page result = unionOpportunityDao.selectPage(page, entityWrapper);
        // toVO
        List<UnionOpportunity> resultDataList = result.getRecords();
        result.setRecords(getOpportunityVOList(resultDataList));

        return result;
    }

    private List<OpportunityVO> getOpportunityVOList(List<UnionOpportunity> opportunityList) throws Exception {
        List<OpportunityVO> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(opportunityList)) {
            for (UnionOpportunity opportunity : opportunityList) {
                OpportunityVO vo = new OpportunityVO();
                vo.setOpportunity(opportunity);

                UnionMain union = unionMainService.getById(opportunity.getUnionId());
                vo.setUnion(union);

                UnionMember fromMember = unionMemberService.getById(opportunity.getFromMemberId());
                vo.setFromMember(fromMember);

                UnionMember toMember = unionMemberService.getById(opportunity.getToMemberId());
                vo.setToMember(toMember);

                result.add(vo);
            }
        }
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
    public List<BrokerageOpportunityVO> listBrokerageOpportunityVO(List<UnionOpportunity> opportunityList) throws Exception {
        List<BrokerageOpportunityVO> result = new ArrayList<>();

        if (ListUtil.isNotEmpty(opportunityList)) {
            for (UnionOpportunity opportunity : opportunityList) {
                BrokerageOpportunityVO vo = new BrokerageOpportunityVO();
                vo.setOpportunity(opportunity);

                vo.setFromMember(unionMemberService.getById(opportunity.getFromMemberId()));

                vo.setToMember(unionMemberService.getById(opportunity.getToMemberId()));

                vo.setUnion(unionMainService.getById(opportunity.getUnionId()));

                result.add(vo);
            }
        }

        return result;
    }

    //********************************************* Base On Business - save ********************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveOpportunityVOByBusIdAndUnionId(Integer busId, Integer unionId, OpportunityVO vo) throws Exception {
        if (busId == null || unionId == null || vo == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // 判断union有效性
        UnionMain union = unionMainService.getValidById(unionId);
        if (!unionMainService.isUnionValid(union)) {
            throw new BusinessException(CommonConstant.UNION_INVALID);
        }
        // 判断member读权限
        UnionMember member = unionMemberService.getValidReadByBusIdAndUnionId(busId, unionId);
        if (member == null) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_ERROR);
        }
        // 校验表单数据
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
        UnionMember toMember = unionMemberService.getValidReadByIdAndUnionId(toMemberId, unionId);
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
        if (StringUtil.getStringLength(businessMsg) > 50) {
            throw new BusinessException("业务备注字段长度不能超过50");
        }
        saveOpportunity.setBusinessMsg(businessMsg);

        save(saveOpportunity);

        // 短信通知
        String phone = StringUtil.isNotEmpty(toMember.getNotifyPhone()) ? toMember.getNotifyPhone() : toMember.getDirectorPhone();
        String content = "\"" + union.getName() + "\"的盟员\""
                + member.getEnterpriseName() + "\"为你推荐了客户，请到商机消息处查看。客户信息："
                + clientName + "，" + clientPhone + "，" + businessMsg;
        phoneMessageSender.sendMsg(new PhoneMessage(toMember.getBusId(), phone, content));
    }

    //********************************************* Base On Business - remove ******************************************

    //********************************************* Base On Business - update ******************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateByBusIdAndIdAndUnionId(Integer busId, Integer opportunityId, Integer unionId, Integer isAccept, Double acceptPrice) throws Exception {
        if (opportunityId == null || unionId == null || busId == null || isAccept == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // 判断union有效性
        UnionMain union = unionMainService.getValidById(unionId);
        if (!unionMainService.isUnionValid(union)) {
            throw new BusinessException(CommonConstant.UNION_INVALID);
        }
        // 判断member读权限
        UnionMember member = unionMemberService.getValidReadByBusIdAndUnionId(busId, unionId);
        if (member == null) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_ERROR);
        }
        // 判断opportunityId有效性
        UnionOpportunity opportunity = getValidByIdAndUnionIdAndToMemberIdAndAcceptStatus(opportunityId, unionId, member.getId(), OpportunityConstant.ACCEPT_STATUS_CONFIRMING);
        if (opportunity == null) {
            throw new BusinessException("找不到商机信息");
        }
        // 接受时受理金额不能为空，且需大于0
        UnionOpportunity updateOpportunity = new UnionOpportunity();
        if (CommonConstant.COMMON_YES == isAccept) {
            if (acceptPrice == null || acceptPrice < 1) {
                throw new BusinessException("接受商机时受理金额不能为空，且必须大于1");
            }
            UnionOpportunityRatio ratio = opportunityRatioService.getValidByUnionIdAndFromMemberIdAndToMemberId(unionId, member.getId(), opportunity.getFromMemberId());
            if (ratio == null) {
                throw new BusinessException("没有对商机推荐者设置佣金比例");
            }
            updateOpportunity.setId(opportunityId);
            updateOpportunity.setAcceptStatus(OpportunityConstant.ACCEPT_STATUS_CONFIRMED);
            updateOpportunity.setAcceptPrice(acceptPrice);
            updateOpportunity.setBrokerageMoney(BigDecimalUtil.toDouble(BigDecimalUtil.multiply(acceptPrice, ratio.getRatio())));
        } else {
            updateOpportunity.setId(opportunityId);
            updateOpportunity.setAcceptStatus(OpportunityConstant.ACCEPT_STATUS_REJECT);
        }

        update(updateOpportunity);
        // 短信通知
        UnionMember fromMember = unionMemberService.getValidReadByIdAndUnionId(opportunity.getFromMemberId(), unionId);
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

    //********************************************* Base On Business - other *******************************************

    @Override
    public Double sumValidBrokerageMoneyByFromMemberIdListAndAcceptStatusAndIsClose(List<Integer> fromMemberIdList, Integer acceptStatus, Integer isClose) throws Exception {
        if (fromMemberIdList == null || acceptStatus == null || isClose == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionOpportunity> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .in("from_member_id", fromMemberIdList)
                .eq("accept_status", acceptStatus)
                .eq("is_close", isClose);

        entityWrapper.setSqlSelect("IfNull(SUM(brokerage_money),0) brokerageMoneySum");
        Map<String, Object> resultMap = unionOpportunityDao.selectMap(entityWrapper);

        return Double.valueOf(resultMap.get("brokerageMoneySum").toString());
    }

    @Override
    public Double sumValidBrokerageMoneyByToMemberIdListAndAcceptStatusAndIsClose(List<Integer> toMemberIdList, Integer acceptStatus, Integer isClose) throws Exception {
        if (toMemberIdList == null || acceptStatus == null || isClose == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionOpportunity> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .in("to_member_id", toMemberIdList)
                .eq("accept_status", acceptStatus)
                .eq("is_close", isClose);

        entityWrapper.setSqlSelect("IfNull(SUM(brokerage_money),0) brokerageMoneySum");
        Map<String, Object> resultMap = unionOpportunityDao.selectMap(entityWrapper);

        return Double.valueOf(resultMap.get("brokerageMoneySum").toString());
    }

    //********************************************* Base On Business - filter ******************************************

    @Override
    public List<UnionOpportunity> filterByDelStatus(List<UnionOpportunity> unionOpportunityList, Integer delStatus) throws Exception {
        if (delStatus == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionOpportunity> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(unionOpportunityList)) {
            for (UnionOpportunity unionOpportunity : unionOpportunityList) {
                if (delStatus.equals(unionOpportunity.getDelStatus())) {
                    result.add(unionOpportunity);
                }
            }
        }

        return result;
    }

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
    public List<UnionOpportunity> filterInvalidFromMemberId(List<UnionOpportunity> opportunityList) throws Exception {
        if (opportunityList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionOpportunity> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(opportunityList)) {
            for (UnionOpportunity opportunity : opportunityList) {
                if (unionMemberService.getValidById(opportunity.getFromMemberId()) != null) {
                    result.add(opportunity);
                }
            }
        }

        return result;
    }

    @Override
    public List<UnionOpportunity> filterInvalidToMemberId(List<UnionOpportunity> opportunityList) throws Exception {
        if (opportunityList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionOpportunity> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(opportunityList)) {
            for (UnionOpportunity opportunity : opportunityList) {
                if (unionMemberService.getValidById(opportunity.getToMemberId()) != null) {
                    result.add(opportunity);
                }
            }
        }

        return result;
    }

    //********************************************* Object As a Service - get ******************************************

    @Override
    public UnionOpportunity getById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        return unionOpportunityDao.selectById(id);
    }

    @Override
    public UnionOpportunity getValidById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionOpportunity> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("id", id);

        return unionOpportunityDao.selectOne(entityWrapper);
    }

    @Override
    public UnionOpportunity getInvalidById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionOpportunity> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("id", id);

        return unionOpportunityDao.selectOne(entityWrapper);
    }

    //********************************************* Object As a Service - list *****************************************

    @Override
    public List<Integer> getIdList(List<UnionOpportunity> unionOpportunityList) throws Exception {
        if (unionOpportunityList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<Integer> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(unionOpportunityList)) {
            for (UnionOpportunity unionOpportunity : unionOpportunityList) {
                result.add(unionOpportunity.getId());
            }
        }

        return result;
    }

    @Override
    public List<Integer> getFromMemberIdList(List<UnionOpportunity> unionOpportunityList) throws Exception {
        if (unionOpportunityList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<Integer> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(unionOpportunityList)) {
            for (UnionOpportunity unionOpportunity : unionOpportunityList) {
                result.add(unionOpportunity.getFromMemberId());
            }
        }

        return result;
    }

    @Override
    public List<Integer> getToMemberIdList(List<UnionOpportunity> unionOpportunityList) throws Exception {
        if (unionOpportunityList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<Integer> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(unionOpportunityList)) {
            for (UnionOpportunity unionOpportunity : unionOpportunityList) {
                result.add(unionOpportunity.getToMemberId());
            }
        }

        return result;
    }

    @Override
    public List<UnionOpportunity> listByFromMemberId(Integer fromMemberId) throws Exception {
        if (fromMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionOpportunity> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("from_member_id", fromMemberId);

        return unionOpportunityDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionOpportunity> listValidByFromMemberId(Integer fromMemberId) throws Exception {
        if (fromMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionOpportunity> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("from_member_id", fromMemberId);

        return unionOpportunityDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionOpportunity> listInvalidByFromMemberId(Integer fromMemberId) throws Exception {
        if (fromMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionOpportunity> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("from_member_id", fromMemberId);

        return unionOpportunityDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionOpportunity> listByToMemberId(Integer toMemberId) throws Exception {
        if (toMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionOpportunity> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("to_member_id", toMemberId);

        return unionOpportunityDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionOpportunity> listValidByToMemberId(Integer toMemberId) throws Exception {
        if (toMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionOpportunity> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("to_member_id", toMemberId);

        return unionOpportunityDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionOpportunity> listInvalidByToMemberId(Integer toMemberId) throws Exception {
        if (toMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionOpportunity> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("to_member_id", toMemberId);

        return unionOpportunityDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionOpportunity> listByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionOpportunity> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("union_id", unionId);

        return unionOpportunityDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionOpportunity> listValidByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionOpportunity> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("union_id", unionId);

        return unionOpportunityDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionOpportunity> listInvalidByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionOpportunity> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("union_id", unionId);

        return unionOpportunityDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionOpportunity> listByIdList(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionOpportunity> entityWrapper = new EntityWrapper<>();
        entityWrapper.in("id", idList);

        return unionOpportunityDao.selectList(entityWrapper);
    }

    @Override
    public Page pageSupport(Page page, EntityWrapper<UnionOpportunity> entityWrapper) throws Exception {
        if (page == null || entityWrapper == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        return unionOpportunityDao.selectPage(page, entityWrapper);
    }
    //********************************************* Object As a Service - save *****************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(UnionOpportunity newUnionOpportunity) throws Exception {
        if (newUnionOpportunity == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        unionOpportunityDao.insert(newUnionOpportunity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveBatch(List<UnionOpportunity> newUnionOpportunityList) throws Exception {
        if (newUnionOpportunityList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        unionOpportunityDao.insertBatch(newUnionOpportunityList);
    }

    //********************************************* Object As a Service - remove ***************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        UnionOpportunity removeUnionOpportunity = new UnionOpportunity();
        removeUnionOpportunity.setId(id);
        removeUnionOpportunity.setDelStatus(CommonConstant.DEL_STATUS_YES);
        unionOpportunityDao.updateById(removeUnionOpportunity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeBatchById(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionOpportunity> removeUnionOpportunityList = new ArrayList<>();
        for (Integer id : idList) {
            UnionOpportunity removeUnionOpportunity = new UnionOpportunity();
            removeUnionOpportunity.setId(id);
            removeUnionOpportunity.setDelStatus(CommonConstant.DEL_STATUS_YES);
            removeUnionOpportunityList.add(removeUnionOpportunity);
        }
        unionOpportunityDao.updateBatchById(removeUnionOpportunityList);
    }

    //********************************************* Object As a Service - update ***************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(UnionOpportunity updateUnionOpportunity) throws Exception {
        if (updateUnionOpportunity == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        unionOpportunityDao.updateById(updateUnionOpportunity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBatch(List<UnionOpportunity> updateUnionOpportunityList) throws Exception {
        if (updateUnionOpportunityList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        unionOpportunityDao.updateBatchById(updateUnionOpportunityList);
    }

}