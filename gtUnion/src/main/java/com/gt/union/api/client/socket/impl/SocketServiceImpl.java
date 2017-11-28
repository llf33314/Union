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
	public boolean socketSendMessage(String pushName,String message,String pushStyle) {
		try {
			Map<String,Object> params = new HashMap<>();
			if(pushStyle == null){
				params.put("pushStyle","");
			}else {
				params.put("pushStyle",pushStyle);
			}
			params.put("pushMsg", message);
			params.put("pushName",pushName);
			logger.info("socket推送参数：{}", JSON.toJSONString(params));
			SignHttpUtils.WxmppostByHttp(PropertiesUtil.getWxmpUrl()+"/8A5DA52E/socket/getSocketApi.do", params, PropertiesUtil.getWxmpSignKey());
		}catch (Exception e){
			return false;
		}
		return true;
	}
}

