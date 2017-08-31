package com.gt.union.api.client.user.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gt.api.util.HttpClienUtils;
import com.gt.api.util.MapUtils;
import com.gt.api.util.sign.SignHttpUtils;
import com.gt.union.api.client.user.BusUserService;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.util.*;
import com.gt.union.entity.common.BusUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 商家信息 服务实现类
 * Created by Administrator on 2017/8/22 0022.
 */
@Service
public class BusUserServiceImpl implements BusUserService {

	@Autowired
	private RedisCacheUtil redisCacheUtil;

	@Override
	public BusUser getBusUserById(Integer id) {
		String busUserKey = RedisKeyUtil.getBusUserKey(id);
		if (this.redisCacheUtil.exists(busUserKey)) {//（1）通过busId获取缓存中的busUser对象，如果存在，则直接返回
			Object obj = this.redisCacheUtil.get(busUserKey);
			return JSON.parseObject(obj.toString(), BusUser.class);
		}
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("userId",id);
		BusUser busUser = null;
		String url = PropertiesUtil.getWxmpUrl() + "/8A5DA52E/busUserApi/getBusUserApi.do";
		try{
			String result = SignHttpUtils.WxmppostByHttp(url, param, CommonConstant.WXMP_SIGN_KEY);
			if(StringUtil.isEmpty(result)){
				return null;
			}
			Map<String,Object> data= JSON.parseObject(result,Map.class);
			if(CommonUtil.isEmpty(data.get("data"))){
				return null;
			}
			busUser = JSON.parseObject(data.get("data").toString(),BusUser.class);
			if (busUser != null) {
				this.redisCacheUtil.set(busUserKey, JSON.toJSONString(busUser));
			}
		}catch (Exception e){
			return null;
		}
		return busUser;
	}

	@Override
	public BusUser getBusUserByName(String name){
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("name",name);
		BusUser busUser = null;
		String url = PropertiesUtil.getWxmpUrl() + "/8A5DA52E/busUserApi/getBusUserApi.do";
		try{
			String result = SignHttpUtils.WxmppostByHttp(url, param, CommonConstant.WXMP_SIGN_KEY);
			if(StringUtil.isEmpty(result)){
				return null;
			}
			Map<String,Object> data= JSON.parseObject(result,Map.class);
			if(CommonUtil.isEmpty(data.get("data"))){
				return null;
			}
			busUser = JSON.parseObject(data.get("data").toString(),BusUser.class);
		}catch (Exception e){
			return null;
		}
		return busUser;
	}

	@Override
	public Map<String, Object> getWxPublicUserByBusId(Integer busId) {
		String wxUserKey = RedisKeyUtil.getWxPublicUserBusIdKey(busId);
		if (this.redisCacheUtil.exists(wxUserKey)) {//（1）通过busId获取缓存中的busUser对象，如果存在，则直接返回
			Object obj = this.redisCacheUtil.get(wxUserKey);
			return JSON.parseObject(obj.toString(),Map.class);
		}
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("reqdata",busId);
		String url = PropertiesUtil.getWxmpUrl() + "/8A5DA52E/wxpublicapi/6F6D9AD2/79B4DE7C/selectByUserId.do";
		Map result = HttpClienUtils.reqPostUTF8(JSONObject.toJSONString(param),url, Map.class, CommonConstant.WXMP_SIGN_KEY);
		if(CommonUtil.isEmpty(result)){
			return null;
		}
		if(CommonUtil.toInteger(result.get("code")) != 0){
			return null;
		}
		Map<String,Object> data = JSONObject.parseObject(result.get("data").toString(),Map.class);
		if(CommonUtil.isNotEmpty(data)){
			redisCacheUtil.set(wxUserKey,JSON.toJSONString(data));
		}
		return data;
	}
}
