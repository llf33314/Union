package com.gt.union.api.client.user.impl;

import com.alibaba.fastjson.JSON;
import com.gt.api.util.sign.SignHttpUtils;
import com.gt.union.api.client.user.BusUserService;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.util.CommonUtil;
import com.gt.union.common.util.PropertiesUtil;
import com.gt.union.common.util.StringUtil;
import com.gt.union.entity.common.BusUser;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 商家信息 服务实现类
 * Created by Administrator on 2017/8/22 0022.
 */
@Service
public class BusUserServiceImpl implements BusUserService {


	@Override
	public BusUser getBusUserById(Integer id) {
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
}
