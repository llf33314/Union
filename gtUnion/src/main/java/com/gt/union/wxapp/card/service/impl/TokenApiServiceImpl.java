package com.gt.union.wxapp.card.service.impl;

import com.alibaba.fastjson.JSON;
import com.gt.api.bean.session.Member;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.CommonUtil;
import com.gt.union.common.util.PropertiesUtil;
import com.gt.union.common.util.RedisCacheUtil;
import com.gt.union.wxapp.card.service.ITokenApiService;
import com.gt.union.wxapp.card.util.WxAppCacheKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * tokenService
 * @author hongjiye
 * @time 2017-12-29 16:07
 **/
@Service
public class TokenApiServiceImpl implements ITokenApiService {

	@Autowired
	private RedisCacheUtil redisCacheUtil;

	@Override
	public Member getMemberByToken(String token, Integer busId) throws Exception{
		if(busId == null){
			throw new ParamException(CommonConstant.PARAM_ERROR);
		}
		String redisMemberKey = WxAppCacheKeyUtil.getRedisMemberKeyByToken(token, PropertiesUtil.getTokenKey());
		Member member = JSON.parseObject(redisCacheUtil.get(redisMemberKey), Member.class);
		if(CommonUtil.isNotEmpty(member)){
			if(!busId.equals(member.getBusid())){
				return null;
			}
		}
		return member;
	}
}
