package com.gt.union.brokerage.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.plugins.Page;
import com.gt.api.bean.session.BusUser;
import com.gt.api.bean.sign.SignBean;
import com.gt.api.util.SessionUtils;
import com.gt.api.util.sign.SignHttpUtils;
import com.gt.api.util.sign.SignUtils;
import com.gt.union.api.client.user.IBusUserService;
import com.gt.union.brokerage.mapper.UnionH5BrokerageMapper;
import com.gt.union.brokerage.service.IUnionBrokerageIncomeService;
import com.gt.union.brokerage.service.IUnionBrokerageWithdrawalService;
import com.gt.union.brokerage.service.IUnionH5BrokerageService;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.CommonUtil;
import com.gt.union.common.util.ListUtil;
import com.gt.union.common.util.RedisCacheUtil;
import com.gt.union.common.util.StringUtil;
import com.gt.union.member.entity.UnionMember;
import com.gt.union.member.service.IUnionMemberService;
import com.gt.union.verifier.entity.UnionVerifier;
import com.gt.union.verifier.service.IUnionVerifierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/9/18 0018.
 */
@Service
public class IUnionH5BrokerageServiceImpl implements IUnionH5BrokerageService {

	@Autowired
	private RedisCacheUtil redisCacheUtil;

	@Value("${wxmp.signkey}")
	private String wxmpKey;

	@Value(("${wxmp.url}"))
	private String wxmpUrl;

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
			SignBean sign = SignUtils.sign(wxmpKey , JSONObject.toJSONString(param));
			if(CommonUtil.isEmpty(sign)){
				throw new BusinessException("登录错误");
			}
			JSONObject objSing = JSONObject.parseObject(JSON.toJSONString(sign));
			JSONObject obj = new JSONObject();
			obj.put("login_name",username);
			obj.put("password",userpwd);
			obj.put("sign",objSing);
			String url = wxmpUrl + "/ErpMenus/79B4DE7C/UnionErplogin.do";
			String result = SignHttpUtils.WxmppostByHttp(url,obj,wxmpKey);
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
			Object obj = redisCacheUtil.get("h5brokerage"+phone);
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
			redisCacheUtil.remove("h5brokerage"+phone);
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


}
