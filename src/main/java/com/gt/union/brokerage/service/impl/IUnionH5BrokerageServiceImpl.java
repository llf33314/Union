package com.gt.union.brokerage.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
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
import com.gt.union.brokerage.constant.BrokerageConstant;
import com.gt.union.brokerage.entity.UnionBrokerageIncome;
import com.gt.union.brokerage.entity.UnionBrokeragePay;
import com.gt.union.brokerage.mapper.UnionH5BrokerageMapper;
import com.gt.union.brokerage.service.IUnionBrokerageIncomeService;
import com.gt.union.brokerage.service.IUnionBrokeragePayService;
import com.gt.union.brokerage.service.IUnionBrokerageWithdrawalService;
import com.gt.union.brokerage.service.IUnionH5BrokerageService;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.net.URLEncoder;
import java.util.*;

/**
 * Created by Administrator on 2017/9/18 0018.
 */
@Service
public class IUnionH5BrokerageServiceImpl implements IUnionH5BrokerageService {

	@Autowired
	private RedisCacheUtil redisCacheUtil;

	@Autowired
	private SmsService smsService;

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
	private IUnionBrokeragePayService unionBrokeragePayService;

	@Override
	public void checkLogin(Integer type, String username, String userpwd, String phone, String code, HttpServletRequest request) throws Exception{
		if(type == null){
			throw new ParamException(CommonConstant.PARAM_ERROR);
		}
		if(type == 1){
			if(StringUtil.isEmpty(username) || StringUtil.isEmpty(userpwd)){
				throw new ParamException(CommonConstant.PARAM_ERROR);
			}
			BusUser user = busUserService.getBusUserByName(username);
			if(user == null){
				throw new BusinessException("登录错误");
			}
			if(user.getPid() != null && user.getPid() != 0){
				throw new BusinessException("请使用主账号登录");
			}
			Map<String,Object> param = new HashMap<String,Object>();
			param.put("login_name",username);
			param.put("password",userpwd);
			SignBean sign = SignUtils.sign(ConfigConstant.WXMP_SIGNKEY , JSONObject.toJSONString(param));
			if(CommonUtil.isEmpty(sign)){
				throw new BusinessException("登录错误");
			}
			JSONObject objSing = JSONObject.parseObject(JSON.toJSONString(sign));
			JSONObject obj = new JSONObject();
			obj.put("login_name",username);
			obj.put("password",userpwd);
			obj.put("sign",objSing);
			String url = ConfigConstant.WXMP_ROOT_URL + "/ErpMenus/79B4DE7C/UnionErplogin.do";
			String result = SignHttpUtils.WxmppostByHttp(url,obj,ConfigConstant.WXMP_SIGNKEY);
			if(StringUtil.isEmpty(result)){
				throw new BusinessException("登录错误");
			}
			JSONObject data = JSONObject.parseObject(result);
			if(data.get("code").equals("1")){//验证错误
				throw new BusinessException("登录错误");
			}
			if(data.get("code").equals("2")){//参数错误
				throw new BusinessException("登录错误");
			}


		}
		if(type == 2){
			if(StringUtil.isEmpty(phone) || StringUtil.isEmpty(code)){
				throw new ParamException(CommonConstant.PARAM_ERROR);
			}
			String phoneKey = RedisKeyUtil.getBrokeragePhoneKey(phone);
			Object obj = redisCacheUtil.get(phoneKey);
			if(obj == null){
				throw new BusinessException("验证码失效");
			}
			if(!code.equals(JSON.toJSONString(obj))){
				throw new BusinessException("验证码错误");
			}
			UnionVerifier unionVerifier = unionVerifierService.getByPhone(phone);
			if(unionVerifier == null){
				throw new BusinessException("手机号不存在");
			}
			BusUser user = busUserService.getBusUserById(unionVerifier.getBusId());
			SessionUtils.setLoginUser(request,user);
			com.gt.union.common.util.SessionUtils.setUnionVerifier(request,unionVerifier);
			redisCacheUtil.remove(phoneKey);
		}
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

	@Override
	public double getUnComeUnionBrokerage(Integer busId, Integer unionId) throws Exception{
		List<UnionMember> members = unionMemberService.listWriteWithValidUnionByBusId(busId);
		if(ListUtil.isEmpty(members)){
			return 0;
		}
		if(unionId != null){
			Iterator<UnionMember> it = members.iterator();
			while (it.hasNext()){
				UnionMember member = it.next();
				if(!member.getUnionId().equals(unionId)){
					it.remove();
				}
			}
		}
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
		if(unionId != null){
			Iterator<UnionMember> it = members.iterator();
			while (it.hasNext()){
				UnionMember member = it.next();
				if(!member.getUnionId().equals(unionId)){
					it.remove();
				}
			}
		}
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
		if(unionId != null){
			Iterator<UnionMember> it = members.iterator();
			while (it.hasNext()){
				UnionMember member = it.next();
				if(!member.getUnionId().equals(unionId)){
					it.remove();
				}
			}
		}
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
		if(unionId != null){
			Iterator<UnionMember> it = members.iterator();
			while (it.hasNext()){
				UnionMember member = it.next();
				if(!member.getUnionId().equals(unionId)){
					it.remove();
				}
			}
		}
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
		if(unionId != null){
			Iterator<UnionMember> it = members.iterator();
			while (it.hasNext()){
				UnionMember member = it.next();
				if(!member.getUnionId().equals(unionId)){
					it.remove();
				}
			}
		}
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
		if(unionId != null){
			Iterator<UnionMember> it = members.iterator();
			while (it.hasNext()){
				UnionMember member = it.next();
				if(!member.getUnionId().equals(unionId)){
					it.remove();
				}
			}
		}
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
		if(unionId != null){
			Iterator<UnionMember> it = members.iterator();
			while (it.hasNext()){
				UnionMember member = it.next();
				if(!member.getUnionId().equals(unionId)){
					it.remove();
				}
			}
		}
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
		if(unionId != null){
			Iterator<UnionMember> it = members.iterator();
			while (it.hasNext()){
				UnionMember member = it.next();
				if(!member.getUnionId().equals(unionId)){
					it.remove();
				}
			}
		}
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
		if(unionId != null){
			Iterator<UnionMember> it = members.iterator();
			while (it.hasNext()){
				UnionMember member = it.next();
				if(!member.getUnionId().equals(unionId)){
					it.remove();
				}
			}
		}
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
		if(unionId != null){
			Iterator<UnionMember> it = members.iterator();
			while (it.hasNext()){
				UnionMember member = it.next();
				if(!member.getUnionId().equals(unionId)){
					it.remove();
				}
			}
		}
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
		UnionOpportunity opportunity = unionOpportunityService.getById(id);
		if(opportunity == null){
			throw new BusinessException("商机不存在");
		}
		if(opportunity.getIsAccept() != OpportunityConstant.ACCEPT_YES){
			throw new BusinessException("商机未接受");
		}
		UnionMember fromMember = unionMemberService.getById(opportunity.getFromMemberId());
		if(fromMember == null){
			throw new BusinessException(CommonConstant.UNION_MEMBER_READ_REJECT);
		}
		if(!fromMember.getBusId().equals(busId)){
			throw new BusinessException("该商机不属于您");
		}
		UnionMember toMember = unionMemberService.getById(opportunity.getToMemberId());
		if(toMember == null){
			throw new BusinessException("盟员不存在");
		}
		UnionMain main = unionMainService.getById(toMember.getUnionId());
		HashMap<String, Object> smsParams = new HashMap<String,Object>();
		smsParams.put("mobiles", StringUtil.isEmpty(toMember.getNotifyPhone()) ? toMember.getDirectorPhone() : toMember.getNotifyPhone());
		smsParams.put("content", "您尚未支付\"" + main.getName()+ "\"的\"" + fromMember.getEnterpriseName() + "\"" +opportunity.getBrokeragePrice() + "元的商机推荐佣金，请尽快支付，谢谢");
		smsParams.put("company", ConfigConstant.WXMP_COMPANY);
		smsParams.put("busId", busId);
		smsParams.put("model", ConfigConstant.SMS_UNION_MODEL);
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("reqdata",smsParams);
		if(smsService.sendSms(param) != 1){
			throw new BusinessException("发送失败");
		}
		UnionOpportunity unionOpportunity = new UnionOpportunity();
		unionOpportunity.setId(opportunity.getId());
		unionOpportunity.setIsUrgeBrokerage(OpportunityConstant.URGE_YES);
		unionOpportunityService.updateById(unionOpportunity);
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
		double money = this.getSumUnPayUnionBrokerage(unionId,busId);
		if (money <= 0 || fee.doubleValue() <= 0) {
			throw new BusinessException("支付金额有误");
		}
		if(money != fee.doubleValue()){
			throw new BusinessException("支付金额有误");
		}
		String orderNo = OpportunityConstant.ORDER_PREFIX + System.currentTimeMillis();
		List<UnionOpportunity> list = this.listAllUnPayUnionBrokerage(busId,unionId);
		String encrypt = EncryptUtil.encrypt(ConfigConstant.UNION_ENCRYPTKEY, JSON.toJSONString(list));
		encrypt = URLEncoder.encode(encrypt, "UTF-8");
		Map<String,Object> data = new HashMap<String,Object>();
		data.put("totalFee",money);
		data.put("model", ConfigConstant.ENTERPRISE_PAY_MODEL);
		data.put("busId",ConfigConstant.WXMP_DUOFEN_BUSID);
		data.put("appidType",0);//公众号
		data.put("orderNum",orderNo);
		data.put("memberId",memberId);
		data.put("desc", "联盟商机佣金");
		data.put("returnUrl",ConfigConstant.UNION_PHONE_ROOT_URL + url);
		data.put("notifyUrl",ConfigConstant.UNION_ROOT_URL + "/unionH5Brokerage/79B4DE7C/paymentAllSuccess/" + encrypt);
		data.put("isSendMessage",0);//不需要推送
		data.put("payWay",1);//微信支付
		data.put("sourceType",1);
		String payUrl = payService.wxPay(data);
		return payUrl;
	}

	@Override
	public String payOpportunity(Integer id, String url, Integer memberId) throws Exception{
		UnionOpportunity opportunity = unionOpportunityService.getById(id);
		if(opportunity == null){
			throw new BusinessException("该商机不存在");
		}
		if(opportunity.getIsAccept() != OpportunityConstant.ACCEPT_YES){
			throw new BusinessException("该商机未受理");
		}
		UnionBrokerageIncome income = unionBrokerageIncomeService.getByUnionOpportunityId(id);
		if(income != null){
			throw new BusinessException("该商机已支付");
		}
		if(opportunity.getBrokeragePrice().doubleValue() <= 0){
			throw new BusinessException("支付金额有误");
		}
		String orderNo = OpportunityConstant.ORDER_PREFIX + System.currentTimeMillis();
		String encrypt = EncryptUtil.encrypt(ConfigConstant.UNION_ENCRYPTKEY, String.valueOf(id));
		encrypt = URLEncoder.encode(encrypt, "UTF-8");
		Map<String,Object> data = new HashMap<String,Object>();
		data.put("totalFee",opportunity.getBrokeragePrice());
		data.put("model", ConfigConstant.ENTERPRISE_PAY_MODEL);
		data.put("busId",ConfigConstant.WXMP_DUOFEN_BUSID);
		data.put("appidType",0);//公众号
		data.put("orderNum",orderNo);
		data.put("memberId",memberId);
		data.put("desc", "联盟商机佣金");
		data.put("returnUrl",ConfigConstant.UNION_PHONE_ROOT_URL + url);
		data.put("notifyUrl",ConfigConstant.UNION_ROOT_URL + "/unionH5Brokerage/79B4DE7C/paymentOneSuccess/" + encrypt);
		data.put("isSendMessage",0);//不需要推送
		data.put("payWay",1);//微信支付
		data.put("sourceType",1);
		String payUrl = payService.wxPay(data);
		return payUrl;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void paymentOneOpportunitySuccess(String encrypt, String orderNo, Integer verifierId) throws Exception{
		String id = EncryptUtil.decrypt(ConfigConstant.UNION_ENCRYPTKEY, encrypt);
		Integer oppId = Integer.parseInt(id);
		UnionOpportunity opportunity = unionOpportunityService.getById(oppId);
		if(opportunity == null){
			throw new BusinessException("商机不存在");
		}
		if(opportunity.getIsAccept() != OpportunityConstant.ACCEPT_YES){
			throw new BusinessException("该商机未受理");
		}
		UnionBrokerageIncome brokerageIncome = unionBrokerageIncomeService.getByUnionOpportunityId(oppId);
		if(brokerageIncome != null){
			throw new BusinessException("该商机已支付");
		}
		UnionMember fromMember = unionMemberService.getById(opportunity.getFromMemberId());
		UnionMember toMember = unionMemberService.getById(opportunity.getToMemberId());
		//商家佣金收入
		UnionBrokerageIncome income = new UnionBrokerageIncome();
		income.setBusId(fromMember.getBusId());
		income.setMoney(opportunity.getBrokeragePrice());
		income.setOpportunityId(oppId);
		income.setDelStatus(CommonConstant.DEL_STATUS_NO);
		income.setCreatetime(new Date());
		income.setType(BrokerageConstant.SOURCE_TYPE_OPPORTUNITY);
		unionBrokerageIncomeService.insert(income);

		//收支明细
		UnionBrokeragePay pay = new UnionBrokeragePay();
		pay.setToBusId(fromMember.getBusId());
		pay.setFromBusId(toMember.getBusId());
		pay.setDelStatus(CommonConstant.DEL_STATUS_NO);
		pay.setCreatetime(new Date());
		pay.setOrderNo(orderNo);
		pay.setStatus(BrokerageConstant.BROKERAGE_PAY_STATUS_YES);//支付成功
		pay.setMoney(opportunity.getBrokeragePrice());
		pay.setType(BrokerageConstant.BROKERAGE_PAY_TYPE_WX);//微信支付
		pay.setOpportunityId(opportunity.getId());
		pay.setVerifierId(verifierId);
		unionBrokeragePayService.insert(pay);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void payAllOpportunitySuccess(String encrypt, String orderNo, Integer verifierId) throws Exception{
		String data = EncryptUtil.decrypt(ConfigConstant.UNION_ENCRYPTKEY, encrypt);
		List<UnionOpportunity> list =JSONArray.parseArray(data,UnionOpportunity.class);
		unionOpportunityService.insertBatchByList(list,orderNo,verifierId);
	}

	@Override
	public List<UnionOpportunity> listAllUnPayUnionBrokerage(Integer busId, Integer unionId) throws Exception{
		List<UnionMember> members = unionMemberService.listWriteWithValidUnionByBusId(busId);
		if(unionId != null){
			Iterator<UnionMember> it = members.iterator();
			while (it.hasNext()){
				UnionMember member = it.next();
				if(!member.getUnionId().equals(unionId)){
					it.remove();
				}
			}
		}
		List<UnionOpportunity> list = unionH5BrokerageMapper.listAllUnPayUnionBrokerage(members);
		return list;
	}


}
