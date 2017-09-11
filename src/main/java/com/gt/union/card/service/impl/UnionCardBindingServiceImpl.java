package com.gt.union.card.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.api.entity.result.UnionBindCardResult;
import com.gt.union.card.entity.UnionCard;
import com.gt.union.card.entity.UnionCardBinding;
import com.gt.union.card.mapper.UnionCardBindingMapper;
import com.gt.union.card.service.IUnionCardBindingService;
import com.gt.union.common.constant.CommonConstant;
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

	@Override
	public UnionBindCardResult bindUnionCard(Integer busid, Integer memberId, String phone, String code) throws Exception{
		if(busid == null){
			throw new ParamException(CommonConstant.PARAM_ERROR);
		}
		if(memberId == null){
			throw new ParamException(CommonConstant.PARAM_ERROR);
		}
		if(StringUtil.isEmpty(phone)){
			throw new ParamException(CommonConstant.PARAM_ERROR);
		}
		if(StringUtil.isEmpty(code)){
			throw new ParamException( CommonConstant.PARAM_ERROR);
		}
		String phoneKey = RedisKeyUtil.getMemberPhoneCodeKey(memberId);
		Object obj = redisCacheUtil.get(phoneKey);
		if(obj == null){
			UnionBindCardResult result = new UnionBindCardResult();
			result.setMessage("验证码已失效");
			result.setSuccess(false);
			return result;
		}
		if(!code.equals(obj.toString())) {
			UnionBindCardResult result = new UnionBindCardResult();
			result.setSuccess(false);
			result.setMessage("验证码有误");
		}
		UnionCardBinding unionCardBinding = new UnionCardBinding();
		//unionCardBinding.setCardId(1);
		unionCardBinding.setCreatetime(new Date());
		unionCardBinding.setDelStatus(CommonConstant.DEL_STATUS_NO);
		unionCardBinding.setThirdMemberId(memberId);
		this.insert(unionCardBinding);
		UnionBindCardResult result = new UnionBindCardResult();
		result.setSuccess(true);
		result.setMessage("绑定成功");
		redisCacheUtil.remove(phoneKey);
		return result;
	}

}
