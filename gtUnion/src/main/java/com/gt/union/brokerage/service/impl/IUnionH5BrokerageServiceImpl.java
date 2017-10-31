package com.gt.union.brokerage.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.plugins.Page;
import com.gt.api.bean.session.BusUser;
import com.gt.api.bean.sign.SignBean;
import com.gt.api.util.SessionUtils;
import com.gt.api.util.sign.SignHttpUtils;
import com.gt.api.util.sign.SignUtils;
import com.gt.union.api.client.pay.WxPayService;
import com.gt.union.api.client.sms.SmsService;
import com.gt.union.api.client.user.IBusUserService;
import com.gt.union.brokerage.entity.UnionBrokerageIncome;
import com.gt.union.brokerage.mapper.UnionH5BrokerageMapper;
import com.gt.union.brokerage.service.IUnionBrokerageIncomeService;
import com.gt.union.brokerage.service.IUnionBrokeragePayService;
import com.gt.union.brokerage.service.IUnionBrokerageWithdrawalService;
import com.gt.union.brokerage.service.IUnionH5BrokerageService;
import com.gt.union.common.amqp.entity.PhoneMessage;
import com.gt.union.common.amqp.sender.PhoneMessageSender;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.constant.ConfigConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.*;
import com.gt.union.main.entity.UnionMain;
import com.gt.union.main.service.IUnionMainService;
import com.gt.union.member.entity.UnionMember;
import com.gt.union.member.service.IUnionMemberService;
import com.gt.union.opportunity.constant.OpportunityConstant;
import com.gt.union.opportunity.entity.UnionOpportunity;
import com.gt.union.opportunity.service.IUnionOpportunityService;
import com.gt.union.verifier.entity.UnionVerifier;
import com.gt.union.verifier.service.IUnionVerifierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Created by Administrator on 2017/9/18 0018.
 */
@Service
public class IUnionH5BrokerageServiceImpl implements IUnionH5BrokerageService {

	@Autowired
	private RedisCacheUtil redisCacheUtil;

	@Autowired
	private IBusUserService busUserService;

	@Autowired
	private IUnionVerifierService unionVerifierService;

	@Autowired
	private IUnionBrokerageIncomeService unionBrokerageIncomeService;

	@Autowired
	private IUnionBrokerageWithdrawalService unionBrokerageWithdrawalService;

	@Autowired
	private UnionH5BrokerageMapper unionH5BrokerageMapper;

	@Autowired
	private IUnionMemberService unionMemberService;

	@Autowired
	private IUnionOpportunityService unionOpportunityService;

	@Autowired
	private IUnionMainService unionMainService;

	@Autowired
	private WxPayService payService;

	@Autowired
	private PhoneMessageSender phoneMessageSender;

	@Override
	public void checkLogin(String phone, String code, HttpServletRequest request) throws Exception{
		if(StringUtil.isEmpty(phone) || StringUtil.isEmpty(code)){
			throw new ParamException(CommonConstant.PARAM_ERROR);
		}
		String phoneKey = RedisKeyUtil.getBrokeragePhoneKey(phone);
		String obj = redisCacheUtil.get(phoneKey);
		if(StringUtil.isEmpty(obj)){
			throw new BusinessException(CommonConstant.CODE_ERROR_MSG);
		}
		if(!code.equals(JSON.parse(obj))){
			throw new BusinessException(CommonConstant.CODE_ERROR_MSG);
		}
		UnionVerifier unionVerifier = unionVerifierService.getByPhone(phone);
		if(unionVerifier == null){
			throw new BusinessException("手机号不存在");
		}
		BusUser user = busUserService.getBusUserById(unionVerifier.getBusId());
		SessionUtils.setUnionBus(request,user);
		com.gt.union.common.util.SessionUtils.setUnionVerifier(request,unionVerifier);
		redisCacheUtil.remove(phoneKey);
	}

	@Override
	public double getSumInComeUnionBrokerage(Integer busId) {
		return unionBrokerageIncomeService.getSumInComeUnionBrokerage(busId);
	}

	@Override
	public double getSumWithdrawalsUnionBrokerage(Integer busId) {
		return unionBrokerageWithdrawalService.getSumWithdrawalsUnionBrokerage(busId);
	}

	@Override
	public double getSumInComeUnionBrokerageByType(Integer busId, int type) {
		return unionBrokerageIncomeService.getSumInComeUnionBrokerageByType(busId,type);
	}

	@Override
	public Page listWithdrawals(Page page, Integer busId) {
		return unionBrokerageWithdrawalService.listWithdrawals(page,busId);
	}

	/**
	 * 得到该联盟的我的盟员信息
	 * @param members
	 * @param unionId
	 * @return
	 */
	private List<UnionMember> getMembers(List<UnionMember> members, Integer unionId){
		List<UnionMember> list = new ArrayList<UnionMember>();
		if(unionId != null){
			for(UnionMember member : members){
				if(member.getUnionId().equals(unionId)){
					list.add(member);
					break;
				}
			}
		}else {
			list = members;
		}
		return list;
	}


	@Override
	public double getUnComeUnionBrokerage(Integer busId, Integer unionId) throws Exception{
		List<UnionMember> members = unionMemberService.listWriteWithValidUnionByBusId(busId);
		if(ListUtil.isEmpty(members)){
			return 0;
		}
		members = getMembers(members,unionId);
		if(ListUtil.isEmpty(members)){
			return 0;
		}
		return unionH5BrokerageMapper.getUnComeUnionBrokerage(members);
	}

	@Override
	public Page listUnComeUnionBrokerage(Page page, Integer busId, Integer unionId) throws Exception{
		List<UnionMember> members = unionMemberService.listWriteWithValidUnionByBusId(busId);
		if(ListUtil.isEmpty(members)){
			return page;
		}
		members = getMembers(members,unionId);
		if(ListUtil.isEmpty(members)){
			return page;
		}
		List<Map<String,Object>> list = unionH5BrokerageMapper.listUnComeUnionBrokerage(page, members);
		page.setRecords(list);
		return page;
	}


	@Override
	public double getSumUnPayUnionBrokerage(Integer unionId, Integer busId) throws Exception{
		List<UnionMember> members = unionMemberService.listWriteWithValidUnionByBusId(busId);
		if(ListUtil.isEmpty(members)){
			return 0;
		}
		members = getMembers(members,unionId);
		if(ListUtil.isEmpty(members)){
			return 0;
		}
		return unionH5BrokerageMapper.getSumUnPayUnionBrokerage(members);
	}


	@Override
	public Page listUnPayUnionBrokerage(Page page, Integer busId, Integer unionId) throws Exception{
		List<UnionMember> members = unionMemberService.listWriteWithValidUnionByBusId(busId);
		if(ListUtil.isEmpty(members)){
			return page;
		}
		members = getMembers(members,unionId);
		if(ListUtil.isEmpty(members)){
			return page;
		}
		List<Map<String,Object>> list = unionH5BrokerageMapper.listUnPayUnionBrokerage(page, members);
		page.setRecords(list);
		return page;
	}

	@Override
	public Page listPayUnionBrokerage(Page page, Integer busId, Integer unionId) throws Exception{
		List<UnionMember> members = unionMemberService.listWriteWithValidUnionByBusId(busId);
		if(ListUtil.isEmpty(members)){
			return page;
		}
		members = getMembers(members,unionId);
		if(ListUtil.isEmpty(members)){
			return page;
		}
		List<Map<String,Object>> list = unionH5BrokerageMapper.listPayUnionBrokerage(page, members);
		page.setRecords(list);
		return page;
	}

	@Override
	public double getSumPayUnionBrokerage(Integer unionId, Integer busId) throws Exception{
		List<UnionMember> members = unionMemberService.listWriteWithValidUnionByBusId(busId);
		if(ListUtil.isEmpty(members)){
			return 0;
		}
		members = getMembers(members,unionId);
		if(ListUtil.isEmpty(members)){
			return 0;
		}
		return unionH5BrokerageMapper.getSumPayUnionBrokerage(members);
	}

	@Override
	public Page listOpportunityPayToMe(Page page, Integer busId, Integer unionId) throws Exception{
		List<UnionMember> members = unionMemberService.listWriteWithValidUnionByBusId(busId);
		if(ListUtil.isEmpty(members)){
			return page;
		}
		members = getMembers(members,unionId);
		if(ListUtil.isEmpty(members)){
			return page;
		}
		List<Map<String,Object>> list = unionH5BrokerageMapper.listOpportunityPayToMe(page, members);
		page.setRecords(list);
		return page;
	}

	@Override
	public double getOpportunitySumToMe(Integer busId, Integer unionId) throws Exception{
		List<UnionMember> members = unionMemberService.listWriteWithValidUnionByBusId(busId);
		if(ListUtil.isEmpty(members)){
			return 0;
		}
		members = getMembers(members,unionId);
		if(ListUtil.isEmpty(members)){
			return 0;
		}
		return unionH5BrokerageMapper.getOpportunitySumToMe(members);
	}

	@Override
	public Page listCardDivide(Page page, Integer busId, Integer unionId) throws Exception{
		List<UnionMember> members = unionMemberService.listWriteWithValidUnionByBusId(busId);
		if(ListUtil.isEmpty(members)){
			return page;
		}
		members = getMembers(members,unionId);
		if(ListUtil.isEmpty(members)){
			return page;
		}
		List<Map<String,Object>> list = unionH5BrokerageMapper.listCardDivide(page, members, busId);
		page.setRecords(list);
		return page;
	}

	@Override
	public double getCardDivideSum(Integer busId, Integer unionId) throws Exception{
		List<UnionMember> members = unionMemberService.listWriteWithValidUnionByBusId(busId);
		if(ListUtil.isEmpty(members)){
			return 0;
		}
		members = getMembers(members,unionId);
		if(ListUtil.isEmpty(members)){
			return 0;
		}
		return unionH5BrokerageMapper.getCardDivideSum(members, busId);
	}

	@Override
	public void urgeOpportunity(Integer busId, Integer id) throws Exception{
		if(busId == null || id == null){
			throw new ParamException(CommonConstant.PARAM_ERROR);
		}
		//(1)判断商机是否存在
		UnionOpportunity opportunity = unionOpportunityService.getById(id);
		if(opportunity == null){
			throw new BusinessException("商机不存在");
		}
		//(2)判断商机状态
		if(opportunity.getIsAccept() != OpportunityConstant.ACCEPT_YES){
			throw new BusinessException("商机未接受");
		}
		//(3)判断该商机是否我推荐的
		UnionMember fromMember = unionMemberService.getById(opportunity.getFromMemberId());
		if(fromMember == null){
			throw new BusinessException(CommonConstant.UNION_MEMBER_READ_REJECT);
		}
		if(!fromMember.getBusId().equals(busId)){
			throw new BusinessException("该商机不属于您");
		}
		//(4)判断该商机被推荐的盟员是否存在
		UnionMember toMember = unionMemberService.getById(opportunity.getToMemberId());
		if(toMember == null){
			throw new BusinessException("盟员不存在");
		}
		//(5)判断联盟是否有效
		unionMainService.checkUnionValid(toMember.getUnionId());

		//(6)判断我是否具有写权限
		if(!unionMemberService.hasWriteAuthority(fromMember)){
			throw new BusinessException(CommonConstant.UNION_MEMBER_INVALID);
		}

		UnionMain main = unionMainService.getById(toMember.getUnionId());
		PhoneMessage phoneMessage = new PhoneMessage(busId,StringUtil.isEmpty(toMember.getNotifyPhone()) ? toMember.getDirectorPhone() : toMember.getNotifyPhone(),
				"您尚未支付\"" + main.getName()+ "\"的\"" + fromMember.getEnterpriseName() + "\"" +opportunity.getBrokeragePrice() + "元的商机推荐佣金，请尽快支付，谢谢");
		this.phoneMessageSender.sendMsg(phoneMessage);
		UnionOpportunity unionOpportunity = new UnionOpportunity();
		unionOpportunity.setId(opportunity.getId());
		unionOpportunity.setIsUrgeBrokerage(OpportunityConstant.URGE_YES);
		unionOpportunityService.update(unionOpportunity);
	}

	@Override
	public double getPayAllOpportunitySum(Integer busId, Integer unionId) throws Exception{
		if(busId == null){
			throw new ParamException(CommonConstant.PARAM_ERROR);
		}
		double money = this.getSumUnPayUnionBrokerage(unionId,busId);
		return money;
	}

	@Override
	public String payAllOpportunity(Integer busId, Integer unionId, Double fee, String url, Integer memberId) throws Exception{
		if(busId == null){
			throw new ParamException(CommonConstant.PARAM_ERROR);
		}
		//(1)判断金额
		double money = this.getSumUnPayUnionBrokerage(unionId,busId);
		if (money <= 0 || fee.doubleValue() <= 0) {
			throw new BusinessException("支付金额有误");
		}
		if(money != fee.doubleValue()){
			throw new BusinessException("支付金额有误");
		}
		//(2)判断我的盟员权限
		List<UnionMember> members = unionMemberService.listWriteWithValidUnionByBusId(busId);
		if(ListUtil.isEmpty(members)){
			throw new BusinessException(CommonConstant.UNION_MEMBER_READ_REJECT);
		}
		if(unionId != null){
			int flag = 0;
			for(UnionMember member : members){
				if(member.getUnionId().equals(unionId)){
					flag = 1;
					break;
				}
			}
			if(flag == 0){
				throw new BusinessException(CommonConstant.UNION_MEMBER_READ_REJECT);
			}
		}
		String orderNo = OpportunityConstant.ORDER_PREFIX + System.currentTimeMillis();
		//得到该支付佣金的商机列表
		List<UnionOpportunity> list = this.listAllUnPayUnionBrokerage(busId,unionId);

		List<Integer> ids = new ArrayList<Integer>();
		for(UnionOpportunity opportunity : list){
			ids.add(opportunity.getId());
		}
		int count = unionBrokerageIncomeService.countByOpportunityIds(ids);
		if(count > 0){
			throw new BusinessException("商机已支付");
		}
		Map<String,Object> data = new HashMap<String,Object>();
		data.put("totalFee",money);
		data.put("model", ConfigConstant.ENTERPRISE_PAY_MODEL);
		data.put("busId",PropertiesUtil.getDuofenBusId());
		data.put("appidType",0);//公众号
		data.put("orderNum",orderNo);
		data.put("memberId",memberId);
		data.put("desc", "联盟商机佣金");
		data.put("returnUrl",PropertiesUtil.getUnionUrl() + "/brokeragePhone/#/" + url);
		data.put("notifyUrl",PropertiesUtil.getUnionUrl() + "/unionH5Brokerage/79B4DE7C/paymentAllSuccess/"+ busId + "/" + (unionId == null ? 0 : unionId));
		data.put("isSendMessage",0);//不需要推送
		data.put("payWay",1);//微信支付
		data.put("sourceType",1);
		data.put("isreturn",1);//需要同步跳转
		String payUrl = payService.wxPay(data);
		return payUrl;
	}

	@Override
	public String payOpportunity(Integer id, String url, Integer memberId, Integer busId) throws Exception{
		UnionOpportunity opportunity = unionOpportunityService.getById(id);
		//(1)判断佣金是否存在
		if(opportunity == null){
			throw new BusinessException("该商机不存在");
		}
		//(2)判断佣金是否被受理了
		if(opportunity.getIsAccept() != OpportunityConstant.ACCEPT_YES){
			throw new BusinessException("该商机未受理");
		}
		//(3)判断该商机是否已支付
		List<Integer> ids = new ArrayList<Integer>();
		ids.add(id);
		int count = unionBrokerageIncomeService.countByOpportunityIds(ids);
		if(count > 0){
			throw new BusinessException("该商机已支付");
		}
		//(4)判断支付的金额
		if(opportunity.getBrokeragePrice().doubleValue() <= 0){
			throw new BusinessException("支付金额有误");
		}
		//(5)判断该商机被推荐的盟员是否存在
		UnionMember fromMember = unionMemberService.getById(opportunity.getFromMemberId());
		if(fromMember == null){
			throw new BusinessException("盟员不存在");

		}
		//(6)判断该商机是否推荐给我的
		UnionMember toMember = unionMemberService.getById(opportunity.getToMemberId());
		if(toMember == null){
			throw new BusinessException(CommonConstant.UNION_MEMBER_READ_REJECT);
		}
		if(!toMember.getBusId().equals(busId)){
			throw new BusinessException("该商机不属于您");
		}
		//(7)判断联盟是否有效
		unionMainService.checkUnionValid(toMember.getUnionId());

		//(8)判断我是否具有写权限
		if(!unionMemberService.hasWriteAuthority(toMember)){
			throw new BusinessException(CommonConstant.UNION_MEMBER_INVALID);
		}
		String orderNo = OpportunityConstant.ORDER_PREFIX + System.currentTimeMillis();
		Map<String,Object> data = new HashMap<String,Object>();
		data.put("totalFee",opportunity.getBrokeragePrice());
		data.put("model", ConfigConstant.ENTERPRISE_PAY_MODEL);
		data.put("busId",PropertiesUtil.getDuofenBusId());
		data.put("appidType",0);//公众号
		data.put("orderNum",orderNo);
		data.put("memberId",memberId);
		data.put("desc", "联盟商机佣金");
		data.put("returnUrl",PropertiesUtil.getUnionUrl() + "/brokeragePhone/#/" + url);
		data.put("notifyUrl",PropertiesUtil.getUnionUrl() + "/unionH5Brokerage/79B4DE7C/paymentOneSuccess/" + id);
		data.put("isSendMessage",0);//不需要推送
		data.put("payWay",1);//微信支付
		data.put("sourceType",1);
		data.put("isreturn",1);//需要同步跳转
		String payUrl = payService.wxPay(data);
		return payUrl;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void paymentOneOpportunitySuccess(Integer id, String orderNo, Integer verifierId) throws Exception{
		UnionOpportunity opportunity = unionOpportunityService.getById(id);
		if(opportunity == null){
			throw new BusinessException("商机不存在");
		}
		if(opportunity.getIsAccept() != OpportunityConstant.ACCEPT_YES){
			throw new BusinessException("该商机未受理");
		}
		UnionBrokerageIncome brokerageIncome = unionBrokerageIncomeService.getByOpportunityId(id);
		if(brokerageIncome != null){
			throw new BusinessException("该商机已支付");
		}
		List<UnionOpportunity> list = new ArrayList<UnionOpportunity>();
		list.add(opportunity);
		unionOpportunityService.insertBatchByList(list,orderNo,verifierId);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void payAllOpportunitySuccess(Integer busId, Integer unionId,  String orderNo, Integer verifierId) throws Exception{
		List<UnionOpportunity> list = this.listAllUnPayUnionBrokerage(busId,unionId == 0 ? null : unionId);
		unionOpportunityService.insertBatchByList(list,orderNo,verifierId);
	}

	@Override
	public List<UnionOpportunity> listAllUnPayUnionBrokerage(Integer busId, Integer unionId) throws Exception{
		List<UnionMember> members = unionMemberService.listWriteWithValidUnionByBusId(busId);
		members = getMembers(members,unionId);
		List<UnionOpportunity> list = unionH5BrokerageMapper.listAllUnPayUnionBrokerage(members);
		return list;
	}


}
