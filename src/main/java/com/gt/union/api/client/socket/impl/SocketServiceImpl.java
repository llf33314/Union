package com.gt.union.api.client.socket.impl;

import com.alibaba.fastjson.JSON;
import com.gt.api.util.sign.SignHttpUtils;
import com.gt.union.api.client.socket.SocketService;
import com.gt.union.common.constant.ConfigConstant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/9/21 0021.
 */
@Service
public class SocketServiceImpl implements SocketService{

	@Override
	public int socketSendMessage(String pushName,String message,String pushStyle) {
		try {

			Map<String,Object> params = new HashMap<>();
			if(pushStyle == null){
				params.put("pushStyle","");
			}else {
				params.put("pushStyle",pushStyle);
			}
			params.put("pushMsg", message);
			params.put("pushName",pushName);
			String result = SignHttpUtils.WxmppostByHttp(ConfigConstant.WXMP_ROOT_URL+"/8A5DA52E/socket/getSocketApi.do", params, ConfigConstant.WXMP_SIGNKEY);
		}catch (Exception e){
			return 0;
		}
		return 1;
	}
}

