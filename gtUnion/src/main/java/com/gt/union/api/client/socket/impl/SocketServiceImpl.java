package com.gt.union.api.client.socket.impl;

import com.alibaba.fastjson.JSON;
import com.gt.api.util.sign.SignHttpUtils;
import com.gt.union.api.client.socket.SocketService;
import com.gt.union.common.util.PropertiesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/9/21 0021.
 */
@Service
public class SocketServiceImpl implements SocketService{

	private Logger logger = LoggerFactory.getLogger(SocketServiceImpl.class);

	@Override
	public boolean socketCommonSendMessage(String socketKey,String message,String pushStyle) {
		try {
			Map<String,Object> params = new HashMap<>();
			if(pushStyle == null){
				params.put("pushStyle","");
			}else {
				params.put("pushStyle",pushStyle);
			}
			params.put("pushMsg", message);
			params.put("pushName",socketKey);
			logger.info("socket推送参数：{}", JSON.toJSONString(params));
			SignHttpUtils.WxmppostByHttp(PropertiesUtil.getWxmpUrl()+"/8A5DA52E/socket/getSocketApi.do", params, PropertiesUtil.getWxmpSignKey());
		}catch (Exception e){
			logger.error("socket公用推送错误",e);
			return false;
		}
		return true;
	}

	@Override
	public boolean socketPaySendMessage(String socketKey, Integer status, String pushStyle) {
		try {
			Map<String,Object> params = new HashMap<>();
			if(pushStyle == null){
				params.put("pushStyle","");
			}else {
				params.put("pushStyle",pushStyle);
			}
			Map data = new HashMap<>();
			data.put("status",status);
			data.put("socketKey",socketKey);

			params.put("pushMsg", JSON.toJSONString(data));
			params.put("pushName",socketKey);
			logger.info("socket推送参数：{}", JSON.toJSONString(params));
			SignHttpUtils.WxmppostByHttp(PropertiesUtil.getWxmpUrl()+"/8A5DA52E/socket/getSocketApi.do", params, PropertiesUtil.getWxmpSignKey());
		}catch (Exception e){
			logger.error("socket支付推送错误",e);
			return false;
		}
		return true;
	}
}

