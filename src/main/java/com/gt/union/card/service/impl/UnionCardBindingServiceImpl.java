package com.gt.union.card.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.api.entity.result.UnionBindCardResult;
import com.gt.union.card.entity.UnionCard;
import com.gt.union.card.entity.UnionCardBinding;
import com.gt.union.card.entity.UnionCardRoot;
import com.gt.union.card.mapper.UnionCardBindingMapper;
import com.gt.union.card.service.IUnionCardBindingService;
import com.gt.union.card.service.IUnionCardRootService;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.RedisCacheUtil;
import com.gt.union.common.util.RedisKeyUtil;
import com.gt.union.common.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 联盟绑定表 服务实现类
 * </p>
 *
 * @author linweicong
 * @since 2017-09-11
 */
@Service
public class UnionCardBindingServiceImpl extends ServiceImpl<UnionCardBindingMapper, UnionCardBinding> implements IUnionCardBindingService {

	@Autowired
	private RedisCacheUtil redisCacheUtil;

	@Autowired
	private IUnionCardRootService unionCardRootService;

	@Override
	public UnionBindCardResult bindUnionCard(Integer busid, Integer memberId, String phone, String code) throws Exception{
		if(busid == null || memberId == null || StringUtil.isEmpty(phone) || StringUtil.isEmpty(code)){
			throw new ParamException(CommonConstant.PARAM_ERROR);
		}
		String phoneKey = RedisKeyUtil.getMemberPhoneCodeKey(memberId);
		Object obj = redisCacheUtil.get(phoneKey);
		if(obj == null){
			throw new ParamException("验证码失效");
		}
		if(!code.equals(obj.toString())) {
			throw new ParamException("验证码错误");

		}
		//1、判断手机号是否升级了联盟卡
		UnionCardRoot root = unionCardRootService.getByPhone(phone);
		if(root == null){
			throw new BusinessException("该手机号未升级联盟卡");
		}
		UnionCardBinding unionCardBinding = this.getByMemberId(memberId);
		if(unionCardBinding != null){
			throw new BusinessException("您已绑定联盟卡");
		}
		UnionCardBinding cardBinding = new UnionCardBinding();
		cardBinding.setRootId(root.getId());
		cardBinding.setCreatetime(new Date());
		cardBinding.setDelStatus(CommonConstant.DEL_STATUS_NO);
		cardBinding.setThirdMemberId(memberId);
		this.insert(cardBinding);
		UnionBindCardResult result = new UnionBindCardResult();
		result.setSuccess(true);
		result.setMessage("绑定成功");
		redisCacheUtil.remove(phoneKey);
		return result;
	}

	@Override
	public UnionCardBinding getByCardRootIdAndMemberId(Integer rootId, Integer memberId) {
		EntityWrapper entityWrapper = new EntityWrapper<>();
		entityWrapper.eq("root_id",rootId);
		entityWrapper.eq("third_member_id",memberId);
		entityWrapper.eq("del_status",CommonConstant.DEL_STATUS_NO);
		return this.selectOne(entityWrapper);
	}

	@Override
	public UnionCardBinding getByMemberId(Integer memberId) {
		EntityWrapper entityWrapper = new EntityWrapper<>();
		entityWrapper.eq("third_member_id",memberId);
		entityWrapper.eq("del_status",CommonConstant.DEL_STATUS_NO);
		return this.selectOne(entityWrapper);
	}

}
