package com.gt.union.h5.brokerage.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.gt.api.bean.session.BusUser;
import com.gt.api.bean.session.Member;
import com.gt.api.bean.session.TCommonStaff;
import com.gt.union.api.amqp.entity.TemplateSmsMessage;
import com.gt.union.api.amqp.sender.PhoneMessageSender;
import com.gt.union.api.client.pay.WxPayService;
import com.gt.union.api.client.sms.SmsService;
import com.gt.union.api.client.sms.constant.SmsConstant;
import com.gt.union.api.client.staff.ITCommonStaffService;
import com.gt.union.api.client.user.IBusUserService;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.constant.ConfigConstant;
import com.gt.union.common.constant.SmsCodeConstant;
import com.gt.union.common.exception.BaseException;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.response.GtJsonResult;
import com.gt.union.common.util.*;
import com.gt.union.finance.verifier.entity.UnionVerifier;
import com.gt.union.finance.verifier.service.IUnionVerifierService;
import com.gt.union.h5.brokerage.service.IH5BrokerageService;
import com.gt.union.h5.brokerage.vo.*;
import com.gt.union.opportunity.brokerage.constant.BrokerageConstant;
import com.gt.union.opportunity.brokerage.entity.UnionBrokerageIncome;
import com.gt.union.opportunity.brokerage.entity.UnionBrokeragePay;
import com.gt.union.opportunity.brokerage.entity.UnionBrokerageWithdrawal;
import com.gt.union.opportunity.brokerage.service.IUnionBrokerageIncomeService;
import com.gt.union.opportunity.brokerage.service.IUnionBrokeragePayService;
import com.gt.union.opportunity.brokerage.service.IUnionBrokeragePayStrategyService;
import com.gt.union.opportunity.brokerage.service.IUnionBrokerageWithdrawalService;
import com.gt.union.opportunity.main.constant.OpportunityConstant;
import com.gt.union.opportunity.main.entity.UnionOpportunity;
import com.gt.union.opportunity.main.service.IUnionOpportunityService;
import com.gt.union.union.main.entity.UnionMain;
import com.gt.union.union.main.service.IUnionMainService;
import com.gt.union.union.main.vo.UnionPayVO;
import com.gt.union.union.member.entity.UnionMember;
import com.gt.union.union.member.service.IUnionMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 佣金平台 服务实现类
 *
 * @author linweicong
 * @version 2017-12-18 11:46:53
 */
@Service
public class H5BrokerageServiceImpl implements IH5BrokerageService {

    @Autowired
    private SmsService smsService;

    @Autowired
    private IUnionVerifierService unionVerifierService;

    @Autowired
    private IBusUserService busUserService;

    @Autowired
    private ITCommonStaffService itCommonStaffService;

    @Autowired
    private IUnionBrokerageIncomeService unionBrokerageIncomeService;

    @Autowired
    private IUnionBrokerageWithdrawalService unionBrokerageWithdrawalService;

    @Autowired
    private IUnionOpportunityService unionOpportunityService;

    @Autowired
    private IUnionMemberService unionMemberService;

    @Autowired
    private IUnionMainService unionMainService;

    @Autowired
    private WxPayService wxPayService;

    @Autowired
    private IUnionBrokeragePayService unionBrokeragePayService;

    @Autowired
    private PhoneMessageSender phoneMessageSender;

    @Resource(name = "unionPhoneBrokeragePayService")
    private IUnionBrokeragePayStrategyService unionBrokeragePayStrategyService;

    //***************************************** Domain Driven Design - get ********************************************

    @Override
    public IndexVO getIndexVO(H5BrokerageUser h5BrokerageUser) throws Exception {
        if (h5BrokerageUser == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        IndexVO result = new IndexVO();
        // （1）获取历史佣金收入总额=售卡佣金+商机佣金(已支付)
        Integer busId = h5BrokerageUser.getVerifier().getBusId();
        Double brokerageSum = unionBrokerageIncomeService.sumValidMoneyByBusId(busId);
        result.setBrokerageSum(brokerageSum);
        // （2）	获取可提佣金金额=历史佣金收入总额-历史佣金提现总额
        Double withdrawalSum = unionBrokerageWithdrawalService.sumValidMoneyByBusId(busId);
        BigDecimal availableBrokerage = BigDecimalUtil.subtract(brokerageSum, withdrawalSum);
        result.setAvailableBrokerage(BigDecimalUtil.toDouble(availableBrokerage));
        // （3）	获取未支付佣金金额，即商机已接受，但仍未支付
        List<UnionMember> memberList = unionMemberService.listByBusId(busId);
        List<Integer> memberIdList = unionMemberService.getIdList(memberList);
        Double unPaidOpportunityBrokerage = unionOpportunityService.sumValidBrokerageMoneyByToMemberIdListAndAcceptStatusAndIsClose(memberIdList,
                OpportunityConstant.ACCEPT_STATUS_CONFIRMED, OpportunityConstant.IS_CLOSE_NO);
        result.setUnPaidOpportunityBrokerage(unPaidOpportunityBrokerage);

        return result;
    }

    @Override
    public WithdrawalVO getWithdrawalVO(H5BrokerageUser h5BrokerageUser) throws Exception {
        if (h5BrokerageUser == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        WithdrawalVO result = new WithdrawalVO();
        Integer busId = h5BrokerageUser.getVerifier().getBusId();
        List<UnionMember> memberList = unionMemberService.listByBusId(busId);
        List<Integer> memberIdList = unionMemberService.getIdList(memberList);

        // （1）	获取已支付的商机佣金收入总额
        Double paidOpportunityBrokerage = unionOpportunityService.sumValidBrokerageMoneyByFromMemberIdListAndAcceptStatusAndIsClose(memberIdList,
                OpportunityConstant.ACCEPT_STATUS_CONFIRMED, OpportunityConstant.IS_CLOSE_YES);
        result.setPaidOpportunityBrokerage(paidOpportunityBrokerage);
        // （2）	获取未支付商机佣金总额，即商机已接受，但仍未支付
        Double unPaidOpportunityBrokerage = unionOpportunityService.sumValidBrokerageMoneyByFromMemberIdListAndAcceptStatusAndIsClose(memberIdList,
                OpportunityConstant.ACCEPT_STATUS_CONFIRMED, OpportunityConstant.IS_CLOSE_NO);
        result.setUnPaidOpportunityBrokerage(unPaidOpportunityBrokerage);
        // （3）	获取售卡佣金收入总额
        Double cardBrokerage = unionBrokerageIncomeService.sumValidMoneyByBusIdAndType(busId, BrokerageConstant.INCOME_TYPE_CARD);
        result.setCardBrokerage(cardBrokerage);
        // （4）	获取历史提现佣金总额
        Double historyWithdrawal = unionBrokerageWithdrawalService.sumValidMoneyByBusId(busId);
        result.setHistoryWithdrawal(historyWithdrawal);
        // （5）	获取可提佣金总额 总收入金额-历史提现金额
        Double incomeSum = unionBrokerageIncomeService.sumValidMoneyByBusId(busId);
        BigDecimal availableBrokerage = BigDecimalUtil.subtract(incomeSum, historyWithdrawal);
        result.setAvailableBrokerage(BigDecimalUtil.toDouble(availableBrokerage));

        return result;
    }

    //***************************************** Domain Driven Design - list ********************************************

    @Override
    public Page pageWithdrawalHistory(H5BrokerageUser h5BrokerageUser, Page page) throws Exception {
        if (h5BrokerageUser == null || page == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        Integer busId = h5BrokerageUser.getVerifier().getBusId();
        // 按时间倒序排序
        EntityWrapper<UnionBrokerageWithdrawal> wrapper = new EntityWrapper<>();
        wrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("bus_id", busId)
                .orderBy("create_time", false);
        Page result = unionBrokerageWithdrawalService.pageSupport(page, wrapper);

        return result;
    }

    @Override
    public List<UnionMain> listMyUnion(H5BrokerageUser h5BrokerageUser) throws Exception {
        if (h5BrokerageUser == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        return unionMainService.listReadByBusId(h5BrokerageUser.getVerifier().getBusId());
    }

    @Override
    public Page pageOpportunityBrokerageVO(H5BrokerageUser h5BrokerageUser, Integer optUnionId, Page page) throws Exception {
        if (h5BrokerageUser == null || page == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        Integer busId = h5BrokerageUser.getVerifier().getBusId();
        List<UnionMember> memberList = unionMemberService.listByBusId(busId);
        if (optUnionId != null) {
            memberList = unionMemberService.filterByUnionId(memberList, optUnionId);
        }
        // （1）	获取商机佣金明细，如果unionId不为空，则对unionId进行过滤，按时间倒序排序
        List<Integer> memberIdList = unionMemberService.getIdList(memberList);
        EntityWrapper<UnionOpportunity> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .in("to_member_id", memberIdList)
                .eq(ListUtil.isEmpty(memberList), "to_member_id", null)
                .eq("accept_status", OpportunityConstant.ACCEPT_STATUS_CONFIRMED)
                .eq("is_close", OpportunityConstant.IS_CLOSE_YES)
                .orderBy("create_time", false);
        Page result = unionOpportunityService.pageSupport(page, entityWrapper);

        List<OpportunityBrokerageVO> list = getOpportunityBrokerageVOList(result.getRecords());
        result.setRecords(list);

        return result;
    }

    @Override
    public Page pageCardBrokerageVO(H5BrokerageUser h5BrokerageUser, Integer optUnionId, Page page) throws Exception {
        if (h5BrokerageUser == null || page == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        Integer busId = h5BrokerageUser.getVerifier().getBusId();
        List<UnionMember> memberList = unionMemberService.listByBusId(busId);
        if (optUnionId != null) {
            memberList = unionMemberService.filterByUnionId(memberList, optUnionId);
        }
        List<Integer> memberIdList = unionMemberService.getIdList(memberList);
        // （1）	获取售卡佣金明细，如果unionId不为空，则对unionId进行过滤，按时间倒序排序

        EntityWrapper<UnionBrokerageIncome> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("bus_id", busId)
                .eq("type", BrokerageConstant.INCOME_TYPE_CARD)
                .in("member_id", memberIdList)
                .eq(ListUtil.isEmpty(memberList), "member_id", null)
                .orderBy("create_time", false);
        Page result = unionBrokerageIncomeService.pageSupport(page, entityWrapper);

        List<UnionBrokerageIncome> incomeList = result.getRecords();
        List<CardBrokerageVO> list = new ArrayList<>();
        if (ListUtil.isNotEmpty(incomeList)) {
            for (UnionBrokerageIncome income : incomeList) {
                CardBrokerageVO vo = new CardBrokerageVO();
                vo.setBrokerageIncome(income);

                Integer unionId = income.getUnionId();
                vo.setUnion(unionMainService.getById(unionId));

                vo.setMember(unionMemberService.getById(income.getMemberId()));

                list.add(vo);
            }
        }
        result.setRecords(list);

        return result;
    }

    @Override
    public Page pageUnPaidOpportunityBrokerageVO(H5BrokerageUser h5BrokerageUser, Integer optUnionId, Page page) throws Exception {
        if (h5BrokerageUser == null || page == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        Integer busId = h5BrokerageUser.getVerifier().getBusId();
        List<UnionMember> memberList = unionMemberService.listByBusId(busId);
        if (optUnionId != null) {
            memberList = unionMemberService.filterByUnionId(memberList, optUnionId);
        }
        List<Integer> memberIdList = unionMemberService.getIdList(memberList);
        // （1）	获取未支付的商机佣金明细，如果unionId不为空，则对unionId进行过滤，按时间倒序排序
        EntityWrapper<UnionOpportunity> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .in("to_member_id", memberIdList)
                .eq(ListUtil.isEmpty(memberIdList), "to_member_id", null)
                .eq("accept_status", OpportunityConstant.ACCEPT_STATUS_CONFIRMED)
                .eq("is_close", OpportunityConstant.IS_CLOSE_NO)
                .orderBy("create_time", false);

        Page result = unionOpportunityService.pageSupport(page, entityWrapper);
        List<OpportunityBrokerageVO> opportunityList = getOpportunityBrokerageVOList(result.getRecords());
        result.setRecords(opportunityList);
        return result;
    }

    @Override
    public Page pageUnReceivedOpportunityBrokerageVO(H5BrokerageUser h5BrokerageUser, Integer optUnionId, Page page) throws Exception {
        if (h5BrokerageUser == null || page == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        Integer busId = h5BrokerageUser.getVerifier().getBusId();
        List<UnionMember> memberList = unionMemberService.listByBusId(busId);
        if (optUnionId != null) {
            memberList = unionMemberService.filterByUnionId(memberList, optUnionId);
        }
        List<Integer> memberIdList = unionMemberService.getIdList(memberList);
        // （1）	获取未收的商机佣金明细，如果unionId不为空，则对unionId进行过滤，按时间倒序排序
        EntityWrapper<UnionOpportunity> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .in("from_member_id", memberIdList)
                .eq(ListUtil.isEmpty(memberIdList), "from_member_id", null)
                .eq("accept_status", OpportunityConstant.ACCEPT_STATUS_CONFIRMED)
                .eq("is_close", OpportunityConstant.IS_CLOSE_NO)
                .orderBy("create_time", false);
        Page result = unionOpportunityService.pageSupport(page, entityWrapper);

        List<OpportunityBrokerageVO> opportunityList = getOpportunityBrokerageVOList(result.getRecords());
        page.setRecords(opportunityList);
        return result;
    }

    private List<OpportunityBrokerageVO> getOpportunityBrokerageVOList(List<UnionOpportunity> opportunityList) throws Exception {
        List<OpportunityBrokerageVO> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(opportunityList)) {
            for (UnionOpportunity opportunity : opportunityList) {
                OpportunityBrokerageVO vo = new OpportunityBrokerageVO();
                vo.setOpportunity(opportunity);

                Integer unionId = opportunity.getUnionId();
                vo.setUnion(unionMainService.getById(unionId));

                vo.setFromMember(unionMemberService.getById(opportunity.getFromMemberId()));

                vo.setToMember(unionMemberService.getById(opportunity.getToMemberId()));

                result.add(vo);
            }
        }

        return result;
    }

    @Override
    public GtJsonResult withdrawal(H5BrokerageUser h5BrokerageUser, Member member, Double money) throws Exception {
        if (h5BrokerageUser == null || member == null || money == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        UnionVerifier verifier = h5BrokerageUser.getVerifier();
        Integer busId = verifier.getBusId();
        // （1）	判断money有效性
        String key = RedissonKeyUtil.getWithdrawalByBusIdKey(busId);
        try {
            //加锁
            RedissonLockUtil.lock(key, 10);
            //总收入
            Double incomeSum = unionBrokerageIncomeService.sumValidMoneyByBusId(busId);

            //总提现
            Double withdrawalSum = unionBrokerageWithdrawalService.sumValidMoneyByBusId(busId);
            //可提现金额
            BigDecimal availableSum = BigDecimalUtil.subtract(incomeSum, withdrawalSum);
            if (BigDecimalUtil.subtract(availableSum, money).doubleValue() < 0) {
                throw new BusinessException("提现金额有误");
            }

            // （2）	调用提现接口，如果成功，则保存记录并返回；否则，直接返回错误提示
            UnionBrokerageWithdrawal saveWithdrawal = new UnionBrokerageWithdrawal();
            saveWithdrawal.setCreateTime(DateUtil.getCurrentDate());
            saveWithdrawal.setDelStatus(CommonConstant.COMMON_NO);
            saveWithdrawal.setBusId(busId);
            String orderNo = "LM" + ConfigConstant.PAY_MODEL_WITHDRAWAL + DateUtil.getSerialNumber();
            saveWithdrawal.setSysOrderNo(orderNo);
            saveWithdrawal.setVerifierId(verifier.getId());
            saveWithdrawal.setVerifierName(verifier.getEmployeeName());

            GtJsonResult payResult = wxPayService.enterprisePayment(orderNo, member.getOpenid(), "佣金提现", money, 0);
            if (payResult.isSuccess()) {
                unionBrokerageWithdrawalService.save(saveWithdrawal);
            }

            return payResult;
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof BaseException) {
                throw new BaseException(e.getMessage());
            } else {
                throw new Exception(e.getMessage());
            }
        } finally {
            //释放锁
            RedissonLockUtil.unlock(key);
        }
    }

    //***************************************** Domain Driven Design - save ********************************************

    @Override
    public UnionPayVO toPayByUnionIdAndOpportunityId(H5BrokerageUser h5BrokerageUser, Integer unionId, Integer opportunityId) throws Exception {
        if (h5BrokerageUser == null || unionId == null || opportunityId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        Integer busId = h5BrokerageUser.getVerifier().getBusId();
        UnionMember member = unionMemberService.getValidWriteByBusIdAndUnionId(busId, unionId);
        if (member == null) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_ERROR);
        }
        // （2）判断opportunityId有效性
        UnionOpportunity opportunity = unionOpportunityService.getValidByIdAndUnionIdAndToMemberId(opportunityId, unionId, member.getId());
        if (opportunity == null) {
            throw new BusinessException("找不到商机信息");
        }
        if (OpportunityConstant.IS_CLOSE_NO != opportunity.getIsClose()) {
            throw new BusinessException("商机不是未结算状态");
        }
        // （3）生成支付中记录
        UnionMember fromMember = unionMemberService.getById(opportunity.getFromMemberId());
        if (fromMember == null) {
            throw new BusinessException("找不到商机推荐者信息");
        }

        UnionBrokeragePay savePay = new UnionBrokeragePay();
        savePay.setDelStatus(CommonConstant.COMMON_NO);
        savePay.setCreateTime(DateUtil.getCurrentDate());
        savePay.setStatus(BrokerageConstant.PAY_STATUS_PAYING);
        savePay.setFromMemberId(member.getId());
        savePay.setToMemberId(fromMember.getId());
        savePay.setUnionId(opportunity.getUnionId());
        savePay.setFromBusId(member.getBusId());
        savePay.setToBusId(fromMember.getBusId());
        String orderNo = "LM" + ConfigConstant.PAY_MODEL_OPPORTUNITY + DateUtil.getSerialNumber();
        savePay.setSysOrderNo(orderNo);
        savePay.setOpportunityId(opportunityId);
        savePay.setMoney(opportunity.getBrokerageMoney());


        // （4）调用支付接口
        UnionPayVO result = unionBrokeragePayStrategyService.unionBrokerageApply(orderNo, opportunity.getBrokerageMoney());

        unionBrokeragePayService.save(savePay);
        return result;
    }

    @Override
    public UnionPayVO batchPayByUnionId(H5BrokerageUser h5BrokerageUser, Integer unionId, IUnionBrokeragePayStrategyService unionBrokeragePayStrategyService) throws Exception {
        if (h5BrokerageUser == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        UnionVerifier verifier = h5BrokerageUser.getVerifier();
        Integer busId = verifier.getBusId();
        List<UnionMember> memberList = unionMemberService.listByBusId(busId);
        if (unionId != null) {
            memberList = unionMemberService.filterByUnionId(memberList, unionId);
        }
        List<Integer> memberIdList = unionMemberService.getIdList(memberList);
        // （1）获取所有已接受但未支付的商机id列表
        List<UnionOpportunity> opportunityList = unionOpportunityService.listValidByToMemberIdListAndAcceptStatusAndIsClose(memberIdList,
                OpportunityConstant.ACCEPT_STATUS_CONFIRMED, OpportunityConstant.IS_CLOSE_NO);
        List<Integer> opportunityIdList = unionOpportunityService.getIdList(opportunityList);

        return unionBrokeragePayService.batchPayByBusId(busId, opportunityIdList, verifier.getId(), unionBrokeragePayStrategyService);
    }

    //***************************************** Domain Driven Design - remove ******************************************

    //***************************************** Domain Driven Design - update ******************************************

    @Override
    public void loginByPhone(HttpServletRequest request, LoginPhone loginPhone) throws Exception {
        if (request == null || loginPhone == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        // （1）	判断验证码有效性
        String phone = loginPhone.getPhone();
        String code = loginPhone.getCode();
        if (StringUtil.isEmpty(phone)) {
            throw new BusinessException("手机号不能为空");
        }
        if (!StringUtil.isPhone(phone)) {
            throw new BusinessException("手机号格式有误");
        }
        if (!smsService.checkPhoneCode(SmsCodeConstant.BROKERAGE_LOGIN_TYPE, code, phone)) {
            throw new BusinessException("手机验证码错误");
        }
        // （2）	判断本地手机号，如果存在，则继续；否则调用接口查询并更新
        List<UnionVerifier> verifierList = unionVerifierService.listValid();
        List<UnionVerifier> loginVerifierList = unionVerifierService.filterByPhone(verifierList, phone);
        if (ListUtil.isNotEmpty(loginVerifierList)) {
            UnionVerifier loginVerifier = loginVerifierList.get(0);
            BusUser busUser = busUserService.getBusUserById(loginVerifier.getBusId());

            H5BrokerageUser h5BrokerageUser = new H5BrokerageUser();
            h5BrokerageUser.setVerifier(loginVerifier);
            h5BrokerageUser.setBusUser(busUser);

            UnionSessionUtil.setH5BrokerageUser(request, h5BrokerageUser);
            return;
        } else if (ListUtil.isNotEmpty(verifierList)) {
            for (UnionVerifier verifier : verifierList) {
                TCommonStaff employee = itCommonStaffService.getTCommonStaffById(verifier.getEmployeeId());
                if (employee != null && phone.equals(employee.getPhone())) {
                    verifier.setPhone(employee.getPhone());
                    BusUser busUser = busUserService.getBusUserById(verifier.getBusId());

                    H5BrokerageUser h5BrokerageUser = new H5BrokerageUser();
                    h5BrokerageUser.setVerifier(verifier);
                    h5BrokerageUser.setBusUser(busUser);
                    UnionSessionUtil.setH5BrokerageUser(request, h5BrokerageUser);

                    UnionVerifier updateVerifier = new UnionVerifier();
                    updateVerifier.setId(verifier.getId());
                    updateVerifier.setPhone(verifier.getPhone());
                    unionVerifierService.update(updateVerifier);
                    return;
                }
            }
        }
        // 判断是否是管理员
        BusUser busUser = busUserService.getBusUserByPhone(phone);
        if (busUser != null) {
            H5BrokerageUser h5BrokerageUser = new H5BrokerageUser();
            h5BrokerageUser.setBusUser(busUser);

            UnionVerifier adminVerifier = new UnionVerifier();
            adminVerifier.setBusId(busUser.getId());
            adminVerifier.setPhone(phone);
            adminVerifier.setEmployeeName("管理员");
            h5BrokerageUser.setVerifier(adminVerifier);

            UnionSessionUtil.setH5BrokerageUser(request, h5BrokerageUser);
            return;
        }
        throw new BusinessException("登录失败");
    }

    @Override
    public void urgeUnreceived(H5BrokerageUser h5BrokerageUser, Integer opportunityId) throws Exception {
        if (h5BrokerageUser == null || opportunityId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        UnionOpportunity opportunity = unionOpportunityService.getById(opportunityId);
        if (opportunity == null) {
            throw new BusinessException("找不到商机信息");
        }

        Integer unionId = opportunity.getUnionId();
        Integer busId = h5BrokerageUser.getVerifier().getBusId();
        UnionMember member = unionMemberService.getByBusIdAndIdAndUnionId(busId, opportunity.getFromMemberId(), unionId);
        if (member == null) {
            throw new BusinessException("无法催促别人的商机");
        }

        UnionMain union = unionMainService.getById(unionId);
        UnionMember toMember = unionMemberService.getById(opportunity.getToMemberId());
        if (toMember == null) {
            throw new BusinessException("无法催促别人的商机");
        }

        String phone = toMember.getNotifyPhone() != null ? toMember.getNotifyPhone() : toMember.getDirectorPhone();
        String message = union.getName() + "," + member.getEnterpriseName()
                + "," + opportunity.getBrokerageMoney();

        TemplateSmsMessage msg = new TemplateSmsMessage();
        msg.setBusId(toMember.getBusId());
        msg.setMobile(phone);
        msg.setTmplId(SmsConstant.NOTIFY_MEMBER_PAY_BROKERAGE_TEMPLATE_ID);
        msg.setParamsStr(message);
        msg.setModel(ConfigConstant.SMS_UNION_MODEL);
        phoneMessageSender.sendMsg(msg);
    }

    //***************************************** Domain Driven Design - count *******************************************

    @Override
    public Double sumPaidOpportunityBrokerage(H5BrokerageUser h5BrokerageUser, Integer optUnionId) throws Exception {
        if (h5BrokerageUser == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        Integer busId = h5BrokerageUser.getVerifier().getBusId();
        List<UnionMember> memberList = unionMemberService.listByBusId(busId);
        if (optUnionId != null) {
            memberList = unionMemberService.filterByUnionId(memberList, optUnionId);
        }
        List<Integer> memberIdList = unionMemberService.getIdList(memberList);
        // （1）	获取已支付的商机佣金收入总额
        return unionOpportunityService.sumValidBrokerageMoneyByToMemberIdListAndAcceptStatusAndIsClose(memberIdList,
                OpportunityConstant.ACCEPT_STATUS_CONFIRMED, OpportunityConstant.IS_CLOSE_YES);
    }

    @Override
    public Double sumPaidCardBrokerage(H5BrokerageUser h5BrokerageUser, Integer optUnionId) throws Exception {
        if (h5BrokerageUser == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        Integer busId = h5BrokerageUser.getVerifier().getBusId();
        List<UnionMember> memberList = unionMemberService.listByBusId(busId);
        if (optUnionId != null) {
            memberList = unionMemberService.filterByUnionId(memberList, optUnionId);
        }
        List<Integer> memberIdList = unionMemberService.getIdList(memberList);
        // （1）	获取售卡佣金收入总额
        return unionBrokerageIncomeService.sumValidMoneyByBusIdAndTypeAndMemberIdList(busId, BrokerageConstant.INCOME_TYPE_CARD, memberIdList);
    }

    @Override
    public Double sumUnPaidOpportunityBrokerage(H5BrokerageUser h5BrokerageUser, Integer optUnionId) throws Exception {
        if (h5BrokerageUser == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        Integer busId = h5BrokerageUser.getVerifier().getBusId();
        List<UnionMember> memberList = unionMemberService.listByBusId(busId);
        if (optUnionId != null) {
            memberList = unionMemberService.filterByUnionId(memberList, optUnionId);
        }
        List<Integer> memberIdList = unionMemberService.getIdList(memberList);
        // （1）	获取未支付的商机佣金金额
        return unionOpportunityService.sumValidBrokerageMoneyByToMemberIdListAndAcceptStatusAndIsClose(memberIdList,
                OpportunityConstant.ACCEPT_STATUS_CONFIRMED, OpportunityConstant.IS_CLOSE_NO);
    }

    @Override
    public Double sumUnReceivedOpportunityBrokerage(H5BrokerageUser h5BrokerageUser, Integer optUnionId) throws Exception {
        if (h5BrokerageUser == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        Integer busId = h5BrokerageUser.getVerifier().getBusId();
        List<UnionMember> memberList = unionMemberService.listByBusId(busId);
        if (optUnionId != null) {
            memberList = unionMemberService.filterByUnionId(memberList, optUnionId);
        }
        List<Integer> memberIdList = unionMemberService.getIdList(memberList);
        // （1）	获取未收的商机佣金金额
        return unionOpportunityService.sumValidBrokerageMoneyByFromMemberIdListAndAcceptStatusAndIsClose(memberIdList,
                OpportunityConstant.ACCEPT_STATUS_CONFIRMED, OpportunityConstant.IS_CLOSE_NO);
    }

    //***************************************** Domain Driven Design - boolean *****************************************

    //***************************************** Domain Driven Design - filter ******************************************
}
